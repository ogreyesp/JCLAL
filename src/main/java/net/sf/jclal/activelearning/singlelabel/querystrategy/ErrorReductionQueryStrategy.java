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
 * Abstract class for Error Reduction family of query strategies. All the query
 * strategies that belong to ErrorReduction category must be extend this class.
 *
 * This category of query strategy measures how much its generalization error is
 * likely to be reduced.
 *
 * Burr Settles. Active Learning Literature Survey. Computer Sciences Technical
 * Report 1648, University ofWisconsin–Madison. 2009.
 *
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 *
 */
public abstract class ErrorReductionQueryStrategy extends AbstractSingleLabelQueryStrategy {

    private static final long serialVersionUID = -1290150080123319178L;

    /**
     * Empty(default) constructor.
     */
    public ErrorReductionQueryStrategy() {

        //By default this type of query strategy is minimalistic
        setMaximal(false);
    }
}