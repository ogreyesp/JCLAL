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
 * Abstract class for query by committee family of query strategies.
 *
 * The QBC approach has a committee of models which are all trained on the
 * current labeled set, but represent competing hypotheses.
 *
 * H.S. Seung, M. Opper, and H. Sompolinsky. Query by committee. In Proceed-
 * ings of the ACMWorkshop on Computational Learning Theory, pages 287–294,
 * 1992.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class QueryByCommittee extends AbstractSingleLabelQueryStrategy {

    private static final long serialVersionUID = -686806225060607999L;
}