package net.vpc.scholar.hadrumaths;

import net.vpc.scholar.hadrumaths.util.ArrayUtils;
import net.vpc.scholar.hadrumaths.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * User: taha Date: 2 juil. 2003 Time: 10:40:39
 */
public abstract class AbstractTMatrix<T> implements TMatrix<T> {

    private static final long serialVersionUID = -1010101010101001044L;
    private transient TMatrixFactory<T> factory;
    private transient String factoryId;

    public AbstractTMatrix() {
    }

    /*To exchange two rows in a matrix*/
    public static <T> void exchange_row(TMatrix<T> M, int k, int l, int m, int n) {
        if (k <= 0 || l <= 0 || k > n || l > n || k == l) {
            return;
        }
        T tmp;
        for (int j = 0; j < n; j++) {
            tmp = M.get(k - 1, j);
            M.set(k - 1, j, M.get(l - 1, j));
            M.set(l - 1, j, tmp);
        }
    }

    public double norm() {
        return norm(NormStrategy.DEFAULT);
    }

    public double norm(NormStrategy ns) {
        switch (ns) {
            case DEFAULT:
            case NORM2: {
                return norm2();
            }
            case NORM3: {
                return norm3();
            }
            case NORM1: {
                return norm1();
            }
            case NORM_INF: {
                return normInf();
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * One norm
     *
     * @return maximum column sum.
     */
    public double norm1() {
        double f = 0;
        int columnDimension = getColumnCount();
        int rows = getRowCount();
        for (int c = 0; c < columnDimension; c++) {
            double s = 0;
            for (int r = 0; r < rows; r++) {
                s += getComponentVectorSpace().absdbl(get(r, c));
            }
            f = Math.max(f, s);
        }
        return f;
    }

    public double norm2() {
        double f = 0;
        int columnDimension = getColumnCount();
        int rows = getRowCount();
        for (int c = 0; c < columnDimension; c++) {
            for (int r = 0; r < rows; r++) {
                f += Maths.sqr(getComponentVectorSpace().absdbl(get(r, c)));
            }
        }
        return Math.sqrt(f);
    }

    /**
     * One norm
     *
     * @return maximum elemet absdbl.
     */
    public double norm3() {
        double f = 0;
        int columnDimension = getColumnCount();
        int rows = getRowCount();
        for (int j = 0; j < columnDimension; j++) {
            for (int r = 0; r < rows; r++) {
                f = Math.max(f, getComponentVectorSpace().absdbl(get(r, j)));
            }
        }
        return f;
    }

    public double getError(TMatrix<T> baseMatrix) {
        double norm = baseMatrix.norm(NormStrategy.DEFAULT);
        if (norm == 0) {
            return this.norm(NormStrategy.DEFAULT);
        }
        return (this.sub(baseMatrix).norm(NormStrategy.DEFAULT) / norm);
    }

    @Override
    public double getDistance(Normalizable other) {
        return getError((TMatrix<T>) other);
    }

    public DMatrix getErrorMatrix(TMatrix<T> baseMatrix, double minErrorForZero, ErrorMatrixStrategy strategy) {
        switch (strategy) {
            case ABSOLUTE: {
                return getErrorMatrix(baseMatrix, minErrorForZero);
            }
            case RELATIVE: {
                return getErrorMatrix2(baseMatrix, minErrorForZero);
            }
            case RELATIVE_RI: {
                return getErrorMatrix3(baseMatrix, minErrorForZero);
            }
        }
        throw new IllegalArgumentException("Unsupported");
    }

    public DMatrix getErrorMatrix(TMatrix<T> baseMatrix) {
        return getErrorMatrix(baseMatrix, Double.NaN);
    }

    public DMatrix getErrorMatrix(TMatrix<T> baseMatrix, double minErrorForZero) {
        TMatrix<T> m = baseMatrix.sub(this).div(baseMatrix.norm3());
        T[][] mm = m.getArray();
        double[][] d = new double[mm.length][mm[0].length];
        for (int i = 0; i < mm.length; i++) {
            T[] complexes = mm[i];
            for (int j = 0; j < complexes.length; j++) {
                if (!Double.isNaN(minErrorForZero) && getComponentVectorSpace().absdbl(complexes[j]) < minErrorForZero) {
                    d[i][j] = 0;
                } else {
                    d[i][j] = getComponentVectorSpace().absdbl(complexes[j]);
                }
            }
        }
        return new DMatrix(d);
    }

    /**
     * term by term
     *
     * @param baseMatrix
     * @param minErrorForZero
     * @return
     */
    public DMatrix getErrorMatrix2(TMatrix<T> baseMatrix, double minErrorForZero) {
        TMatrix<T> m = baseMatrix.sub(this);
        T[][] mm = m.getArray();
        double[][] d = new double[mm.length][mm[0].length];
        for (int i = 0; i < mm.length; i++) {
            T[] complexes = mm[i];
            for (int j = 0; j < complexes.length; j++) {
                double baseAbs = getComponentVectorSpace().absdbl(baseMatrix.get(i, j));
                double newAbs = getComponentVectorSpace().absdbl(complexes[j]);
                double dd = baseAbs == 0 ? newAbs : (newAbs / baseAbs);
                if (!Double.isNaN(minErrorForZero) && dd < minErrorForZero) {
                    d[i][j] = 0;
                } else {
                    d[i][j] = dd;
                }
            }
        }
        return new DMatrix(d);
    }

    public DMatrix getErrorMatrix3(TMatrix<T> baseMatrix, double minErrorForZero) {
        TMatrix<T> m = baseMatrix.sub(this);
        T[][] mm = m.getArray();
        double[][] d = new double[mm.length][mm[0].length];
        for (int i = 0; i < mm.length; i++) {
            T[] complexes = mm[i];
            for (int j = 0; j < complexes.length; j++) {
                T base = baseMatrix.get(i, j);
                T val = complexes[j];
                double baseR = getComponentVectorSpace().absdbl(getComponentVectorSpace().real(base));
                double baseI = getComponentVectorSpace().absdbl(getComponentVectorSpace().imag(base));
                double valR = getComponentVectorSpace().absdbl(getComponentVectorSpace().real(val));
                double valI = getComponentVectorSpace().absdbl(getComponentVectorSpace().imag(val));
                double ddR = baseR == 0 ? valR : (valR / baseR);
                double ddI = baseI == 0 ? valI : (valI / baseI);
                double dd = Math.max(ddR, ddI);
                if (!Double.isNaN(minErrorForZero) && dd < minErrorForZero) {
                    d[i][j] = 0;
                } else {
                    d[i][j] = dd;
                }
            }
        }
        return new DMatrix(d);
    }

    /**
     * Infinity norm
     *
     * @return maximum row sum.
     */
    public double normInf() {
        double f = 0;
        int rows = getRowCount();
        int columns = getColumnCount();
        for (int r = 0; r < rows; r++) {
            double s = 0;
            for (int c = 0; c < columns; c++) {
                s += getComponentVectorSpace().absdbl(get(r, c));
            }
            f = Math.max(f, s);
        }
        return f;
    }

    @Override
    public T avg() {
        T f = getComponentVectorSpace().zero();
        int rows = getRowCount();
        int columns = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                f = getComponentVectorSpace().add(f, get(r, c));
            }
        }
        f = getComponentVectorSpace().div(f, getComponentVectorSpace().convert(rows * columns));
        return f;
    }

    @Override
    public T sum() {
        T f = getComponentVectorSpace().zero();
        int rows = getRowCount();
        int columns = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                f = getComponentVectorSpace().add(f, get(r, c));
            }
        }
        return f;
    }

    @Override
    public T prod() {
        T f = getComponentVectorSpace().zero();
        int rows = getRowCount();
        int columns = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                f = getComponentVectorSpace().mul(f, get(r, c));
            }
        }
        return f;
    }

    public void setAll(T value) {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                set(i, j, value);
            }
        }
    }

    public T[][] getArray() {
        T[][] arr = newT(getRowCount(), getColumnCount());
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[0].length; c++) {
                arr[r][c] = get(r, c);
            }
        }
        return arr;
    }

    protected TMatrix<T> createMatrix(int rows, int cols) {
        TMatrixFactory<T> f = getFactory();
        if (f == null) {
            f = Maths.Config.getDefaultMatrixFactory(getComponentType());
        }
        return f.newMatrix(rows, cols);
    }

    /**
     * Get a submatrix.
     * TODO check me pleaze
     *
     * @param i0 Initial row index
     * @param i1 Final row index
     * @param j0 Initial column index
     * @param j1 Final column index
     * @return A(i0:i1, j0:j1)
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */
    public TMatrix<T> getMatrix(int i0, int i1, int j0, int j1) {
        TMatrix<T> X = createMatrix(i1 - i0 + 1, j1 - j0 + 1);
        //T[][] B = X.elements;// X.getArray();
        try {
            for (int i = i0; i <= i1; i++) {

                for (int k = 0; k < j1 + 1 - j0; k++) {
                    X.set(i - i0, k, get(i, j0 + k));
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     *
     * @param r Array of row indices.
     * @param c Array of column indices.
     * @return A(r(:), c(:))
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */
    public TMatrix<T> getMatrix(int[] r, int[] c) {
        TMatrix<T> X = createMatrix(r.length, c.length);
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < c.length; j++) {
                    X.set(i, j, get(r[i], c[j]));
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     *
     * @param r1 Initial row index
     * @param r2 Final row index
     * @param c  Array of column indices.
     * @return A(i0:i1, c(:))
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */
    public TMatrix<T> getMatrix(int r1, int r2, int[] c) {
        TMatrix<T> X = createMatrix(r2 - r1 + 1, c.length);
        try {
            for (int i = r1; i <= r2; i++) {
                for (int j = 0; j < c.length; j++) {
                    X.set(i - r1, j, get(i, c[j]));
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    /**
     * Get a submatrix.
     *
     * @param r  Array of row indices.
     * @param c1 Initial column index
     * @param c2 Final column index
     * @return A(r(:), j0:j1)
     * @throws ArrayIndexOutOfBoundsException Submatrix indices
     */
    public TMatrix<T> getMatrix(int[] r, int c1, int c2) {
        TMatrix<T> X = createMatrix(r.length, c2 - c1 + 1);
        try {
            for (int i = 0; i < r.length; i++) {
                for (int j = c1; j <= c2; j++) {
                    X.set(i, j - c1, get(r[i], j));
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    public TMatrix<T> div(double c) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().div(get(i, j), getComponentVectorSpace().convert(c)));
            }
        }
        return X;
    }

    public TMatrix<T> div(T c) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().div(get(i, j), c));
            }
        }
        return X;
    }

    public TMatrix<T> mul(T c) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().mul(get(i, j), c));
            }
        }
        return X;
    }

    public TMatrix<T> mul(double c) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().mul(get(i, j), getComponentVectorSpace().convert(c)));
            }
        }
        return X;
    }

    public TMatrix<T> neg() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().neg(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> add(T c) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().add(get(i, j), c));
            }
        }
        return X;
    }

    public TMatrix<T> sub(T c) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sub(get(i, j), c));
            }
        }
        return X;
    }

    public TMatrix<T> div(TMatrix<T> other) {
        return mul(other.inv());
    }

    public TMatrix<T> mul(TMatrix<T> other) {
        if(getColumnCount()!=other.getRowCount()){
            throw new IllegalArgumentException("The column dimension "+getColumnCount()+" of the left matrix does not match the row dimension "+other.getRowCount()+" of the right matrix!");
        }
        int a_rows = getRowCount();
        int b_cols = other.getColumnCount();
        int b_rows = other.getRowCount();
        TMatrix<T> newElements = createMatrix(a_rows, b_cols);
        for (int i = 0; i < a_rows; i++) {
            for (int j = 0; j < b_cols; j++) {
                T sum = getComponentVectorSpace().zero();
                for (int k = 0; k < b_rows; k++) {
                    sum = getComponentVectorSpace().add(sum, getComponentVectorSpace().mul(get(i, k), (other.get(k, j))));
                }
                newElements.set(i, j, sum);
            }
        }
        return newElements;
    }

    public TMatrix<T> mul(T[][] other) {
        int a_rows = getRowCount();
        int b_cols = other[0].length;
        int b_rows = other.length;
        TMatrix<T> newElements = createMatrix(a_rows, b_cols);
        for (int i = 0; i < a_rows; i++) {
            for (int j = 0; j < b_cols; j++) {
                T sum = getComponentVectorSpace().zero();
                for (int k = 0; k < b_rows; k++) {
                    sum = getComponentVectorSpace().add(sum, getComponentVectorSpace().mul(get(i, k), (other[k][j])));
                }
                newElements.set(i, j, sum);
            }
        }
        return newElements;
    }

    public TMatrix<T> dotmul(TMatrix<T> other) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().mul(get(i, j), other.get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> dotdiv(TMatrix<T> other) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().div(get(i, j), other.get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> conj() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().conj(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> dotinv() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().inv(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> cos() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().cos(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> acos() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().acos(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> asin() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().asin(get(i, j)));
            }
        }
        return X;
    }


    public TMatrix<T> sin() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sin(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> acosh() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().acosh(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> cosh() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().cosh(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> asinh() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().asinh(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> sinh() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sinh(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> arg() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().arg(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> atan() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().atan(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> cotan() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().cotan(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> tan() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().tan(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> tanh() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().tanh(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> cotanh() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().cotanh(get(i, j)));
            }
        }
        return X;
    }

    //    public Matrix dotabs() {
//        int rows = getRowCount();
//        int columns = getColumnCount();
//        Matrix X = createMatrix(rows, columns);
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                X.set(i, j, new Complex(get(i, j).absdbl()));
//            }
//        }
//        return X;
//    }
//
    public TMatrix<T> getImag() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, (getComponentVectorSpace().imag(get(i, j))));
            }
        }
        return X;
    }

    @Override
    public TMatrix<T> imag() {
        return getImag();
    }

    @Override
    public TMatrix<T> real() {
        return getReal();
    }

    public TMatrix<T> getReal() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().real(get(i, j)));
            }
        }
        return X;
    }

