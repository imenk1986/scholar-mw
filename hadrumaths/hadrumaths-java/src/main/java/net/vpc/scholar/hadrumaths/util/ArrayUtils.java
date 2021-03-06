package net.vpc.scholar.hadrumaths.util;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.symbolic.Range;

import java.lang.reflect.Array;
import java.util.*;

public final class ArrayUtils {
    public static final Expr[] EMPTY_EXPR_ARRAY=new Expr[0];
    private ArrayUtils() {
    }

    public static Integer[] box(int[] c) {
        Integer[] r = new Integer[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static int[] unbox(Integer[] c) {
        int[] r = new int[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static Long[] boxLongArray(long[] c) {
        Long[] r = new Long[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static long[] unbox(Long[] c) {
        long[] r = new long[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static Double[] box(double[] c) {
        Double[] r = new Double[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static double[] unboxDoubleList(List<Double> c) {
        double[] r = new double[c.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = c.get(i);
        }
        return r;
    }

    public static double[] complexListToDoubleArray(List<Complex> c) {
        double[] r = new double[c.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = c.get(i).toDouble();
        }
        return r;
    }

    public static double[] exprListToDoubleArray(List<Expr> c) {
        double[] r = new double[c.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = c.get(i).toDouble();
        }
        return r;
    }

    public static double[] complexToDoubleArray(Complex[] c) {
        double[] r = new double[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i].toDouble();
        }
        return r;
    }

    public static double[] exprToDoubleArray(Expr[] c) {
        double[] r = new double[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i].toDouble();
        }
        return r;
    }

    public static double[] unbox(Double[] c) {
        double[] r = new double[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static Float[] box(float[] c) {
        Float[] r = new Float[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static float[] unbox(Float[] c) {
        float[] r = new float[c.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = c[i];
        }
        return r;
    }

    public static double[] abs(double[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = c[i] < 0 ? -c[i] : c[i];
        }
        return ret;
    }

    public static double[][] abs(double[][] c) {
        int len = c.length;
        double[][] ret = new double[len][];
        for (int i = 0; i < len; i++) {
            int clength = c[i].length;
            ret[i] = new double[clength];
            double[] iret = ret[i];
            double[] ic = c[i];
            for (int j = 0; j < clength; j++) {
                double ijc = ic[j];
                iret[j] = ijc < 0 ? -ijc : ijc;
            }
        }
        return ret;
    }

    public static double[] fill(double[] c, double value) {
        for (int i = 0; i < c.length; i++) {
            c[i] = value;
        }
        return c;
    }

    public static double[][] fill(double[][] c, double value) {
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[i].length; j++) {
                c[i][j] = value;
            }
        }
        return c;
    }

    public static double[] addInto(double[] ret, double[] a, double[] b) {
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i] + b[i];
        }
        return ret;
    }

    public static double[] add(double[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] + b[i];
        }
        return ret;
    }

    public static double[] add(double[] a, double b) {
        int max = a.length;
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] + b;
        }
        return ret;
    }

    public static double[] sub(double[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] - b[i];
        }
        return ret;
    }

    public static double[] sub(double[] a, double b) {
        int max = a.length;
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] + b;
        }
        return ret;
    }

    public static double[] div(double[] a, double b) {
        int max = a.length;
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] / b;
        }
        return ret;
    }

