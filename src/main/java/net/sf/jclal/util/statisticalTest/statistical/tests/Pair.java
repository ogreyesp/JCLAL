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

public class Pair implements Comparable {

  public double indice; //first element
  public double valor;  //second element
  
  /**
  * Default builder
  */
  public Pair() {

  }//end-method
  
  /**
  * Builder
  *
  * @param i First double
  * @param v Second double
  */
  public Pair(double i, double v) {
    indice = i;
    valor = v;
  }//end-method

  /**
  * CompareTo method
  *
  * @param o1 pair
  * @return A integer representing the order
  */
  public int compareTo (Object o1) { //sort by value

	    if (this.valor > ((Pair)o1).valor)
	      return -1;
	    else if (this.valor < ((Pair)o1).valor)
	      return 1;
	    else return 0;
  }//end-method


}//end-class
