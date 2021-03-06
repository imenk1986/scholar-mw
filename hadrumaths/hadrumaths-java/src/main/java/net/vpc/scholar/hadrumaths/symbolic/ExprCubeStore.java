package net.vpc.scholar.hadrumaths.symbolic;

import net.vpc.scholar.hadrumaths.Expr;

/**
 * Created by vpc on 2/14/15.
 */
public interface ExprCubeStore {
    public int getColumns();

    public int getRows();

    public int getHeight();

    public Expr get(int row, int col, int h);

    public void set(Expr exp, int row, int col, int h);
}
