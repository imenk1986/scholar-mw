package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.*;

import java.util.Collections;
import java.util.List;

import net.vpc.scholar.hadrumaths.FormatFactory;

/**
 * User: taha Date: 2 juil. 2003 Time: 10:39:39
 */
public abstract class AbstractDoubleToDouble extends AbstractExprPropertyAware implements DoubleToDouble {

    protected Domain domain;

    public AbstractDoubleToDouble(Domain domain) {
        this.domain = domain;
    }

    public double[] computeDouble(double x, double[] y, Domain d0) {
        double[] r = new double[y.length];
        Range abcd = (d0 == null ? domain : domain.intersect(d0)).range(new double[]{x}, y);
        if (abcd != null) {
            int cy = abcd.ymin;
            int dy = abcd.ymax;
            for (int yIndex = cy; yIndex <= dy; yIndex++) {
                r[yIndex] = computeDouble(x, y[yIndex]);
            }
        }
        return r;
    }

    public double[] computeDouble(double[] x, double y, Domain d0) {
        double[] r = new double[x.length];
        Range abcd = (d0 == null ? domain : domain.intersect(d0)).range(x, new double[]{y});
        if (abcd != null) {
            int cy = abcd.ymin;
            int dy = abcd.ymax;
            for (int xIndex = cy; xIndex <= dy; xIndex++) {
                r[xIndex] = computeDouble(x[xIndex], y);
            }
        }
        return r;
    }



    public Domain getDomainImpl() {
        return domain;
    }

    public Domain intersect(DoubleToDouble other) {
        return domain.intersect(other.getDomain());
    }

    public Domain intersect(DoubleToDouble other, Domain someDomain) {
        //return Domain.intersect(domain, other.domain, domain);
        return this.domain.intersect(someDomain).intersect(other.getDomain());
    }

    public DoubleToDouble add(DoubleToDouble... others) {
        return Maths.sum(this, Maths.sum(others)).toDD();
    }

    public DoubleToDouble getSymmetricX(Domain newDomain) {
        return getSymmetricX(((newDomain.xmin() + newDomain.xmax()) / 2));
    }

    public DoubleToDouble getSymmetricY(Domain newDomain) {
        return getSymmetricY(((newDomain.ymin() + newDomain.ymax()) / 2));
    }

    public DoubleToDouble getSymmetricX(double x0) {
        return ((AbstractDoubleToDouble)getSymmetricX()).translate(2 * (x0 - ((domain.xmin() + domain.xmax()) / 2)), 0);
    }

    public DoubleToDouble getSymmetricY(double y0) {
        return ((AbstractDoubleToDouble)getSymmetricY()).translate(0, 2 * (y0 - ((domain.ymin() + domain.ymax()) / 2)));
    }

    public DoubleToDouble mul(double factor, Domain newDomain) {
        throw new IllegalArgumentException("Multiply Not Implemented in " + getClass().getSimpleName());
    }

//    public IDDxy multiply(IDDxy other) {
//        if (other instanceof DDxyCst) {
//            DDxyCst cst = ((DDxyCst) other);
//            return cst.value == 0 ? FunctionFactory.DZEROXY : multiply(cst.value, domain.intersect(other.domain));
//        } else if (other instanceof DDxyAbstractSum) {
//            return (other).multiply(this);
//        } else {
//            return new DDxyProduct(this, other);
//        }
//    }
    public DoubleToDouble simplify() {
        return this;
    }

    public DoubleToDouble toXOpposite() {
        throw new IllegalArgumentException("Not Implemented in " + getClass().getSimpleName());
    }

    public DoubleToDouble toYOpposite() {
        throw new IllegalArgumentException("Not Implemented in " + getClass().getSimpleName());
    }

    public DoubleToDouble getSymmetricX() {
        throw new IllegalArgumentException("Not Implemented in " + getClass().getSimpleName());
    }

    public DoubleToDouble getSymmetricY() {
        throw new IllegalArgumentException("Not Implemented in " + getClass().getSimpleName());
    }

    public DoubleToDouble translate(double deltaX, double deltaY) {
        throw new IllegalArgumentException("Not Implemented in " + getClass().getSimpleName());
    }

    @Override
    public DoubleToDouble clone() {
        return (DoubleToDouble) super.clone();
    }


