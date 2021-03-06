package net.vpc.scholar.hadrumaths.plot.console;

import net.vpc.scholar.hadrumaths.ExternalLibrary;
import net.vpc.scholar.hadrumaths.Plot;
import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToVector;
import net.vpc.scholar.hadrumaths.plot.*;
import net.vpc.scholar.hadrumaths.plot.CellPosition;

import javax.swing.*;
import java.util.Set;


public class FunctionsXYPlotConsoleAction implements ConsoleAction {
    public static final long serialVersionUID = -1231231231240000003L;
    private DoubleToVector[] fn;
    private String title;
    private Domain domain;
    private WindowPath preferredPath;
    private PlotType plotType;
    private Set<ExternalLibrary> preferredLibraries;
    public FunctionsXYPlotConsoleAction(String typeTitle, String title, DoubleToVector[] fn, Domain domain,WindowPath preferredPath, PlotType plotType,Set<ExternalLibrary> preferredLibraries) {
        this.fn = fn;
        this.title = (title==null || title.length()==0)?typeTitle:title;
        this.domain = domain;
        this.preferredPath = new WindowPath(preferredPath,typeTitle);
        this.plotType = plotType;
        this.preferredLibraries = preferredLibraries;
    }

    public void execute(PlotConsole plotter) {
        ExpressionsPlotModel m=new ExpressionsPlotModel();
        m.setTitle(title).setDomain(domain).setExpressions(fn)
                .setShowType(PlotBuilder.toShowType(plotType))
                .setPreferredLibraries(preferredLibraries)
                .setSelectedAxis(new CellPosition[]{new CellPosition(0,0)})
        ;
        plotter.getMainPlotterFrame().getWindow(preferredPath).addChild(
                title, (JComponent) Plot.create(m, Plot.getDefaultWindowManager())
        );
    }
}