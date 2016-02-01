/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sf.jclal.activelearning.singlelabel.querystrategy;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.classifier.AbstractClassifier;
import net.sf.jclal.classifier.WekaComitteClassifier;
import weka.core.Instance;

/**
 * Implementation of Query By Committee strategy, variant: Vote entropy.
 *
 * The vote entropy approach is used for measuring the level of disagreement.
 *
 * I. Dagan and S. Engelson. Committee-based sampling for training probabilistic
 * classifiers. In Proceedings of the International Conference on Machine
 * Learning (ICML), pages 150–157. Morgan Kaufmann, 1995.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public class VoteEntropyQueryStrategy extends QueryByCommittee {

    private static final long serialVersionUID = 1512361350121382024L;

    /**
     * {@inheritDoc}
     */
    @Override
    public double utilityInstance(Instance instance) {

        try {

            int committeeSize = ((AbstractClassifier) getClassifier()).getNumberClassifiers();

            int[] votes = ((WekaComitteClassifier) getClassifier()).countVotesForEachClass(instance);

            double division;

            double sumatoria = 0;

            for (int vote : votes) {

                division = vote / (double) committeeSize;

                if (vote != 0) {
                    division *= Math.log(division);
                }

                sumatoria += division;
            }

            return -sumatoria;

        } catch (Exception ex) {

            Logger.getLogger(VoteEntropyQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);

        }
        return 0;

    }
}