//    public double[][] getDoubleArray() {
//        int rows = getRowCount();
//        int columns = getColumnCount();
//        double[][] X = new double[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                X[i][j] = get(i, j).toDouble();
//            }
//        }
//        return X;
//    }
//
//    public double[][] getRealArray() {
//        int rows = getRowCount();
//        int columns = getColumnCount();
//        double[][] X = new double[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                X[i][j] = get(i, j).getReal();
//            }
//        }
//        return X;
//    }
//
//    public double[][] getImagArray() {
//        int rows = getRowCount();
//        int columns = getColumnCount();
//        double[][] X = new double[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                X[i][j] = get(i, j).getReal();
//            }
//        }
//        return X;
//    }
//
//    public double[][] getAbsArray() {
//        int rows = getRowCount();
//        int columns = getColumnCount();
//        double[][] X = new double[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                X[i][j] = get(i, j).absdbl();
//            }
//        }
//        return X;
//    }
//
//    public double[][] getAbsSquareArray() {
//        int rows = getRowCount();
//        int columns = getColumnCount();
//        double[][] X = new double[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                X[i][j] = Maths.sqr(get(i, j).absdbl());
//            }
//        }
//        return X;
//    }

    public TMatrix<T> acotan() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().acotan(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> exp() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().exp(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> log() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().log(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> log10() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().log10(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> db() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().db(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> db2() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().db2(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> dotsqr() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sqr(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> dotsqrt() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sqrt(get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> dotsqrt(int n) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sqrt(get(i, j), n));
            }
        }
        return X;
    }

    public TMatrix<T> dotnpow(int n) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().npow(get(i, j), n));
            }
        }
        return X;
    }

    public TMatrix<T> dotpow(double n) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().pow(get(i, j), n));
            }
        }
        return X;
    }

    public TMatrix<T> dotpow(T n) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().pow(get(i, j), n));
            }
        }
        return X;
    }

    public TMatrix<T> add(TMatrix<T> other) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().add(get(i, j), other.get(i, j)));
            }
        }
        return X;
    }

    public TMatrix<T> add(T[][] other) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().add(get(i, j), other[i][j]));
            }
        }
        return X;
    }

    public TMatrix<T> sub(T[][] other) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sub(get(i, j), other[i][j]));
            }
        }
        return X;
    }

    public TMatrix<T> sub(TMatrix<T> other) {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, getComponentVectorSpace().sub(get(i, j), other.get(i, j)));
            }
        }
        return X;
    }

    public T getScalar() {
        return get(0, 0);
    }

    public ComponentDimension getComponentDimension() {
        return ComponentDimension.create(
                getRowCount(),
                getColumnCount()
        );
    }

    public T get(int vectorIndex) {
        if (getColumnCount() == 1) {
            return get(vectorIndex, 0);
        }
        if (getRowCount() == 1) {
            return get(0, vectorIndex);
        }
        throw new IllegalArgumentException("No a valid vector");
    }

    public void add(int row, int col, T val) {
        set(row, col, getComponentVectorSpace().add(get(row, col), val));
    }

    public void mul(int row, int col, T val) {
        set(row, col, getComponentVectorSpace().mul(get(row, col), val));
    }

    public void div(int row, int col, T val) {
        set(row, col, getComponentVectorSpace().div(get(row, col), val));
    }

    public void sub(int row, int col, T val) {
        set(row, col, getComponentVectorSpace().sub(get(row, col), val));
    }


    public void set(int row, int col, TMatrix<T> src, int srcRow, int srcCol, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                set(r + row, col + c, src.get(r + srcRow, c + srcCol));
            }
        }
    }

    public void set(int row, int col, TMatrix<T> subMatrix) {
        set(row, col, subMatrix, 0, 0, subMatrix.getRowCount(), subMatrix.getColumnCount());
    }

    public TMatrix<T> subMatrix(int row, int col, int rows, int cols) {
        TMatrix<T> m = createMatrix(rows, cols);
        m.set(row, col, this);
        return m;
    }

    @Override
    public TVector<TVector<T>> getRows() {
        return (TVector<TVector<T>>) Maths.<TVector<T>>columnTVector(TypeReference.of(TVector.class, getComponentType().getType()), new TVectorModel<TVector<T>>() {
            @Override
            public int size() {
                return getRowCount();
            }

            @Override
            public TVector<T> get(int index) {
                return getRow(index);
            }
        });
    }

    @Override
    public TVector<TVector<T>> getColumns() {
        return (TVector<TVector<T>>) Maths.<TVector<T>>columnTVector(TypeReference.of(TVector.class, getComponentType().getType()), new TVectorModel<TVector<T>>() {
            @Override
            public int size() {
                return getColumnCount();
            }

            @Override
            public TVector<T> get(int index) {
                return getColumn(index);
            }
        });
    }

    public TMatrix<T> transpose() {
        return arrayTranspose();
    }

    /**
     * @return equivalent to transposeHermitian
     */
    public TMatrix<T> transjugate() {
        return transposeHermitian();
    }

    /**
     * @return equivalent to transposeHermitian
     */
    public TMatrix<T> transposeConjugate() {
        return transposeHermitian();
    }

    /**
     * Hermitian conjugate
     *
     * @return
     */
    public TMatrix<T> transposeHermitian() {
        int r = getRowCount();
        if (r == 0) {
            return this;
        }
        int c = getColumnCount();
        TMatrix<T> e = createMatrix(c, r);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                e.set(j, i, getComponentVectorSpace().conj(get(i, j)));
            }
        }
        return e;
    }

    public TMatrix<T> arrayTranspose() {
        int r = getRowCount();
        if (r == 0) {
            return this;
        }
        int c = getColumnCount();
        TMatrix<T> e = createMatrix(c, r);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                e.set(j, i, get(i, j));
            }
        }
        return e;
    }

    public TMatrix<T> invCond() {
        return inv(Maths.Config.getDefaultMatrixInverseStrategy(), ConditioningStrategy.DEFAULT, NormStrategy.DEFAULT);
    }

    public TMatrix<T> inv(InverseStrategy invStr, ConditioningStrategy condStr, NormStrategy normStr) {
        switch (condStr) {
            case DEFAULT:
            case NORM: {
                double n = norm(normStr);
                return div(n).inv(invStr).div(n);
            }
            case NONE: {
                return inv(invStr);
            }
        }
        throw new UnsupportedOperationException();
    }

    public TMatrix<T> inv() {
        return inv(Maths.Config.getDefaultMatrixInverseStrategy());
    }

    public TMatrix<T> inv(InverseStrategy st) {
        switch (st) {
            case DEFAULT: {
                return inv(Maths.Config.getDefaultMatrixInverseStrategy());
            }
            case BLOCK_SOLVE: {
                return invBlock(InverseStrategy.SOLVE, Maths.Config.getMatrixBlockPrecision());
            }
            case BLOCK_ADJOINT: {
                return invBlock(InverseStrategy.ADJOINT, Maths.Config.getMatrixBlockPrecision());
            }
            case BLOCK_GAUSS: {
                return invBlock(InverseStrategy.GAUSS, Maths.Config.getMatrixBlockPrecision());
            }
            case SOLVE: {
                return invSolve();
            }
            case ADJOINT: {
                return invAdjoint();
            }
            case GAUSS: {
                return invGauss();
            }
//            case BLOCK_OJALGO: {
//                return invBlock(InverseStrategy.OJALGO, 64);
//            }
//            case OJALGO: {
//                throw new IllegalArgumentException("Unsupported");
////                return OjalgoHelper.INSTANCE.inv(this);
//            }
        }
        throw new UnsupportedOperationException("strategy " + st.toString());
    }

    public TMatrix<T> invSolve() {
        return solve(getFactory().newIdentity(getRowCount()));
    }

    private T[] newT(int size) {
        return ArrayUtils.newArray(getComponentType(), size);
    }

    private T[][] newT(int size1, int size2) {
        return ArrayUtils.newArray(getComponentType(), size1, size2);
    }

    /**
     * inversion using Bloc Matrix expansion
     *
     * @return inverse
     */
    public TMatrix<T> invBlock(InverseStrategy delegate, int precision) {
        int n = getRowCount();
        int p = getColumnCount();
        if (n != p || (n > 1 && n % 2 != 0) || (precision > 1 && n > 1 && n <= precision)) {
            return inv(delegate);
        }
        switch (n) {
            case 1: {
                T[][] ts = newT(1, 1);
                ts[0][0] = getComponentVectorSpace().inv(get(0, 0));
                return getFactory().newMatrix(ts);
            }
            case 2: {
                T A = get(0, 0);
                T B = get(0, 1);
                T C = get(1, 0);
                T D = get(1, 1);
                T Ai = getComponentVectorSpace().inv(A);
                T CAi = getComponentVectorSpace().mul(C, Ai);
                T AiB = getComponentVectorSpace().mul(Ai, B);
                T DCABi = getComponentVectorSpace().inv(getComponentVectorSpace().sub(D, getComponentVectorSpace().mul(getComponentVectorSpace().mul(C, Ai), B)));
                T mDCABi = getComponentVectorSpace().mul(DCABi, minusOne());
                T AiBmDCABi = getComponentVectorSpace().mul(AiB, mDCABi);
                T[][] t = newT(2, 2);
                t[0][0] = getComponentVectorSpace().sub(Ai, (getComponentVectorSpace().mul(AiBmDCABi, CAi)));
                t[0][1] = AiBmDCABi;
                t[1][0] = getComponentVectorSpace().mul(mDCABi, CAi);
                t[1][1] = DCABi;
                return getFactory().newMatrix(t);
            }
            default: {
                int n2 = n / 2;
                TMatrix<T> A = getMatrix(0, n2 - 1, 0, n2 - 1);
                TMatrix<T> B = getMatrix(n2, n - 1, 0, n2);
                TMatrix<T> C = getMatrix(0, n2 - 1, n2, n - 1);
                TMatrix<T> D = getMatrix(n2, n - 1, n2, n - 1);
                TMatrix<T> Ai = A.invBlock(delegate, precision);
                TMatrix<T> CAi = C.mul(Ai);
                TMatrix<T> AiB = Ai.mul(B);
                TMatrix<T> DCABi = D.sub(C.mul(Ai).mul(B)).invBlock(delegate, precision);
                TMatrix<T> mDCABi = DCABi.mul(-1);
                TMatrix<T> AiBmDCABi = AiB.mul(mDCABi);
                TMatrix<T> mm = createMatrix(getRowCount(), getColumnCount());
                mm.set(new TMatrix[][]{
                        {Ai.sub(AiBmDCABi.mul(CAi)), AiBmDCABi},
                        {mDCABi.mul(CAi), DCABi}
                });
                return mm;
            }
            //case 2?
        }
    }

    public TMatrix<T> invGauss() {
        int m = getRowCount();
        int n = getColumnCount();
        if (m != n) {
            throw new ArithmeticException("Determinant Equals 0, Not Invertible.");
        }

        //Pour stocker les lignes pour lesquels un pivot a déjà été trouvé
        ArrayList<Integer> I = new ArrayList<Integer>();

        //Pour stocker les colonnes pour lesquels un pivot a déjà été trouvé
        ArrayList<Integer> J = new ArrayList<Integer>();

        //Pour calculer l'inverse de la matrice initiale
        TMatrix<T> A = createMatrix(m, n);
        TMatrix<T> B = createMatrix(m, n);

        //Copie de M dans A et Mise en forme de B : B=I
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A.set(i, j, get(i, j));
                if (i == j) {
                    B.set(i, j, getComponentVectorSpace().one());
                } else {
                    B.set(i, j, getComponentVectorSpace().zero());
                }
            }
        }

        //Paramètres permettant l'arrêt prématuré des boucles ci-dessous si calcul impossible
        boolean bk = true;
        boolean bl = true;

        //Paramètres de contrôle pour la recherche de pivot
        int cnt_row = 0;
        int cnt_col = 0;

        //paramètre de stockage de coefficients
        T a, tmp;

        for (int k = 0; k < n && bk; k++) {
            if (!I.contains(k)) {
                I.add(k);
                cnt_row++;
                bl = true;
                for (int l = 0; l < n && bl; l++) {
                    if (!J.contains(l)) {
                        a = A.get(k, l);
                        if (!getComponentVectorSpace().isZero(a)) {
                            J.add(l);
                            cnt_col++;
                            bl = false; //permet de sortir de la boucle car le pivot a été trouvé
                            for (int p = 0; p < n; p++) {
                                if (p != k) {
                                    tmp = A.get(p, l);
                                    for (int q = 0; q < n; q++) {
                                        A.set(p, q, getComponentVectorSpace().sub(A.get(p, q), getComponentVectorSpace().mul(A.get(k, q), getComponentVectorSpace().div(tmp, a))));
                                        B.set(p, q, getComponentVectorSpace().sub(B.get(p, q), getComponentVectorSpace().mul(B.get(k, q), (getComponentVectorSpace().div(tmp, a)))));
                                    }
                                }
                            }
                        }
                    }
                }
                if (cnt_row != cnt_col) {
                    //TMatrix<T> is singular";
                    //Pas de pivot possible, donc pas d'inverse possible! On sort de la boucle
                    bk = false;
                    k = n;
                }
            }
        }

        if (!bk) {
            throw new ArithmeticException("Matrix is singular");
            //Le pivot n'a pas pu être trouve précédemment, ce qui a donne bk = false
        } else {
            //Réorganisation des colonnes de sorte que A=I et B=Inv(M). Méthode de Gauss-Jordan
            for (int l = 0; l < n; l++) {
                for (int k = 0; k < n; k++) {
                    a = A.get(k, l);
                    if (!getComponentVectorSpace().isZero(a)) {
                        A.set(k, l, getComponentVectorSpace().one());
                        for (int p = 0; p < n; p++) {
                            B.set(k, p, getComponentVectorSpace().div(B.get(k, p), a));
                        }
                        if (k != l) {
                            exchange_row(A, k + 1, l + 1, n, n);
                            exchange_row(B, k + 1, l + 1, n, n);
                        }
                        k = n; //Pour sortir de la boucle car le coefficient non nul a ete trouve
                    }
                }
            }
            A.dispose();
            return B;
        }
    }

    /**
     * Formula used to Calculate Inverse: inv(A) = 1/det(A) * adj(A)
     *
     * @return inverse
     */
    public TMatrix<T> invAdjoint() {
        // Formula used to Calculate Inverse:
        // inv(A) = 1/det(A) * adj(A)
        int tms = getRowCount();

        T[][] m = newT(tms, tms);

        T det = det();

        if (det.equals(Complex.ZERO)) {
            throw new ArithmeticException("Determinant Equals 0, Not Invertible.");
        }

//            System.out.println("determinant is " + det);
        T dd = getComponentVectorSpace().inv(det);
        if (tms == 1) {
            T[][] t0 = newT(1, 1);
            t0[0][0] = dd;
            return getFactory().newMatrix(t0);
        }
        TMatrix<T> mm = adjoint();
        for (int i = 0; i < tms; i++) {
            for (int j = 0; j < tms; j++) {
                m[i][j] = getComponentVectorSpace().mul(dd, mm.get(i, j));
            }
        }

        return mm;
    }

    public TMatrix<T> coMatrix(int row, int col) {
        int tms = getRowCount();
        TMatrix<T> ap = createMatrix(tms - 1, tms - 1);
//                for (ii = 0; ii < tms; ii++) {
//                    for (jj = 0; jj < tms; jj++) {
//                        if ((ii != i) && (jj != j)) {
//                            ap[ia][ja] = elements[ii][jj];
//                            ja++;
//                        }
//                    }
//                    if ((ii != i) && (jj != j)) {
//                        ia++;
//                    }
//                    ja = 0;
//                }
        int ia = 0;
        int ja;
        for (int ii = 0; ii < row; ii++) {
            ja = 0;
//            T[] apia = ap[ia];
            for (int jj = 0; jj < col; jj++) {
                ap.set(ia, ja, get(ii, jj));
                ja++;
            }
            for (int jj = col + 1; jj < tms; jj++) {
                ap.set(ia, ja, get(ii, jj));
                ja++;
            }
            ia++;
        }
        for (int ii = row + 1; ii < tms; ii++) {
            ja = 0;
            for (int jj = 0; jj < col; jj++) {
                ap.set(ia, ja, get(ii, jj));
                ja++;
            }
            for (int jj = col + 1; jj < tms; jj++) {
                ap.set(ia, ja, get(ii, jj));
                ja++;
            }
            ia++;
        }
        return ap;
    }

    private boolean isSquare() {
        return getColumnCount() == getRowCount();
    }

    private void checkSquare() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Expected Square Matrix");
        }
    }

    public TMatrix<T> pow(double rexp) {
        checkSquare();
        int exp = (int) rexp;
        if (exp != rexp) {
            throw new IllegalArgumentException("Unable to raise Matrix to Non integer power");
        }

        switch (exp) {
            case 0: {
                return getFactory().newIdentity(getColumnCount());
            }
            case 1: {
                return this;
            }
            case -1: {
                return inv();
            }
            default: {
                if (exp > 0) {
                    TMatrix<T> m = this;
                    while (exp > 1) {
                        m = m.mul(this);
                        exp--;
                    }
                    return m;
                } else {
                    TMatrix<T> m = this;
                    int t = -exp;
                    while (t > 1) {
                        m = m.mul(this);
                        t--;
                    }
                    ;
                    m = m.inv();
                    return m;
                }
            }
        }
    }

    public TMatrix<T> adjoint() {
        int tms = getRowCount();

        TMatrix<T> m = createMatrix(tms, tms);

        for (int i = 0; i < tms; i++) {
            for (int j = 0; j < tms; j++) {
                T det = coMatrix(i, j).det();
                // (-1) power (i+j)
                if (((i + j) % 2) != 0) {
                    det = getComponentVectorSpace().mul(det, minusOne());
                }
                // transpose needed;
                m.set(j, i, det);
            }
        }
        return m;
    }

    public T det() {
        int tms = getRowCount();

        T det = getComponentVectorSpace().one();
        TMatrix<T> matrix = upperTriangle();

        for (int i = 0; i < tms; i++) {
            det = getComponentVectorSpace().mul(det, matrix.get(i, i));
        }      // multiply down diagonal
//        int iDF = 1;
//        det = det.multiply(iDF);                    // adjust w/ determinant factor

        return det;
    }

    public TMatrix<T> upperTriangle() {
//        System.out.println(new java.util.Date() + " upperTriangle IN (" + elements.length + ")");
        VectorSpace<T> cs = getComponentVectorSpace();
        TMatrix<T> o = createMatrix(getRowCount(), getColumnCount());
        o.set(this);

        T f1;
        T temp;
        int tms = getRowCount();  // get This Matrix Size (could be smaller than global)
        int v;

        int iDF = 1;

        for (int col = 0; col < tms - 1; col++) {
            for (int row = col + 1; row < tms; row++) {
                v = 1;

                outahere:
                while (o.get(col, col).equals(Complex.ZERO)) // check if 0 in diagonal
                {                                   // if so switch until not
                    if (col + v >= tms) // check if switched all rows
                    {
                        iDF = 0;
                        break outahere;
                    } else {
                        for (int c = 0; c < tms; c++) {
                            temp = o.get(col, c);
                            o.set(col, c, o.get(col + v, c));       // switch rows
                            o.set(col + v, c, temp);
                        }
                        v++;                            // count row switchs
                        iDF = iDF * -1;                 // each switch changes determinant factor
                    }
                }

                if (!o.get(col, col).equals(Complex.ZERO)) {

                    f1 = cs.mul(cs.div(o.get(row, col), o.get(col, col)), minusOne());
                    for (int i = col; i < tms; i++) {
                        o.set(row, i, cs.add(cs.mul(f1, o.get(col, i)), o.get(row, i)));
                    }

                }

            }
        }

//        System.out.println(new java.util.Date() + " upperTriangle OUT");
        return o;
    }

    private T minusOne() {
        return getComponentVectorSpace().neg(getComponentVectorSpace().one());
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public String format() {
        return format(null, null);
    }

    public String format(String commentsChar, String varName) {
        StringBuilder sb = new StringBuilder();
        if (commentsChar != null) {
            sb.append(commentsChar).append(" dimension [").append(getRowCount()).append(",").append(getColumnCount()).append("]").append(System.getProperty("line.separator"));
        }
        if (varName != null) {
            sb.append(varName).append(" = ");
        }

        int columns = getColumnCount();
        int rows = getRowCount();
        int[] colsWidth = new int[columns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int len = String.valueOf(get(r, c)).length();
                if (len > colsWidth[c]) {
                    colsWidth[c] = len;
                }
            }
        }

        String lineSep = System.getProperty("line.separator");
        sb.append("[");
        for (int i = 0; i < rows; i++) {
            if (i > 0 || rows > 1) {
                sb.append(lineSep);
            }
            for (int j = 0; j < columns; j++) {
                StringBuilder sbl = new StringBuilder(colsWidth[j]);
                //sbl.clear();
                if (j > 0) {
                    sbl.append(' ');
                }
                String disp = String.valueOf(get(i, j));
                sbl.append(disp);
                int x = colsWidth[j] - disp.length();
                while (x > 0) {
                    sbl.append(' ');
                    x--;
                }
                //sbl.append(' ');
                sb.append(sbl.toString());
            }
        }
        if (rows > 1) {
            sb.append(lineSep);
        }
        sb.append("]");
        return sb.toString();
    }

    public T[][] getArrayCopy() {
        T[][] arr = newT(getRowCount(), getColumnCount());
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[0].length; c++) {
                arr[r][c] = get(r, c);
            }
        }
        return arr;
    }

    public TMatrix<T> solve(TMatrix<T> B) {
        return solve(B, Maths.Config.getDefaultMatrixSolveStrategy());
    }

    public TMatrix<T> solve(TMatrix<T> B, SolveStrategy solveStrategy) {
        switch (solveStrategy) {
            case DEFAULT: {
                if (getRowCount() == getColumnCount()) {
                    return new TCLUDecomposition<T>(this).solve(B);
                }
                throw new IllegalArgumentException("Not a square matrix");
            }
//            case OJALGO: {
//                throw new IllegalArgumentException("Unsupported");
//            }
        }
        throw new IllegalArgumentException("Invalid SolveStrategy");
    }

    //    /*****************************************
