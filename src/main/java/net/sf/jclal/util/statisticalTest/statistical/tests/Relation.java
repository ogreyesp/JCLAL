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

package net.sf.jclal.util.statisticalTest.statistical.tests;

public class Relation {

  public int i; //first element
  public int j; //second element

  /**
  * Default builder
  */
  public Relation() {

  }//end-method

  /**
  * Builder
  *
  * @param x First integer
  * @param y Second integer
  */
  public Relation(int x, int y) {
    i = x;
    j = y;
  }//end-method
  
  /**
  * To string method
  *
  * @return A string representing the Relation
  */
  public String toString() {
	  return "("+i+","+j+")";	  
  }//end-method

}//end-class
