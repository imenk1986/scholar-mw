/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.vpc.scholar.hadrumaths.format.impl;

import net.vpc.scholar.hadrumaths.Matrix;
import net.vpc.scholar.hadrumaths.Complex;
import net.vpc.scholar.hadrumaths.format.FormatParam;
import net.vpc.scholar.hadrumaths.format.Formatter;

/**
 *
 * @author vpc
 */
public class MatrixFormatter implements Formatter<Matrix>{
    public MatrixFormatter() {
    }

    @Override
    public String format(Matrix o, FormatParam... format) {
        Complex[][] elements=o.getArray();
        int[] colsWidth = new int[o.getColumnCount()];
        for (Complex[] element : elements) {
            for (int j = 0; j < element.length; j++) {
                int len = String.valueOf(element[j]).length();
                if (len > colsWidth[j]) {
                    colsWidth[j] = len;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        String lineSep = System.getProperty("line.separator");
        sb.append("[");
        for (int i = 0; i < elements.length; i++) {
            if (i > 0 || elements.length > 1) {
                sb.append(lineSep);
            }
            for (int j = 0; j < elements[i].length; j++) {
                StringBuilder sbl = new StringBuilder(colsWidth[j]);
                //sbl.clear();
                if (j > 0) {
                    sbl.append(' ');
                }
                String disp = String.valueOf(elements[i][j]);
                sbl.append(disp);
                int x = colsWidth[j] - disp.length();
                while (x > 0) {
                    sbl.append(' ');
                    x--;
                }
                //sbl.append(' ');
                sb.append(sbl.toString());
            }
        }
        if (elements.length > 1) {
            sb.append(lineSep);
        }
        sb.append("]");
        return sb.toString();
    }
    
}
