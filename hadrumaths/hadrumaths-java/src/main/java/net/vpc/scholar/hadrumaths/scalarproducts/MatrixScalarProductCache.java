package net.vpc.scholar.hadrumaths.scalarproducts;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.cache.CacheObjectSerializedForm;
import net.vpc.scholar.hadrumaths.cache.CacheObjectSerializerProvider;
import net.vpc.scholar.hadrumaths.cache.ObjectCache;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToComplex;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToDouble;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToVector;
import net.vpc.scholar.hadrumaths.util.EnhancedProgressMonitor;
import net.vpc.scholar.hadrumaths.util.HFile;
import net.vpc.scholar.hadrumaths.util.ProgressMonitor;
import net.vpc.scholar.hadrumaths.util.VoidMonitoredAction;

import java.io.IOException;

/**
 * implements CacheObjectSerializerProvider because the inner matrix is not necessary serializable
 */
public class MatrixScalarProductCache extends AbstractScalarProductCache implements CacheObjectSerializerProvider {
    boolean doubleValue = true;
    boolean scalarValue = true;
    private Matrix cache;
    private transient MatrixFactory matrixFactory;
    private String name;
    private boolean hermitian;

    private MatrixScalarProductCache(boolean hermitian, boolean doubleValue, boolean scalarValue, Matrix cache, MatrixFactory matrixFactory) {
        if (matrixFactory == null) {
            throw new IllegalArgumentException("Factory should not be null");
        }
        this.cache = cache;
        this.hermitian = hermitian;
        this.doubleValue = doubleValue;
        this.scalarValue = scalarValue;
        this.matrixFactory = matrixFactory;
        name = getClass().getSimpleName() + "(" + matrixFactory.getClass().getSimpleName() + ")";
    }

    public MatrixScalarProductCache(MatrixFactory matrixFactory) {
        if (matrixFactory == null) {
            throw new IllegalArgumentException("Factory should not be null");
        }
        this.matrixFactory = matrixFactory;
        name = getClass().getSimpleName() + "(" + matrixFactory.getClass().getSimpleName() + ")";
    }

