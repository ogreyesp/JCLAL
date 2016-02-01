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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Visual Component to select a color
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ColorEditor extends AbstractCellEditor implements TableCellEditor,
        ActionListener {

    private static final long serialVersionUID = 6860595567665116281L;
    private Color currentColor;
    private JButton button;
    private JColorChooser colorChooser;
    private JDialog dialog;
    protected static final String EDIT = "edit";
    protected JTable table;
    private ExternalBasicChart chart;

    public ColorEditor(JTable table, ExternalBasicChart chart) {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        // Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button, "Pick a color", true, // modal
                colorChooser, this, // OK button handler
                null); // no CANCEL button handler

        this.table = table;
        this.chart = chart;
    }

    /**
     * Handles events from the editor button and from the "OK" button.
     */
    public void actionPerformed(ActionEvent e) {
        int row = table.getSelectedRow();
        if (EDIT.equals(e.getActionCommand())) {
            // The user has clicked the cell, so the dialog is showed.
            button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);

            // Make the renderer reappear.
            fireEditingStopped();

        } else { // The user pressed the "OK" button.
            currentColor = colorChooser.getColor();
            chart.getControlCurveColor().put(indice(row, 1), currentColor);
            chart.jComboBoxItemStateChanged();
        }

    }

    private String indice(int row, int column) {
        return (String) chart.getData()[row][column];
    }

    public Object getCellEditorValue() {
        return currentColor;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentColor = (Color) value;
        return button;
    }
}
