package net.vpc.scholar.hadrumaths.plot.console.params;

import net.vpc.scholar.hadrumaths.Maths;
import net.vpc.scholar.hadrumaths.Param;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 17 juil. 2005 10:23:32
 */
public class DoubleArrayParamSet extends ParamSet implements Cloneable {
    private double[] values;
    private int index = -1;

    public DoubleArrayParamSet(Param configurator) {
        this(configurator, new double[0]);
    }

    public DoubleArrayParamSet(Param configurator, double value) {
        this(configurator, new double[]{value});
    }

    public DoubleArrayParamSet(Param configurator, double min, double max, int times) {
        this(configurator, Maths.dtimes(min, max, times));
    }

    public DoubleArrayParamSet(Param configurator, double min, double max, double step) {
        this(configurator, Maths.dsteps(min, max, step));
    }

    public DoubleArrayParamSet init(double value) {
        this.values = new double[]{value};
        return this;
    }

    public DoubleArrayParamSet dtimes(double min, double max, int times) {
        values = Maths.dtimes(min, max, times);
        return this;
    }

    public DoubleArrayParamSet dsteps(double min, double max, double step) {
        values = Maths.dsteps(min, max, step);
        return this;
    }

    public DoubleArrayParamSet(Param configurator, double[] values) {
        super(configurator);
        this.values = values;
    }

    protected Double getValueImpl() {
        return values[index == -1 ? 0 : index];
    }

    protected Double getValueImpl(int index) {
        return values[index == -1 ? 0 : index];
    }

    protected boolean hasNextImpl() {
        return (index + 1) < values.length;
    }

    protected int getSizeImpl() {
        return values.length;
    }

    protected void resetImpl() {
        index = -1;
    }

    protected Double nextImpl() {
        index++;
        Double v = getValueImpl();
        return v;
    }

    public double[] getValues() {
        return values;
    }

    public Double getValue() {
        return (Double) super.getValue();
    }
}
