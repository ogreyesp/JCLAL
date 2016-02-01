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
package net.sf.jclal.examples;

import net.sf.jclal.classifier.BinaryRelevanceUpdateable;
import net.sf.jclal.classifier.MOAWrapper;
import moa.classifiers.functions.SGD;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;

public class UpdatableClassifier {

	public static void main(String[] args) {
        try {
            MultiLabelInstances train = new MultiLabelInstances("datasets/enron/enron-train.arff", "datasets/enron/enron.xml");
            MultiLabelInstances test = new MultiLabelInstances("datasets/enron/enron-test.arff", "datasets/enron/enron.xml");

            Evaluator eval = new Evaluator();
            Evaluation results;
            
            long time = System.currentTimeMillis();
            
            SGD sgd= new SGD();
            
            sgd.setLossFunction(0);
            
            MOAWrapper moawrapper = new MOAWrapper(sgd);
            
            BinaryRelevanceUpdateable br = new BinaryRelevanceUpdateable(moawrapper);
            
            br.build(train);
            results = eval.evaluate(br, test);
            System.out.println(results);
            
            System.out.println("Time:"+ (System.currentTimeMillis()-time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}