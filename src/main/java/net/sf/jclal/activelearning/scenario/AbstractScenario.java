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
package net.sf.jclal.activelearning.scenario;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.core.IBatchMode;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IOracle;
import net.sf.jclal.core.IQueryStrategy;
import net.sf.jclal.core.IScenario;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Abstract class for active learning scenario. Every AL scenario must extend
 * this class.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class AbstractScenario implements IScenario, IConfigure {

	private static final long serialVersionUID = -7380708971776324455L;
	/**
	 * The query strategy used.
	 */
	protected IQueryStrategy queryStrategy;

	/**
	 * The batch mode strategy used, it allows to define which of the analyzed
	 * instances will be selected, e.g. the k best instances.
	 */
	protected IBatchMode batchMode;

	/**
	 * The oracle used, it allows to define how the chosen instances will be
	 * labeled.
	 */
	protected IOracle oracle;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IOracle getOracle() {
		return oracle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOracle(IOracle oracle) {
		this.oracle = oracle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBatchMode getBatchMode() {
		return batchMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBatchMode(IBatchMode batchMode) {
		this.batchMode = batchMode;
	}

	/**
	 * Empty Constructor.
	 */
	public AbstractScenario() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQueryStrategy getQueryStrategy() {
		return queryStrategy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQueryStrategy(IQueryStrategy queryStrategy) {
		this.queryStrategy = queryStrategy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateLabelledData() {
		getQueryStrategy().updateLabeledData();
	}

	/**
	 *
	 * @param configuration
	 *            The configuration of Abstract Scenario.
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li><b>batch-mode type= class</b>
	 *            <p>
	 *            Package: net.sf.jclal.activelearning.batchmode
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            <li><b>query-strategy type= class</b>
	 *            <p>
	 *            Package: net.sf.jclal.activelearning.querystrategy
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            <li><b>oracle type= class</b>
	 *            <p>
	 *            Package: net.sf.jclal.activelearning.oracle
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		setBatchModeConfiguration(configuration);
		setQueryStrategyConfiguration(configuration);
		setOracleConfiguration(configuration);

	}

	/**
	 * Configuration of the batch mode object
	 *
	 * @param configuration
	 *            The configuration object to use
	 */
	public void setBatchModeConfiguration(Configuration configuration) {

		String batchError = "batch-mode type= ";
		try {
			// Set batch
			String batchModeClassName = configuration.getString("batch-mode[@type]", "");
			batchError += batchModeClassName;

			// batch-mode class
			Class<? extends IBatchMode> batchModeClass = (Class<? extends IBatchMode>) Class
					.forName(batchModeClassName);

			IBatchMode batchModeT = batchModeClass.newInstance();

			// Configure query strategy (if necessary)
			if (batchModeT instanceof IConfigure) {
				((IConfigure) batchModeT).configure(configuration.subset("batch-mode"));
			}

			setBatchMode(batchModeT);

		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("\nIllegal batch-mode classname: " + batchError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("\nIllegal batch-mode classname: " + batchError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("\nIllegal batch-mode classname: " + batchError, e);
		}

	}

	/**
	 * Configuration of the Query Strategy.
	 *
	 * @param configuration
	 *            The configuration object to use
	 */
	public void setQueryStrategyConfiguration(Configuration configuration) {

		String queryError = "query-strategy type= ";
		try {
			// query strategy
			// query strategy classname
			String queryStrategyClassname = configuration.getString("query-strategy[@type]");
			queryError += queryStrategyClassname;
			// query strategy class
			Class<? extends IQueryStrategy> queryStrategyClass = (Class<? extends IQueryStrategy>) Class
					.forName(queryStrategyClassname);
			// query strategy instance
			IQueryStrategy queryStrategyTemp = queryStrategyClass.newInstance();
			// Configure query strategy (if necessary)
			if (queryStrategyTemp instanceof IConfigure) {
				((IConfigure) queryStrategyTemp).configure(configuration.subset("query-strategy"));
			}
			// Add this query strategy to the scenario
			setQueryStrategy(queryStrategyTemp);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("\nIllegal query strategy classname: " + queryError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("\nIllegal query strategy classname: " + queryError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("\nIllegal query strategy classname: " + queryError, e);
		}

	}

	/**
	 * Configuration of the Oracle.
	 *
	 * @param configuration
	 *            The configuration object to use
	 */
	public void setOracleConfiguration(Configuration configuration) {

		String oracleError = "oracle type= ";
		try {
			// oracle classname
			String oracleClassname = configuration.getString("oracle[@type]");

			oracleError += oracleClassname;

			// oracle class
			Class<? extends IOracle> oracleClass = (Class<? extends IOracle>) Class.forName(oracleClassname);

			// oracle instance
			IOracle oracle = oracleClass.newInstance();

			// Configure query strategy (if necessary)
			if (oracle instanceof IConfigure) {
				((IConfigure) oracle).configure(configuration.subset("oracle"));
			}

			// Add this oracle to the scenario
			setOracle(oracle);

		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("\nIllegal oracle classname: " + oracleError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("\nIllegal oracle classname: " + oracleError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("\nIllegal oracle classname: " + oracleError, e);
		}

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void labelInstances() {

		getOracle().labelInstances(getQueryStrategy());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void training() {
		try {

			getQueryStrategy().training();

		} catch (Exception ex) {
			Logger.getLogger(AbstractScenario.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void evaluationTest() {

		try {

			getQueryStrategy().testModel();

		} catch (Exception ex) {
			Logger.getLogger(AbstractScenario.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void algorithmFinished() {
		getQueryStrategy().algorithmFinished();
	}

}
