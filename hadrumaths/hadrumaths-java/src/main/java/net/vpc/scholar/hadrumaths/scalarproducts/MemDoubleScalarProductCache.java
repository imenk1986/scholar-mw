package net.vpc.scholar.hadrumaths.scalarproducts;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToDouble;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToVector;
import net.vpc.scholar.hadrumaths.util.EnhancedProgressMonitor;
import net.vpc.scholar.hadrumaths.util.MonitoredAction;
import net.vpc.scholar.hadrumaths.util.ProgressMonitor;
import net.vpc.scholar.hadrumaths.util.VoidMonitoredAction;

import java.io.Serializable;

public class MemDoubleScalarProductCache extends AbstractScalarProductCache implements Serializable,DoubleScalarProductCache {
    private double[/** p index **/][/** n index **/] cache = new double[0][0];
    private boolean scalarValue;

    public MemDoubleScalarProductCache(boolean scalarValue) {
        this.scalarValue = scalarValue;
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

    public Matrix toMatrix() {
        return Maths.matrix(cache);
    }

    public Vector getColumn(int column) {
        Complex[] vmatrix = new Complex[cache.length];
        for (int i = 0; i < vmatrix.length; i++) {
            vmatrix[i] = Complex.valueOf(cache[i][column]);
        }
        return Maths.columnVector(vmatrix);
    }

    public Vector getRow(int row) {
//        Complex[] vmatrix = new Complex[cache[0].length];
//        System.arraycopy(cache[row], 0, vmatrix, 0, vmatrix.length);
//        return Maths.columnVector(vmatrix);
        return Maths.columnVector(cache[row]);
    }

    public double[][] toArray() {
        return cache;
    }

    public Complex apply(int p, int n) {
        return Complex.valueOf(cache[p][n]);
    }

    public Complex gf(int p, int n) {
        return Complex.valueOf(cache[p][n]);
    }

    public Complex fg(int n, int p) {
        return Complex.valueOf(cache[p][n]);
    }

    public double gfDouble(int p, int n) {
        return cache[p][n];
    }

    public double fgDouble(int n,int p) {
        return cache[p][n];
    }

    public double[] getRowDouble(int p) {
        return cache[p];
    }

    public ScalarProductCache evaluate(ScalarProductOperator sp, Expr[] fn, Expr[] gp, boolean hermitian, AxisXY axis, ProgressMonitor monitor) {
//        EnhancedProgressMonitor emonitor = ProgressMonitorFactory.enhance(monitor);
        String monMessage = getClass().getSimpleName();
        if (sp == null) {
            sp = Maths.Config.getDefaultScalarProductOperator();
        }
//        EnhancedProgressMonitor[] hmon = emonitor.split(new double[]{2, 1, 3});
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
//        int maxG = gp.length;
        double[][] gfps = new double[gp.length][maxF];
        if (scalarValue) {
            switch (axis) {
                case XY:
                case X: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(monitor, gp.length * 2 + maxF);
                    Expr[] finalGp3 = gp;
                    ScalarProductOperator finalSp2 = sp;
                    Expr[] finalFn2 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            DoubleToDouble[] df = new DoubleToDouble[maxF];
                            DoubleToDouble[] dg = new DoubleToDouble[finalGp3.length];
                            for (int n = 0; n < maxF; n++) {
                                df[n] = finalFn2[n].toDD();
                                mon.inc(monMessage);
                            }
                            for (int q = 0; q < finalGp3.length; q++) {
                                dg[q] = finalGp3[q].toDD();
                                mon.inc(monMessage);
                            }
                            for (int q = 0; q < finalGp3.length; q++) {
                                gfps[q] = finalSp2.evalDD(null, dg[q], df); // switching is not a matter, it real!
                                mon.inc(monMessage);
                            }
                        }
                    });

                    break;
                }
                case Y: {
                    //nothing to do , all zeros!
                    break;
                }
            }
        } else {
            switch (axis) {
                case XY: {
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(monitor, (gp.length * maxF));
                    Expr[] finalGp2 = gp;
                    ScalarProductOperator finalSp1 = sp;
                    Expr[] finalFn1 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            String _monMessage = monMessage + "({0,number,#},{1,number,#})";
                            for (int q = 0; q < finalGp2.length; q++) {
                                Expr gpq = finalGp2[q];
                                DoubleToVector gpqv = gpq.toDV();
                                for (int n = 0; n < maxF; n++) {
                                    DoubleToVector fnndv = finalFn1[n].toDV();
                                    gfps[q][n] = (
                                            finalSp1.evalDD(gpqv.getComponent(Axis.X).toDC().getReal(), fnndv.getComponent(Axis.X).toDC().getReal())
                                                    + finalSp1.evalDD(gpqv.getComponent(Axis.Y).toDC().getReal(), fnndv.getComponent(Axis.Y).toDC().getReal()));
                                    mon.inc(_monMessage, q, n);
                                }
                            }

                        }
                    });

