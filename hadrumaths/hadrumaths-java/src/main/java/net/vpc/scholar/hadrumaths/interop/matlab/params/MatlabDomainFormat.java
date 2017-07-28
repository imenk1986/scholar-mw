package net.vpc.scholar.hadrumaths.interop.matlab.params;

import net.vpc.scholar.hadrumaths.interop.matlab.*;

/**
 * Created by IntelliJ IDEA.
 * User: vpc
 * Date: 23 juil. 2005
 * Time: 11:21:16
 * To change this template use File | Settings | File Templates.
 */
public class MatlabDomainFormat implements ToMatlabStringParam{
    public static enum Type{NONE,GATE};
    private Type type;

    public MatlabDomainFormat(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
