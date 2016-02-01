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
package net.sf.jclal.util.distancefunction;

/**
 * Entropy Distance.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class EntropicDistance extends AbstractHammingDistance {

	private static final long serialVersionUID = 3573909035274980729L;

	/**
	 * Empty(default) constructor.
	 */
	public EntropicDistance() {
		super();

	}

	/**
	 * Compute the entropy distance among i1 and i2
	 *
	 * @param i1
	 *            The array of boolean
	 * @param i2
	 *            The array of boolean
	 * @return The distance among the vectors
	 */
	@Override
	public double distance(boolean[] i1, boolean[] i2) {

		if (i1.length != i2.length) {
			System.err.println("Different lenghts");
			System.exit(1);
		}

		contingencyTable(i1, i2);

		double q = a + b + c + d;

		if (i1.length != q) {
			System.err.println("Diferentes distancias");
			System.exit(1);
		}

		double div;

		// Se calcula la joint entropy
		double h12 = 0;

		double h121 = 0;

		if ((b + c) != 0) {

			div = (b + c) / q;

			h121 += div * log2(div);

		}

		if ((a + d) != 0) {

			div = (a + d) / q;

			h121 += div * log2(div);
		}

		h121 = -h121;

		double h122 = 0;

		if ((b + c) != 0) {

			if (b != 0) {

				div = b / (b + c);

				h122 += div * log2(div);

			}

			if (c != 0) {

				div = c / (b + c);
				h122 += div * log2(div);
			}
		}

		h122 = -h122;
		h122 *= (b + c) / q;

		double h123 = 0;

		if ((a + d) != 0) {

			if (a != 0) {
				div = a / (a + d);
				h123 += div * log2(div);
			}

			if (d != 0) {
				div = d / (a + d);
				h123 += div * log2(div);
			}

		}

		h123 = -h123;
		h123 *= (a + d) / q;

		h12 = h121 + h122 + h123;

		// se calculan las entropias de H1 y H2
		double h1 = 0;

		if ((a + b) != 0) {

			div = (a + b) / q;

			h1 += div * log2(div);
		}

		if ((c + d) != 0) {

			div = (c + d) / q;

			h1 += div * log2(div);
		}

		h1 = -h1;

		double h2 = 0;

		if ((a + c) != 0) {

			div = (a + c) / q;
			h2 += div * log2(div);
		}

		if ((b + d) != 0) {

			div = (b + d) / q;
			h2 += div * log2(div);
		}

		h2 = -h2;

		// normalized distance
		double result = ((2 * h12 - h1 - h2) / h12);

		if (Double.isNaN(result) || Double.isInfinite(result)) {
			result = 0;
		}

		if (result < 0) {
			System.err.println("The entropy distance is less than 0");
			System.exit(1);
		}

		return result;

	}

	/**
	 * Compute the logarithmic based 2
	 *
	 * @param num
	 *            The number.
	 * @return The log in base 2.
	 */
	public double log2(double num) {
		return Math.log(num) / Math.log(2);
	}
	
	public static void main(String args[]){
		
		boolean v1[]=new boolean []{false,false,false,false,true,false};
		boolean v2[]=new boolean []{true,false,true,true,false,false};
		boolean p[]= new boolean []{true,true,true,true,false,true};
		
		System.out.println(new EntropicDistance().distance(v1, p));
		System.out.println(new EntropicDistance().distance(v2, p));
		
		System.out.println(new EntropicDistance().distance(v1, v2));
	}	
}
