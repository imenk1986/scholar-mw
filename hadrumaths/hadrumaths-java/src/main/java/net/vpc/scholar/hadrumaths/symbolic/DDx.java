package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.Expr;
import net.vpc.scholar.hadrumaths.Out;

import java.util.Arrays;
import java.util.List;

/**
* Created by IntelliJ IDEA. User: vpc Date: 29 juil. 2005 Time: 20:33:56 To
* change this template use File | Settings | File Templates.
*/
public class DDx extends AbstractDoubleToDouble implements Cloneable{

    private static final long serialVersionUID = -1010101010101001008L;
    DoubleToDouble base;
    double defaultY;
    double defaultZ;

    public DDx(DoubleToDouble base, double defaultY,double defaultZ) {
        super(Domain.forBounds(base.getDomain().xmin(), base.getDomain().xmax()));
        this.base = base;
        this.defaultY = defaultY;
        this.defaultZ = defaultZ;
    }

    public boolean isZeroImpl() {
        return base.isZero();
    }

    public boolean isNaNImpl() {
        return base.isNaN();
    }

    public boolean isInfiniteImpl() {
        return base.isInfinite();
    }

    public double computeDouble0(double x) {
        return base.computeDouble(x, defaultY,defaultZ);
    }

    @Override
    protected double computeDouble0(double x, double y) {
        return base.computeDouble(x, defaultY,defaultZ);
    }

    @Override
    protected double computeDouble0(double x, double y, double z) {
        return base.computeDouble(x, defaultY,defaultZ);
    }

    public double[] computeDouble(double[] x, Domain d0, Out<Range> range) {
        Out<Range> r2 = new Out<Range>();
        double[][] yy = base.computeDouble(x, new double[]{defaultY}, d0 == null ? null : Domain.forBounds(d0.xmin(), d0.xmax(), base.getDomain().ymin(), base.getDomain().ymax()), r2);
        if (range != null) {
            range.set(Range.forBounds(r2.get().xmin, r2.get().xmax));
        }
        return yy[0];
    }

    public boolean isInvariantImpl(Axis axis) {
        switch (axis) {
            case X: {
                return base.isInvariant(axis);
            }
        }
        return true;
    }


//    public DDx multiply(double factor, DomainX newDomain) {
//        return new DDxyToDDx(base.multiply(factor, newDomain == null ? null : new DomainXY(newDomain.xmin, base.getDomain().ymin, newDomain.xmax,
//                base.getDomain().ymax)), defaultY);
//    }
//
//    public DDx toXOpposite() {
//        return new DDxyToDDx(base.toXOpposite(), defaultY);
//    }
//
//    public DDx toSymmetric() {
//        return new DDxyToDDx(base.getSymmetricX(), defaultY);
//    }
//
//    public DDx translate(double xDelta, double yDelta) {
//        return new DDxyToDDx(base.translate(xDelta, yDelta), defaultY);
//    }
//    public DDx simplify() {
//        return new DDxyToDDx(base.simplify(), defaultY);
//    }

    public double getDefaultY() {
        return defaultY;
    }

    public void setDefaultY(double defaultY) {
        this.defaultY = defaultY;
    }


    public DoubleToDouble getArg() {
        return base;
    }


    public List<Expr> getSubExpressions() {
        return Arrays.asList(new Expr[]{base});
    }

    @Override
    public Expr setParam(String name, Expr value) {
        Expr updated = base.setParam(name, value);
        if (updated != base) {
            Expr e = new DDx(updated.toDD(), defaultY,defaultZ);
            e= Any.copyProperties(this, e);
            return Any.updateTitleVars(e,name,value);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DDx)) return false;
        if (!super.equals(o)) return false;

        DDx dDxyToDDx = (DDx) o;

        if (Double.compare(dDxyToDDx.defaultY, defaultY) != 0) return false;
        if (Double.compare(dDxyToDDx.defaultZ, defaultZ) != 0) return false;
        if (base != null ? !base.equals(dDxyToDDx.base) : dDxyToDDx.base != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (base != null ? base.hashCode() : 0);
        temp = Double.doubleToLongBits(defaultY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(defaultZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