    public static double[] mul(double[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] * (b[i]);
        }
        return ret;
    }

    public static double[] mul(double[] a, double b) {
        int max = a.length;
        double[] ret = new double[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] * b;
        }
        return ret;
    }

    public static double[] mulInto(double[] ret, double[] a, double b) {
        int max = ret.length;
        for (int i = 0; i < max; i++) {
            ret[i] = a[i] * (b);
        }
        return ret;
    }


    public static boolean equals(double[] a, double[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(double[][] a, double[][] a2) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i = 0; i < length; i++) {
            if (!Arrays.equals(a[i], a2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Object[][] a, Object[][] a2) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i = 0; i < length; i++) {
            if (!Arrays.equals(a[i], a2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(double[][][] a, double[][][] a2) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i = 0; i < length; i++) {
            if (!equals(a[i], a2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Object[][][] a, Object[][][] a2) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i = 0; i < length; i++) {
            if (!equals(a[i], a2[i])) {
                return false;
            }
        }
        return true;
    }

    public static double[] absdbl(Complex[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = c[i].absdbl();
        }
        return ret;
    }

    public static Complex[] toComplex(double[] c) {
        Complex[] ret = new Complex[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Complex.valueOf(c[i]);
        }
        return ret;
    }

    public static Complex[][] toComplex(double[][] c) {
        Complex[][] ret = new Complex[c.length][];
        for (int i = 0; i < c.length; i++) {
            ret[i] = new Complex[c[i].length];
            for (int j = 0; j < c[i].length; j++) {
                ret[i][j] = Complex.valueOf(c[i][j]);
            }
        }
        return ret;
    }

    public static Complex[][][] toComplex(double[][][] c) {
        Complex[][][] ret = new Complex[c.length][][];
        for (int i = 0; i < c.length; i++) {
            ret[i] = toComplex(c[i]);
        }
        return ret;
    }

    public static double[] getReal(Complex[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = c[i].getReal();
        }
        return ret;
    }

    public static double[] getImag(Complex[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = c[i].getImag();
        }
        return ret;
    }

    public static double[][] absdbl(Complex[][] c) {
        double[][] ret = new double[c.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new double[c[i].length];
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = c[i][j].absdbl();
            }
        }
        return ret;
    }

    public static double[][][] getReal(Complex[][][] c) {
        double[][][] ret = new double[c.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getReal(c[i]);
        }
        return ret;
    }

    public static double[][][] getImag(Complex[][][] c) {
        double[][][] ret = new double[c.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getImag(c[i]);
        }
        return ret;
    }

    public static double[][] getReal(Complex[][] c) {
        double[][] ret = new double[c.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new double[c[i].length];
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = c[i][j].getReal();
            }
        }
        return ret;
    }

    public static double[][] getImag(Complex[][] c) {
        double[][] ret = new double[c.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new double[c[i].length];
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = c[i][j].getImag();
            }
        }
        return ret;
    }

    public static double[][] getDb(Complex[][] c) {
        double[][] ret = new double[c.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new double[c[i].length];
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = Maths.db(c[i][j].absdbl());
            }
        }
        return ret;
    }

    public static double[][] getDb2(Complex[][] c) {
        double[][] ret = new double[c.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new double[c[i].length];
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = Maths.db2(c[i][j].absdbl());
            }
        }
        return ret;
    }

    public static double[][] getArg(Complex[][] c) {
        double[][] ret = new double[c.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new double[c[i].length];
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = (c[i][j]).arg().getReal();
            }
        }
        return ret;
    }

    public static double[] getDb(Complex[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Maths.db(c[i].absdbl());
        }
        return ret;
    }

    public static double[] getDb2(Complex[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Maths.db2(c[i].absdbl());
        }
        return ret;
    }

    public static double[] getArg(Complex[] c) {
        double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = c[i].arg().getReal();
        }
        return ret;
    }

    public static Complex[] fill0(Complex[] c, Complex value) {
        for (int i = 0; i < c.length; i++) {
            c[i] = value;
        }
        return c;
    }

//    public static void main(String[] args) {
//        System.out.println(Maths.chrono(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 1000; i++) {
//                    Complex[][] arr = new Complex[50][50];
//                    fillArray2ZeroComplex0(
//                            arr,new Range(
//                                    1,30,1,30,2,4,3
//                            ));
//
//                }
//            }
//        }));
//
//        System.out.println(Maths.chrono(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 1000; i++) {
//                    Complex[][] arr = new Complex[50][50];
//                    fillArray2ZeroComplex(
//                            arr,new Range(
//                                    1,30,1,30,2,4,3
//                            ));
//
//                }
//            }
//        }));
//
//    }

    //    public static void main0(String[] args) {
//        final int err=1000000000;
//        final int size=100;
//
//
//        System.out.println(
//                Maths.chrono(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < err; i++) {
//                            fill(new Complex[size],Complex.ZERO);
//                        }
//                    }
//                })
//        );
//        System.out.println(
//                Maths.chrono(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < err; i++) {
//                            fill0(new Complex[size],Complex.ZERO);
//                        }
//                    }
//                })
//        );
//
//    }
    public static Complex[] fill(Complex[] c, Complex value) {
        return fill(c, 0, c.length, value);
    }

    public static Complex[] fill(Complex[] c, int from, int to, Complex value) {
        int len = to - from;

        if (len < 100) {
            for (int i = from; i < to; i++) {
                c[i] = value;
            }
            return c;
        }

        if (len > 0) {
            c[from] = value;
        }

        for (int i = 1; i < len; i += i) {
            int j = len - i;
            System.arraycopy(c, from, c, i + from, (j < i) ? j : i);
        }
        return c;
    }

