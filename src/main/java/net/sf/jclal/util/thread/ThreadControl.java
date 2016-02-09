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
package net.sf.jclal.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to control the execution of threads.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ThreadControl {

	/**
	 * The executor.
	 */
	private ExecutorService executor;

	/**
	 * Quantity of processors
	 */
	private int defaultCores = 1;

	/**
	 * Default empty constructor.
	 */
	public ThreadControl() {
	}

	/**
	 * Constructor
	 * 
	 * Init the class with a number of threads.
	 *
	 * @param numberOfCores
	 *            The number of threads.
	 */
	public ThreadControl(int numberOfCores) {
		init(numberOfCores);
	}

	/**
	 * Constructor
	 *
	 * @param systemProperty
	 *            String representing a property assigned with the method
	 *            'System.setProperty()', this will be retrieved with
	 *            'System.getProperty()', the returned value have to represent a
	 *            integer number or 'all' indicating employ all the cores.
	 */
	public ThreadControl(String systemProperty) {
		String value = System.getProperty(systemProperty);

		if (value == null || value.isEmpty()) {
			init(1);
			return;
		}

		int c = 1;
		if (value.equalsIgnoreCase("all")) {
			c = Runtime.getRuntime().availableProcessors();
		} else {
			try {
				c = Integer.parseInt(value);
			} catch (NumberFormatException ex) {
				java.util.logging.Logger.getLogger(ThreadControl.class.getName()).log(Level.WARNING,
						"\nThe property << " + systemProperty + " >> " + "was not set correctly. "
								+ "Default value of << 1 >> was assigned.\n",
						ex);
			}
		}

		init(c);
	}

	/**
	 * Init the parallel executor.
	 *
	 * @param numberOfCores
	 *            The number of threads to use.
	 */
	public void init(int numberOfCores) {
		defaultCores = numberOfCores;
		init();
	}

	/**
	 * Init with default cores.
	 */
	public void init() {
		executor = Executors.newFixedThreadPool(defaultCores);
	}

	/**
	 * Execute a task.
	 *
	 * @param task
	 *            A new task.
	 */
	public void execute(Runnable task) {
		executor.execute(task);
	}

	/**
	 * Waits for the current's threads execution, no more thread will be
	 * accepted when this method is call.
	 */
	public void end() {
		if (executor != null) {
			executor.shutdown();
			while (!executor.isTerminated()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
					Logger.getLogger(ThreadControl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			executor.shutdownNow();
			executor = null;
		}
	}

	public int getDefaultCores() {
		return defaultCores;
	}

	public void setDefaultCores(int defaultCores) {
		this.defaultCores = defaultCores;
	}

    /**
     * Takes like default system property "cores-per-processor".
     *
     * @param isParallelContext
     * @return
     */
    public static ThreadControl defaultThreadControl(boolean isParallelContext) {
        return defaultThreadControl(isParallelContext, "cores-per-processor");
}

    /**
     *
     * @param isParallelContext
     * @return
     */
    public static ThreadControl defaultThreadControl(boolean isParallelContext,
            String systemProperty) {

        return isParallelContext
                ? new ThreadControl(systemProperty) : new ThreadControl(1);
    }
}