    private static Expr[] simplifyAll(Expr[] e, EnhancedProgressMonitor mon) {
        Expr[] all = new Expr[e.length];
        Maths.invokeMonitoredAction(mon, "Simplify All", new VoidMonitoredAction() {
            @Override
            public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                int length = all.length;
                for (int i = 0; i < length; i++) {
                    mon.setProgress(i, length, messagePrefix + " {0}/{1}", (i + 1), length);
                    all[i] = Maths.simplify(e[i]);
                }
            }
        });
        return all;
    }

    @Override
    public CacheObjectSerializedForm createCacheObjectSerializedForm(HFile serFile) throws IOException {
        return new SerMatrixScalarProductCache(hermitian, doubleValue, scalarValue, cache, serFile);
    }

    public Matrix toMatrix() {
        return cache;
    }

    public Vector getColumn(int column) {
        return cache.getColumn(column);
    }

    public Vector getRow(int row) {
        return cache.getRow(row);
    }

    public Complex[][] toArray() {
        return cache.getArrayCopy();
    }

    public Complex apply(int p, int n) {
        return cache.get(p, n);
    }

    public Complex gf(int p, int n) {
        return cache.get(p, n);
    }

    public Complex fg(int n, int p) {
        Complex complex = cache.get(p, n);
        return hermitian ? complex.conj() : complex;
    }

    public ScalarProductCache evaluate(ScalarProductOperator sp, Expr[] fn, Expr[] gp, boolean hermitian, AxisXY axis, ProgressMonitor monitor) {
        EnhancedProgressMonitor emonitor = ProgressMonitorFactory.enhance(monitor);
        String monMessage = name;
        if (sp == null) {
            sp = Maths.Config.getDefaultScalarProductOperator();
        }
        EnhancedProgressMonitor[] hmon = emonitor.split(new double[]{2, 1, 3});
//        if (doSimplifyAll) {
//            Expr[] finalFn = fn;
//            Expr[] finalGp = gp;
//            Expr[][] fg = Maths.invokeMonitoredAction(emonitor, "Simplify All", new MonitoredAction<Expr[][]>() {
//                @Override
//                public Expr[][] process(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
//                    Expr[][] fg = new Expr[2][];
//                    fg[0] = simplifyAll(finalFn, hmon[0]);
//
//                    fg[1] = simplifyAll(finalGp, hmon[1]);
//                    return fg;
//                }
//            });
//            fn = fg[0];
//            gp = fg[1];
//        }
        int maxF = fn.length;
        if (matrixFactory == null) {
            throw new IllegalArgumentException("Factory could not be null");
        }
        Matrix gfps = matrixFactory.newMatrix(gp.length, maxF);
        if (scalarValue) {
            switch (axis) {
                case XY:
                case X: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(hmon[2], (gp.length * maxF));
                    Expr[] finalGp1 = gp;
                    ScalarProductOperator finalSp = sp;
                    Expr[] finalFn1 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            String _monMessage = monMessage + "({0,number,#},{1,number,#})";
                            if (!doubleValue) {
                                for (int q = 0; q < finalGp1.length; q++) {
                                    DoubleToComplex gpq = finalGp1[q].toDC();
                                    for (int n = 0; n < maxF; n++) {
                                        gfps.set(q, n, finalSp.eval(hermitian, gpq, finalFn1[n].toDC()));
                                        mon.inc(_monMessage, q, n);
                                    }
                                }
                            } else {
                                if (finalGp1.length < maxF) {
                                    for (int q = 0; q < finalGp1.length; q++) {
                                        DoubleToDouble gpq = finalGp1[q].toDD();
                                        for (int n = 0; n < maxF; n++) {
                                            gfps.set(q, n, Complex.valueOf(finalSp.evalDD(gpq, finalFn1[n].toDD())));
                                            mon.inc(_monMessage, q, n);
                                        }
                                    }
                                } else {
                                    for (int n = 0; n < maxF; n++) {
                                        DoubleToDouble fnn = finalFn1[n].toDD();
                                        for (int q = 0; q < finalGp1.length; q++) {
                                            DoubleToDouble gpq = finalGp1[q].toDD();
                                            gfps.set(q, n, Complex.valueOf(finalSp.evalDD(gpq, fnn)));
                                            mon.inc(_monMessage, q, n);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    break;
                }
                case Y: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(hmon[2], (gp.length * maxF));
                    Expr[] finalGp2 = gp;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            String _monMessage = monMessage + "({0,number,#},{1,number,#})";
                            for (int q = 0; q < finalGp2.length; q++) {
                                for (int n = 0; n < maxF; n++) {
                                    gfps.set(q, n, Complex.ZERO);
                                    mon.inc(_monMessage, q, n);
                                }
                            }
                        }
                    });
                    break;
                }
            }
        } else {
            switch (axis) {
                case XY: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(hmon[2], (gp.length * maxF));
                    boolean finalDoubleValue1 = doubleValue;
                    Expr[] finalGp3 = gp;
                    ScalarProductOperator finalSp1 = sp;
                    Expr[] finalFn2 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            String _monMessage = monMessage + "({0,number,#},{1,number,#})";

                            if (!finalDoubleValue1) {
                                for (int q = 0; q < finalGp3.length; q++) {
                                    for (int n = 0; n < maxF; n++) {
                                        gfps.set(q, n, finalSp1.eval(hermitian, finalGp3[q], finalFn2[n]));
                                        mon.inc(_monMessage, q, n);
                                    }
                                }
                            } else {
                                for (int q = 0; q < finalGp3.length; q++) {
                                    Expr gpq = finalGp3[q];
                                    DoubleToVector gpqv = gpq.toDV();
                                    for (int n = 0; n < maxF; n++) {
                                        DoubleToVector fnndv = finalFn2[n].toDV();
                                        gfps.set(q, n, Complex.valueOf(
                                                finalSp1.evalDD(gpqv.getComponent(Axis.X).toDC().getReal(), fnndv.getComponent(Axis.X).toDC().getReal())
                                                        + finalSp1.evalDD(gpqv.getComponent(Axis.Y).toDC().getReal(), fnndv.getComponent(Axis.Y).toDC().getReal())));
                                        mon.inc(_monMessage, q, n);
                                    }
                                }
                            }

                        }
                    });

