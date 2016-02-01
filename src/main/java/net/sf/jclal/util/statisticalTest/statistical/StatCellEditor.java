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

package net.sf.jclal.util.statisticalTest.statistical;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class StatCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 8065855130952389408L;
	private JTextField field;

	/**
	 * Get the value of a cell
	 *
	 * @return Value stored
	 */
	public Object getCellEditorValue() {
		return field.getText();

	}

	/**
	 * Gets the cell editor component used
	 *
	 * @param table
	 *            Table to modify
	 * @param value
	 *            Value to represent
	 * @param isSelected
	 *            Tests if the cell is currently selected
	 * @param row
	 *            Row selected
	 * @param column
	 *            Column selected
	 *
	 * @return Cell editor component
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		field.setText(value + "");
		return field;
	}

}
