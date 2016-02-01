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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class LearningCurvesVisualTable extends JDialog {
    
    private static final long serialVersionUID = -26034215746849616L;
    
    private JScrollPane scroll;
    private JTable table;
    private ArrayList<String> queryNames;
    private Set<Integer> set;
    private JButton button;
    private Object[][] data;
    
    public LearningCurvesVisualTable(final ExternalBasicChart chart) {
        
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        queryNames = chart.getQueryNames();
        set = chart.getSet();
        data = chart.getData();
        button = new JButton("Update");
        
        LearningCurvesTableModel tableModel = new LearningCurvesTableModel(chart.getData(), chart.getQueryNames());
        this.setLocationRelativeTo(chart);
        table.setModel(tableModel);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                
                int nrow = table.getSelectedRow();
                int column = table.getSelectedColumn();
                
                if (column == 0) {
                    int indice = index(nrow);
                    String value = table.getValueAt(nrow, column).toString();
                    
                    if (value.equals("true")) {
                        
                        set.remove(indice);
                        
                    } else {
                        
                        set.add(indice);
                        
                    }
                    
                }
                
                chart.jComboBoxItemStateChanged();
            }
            
        });
        
        table.setDefaultRenderer(java.awt.Color.class, new ColorRenderer(true));
        table.setDefaultEditor(java.awt.Color.class, new ColorEditor(table, chart));
        
        scroll.setViewportView(table);
        
        button.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int index = 0;
                for (int i = 0; i < data.length; i++) {
                    String visualTable = (String) table.getValueAt(i, 1);
                    String visualData = (String) (data[i][1]);
                    if (!visualTable.equals(visualData)) {
                        queryNames.set(index, visualTable);
                        data[i][1] = visualTable;
                        Color color = chart.getControlCurveColor().get(visualData);
                        chart.getControlCurveColor().remove(visualData);
                        chart.getControlCurveColor().put(visualTable, color);
                        
                    }
                    index++;
                }
                chart.jComboBoxItemStateChanged();
                
            }
            
        });
        
        setBounds(40, 20, 50, 20);
        setTitle("Learning curves ");
        setAlwaysOnTop(false);
        
        setVisible(true);
        setResizable(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        add(scroll, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        
        pack();
        
    }
    
    private int index(int row) {
        for (int i = 0; i < queryNames.size(); i++) {
            
            if (data[row][1].equals(queryNames.get(i))) {
                
                return i;
            }
        }
        
        return -1;
    }
}
