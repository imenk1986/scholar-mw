package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.Expr;

import java.util.Arrays;
import java.util.List;

/**
* Created by IntelliJ IDEA. User: vpc Date: 29 juil. 2005 Time: 20:33:56 To
* change this template use File | Settings | File Templates.
*/
public class DDz extends AbstractDoubleToDouble implements Cloneable{

    private static final long serialVersionUID = -1010101010101001008L;
    DoubleToDouble base;
    double defaultX;
    double defaultY;

    public DDz(DoubleToDouble base, double defaultX, double defaultY) {
        super(Domain.forBounds(base.getDomain().xmin(), base.getDomain().xmax()));
        this.base = base;
        this.defaultX = defaultX;
        this.defaultY = defaultY;
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
        return base.computeDouble(defaultX, defaultY,x);
    }

    @Override
    protected double computeDouble0(double x, double y) {
        return base.computeDouble(defaultX, defaultY,x);
    }

    @Override
    protected double computeDouble0(double x, double y, double z) {
        return base.computeDouble(defaultX, defaultY,x);
    }


    public boolean isInvariantImpl(Axis axis) {
        switch (axis) {
            case Z: {
                return base.isInvariant(axis);
            }
        }
        return true;
    }


    public double getDefaultX() {
        return defaultX;
    }

    public void setDefaultX(double defaultX) {
        this.defaultX = defaultX;
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
            Expr e = new DDz(updated.toDD(), defaultX, defaultY);
            e= Any.copyProperties(this, e);
            return Any.updateTitleVars(e,name,value);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DDz)) return false;
        if (!super.equals(o)) return false;

        DDz dDxyToDDx = (DDz) o;

        if (Double.compare(dDxyToDDx.defaultX, defaultX) != 0) return false;
        if (Double.compare(dDxyToDDx.defaultY, defaultY) != 0) return false;
        if (base != null ? !base.equals(dDxyToDDx.base) : dDxyToDDx.base != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (base != null ? base.hashCode() : 0);
        temp = Double.doubleToLongBits(defaultX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(defaultY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
