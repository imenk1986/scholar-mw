package net.vpc.scholar.hadrumaths.plot.mesh;

import net.vpc.scholar.hadrumaths.Maths;
import net.vpc.scholar.hadrumaths.plot.*;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Created by vpc on 1/3/17.
 */
public class Jzy3dMeshPlot extends JPanel implements PlotComponentPanel {
    private ValuesPlotModel model;
    private JColorPalette colorPalette;
    private JLabel titleLabel;
    private double[] x;
    private double[] y;
    private double[][] z;
    private Chart chart;

    public Jzy3dMeshPlot(ValuesPlotModel model, JColorPalette colorPalette) {
        this(model.getTitle(), model.getXVector(), model.getY() == null || model.getY().length == 0 ? Maths.dsteps(1, model.getX().length, 1.0) : model.getY()[0], model.getZd(),
                colorPalette, null);
    }

    public Jzy3dMeshPlot(PlotModelProvider modelProvider, JColorPalette colorPalette) {
        this(modelProvider.getModel().getTitle(), modelProvider.getModel().getXVector(), modelProvider.getModel().getY() == null || modelProvider.getModel().getY().length == 0 ? Maths.dsteps(1, modelProvider.getModel().getX().length, 1.0) : modelProvider.getModel().getY()[0], modelProvider.getModel().getZd(),
                colorPalette, modelProvider);
    }

    @Override
    public JComponent toComponent() {
        return this;
    }

    @Override
    public JPopupMenu getPopupMenu() {
        return null;
    }

    public Jzy3dMeshPlot(String title, double[] x, double[] y, double[][] z, JColorPalette colorPalette, PlotModelProvider plotModelProvider) {
        super(new BorderLayout());
        titleLabel = new JLabel(title, SwingConstants.CENTER);

        if (colorPalette == null) {
            colorPalette = HSBColorPalette.DEFAULT_PALETTE;
        }
        if (x == null) {
            x = z.length == 0 ? new double[0] : Maths.dsteps(0, z[0].length - 1, 1.0);
        }
        if (y == null) {
            y = z.length == 0 ? new double[0] : Maths.dsteps(0, z.length - 1, 1.0);
        }
        this.x = x;
        this.y = y;
        this.z = z;
        setPreferredSize(new Dimension(600, 600));
        add(titleLabel, BorderLayout.NORTH);
        final Component canvas = createComponent();
        add(canvas, BorderLayout.CENTER);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.setPreferredSize(e.getComponent().getSize());
                canvas.setSize(e.getComponent().getSize());
                chart.render();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private Component createComponent() {
        Settings.getInstance().setHardwareAccelerated(true);
        // Define a function to plot
        org.jzy3d.plot3d.builder.Mapper mapper = new org.jzy3d.plot3d.builder.Mapper() {
            @Override
            public double f(double x, double y) {
                return z[(int) y][(int) x];
            }
        };

        // Define range and precision for the function to plot
        Range xrange = new Range(0, x.length - 1);
        int xsteps = x.length;
        Range yrange = new Range(0, y.length - 1);
        int ysteps = y.length;

        // Create the object to represent the function over the given range.
        final org.jzy3d.plot3d.primitives.Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(xrange, xsteps, yrange, ysteps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new org.jzy3d.colors.Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        surface.setLegendDisplayed(true);

        // Create a chart
        final Chart chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
        chart.getScene().getGraph().add(surface);
        chart.setViewPoint(new Coord3d(-0.4 * Math.PI, Math.PI / 4, 0));

//        chart.setViewPoint(new Coord3d(-Math.PI/2,Math.PI/4,0));
//        chart.setViewPoint(new Coord3d(-3*Math.PI/4,Math.PI/4,0));
//        chart.setViewPoint(new Coord3d(-Math.PI/2,Math.PI/2,0));
//        chart.getView().addViewPointChangedListener(new IViewPointChangedListener() {
//            @Override
//            public void viewPointChanged(ViewPointChangedEvent viewPointChangedEvent) {
//                Coord3d x = viewPointChangedEvent.getViewPoint();
//                System.out.println((x.x / Math.PI) + " , " + (x.y / Math.PI) + " , " + (x.z / Math.PI));
//            }
//        });
        Component canvas = (Component) chart.getCanvas();
//        canvas.setPreferredSize(getPreferredSize());
        this.chart = chart;
        ICameraMouseController mouse = ChartLauncher.configureControllers(chart, "test", true, true);
        return canvas;

    }
}
