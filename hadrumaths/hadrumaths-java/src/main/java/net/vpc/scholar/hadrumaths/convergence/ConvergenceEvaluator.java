/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.vpc.scholar.hadrumaths.convergence;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.plot.console.params.ParamSet;
import net.vpc.scholar.hadrumaths.util.EnhancedProgressMonitor;
import net.vpc.scholar.hadrumaths.util.ProgressMonitor;
import net.vpc.scholar.hadrumaths.util.VoidMonitoredAction;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vpc
 */
public class ConvergenceEvaluator {

    private ParamSet paramSet;
    private ConvergenceConfig config=new ConvergenceConfig();
    private ConvergenceEvaluator subEvaluator;


    public static ConvergenceEvaluator create(ParamSet[] params, ConvergenceConfig config) {
        ConvergenceEvaluator c = null;
        for (int i = 0; i < params.length; i++) {
            if (c == null) {
                c = new ConvergenceEvaluator(params[i], config);
            } else {
                c = c.combine(params[i]);
            }
        }
        return c;
    }

    public static ConvergenceEvaluator create(ParamSet paramSet, ConvergenceConfig config) {
        return new ConvergenceEvaluator(paramSet, config);
    }

    public static ConvergenceEvaluator create(Param param, Object[] var) {
        return create(Maths.paramSet(param, var));
    }

    public static ConvergenceEvaluator create(Param param, int[] var) {
        return create(Maths.paramSet(param, var));
    }

    public static ConvergenceEvaluator create(Param param, double[] var) {
        return create(Maths.paramSet(param, var));
    }

    public static ConvergenceEvaluator create(Param param, float[] var) {
        return create(Maths.paramSet(param, var));
    }

    public static ConvergenceEvaluator create(Param param, long[] var) {
        return create(Maths.paramSet(param, var));
    }

    public static ConvergenceEvaluator create(ParamSet paramSet) {
        return new ConvergenceEvaluator(paramSet);
    }

    public ConvergenceEvaluator(ParamSet paramSet) {
        this(paramSet, null);
    }

    public ConvergenceEvaluator(ParamSet paramSet, ConvergenceConfig config) {
        this.paramSet = paramSet;
        this.config = config == null ? new ConvergenceConfig() : config;
    }

    public ConvergenceEvaluator combine(ParamSet paramSet, ConvergenceConfig convPars) {
        ConvergenceEvaluator x = new ConvergenceEvaluator(paramSet, convPars);
        x.subEvaluator = this;
        return x;
    }


    public ConvergenceEvaluator combine(ParamSet scf) {
        return combine(scf, config);
    }

    public ConvergenceEvaluator combine(Param scf, Object[] var) {
        return combine(Maths.paramSet(scf,var), config);
    }

    public ConvergenceEvaluator combine(Param scf, int[] var) {
        return combine(Maths.paramSet(scf,var), config);
    }

    public ConvergenceEvaluator combine(Param scf, long[] var) {
        return combine(Maths.paramSet(scf,var), config);
    }

    public ConvergenceEvaluator combine(Param scf, float[] var) {
        return combine(Maths.paramSet(scf,var), config);
    }

    public ConvergenceEvaluator combine(Param scf, double[] var) {
        return combine(Maths.paramSet(scf,var), config);
    }


    public ConvergenceResult evaluate(Object source, ObjectEvaluator evaluator, ProgressMonitor monitor) {
        return evaluate(source, -1, evaluator, monitor);
    }