//     * Solves one of the matrix equations.
//     *    A * X = B
//     *
//     * @param A
//     *   The A matrix.
//     * @param B
//     *   The B matrix.
//     *   Thrown if a paramater is invalid.
//     ****************************************/
//    public static CMatrix
//            solve_AX_B(boolean upper, boolean nounit,
//                       CMatrix A, CMatrix B) {
//        ZVector2 tmpVec1 = new ZVector2();
//        ZVector2 tmpVec2 = new ZVector2();
////        CMatrix tmpVec3 = null;
//        T elt1 = T.ZERO;
//        T elt2 = T.ZERO;
//
//        int m, n, i, j, k;
//
//        m = B.getRowCount();
//        n = B.getColumnCount();
//
//        B = new CMatrix(B); // copy of it
//
//        // Left Side
//        // B = alpha * inv(A) * B
//        if (upper) {
//            for (j = 0; j < n; j++) {
//                for (k = m - 1; k >= 0; k--) {
//                    elt2 = B.get(m, j);
//                    if (!elt2.equals(T.ZERO)) {
//                        if (nounit) {
//                            B.set(elt2.divide(A.get(k, k)), k, j);
//                        }
//                        elt2 = elt2.multiply(-1);
//                        for(int x=0;x<k;x++){
//                            B.set(B.get(x,j).add(B.get(x,k).multiply(elt2)),x,j);
//                        }
//                    }
//                }
//            }
//
//            // !upper
//        } else {
//            for (j = 0; j < n; j++) {
//                for (k = 0; k < m; k++) {
//                    elt2=B.get(k,j);
//                    if (!elt2.equals(T.ZERO)) {
//                        if (nounit) {
//                            elt1 = A.get(k, k);
//                            elt2=elt2.divide(elt1);
//                            B.set(elt2, k, j);
//                        }
//                        elt2 =elt2.multiply(-1);
//                        for(int x=0;x<(m - k - 1);x++){
//                            B.set(B.get(x + (k+1),j).add(B.get(x + (k+1),k).multiply(elt2)),x,j);
//                        }
//                    }
//                }
//            }
//        }
//        return B;
    //    }
    public void store(String file) {
        store(new File(Maths.Config.expandPath(file)));
    }

    public void store(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            try {
                store(new PrintStream(fileOutputStream = new FileOutputStream(file)));
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public void store(PrintStream stream) {
        store(stream, null, null);
    }


    public void store(String file, String commentsChar, String varName) {
        PrintStream out = null;
        try {
            try {
                out = new PrintStream(file);
                store(out, commentsChar, varName);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public void store(File file, String commentsChar, String varName) {
        PrintStream out = null;
        try {
            try {
                out = new PrintStream(file);
                store(out, commentsChar, varName);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }


    public void store(PrintStream stream, String commentsChar, String varName) {
        int columns = getColumnCount();
        int rows = getRowCount();
        int[] colsWidth = new int[columns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int len = String.valueOf(get(r, c)).length();
                if (len > colsWidth[c]) {
                    colsWidth[c] = len;
                }
            }
        }
        if (commentsChar != null) {
            stream.print(commentsChar);
            stream.print(" dimension [" + getRowCount() + "," + getColumnCount() + "]");
            stream.println();
        }
        if (varName != null) {
            stream.print(varName);
            stream.print(" = ");
        }
        stream.print("[");
        stream.println();

        for (int i = 0; i < rows; i++) {
            if (i > 0) {
                stream.println();
            }
            for (int j = 0; j < columns; j++) {
                StringBuilder sbl = new StringBuilder(colsWidth[j]);
                //sbl.clear();
                if (j > 0) {
                    sbl.append(' ');
                }
                String disp = String.valueOf(get(i, j));
                sbl.append(disp);
                sbl.append(' ');
                int x = colsWidth[j] - disp.length();
                while (x > 0) {
                    sbl.append(' ');
                    x--;
                }
                stream.print(sbl.toString());
            }
        }
        stream.println();
        stream.print("]");

    }

    public void read(String reader) {
        BufferedReader r = null;
        try {
            read(r = new BufferedReader(new StringReader(reader)));
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace(); // should'nt be thrown
                }
            }
        }
    }

    public void read(File file) {
        BufferedReader r = null;
        try {
            try {
                read(r = new BufferedReader(new FileReader(file)));
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public void read(BufferedReader reader) {
        int rows = getRowCount();
        int columns = getColumnCount();
        ArrayList<ArrayList<T>> l = new ArrayList<ArrayList<T>>(rows > 0 ? rows : 10);
        String line;
        int cols = 0;
        final int START = 0;
        final int LINE = 1;
        final int END = 2;
        int pos = START;
        try {
            try {
                while (pos != END) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    if (pos == START) {
                        boolean isFirstLine = (line.startsWith("["));
                        if (!isFirstLine) {
                            throw new IOException("Expected a '[' but found '" + line + "'");
                        }
                        line = line.substring(1, line.length());
                        pos = LINE;
                    }
                    if (line.endsWith("]")) {
                        line = line.substring(0, line.length() - 1);
                        pos = END;
                    }
                    StringTokenizer stLines = new StringTokenizer(line, ";");
                    while (stLines.hasMoreTokens()) {
                        StringTokenizer stRow = new StringTokenizer(stLines.nextToken(), " \t");
                        ArrayList<T> c = new ArrayList<T>();
                        int someCols = 0;
                        while (stRow.hasMoreTokens()) {
                            c.add(getComponentVectorSpace().parse(stRow.nextToken()));
                            someCols++;
                        }
                        cols = Math.max(cols, someCols);
                        if (c.size() > 0) {
                            l.add(c);
                        }
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }

        if (rows == l.size() && columns == cols) {
            //
        } else {
            resize(l.size(), cols);
        }
        for (int i = 0; i < l.size(); i++) {
            for (int j = 0; j < cols; j++) {
                set(i, j, l.get(i).get(j));
            }
        }
    }

    @Override
    public final TVector<TVector<T>> rows() {
        return getRows();
    }

    @Override
    public final TVector<TVector<T>> columns() {
        return getColumns();
    }

    @Override
    public TVector<T> row(int row) {
        return getRow(row);
    }

    @Override
    public TVector<T> column(int column) {
        return getColumn(column);
    }

    public TVector<T> getRow(final int row) {
        return new TMatrixRow<T>(row, this);
    }

    public TVector<T> toVector() {
        if (isColumn()) {
            return getColumn(0);
        }
        if (isRow()) {
            return getRow(0);
        }
        throw new RuntimeException("Not a vector");
    }

    public T scalarProduct(boolean hermitian, TMatrix<T> m) {
        return toVector().scalarProduct(hermitian, m.toVector());
    }

    @Override
    public T scalarProduct(TMatrix<T> m) {
        return scalarProduct(false, m);
    }

    @Override
    public T scalarProduct(TVector<T> v) {
        return scalarProduct(false, v);
    }

    @Override
    public T hscalarProduct(TMatrix<T> m) {
        return scalarProduct(true, m);
    }

    @Override
    public T hscalarProduct(TVector<T> v) {
        return scalarProduct(true, v);
    }

    public T scalarProduct(boolean hermitian, TVector<T> v) {
        return toVector().scalarProduct(hermitian, v);
    }

    public boolean isColumn() {
        return getColumnCount() == 1;
    }

    public boolean isRow() {
        return getRowCount() == 1;
    }

    @Override
    public void update(int row, int col, T val) {
        set(row, col, val);
    }

    @Override
    public void update(int row, int col, TMatrix<T> subMatrix) {
        set(row, col, subMatrix);
    }

    public TVector<T> getColumn(final int column) {
        return new TMatrixColumn<T>(column, this);
    }

    public double[][] absdbls() {
        double[][] d = new double[getRowCount()][getColumnCount()];
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                d[i][j] = getComponentVectorSpace().absdbl(get(i, j));
            }
        }
        return d;
    }

    public TMatrix<T> abs() {
        int rows = getRowCount();
        int columns = getColumnCount();
        TMatrix<T> X = createMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                X.set(i, j, (getComponentVectorSpace().abs(get(i, j))));
            }
        }
        return X;
    }

    public double cond() {
        return this.norm1() * this.inv().norm1();
    }

    public double cond2() {
        return this.norm2() * this.inv().norm2();
    }

    public double condHadamard() {
        T det = det();
        double x = 1;
        double alpha;
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            alpha = 0;
            for (int c = 0; c < cols; c++) {
                alpha = alpha + Maths.sqr(getComponentVectorSpace().absdbl(get(r, c)));
            }
            x *= Math.sqrt(alpha);
        }
        return getComponentVectorSpace().absdbl(det) / x;
    }

    public TMatrix<T> sparsify(double ceil) {
        TMatrix<T> array = createMatrix(getRowCount(), getColumnCount());
        double max = Double.NaN;
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double d = getComponentVectorSpace().absdbl(get(r, c));
                if (!Double.isNaN(d) && (Double.isNaN(max) || d > max)) {
                    max = d;
                }
            }
        }
        if (!Double.isNaN(max)) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    T v = get(r, c);
                    double d = getComponentVectorSpace().absdbl(v);
                    if (!Double.isNaN(d)) {
                        d = d / max * 100;
                        if (d <= ceil) {
                            v = getComponentVectorSpace().zero();
                        }
                    }
                    array.set(r, c, v);
                }
            }
        }
        return array;
    }

