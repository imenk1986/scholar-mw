package net.vpc.scholar.hadruwaves.mom.console.params.hints;

import net.vpc.scholar.hadrumaths.AbstractParam;
import net.vpc.scholar.hadruwaves.mom.MomStructure;
import net.vpc.scholar.hadruwaves.mom.str.zsfractalmodel.MomStructureFractalZop;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 14 oct. 2006 12:13:57
 */
public class HintSubModelEquivalentParam extends AbstractParam implements Cloneable{
    public static final String NAME="HintSubModelEquivalent";

    public HintSubModelEquivalentParam() {
        super(NAME);
    }

    @Override
    public void configure(Object source,Object value) {
        ((MomStructure) source).setParameterNotNull(MomStructureFractalZop.HINT_SUB_MODEL_EQUIVALENT,value);
    }
}
