package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.util.NonStateField;

import java.io.Serializable;

/**
 * Created by vpc on 4/30/14.
 */
public abstract class GenericFunctionXY extends AbstractComposedFunction {
    private final FunctionType functionType;
    @NonStateField
    protected Domain _cache_domain;
    @NonStateField
    private Expressions.BinaryExprHelper<GenericFunctionXY> exprHelper = new GenericFunctionXYBinaryExprHelper();
    private Expr xargument;
    private Expr yargument;
    protected GenericFunctionXY(String functionName,Expr xargument, Expr yargument) {
        this(functionName,xargument, yargument, null);
    }

    protected GenericFunctionXY(String functionName,Expr xargument, Expr yargument, FunctionType lowerFunctionType) {
        super();
        this.xargument=xargument;
        this.yargument=yargument;
        if (!xargument.getComponentDimension().equals(yargument.getComponentDimension())) {
            throw new IllegalArgumentException("Dimension Mismatch " + xargument.getComponentDimension() + " vs " + yargument.getComponentDimension());
        }
        FunctionType functionType0 = null;
        if (xargument.isDD()) {
            functionType0 = FunctionType.DOUBLE;
        } else if (xargument.isDC()) {
            functionType0 = FunctionType.COMPLEX;
        } else if (xargument.isDM()) {
            functionType0 = FunctionType.MATRIX;
        } else {
            throw new IllegalArgumentException("Unknown functionType");
        }
        if (yargument.isDD()) {
            functionType0 = FunctionType.DOUBLE;
        } else if (yargument.isDC()) {
            functionType0 = FunctionType.COMPLEX;
        } else if (yargument.isDM()) {
            functionType0 = FunctionType.MATRIX;
        } else {
            throw new IllegalArgumentException("Unknown functionType");
        }
        if (lowerFunctionType == null) {
            this.functionType = functionType0;
        } else {
            if (functionType0.ordinal() > lowerFunctionType.ordinal()) {
                this.functionType = functionType0;
            } else {
                this.functionType = lowerFunctionType;
            }
        }
    }

    public Expr getXArgument() {
        return xargument;
    }

    public Expr getYArgument() {
        return yargument;
    }

    @Override
    public Expr[] getArguments() {
        return new Expr[]{xargument,yargument};
    }

    @Override
    public final Domain getDomain() {
        if (!Maths.Config.isCacheExpressionPropertiesEnabled()) {
            return getDomainImpl();
        }
        if (_cache_domain == null) {
            _cache_domain = getDomainImpl();
        }
        return _cache_domain;
    }

    @Override
    public Domain getDomainImpl() {
        return getXArgument().getDomain().intersect(getYArgument().getDomain());
//        switch (this.functionType) {
//            case DOUBLE: {
//                if (evalDouble(0) == 0) {
//                    return getXArgument().getDomain().intersect(getYArgument().getDomain());
//                }
//            }
//            case COMPLEX: {
//                if (evalComplex(Complex.ZERO).isZero()) {
//                    return getXArgument().getDomain().intersect(getYArgument().getDomain());
//                }
//            }
//            case MATRIX: {
//                if (evalMM(Complex.ZERO.toMatrix()).isZero()) {
//                    return getXArgument().getDomain().intersect(getYArgument().getDomain());
//                }
//            }
//        }
//        return Domain.FULL(getArgument().getDomainDimension());

    }

    @Override
    public ComponentDimension getComponentDimension() {
        return getXArgument().getComponentDimension();
    }

    @Override
    public int getDomainDimension() {
        return Math.max(getXArgument().getDomainDimension(), getYArgument().getDomainDimension());
    }

    @Override
    public Complex[][] computeComplex(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeComplex(this, exprHelper, x, y, d0, ranges);
    }

    @Override
    public Complex[] computeComplex(double[] x, Domain d0, Out<Range> ranges) {
        return Expressions.computeComplex(this, exprHelper, x, d0, ranges);
    }

    @Override
    public Complex[][][] computeComplex(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeComplex(this, exprHelper, x, y, z, d0, ranges);
    }

    @Override
    public DoubleToDouble getReal() {
        return new Real(this);
    }

    @Override
    public DoubleToDouble getImag() {
        return new Imag(this);
    }