    public boolean isZeroImpl() {
        return false;
    }

    public boolean isNaNImpl() {
        return false;
    }

    public boolean isInfiniteImpl() {
        return false;
    }

    public boolean isInvariantImpl(Axis axis) {
        return false;
    }

    public boolean isSymmetric(AxisXY axis) {
        switch (axis) {
            case X: {
                return getSymmetricX().equals(this);
            }
            case Y: {
                return getSymmetricY().equals(this);
            }
            case XY: {
                return getSymmetricX().equals(this) && getSymmetricY().equals(this);
            }
        }
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public String toString() {
        return FormatFactory.toString(this);
    }

    public boolean isDCImpl() {
        return true;
    }

    public DoubleToComplex toDC() {
        return Maths.complex(this);
    }

    public boolean isDDImpl() {
        return true;
    }

    public DoubleToDouble toDD() {
        return this;
    }

    public boolean isDMImpl() {
        return true;
    }

    public DoubleToMatrix toDM() {
        return toDC().toDM();
    }

    @Override
    public boolean isDVImpl() {
        return true;
    }

    @Override
    public DoubleToVector toDV() {
        return toDC().toDV();
    }

//    public boolean isDDx() {
//        return (isInvariant(Axis.Y) && isInvariant(Axis.Z));
//    }
//
//    public IDDx toDDx() {
//        if (isDDx()) {
//            return new DDxyToDDx(this, getDomain().getCenterY());
//        }
//        throw new IllegalArgumentException("Unsupported");
//    }

    public List<Expr> getSubExpressions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Expr setParam(String name, Expr value) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractDoubleToDouble)) return false;
        if (!(o.getClass().equals(getClass()))) return false;

        AbstractDoubleToDouble dDxy = (AbstractDoubleToDouble) o;

        if (domain != null ? !domain.equals(dDxy.domain) : dDxy.domain != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domain != null ? domain.hashCode() : 0;
        return result;
    }

    @Override
    public boolean isScalarExprImpl() {
        return true;//getDomainDimension()<=1;
    }

    @Override
    public boolean isDoubleExprImpl() {
        return true;
    }
    @Override
    public boolean isComplexImpl() {
        return isDouble();
    }


    public double[] computeDouble(double[] x, double y, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, x, y, d0, ranges);
    }

    public double[] computeDouble(double x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeDouble(this, x, y, d0, ranges);
    }

    @Override
    public Matrix toMatrix() {
        return toComplex().toMatrix();
    }

    @Override
    public Complex toComplex() {
        return Complex.valueOf(toDouble());
    }
//    public double computeDouble(double x, double y) {
//        return Expressions.computeDouble(this, x, y);
//    }

    public double[][][] computeDouble(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        double[][][] r = new double[z.length][y.length][x.length];
        Range currRange = (d0 == null ? domain : domain.intersect(d0)).range(x, y);
        if (currRange != null) {
            int ax = currRange.xmin;
            int bx = currRange.xmax;
            int cy = currRange.ymin;
            int dy = currRange.ymax;
            BooleanArray3 d = BooleanArrays.newArray(z.length,y.length,x.length);
            currRange.setDefined(d);
            for (int i = currRange.zmin; i <= currRange.zmax; i++) {
                for (int k = ax; k <= bx; k++) {
                    for (int j = cy; j <= dy; j++) {
                        if (contains(x[k], y[j])) {
                            double v = computeDouble0(x[k], y[j]);
                            r[i][j][k] = v;
                            d.set(i,j,k);
                        }
                    }
                }
            }
            if (ranges != null) {
                ranges.set(currRange);
            }
        }
        return r;
    }
    public double[][] computeDouble(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        double[][] r = new double[y.length][x.length];
        Range currRange = (d0 == null ? domain : domain.intersect(d0)).range(x, y);
        if (currRange != null) {
            int ax = currRange.xmin;
            int bx = currRange.xmax;
            int cy = currRange.ymin;
            int dy = currRange.ymax;
            BooleanArray2 d = BooleanArrays.newArray(y.length,x.length);
            currRange.setDefined(d);
            for (int k = ax; k <= bx; k++) {
                for (int j = cy; j <= dy; j++) {
                    if(contains(x[k], y[j])) {
                        double v = computeDouble0(x[k], y[j]);
                        r[j][k] = v;
                        d.set(j,k);
                    }
                }
            }
            if (ranges != null) {
                ranges.set(currRange);
            }
        }
        return r;
    }


    @Override
    public double[] computeDouble(double[] x, Domain d0, Out<Range> ranges) {
        double[] r = new double[x.length];
        Range currRange = (d0 == null ? domain : domain.intersect(d0)).range(x);
        if (currRange != null) {
            int ax = currRange.xmin;
            int bx = currRange.xmax;
            BooleanArray1 d = BooleanArrays.newArray(x.length);
            currRange.setDefined(d);
            for (int xIndex = ax; xIndex <= bx; xIndex++) {
                if(contains(x[xIndex])) {
                    r[xIndex] = computeDouble0(x[xIndex]);
                }else{
                    d.clear(xIndex);
                }
            }
            if (ranges != null) {
                ranges.set(currRange);
            }
        }
        return r;
    }