//                System.out.println("c1 = " + c1);
                    break;
                }
                default: {
                    Axis aa = axis == AxisXY.X ? Axis.X : axis == AxisXY.Y ? Axis.Y : null;
                    EnhancedProgressMonitor mon = ProgressMonitorFactory.createIncrementalMonitor(monitor, gp.length);
                    Expr[] finalGp3 = gp;
                    ScalarProductOperator finalSp2 = sp;
                    Expr[] finalFn2 = fn;
                    Maths.invokeMonitoredAction(mon, monMessage, new VoidMonitoredAction() {
                        @Override
                        public void invoke(EnhancedProgressMonitor monitor, String monMessage) throws Exception {
                            DoubleToDouble[] df = new DoubleToDouble[maxF];
                            DoubleToDouble[] dg = new DoubleToDouble[finalGp3.length];
                            for (int n = 0; n < maxF; n++) {
                                df[n] = finalFn2[n].toDV().getComponent(aa).toDD();
                            }
                            for (int q = 0; q < finalGp3.length; q++) {
                                dg[q] = finalGp3[q].toDV().getComponent(aa).toDD();
                            }
                            for (int q = 0; q < finalGp3.length; q++) {
                                gfps[q] = finalSp2.evalDD(null, dg[q], df); // switching is not a matter, it real!
                                mon.inc(monMessage);
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

//    public Complex zop(int p,int q,FnIndexes[] n_evan){
//        Complex c = Complex.ZERO;
//        for (FnIndexes n : n_evan) {
//            c = c.add(n.zn.multiply(gf(p, n)).multiply(fg(n, q)));
//        }
//        return c;
//    }

//    private void writeObject(ObjectOutputStream oos)
//            throws IOException {
//        // default serialization
//        oos.defaultWriteObject();
//        int rowCount = cache.length;
//        oos.writeBoolean(hermitian);
//        oos.writeBoolean(doSimplifyAll);
//        oos.writeInt(rowCount);
//        int columnCount = cache.length ==0?0:cache[0].length;
//        oos.writeInt(columnCount);
//        for (int i = 0; i < rowCount; i++) {
//            for (int j = 0; j < columnCount; j++) {
//                Complex.writeObjectHelper(cache[i][j],oos);
//            }
//        }
//    }
//
//    private void readObject(ObjectInputStream ois)
//            throws ClassNotFoundException, IOException {
//        // default deserialization
//        ois.defaultReadObject();
//        hermitian=ois.readBoolean();
//        doSimplifyAll=ois.readBoolean();
//        int rowCount = ois.readInt(); // Replace with real deserialization
//        int columnCount = ois.readInt(); // Replace with real deserialization
//        cache=new Complex[rowCount][columnCount];
//        for (int i = 0; i < rowCount; i++) {
//            for (int j = 0; j < columnCount; j++) {
//                cache[i][j]=Complex.readObjectResolveHelper(ois);
//            }
//        }
//    }


}
