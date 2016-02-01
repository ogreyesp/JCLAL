package net.sf.jclal.gui.view.components.chart;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class LearningCurvesTableModel extends DefaultTableModel {

    private static final long serialVersionUID = -6760941065024980148L;
    private Object[][] data;
    private ArrayList<String> queryNames;

    private Class<?>[] types = new Class[]{java.lang.Boolean.class,
        java.lang.String.class, java.awt.Color.class};

    public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    public LearningCurvesTableModel(Object[][] data,
            ArrayList<String> queryNames) {

        super(data, new String[]{"Visible", "Name of curve", "Color of curve"});

        this.data = data;
        this.queryNames = queryNames;

    }

}
