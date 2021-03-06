package net.vpc.scholar.hadrumaths.plot;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.plot.curve.CurvePlot;
import net.vpc.scholar.hadrumaths.plot.surface.DefaultHeatMapPlotNormalizer;
import net.vpc.scholar.hadrumaths.plot.surface.HeatMapPlot;
import net.vpc.scholar.hadrumaths.symbolic.Discrete;
import net.vpc.scholar.hadrumaths.symbolic.VDiscrete;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 19 juil. 2007 01:05:06
 */
public class VDiscretePlotPanel extends BasePlotComponent implements PlotPanel {
    private final JPanel pp = new JPanel();
    private JComboBox d2d3 = new JComboBox(new Object[]{"1D", "2D", "Matrix", "3D"});
    private JComboBox xyz = new JComboBox(new Object[]{"X", "Y", "Z", "All"});
    private JComboBox surface = new JComboBox(new Object[]{"YZ", "XZ", "XY"});
    private JComboBox norm = new JComboBox(NormalizerType.values());
    private JSlider slider = new JSlider();
    private static final int TYPE_1D = 1;
    private static final int TYPE_2D = 2;
    private static final int TYPE_MATRIX = 3;
    private static final int TYPE_MESH = 4;
    private int typeSurface = TYPE_1D;
    private Axis[] xyzValue = Axis.values();
    private Axis surfaceValue = Axis.Z;
//    private String title;
    private PlotWindowManager windowManager;
    private NormalizerType normalizerType = NormalizerType.FULL;
    private int revalidatingPlot = 0;
    private String layoutConstraints = "";
    private VDiscretePlotModel model2;

//    public VDiscretePlotPanel(PlotWindowManager windowManager, String title, Set<ExternalLibrary> preferredLibraries, VDiscrete... _model) {
//
//    }
    public VDiscretePlotPanel(VDiscretePlotModel model, PlotWindowManager windowManager) {
        super(new BorderLayout());
        if(model==null){
            model=new VDiscretePlotModel();
        }
        this.model2 = model;
        this.windowManager = windowManager;
        JToolBar tb = new JToolBar();
        d2d3.setSelectedIndex(typeSurface - 1);
        tb.add(new JLabel("2D/3D"));
        tb.add(d2d3);
        tb.add(new JLabel("Axis"));
        tb.add(xyz);
        tb.add(new JLabel("Norm"));
        tb.add(norm);
        tb.addSeparator();
        tb.add(new JLabel("Surface"));
        tb.add(surface);
        tb.addSeparator();
        tb.add(new JLabel("Value"));
        tb.add(slider);
        add(tb, BorderLayout.PAGE_START);
        xyz.setSelectedIndex(3);
        norm.setSelectedItem(normalizerType);
        pp.setLayout(new GridLayout(1, 0));
        add(pp, BorderLayout.CENTER);
        surface.setSelectedIndex(2);
        xyz.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int selectedIndex = xyz.getSelectedIndex();
//                    Axis[] old = xyzValue;
                    if (selectedIndex >= 0 && selectedIndex < 3) {
                        xyzValue = new Axis[]{Axis.values()[selectedIndex]};
                    } else {
                        xyzValue = Axis.values();
                    }
                    revalidatePlot();
                }
            }
        });
        d2d3.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int selectedIndex = d2d3.getSelectedIndex();
                    typeSurface = (selectedIndex + 1);
                    revalidatePlot();
                }
            }
        });
        norm.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    normalizerType = (NormalizerType) norm.getSelectedItem();
                    revalidatePlot();
                }
            }
        });
        surface.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    surfaceValue = Axis.values()[surface.getSelectedIndex()];
                    revalidatePlot();
                }
            }
        });
        slider.setValue(0);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