    @Override
    public double[][] computeDouble(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, exprHelper, x, y, d0, ranges);
    }

    @Override
    public double[][][] computeDouble(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, exprHelper, x, y, z, d0, ranges);
    }

    @Override
    public Complex computeComplex(double x, double y) {
        return computeComplex(new double[]{x}, new double[]{y})[0][0];
    }

    @Override
    public double computeDouble(double x) {
        return computeDouble(new double[]{x})[0];
    }

    @Override
    public double computeDouble(double x, double y) {
        return computeDouble(new double[]{x}, new double[]{y}, null, null)[0][0];
    }

    @Override
    public double computeDouble(double x, double y, double z) {
        return computeDouble(new double[]{x}, new double[]{y}, new double[]{z}, null, null)[0][0][0];
    }

    public Complex computeComplex(double x, double y, double z) {
        if (getDomain().contains(x, y, z)) {
            Complex xx = getXArgument().toDC().computeComplex(x, y, z);
            Complex yy = getYArgument().toDC().computeComplex(x, y, z);
            return evalComplex(xx, yy);
        }
        return Complex.ZERO;
    }

    @Override
    public Matrix computeMatrix(double x, double y) {
        if (getDomain().contains(x, y)) {
            Matrix xx = getXArgument().toDM().computeMatrix(x, y);
            Matrix yy = getYArgument().toDM().computeMatrix(x, y);
            return evalMatrix(xx,yy);
        }
        return Complex.ZERO.toMatrix();
    }

    @Override
    public Matrix computeMatrix(double x, double y, double z) {
        if (getDomain().contains(x, y, z)) {
            Matrix xx = getXArgument().toDM().computeMatrix(x, y, z);
            Matrix yy = getYArgument().toDM().computeMatrix(x, y, z);
            return evalMatrix(xx,yy);
        }
        return Complex.ZERO.toMatrix();
    }

    @Override
    public double[] computeDouble(double[] x, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, exprHelper, x, d0, ranges);
    }

    @Override
    public Matrix[][] computeMatrix(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, exprHelper, x, y, d0, ranges);
    }

    @Override
    public Matrix[] computeMatrix(double[] x, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, exprHelper, x, d0, ranges);
    }

    @Override
    public Matrix[][][] computeMatrix(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, exprHelper, x, y, z, d0, ranges);
    }

//    protected double evalCD(Complex x) {
//        return evalComplex(x).toDouble();
//    }
//
//    protected Complex evalDC(double x) {
//        return evalComplex(Complex.valueOf(x));
//    }
//
//    protected Complex evalMC(Matrix x) {
//        return evalComplex(x.toComplex());
//    }
//
//    protected Double evalMD(Matrix x) {
//        return evalComplex(x.toComplex()).toDouble();
//    }
//
//    protected Matrix evalDM(double x) {
//        return evalComplex(Complex.valueOf(x)).toMatrix();
//    }
//
//    protected Matrix evalCM(Complex x) {
//        return evalComplex(x).toMatrix();
//    }
//
//    public Matrix computeMatrix(Matrix c) {
//        return evalMM(c);
//    }
//
    protected Matrix evalMatrix(Matrix x,Matrix y) {
        int columnCount = x.getColumnCount();
        int rowCount = x.getRowCount();
        if(
                columnCount !=y.getColumnCount()
                || rowCount !=y.getRowCount()
                ){
            throw new IllegalArgumentException("Dimension mismatch");
        }
        return Maths.matrix(rowCount, columnCount, new MatrixCell() {
            @Override
            public Complex get(int row, int column) {
                return evalComplex(x.get(row,column),y.get(row,column));
            }
        });
    }
