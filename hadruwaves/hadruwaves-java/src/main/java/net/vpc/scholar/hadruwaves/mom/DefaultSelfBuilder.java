package net.vpc.scholar.hadruwaves.mom;

import net.vpc.scholar.hadrumaths.Complex;
import net.vpc.scholar.hadrumaths.Maths;
import net.vpc.scholar.hadrumaths.Matrix;
import net.vpc.scholar.hadruwaves.str.MWStructure;
import net.vpc.scholar.hadruwaves.builders.AbstractSelfBuilder;

import static net.vpc.scholar.hadrumaths.Complex.I;
import static net.vpc.scholar.hadruwaves.Physics.omega;

/**
 * @author taha.bensalah@gmail.com on 7/17/16.
 */
class DefaultSelfBuilder extends AbstractSelfBuilder {

    public DefaultSelfBuilder(MWStructure momStructure) {
        super(momStructure);
    }

    public Matrix computeMatrixImpl() {
        MomStructure momStructure = (MomStructure) getStructure();
        Matrix z = momStructure.inputImpedance().monitor(getMonitor()).computeMatrix();
        Complex[][] cc = z.getArrayCopy();
        double o = omega(momStructure.getFrequency());
        for (int i = 0; i < cc.length; i++) {
            Complex[] complexes = cc[i];
            for (int j = 0; j < complexes.length; j++) {
                Complex zz = complexes[j];
                cc[i][j] = zz.div(I.mul(o));
            }
        }
        return Maths.matrix(cc);
    }
}
