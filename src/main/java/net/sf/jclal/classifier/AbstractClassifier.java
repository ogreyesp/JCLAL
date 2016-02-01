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
package net.sf.jclal.classifier;

import java.io.Serializable;

import org.apache.commons.configuration.Configuration;

import net.sf.jclal.core.IClassifier;
import net.sf.jclal.core.IConfigure;
import weka.core.SerializedObject;

/**
 * Abstract class used by the classifiers.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class AbstractClassifier implements IClassifier, Serializable, IConfigure {

	private static final long serialVersionUID = 5403153500223979136L;
	/**
	 * Stores the number of classifiers.
	 */
	private int numberClassifiers = 1;

	/**
	 * Set if the internal process can be paralyzed.
	 */
	private boolean parallel = false;

	/**
	 * Get the number of classifier used.
	 * 
	 * @return The number of classifiers used.
	 */
	public int getNumberClassifiers() {
		return numberClassifiers;
	}

	/**
	 * Set the number of classifier
	 * 
	 * @param numberClassifiers
	 *            The number of classifiers used.
	 */
	public void setNumberClassifiers(int numberClassifiers) {
		this.numberClassifiers = numberClassifiers;
	}

	/**
	 * Return true if the internal process is parallelized.
	 *
	 * @return True if the internal process is parallelized, false otherwise
	 */
	public boolean isParallel() {
		return parallel;
	}

	/**
	 * Set if the internal process will be parallelized.
	 *
	 * @param parallel
	 *            If the internal process will be parallelized.
	 */
	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	/**
	 * Copy a classifier.
	 *
	 * @return a copy of the object
	 * @throws Exception
	 *             The exception that will be launched.
	 */
	@Override
	public IClassifier makeCopy() throws Exception {
		return (IClassifier) new SerializedObject(this).getObject();
	}

	@Override
	public void configure(Configuration settings) {
		boolean parallel = settings.getBoolean("parallel", isParallel());
		setParallel(parallel);
	}
}