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
package net.sf.jclal.experiment;

import java.util.ArrayList;

/**
 * Class that executes an experiment like a thread.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Pérez Perdomo
 */
public class ExperimentThread extends Thread {

	/**
	 * The experiment executed.
	 */
	private final Experiment experiment;
	/**
	 * The configuration file.
	 */
	private final ArrayList<String> jobFile;

	/**
	 * Constructor
	 * 
	 * @param experiment
	 *            The experiment to by executed.
	 * @param jobFile
	 *            The configuration elements.
	 */
	public ExperimentThread(Experiment experiment, ArrayList<String> jobFile) {
		this.experiment = experiment;
		this.jobFile = jobFile;
	}

	/**
	 * Execute the experiment.
	 */
	@Override
	public void run() {
		executeExperiment();
	}

	/**
	 * Get the experiment executed.
	 * 
	 * @return the experiment.
	 */
	public Experiment getExperiment() {
		return experiment;
	}

	/**
	 * Execute the experiment.
	 */
	public void executeExperiment() {
		for (String current : jobFile) {
			System.out.println("Algorithm started");
			experiment.executeJob(current);
			System.out.println("Algorithm finished");
		}
	}
}
