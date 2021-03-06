/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.transform.ExpressionTransform;
import net.vpc.scholar.hadrumaths.transform.ExpressionTransformer;
import net.vpc.scholar.hadrumaths.util.NonStateField;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class Mul extends AbstractExprOperator implements Cloneable {

    private static Expressions.BinaryExprHelper<Mul> binaryExprHelper = new Expressions.BinaryExprHelper<Mul>() {
        @Override
        public int getBaseExprCount(Mul expr) {
            return expr.expressions.length;
        }

        @Override
        public Expr getBaseExpr(Mul expr, int index) {
            return expr.expressions[index];
        }

        @Override
        public double computeDouble(double a, double b, Expressions.ComputeDefOptions options) {
            boolean def = options.value1Defined && options.value2Defined;
            if (def) {
                double d = a * b;
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
                Complex d = a.mul(b);
                options.resultDefined = def;
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
                Matrix d = a.mul(b);
                options.resultDefined = def;
                return d;
            } else {
                options.resultDefined = false;
                return zero;
            }
        }
    };

    static {
        ExpressionTransformFactory.setExpressionTransformer(Mul.class, ExpressionTransform.class, new ExpressionTransformer() {

            public Expr transform(Expr expression, ExpressionTransform transform) {
                Mul e = (Mul) expression;
                ExpressionTransform t = transform;
                Expr[] e2 = new Expr[e.expressions.length];
                for (int i = 0; i < e2.length; i++) {
                    e2[i] = ExpressionTransformFactory.transform(e.expressions[i], transform);
                }
                return Maths.sum(e.expressions);
            }
        });
    }

    @NonStateField
    protected Domain _cache_domain;
    private Expr[] expressions;
    private int domainDim;

    public Mul(TVector<Expr> list) {
        this(list.toArray());
    }

    public Mul(Expr... expressions) {
        this.expressions = expressions;
        domainDim = 0;
        for (Expr expression : expressions) {
            int d = expression.getDomainDimension();
            if (d > domainDim) {
                domainDim = d;
            }
        }
    }

    public boolean isZeroImpl() {
        for (Expr expression : expressions) {
            if (expression.isZero()) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return expressions.length;
    }

    public Expr getExpression(int index) {
        return expressions[index];
    }

    //    public boolean isDDx() {
//        for (Expr e : expressions) {
//            if (!e.isDDx()) {
//                return false;
//            }
//        }
//        return true;
//    }

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
        if (isZero()) {
            return Domain.ZERO(getDomainDimension());
        }
        Domain d = Domain.FULL(getDomainDimension());
        for (Expr expression : getSubExpressions()) {
            if (!expression.isZero()) {
                d = d.intersect(expression.getDomain());
            }
        }
        return d.toDomain(getDomainDimension());
    }

    //    @Override
//    public Domain getDomain() {
//        if (isZero()) {
//            return Domain.EMPTYX;
//        }
//        Domain d = Domain.FULLX;
//        if (isDDx()) {
//            for (Expr expression : expressions) {
//                IDDx t = expression.toDDx();
//                if (!t.isZero()) {
//                    d = d.intersect(t.getDomain());
//                }
//            }
//        } else {
//            throw new ClassCastException();
//        }
//        return d;
//    }
    @Override
    public ComponentDimension getComponentDimension() {
        for (Expr expression : getSubExpressions()) {
            ComponentDimension d = expression.getComponentDimension();
            if (d.rows != 1 || d.columns != 1) {
                return d;
            }
        }
        return ComponentDimension.SCALAR;
    }

    public Expr getComponent(int row, int col) {
        if (isDM()) {
            if (isScalarExpr() && (row != col || col != 0)) {
                return FunctionFactory.DZEROXY;
            }
            DoubleToMatrix[] m = new DoubleToMatrix[expressions.length];
            ComponentDimension dd = null;

            for (int i = 0; i < expressions.length; i++) {
                m[i] = expressions[i].toDM();
                ComponentDimension d = m[i].getComponentDimension();
                if (d.equals(ComponentDimension.SCALAR)) {
                    //scalar , ok
                } else {
                    if (dd == null) {
                        dd = d;
                    } else if (!dd.equals(d)) {
                        throw new IllegalArgumentException("Matrix Dimensions does not match");
                    }
                }
            }
            if (dd == null) {
                dd = ComponentDimension.SCALAR;
            }
            if (dd.equals(ComponentDimension.SCALAR)) {
                Expr[] inner = new Expr[expressions.length];
                for (int i = 0; i < inner.length; i++) {
                    inner[i] = m[i].getComponent(row, col);
                }
                return new Mul(inner);
            } else {
                Expr[] inner = new Expr[expressions.length];
                for (int i = 0; i < inner.length; i++) {
                    if (m[i].isScalarExpr()) {
                        inner[i] = m[i].getComponent(0, 0);
                    } else {
                        inner[i] = m[i].getComponent(row, col);
                    }
                }
                return new Mul(inner);
            }
        } else {
            throw new ClassCastException();
        }
    }


    public Expr clone() {
        Mul cloned = (Mul) super.clone();
        cloned.expressions = new Expr[cloned.expressions.length];
        for (int i = 0; i < cloned.expressions.length; i++) {
            cloned.expressions[i] = expressions[i].clone();
        }
        return cloned;
    }

    public List<Expr> getSubExpressions() {
        return Arrays.asList(expressions);
    }

    @Override
    public Complex toComplex() {
        Complex c = Complex.ONE;
        for (Expr e : expressions) {
            c = c.mul(e.toComplex());
        }
        return c;
    }

    @Override
    public double toDouble() {
        double c = 1;
        for (Expr e : expressions) {
            c *= (e.toDouble());
        }
        return c;
    }

    @Override
    public Matrix toMatrix() {
        Matrix c = expressions[0].toMatrix();
        for (int i = 1; i < expressions.length; i++) {
            c = c.mul(expressions[i].toMatrix());
        }
        return c;
    }

    @Override
    public Expr setParam(String name, Expr value) {
        Expr[] updated = new Expr[expressions.length];
        boolean changed = false;
        for (int i = 0; i < updated.length; i++) {
            Expr s1 = expressions[i];
            Expr s2 = s1.setParam(name, value);
            if (s1 != s2) {
                changed = true;
            }
            updated[i] = s2;
        }
        if (changed) {
            Expr e = new Mul(updated);
            e= Any.copyProperties(this, e);
            return Any.updateTitleVars(e,name,value);
        }
        return this;
    }

    @Override
    public Expr composeX(Expr xreplacement) {
        Expr[] updated = new Expr[expressions.length];
        boolean changed = false;
        for (int i = 0; i < updated.length; i++) {
            Expr s1 = expressions[i];
            Expr s2 = s1.composeX(xreplacement);
            if (s1 != s2) {
                changed = true;
            }
            updated[i] = s2;
        }
        if (changed) {
            Expr e = new Mul(updated);
            e= Any.copyProperties(this, e);
            return e;
        }
        return this;
    }

    @Override
    public Expr composeY(Expr yreplacement) {
        Expr[] updated = new Expr[expressions.length];
        boolean changed = false;
        for (int i = 0; i < updated.length; i++) {
            Expr s1 = expressions[i];
            Expr s2 = s1.composeY(yreplacement);
            if (s1 != s2) {
                changed = true;
            }
            updated[i] = s2;
        }
        if (changed) {
            Expr e = new Mul(updated);
            e= Any.copyProperties(this, e);
            return e;
        }
        return this;
    }

    @Override
    public String toString() {
        return FormatFactory.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Mul mul = (Mul) o;

        if (domainDim != mul.domainDim) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(expressions, mul.expressions);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(expressions);
        result = 31 * result + domainDim;
        return result;
    }

    @Override
    public int getDomainDimension() {
        return domainDim;
    }

    @Override
    public Complex computeComplex(double x, double y, double z) {
        Complex c = Complex.ONE;
        for (Expr expression : expressions) {
            c = c.mul(expression.toDC().computeComplex(x, y, z));
        }
        //super.computeComplex(x, y, z)
        return c;
    }

    @Override
    public double computeDouble(double x, double y, double z) {
        double c = 0;
        for (Expr expression : expressions) {
            c *= (expression.toDD().computeDouble(x, y, z));
        }
        //super.computeComplex(x, y, z)
        return c;
    }

    @Override
    public Matrix computeMatrix(double x, double y, double z) {
        Matrix c = expressions[0].toDM().computeMatrix(x, y, z);
        for (int i = 1; i < expressions.length; i++) {
            c = c.mul(expressions[i].toDM().computeMatrix(x, y, z));
        }
        return c;
    }

    @Override
    public Complex computeComplex(double x) {
        return computeComplex(new double[]{x})[0];
    }


    public DoubleToDouble getImag() {
        boolean allDD = true;
        for (Expr e : expressions) {
            if (!e.isDD()) {
                allDD = false;
                break;
            }
        }
        if (allDD) {
            return FunctionFactory.DZERO(getDomainDimension());
        }
        return super.getImag();
    }

    public DoubleToDouble getReal() {
        boolean allDD = true;
        for (Expr e : expressions) {
            if (!e.isDD()) {
                allDD = false;
                break;
            }
        }
        if (allDD) {
            return this;
        }
        return super.getReal();
    }

    @Override
    public double[] computeDouble(double[] x, Domain d0, Out<Range> range) {
        return Expressions.computeDouble(this, binaryExprHelper, x, d0, range);
    }

    @Override
    public double[][] computeDouble(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, binaryExprHelper, x, y, d0, ranges);
    }

    @Override
    public double[][][] computeDouble(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, binaryExprHelper, x, y, z, d0, ranges);
    }

    @Override
    public Complex[][] computeComplex(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeComplex(this, binaryExprHelper, x, y, d0, ranges);
    }

    @Override
    public Complex[] computeComplex(double[] x, Domain d0, Out<Range> ranges) {
        return Expressions.computeComplex(this, binaryExprHelper, x, d0, ranges);
    }

    @Override
    public Complex[][][] computeComplex(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeComplex(this, binaryExprHelper, x, y, z, d0, ranges);
    }

    @Override
    public Matrix[] computeMatrix(double[] x, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, binaryExprHelper, x, d0, ranges);
    }

    @Override
    public Matrix[][] computeMatrix(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, binaryExprHelper, x, y, d0, ranges);
    }

    @Override
    public Matrix[][][] computeMatrix(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, binaryExprHelper, x, y, z, d0, ranges);
    }
}
