package net.vpc.scholar.hadrumaths.format.params;

import net.vpc.scholar.hadrumaths.format.FormatParam;

/**
 * Created by IntelliJ IDEA. User: vpc Date: 23 juil. 2005 Time: 11:21:16 To
 * change this template use File | Settings | File Templates.
 */
public class ProductFormat implements FormatParam {

    private String op;

    public ProductFormat(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

}
