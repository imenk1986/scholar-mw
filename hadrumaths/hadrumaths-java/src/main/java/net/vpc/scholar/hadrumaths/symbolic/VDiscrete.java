package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.ComponentDimension;
import net.vpc.scholar.hadrumaths.Expr;
import net.vpc.scholar.hadrumaths.PlaneAxis;
import net.vpc.scholar.hadrumaths.UnsupportedComponentDimensionException;

import java.util.Arrays;
import java.util.List;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 18 juil. 2007 23:36:28
 */
public class VDiscrete extends AbstractDoubleToVector implements Cloneable,Normalizable {
    private static final long serialVersionUID = 1L;
    private Discrete[] values;
    private Domain domain;
    private ComponentDimension componentDimension;

    public VDiscrete(Discrete fx) {
        values = new Discrete[1];
        if (fx == null) {
            throw new NullPointerException();
        }
        values[Axis.X.ordinal()] = fx;
//        values[Axis.Y.ordinal()] = Discrete.cst(fx.getDomain(), Complex.ZERO, fx.getDx(), fx.getDy(), fx.getDz(), fx.getAxis()[0], fx.getAxis()[1], fx.getAxis()[2]);
//        values[Axis.Z.ordinal()] = Discrete.cst(fx.getDomain(), Complex.ZERO, fx.getDx(), fx.getDy(), fx.getDz(), fx.getAxis()[0], fx.getAxis()[1], fx.getAxis()[2]);
        domain = values[0].getDomain();//.expand(values[1].getDomain()).expand(values[2].getDomain());
        componentDimension = ComponentDimension.SCALAR;
    }

    public VDiscrete(Discrete fx, Discrete fy) {
        values = new Discrete[2];
        if (fx == null) {
            throw new NullPointerException();
        }
        if (fy == null) {
            throw new NullPointerException();
        }
        values[Axis.X.ordinal()] = fx;
        values[Axis.Y.ordinal()] = fy;
        //values[Axis.Z.ordinal()] = Discrete.cst(fx.getDomain(), Complex.ZERO, fx.getDx(), fx.getDy(), fx.getDz(), fx.getAxis()[0], fx.getAxis()[1], fx.getAxis()[2]);
        domain = values[0].getDomain().expand(values[1].getDomain());//.expand(values[2].getDomain());
        componentDimension = ComponentDimension.VECTOR2;
    }

    public VDiscrete(Discrete fx, Discrete fy, Discrete fz) {
        values = new Discrete[3];
        if (fx == null) {
            throw new NullPointerException();
        }
        if (fy == null) {
            throw new NullPointerException();
        }
        if (fz == null) {
            throw new NullPointerException();
        }
        values[Axis.X.ordinal()] = fx;
        values[Axis.Y.ordinal()] = fy;
        values[Axis.Z.ordinal()] = fz;
        domain = values[0].getDomain().expand(values[1].getDomain()).expand(values[2].getDomain());
        componentDimension = ComponentDimension.VECTOR3;
    }

