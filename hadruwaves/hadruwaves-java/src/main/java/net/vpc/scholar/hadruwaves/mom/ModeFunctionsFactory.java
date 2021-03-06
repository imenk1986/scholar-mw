/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.hadruwaves.mom;

import net.vpc.scholar.hadrumaths.AbstractFactory;
import net.vpc.scholar.hadrumaths.Axis;
import net.vpc.scholar.hadruwaves.mom.modes.DefaultBoxModeFunctions;
import net.vpc.scholar.hadruwaves.Wall;
import net.vpc.scholar.hadruwaves.WallBorders;

/**
 *
 * @author vpc
 */
public class ModeFunctionsFactory extends AbstractFactory{

    private ModeFunctionsFactory() {
    }

    public static ModeFunctions createBox(String wallBorders) {
        return new DefaultBoxModeFunctions(wallBorders);
    }

    public static ModeFunctions createBox(WallBorders wallBorders) {
        return new DefaultBoxModeFunctions(wallBorders);
    }

    public static ModeFunctions createBox(Wall north, Wall east, Wall south, Wall west) {
        return new DefaultBoxModeFunctions(north, east, south, west);
    }

    public static ModeFunctions createBox(Wall north, Wall east, Wall south, Wall west, Axis polarization) {
        return new DefaultBoxModeFunctions(north, east, south, west, polarization);
    }

    public static ModeFunctions createBox(Wall north, Wall east, Wall south, Wall west, Axis polarization, double phasex, double phasey) {
        return new DefaultBoxModeFunctions(north, east, south, west, polarization, phasex, phasey);
    }
}