//    public static void main(String[] args) {
//        Complex[][] rr = new Complex[3][2];
//        fill(rr, Complex.ZERO);
//        System.out.println(Arrays.deepToString(rr));
//    }

    public static Complex[][] fillMatrix(Complex[][] sourceMatrix, Complex value) {
        int rowsCount=sourceMatrix==null?0:sourceMatrix.length;
        int columnsCount=0;
        if(rowsCount>0){
            for (Complex[] doubles : sourceMatrix) {
                if(columnsCount<doubles.length){
                    columnsCount=doubles.length;
                }
            }
        }
        for (int i = 0; i < rowsCount; i++) {
            if(sourceMatrix[i].length<columnsCount){
                Complex[] newRef=new Complex[columnsCount];
                System.arraycopy(sourceMatrix[i],0,newRef,0,sourceMatrix[i].length);
                for (int j = sourceMatrix[i].length; j < newRef.length; j++) {
                    newRef[j]=value;
                }
                sourceMatrix[i]=newRef;
            }
        }
        return sourceMatrix;
    }

    public static double[][] fillMatrix(double[][] sourceMatrix, double value) {
        int rowsCount=sourceMatrix==null?0:sourceMatrix.length;
        int columnsCount=0;
        if(rowsCount>0){
            for (double[] doubles : sourceMatrix) {
                if(columnsCount<doubles.length){
                    columnsCount=doubles.length;
                }
            }
        }
        for (int i = 0; i < rowsCount; i++) {
            if(sourceMatrix[i].length<columnsCount){
                double[] newRef=new double[columnsCount];
                System.arraycopy(sourceMatrix[i],0,newRef,0,sourceMatrix[i].length);
                for (int j = sourceMatrix[i].length; j < newRef.length; j++) {
                    newRef[j]=value;
                }
                sourceMatrix[i]=newRef;
            }
        }
        return sourceMatrix;
    }


    public static Complex[][] fill(Complex[][] c, Complex value) {
        int clen = c.length;
        if (clen > 0) {
            fill(c[0], value);
            int c0length = c[0].length;
            for (int i = 1; i < clen; i++) {
                Complex[] ci = c[i];
                if (ci.length == c0length) {
                    System.arraycopy(c[0], 0, ci, 0, c0length);
                } else {
                    fill(ci, value);
                }
            }
        }
        return c;
    }

    public static Complex[][] fillMatrix(Complex[][] c, int colmin, int colmax, int rowmin, int rowmax, Complex value) {
        int cols = colmax - colmin;
        int rows = rowmax - rowmin;
        if (cols > 0 && rows > 0) {
            fill(c[rowmin], colmin, colmax, value);
            Complex[] crowmin = c[rowmin];
            for (int r = rowmin + 1; r < rowmax; r++) {
                System.arraycopy(crowmin, colmin, c[r], colmin, cols);
            }
        }
        return c;
    }

    public static Complex[][][] fill(Complex[][][] arr, Complex value) {
        for (int zi = 0, len=arr.length; zi < len; zi++) {
            fill(arr[zi], value);
        }
        return arr;
    }

    public static Matrix[] fill(Matrix[] c, Matrix value) {
        for (int i = 0,len=c.length; i < len; i++) {
            c[i] = value;
        }
        return c;
    }

    public static Matrix[][] fill(Matrix[][] c, Matrix value) {
        for (int i = 0,len=c.length; i < len; i++) {
            for (int j = 0,ilen=c[i].length; j < ilen; j++) {
                c[i][j] = value;
            }
        }
        return c;
    }

    public static Matrix[][][] fill(Matrix[][][] arr, Matrix value) {
        for (int zi = 0; zi < arr.length; zi++) {
            for (int yi = 0; yi < arr[zi].length; yi++) {
                for (int xi = 0; xi < arr[zi][yi].length; xi++) {
                    arr[zi][yi][xi] = value;
                }
            }
        }
        return arr;
    }

    public static Complex[] addInto(Complex[] ret, Complex[] a, double[] b) {
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i].add(b[i]);
        }
        return ret;
    }

    public static Complex[] addInto(Complex[] ret, Complex[] a, Complex[] b) {
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i].add(b[i]);
        }
        return ret;
    }

    public static Complex[] add(Complex[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].add(b[i]);
        }
        return ret;
    }

    public static Complex[] add(Complex[] a, double b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].add(b);
        }
        return ret;
    }

    public static Complex[] add(Complex[] a, Complex b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].add(b);
        }
        return ret;
    }

    public static Complex[] add(Complex[] a, Complex[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].add(b[i]);
        }
        return ret;
    }

    public static Complex[] sub(Complex[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].sub(b[i]);
        }
        return ret;
    }

    public static Complex[] sub(Complex[] a, double b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].sub(b);
        }
        return ret;
    }

    public static Complex[] sub(Complex[] a, Complex b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].sub(b);
        }
        return ret;
    }

    public static Complex[] sub(Complex[] a, Complex[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].sub(b[i]);
        }
        return ret;
    }

    public static Complex[][] sub(Complex[][] a, Complex[][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][] ret = new Complex[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new Complex[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j].sub(b[i][j]);
            }
        }
        return ret;
    }

    public static double[][] sub(double[][] a, double[][] b) {
        int max = Math.max(a.length, b.length);
        double[][] ret = new double[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new double[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j] - (b[i][j]);
            }
        }
        return ret;
    }

    public static double[][] mul(double[][] a, double[][] b) {
        int max = Math.max(a.length, b.length);
        double[][] ret = new double[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new double[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j] * (b[i][j]);
            }
        }
        return ret;
    }

    public static double[][] div(double[][] a, double[][] b) {
        int max = Math.max(a.length, b.length);
        double[][] ret = new double[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new double[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j] / (b[i][j]);
            }
        }
        return ret;
    }

    public static Complex[][] add(Complex[][] a, Complex[][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][] ret = new Complex[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new Complex[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j].add(b[i][j]);
            }
        }
        return ret;
    }

    public static double[][] add(double[][] a, double[][] b) {
        int max = Math.max(a.length, b.length);
        double[][] ret = new double[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new double[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j] + (b[i][j]);
            }
        }
        return ret;
    }

    public static Complex[][][] add(Complex[][][] a, Complex[][][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][][] ret = new Complex[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = add(a[i], b[i]);
        }
        return ret;
    }

    public static double[][][] add(double[][][] a, double[][][] b) {
        int max = Math.max(a.length, b.length);
        double[][][] ret = new double[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = add(a[i], b[i]);
        }
        return ret;
    }

    public static Complex[][][] sub(Complex[][][] a, Complex[][][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][][] ret = new Complex[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = sub(a[i], b[i]);
        }
        return ret;
    }

    public static double[][][] sub(double[][][] a, double[][][] b) {
        int max = Math.max(a.length, b.length);
        double[][][] ret = new double[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = sub(a[i], b[i]);
        }
        return ret;
    }

    public static double[][][] mul(double[][][] a, double[][][] b) {
        int max = Math.max(a.length, b.length);
        double[][][] ret = new double[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = mul(a[i], b[i]);
        }
        return ret;
    }

    public static double[][][] div(double[][][] a, double[][][] b) {
        int max = Math.max(a.length, b.length);
        double[][][] ret = new double[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = div(a[i], b[i]);
        }
        return ret;
    }

    public static Complex[][][] mul(Complex[][][] a, Complex[][][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][][] ret = new Complex[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = mul(a[i], b[i]);
        }
        return ret;
    }

    public static Complex[][][] div(Complex[][][] a, Complex[][][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][][] ret = new Complex[max][][];
        for (int i = 0; i < max; i++) {
            ret[i] = div(a[i], b[i]);
        }
        return ret;
    }

    public static Complex[][] mul(Complex[][] a, Complex[][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][] ret = new Complex[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new Complex[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j].mul(b[i][j]);
            }
        }
        return ret;
    }

    public static Complex[][] div(Complex[][] a, Complex[][] b) {
        int max = Math.max(a.length, b.length);
        Complex[][] ret = new Complex[max][];
        for (int i = 0; i < max; i++) {
            int maxj = Math.max(a[i].length, b[i].length);
            ret[i] = new Complex[maxj];
            for (int j = 0; j < maxj; j++) {
                ret[i][j] = a[i][j].div(b[i][j]);
            }
        }
        return ret;
    }

    public static Complex[] div(Complex[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].div(b[i]);
        }
        return ret;
    }

    public static Complex[] div(Complex[] a, double b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].div(b);
        }
        return ret;
    }

    public static Complex[] div(Complex[] a, Complex b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].div(b);
        }
        return ret;
    }

    public static Complex[] div(Complex[] a, Complex[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].div(b[i]);
        }
        return ret;
    }

    public static Complex[] mul(Complex[] a, double[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].mul(b[i]);
        }
        return ret;
    }

    public static Complex[] mul(Complex[] a, double b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].mul(b);
        }
        return ret;
    }


    public static Complex[] mul(Complex[] a, Complex b) {
        int max = a.length;
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].mul(b);
        }
        return ret;
    }

    public static Complex[] mulInto(Complex[] ret, Complex[] a, Complex b) {
        int max = ret.length;
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].mul(b);
        }
        return ret;
    }

    public static Complex[] mul(Complex[] a, Complex[] b) {
        int max = Math.max(a.length, b.length);
        Complex[] ret = new Complex[max];
        for (int i = 0; i < max; i++) {
            ret[i] = a[i].mul(b[i]);
        }
        return ret;
    }

    public static Complex[] getColumn(Complex[][] a, int index) {
        int maxi = a.length;
        Complex[] ret = new Complex[maxi];
        for (int i = 0; i < maxi; i++) {
            ret[i] = a[i][index];
        }
        return ret;
    }


    /**
     * @param gfps0
     * @param epsilon
     * @return
     * @deprecated
     */
    public static int[][] findLineDuplicates(Complex[][] gfps0, double epsilon) {
        Map<Integer, ArrayList<Integer>> h = new HashMap<Integer, ArrayList<Integer>>();

        double f1;
        double f2;
        double f12;
//        double fmin;

        for (int i = 0; i < gfps0.length; i++) {
            Complex[] complexes = gfps0[i];
            ArrayList<Integer> a = h.get(i);
            if (a == null) {
                a = new ArrayList<Integer>(3);
                h.put(i, a);
                a.add(i);
            } else {
                continue;
            }
            for (int j = i + 1; j < gfps0.length; j++) {

                double f = 0;
                double fallmax = 0;
                for (int k = 0; k < complexes.length; k++) {
                    f1 = gfps0[i][k].absSquare();
                    f2 = gfps0[j][k].absSquare();
                    f12 = (gfps0[i][k].sub(gfps0[j][k]).absSquare());
                    fallmax = Math.max(fallmax, Math.max(f1, f2));
                    f += f12;
                }
                f = f / fallmax;
                if (f <= epsilon) {
                    a.add(j);
                    h.put(j, a);
//                    for (Iterator<Integer> iterator = a.iterator(); iterator.hasNext();) {
//                        Integer integer = iterator.next();
//                        h.put(integer,a);
//                    }
                } else {
//                    System.out.println((i+1)+"!="+(j+1));
                }
            }
        }
        ArrayList<ArrayList<Integer>> all = new ArrayList<ArrayList<Integer>>();
        TreeSet<Integer> seen = new TreeSet<Integer>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : h.entrySet()) {
            if (!seen.contains(entry.getKey())) {
                all.add(entry.getValue());
                for (Integer integer : entry.getValue()) {
                    seen.add(integer);
                }
            }
        }
        seen.clear();
        int[][] ret = new int[all.size()][];
        for (int i = 0; i < all.size(); i++) {
            ArrayList<Integer> integers = all.get(i);
            int[] line = new int[integers.size()];
            for (int j = 0; j < line.length; j++) {
                line[j] = integers.get(j);
            }
        }
        return ret;
    }

    public static double[] copy(double[] other) {
        if (other == null) {
            return null;
        }
        double[] d = new double[other.length];
        if (d.length > 0) {
            System.arraycopy(other, 0, d, 0, d.length);
        }
        return d;
    }

    public static boolean[] copy(boolean[] other) {
        if (other == null) {
            return null;
        }
        boolean[] d = new boolean[other.length];
        if (d.length > 0) {
            System.arraycopy(other, 0, d, 0, d.length);
        }
        return d;
    }

    public static double[][] copy(double[][] other) {
        if (other == null) {
            return null;
        }
        double[][] d = new double[other.length][];
        if (d.length > 0) {
            for (int i = 0; i < d.length; i++) {
                d[i] = copy(other[i]);
            }
        }
        return d;
    }

    public static boolean[][] copy(boolean[][] other) {
        if (other == null) {
            return null;
        }
        boolean[][] d = new boolean[other.length][];
        if (d.length > 0) {
            for (int i = 0; i < d.length; i++) {
                d[i] = copy(other[i]);
            }
        }
        return d;
    }

    public static double[][][] copy(double[][][] other) {
        if (other == null) {
            return null;
        }
        double[][][] d = new double[other.length][][];
        if (d.length > 0) {
            for (int i = 0; i < d.length; i++) {
                d[i] = copy(other[i]);
            }
        }
        return d;
    }

    public static Complex[] copy(Complex[] other) {
        if (other == null) {
            return null;
        }
        Complex[] d = new Complex[other.length];
        if (d.length > 0) {
            System.arraycopy(other, 0, d, 0, d.length);
        }
        return d;
    }

    public static Complex[][] copy(Complex[][] other) {
        if (other == null) {
            return null;
        }
        Complex[][] d = new Complex[other.length][];
        if (d.length > 0) {
            for (int i = 0; i < d.length; i++) {
                d[i] = copy(other[i]);
            }
        }
        return d;
    }

    public static Complex[][][] copy(Complex[][][] other) {
        if (other == null) {
            return null;
        }
        Complex[][][] d = new Complex[other.length][][];
        if (d.length > 0) {
            for (int i = 0; i < d.length; i++) {
                d[i] = copy(other[i]);
            }
        }
        return d;
    }

    public static double[][] subarray(double[][] values, Range ranges) {
        if (ranges == null) {
            return new double[0][];
        }
        double[][] arr = new double[ranges.ywidth + 1][ranges.xwidth + 1];
        for (int j = ranges.ymin; j <= ranges.ymax; j++) {
            for (int i = ranges.xmin; j <= ranges.xmax; j++) {
                arr[j - ranges.ymin][i - ranges.xmin] = values[j][i];
            }
        }
        return arr;
    }

    public static double[] subarray(double[] values, Range ranges) {
        if (ranges == null) {
            return new double[0];
        }
        double[] arr = new double[ranges.xwidth + 1];
        int xmin = ranges.xmin;
        for (int i = xmin; i <= ranges.xmax; i++) {
            arr[i - xmin] = values[i];
        }
        return arr;
    }

    public static double[][] fillArray2Double(int columns, int rows, double c) {
        double[] firstRow = new double[columns];
        for (int i = 0; i < columns; i++) {
            firstRow[i] = c;
        }
        double[][] r = new double[rows][];
        r[0] = firstRow;
        for (int i = 1; i < r.length; i++) {
            double[] nextRow = new double[columns];
            System.arraycopy(firstRow, 0, nextRow, 0, columns);
            r[i] = nextRow;
        }
        return r;
    }

    public static void fillArray1ZeroComplex(Complex[] arr, Range nonNullRanges) {
        int i = 0;
        while (i < nonNullRanges.xmin) {
            arr[i] = Complex.ZERO;
            i++;
        }
        i = nonNullRanges.xmax + 1;
        while (i < arr.length) {
            arr[i] = Complex.ZERO;
            i++;
        }
    }

    public static void fillArray1ZeroMatrix(Matrix[] arr, Range nonNullRanges, int zeroRows, int zeroColumns) {
        Matrix zero = null;
        if (zeroRows <= 0 && zeroColumns <= 0) {
            for (Matrix m : arr) {
                if (m != null && !m.isScalar()) {
                    zero = Maths.zerosMatrix(m.getRowCount(), m.getColumnCount());
                    break;
                }
            }
        }
        if (zero == null) {
            zero = Maths.zerosMatrix(1, 1);
        }
        int i = 0;
        while (i < nonNullRanges.xmin) {
            arr[i] = zero;
            i++;
        }
        i = nonNullRanges.xmax + 1;
        while (i < arr.length) {
            arr[i] = zero;
            i++;
        }
    }


    public static void fillArray2ZeroComplex0(Complex[][] arr, Range nonNullRanges) {
        int i = 0;
        int rows = arr.length;
        int cols = rows == 0 ? 0 : arr[0].length;
        while (i < nonNullRanges.ymin) {
            int j = 0;
            while (j < cols) {
                arr[i][j] = Complex.ZERO;
                j++;
            }
            i++;
        }
        i = nonNullRanges.ymax + 1;
        while (i < rows) {
            int j = 0;
            while (j < cols) {
                arr[i][j] = Complex.ZERO;
                j++;
            }
            i++;
        }

        i = nonNullRanges.ymin;
        while (i <= nonNullRanges.ymax) {
            int j = 0;
            while (j < nonNullRanges.xmin) {
                arr[i][j] = Complex.ZERO;
                j++;
            }
            j = nonNullRanges.xmax + 1;
            while (j < cols) {
                arr[i][j] = Complex.ZERO;
                j++;
            }
            i++;
        }
    }

    public static void fillArray2ZeroComplex(Complex[][] arr, Range nonNullRanges) {
        int i = 0;
        int rows = arr.length;
        int cols = rows == 0 ? 0 : arr[0].length;
        fillMatrix(arr, 0, cols, 0, nonNullRanges.ymin, Complex.ZERO);
        fillMatrix(arr, 0, cols, nonNullRanges.ymax + 1, rows, Complex.ZERO);
        fillMatrix(arr, 0, nonNullRanges.xmin, nonNullRanges.ymin, nonNullRanges.ymax + 1, Complex.ZERO);
        fillMatrix(arr, nonNullRanges.xmax + 1, cols, nonNullRanges.ymin, nonNullRanges.ymax + 1, Complex.ZERO);
    }

    public static void fillArray3ZeroMatrix(Matrix[][][] arr, Range nonNullRanges, int zeroColumns, int zeroRows) {
        Matrix zero = Maths.zerosMatrix(zeroRows, zeroColumns);
        int i = 0;
        int zlen = arr.length;
        int ylen = zlen == 0 ? 0 : arr[0].length;
        int xlen = ylen == 0 ? 0 : arr[0][0].length;
        while (i < nonNullRanges.zmin) {
            int j = 0;
            while (j < ylen) {
                int k = 0;
                while (j < xlen) {
                    arr[i][j][k] = zero;
                    k++;
                }
                j++;
            }
            i++;
        }
        i = nonNullRanges.zmax;
        while (i < zlen) {
            int j = 0;
            while (j < ylen) {
                int k = 0;
                while (j < xlen) {
                    arr[i][j][k] = zero;
                    k++;
                }
                j++;
            }
            i++;
        }
        i = nonNullRanges.zmin;
        while (i < zlen) {
            fillArray2ZeroMatrix(arr[i], nonNullRanges, zeroRows, zeroColumns);
        }
    }

    public static void fillArray3ZeroComplex(Complex[][][] arr, Range nonNullRanges) {
        Complex zero = Complex.ZERO;
        int i = 0;
        int zlen = arr.length;
        int ylen = zlen == 0 ? 0 : arr[0].length;
        int xlen = ylen == 0 ? 0 : arr[0][0].length;
        while (i < nonNullRanges.zmin) {
            int j = 0;
            while (j < ylen) {
                int k = 0;
                while (k < xlen) {
                    arr[i][j][k] = zero;
                    k++;
                }
                j++;
            }
            i++;
        }
        i = nonNullRanges.zmax + 1;
        while (i < zlen) {
            int j = 0;
            while (j < ylen) {
                int k = 0;
                while (k < xlen) {
                    arr[i][j][k] = zero;
                    k++;
                }
                j++;
            }
            i++;
        }
        i = nonNullRanges.zmin;
        while (i < zlen) {
            fillArray2ZeroComplex(arr[i], nonNullRanges);
            i++;
        }
    }

    public static void fillArray2ZeroMatrix(Matrix[][] arr, Range nonNullRanges, int zeroRows, int zeroColumns) {
        Matrix zero = null;
        if (zeroRows <= 0 && zeroColumns <= 0) {
            LOOP1:
            for (Matrix[] mm : arr) {
                for (Matrix m : mm) {
                    if (m != null && !m.isScalar()) {
                        zero = Maths.zerosMatrix(m.getRowCount(), m.getColumnCount());
                        break LOOP1;
                    }
                }
            }
        }
        if (zero == null) {
            zero = Maths.zerosMatrix(1, 1);
        }
        int i = 0;
        int rows = arr.length;
        int cols = rows == 0 ? 0 : arr[0].length;
        while (i < nonNullRanges.ymin) {
            int j = 0;
            while (j < cols) {
                arr[i][j] = zero;
                j++;
            }
            i++;
        }
        i = nonNullRanges.ymax + 1;
        while (i < rows) {
            int j = 0;
            while (j < cols) {
                arr[i][j] = zero;
                j++;
            }
            i++;
        }

        i = nonNullRanges.ymin;
        while (i < nonNullRanges.ymax) {
            int j = 0;
            while (j < nonNullRanges.xmin) {
                arr[i][j] = zero;
                j++;
            }
            j = nonNullRanges.xmax + 1;
            while (j < cols) {
                arr[i][j] = zero;
                j++;
            }
            i++;
        }
    }

    public static double[][][] fillArray3Double(int x, int y, int z, double c) {
        if (c == 0) {
            return new double[z][y][z];
        }
        double[][][] ret = new double[z][][];
        for (int i = 0; i < z; i++) {
            ret[i] = fillArray2Double(x, y, c);
        }
        return ret;
    }

    public static Complex[][][] fillArray3Complex(int x, int y, int z, Complex c) {
        Complex[][][] ret = new Complex[z][][];
        for (int i = 0; i < z; i++) {
            ret[i] = fillArray2Complex(x, y, c);
        }
        return ret;
    }

    public static Matrix[][][] fillArray3Matrix(int x, int y, int z, Matrix c) {
        Matrix[][][] ret = new Matrix[z][][];
        for (int i = 0; i < z; i++) {
            ret[i] = fillArray2Matrix(x, y, c);
        }
        return ret;
    }

    public static Complex[] fillArray1Complex(int x, Complex c) {
        Complex[] firstRow = new Complex[x];
        for (int i = 0; i < x; i++) {
            firstRow[i] = c;
        }
        return firstRow;
    }

    public static Matrix[] fillArray1Matrix(int x, Matrix c) {
        Matrix[] firstRow = new Matrix[x];
        for (int i = 0; i < x; i++) {
            firstRow[i] = c;
        }
        return firstRow;
    }

    public static Complex[][] fillArray2Complex(int x, int y, Complex c) {
        Complex[] firstRow = new Complex[x];
        for (int i = 0; i < x; i++) {
            firstRow[i] = c;
        }
        Complex[][] r = new Complex[y][];
        if (r.length > 0) {
            r[0] = firstRow;
            for (int i = 1; i < r.length; i++) {
                Complex[] nextRow = new Complex[x];
                System.arraycopy(firstRow, 0, nextRow, 0, x);
                r[i] = nextRow;
            }
        }
        return r;
    }

    public static Matrix[][] fillArray2Matrix(int columns, int rows, Matrix c) {
        Matrix[] firstRow = new Matrix[columns];
        for (int i = 0; i < columns; i++) {
            firstRow[i] = c;
        }
        Matrix[][] r = new Matrix[rows][];
        if (r.length > 0) {
            r[0] = firstRow;
            for (int i = 1; i < r.length; i++) {
                Matrix[] nextRow = new Matrix[columns];
                System.arraycopy(firstRow, 0, nextRow, 0, columns);
                r[i] = nextRow;
            }
        }
        return r;
    }


    public static int hashCode(Object[][] a) {
        if (a == null)
            return 0;

        int result = 1;
        for (Object[] element : a) {
            long bits = Arrays.hashCode(element);
            result = 31 * result + (int) (bits ^ (bits >>> 32));
        }
        return result;
    }

    public static <T> T[] newArray(TypeReference<T> type, int size){
        return (T[])Array.newInstance(type.getTypeClass(),size);
    }
    public static <T> T[][] newArray(TypeReference<T> type,int size,int size2){
        return (T[][]) Array.newInstance(type.getTypeClass(),size,size2);
    }
    public static <T> T[] newArray(Class type,int size){
        return (T[]) Array.newInstance(type,size);
    }

    public static <T> T[][] newArray(Class type,int size,int size2){
        return (T[][]) Array.newInstance(type,size,size2);
    }
}
