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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.core.IClassifier;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.core.IQueryStrategy;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.util.distancefunction.DistanceContainer;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;

/**
 * Implementation of Density Diversity query strategy.
 *
 * The similarity values are efficiently pre-computed.
 *
 * B. Settles and M. Craven. An analysis of active learning strategies for
 * sequence labeling tasks. In Proceedings of the Conference on Empirical
 * Methods in Natural Language Processing (EMNLP), pages 1069–1078. ACL Press,
 * 2008.
 *
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public class DensityDiversityQueryStrategy extends AbstractSingleLabelQueryStrategy {

	private static final long serialVersionUID = -7119231401618234126L;

	/**
	 * It stores the distance among instances.
	 */
	private DistanceContainer distanceValues;

	/**
	 * Importance of density.
	 */
	private double relativeImportanceOfDensity = 1;

	/**
	 * Pointer to the subQuery strategy used.
	 */
	private IQueryStrategy subQueryStrategy;

	/**
	 * Type of distance used.
	 */
	private NormalizableDistance typeOfDistance;

	// It stores whether the matrix used is stored over a file or the main
	// memory
	private boolean matrixOverFile = false;

	/**
	 * Empty(default) constructor.
	 */
	public DensityDiversityQueryStrategy() {
		super();
		this.setMaximal(false);
	}

	/**
	 *
	 * @param typeOfDistance
	 *            Type of distance used
	 */
	public DensityDiversityQueryStrategy(NormalizableDistance typeOfDistance) {

		super();
		this.setMaximal(false);
		this.typeOfDistance = typeOfDistance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void training() {
		subQueryStrategy.training();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testModel() {
		subQueryStrategy.testModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTestData(IDataset testData) {
		subQueryStrategy.setTestData(testData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset getTestData() {
		return subQueryStrategy.getTestData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] distributionForInstance(Instance instance) {
		return subQueryStrategy.distributionForInstance(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractEvaluation> getEvaluations() {
		return subQueryStrategy.getEvaluations();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setEvaluations(List<AbstractEvaluation> evaluations) {
		subQueryStrategy.setEvaluations(evaluations);
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public IClassifier getClassifier() {
		return subQueryStrategy.getClassifier();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClassifier(IClassifier classifier) {
		subQueryStrategy.setClassifier(classifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset getUnlabelledData() {
		return subQueryStrategy.getUnlabelledData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUnlabelledData(IDataset unlabelledData) {
		subQueryStrategy.setUnlabelledData(unlabelledData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset getLabelledData() {
		return subQueryStrategy.getLabelledData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLabelledData(IDataset labelledData) {
		subQueryStrategy.setLabelledData(labelledData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateLabeledData() {
		if (distanceValues != null) {
			distanceValues.updateIndexes(getSelectedInstances());
		}
		subQueryStrategy.updateLabeledData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Integer> getSelectedInstances() {
		return ((AbstractSingleLabelQueryStrategy) subQueryStrategy).getSelectedInstances();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedInstances(ArrayList<Integer> selectedInstances) {
		((AbstractSingleLabelQueryStrategy) this.getSubQueryStrategy()).setSelectedInstances(selectedInstances);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] testUnlabeledData() {
		/**
		 * Compute the distance just once in each active learning iteration the
		 * distance among the instances of the unlabeled set are calculated
		 */
		Instances unlabelled = subQueryStrategy.getUnlabelledData().getDataset();
		
		if (distanceValues == null) {
			typeOfDistance.setInstances(unlabelled);
			try {
				distanceValues = new DistanceContainer(unlabelled, typeOfDistance, matrixOverFile);
			} catch (Exception ex) {
				Logger.getLogger(DensityDiversityQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return super.testUnlabeledData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		Instances unlabelled = subQueryStrategy.getUnlabelledData().getDataset();

		int sizeUnlabeledData = unlabelled.numInstances();

		// Step1
		double step1;

		// standard values
		step1 = subQueryStrategy.utilityInstance(instance);

		// If the utility is equal to 0 then the rest of the process is
		// unnecessary
		if (step1 == 0) {
			return 0;
		}

		if (subQueryStrategy.isMaximal()) {

			// To convert the value
			step1 = 1 / step1;

		}

		// Step 2
		int indexOfInstance = unlabelled.indexOf(instance);

		double step2 = distanceValues.getAcumulativeValue(indexOfInstance);

		// step2 is divided by U=numInstancesUnlabeled
		step2 /= sizeUnlabeledData;

		// step2 is powered to B(relative importance of the density)
		step2 = Math.pow(step2, getRelativeImportanceOfDensity());

		// Step 3
		double step3 = step1 * step2;

		return step3;
	}

	/**
	 * Set the sub-query strategy to use
	 * 
	 * @param subQueryStrategy
	 *            The sub-Query Strategy used in Density Diversity.
	 */
	public void setSubQueryStrategy(IQueryStrategy subQueryStrategy) {
		this.subQueryStrategy = subQueryStrategy;
	}

	/**
	 * Get the sub-query strategy used
	 *
	 * @return The sub-Query Strategy. Used in Density Diversity.
	 */
	public IQueryStrategy getSubQueryStrategy() {
		return subQueryStrategy;
	}

	/**
	 * Set the relative importance of density
	 *
	 * @param relativeImportanceOfDensity
	 *            The relative importance of density.
	 */
	public void setRelativeImportanceOfDensity(double relativeImportanceOfDensity) {
		this.relativeImportanceOfDensity = relativeImportanceOfDensity;
	}

	/**
	 * Get the relative importance of density
	 *
	 * @return The relative importance of density.
	 */
	public double getRelativeImportanceOfDensity() {
		return relativeImportanceOfDensity;
	}

	/**
	 * Get the distance function used
	 *
	 * @return The type of similarity.
	 */
	public NormalizableDistance getTypeOfDistance() {
		return typeOfDistance;
	}

	/**
	 * Set the distance function to use
	 *
	 * @param typeOfDistance
	 *            The type of similarity. Used in Density Diversity.
	 */
	public void setTypeOfDistance(NormalizableDistance typeOfDistance) {
		this.typeOfDistance = typeOfDistance;
	}

	/**
	 *
	 * @param configuration
	 *            Configuration object for density diversity strategy.
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li><b>importance-density= double</b></li>
	 *            <li><b>distance-function type= class</b>
	 *            <p>
	 *            Package: net.sf.jclal.util.distancefunction
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            <p>
	 *            Package: weka.core
	 *            </p>
	 *            <p>
	 *            Class: EuclideanDistance || ManhattanDistance ||
	 *            MinkowskiDistance...
	 *            </p>
	 *            </li>
	 *            <li>matrix-file= boolean</li>
	 *            <li><b>sub-query-strategy type= class</b>
	 *            <p>
	 *            Package: net.sf.jclal.activelearning.querystrategy
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		try {
			super.configure(configuration);
		} catch (Exception e) {
		}

		// Set relativeImportanceOfDensity
		double currentImportance = configuration.getDouble("importance-density", relativeImportanceOfDensity);

		setRelativeImportanceOfDensity(currentImportance);

		String distanceError = "distance-function type= ";
		try {

			// Set the distance classname
			String distanceClassname = configuration.getString("distance-function[@type]");
			distanceError += distanceClassname;

			// the distance class
			Class<? extends NormalizableDistance> distance = (Class<? extends NormalizableDistance>) Class
					.forName(distanceClassname);

			// the distance instance
			NormalizableDistance currentDistance = distance.newInstance();

			// Configure the distance
			if (currentDistance instanceof IConfigure) {
				((IConfigure) currentDistance).configure(configuration.subset("distance-function"));
			}

			// Set the distance
			setTypeOfDistance(currentDistance);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal distance classname: " + distanceError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Illegal distance classname: " + distanceError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Illegal distance classname: " + distanceError, e);
		}

		// Set the sub query strategy
		String subError = "sub-query-strategy type= ";
		try {
			// sub Query strategy classname
			String strategyClassname = configuration.getString("sub-query-strategy[@type]");
			subError += strategyClassname;
			// sub Query strategy class
			Class<? extends IQueryStrategy> strategyClass = (Class<? extends IQueryStrategy>) Class
					.forName(strategyClassname);
			// sub Query strategy instance
			IQueryStrategy currentSubStrategy = strategyClass.newInstance();

			// Configure sub Query strategy (if necessary)
			if (currentSubStrategy instanceof IConfigure) {
				((IConfigure) currentSubStrategy).configure(configuration.subset("sub-query-strategy"));
			}
			// Set the sub Query strategy
			setSubQueryStrategy(currentSubStrategy);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal sub-query-strategy classname: " + subError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Illegal sub-query-strategy classname: " + subError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Illegal sub-query-strategy classname: " + subError, e);
		}

		// Set if handle the matrix over a file and not over the main memory
		boolean matrixFile = configuration.getBoolean("matrix-file", matrixOverFile);

		setMatrixOverFile(matrixFile);
	}

	/**
	 * Set if the matrix used is stored over a file or the main memory.
	 * 
	 * @param matrixFile
	 *            Flag which indicates whether the matrix is stored over a file
	 *            or main memory
	 */
	public void setMatrixOverFile(boolean matrixFile) {
		matrixOverFile = matrixFile;
	}

	/**
	 * Get whether the matrix is used over a file or the main memory.
	 *
	 * @return Whether the mtrix is stored over a file or not
	 */
	public boolean isMatrixOverFile() {
		return matrixOverFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void algorithmFinished() {
		super.algorithmFinished();

		if (distanceValues != null) {
			distanceValues.destroy();
		}
	}

}
