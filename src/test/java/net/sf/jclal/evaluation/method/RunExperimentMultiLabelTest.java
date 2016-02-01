/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jclal.evaluation.method;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.jclal.experiment.RunExperiment;

/**
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class RunExperimentMultiLabelTest {

	public RunExperimentMultiLabelTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of evaluate method, of class HoldOut.
	 */
	@Test
	public void testEvaluate() {

		System.out.println("evaluate");

		String args[] = new String[] { "-d", "examples/MultiLabel" };

		try {
			RunExperiment.main(args);
		} catch (Exception e) {
			Logger.getLogger(RunExperimentSingleLabelTest.class.getName()).log(Level.SEVERE, null, e);
		}
	}

}