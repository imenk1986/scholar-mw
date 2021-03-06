package net.vpc.scholar.hadrumaths;

/**
 * Created by vpc on 3/23/17.
 */
public class TMatrixFromColumnVector<T> extends AbstractTMatrix<T> {
    private TVector<T> vector;
    private boolean row;

    public TMatrixFromColumnVector(TVector<T> vector) {
        this.vector = vector;
        if(!vector.isColumn()){
           throw new IllegalArgumentException("Expected Column Vector");
        }
    }

    @Override
    public T get(int row, int col) {
        return vector.get(row);
    }

    @Override
    public void set(int row, int col, T val) {
        vector.set(row,val);
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return vector.size();
    }

    @Override
    protected TMatrixFactory<T> createDefaultFactory() {
        return Maths.Config.getDefaultMatrixFactory(vector.getComponentType());
    }

    @Override
    public boolean isComplex() {
        return vector.isComplex();
    }

    @Override
    public Complex toComplex() {
        return vector.toComplex();
    }

    @Override
    public void resize(int rows, int columns) {
        throw new IllegalArgumentException("Unsupported Resize");
    }

    @Override
    public TypeReference<T> getComponentType() {
        return vector.getComponentType();
    }

    @Override
    public VectorSpace<T> getComponentVectorSpace() {
        return vector.getComponentVectorSpace();
    }
}