//    public Complex complexValue() {
//        return (getRowCount() == 1 && getColumnCount() == 1) ? get(0, 0) : componentVectorSpace.nan();
//    }

    @Override
    public double doubleValue() {
        return complexValue().doubleValue();
    }

    @Override
    public float floatValue() {
        return complexValue().floatValue();
    }

    @Override
    public int intValue() {
        return complexValue().intValue();
    }

    @Override
    public long longValue() {
        return complexValue().longValue();
    }

    public double maxAbs() {
        double f = 0;
        double f0 = 0;
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                f0 = getComponentVectorSpace().absdbl(get(r, c));
                f = Math.max(f, f0);
            }
        }
        return f;
    }

    public double minAbs() {
        double f = 0;
        double f0 = 0;
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                f0 = getComponentVectorSpace().absdbl(get(r, c));
                f = Math.min(f, f0);
            }
        }
        return f;
    }

    public TMatrix<T> pow(TMatrix<T> power) {
        if (power.isScalar()) {
            return pow(power.get(0, 0));
        } else {
            throw new IllegalArgumentException("Unable to raise Matrix to Matrix Power");
        }

    }

    public TMatrix<T> pow(Complex power) {
        if (power.isReal()) {
            return pow(power.toReal());
        }
        throw new IllegalArgumentException("Unable to raise Matrix to Complex Power");
    }

    public TMatrix<T> pow(T power) {
        if (isScalar()) {
            return getFactory().newConstant(1, getComponentVectorSpace().pow(get(0, 0), power));
        } else {
            if (getComponentVectorSpace().isComplex(power)) {
                Complex r = getComponentVectorSpace().toComplex(power);
                return pow(r);
            } else {
                throw new IllegalArgumentException("Unable to raise Matrix to Complex power");
            }
        }
    }

    public boolean isScalar() {
        return getRowCount() == 1 && getColumnCount() == 1;
    }

    public boolean isDouble() {
        return isComplex() && toComplex().isDouble();
    }

    public double toDouble() {
        return toComplex().toDouble();
    }

    public boolean isZero() {
        int columns = getColumnCount();
        int rows = getRowCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!getComponentVectorSpace().isZero(get(r, c))) {
                    return false;
                }
            }
        }

        return true;
    }

    public void set(TMatrix<T>[][] subMatrixes) {
        int rows = 0;
        int cols = 0;
        for (TMatrix<T>[] subMatrixe : subMatrixes) {
            int r = 0;
            int c = 0;
            for (TMatrix<T> aSubMatrixe : subMatrixe) {
                c += aSubMatrixe.getColumnCount();
                if (r == 0) {
                    r = aSubMatrixe.getRowCount();
                } else if (r != aSubMatrixe.getRowCount()) {
                    throw new IllegalArgumentException("Column count does not match");
                }
            }
            if (cols == 0) {
                cols = c;
            } else if (cols != c) {
                throw new IllegalArgumentException("Column count does not match");
            }
            rows += r;
        }
        if (rows == 0 || cols == 0) {
            throw new EmptyMatrixException();
        }
        if (rows != getColumnCount() || rows != getRowCount()) {
            throw new IllegalArgumentException("Columns or Rows count does not match");
        }
        int row = 0;
        int col;
        for (TMatrix<T>[] subMatrixe1 : subMatrixes) {
            col = 0;
            for (TMatrix<T> aSubMatrixe1 : subMatrixe1) {
                set(row, col, aSubMatrixe1);
                col += aSubMatrixe1.getColumnCount();
            }
            row += subMatrixe1[0].getRowCount();
        }
    }

    public void set(TMatrix<T> other) {
        int cols = other.getColumnCount();
        int rows = other.getRowCount();
        if (rows != getColumnCount() || rows != getRowCount()) {
            throw new IllegalArgumentException("Columns or Rows count does not match");
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                set(r, c, other.get(r, c));
            }
        }
    }

    public void dispose() {

    }

    protected TMatrixFactory<T> createDefaultFactory() {
        return Maths.Config.getDefaultMatrixFactory(getComponentType());
    }

    @Override
    public TMatrixFactory<T> getFactory() {
        if (factory != null) {
            return factory;
        }
        if (factoryId != null) {
            return factory=Maths.Config.getTMatrixFactory(factoryId);
        }

        TMatrixFactory<T> t = createDefaultFactory();
        if (t == null) {
            throw new IllegalArgumentException("Invalid Factory");
        }
        return t;
    }

    @Override
    public void setFactory(TMatrixFactory<T> factory) {
        this.factory = factory;
        this.factoryId = factory == null ? null : factory.getId();
    }

    @Override
    public String getFactoryId() {
        return factoryId;
    }

    @Override
    public T apply(int row, int col) {
        return get(row, col);
    }

    @Override
    public T apply(int vectorIndex) {
        return get(vectorIndex);
    }

    public Complex complexValue() {
        return (getRowCount() == 1 && getColumnCount() == 1) ? getComponentVectorSpace().toComplex(get(0, 0)) : Complex.NaN;
    }

    @Override
    public boolean isComplex() {
        return getRowCount() == 1 && getColumnCount() == 1 && get(0, 0) instanceof Complex;
    }

    @Override
    public VectorSpace<T> getComponentVectorSpace() {
        return Maths.getVectorSpace(getComponentType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof TMatrix)) {
            return false;
        }

        TMatrix<?> that = (TMatrix<?>) o;
        int columnCount = getColumnCount();
        int rowCount = getRowCount();
        if (that.getColumnCount() != columnCount) {
            return false;
        }
        if (that.getRowCount() != rowCount) {
            return false;
        }
        if (!that.getComponentType().equals(getComponentType())) {
            return false;
        }
        for (int c = 0; c < columnCount; c++) {
            for (int r = 0; r < rowCount; r++) {
                if (!Objects.equals(get(r, c), that.get(r, c))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        int columnCount = getColumnCount();
        int rowCount = getRowCount();
        hash = 89 * hash + columnCount;
        hash = 89 * hash + rowCount;
        for (int c = 0; c < columnCount; c++) {
            for (int r = 0; r < rowCount; r++) {
                T t = get(r, c);
                if (t != null) {
                    hash = 89 * hash + t.hashCode();
                }
            }
        }
        return hash;

    }

    @Override
    public TMatrix<T> copy() {
        return getFactory().newMatrix(this);
    }
}
