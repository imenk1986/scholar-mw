package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.Expr;
import net.vpc.scholar.hadrumaths.Expressions;
import net.vpc.scholar.hadrumaths.Out;
import net.vpc.scholar.hadrumaths.UnsupportedDomainDimensionException;

/**
 * User: taha
 * Date: 2 juil. 2003
 * Time: 11:51:13
 */
public final class Linear extends AbstractDoubleToDouble implements Cloneable{
    private static final long serialVersionUID = -1010101010101001018L;
    public double a;
    public double b;
    public double c;

    public static Linear castOrConvert(Expr e){
        if(e instanceof Linear){
            return (Linear) e;
        }
        if(e instanceof XX){
            return new Linear(1,0,0,Domain.EMPTYX);
        }
        if(e instanceof YY){
            return new Linear(0,1,0,Domain.EMPTYX);
        }
        if(e instanceof DoubleValue){
            DoubleValue d = (DoubleValue) e;
            return new Linear(0,0, d.getValue(),d.getDomain());
        }
        return null;
    }

    @Override
    public boolean isZeroImpl() {
        return a==0 && b==0 && c==0;
    }

    @Override
    public boolean isDouble() {
        return super.isDouble();
    }

    @Override
    public boolean isDoubleImpl() {
        return super.isDoubleImpl();
    }

    @Override
    public boolean isNaNImpl() {
        return
                Double.isNaN(a)
                ||Double.isNaN(b)
                ||Double.isNaN(c);
    }

    @Override
    public boolean isInfiniteImpl() {
        return
                Double.isInfinite(a)
                ||Double.isInfinite(b)
                ||Double.isInfinite(c);
    }

    @Override
    public boolean isInvariantImpl(Axis axis) {
        switch(axis){
            case X:{
                return (a == 0);
            }
            case Y:{
                return (b == 0);
            }
            case Z:{
                return true;
            }
        }
        throw new UnsupportedDomainDimensionException();
    }

    public Linear(double a, double b, double c, Domain domain) {
        super(domain);
        this.a = a;
        this.b = b;
        this.c = c;
//        if(b!=0 && domain.getDimension()<=1){
//            throw new IllegalArgumentException("Domain dimension mismatch");
//        }
    }

    public double computeDouble0(double x, double y,double z) {
        return a * x + b *y +c;
    }

    public double computeDouble0(double x, double y) {
        return a * x + b *y +c;
    }

    public double computeDouble0(double x) {
        if(b==0){
            return a * x +c;
        }
        throw new IllegalArgumentException("Missing y");
    }

    public AbstractDoubleToDouble mul(double factor, Domain newDomain) {
        return new Linear(
                a*factor,
                b*factor,
                c*factor,
                newDomain==null?domain: domain.intersect(newDomain));
    }

    public AbstractDoubleToDouble getSymmetricX() {
        double a2 = -a;
        double c2 = a * (domain.xmin() + domain.xmax()) + c;
        return new Linear(a2, b,c2, domain);
    }

    public AbstractDoubleToDouble getSymmetricY() {
        return new Linear(a, -b,b * (domain.ymin ()+ domain.ymax()) + c, domain);
    }

    public AbstractDoubleToDouble translate(double deltaX, double deltaY) {
        return new Linear(a, b,c - a * deltaX-b*deltaY, domain.translate(deltaX, deltaY));
    }

    public AbstractDoubleToDouble toXOpposite() {
        return new Linear(-a, b, c,domain);
    }

    public AbstractDoubleToDouble toYOpposite() {
        return new Linear(a, -b, c,domain);
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }
    
//    public double leftScalarProduct(DFunction other) {
//        Domain d=(this.domain.intersect(other.getDomain()));
//        if(!d.isEmpty()){
//            return ScalarProductFactory.COSCOS_LINEAR.compute(d,other,this);
//        }else{
//            return 0;
//        }
//    }
//
//    public double rightScalarProduct(DFunction other) {
//        Domain d=this.domain.intersect(other.getDomain());
//        if(!d.isEmpty()){
//            return ScalarProductFactory.COSCOS_LINEAR.compute(d,other,this);
//        }else{
//            return 0;
//        }
//    }


    @Override
    public Expr setParam(String name, Expr value) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Linear)) return false;
        if (!super.equals(o)) return false;

        Linear that = (Linear) o;

        if (Double.compare(that.a, a) != 0) return false;
        if (Double.compare(that.b, b) != 0) return false;
        if (Double.compare(that.c, c) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(a);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(c);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public double[] computeDouble(double[] x, Domain d0, Out<Range> range) {
        throw new IllegalArgumentException("Missing y");
    }

    public double[][][] computeDouble(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        return Expressions.computeDoubleFromXY(this, x, y, z, d0, ranges);
    }
}