//                    System.out.println(slider.getValue());
                    revalidatePlot();
                }
            }
        });
        revalidatePlot();
    }

    @Override
    public boolean accept(PlotModel model) {
        return model instanceof VDiscretePlotModel;
    }

    @Override
    public void setModel(PlotModel model) {
        if(model==null){
            model=new VDiscretePlotModel();
        }
        this.model2=(VDiscretePlotModel) model;
    }

    public void revalidatePlot() {
        if (revalidatingPlot != 0) {
            revalidatingPlot = 2;
            return;
        }
        try {
            revalidatingPlot = 1;
            synchronized (pp) {
                pp.removeAll();
                int axisIndex = 0;
                for (final Axis axis : xyzValue) {
                    ComponentDimension componentDimension = model2.getVdiscretes()[0].getComponentDimension();
                    if (axisIndex >= componentDimension.rows) {
                        break;
                    }
                    axisIndex++;
                    Discrete discrete = null;
                    try {
                        discrete = model2.getVdiscretes()[0].getComponent(axis);
                    } catch (Exception ee) {
                        //
                    }
                    if (discrete != null) {
                        int max = discrete.getCount(surfaceValue);
                        if (slider.getMaximum() != max) {
                            slider.setMaximum(max);
                        }
                        int value = slider.getValue();
                        if (value < 0) {
                            value = 0;
                        }
                        if (value > (max - 1)) {
                            value = max - 1;
                        }
                        Complex[][] renderedValues0 = discrete.getArray(surfaceValue, value);
                        Complex[][] renderedValues = new Complex[model2.getVdiscretes().length * renderedValues0.length][renderedValues0.length == 0 ? 0 : renderedValues0[0].length];
                        for (int i = 0; i < model2.getVdiscretes().length; i++) {
                            Complex[][] cube_i = model2.getVdiscretes()[i].getComponent(axis).getArray(surfaceValue, value);
                            for (int j = 0; j < renderedValues0.length; j++) {
                                renderedValues[i * renderedValues0.length + j] = cube_i[j];
                            }
                        }
                        double[] axis1 = discrete.getX();
                        double[] axis2 = discrete.getY();
                        switch (surfaceValue) {
                            case Z: {
                                axis1 = discrete.getX();
                                axis2 = discrete.getY();
                                break;
                            }
                            case X: {
                                axis1 = discrete.getY();
                                axis2 = discrete.getZ();
                                break;
                            }
                            case Y: {
                                axis1 = discrete.getX();
                                axis2 = discrete.getZ();
                                break;
                            }
                        }
                        ValuesPlotModel pmodel = new ValuesPlotModel(
                                "Axe " + axis + " ; Coupe " + surface.getSelectedItem(), null, null, null, axis1, axis2, renderedValues, ComplexAsDouble.ABS, PlotType.HEATMAP,
                                null
                        );
                        SimplePlotModelProvider modelProvider = new SimplePlotModelProvider(pmodel, this);
                        if (typeSurface == TYPE_2D) {
                            pmodel.setPlotType(PlotType.HEATMAP);
                            HeatMapPlot allHeatMapPlot = new HeatMapPlot(
                                    modelProvider, null, 200
                            );
//                        allHeatMapPlot.rotateLeft();
//                        allHeatMapPlot.rotateLeft();
                            allHeatMapPlot.setNormalizer(new ExtendedNormalizer(axis));
                            Plot.buildJPopupMenu((PlotComponentPanel) allHeatMapPlot, modelProvider);
                            pp.add(allHeatMapPlot);
                        } else if (typeSurface == TYPE_MATRIX) {
                            pmodel.setPlotType(PlotType.MATRIX);
                            HeatMapPlot allHeatMapPlot = new HeatMapPlot(
                                    modelProvider, null, 200
                            );
                            allHeatMapPlot.setNormalizer(new ExtendedNormalizer(axis));
                            Plot.buildJPopupMenu((PlotComponentPanel) allHeatMapPlot, modelProvider);
                            pp.add(allHeatMapPlot);
                        } else if (typeSurface == TYPE_MESH) {
                            pmodel.setPlotType(PlotType.MESH);
                            PlotComponentPanel allSurface2DPlot = ChartFactory.createMesh(
                                    modelProvider, null, model2.getPreferredLibraries()
                            );
                            Plot.buildJPopupMenu(allSurface2DPlot, modelProvider);
//                        allSurface2DPlot.setNormalizer(new ExtendedNormalizer(axis));
                            pp.add(allSurface2DPlot.toComponent());
                        } else if (typeSurface == TYPE_1D) {
                            pmodel.setPlotType(PlotType.CURVE);
                            CurvePlot allSurface2DPlot = new CurvePlot(
                                    modelProvider, true
                            );
//                        allSurface2DPlot.setNormalizer(new ExtendedNormalizer(axis));
                            Plot.buildJPopupMenu((PlotComponentPanel) allSurface2DPlot, modelProvider);
                            pp.add(allSurface2DPlot);
                        }
                    }
                }
                pp.revalidate();
                pp.repaint();
            }
        } finally {
            if (revalidatingPlot == 2) {
                revalidatingPlot = 0;
                revalidatePlot();
            } else {
                revalidatingPlot = 0;
            }
        }
    }

    public String getPlotTitle() {
        return model2.getTitle();
    }

    public JComponent toComponent() {
        return (JComponent) this;
    }

    public void display() {
        windowManager.add(this);
    }

    public static enum NormalizerType {
        MATRIX, CUBE, FULL
    }

    private class ExtendedNormalizer extends DefaultHeatMapPlotNormalizer {
        private Axis axis;

        public ExtendedNormalizer(Axis axis) {
            this.axis = axis;
        }

        public double[][] normalize(double[][] baseValues) {
            MinMax minMax = new MinMax();
            switch (normalizerType) {
                case MATRIX: {
                    for (double[] y : baseValues) {
                        for (double x : y) {
                            minMax.registerValue(x);
                        }
                    }
                    break;
                }
                case FULL: {
                    for (VDiscrete c3D : model2.getVdiscretes()) {
                        ComponentDimension d = c3D.getComponentDimension();
                        for (int[] rc : d.iterate()) {
                            Complex[][][] c3 = ((Discrete) c3D.getComponent(rc[0], rc[1])).getValues();
                            for (Complex[][] z : c3) {
                                for (Complex[] y : z) {
                                    for (Complex x : y) {
                                        minMax.registerValue(x.absdbl());
                                    }
                                }
                            }
                        }
                    }
                }
                case CUBE: {
                    for (VDiscrete c3D : model2.getVdiscretes()) {
                        Complex[][][] c3 = c3D.getComponent(axis).getValues();
                        for (Complex[][] z : c3) {
                            for (Complex[] y : z) {
                                for (Complex x : y) {
                                    minMax.registerValue(x.absdbl());
                                }
                            }
                        }
                    }
                    break;
                }
            }
            return normalize(baseValues, minMax.getMin(), minMax.getMax());
        }
    }

    @Override
    public String getLayoutConstraints() {
        return layoutConstraints;
    }

    public void setLayoutConstraints(String layoutConstraints) {
        this.layoutConstraints = layoutConstraints;
    }

    @Override
    public PlotModel getModel() {
        return getModel();
    }
}