//
//    public Complex computeComplex(Complex c) {
//        return evalComplex(c);
//    }

    /**
     * evaluate expression and return the most "reduced" type
     *
     * @param x
     * @param y
     * @return
     */
    public abstract Complex evalComplex(Complex x, Complex y);

    public abstract Complex evalComplex(double x, double y);

    public abstract double evalDouble(double x, double y);

    public abstract Expr newInstance(Expr xargument, Expr yargument);

    @Override
    public String toString() {
        return getFunctionName() + "(" + getXArgument() + "," + getYArgument() + ")";
    }


    public Expr newInstance(Expr... arguments) {
        if (arguments.length != 2) {
            throw new IllegalArgumentException();
        }
        return newInstance(arguments[0], arguments[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GenericFunctionXY that = (GenericFunctionXY) o;

        if (functionType != that.functionType) return false;
        if (xargument != null ? !xargument.equals(that.xargument) : that.xargument != null) return false;
        return yargument != null ? yargument.equals(that.yargument) : that.yargument == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (functionType != null ? functionType.hashCode() : 0);
        result = 31 * result + (xargument != null ? xargument.hashCode() : 0);
        result = 31 * result + (yargument != null ? yargument.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isDDImpl() {
        return functionType.ordinal() <= FunctionType.DOUBLE.ordinal();
    }

    @Override
    public boolean isDCImpl() {
        return functionType.ordinal() <= FunctionType.COMPLEX.ordinal();
    }

    @Override
    public boolean isDMImpl() {
        return functionType.ordinal() <= FunctionType.MATRIX.ordinal();
    }

    @Override
    public Complex toComplex() {
        if (!isComplex()) {
            Expr ss = simplify();
            boolean cc = ss.isComplex();
            if (cc) {
                return ss.toComplex();
            }
            throw new ClassCastException(toString() + " of type " + getClass().getName() + " cannot be casted to Complex");
        }
        Expr e = simplify();
        if (e instanceof Complex) {
            return (Complex) e;
        }
        e = simplify();
        throw new ClassCastException(toString() + " of type " + getClass().getName() + " cannot be casted to Complex");
        //return evalComplex(Complex.NaN);
    }

    @Override
    public double toDouble() {
        if (!isDouble()) {
            throw new ClassCastException(toString() + " of type " + getClass().getName() + " cannot be casted to Complex");
        }
        Expr e = simplify();
        if (e instanceof Complex) {
            Complex cc = (Complex) e;
            if (cc.isReal()) {
                return cc.getReal();
            }
        }
        throw new ClassCastException(toString() + " of type " + getClass().getName() + " cannot be casted to Double");
//        return evalDouble(Double.NaN);
    }

    @Override
    public Matrix toMatrix() {
        if (!isMatrix()) {
            throw new ClassCastException(toString() + " of type " + getClass().getName() + " cannot be casted to Complex");
        }
        Expr e = simplify();
        if (e instanceof Complex) {
            return ((Complex) e).toMatrix();
        }
        throw new ClassCastException(toString() + " of type " + getClass().getName() + " cannot be casted to MAtrix");
//        return evalDM(Double.NaN);
    }

    @Override
    public int getComponentSize() {
        return getXArgument().toDV().getComponentSize();
    }

    @Override
    public Expr setParam(String name, Expr value) {
        Expr xa = getXArgument();
        Expr xb = xa.setParam(name, value);
        Expr ya = getYArgument();
        Expr yb = ya.setParam(name, value);
        if (xa != xb || ya != yb) {
            Expr e = newInstance(xb,yb);
            e= Any.copyProperties(this, e);
            return Any.updateTitleVars(e,name,value);
        }
        return this;
    }

    @Override
    public Expr getComponent(int row, int col) {
        if (isScalarExpr() && (row != col || col != 0)) {
            return FunctionFactory.DZEROXY;
        }
        return newInstance(getXArgument().toDM().getComponent(row, col),getYArgument().toDM().getComponent(row, col));
    }

    private class GenericFunctionXYBinaryExprHelper implements Expressions.BinaryExprHelper<GenericFunctionXY>, Serializable {

        @Override
        public int getBaseExprCount(GenericFunctionXY expr) {
            return 2;
        }

        @Override
        public Expr getBaseExpr(GenericFunctionXY expr, int index) {
            return getArguments()[index];
        }

        @Override
        public double computeDouble(double a, double b, Expressions.ComputeDefOptions options) {
            boolean def = options.value1Defined && options.value2Defined;
            if (def) {
                double d = evalDouble(a, b);
                options.resultDefined = true;
                return d;
            } else {
                options.resultDefined = false;
                return 0;
            }
        }

        @Override
        public Complex computeComplex(Complex a, Complex b, Expressions.ComputeDefOptions options) {
            boolean def = options.value1Defined && options.value2Defined;
            if (def) {
                Complex d = evalComplex(a, b);
                options.resultDefined = true;
                return d;
            } else {
                options.resultDefined = false;
                return Complex.ZERO;
            }
        }

        @Override
        public Matrix computeMatrix(Matrix a, Matrix b, Matrix zero, Expressions.ComputeDefOptions options) {
            boolean def = options.value1Defined && options.value2Defined;
            if (def) {
                Matrix d = evalMatrix(a, b);
                options.resultDefined = true;
                return d;
            } else {
                options.resultDefined = false;
                return zero;
            }
        }
    }

}