//    @Override
    public Complex[][][] computeComplex(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        double[][][] d = computeDouble(x, y, z, d0, ranges);
        Complex[][][] m = new Complex[d.length][d[0].length][d[0][0].length];
        for (int zi = 0; zi < m.length; zi++) {
            for (int yi = 0; yi < m[zi].length; zi++) {
                for (int xi = 0; xi < m[zi][yi].length; xi++) {
                    m[zi][yi][xi]=Complex.valueOf(d[zi][yi][xi]);
                }
            }
        }
        return m;
    }

    public Matrix[][][] computeMatrix(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        double[][][] d = computeDouble(x, y, z, d0, ranges);
        Matrix[][][] m = new Matrix[d.length][d[0].length][d[0][0].length];
        for (int zi = 0; zi < m.length; zi++) {
            for (int yi = 0; yi < m[zi].length; zi++) {
                for (int xi = 0; xi < m[zi][yi].length; xi++) {
                    m[zi][yi][xi]= Maths.constantMatrix(1, Complex.valueOf(d[zi][yi][xi]));
                }
            }
        }
        return m;
    }

    @Override
    public final double computeDouble(double x) {
        switch (getDomainDimension()) {
            case 1: {
                if (contains(x)) {
                    return computeDouble0(x);
                }
                return 0;
            }
        }
        throw new IllegalArgumentException("Missing y");
    }

    @Override
    public final double computeDouble(double x, double y, double z) {
        switch (getDomainDimension()) {
            case 1: {
                if (contains(x)) {
                    return computeDouble0(x);
                }
                return 0;
            }
            case 2: {
                if (contains(x,y)) {
                    return computeDouble0(x,y);
                }
                return 0;
            }
            case 3: {
                if (contains(x,y,z)) {
                    return computeDouble0(x,y,z);
                }
                return 0;
            }
        }
        throw new IllegalArgumentException("Invalid domain "+getDomainDimension());
    }

    @Override
    public double computeDouble(double x, double y) {
        switch (getDomainDimension()) {
            case 1: {
                if (contains(x)) {
                    return computeDouble0(x);
                }
                return 0;
            }
            case 2: {
                if (contains(x,y)) {
                    return computeDouble0(x,y);
                }
                return 0;
            }
        }
        if (contains(x, y)) {
            return computeDouble0(x,y);
        }
        return 0;
    }


    protected boolean contains(double x) {
        return domain.contains(x);
    }

    protected boolean contains(double x,double y){
        return domain.contains(x,y);
    }

    protected boolean contains(double x,double y,double z) {
        return domain.contains(x,y,z);
    }

    protected abstract double computeDouble0(double x) ;

    protected abstract double computeDouble0(double x,double y) ;

    protected abstract double computeDouble0(double x,double y,double z) ;



    @Override
    public double[] computeDouble(double[] x) {
        return computeDouble(x,(Domain)null,null);
    }

    @Override
    public double[] computeDouble(double x, double[] y) {
        return computeDouble(x,y,(Domain)null,null);
    }

    @Override
    public double[][][] computeDouble(double[] x, double[] y, double[] z) {
        return computeDouble(x,y,z,(Domain)null,null);
    }

    @Override
    public double[][] computeDouble(double[] x, double[] y) {
        return computeDouble(x,y,(Domain)null,null);
    }

    @Override
    public ComponentDimension getComponentDimension() {
        return ComponentDimension.SCALAR;
    }


}