//                System.out.println("c1 = " + c1);
                    break;
                }
                case X: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(hmon[2], (gp.length * maxF));
                    Expr[] finalGp4 = gp;
                    ScalarProductOperator finalSp2 = sp;
                    Expr[] finalFn3 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            if (!doubleValue) {
                                for (int q = 0; q < finalGp4.length; q++) {
                                    for (int n = 0; n < maxF; n++) {
                                        gfps.set(q, n, finalSp2.eval(hermitian, finalGp4[q].toDV().getComponent(Axis.X), finalFn3[n].toDV().getComponent(Axis.X)));
                                        mon.inc(monMessage);
                                    }
                                }
                            } else {
                                for (int q = 0; q < finalGp4.length; q++) {
                                    for (int n = 0; n < maxF; n++) {
                                        gfps.set(q, n, Complex.valueOf(finalSp2.evalDD(finalGp4[q].toDV().getComponent(Axis.X).toDC().getReal(), finalFn3[n].toDV().getComponent(Axis.X).toDC().getReal())));
                                        mon.inc(monMessage);
                                    }
                                }
                            }
                        }
                    });

                    break;
                }
                case Y: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(hmon[2], (gp.length * maxF));
                    boolean finalDoubleValue3 = doubleValue;
                    Expr[] finalGp5 = gp;
                    ScalarProductOperator finalSp3 = sp;
                    Expr[] finalFn4 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            if (!finalDoubleValue3) {
                                for (int q = 0; q < finalGp5.length; q++) {
                                    for (int n = 0; n < maxF; n++) {
                                        gfps.set(q, n, finalSp3.eval(hermitian, finalGp5[q].toDV().getComponent(Axis.Y), finalFn4[n].toDV().getComponent(Axis.Y)));
                                        mon.inc(monMessage);
                                    }
                                }
                            } else {
                                for (int q = 0; q < finalGp5.length; q++) {
                                    for (int n = 0; n < maxF; n++) {
                                        gfps.set(q, n, Complex.valueOf(finalSp3.evalDD(finalGp5[q].toDV().getComponent(Axis.Y).toDC().getReal(), finalFn4[n].toDV().getComponent(Axis.Y).toDC().getReal())));
                                        mon.inc(monMessage);
                                    }
                                }
                            }

                        }
                    });
                    break;
                }
            }
        }
        cache = gfps;
        return this;
    }

    private static class SerMatrixScalarProductCache implements CacheObjectSerializedForm {
        boolean doubleValue = true;
        boolean scalarValue = true;
        private CacheObjectSerializedForm matrix;
        private boolean hermitian;

        public SerMatrixScalarProductCache(boolean hermitian, boolean doubleValue, boolean scalarValue, Matrix cache, HFile file) throws IOException {
            this.matrix = ObjectCache.toSerializedForm(cache, new HFile(file, "matrix"));
            this.hermitian = hermitian;
            this.doubleValue = doubleValue;
            this.scalarValue = scalarValue;
        }

        @Override
        public Object deserialize(HFile file) throws IOException {
            Matrix matrix0 = (Matrix) matrix.deserialize(new HFile(file, "matrix"));
            return new MatrixScalarProductCache(hermitian, doubleValue, scalarValue, matrix0, null);
        }
    }

//    public Complex zop(int p,int q,FnIndexes[] n_evan){
//        Complex c = Complex.ZERO;
//        for (FnIndexes n : n_evan) {
//            c = c.add(n.zn.multiply(gf(p, n)).multiply(fg(n, q)));
//        }
//        return c;
//    }

}
