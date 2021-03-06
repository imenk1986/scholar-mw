package net.vpc.scholar.hadrumaths;

import net.vpc.scholar.hadrumaths.symbolic.Range;

/**
 * Created by vpc on 4/7/17.
 */
public interface BooleanArray2 {
    boolean get(int i, int j);

    BooleanArray1 get(int i);

    void set(int i, int j, boolean value);

    void set(int i, int j);

    void copyFrom(BooleanArray2 other,Range r0);
    void clear(int i, int j);

    BooleanArray2 copy();

    int size1();

    int size2();
}
