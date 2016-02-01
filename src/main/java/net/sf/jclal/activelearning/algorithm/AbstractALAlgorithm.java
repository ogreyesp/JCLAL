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
package net.sf.jclal.activelearning.algorithm;

import java.util.ArrayList;
import java.util.List;
import net.sf.jclal.core.AlgorithmEvent;
import net.sf.jclal.core.IAlgorithm;
import net.sf.jclal.core.IAlgorithmListener;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IRandGen;
import net.sf.jclal.core.ISystem;
import net.sf.jclal.core.ITool;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import weka.core.SerializedObject;

/**
 * Abstract implementation of the classic active learning process.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
@SuppressWarnings("serial")
public abstract class AbstractALAlgorithm implements IAlgorithm, IConfigure, ITool {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Algorithm states
	/////////////////////////////////////////////////////////////////
	/**
	 * State new
	 */
	protected static final int NEW = 0;
	/**
	 * State ready
	 */
	protected static final int READY = 1;
	/**
	 * State running
	 */
	protected static final int RUNNING = 2;
	/**
	 * State finished
	 */
	protected static final int FINISHED = 3;
	/**
	 * State terminated
	 */
	protected static final int TERMINATED = 4;
	/**
	 * Current algorithm state
	 */
	protected int state = NEW;
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Internal variables
	/////////////////////////////////////////////////////////////////
	/**
	 * Registered listeners collection
	 */
	protected List<IAlgorithmListener> listeners = new ArrayList<IAlgorithmListener>();

	/**
	 *
	 * @return Returns the listeners used in the algorithm.
	 */
	@Override
	public List<IAlgorithmListener> getListeners() {
		return listeners;
	}

	/**
	 * Random generator used
	 */
	private IRandGen randgen;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	/**
	 * Empty (default) constructor
	 *
	 */
	public AbstractALAlgorithm() {

		super();

	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
	// IAlgorithm interface
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addListener(IAlgorithmListener listener) {
		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean removeListener(IAlgorithmListener listener) {
		return listeners.remove(listener);
	}

	// Execution methods
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pause() {
		state = READY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void terminate() {
		state = TERMINATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		do {
			switch (state) {
			case (NEW): {
				// Change current state
				state = RUNNING;
				// Call doInit() method
				doInit();
				// Fire algorithm started event
				fireAlgorithmStarted();
				// Finish this switch
				break;
			}
			case (READY): {
				// Change current state
				state = RUNNING;
				// Finish this switch
				break;
			}
			case (RUNNING): {
				// Perform an iteration
				doIterate();
				// Fire Iteration completed event
				if (state == RUNNING) {
					fireIterationCompleted();
				}
				// Finish this switch
				break;
			}
			}
		} while (state == RUNNING);
		// If algorithm has finished...
		if (state == FINISHED) {
			// Fire algorithm terminated event
			fireAlgorithmFinished();
			// Change current state
			state = NEW;
			// Finish this switch
			return;
		}
		// If algorithm was terminated...
		if (state == TERMINATED) {
			// Fire algorithm terminated event
			fireAlgorithmTerminated();
			// Change current state
			state = NEW;
			// Finish this switch
			return;
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////
	// Algorithm execution
	/**
	 * Perform algorithm initialization.
	 */
	public abstract void doInit();

	/**
	 * Perform an algorithm iteration.
	 */
	protected abstract void doIterate();

	/**
	 * It warns when it started the algorithm
	 */
	public final void fireAlgorithmStarted() {

		AlgorithmEvent event = new AlgorithmEvent(this);

		for (IAlgorithmListener listener : listeners) {
			listener.algorithmStarted(event);
		}
	}

	/**
	 * It warns when it completed the iteration
	 */
	public final void fireIterationCompleted() {

		AlgorithmEvent event = new AlgorithmEvent(this);

		for (IAlgorithmListener listener : listeners) {
			listener.iterationCompleted(event);
		}
	}

	/**
	 * It warns when it finished the algorithm
	 */
	public final void fireAlgorithmFinished() {

		AlgorithmEvent event = new AlgorithmEvent(this);

		for (IAlgorithmListener listener : listeners) {
			listener.algorithmFinished(event);
		}

		getScenario().algorithmFinished();
	}

	/**
	 * It warns when it terminated the algorithm
	 */
	public final void fireAlgorithmTerminated() {
		AlgorithmEvent event = new AlgorithmEvent(this);

		for (IAlgorithmListener listener : listeners) {
			listener.algorithmTerminated(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAlgorithm makeCopy() throws Exception {

		return (IAlgorithm) new SerializedObject(this).getObject();
	}

	/////////////////////////////////////////////////////////
	/**
	 * @param configuration
	 *            The configuration object
	 * 
	 *            The XML labels supported are:
	 * 
	 *            <ul>
	 *            <li><b>listener type = class:</b>
	 *            <p>
	 *            Adds the specified listener to receive algorithm events from
	 *            this algorithm.
	 *            </p>
	 *            <p>
	 *            Package: net.sf.jclal.listener
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {
		// Number of defined listeners
		int numberOfListeners = configuration.getList("listener[@type]").size();
		// For each listener in list
		String listenerError;
		for (int i = 0; i < numberOfListeners; i++) {
			String header = "listener(" + i + ")";
			listenerError = "listener type= ";
			try {
				// Listener classname
				String listenerClassname = configuration.getString(header + "[@type]");
				listenerError += listenerClassname;
				// Listener class
				Class<? extends IAlgorithmListener> listenerClass = (Class<? extends IAlgorithmListener>) Class
						.forName(listenerClassname);
				// Listener instance
				IAlgorithmListener listener = listenerClass.newInstance();
				// Configure listener (if necessary)
				if (listener instanceof IConfigure) {
					((IConfigure) listener).configure(configuration.subset(header));
				}
				// Add this listener to the algorithm
				addListener(listener);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationRuntimeException("\nIllegal listener classname: " + listenerError, e);
			} catch (InstantiationException e) {
				throw new ConfigurationRuntimeException("\nIllegal listener classname: " + listenerError, e);
			} catch (IllegalAccessException e) {
				throw new ConfigurationRuntimeException("\nIllegal listener classname: " + listenerError, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextualize(ISystem context) {
		// Attach a random generator to this object
		this.setRandgen(context.createRandGen());

	}

	/**
	 * Return the random number generator
	 * 
	 * @return The random number generator used
	 */
	public IRandGen getRandgen() {
		return randgen;
	}

	/**
	 * Set the random number generator
	 * 
	 * @param randgen
	 *            The random numbers generator to use
	 */
	public void setRandgen(IRandGen randgen) {
		this.randgen = randgen;
	}
}
