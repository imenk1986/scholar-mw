/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.hadrumaths;

/**
 * @author vpc
 */
public abstract class Samples {



    public static AbsoluteSamples absolute(double[] x, double[] y, double[] z) {
        return new AbsoluteSamples(true, x, y, z);
    }

    public static AbsoluteSamples absolute(double[] x, double[] y) {
        return new AbsoluteSamples(true, x, y);
    }

    public static AbsoluteSamples absolute(double[] x) {
        return new AbsoluteSamples(true, x);
    }

    public static RelativeSamples relative(double[] x, double[] y, double[] z) {
        return new RelativeSamples(false, x, y, z);
    }

    public static RelativeSamples relative(double[] x, double[] y) {
        return new RelativeSamples(false, x, y);
    }

    public static RelativeSamples relative(double[] x) {
        return new RelativeSamples(false, x);
    }

    public static RelativeSamples relative(int x, int y, int z) {
        return new RelativeSamples(false, Maths.dtimes(0, 1, x), Maths.dtimes(0, 1, y), Maths.dtimes(0, 1, z));
    }

    public static RelativeSamples relative(int x, int y) {
        return new RelativeSamples(false, Maths.dtimes(0, 1, x), Maths.dtimes(0, 1, y));
    }

    public static RelativeSamples relative(int x) {
        return new RelativeSamples(false, Maths.dtimes(0, 1, x));
    }

    public abstract int getDimension();


    public static AdaptiveSamples adaptive(){
        return new AdaptiveSamples();
    }

    public static AdaptiveSamples adaptive(int min,int max){
        return new AdaptiveSamples().setMinimumXSamples(min).setMaximumXSamples(max);
    }

    public static AbsoluteSamples toAbsoluteSamples(Samples samples,Domain domain){
        if(samples instanceof RelativeSamples){
            if(domain==null) {
                throw new IllegalArgumentException("Missing Domain to evaluate Relative Samples");
            }
            return ((RelativeSamples) samples).toAbsolute(domain);
        }
        return (AbsoluteSamples) samples;
    }
}