    public ConvergenceResult evaluate(Object source, int startIndex, ObjectEvaluator evaluator, ProgressMonitor monitor) {
        EnhancedProgressMonitor monitor0 = ProgressMonitorFactory.enhance(monitor);
        //split into  99 and 1  to disable reaching 100% of the evaluation
        if (startIndex < 0) {
            startIndex = 0;
        }
        class ConvInfo{
            Object old = null;
            int max ;
            int endIndex ;
            int index ;
            int securityMax ;
            int security ;
            Map<String, Object> pars = new HashMap<String, Object>();
            ConvergenceResult lastResult;
            ConvergenceResult bestResult = null;
            ConvergenceResult subResult = null;
            int subEvaluatorStartIndex = -1;
            double err = Double.NaN;
        }
        final ConvInfo convInfo=new ConvInfo();
        convInfo.max = config.getMaxIterations();
        convInfo.endIndex = paramSet.getSize();
        convInfo.index = startIndex;
        convInfo.securityMax = config.getStabilityIterations();
        convInfo.security = convInfo.securityMax;
        convInfo.pars = new HashMap<String, Object>();
        convInfo.bestResult = null;
        convInfo.subResult = null;

        double epsilon = config.getThreshold();
        Maths.invokeMonitoredAction(monitor0, "Convergence", new VoidMonitoredAction() {
            @Override
            public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                EnhancedProgressMonitor monitor99=monitor.translate(0.8, 0);
                while (convInfo.index < convInfo.endIndex && (convInfo.max < 0 || convInfo.max > 0)) {
                    if (convInfo.max > 0) {
                        convInfo.max--;
                    }
//            if(subEvaluator==null) {
//                System.out.println(">>" + monitor0.getProgressValue() + " : " + this);
//            }
                    monitor99.start("New Convergence Iteration {0}",convInfo.index);
                    EnhancedProgressMonitor[] mon2 = monitor99.split(new double[]{50, 50}, new boolean[]{subEvaluator != null,true});
                    Object currValue = paramSet.getValue(convInfo.index);
//            pars.put("value", currValue);
//            pars.put("startIndex", startIndex);
//            pars.put("endIndex", endIndex);
//            pars.put("index", index);
//            pars.put("err", err);
//            pars.put("epsilon", epsilon);
//            pars.put("old", old);
//            pars.put("result", sResult);
//            pars.put("stability", securityMax-security);
                    paramSet.getParam().configure(source, currValue);
                    if (subEvaluator != null) {
                        convInfo.subResult = subEvaluator.evaluate(source, convInfo.subEvaluatorStartIndex, evaluator, mon2[0]);
                        convInfo.subEvaluatorStartIndex = convInfo.subResult.getVarIndex();
                    }
//            if(mon2[1].getProgressValue()!=0){
//                mon2[1].getProgressValue();
//            }
                    Object n = evaluator.evaluate(source, mon2[1]);
                    if(mon2[1].isTerminated()) {
                        mon2[1].terminate("Iteration Eval terminated");
                    }
                    if (convInfo.old != null) {
                        convInfo.err = getRelativeError(convInfo.old, n);
                    }
                    convInfo.lastResult = new ConvergenceResult(paramSet.getName(), source, n, convInfo.index, currValue, convInfo.old,convInfo.err, convInfo.pars, epsilon, convInfo.securityMax - convInfo.security, false,convInfo.subResult);
                    if (convInfo.err < epsilon) {
                        if (config.getListener() != null) {
                            config.getListener().progress(convInfo.lastResult.setConfig(config));
                        }
                        if (convInfo.security == convInfo.securityMax) {
                            convInfo.bestResult = new ConvergenceResult(paramSet.getName(), source, n, convInfo.index, currValue, convInfo.old,convInfo.err, convInfo.pars, epsilon, convInfo.securityMax - convInfo.security, true,convInfo.subResult);
                        }
                        convInfo.security--;
                        if (convInfo.security < 0) {
                            if (config.getListener() != null) {
                                config.getListener().progress(convInfo.bestResult.setConfig(config));
                            }
                            break;
                        }
                    } else {
                        if (config.getListener() != null) {
                            config.getListener().progress(convInfo.lastResult.setConfig(config));
                        }
                        convInfo.security = convInfo.securityMax;
                        convInfo.bestResult = convInfo.lastResult;
                    }
                    convInfo.old = n;
                    convInfo.index++;
//            if(subEvaluator==null) {
//                System.out.println("\t>>" + monitor0.getProgressValue() + " : " + this);
//            }

                }
            }
        });

//        if(subEvaluator==null) {
//            System.out.println("\t###>>" + monitor0.getProgressValue() + " : " + this);
//        }
        monitor0.terminate("Convergence "+convInfo.bestResult);
        return convInfo.bestResult;
    }

    public double getRelativeError(Object olderValue, Object newerValue) {
        if (olderValue == null || newerValue == null) {
            return Double.NaN;
        }
        if (olderValue instanceof Normalizable) {
            return ((Normalizable) newerValue).getDistance((Normalizable) olderValue);
        }
        if (olderValue instanceof Number) {
            olderValue = Complex.valueOf(((Number) olderValue).doubleValue());
        }
        if (newerValue instanceof Number) {
            newerValue = Complex.valueOf(((Number) newerValue).doubleValue());
        }
        return ((Normalizable) newerValue).getDistance((Normalizable) olderValue);
    }

    @Override
    public String toString() {
        return "ConvergenceEvaluator{" +
                "paramSet=" + paramSet +
                ", config=" + config +
                ", subEvaluator=" + subEvaluator +
                '}';
    }

    public ConvergenceConfig getConfig() {
        return config;
    }

    public ConvergenceEvaluator setConfig(ConvergenceConfig config) {
        this.config = config == null ? new ConvergenceConfig() : config;
        return this;
    }

    public ConvergenceEvaluator setThreshold(double epsilon) {
        getConfig().setThreshold(epsilon);
        return this;
    }

    public ConvergenceEvaluator setStabilityIterations(int stabilityIterations) {
        getConfig().setStabilityIterations(stabilityIterations);
        return this;
    }

    public ConvergenceEvaluator setMaxIterations(int maxIterations) {
        getConfig().setMaxIterations(maxIterations);
        return this;
    }

    public ConvergenceEvaluator setListener(ConvergenceListener listener) {
        getConfig().setListener(listener);
        return this;
    }

    public ConvergenceEvaluator setListener(PrintStream listener) {
        getConfig().setListener(listener);
        return this;
    }
}
