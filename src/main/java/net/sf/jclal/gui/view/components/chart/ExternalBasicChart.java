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
package net.sf.jclal.gui.view.components.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.util.dataset.LoadDataFromReporterFile;
import net.sf.jclal.util.file.FileUtil;
import net.sf.jclal.util.learningcurve.LearningCurveUtility;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

/**
 * Visual Component to visualize the learning curves
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ExternalBasicChart extends JFrame {

	private static final long serialVersionUID = 7079334419440144035L;

	private XYSeriesCollection series;
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private JPanel content;
	private JComboBox<String> comboBox;
	private String windowsTitle;
	private List<List<AbstractEvaluation>> evaluationsCollection;
	private String chartTitle;
	private String xTitle;
	private ArrayList<String> queryNames;
	private JMenuBar menubar;
	private JSlider slider;
	private int reportFrecuency;
	private int max;
	// *************************
	private LearningCurvesVisualTable curveOptions;
	private HashMap<String, Color> controlCurveColor;
	private Set<Integer> set;
	private Set<String> setSeries;
	private Object[][] data;
	private Color colors[];
	// ****************************

	private boolean viewPointsForm = false;
	private boolean viewWithOutColor = false;
	private boolean viewdefaultsLineTypes = false;
	private boolean viewSpline = false;

	// Current reports files
	private ArrayList<File> reportsFiles = new ArrayList<File>();
	private ArrayList<File> historyFiles = new ArrayList<File>();

	/**
	 * 
	 * Constructor
	 * 
	 * @param windowsTitleParam
	 *            The title of the window.
	 * @param chartTitleParam
	 *            The title of the chart panel.
	 * @param xTitleParam
	 *            The X-axis label.
	 */
	public ExternalBasicChart(String windowsTitleParam, String chartTitleParam, String xTitleParam) {

		reportFrecuency = 1;

		menubar = new JMenuBar();
		menubar.add(createMenu());
		max = -1;

		windowsTitle = windowsTitleParam;
		chartTitle = chartTitleParam;
		xTitle = xTitleParam;

		queryNames = new ArrayList<String>();
		evaluationsCollection = new ArrayList<List<AbstractEvaluation>>();
		controlCurveColor = new HashMap<String, Color>();
		set = new HashSet<Integer>();
		setSeries = new HashSet<String>();

		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(640, 480));
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(true);

		JMenuItem menuiten = (JMenuItem) chartPanel.getPopupMenu().getComponent(3);
		JMenuItem eps = new JMenuItem("EPS");

		final JFileChooser chooserEPS = new JFileChooser();
		chooserEPS.setFileFilter(new FileNameExtensionFilter("EPS Files", "eps"));

		eps.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (chart == null) {
					JOptionPane.showMessageDialog(null, "No graphic to export.");
					return;
				}

				String path0;
				File file;

				int returnVal = chooserEPS.showSaveDialog(null);

				// Cancel
				if (returnVal == JFileChooser.CANCEL_OPTION) {
					return;
				}

				// OK
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					path0 = chooserEPS.getSelectedFile().getAbsolutePath();
					// System.out.println(path0);
					try {
						if (!path0.endsWith("eps") && !path0.endsWith("EPS")) {
							path0 = path0.concat(".eps");
						}
						file = new File(path0);

						// File exists - overwrite ?
						if (file.exists()) {
							Object[] options = { "OK", "CANCEL" };
							int answer = JOptionPane.showOptionDialog(null, "File exists - Overwrite ?", "Warning",
									JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
							if (answer != JOptionPane.YES_OPTION) {
								return;
							}
							// System.out.println(answer+"");
						}

						// Export file
						export(file, chart, chartPanel.getWidth(), chartPanel.getHeight());
					} catch (Exception f) {
						// I/O - Error
					}
				}
			}

		});

		menuiten.add(eps);

		// pop.add("EPS");
		slider = new JSlider(JSlider.HORIZONTAL);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setToolTipText("Changes the report frecuency");
		slider.setSnapToTicks(true);
		slider.setMinimum(1);
		slider.setMinorTickSpacing(1);

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {

				reportFrecuency = ((JSlider) evt.getSource()).getValue();

				slider.setToolTipText(String.valueOf(reportFrecuency));

				jComboBoxItemStateChanged();
			}
		});

		comboBox = new JComboBox<String>();

		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {

				jComboBoxItemStateChanged();
			}
		});

		content = new JPanel(new BorderLayout());
		content.add(comboBox, BorderLayout.NORTH);
		content.add(chartPanel, BorderLayout.CENTER);
		content.add(slider, BorderLayout.SOUTH);

		setJMenuBar(menubar);
		setTitle(windowsTitle);
		setContentPane(this.content);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Export jfreechart to image format. File must have an extension like jpg,
	 * png, pdf, svg or eps. Otherwise export will fail.
	 *
	 * @param file
	 *            Destination The file
	 * @param chart
	 *            JFreechart the chart object
	 * 
	 * @param width
	 *            The width of the image
	 * @param height
	 *            The height of the image
	 * 
	 * @throws Exception
	 *             An exception
	 */
	public static void export(File file, JFreeChart chart, int width, int height) throws Exception {

		FileOutputStream out = new FileOutputStream(file);
		Graphics2D g = new EpsGraphics(file.getName(), out, 0, 0, width, height, ColorMode.COLOR_RGB);

		chart.draw(g, new Rectangle2D.Double(0, 0, width, height));
		out.write(g.toString().getBytes());
		out.close();

	}

	protected void jComboBoxItemStateChanged() {

		try {

			XYDataset dataset;
			String yTitle;

			if (comboBox.getItemCount() != 0) {

				dataset = createDataset(comboBox.getSelectedItem().toString());
				yTitle = comboBox.getSelectedItem().toString();
			} else {
				dataset = null;
				yTitle = new String();
			}

			createChart(dataset, chartTitle, xTitle, yTitle);

			chartPanel.removeAll();

			chartPanel.setChart(chart);

			chartPanel.repaint();

		} catch (Exception e) {
		}
	}

	private JFreeChart createChart(XYDataset dataset, String title, String xTitle, String yTitle) {

		int numSeries = series.getSeriesCount();
		colors = createTableColor(numSeries);

		chart = ChartFactory.createXYLineChart(title, xTitle, yTitle, dataset, PlotOrientation.VERTICAL, true, true,
				false);

		chart.setBackgroundPaint(Color.white);

		chart.getXYPlot().setBackgroundPaint(Color.white);
		chart.getXYPlot().setDomainGridlinePaint(Color.white);
		chart.getXYPlot().setRangeGridlinePaint(Color.white);

		XYLineAndShapeRenderer renderer;

		if (viewSpline) {
			renderer = new XYSplineRenderer(4);
		} else {
			renderer = new XYLineAndShapeRenderer();
		}

		renderer.setDrawSeriesLineAsPath(true);

		if (!viewdefaultsLineTypes) {

			renderer.setSeriesStroke(0, new BasicStroke(2.0F));

			renderer.setSeriesStroke(1,
					new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 2 }, 0));

			renderer.setSeriesStroke(2, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
					new float[] { 6.0f, 2.0f, 6.0f, 2.0f }, 0.0f));

			renderer.setSeriesStroke(3, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
					new float[] { 12.0f, 2.0f, 2.0f, 2.0f }, 0.0f));

			renderer.setSeriesStroke(4, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
					new float[] { 12.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f }, 0.0f));

			renderer.setSeriesStroke(5, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
					new float[] { 12, 2, 12, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, 0));

			renderer.setSeriesStroke(6, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
					new float[] { 6.0f, 2.0f, 6.0f, 2.0f, 2.0f, 2.0f }, 0.0f));

			renderer.setSeriesStroke(7, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
					new float[] { 6.0f, 2.0f, 6.0f, 2.0f, 6.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f }, 0.0f));
		} else {

			for (int i = 0; i < 10; i++) {
				renderer.setSeriesStroke(i, new BasicStroke(2.0F));
			}
		}

		for (int i = 0; i < numSeries; i++) {

			renderer.setSeriesShapesVisible(i, viewPointsForm);
			renderer.setSeriesShapesFilled(i, false);

			if (viewWithOutColor) {

				renderer.setSeriesPaint(i, Color.BLACK);

			} else {

				String name = series.getSeries(i).getKey().toString();

				if (!controlCurveColor.containsKey(name)) {

					renderer.setSeriesPaint(i, colors[i]);
					controlCurveColor.put(name, colors[i]);

				} else {

					renderer.setSeriesPaint(i, controlCurveColor.get(name));

				}
			}
		}

		chart.getXYPlot().setRenderer(renderer);

		return chart;
	}

	/**
	 * Create the colors to render the curves
	 * 
	 * @param length
	 *            the new array of colors
	 * @return new array of colors
	 */
	private Color[] createTableColor(int length) {
		boolean sta = true;
		Color[] pink = new Color[] { Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN,
				Color.MAGENTA, new Color(111, 83, 64), new Color(153, 51, 255), new Color(102, 204, 255),
				new Color(85, 80, 126), new Color(168, 80, 126) };

		Color[] color = new Color[length];

		try {
			for (int i = 0; i < pink.length; i++) {
				color[i] = pink[i];
			}

		} catch (IndexOutOfBoundsException e) {
			sta = false;
		}
		if (sta) {

			for (int i = pink.length; i < length; i++) {
				color[i] = createNewColor(color[i / 2], color[i - pink.length]);

			}

		}
		return color;
	}

	/**
	 * Create a new color
	 *
	 * @param one
	 *            first color
	 * @param two
	 *            second color
	 * @return the new color
	 */
	private Color createNewColor(Color one, Color two) {

		int red = (one.getRed() + two.getRed()) / 2;
		int green = (one.getGreen() + two.getGreen()) / 2;
		int blue = (one.getBlue() + two.getBlue()) / 2;

		return new Color(red, green, blue);
	}

	private XYDataset createDataset(String metricName) {

		series = new XYSeriesCollection();

		// For each collection of evaluations
		for (int i = 0; i < evaluationsCollection.size(); i++) {

			if (!set.contains(i)) {

				List<AbstractEvaluation> evaluations = evaluationsCollection.get(i);
				XYSeries newXYSerie = new XYSeries(queryNames.get(i));

				int evalIndex = 0;

				if (extractMetricFromCurve(evaluations, metricName)) {
					for (AbstractEvaluation eval : evaluations) {
						if (evalIndex % reportFrecuency == 0) {
							newXYSerie.add(eval.getLabeledSetSize(), eval.getMetricValue(metricName));
						}
						++evalIndex;
					}
				} else {
					// JOptionPane.showMessageDialog(
					// this,
					// "The metric -" + metricName
					// + "- do not appears in -"
					// + queryNames.get(i) + "-");
				}

				series.addSeries(newXYSerie);
			}
		}

		return series;
	}

	/**
	 * Add an evaluation
	 * 
	 * @param evaluation
	 *            The evaluation to add
	 */
	public void add(AbstractEvaluation evaluation) {

		evaluationsCollection.get(evaluationsCollection.size() - 1).add(evaluation);

		// fire the itemStateChanged method
		jComboBoxItemStateChanged();

	}

	public void addSerie(ArrayList<AbstractEvaluation> evaluations, String queryName) {

		if (!setSeries.contains(queryName)) {
			setSeries.add(queryName);
			evaluationsCollection.add(evaluations);
			queryNames.add(queryName);
			curveOptions = null;

		}
	}

	public static boolean extractMetricFromCurve(List<AbstractEvaluation> evaluations, String metricName) {
		if (!evaluations.isEmpty()) {
			return extractMetricFromEvaluation(evaluations.get(0), metricName);
		}
		return false;
	}

	public static boolean extractMetricFromEvaluation(AbstractEvaluation eval, String metricName) {
		for (String metric : eval.getMetricNames()) {
			if (metric.equalsIgnoreCase(metricName)) {
				return true;
			}
		}
		return false;
	}

	protected Object[][] informationTable() {

		int length = queryNames.size();
		Object[][] row = new Object[length][3];

		for (int i = 0; i < queryNames.size(); i++) {
			if (set.contains(i)) {
				row[i][0] = (Object) new Boolean(null);
			} else {
				row[i][0] = (Object) new Boolean("true");
			}
			row[i][1] = queryNames.get(i);
			row[i][2] = controlCurveColor.get(queryNames.get(i));

		}
		return row;
	}

	/**
	 * Set the measure names
	 * 
	 * @param items
	 *            The items for the combobox component
	 */
	public void setMeasuresNames(String[] items) {

		for (String item : items) {
			comboBox.addItem(item);
		}

	}

	/**
	 * Enable the visual components
	 */
	public void enabledMetrics() {
		comboBox.setEnabled(true);
		menubar.setEnabled(true);
		menubar.getMenu(0).setEnabled(true);

		slider.setValue(1);
		slider.setMaximum((evaluationsCollection.get(0).size() > 1) ? (evaluationsCollection.get(0).size() - 1) : 1);
		slider.setEnabled(true);
	}

	/**
	 * Main method
	 * 
	 * @param args
	 *            NOT IN USE.
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				ExternalBasicChart demo = new ExternalBasicChart("Active learning process", "",
						"Number of labeled instances");
				demo.pack();
				RefineryUtilities.centerFrameOnScreen(demo);
				demo.setVisible(true);
			}
		});

	}

	/**
	 * Create the menu
	 * 
	 * @return Create the menu that allows load the result from others
	 *         experiments.
	 */
	private JMenu createMenu() {

		final JMenu fileMenu = new JMenu("Options");

		final JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		fileMenu.add("Add report file or directory").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int action = f.showOpenDialog(fileMenu);

				if (action == JFileChooser.APPROVE_OPTION) {

					loadReportFile(f.getSelectedFile());

				}

			}
		});

		fileMenu.add("Area under learning curve (ALC)").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboBox.getItemCount() != 0) {

					if (!comboBox.getSelectedItem().toString().isEmpty()) {

						StringBuilder st = new StringBuilder();

						st.append("Measure: ").append(comboBox.getSelectedItem()).append("\n\n");

						for (int query = 0; query < queryNames.size(); query++) {

							double value = extractMetricFromCurve(evaluationsCollection.get(query),
									comboBox.getSelectedItem().toString())
											? LearningCurveUtility.getAreaUnderLearningCurve(
													evaluationsCollection.get(query),
													comboBox.getSelectedItem().toString())
											: Double.NaN;

							String valueString = Double.compare(value, Double.NaN) == 0 ? "Do not appears the metric"
									: String.format("%.3f", value);

							System.out.println(queryNames.get(query) + ":" + valueString);
							st.append(queryNames.get(query)).append(": ").append(valueString).append("\n");

						}

						JOptionPane.showMessageDialog(content, st.toString(), "Area under the learning curve",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});

		fileMenu.add("Clear").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				clearCurves();

			}

		});

		fileMenu.add("Curve options").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				data = informationTable();

				if (queryNames.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please add curves", "Information",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (curveOptions == null) {
					curveOptions = new LearningCurvesVisualTable(ExternalBasicChart.this);
				} else {
					curveOptions.setVisible(true);
				}

			}

		});

		fileMenu.add("Ranges").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String ranges = JOptionPane.showInputDialog(
						"Insert the ranges to analyze separated by a space." + "\nExample: 34-56 first-45 74-last");

				if (ranges != null && !ranges.trim().isEmpty()) {
					ArrayList<LearningCurveUtility.Range> r = new ArrayList<LearningCurveUtility.Range>();
					String[] div = ranges.split(" ");
					for (String string : div) {
						try {
							r.add(new LearningCurveUtility.Range(string));
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error parsing: " + string, "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}

					if (!r.isEmpty()) {
						try {
							File temp = File.createTempFile("externalTemp", null);
							temp.delete();
							File directory = new File(temp.getParentFile(), temp.getName());
							directory.mkdirs();

							for (File file : reportsFiles) {
								Files.copy(file.toPath(), new File(directory, file.getName()).toPath());
							}

							File result = LearningCurveUtility.reportsRange(directory.getAbsolutePath(), null, r);

							historyFiles.add(directory);

							clearCurves();

							historyFiles.add(result);
							for (File file : result.listFiles()) {
								loadReportFile(file);
								reportsFiles.add(file);
							}
						} catch (IOException ex) {
							Logger.getLogger(ExternalBasicChart.class.getName()).log(Level.SEVERE, null, ex);
						}

					}
				}
			}
		});

		fileMenu.addSeparator();

		fileMenu.add("View shapes").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				viewPointsForm = !viewPointsForm;

				if (!viewPointsForm) {
					((JMenuItem) e.getSource()).setText("View shapes");
				} else {
					((JMenuItem) e.getSource()).setText("Do not view shapes");
				}

				((JMenuItem) e.getSource()).repaint();

				fileMenu.repaint();

				jComboBoxItemStateChanged();
			}
		});

		fileMenu.add("View default line types").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				viewdefaultsLineTypes = !viewdefaultsLineTypes;

				if (!viewdefaultsLineTypes) {
					((JMenuItem) e.getSource()).setText("View default line types");
				} else {
					((JMenuItem) e.getSource()).setText("View dash types");
				}

				((JMenuItem) e.getSource()).repaint();

				fileMenu.repaint();

				jComboBoxItemStateChanged();

			}
		});

		fileMenu.add("View black-and-white").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				viewWithOutColor = !viewWithOutColor;

				if (!viewWithOutColor) {
					((JMenuItem) e.getSource()).setText("View black-and-white");
				} else {
					((JMenuItem) e.getSource()).setText("View colors");
				}

				((JMenuItem) e.getSource()).repaint();

				fileMenu.repaint();

				jComboBoxItemStateChanged();

			}
		});

		fileMenu.add("View spline").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				viewSpline = !viewSpline;

				if (!viewSpline) {
					((JMenuItem) e.getSource()).setText("View spline");
				} else {
					((JMenuItem) e.getSource()).setText("View lines");
				}

				((JMenuItem) e.getSource()).repaint();

				fileMenu.repaint();

				jComboBoxItemStateChanged();

			}
		});

		return fileMenu;
	}

	private void loadReportFile(File x) {

		if (x.isDirectory()) {
			File[] temp = x.listFiles();
			for (File file : temp) {
				loadReportFile(file);
			}

		} else if (x.isFile()) {
			try {
				LoadDataFromReporterFile fileInput = new LoadDataFromReporterFile(x);

				if (comboBox.getItemCount() == 0) {
					{
						setMeasuresNames(fileInput.getEvaluations().get(0).getMetricNames());
					}
				} else if (!compareMetrics(fileInput.getEvaluations().get(0).getMetricNames())) {
					JOptionPane.showMessageDialog(chartPanel,
							"The report file that you are trying to load does not belong to the same category."
									+ "\nThe metrics are different. In some cases the curves will not appear.");
				}

				// verify to add report file
				int previousSize = evaluationsCollection.size();

				addSerie(fileInput.getEvaluations(), fileInput.getProperties().getProperty("Query strategy"));

				// verify to add report file
				if (previousSize < evaluationsCollection.size()) {
					reportsFiles.add(x);
				}

				// fire the jComboBoxStateChanged()
				slider.setValue(1);
				jComboBoxItemStateChanged();

				if (fileInput.getEvaluations().size() > max) {
					max = fileInput.getEvaluations().size() - 1;
					slider.setMaximum(max);
				}

				if (curveOptions != null) {
					curveOptions.setVisible(false);
					curveOptions = null;
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "A error has happened loading the file: " + x.getName(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Clear the curves
	 */
	public void clearCurves() {

		queryNames = new ArrayList<String>();
		evaluationsCollection = new ArrayList<List<AbstractEvaluation>>();
		comboBox.removeAllItems();
		controlCurveColor = new HashMap<String, Color>();
		colors = null;
		data = null;
		set.clear();
		if (curveOptions != null) {
			curveOptions.setVisible(false);
			curveOptions = null;
		}
		setSeries.clear();

		// clean reports
		for (File file : historyFiles) {
			if (file.isDirectory()) {
				for (File file1 : file.listFiles()) {
					try {
						FileUtil.deleteFileIfTemp(file1);
					} catch (IOException ex) {
						Logger.getLogger(ExternalBasicChart.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			try {
				FileUtil.deleteFileIfTemp(file);
			} catch (IOException ex) {
				Logger.getLogger(ExternalBasicChart.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		historyFiles.clear();

		for (File file : reportsFiles) {
			try {
				FileUtil.deleteFileIfTemp(file);
			} catch (IOException ex) {
				Logger.getLogger(ExternalBasicChart.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		reportsFiles.clear();
	}

	/**
	 * Compare the metric names
	 *
	 * @param metricNames
	 *            Metric names.
	 * @return
	 * 		<p>
	 *         true: if the combobox's values are equals to the metrics's names
	 *         </p>
	 *         <p>
	 *         false: otherwise
	 *         </p>
	 */
	private boolean compareMetrics(String[] metricNames) {

		for (int i = 0; i < metricNames.length; ++i) {

			if (!comboBox.getItemAt(i).equals(metricNames[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Disable the visual components in order to they are not functional in the
	 * AL process
	 */
	public void setDisabledComponents() {

		comboBox.setEnabled(false);
		menubar.setEnabled(false);
		slider.setEnabled(false);
		menubar.getMenu(0).setEnabled(false);
	}

	public Set<Integer> getSet() {
		return set;
	}

	public void setSet(Set<Integer> set) {
		this.set = set;
	}

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}

	public ArrayList<String> getQueryNames() {
		return queryNames;
	}

	public void setQueryNames(ArrayList<String> queryNames) {
		this.queryNames = queryNames;
	}

	public HashMap<String, Color> getControlCurveColor() {
		return controlCurveColor;
	}

	public void setControlCurveColor(HashMap<String, Color> controlCurveColor) {
		this.controlCurveColor = controlCurveColor;
	}
}
