package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Domain;
import net.vpc.scholar.hadrumaths.*;
import net.vpc.scholar.hadrumaths.ComponentDimension;
import net.vpc.scholar.hadrumaths.Expressions;
import net.vpc.scholar.hadrumaths.Matrix;
import net.vpc.scholar.hadrumaths.Out;

/**
 * Created by vpc on 8/24/14.
 */
public abstract class AbstractDoubleToMatrix extends AbstractExprPropertyAware implements DoubleToMatrix {
    @Override
    public boolean isDMImpl() {
        return true;
    }

    @Override
    public DoubleToMatrix toDM() {
        return this;
    }

    @Override
    public boolean isDCImpl() {
        return
                getComponentDimension().equals(ComponentDimension.SCALAR)
                        && getComponent(0, 0).isDC();
    }


    @Override
    public Matrix computeMatrix(double x) {
        return computeMatrix(new double[]{x}, (Domain) null, null)[0];
    }

//    @Override
//    public Matrix computeMatrix(double x, double y) {
//        return computeMatrix(new double[]{x}, new double[]{y}, (Domain) null, null)[0][0];
//    }
//
//    @Override
//    public Matrix computeMatrix(double x, double y, double z) {
//        return computeMatrix(new double[]{x}, new double[]{y}, new double[]{z}, (Domain) null, null)[0][0][0];
//    }

    @Override
    public Matrix[] computeMatrix(double[] x) {
        return computeMatrix(x, (Domain) null, null);
    }

    @Override
    public Matrix[][] computeMatrix(double[] x, double[] y) {
        return computeMatrix(x, y, (Domain) null, null);
    }

    @Override
    public Matrix[][][] computeMatrix(double[] x, double[] y, double[] z) {
        return computeMatrix(x, y, z, (Domain) null, null);
    }

    @Override
    public boolean isDDImpl() {
        return isDC() && toDC().isDD();
    }

