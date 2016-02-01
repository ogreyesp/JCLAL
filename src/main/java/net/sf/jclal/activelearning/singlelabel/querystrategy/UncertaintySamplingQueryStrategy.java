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

/**
 * Abstract class for Uncertainty Sampling family of query strategies.
 *
 * The uncertainty sampling family of query strategies is considered the
 * simplest and commonly used query strategy. The instances about the active
 * learner is least certain are labeled.
 *
 * D. Lewis and W. Gale. A sequential algorithm for training text classifiers.
 * In Proceedings of the ACM SIGIR Conference on Research and Development in
 * Information Retrieval, pages 3–12. ACM/Springer, 1994.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class UncertaintySamplingQueryStrategy extends AbstractSingleLabelQueryStrategy {

    private static final long serialVersionUID = -775271101825003928L;
}