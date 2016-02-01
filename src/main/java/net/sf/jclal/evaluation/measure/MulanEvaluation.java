/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    MulanEvaluation.java
 *    
 */
package net.sf.jclal.evaluation.measure;

import java.util.List;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.measure.Measure;

/**
 * Class that represents an evaluation of MULAN.
 * 
 * @author Oscar Gabriel Reyes Pupo
 */
public class MulanEvaluation extends mulan.evaluation.Evaluation{

    public MulanEvaluation(List<Measure> someMeasures, MultiLabelInstances data) throws Exception {
        super(someMeasures, data);
        
    }
    
    public MulanEvaluation(mulan.evaluation.Evaluation eval, MultiLabelInstances data) throws Exception {
        
        super(eval.getMeasures(), data);
        
    }

    /**
     * Returns a string with the results of the evaluation
     * 
     * @return a string with the results of the evaluation
     */
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        for (Measure m : getMeasures()) {
            sb.append(m);
            sb.append("\n");
        }
        return sb.toString();
    }

}