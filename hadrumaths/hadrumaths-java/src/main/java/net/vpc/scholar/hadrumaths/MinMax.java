package net.vpc.scholar.hadrumaths;

public class MinMax {
    private double min=Double.NaN;
    private double max=Double.NaN;

    public MinMax() {
    }

    public void registerAbsValues(double[] d){
        for (double aD : d) {
            registerAbsValue(aD);
        }
    }

    public void registerValues(double[] d){
        for (double aD : d) {
            registerValue(aD);
        }
    }

    public void registerValues(double[][] d){
        for (double[] aD : d) {
            for (double anAD : aD) {
                registerValue(anAD);
            }
        }
    }

    public void registerValues(double[][][] d){
        for (double[][] z : d) {
            for (double[] y : z) {
                for (double x : y) {
                    registerValue(x);
                }
            }
        }
    }

    public void registerAbsValues(double[][] d){
        for (double[] aD : d) {
            for (double anAD : aD) {
                registerAbsValue(anAD);
            }
        }
    }

    public void registerValue(double d){
        if(Double.isNaN(min) || (!Double.isNaN(d) && d < min)){
            min=d;
        }
        if(Double.isNaN(max) || (!Double.isNaN(d) && d > max)){
            max=d;
        }
    }

    public void registerAbsValue(double d){
        registerValue(Math.abs(d));
    }

    public float getRatio(double d){
        if(Double.isNaN(d)){
            return Float.NaN;
        }
        if(min==max){
            return 0f;
        }
        return (float)((d-min)/(max-min));
    }


    public double getMin() {
        return min;
    }

    public double getLength() {
        return max-min;
    }

    public double getMax() {
        return max;
    }
    public boolean isNaN(){
        return Double.isNaN(min) ||Double.isNaN(max);
    }
}
