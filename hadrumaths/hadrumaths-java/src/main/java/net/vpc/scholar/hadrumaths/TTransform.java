package net.vpc.scholar.hadrumaths;

/**
 * Created by vpc on 8/1/14.
 */
public interface TTransform<T,R> {
    R transform(int index, T e);
}