    @Override
    public Matrix[] computeMatrix(double[] x, double y, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, x, y, d0, ranges);
    }

    @Override
    public Matrix[] computeMatrix(double x, double[] y, Domain d0, Out<Range> ranges) {
        return Expressions.computeMatrix(this, x, y, d0, ranges);
    }

    public Matrix[][] computeMatrix(double[] x, double[] y, Domain d0) {
        return computeMatrix(x, y, d0, null);
    }

    @Override
    public DoubleToDouble toDD() {
        if (isDC()) {
            DoubleToComplex c = toDC();
            if (c.isDD()) {
                return c.toDD();
            }
        }
        throw new UnsupportedOperationException("Not supported toDD");
    }


    ///////////////////////////////////////////////


    @Override
    public boolean isDVImpl() {
        ComponentDimension d = getComponentDimension();
        return d.columns == 1 && d.rows <= 3;
    }

    @Override
    public DoubleToVector toDV() {
        if (!isDV()) {
            throw new IllegalArgumentException("Not a DV");
        }
        ComponentDimension d = getComponentDimension();
        if (d.columns == 1 && d.rows <= 3) {
            switch (d.rows) {
                case 1: {
                    return DefaultDoubleToVector.create(getComponent(0, 0).toDC());
                }
                case 2: {
                    return DefaultDoubleToVector.create(getComponent(0, 0).toDC(), getComponent(1, 0).toDC());
                }
                case 3: {
                    return DefaultDoubleToVector.create(getComponent(0, 0).toDC(), getComponent(1, 0).toDC(), getComponent(2, 0).toDC());
                }
            }
        }
        throw new IllegalArgumentException("Not a DV");
    }

    @Override
    public DoubleToComplex toDC() {
        if (isDC()) {
            return getComponent(0, 0).toDC();
        }
        throw new IllegalArgumentException("Unsupported toDC");
    }

    @Override
    public Matrix computeMatrix(double x, double y) {
        ComponentDimension componentDimension = getComponentDimension();
        Complex[][] values = new Complex[componentDimension.rows][componentDimension.columns];
        for (int[] i : componentDimension.iterate()) {
            int r = i[0];
            int c = i[1];
            values[r][c] = getComponent(r, c).toDC().computeComplex(x, y);
        }
        return Maths.matrix(values);
    }

    @Override
    public Matrix computeMatrix(double x, double y, double z) {
        ComponentDimension componentDimension = getComponentDimension();
        Complex[][] values = new Complex[componentDimension.rows][componentDimension.columns];
        for (int[] ii : componentDimension.iterate()) {
            int r = ii[0];
            int c = ii[1];
            values[r][c] = getComponent(r, c).toDC().computeComplex(x, y, z);
        }
        return Maths.matrix(values);
    }

    @Override
    public Matrix[][][] computeMatrix(double[] x, double[] y, double[] z, Domain d0, Out<Range> ranges) {
        ComponentDimension componentDimension = getComponentDimension();
        Complex[][][][][] values = new Complex[componentDimension.rows][componentDimension.columns][][][];
        Range rangeOut = Range.forBounds(0, x.length - 1, 0, y.length - 1, 0, z.length - 1);
        Out<Range> rangeOutHolder = new Out<Range>();
        for (int[] ii : componentDimension.iterate()) {
            rangeOutHolder.set(null);
            int r = ii[0];
            int c = ii[1];
            values[r][c] = getComponent(r, c).toDC().computeComplex(x, y, z, d0, rangeOutHolder);
            if (rangeOut != null) {
                if (rangeOutHolder.get() == null) {
                    rangeOut = rangeOut.intersect(rangeOutHolder.get());
                    if (rangeOut != null && rangeOut.isEmpty()) {
                        rangeOut = null;
                    }
                }
            }
        }
        Matrix[][][] ret = new Matrix[z.length][y.length][x.length];
        for (int t = 0; t < z.length; t++) {
            for (int i = 0; i < y.length; i++) {
                for (int j = 0; j < x.length; j++) {
                    Complex[][] values2 = new Complex[componentDimension.rows][componentDimension.columns];
                    for (int rr = 0; rr < componentDimension.rows; rr++) {
                        for (int cc = 0; cc < componentDimension.columns; cc++) {
                            values2[rr][cc] = values[rr][cc][t][i][j];
                        }
                    }
                    ret[t][i][j] = Maths.matrix(values2);
                }
            }
        }
        ranges.set(rangeOut);
        return ret;
    }

    @Override
    public Matrix[] computeMatrix(double[] x, Domain d0, Out<Range> ranges) {
        ComponentDimension componentDimension = getComponentDimension();
        Complex[][][] values = new Complex[componentDimension.rows][componentDimension.columns][];
        Range rangeOut = Range.forBounds(0, x.length - 1);
        Out<Range> rangeOutHolder = new Out<Range>();
        for (int[] ii : componentDimension.iterate()) {
            rangeOutHolder.set(null);
            int r = ii[0];
            int c = ii[1];
            values[r][c] = getComponent(r, c).toDC().computeComplex(x, d0, rangeOutHolder);
            if (rangeOut != null) {
                if (rangeOutHolder.get() == null) {
                    rangeOut = rangeOut.intersect(rangeOutHolder.get());
                    if (rangeOut != null && rangeOut.isEmpty()) {
                        rangeOut = null;
                    }
                }
            }
        }
        Matrix[] ret = new Matrix[x.length];
        for (int j = 0; j < x.length; j++) {
            Complex[][] values2 = new Complex[componentDimension.rows][componentDimension.columns];
            for (int rr = 0; rr < componentDimension.rows; rr++) {
                for (int cc = 0; cc < componentDimension.columns; cc++) {
                    values2[rr][cc] = values[rr][cc][j];
                }
            }
            ret[j] = Maths.matrix(values2);
        }
        ranges.set(rangeOut);
        return ret;
    }

    public boolean isNaNImpl() {
        ComponentDimension componentDimension = getComponentDimension();
        for (int[] i : componentDimension.iterate()) {
            if (getComponent(i[0], i[1]).isNaN()) {
                return true;
            }
        }
        return false;
    }

    public boolean isInfiniteImpl() {
        ComponentDimension componentDimension = getComponentDimension();
        for (int[] i : componentDimension.iterate()) {
            if (getComponent(i[0], i[1]).isInfinite()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isZeroImpl() {
        ComponentDimension componentDimension = getComponentDimension();
        for (int[] i : componentDimension.iterate()) {
            if (!getComponent(i[0], i[1]).isZero()) {
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean isDoubleExprImpl() {
        ComponentDimension componentDimension = getComponentDimension();
        for (int[] i : componentDimension.iterate()) {
            if (!getComponent(i[0], i[1]).isDoubleExpr()) {
                return true;
            }
        }
        return true;
    }

    public Matrix[][] computeMatrix(double[] x, double[] y, Domain d0, Out<Range> ranges) {
        ComponentDimension componentDimension = getComponentDimension();
        Complex[][][][] values = new Complex[componentDimension.rows][componentDimension.columns][][];
        Range rangeOut = Range.forBounds(0, x.length - 1, 0, y.length - 1);
        Out<Range> rangeOutHolder = new Out<Range>();
        for (int[] ii : componentDimension.iterate()) {
            rangeOutHolder.set(null);
            int r = ii[0];
            int c = ii[1];
            values[r][c] = getComponent(r, c).toDC().computeComplex(x, y, d0, rangeOutHolder);
            if (rangeOut != null) {
                if (rangeOutHolder.get() == null) {
                    rangeOut = rangeOut.intersect(rangeOutHolder.get());
                    if (rangeOut != null && rangeOut.isEmpty()) {
                        rangeOut = null;
                    }
                }
            }
        }
        Matrix[][] ret = new Matrix[y.length][x.length];
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < x.length; j++) {
                Complex[][] values2 = new Complex[componentDimension.rows][componentDimension.columns];
                for (int rr = 0; rr < componentDimension.rows; rr++) {
                    for (int cc = 0; cc < componentDimension.columns; cc++) {
                        values2[rr][cc] = values[rr][cc][i][j];
                    }
                }
                ret[i][j] = Maths.matrix(values2);
            }
        }
        ranges.set(rangeOut);
        return ret;
    }


}
