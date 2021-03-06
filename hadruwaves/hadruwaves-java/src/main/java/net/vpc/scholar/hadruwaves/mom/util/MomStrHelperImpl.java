/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.vpc.scholar.hadruwaves.mom.util;

import net.vpc.scholar.hadrumaths.Matrix;
import net.vpc.scholar.hadrumaths.cache.ObjectCache;
import net.vpc.scholar.hadrumaths.symbolic.VDiscrete;
import net.vpc.scholar.hadrumaths.util.IOUtils;
import net.vpc.scholar.hadrumaths.util.ProgressMonitor;
import net.vpc.scholar.hadruwaves.mom.MomStructure;
import net.vpc.scholar.hadruwaves.mom.project.MomProject;
import net.vpc.scholar.hadruwaves.mom.str.RequiredRebuildException;

import java.io.File;
import java.util.Collection;

/**
 * @author vpc
 */
public class MomStrHelperImpl implements MomStrHelper {
    private MomProject str;
    private MomStructure momStructure;

    public MomProject getStructureConfig() {
        return str;
    }


    public MomStructure getStructure() {
        return momStructure;
    }


    public void init(MomProject str) {
        this.str = str;
        momStructure = new MomStructure();
        momStructure.getPersistentCache().setRootFolder(IOUtils.createHFile(str.getWorkDir().getPath()+"/cache"));
        momStructure.loadProject(str);
    }


    public Matrix computeTestcoeff(ProgressMonitor monitor) {
        return momStructure.matrixX().monitor(monitor).computeMatrix();
    }


    public VDiscrete computeElectricField(double[] x, double[] y, double[] z, ProgressMonitor monitor) {
        return momStructure.electricField().monitor(monitor).computeVDiscrete(x, y, new double[]{0});
    }


    public VDiscrete computeCurrent(double[] x, double[] y, ProgressMonitor monitor) {
        return momStructure.current().monitor(monitor).computeVDiscrete(x, y);
    }


    public VDiscrete computeTestField(double[] x, double[] y, ProgressMonitor monitor) {
        return momStructure.testField().monitor(monitor).computeVDiscrete(x, y);
    }


    public VDiscrete buildSrc(double[] x, double[] y, ProgressMonitor monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public Matrix computeZin(ProgressMonitor monitor) {
        return momStructure.inputImpedance().monitor(monitor).computeMatrix();
    }


    public Matrix computeS(ProgressMonitor monitor) {
        return momStructure.sparameters().monitor(monitor).computeMatrix();
    }


    public Matrix computeAMatrix(ProgressMonitor monitor) {
        return momStructure.matrixA().monitor(monitor).computeMatrix();
    }


    public Matrix computeBMatrix(ProgressMonitor monitor) {
        return momStructure.matrixB()
                .monitor(monitor).computeMatrix();
    }


    public void checkBuildIsRequired() throws RequiredRebuildException {
        momStructure.checkBuildIsRequired();
    }


    public int estimateMn(int max_mn, double delta, int thresholdLength, int trialCount, ProgressMonitor monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public long getLoggedStatistics(String statName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void recompile() {
        momStructure.loadProject(str);
    }


    public ObjectCache getCurrentCache(boolean autoCreate) {
        return momStructure.getCurrentCache(autoCreate);
    }


    public Collection<ObjectCache> getSimilarCaches(String property) {
        return momStructure.getSimilarCaches(property);
    }


    public Collection<ObjectCache> getAllCaches() {
        return momStructure.getAllCaches();
    }

    public Matrix computeCapacity(ProgressMonitor monitor) {
        return momStructure
                .capacity().monitor(monitor).computeMatrix();
    }

    public String getDump() {
        return momStructure.dump();
    }


}
