package net.vpc.scholar.hadrumaths;

import net.vpc.scholar.hadrumaths.symbolic.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpc on 4/7/17.
 */
public final class ComplexI extends Complex {
    private double imag;

    public ComplexI(double imag) {
        this.imag = imag;
        if(imag==0){
            throw new IllegalArgumentException("Zero Imag is Real!");
        }
    }


    public double getImag() {
        return imag;
    }

    public double getReal() {
        return 0;
    }

    public Complex imag() {
        return Complex.valueOf(imag);
    }

    public Complex real() {
        return Complex.valueOf(0);
    }

    public double realdbl() {
        return 0;
    }

    public double imagdbl() {
        return imag;
    }

    public Complex add(double c) {
        return Complex.valueOf(c, imag);
    }

    public Complex add(Complex c) {
        return Complex.valueOf(c.getReal(), imag + c.getImag());
    }

    public static void main(String[] args) {
        System.out.println(new ComplexI(0.34397383742184495).npow(4));
        System.out.println(new ComplexOld(0,0.34397383742184495).npow(4));
    }
    public Complex npow(int n) {
        if (equals(ONE)) {
            return ONE;
        } else if (equals(ZERO)) {
            if (n == 0) {
                return ONE;
            } else if (n > 0) {
                return ZERO;
            } else {//if(n<0)
                throw new ArithmeticException("Divide by zero");
            }
        } else if (n == 0) {
            return ONE;
        } else if (n == 1) {
            return this;
        } else if (n > 0) {
            return npow(n - 1).mul(this);
        } else {
            return npow(n + 1).div(this);
        }
    }

    public Complex inv() {
        return Complex.I(-1 / imag);
    }

    public Complex conj() {
        return Complex.I(-imag);
    }

    public Complex mul(double c) {
        return Complex.I(imag * c);
    }

    public Complex mulAll(double... c) {
        double i = imag;
        for (double aC : c) {
            i *= aC;
        }
        return Complex.I(i);
    }

    public Complex divAll(double... c) {
        double i = imag;
        for (double aC : c) {
            i /= aC;
        }
        return Complex.I(i);
    }

    public Complex addAll(double... c) {
        double i = imag;
        for (double aC : c) {
            i += aC;
        }
        return Complex.I(i);
    }

    public Complex mul(Complex c) {
        return Complex.valueOf( - imag * c.getImag(), imag * c.getReal());
    }

    public CArray mul(CArray c) {
        return c.mul(this);
    }

    public CArray mul(Complex[] c) {
        return new CArray(c).mul(this);
    }

    public CArray mul(double[] c) {
        return new CArray(c).mul(this);
    }

    public Complex sub(Complex c) {
        return Complex.valueOf(- c.getReal(), imag - c.getImag());
    }

    public Complex sub(double c) {
        return Complex.valueOf(- c, imag);
    }

    public Complex div(double c) {
        return Complex.valueOf(0, imag / c);
    }

    public Complex div(Complex other) {
        double b = imag;
        double c = other.getReal();
        double d = other.getImag();
        double c2d2 = c * c + d * d;

        return Complex.valueOf(
                (b * d) / c2d2,
                (b * c) / c2d2
        );
        //return mul(c.inv());
    }

    public Complex exp() {
        double e = 1;
        return Complex.valueOf(e * Maths.cos2(imag), e * Maths.sin2(imag));
    }

    public Complex abs() {
        return Complex.valueOf(Math.sqrt(imag * imag));
    }

    public double absdbl() {
        return Math.sqrt(imag * imag);
    }

    public double absdblsqr() {
        return (imag * imag);
    }

    public double absSquare() {
        return imag * imag;
    }

    public Complex neg() {
        return Complex.I(-imag);
    }