    public VDiscrete mul(Complex c) {
        switch (componentDimension.rows) {
            case 1: {
                return new VDiscrete(values[0].mul(c));
            }
            case 2: {
                return new VDiscrete(values[0].mul(c), values[1].mul(c));
            }
            case 3: {
                return new VDiscrete(values[0].mul(c), values[1].mul(c), values[2].mul(c));
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public VDiscrete mul(double d) {
        switch (componentDimension.rows) {
            case 1: {
                return new VDiscrete(values[0].mul(d));
            }
            case 2: {
                return new VDiscrete(values[0].mul(d), values[1].mul(d));
            }
            case 3: {
                return new VDiscrete(values[0].mul(d), values[1].mul(d), values[2].mul(d));
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public VDiscrete rot() {
        switch (componentDimension.rows) {
            case 3: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                Discrete fz = values[Axis.Z.ordinal()];
                return new VDiscrete(
                        (fz.diff(Axis.Y)).sub((fy.diff(Axis.Z))),
                        (fx.diff(Axis.Z)).sub((fz.diff(Axis.X))),
                        (fy.diff(Axis.X)).sub((fx.diff(Axis.Y)))
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }


    public Discrete getComponent(Axis axis) {
        if(axis.ordinal()>=values.length){
            throw new IllegalArgumentException("Component "+axis+" is not defined");
        }
        Discrete value = values[axis.ordinal()];
        if(value==null){
            throw new IllegalArgumentException("Component "+axis+" is not defined");
        }
        return value;
    }

    /**
     * vector product
     *
     * @param other
     * @return
     */
    public VDiscrete crossprod(VDiscrete other) {
        switch (componentDimension.rows) {
            case 3: {
                Discrete u1 = values[Axis.X.ordinal()];
                Discrete u2 = values[Axis.Y.ordinal()];
                Discrete u3 = values[Axis.Z.ordinal()];

                Discrete v1 = other.values[Axis.X.ordinal()];
                Discrete v2 = other.values[Axis.Y.ordinal()];
                Discrete v3 = other.values[Axis.Z.ordinal()];

                return new VDiscrete(
                        (u2.mul(v3)).sub((u3.mul(v2))),
                        (u3.mul(v1)).sub((u1.mul(v3))),
                        (u1.mul(v2)).sub((u2.mul(v1)))
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);


    }

    public VDiscrete diff(Axis axis) {
        switch (componentDimension.rows) {
            case 1: {
                Discrete fx = values[Axis.X.ordinal()];
                return new VDiscrete(
                        fx.diff(axis)
                );
            }
            case 2: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                return new VDiscrete(
                        fx.diff(axis),
                        fy.diff(axis)
                );
            }
            case 3: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                Discrete fz = values[Axis.Z.ordinal()];
                return new VDiscrete(
                        fx.diff(axis),
                        fy.diff(axis),
                        fz.diff(axis)
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    /**
     * divergence
     *
     * @return divergence
     */
    public Discrete divergence() {
        switch (componentDimension.rows) {
            case 1: {
                Discrete fx = values[Axis.X.ordinal()];
                return (fx.diff(Axis.X));
            }
            case 2: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                return (fx.diff(Axis.X)).add((fy.diff(Axis.Y)));
            }
            case 3: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                Discrete fz = values[Axis.Z.ordinal()];
                return (fx.diff(Axis.X)).add((fy.diff(Axis.Y))).add((fz.diff(Axis.Z)));
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public VDiscrete sub(VDiscrete other) {
        switch (componentDimension.rows) {
            case 1: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].sub(other.getComponent(Axis.X))
                );
            }
            case 2: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].sub(other.getComponent(Axis.X)),
                        values[Axis.Y.ordinal()].sub(other.getComponent(Axis.Y))
                );
            }
            case 3: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].sub(other.getComponent(Axis.X)),
                        values[Axis.Y.ordinal()].sub(other.getComponent(Axis.Y)),
                        values[Axis.Z.ordinal()].sub(other.getComponent(Axis.Z))
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public VDiscrete relativeError(VDiscrete other) {
        switch (componentDimension.rows) {
            case 1: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].relativeError(other.getComponent(Axis.X))
                );
            }
            case 2: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].relativeError(other.getComponent(Axis.X)),
                        values[Axis.Y.ordinal()].relativeError(other.getComponent(Axis.Y))
                );
            }
            case 3: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].relativeError(other.getComponent(Axis.X)),
                        values[Axis.Y.ordinal()].relativeError(other.getComponent(Axis.Y)),
                        values[Axis.Z.ordinal()].relativeError(other.getComponent(Axis.Z))
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public VDiscrete add(VDiscrete other) {
        switch (componentDimension.rows) {
            case 1: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].add(other.getComponent(Axis.X))
                );
            }
            case 2: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].add(other.getComponent(Axis.X)),
                        values[Axis.Y.ordinal()].add(other.getComponent(Axis.Y))
                );
            }
            case 3: {
                return new VDiscrete(
                        values[Axis.X.ordinal()].add(other.getComponent(Axis.X)),
                        values[Axis.Y.ordinal()].add(other.getComponent(Axis.Y)),
                        values[Axis.Z.ordinal()].add(other.getComponent(Axis.Z))
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    /**
     * @return gradient
     */
    public VDiscrete grad() {
        switch (componentDimension.rows) {
            case 1: {
                Discrete fx = values[Axis.X.ordinal()];
                return new VDiscrete(
                        (fx.diff(Axis.X))
                );
            }
            case 2: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];

                return new VDiscrete(
                        (fx.diff(Axis.X)),
                        (fy.diff(Axis.Y))
                );
            }
            case 3: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                Discrete fz = values[Axis.Z.ordinal()];

                return new VDiscrete(
                        (fx.diff(Axis.X)),
                        (fy.diff(Axis.Y)),
                        (fz.diff(Axis.Z))
                );
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public double norm() {
        switch (componentDimension.rows) {
            case 1: {
                Discrete fx = values[Axis.X.ordinal()];
                return fx.norm();
            }
            case 2: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                return fx.norm() + fy.norm();
            }
            case 3: {
                Discrete fx = values[Axis.X.ordinal()];
                Discrete fy = values[Axis.Y.ordinal()];
                Discrete fz = values[Axis.Z.ordinal()];
                return fx.norm() + fy.norm() + fz.norm();
            }
        }
        throw new UnsupportedComponentDimensionException(componentDimension.rows);
    }

    public Discrete[] getValues() {
        return values;
    }



    public Matrix getMatrix(Axis axis, PlaneAxis plane, int index) {
        return Maths.matrix(getArray(axis, plane.getNormalAxis(), index));
    }

    public Matrix getMatrix(Axis axis, Axis fixedNormalAxis, int index) {
        return Maths.matrix(getComponent(axis).getArray(fixedNormalAxis, index));
    }

    public Complex[][] getArray(Axis axis, PlaneAxis plane, int index) {
        return getArray(axis, plane.getNormalAxis(), index);
    }

    public Complex[][] getArray(Axis axis, Axis fixedNormalAxis, int index) {
        return getComponent(axis).getArray(fixedNormalAxis, index);
    }

    public Complex getValueAt(Axis axis, int xIndex, int yIndex, int zIndex) {
        return getComponent(axis).getValues()[zIndex][yIndex][xIndex];
    }

    /**
     * Example : getVector(Axis.X, Axis.X, Axis.Y,0, Axis.Z,0)
     * from the X cube (cubeAxis) of the vector
     * retrieve the X axis values for y=0 (first index in y values)
     * and z=0 (first index in z values)
     *
     * @param cubeAxis   axis of the cube
     * @param fixedAxis1 first fixed axis
     * @param index1
     * @param fixedAxis2
     * @param index2
     * @return
     */
    public Vector getVector(Axis cubeAxis, Axis fixedAxis1, int index1, Axis fixedAxis2, int index2) {
        return getComponent(cubeAxis).getVector(cubeAxis, fixedAxis1, index1, fixedAxis2, index2);
    }

    public Vector getVector(Axis cubeAxis) {
        return getComponent(cubeAxis).getVector(cubeAxis);
    }




    @Override
    public ComponentDimension getComponentDimension() {
        return componentDimension;
    }

    @Override
    public Expr getComponent(int row, int col) {
        if (col == 0) {
            return values[row];
        }
        throw new IllegalArgumentException("Invalid");
    }

    @Override
    public String getComponentTitle(int row, int col) {
        if (col == 0) {
            return Axis.values()[row].toString();
        }
        throw new IllegalArgumentException("Invalid");
    }

    @Override
    public Domain getDomainImpl() {
        return domain;
    }

    @Override
    public boolean isInvariantImpl(Axis axis) {
        return false;
    }



//    @Override
//    public boolean isDDx() {
//        return false;
//    }

//    @Override
//    public IDDx toDDx() {
//        throw new IllegalArgumentException("Unsupported");
//    }


    @Override
    public Expr setParam(String name, Expr value) {
        return this;
    }

    @Override
    public List<Expr> getSubExpressions() {
        return (List) Arrays.asList(values);
    }



    public Complex integrate(Domain domain, PlaneAxis axis, double fixedValue) {
        ComponentDimension dims = getComponentDimension();
        MutableComplex c=new MutableComplex();
        for (int i = 0; i < componentDimension.rows; i++) {
            c.add(getComponent(Axis.values()[i]).integrate(domain,axis,fixedValue));
        }
        return c.toComplex();
    }

    public Complex integrate(Domain domain) {
        ComponentDimension dims = getComponentDimension();
        MutableComplex c=new MutableComplex();
        for (int i = 0; i < componentDimension.rows; i++) {
            c.add(getComponent(Axis.values()[i]).integrate(domain));
        }
        return c.toComplex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VDiscrete)) return false;
        if (!super.equals(o)) return false;

        VDiscrete vDiscrete = (VDiscrete) o;

        if (componentDimension != null ? !componentDimension.equals(vDiscrete.componentDimension) : vDiscrete.componentDimension!= null) return false;
        if (domain != null ? !domain.equals(vDiscrete.domain) : vDiscrete.domain != null) return false;
        if (!Arrays.equals(values, vDiscrete.values)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (values != null ? Arrays.hashCode(values) : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (componentDimension != null ? componentDimension.hashCode() : 0);
        return result;
    }

    public static VDiscrete discretize(Expr expr, int xSamples, int ySamples, int zSamples) {
        if (expr.isScalarExpr()) {
            return new VDiscrete(Discrete.discretize(expr,xSamples,ySamples,zSamples));
        } else {
            DoubleToVector v = expr.toDV();
            ComponentDimension d = v.getComponentDimension();
            if (d.columns == 1) {
                if (d.rows == 1) {
                    return new VDiscrete(
                            Discrete.discretize(v.getComponent(Axis.X), xSamples, ySamples, zSamples)
                    );

                } else if (d.rows == 2) {
                    return new VDiscrete(
                            Discrete.discretize(v.getComponent(Axis.X), xSamples, ySamples, zSamples),
                            Discrete.discretize(v.getComponent(Axis.Y), xSamples, ySamples, zSamples)
                    );
                } else if (d.rows == 3) {
                    return new VDiscrete(
                            Discrete.discretize(v.getComponent(Axis.X), xSamples, ySamples, zSamples),
                            Discrete.discretize(v.getComponent(Axis.Y), xSamples, ySamples, zSamples),
                            Discrete.discretize(v.getComponent(Axis.Z), xSamples, ySamples, zSamples)
                    );
                }
            }
            throw new UnsupportedComponentDimensionException(d);
        }
    }

    public static VDiscrete discretize(Expr expr, Domain domain,Samples samples) {
        if (expr.isScalarExpr()) {
            return new VDiscrete(Discrete.discretize(expr,domain,samples));
        } else {
            DoubleToVector v = expr.toDV();
            ComponentDimension d = v.getComponentDimension();
            if (d.columns == 1) {
                if (d.rows == 1) {
                    return new VDiscrete(
                            Discrete.discretize(v.getComponent(Axis.X), domain,samples)
                    );

                } else if (d.rows == 2) {
                    return new VDiscrete(
                            Discrete.discretize(v.getComponent(Axis.X), domain,samples),
                            Discrete.discretize(v.getComponent(Axis.Y), domain,samples)
                    );
                } else if (d.rows == 3) {
                    return new VDiscrete(
                            Discrete.discretize(v.getComponent(Axis.X), domain,samples),
                            Discrete.discretize(v.getComponent(Axis.Y), domain,samples),
                            Discrete.discretize(v.getComponent(Axis.Z), domain,samples)
                    );
                }
            }
            throw new UnsupportedComponentDimensionException(d);
        }
    }

    @Override
    public int getComponentSize() {
        return values.length;
    }

    @Override
    public double getDistance(Normalizable other) {
        if(other instanceof VDiscrete) {
            VDiscrete o = (VDiscrete) other;
            return (this.sub(o)).norm() / o.norm();
        }else{
            Normalizable o = other;
            return (this.norm()-o.norm()) / o.norm();
        }
    }
}
