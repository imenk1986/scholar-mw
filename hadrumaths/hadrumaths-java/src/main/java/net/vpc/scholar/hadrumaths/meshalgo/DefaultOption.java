package net.vpc.scholar.hadrumaths.meshalgo;

import net.vpc.scholar.hadrumaths.geom.Triangle;
import net.vpc.scholar.hadrumaths.util.dump.Dumper;

import java.util.List;

public class DefaultOption implements MeshOptions{
    private static final long serialVersionUID = -1010101010101001021L;
    protected EnhancedMeshPolygons enhancedMeshZone;

    public boolean isMeshAllowed(List<Triangle> t, int iteration) {
        return false;
    }

    public Triangle selectMeshTriangle(List<Triangle> t, int iteration) {
        return null;
    }

    public void setZone(EnhancedMeshPolygons zone) {
        enhancedMeshZone = zone;
    }

    public final String dump() {
      return getDumpStringHelper().toString();
    }

    public Dumper getDumpStringHelper() {
        Dumper h=new Dumper(getClass().getSimpleName());
        h.add("enhancedMeshZone",enhancedMeshZone);
        return h;
    }

}
