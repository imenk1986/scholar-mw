/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.hadrumaths.transform.navigaterules;

import net.vpc.scholar.hadrumaths.Axis;
import net.vpc.scholar.hadrumaths.Expr;
import net.vpc.scholar.hadrumaths.Maths;
import net.vpc.scholar.hadrumaths.symbolic.Any;
import net.vpc.scholar.hadrumaths.symbolic.DoubleToVector;
import net.vpc.scholar.hadrumaths.symbolic.DefaultDoubleToVector;
import net.vpc.scholar.hadrumaths.transform.ExpressionRewriter;
import net.vpc.scholar.hadrumaths.transform.ExpressionRewriterRule;
import net.vpc.scholar.hadrumaths.transform.RewriteResult;

/**
 *
 * @author vpc
 */
public class VDCxyNavRule implements ExpressionRewriterRule {

    public static final ExpressionRewriterRule INSTANCE = new VDCxyNavRule();
    public static final Class<? extends Expr>[] TYPES = new Class[]{DefaultDoubleToVector.class};

    @Override
    public Class<? extends Expr>[] getTypes() {
        return TYPES;
    }

    public RewriteResult rewrite(Expr e, ExpressionRewriter ruleset) {
        DoubleToVector ee = (DoubleToVector) e;
        int length = ee.getComponentDimension().rows;
        Expr[] updated = new Expr[length];
        boolean changed = false;
        int bestEfforts = 0;
        for (int i = 0; i < updated.length; i++) {
            Expr s1 = ee.getComponent(Axis.values()[i]);
            RewriteResult s2 = ruleset.rewrite(s1);
            if (s2.isRewritten()) {
                changed = true;
                if(s2.isBestEffort()) {
                    bestEfforts ++;
                }
                updated[i] = s2.getValue();
            }else{
                bestEfforts ++;
                updated[i] = s1;
            }
        }
        if (changed) {
            Expr e2=null;
            switch (ee.getComponentDimension().rows){
                case 1:{
                    e2 = Maths.vector(updated[0].toDC());
                    break;
                }
                case 2:{
                    e2 = Maths.vector(updated[0].toDC(), updated[1].toDC());
                    break;
                }
                case 3:{
                    e2 = Maths.vector(updated[0].toDC(), updated[1].toDC(), updated[2].toDC());
                    break;
                }
                default:{
                    throw new IllegalArgumentException("Invalid dim");
                }
            }
            e2= Any.copyProperties(e, e2);
            if(bestEfforts==length) {
                return RewriteResult.bestEffort(e2);
            }
            return RewriteResult.newVal(e2);
        }
        return RewriteResult.unmodified(e);
    }
    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null || !obj.getClass().equals(getClass())){
            return false;
        }
        return true;
    }

}
