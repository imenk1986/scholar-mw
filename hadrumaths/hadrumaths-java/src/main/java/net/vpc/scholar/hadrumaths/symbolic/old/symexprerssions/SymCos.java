package net.vpc.scholar.hadrumaths.symbolic.old.symexprerssions;

import net.vpc.scholar.hadrumaths.symbolic.old.SymAbstractFct1Param;
import net.vpc.scholar.hadrumaths.symbolic.old.SymExpression;
import net.vpc.scholar.hadrumaths.symbolic.old.symop.SymOpNeg;
import net.vpc.scholar.hadrumaths.Complex;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 18 juil. 2007 21:50:31
 */
public class SymCos extends SymAbstractFct1Param {
    public SymCos(SymExpression value) {
        super("cos",value);
    }

    protected Complex eval(Complex value) {
        return value.cos();
    }

    protected SymExpression diff() {
        return new SymOpNeg(new SymSin(getValue()));
    }

}