    public int compareTo(Complex c) {
        double a1 = absdbl();
        double a2 = c.absdbl();
        if (a1 > a2) {
            return 1;
        } else if (a1 < a2) {
            return -1;
        } else {
            if (0 > c.getReal()) {
                return 1;
            } else if (0 < c.getReal()) {
                return -1;
            } else {
                if (imag > c.getImag()) {
                    return 1;
                } else if (imag < c.getImag()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public int compareTo(Object c) {
        return compareTo((Complex) c);
    }

    public boolean equals(Complex c) {
        return 0 == c.getReal() && imag == c.getImag();
    }

    @Override
    public boolean equals(Object c) {
        if (c != null && c instanceof Complex) {
            return equals((Complex) c);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.imag) ^ (Double.doubleToLongBits(this.imag) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(0) ^ (Double.doubleToLongBits(0) >>> 32));
        return hash;
    }

    public int intValue() {
        return (int) getReal();
    }

    /**
     * Returns the value of the specified number as a <code>long</code>. This
     * may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion to
     * type <code>long</code>.
     */
    public long longValue() {
        return (long) getReal();
    }

    /**
     * Returns the value of the specified number as a <code>float</code>. This
     * may involve rounding.
     *
     * @return the numeric value represented by this object after conversion to
     * type <code>float</code>.
     */
    public float floatValue() {
        return (float) getReal();
    }

    /**
     * Returns the value of the specified number as a <code>double</code>. This
     * may involve rounding.
     *
     * @return the numeric value represented by this object after conversion to
     * type <code>double</code>.
     */
    public double doubleValue() {
        return getReal();
    }

    public Complex sincard() {
        if (isZero()) {
            return ONE;
        }
        return sin().div(this);
    }

    public Complex sin() {
        return Complex.I(Maths.sin2(imag));
    }

    public Complex cos() {
        if (imag == 0) {
            return Complex.ONE;
        }
        return Complex.valueOf(Math.cosh(imag));
    }

    public Complex tan() {
        return sin().div(cos());
    }

    public Complex atan() {
        return Complex.I.mul(-0.5).mul(

                ((I.sub(this)).div(I.add(this))).log()
        );
    }

    public Complex acos() {
        return Complex.I.mul(-1).mul(

                (
                        this.add(
                                (this.sqr().sub(ONE)).sqrt()
                        ).log()
                )
        );
    }

    public Complex acosh() {
        Complex z = this;
        return (z.add((z.sqr().sub(ONE)).sqrt())).log();
    }

    public Complex asinh() {
        Complex z = this;
        //z +sqrt(z^2-1)
        return (z.add((z.sqr().add(ONE)).sqrt())).log();
    }

    public Complex arg() {
        return Complex.valueOf(Math.atan(getImag() / getReal()));
    }

    public Complex asin() {
        return Complex.I.mul(-1).mul(

                (
                        (this.mul(I)
                                .add(
                                        (ONE.sub(this.sqr()))
                                                .sqrt()
                                )
                        ).log()
                )

        );
    }

    public Complex acotan() {
        if (isZero()) {
            return HALF_PI;
        }
        return inv().atan();
    }

    public Complex cotan() {
        return cos().div(sin());
    }

    public Complex sinh() {
        return exp().sub(this.neg().exp()).div(2);
    }

    public Complex cosh() {
        return exp().add(this.neg().exp()).div(2);
    }

    public Complex tanh() {
        Complex eplus = exp();
        Complex eminus = this.neg().exp();
        return (eplus.sub(eminus)).div(eplus.add(eminus));
//        return sinh().divide(cosh());
    }

    public Complex cotanh() {
        Complex eplus = exp();
        Complex eminus = this.neg().exp();
        return (eplus.add(eminus)).div(eplus.sub(eminus));
//        return cosh().divide(sinh());
    }

    public Complex log() {
        return Complex.valueOf(Math.log(absdbl()), Math.atan2(imag, 0));
    }

    public Complex log10() {
        return Complex.valueOf(Math.log(absdbl()), Math.atan2(imag, 0)).div(Math.log(10));
    }

    public Complex db() {
        return Complex.valueOf(Math.log10(absdbl()) * (10));
        //return log10().mul(10);
    }

    public Complex db2() {
        return Complex.valueOf(Math.log10(absdbl()) * (20));
        //return log10().mul(10);
    }

    @Override
    public String toString() {
        if (Double.isNaN(imag)) {
            return String.valueOf(imag);
        }
        String imag_string = String.valueOf(imag);
            return ((imag == 1) ? "i" : (imag == -1) ? "i" : (imag > 0) ? ("" + (imag_string + "i")) : (imag_string + "i"));
    }

    public Complex angle() {
        //workaround
//        if(real==0){
//            return imag>=0?Math.PI/2:-Math.PI/2;
//        }else if(imag==0){
//            return real>=0?0:Math.PI;
//        }
        return Complex.valueOf(Math.atan2(imag, 0));
    }

    public Complex sqr() {
        return this.mul(this);
    }

    public double dsqrt() {
        return Double.NaN;
    }

    public Complex sqrt() {
        double r = Math.sqrt(absdbl());
        double theta = angle().toDouble() / 2;
        return Complex.valueOf(r * Maths.cos2(theta), r * Maths.sin2(theta));
    }

    public Complex sqrt(int n) {
        return n == 0 ? ONE : pow(1.0 / n);
    }

    public Complex pow(Complex y) {
        if (y.getImag() == 0) {
            return pow(y.getReal());
        }
        //x^y=exp(y*ln(x))
        return y.mul(this.log()).exp();
    }

    public Complex pow(double power) {
        if (power == 0) {
            return ONE;
        } else if (power == 1) {
            return this;
        } else if (power == -1) {
            return inv();
        } else if (power == 2) {
            return sqr();
//        } else if (imag == 0) {
//            return real >= 0 ? new Complex(Math.pow(real, power), 0) : new Complex(0, Math.pow(-real, power));
        } else if (power >= 0) {
            double r = Math.pow(absdbl(), power);
            double angle = angle().toDouble();
            double theta = angle * power;
            return Complex.valueOf(r * Maths.cos2(theta), r * Maths.sin2(theta));
        } else { //n<0
            power = -power;
            double r = Math.pow(absdbl(), power);
            double theta = angle().toDouble() * power;
            Complex c = Complex.valueOf(r * Maths.cos2(theta), r * Maths.sin2(theta));
            return c.inv();
        }
    }

    public boolean isNaN() {
        return Double.isNaN(imag);
    }

    public boolean isZero() {
        return imag == 0;
    }

    public boolean isInfinite() {
        return Double.isInfinite(imag);
    }

    //    public static Complex sin(Complex c){
//        return c.sin();
//    }
//
//    public static Complex sinh(Complex c){
//        return c.sinh();
//    }
//
//    public static Complex cos(Complex c){
//        return c.cos();
//    }
//
//    public static Complex cosh(Complex c){
//        return c.cosh();
//    }
    public boolean isDC() {
        return true;
    }

    public boolean isDD() {
        return isNaN() || imag == 0;
    }

//    public boolean isDDx() {
//        return isNaN() || imag == 0;
//    }

    public boolean isDM() {
        return true;
    }

    public DoubleToComplex toDC() {
        return new ComplexValue(this, Domain.FULLX);
    }

    @Override
    public boolean isDV() {
        return true;
    }

    @Override
    public DoubleToVector toDV() {
        return null;
    }

    public DoubleToDouble toDD() {
        if (imag == 0) {
            return DoubleValue.valueOf(getReal(), Domain.FULL(getDomainDimension()));
        }
        if (isNaN()) {
            return DoubleValue.valueOf(Double.NaN, Domain.FULL(getDomainDimension()));
        }
        throw new ClassCastException();
    }

//    public IDDx toDDx() {
//        if (imag == 0) {
//            return new DDxLinear(Domain.FULLX, 0, getReal());
//        }
//        throw new ClassCastException();
//    }

    public DoubleToMatrix toDM() {
        return toDC().toDM();
    }

    public boolean isReal() {
        return false;
    }

    public boolean isImag() {
        return true;
    }

    public double toReal() {
        throw new ClassCastException("Complex has imaginary value and cant be cast to double");
    }
}