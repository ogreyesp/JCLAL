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
package net.sf.jclal.core;

/**
 * Algorithm listener.
 *
 * @author Sebastian Ventura
 */
public interface IAlgorithmListener {

	/**
	 * This event is fired when the algorithm has completed normally.
	 *
	 * @param event
	 *            An event containing a reference to the source algorithm.
	 */
	public void algorithmStarted(AlgorithmEvent event);

	/**
	 * This event is fired when an iteration is terminated.
	 *
	 * @param event
	 *            An event containing a reference to the source algorithm.
	 */
	public void iterationCompleted(AlgorithmEvent event);

	/**
	 * This event is fired when the algorithm has completed normally.
	 *
	 * @param event
	 *            An event containing a reference to the source algorithm.
	 */
	public void algorithmFinished(AlgorithmEvent event);

	/**
	 * This event is fired when the algorithm is terminated abnormally.
	 *
	 * @param e
	 *            an event containing a reference to the source algorithm.
	 */
	public void algorithmTerminated(AlgorithmEvent e);
}
