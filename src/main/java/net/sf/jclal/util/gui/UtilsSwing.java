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
package net.sf.jclal.util.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.sf.jclal.gui.view.xml.GUIXmlConfig;
import org.jfree.ui.RefineryUtilities;

/**
 * Utility class to work with Java's swing components
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class UtilsSwing {

    /**
     * The file path with '\', is replaced by '/'.
     *
     * @param path The path
     * @return A string 
     */
    public static String filePathJava(String path) {
        String dev = "";
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '\\') {
                dev += '/';
            } else {
                dev += path.charAt(i);
            }
        }
        return dev;
    }

    /**
     * Creates a button that allows select a file
     *
     * @param text The text field
     * @return The JButton 
     */
    public static JButton buttonFileSelect(final JTextField text) {
        final JButton boton = new JButton("Select...");
        final JFileChooser f = new JFileChooser();

        boton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int action = f.showOpenDialog(boton);

                if (action == JFileChooser.APPROVE_OPTION) {
                    text.setText(filePathJava(f.getSelectedFile().getAbsolutePath()));
                }
            }
        });
        return boton;
    }

    /**
     * Creates a button that allows select a directory
     *
     * @param text The textfield
     * @return The jbutton
     */
    public static JButton buttonFolderSelect(final JTextField text) {
        final JButton boton = new JButton("Select...");
        final JFileChooser f = new JFileChooser();

        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        boton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int action = f.showOpenDialog(boton);

                if (action == JFileChooser.APPROVE_OPTION) {
                    text.setText(filePathJava(f.getSelectedFile().getAbsolutePath()));
                }
            }
        });
        return boton;
    }

    /**
     * Allows to relate a frame to a button
     *
     * @param frame The frame to use
     * @param button The button to use
     */
    public static void jointButton(JFrame frame, final JButton button) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                button.setEnabled(true);
            }
        });
    }

    /**
     * Allows to relate a frame to a button and a checkbox
     *
     * @param frame The frame to use
     * @param button The button to use
     * @param check The checkbox to use
     */
    public static void jointButton(JFrame frame, final JButton button, final JCheckBox check) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (check.isSelected()) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        });
    }

    /**
     * Allows to relate a dialog to a button
     *
     * @param frame The frame to use
     * @param button The button to use
     */
    public static void jointButton(JDialog frame, final JButton button) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                button.setEnabled(true);
            }
        });
    }

    /**
     * Allows to relate a dialog to a button and a checkbox
     *
     * @param frame The jdialog to use
     * @param button The button to use
     * @param check The checkbox to use
     */
    public static void jointButton(JDialog frame, final JButton button, final JCheckBox check) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (check.isSelected()) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        });
    }

    /**
     * It establishes the size in accordance with its components. It centers the
     * content.
     *
     * @param x The JFrame.
     */
    public static void initFrame(JFrame x) {
        x.pack();
        RefineryUtilities.centerFrameOnScreen(x);
        x.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * It establishes the size in accordance with its components. It centers the
     * content.
     *
     * @param x The JDialog.
     */
    public static void initFrame(final JDialog x) {
        x.pack();
        RefineryUtilities.centerFrameOnScreen(x);
        x.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * Return true if the combo contains the element, return false otherwise
     *
     * @param combo The combobox 
     * @param element The element
     * @return True if the combobox has the element, false otherwise
     */
    public static boolean comboContainsElement(JComboBox combo, String element) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the combo contains the element, return false otherwise
     *
     * @param combo The combo
     * @param element The element to validate
     * @return True if the element exist in the combobox, false otherwise
     */
    public static boolean comboContainsElement(JComboBox combo, GUIXmlConfig.ComboElement element) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if the best one contains to the element and if is not then add
     * the element into the combo box
     *
     * @param combo The comobobox
     * @param element The element to add
     */
    public static void addComboElement(JComboBox combo, String element) {
        GUIXmlConfig.ComboElement curr = new GUIXmlConfig.ComboElement(element);
        if (!comboContainsElement(combo, curr)) {
            combo.addItem(curr);
        }
        combo.setSelectedItem(curr);
    }

    /**
     * Initiates the text Field
     *
     * @param text The textfield to init
     * @param element The element
     */
    public static void initJTextField(JTextField text, String element) {
        text.setText(element);
    }

    /**
     * Initiates the Check Box
     *
     * @param box The checkbox to init
     * @param element The element
     */
    public static void initJCheckBox(JCheckBox box, String element) {
        box.setSelected(Boolean.parseBoolean(element));
    }

    /**
     * Validates if the text is a number
     *
     * @param text The string 
     * @param x The component
     * @return A number
     * @throws NumberFormatException The exception that will be launched.
     */
    public static double validateValue(String text, Component x) throws NumberFormatException {
        double dev = -1;
        try {
            dev = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(x, "Error parsing to number in field: " + text,
                    "Error message", JOptionPane.ERROR_MESSAGE);
            throw ex;
        }
        return dev;
    }

    /**
     * Validates if the text field contains a number
     *
     * @param text The jtext component
     * @param aThis The other component
     * @return A number
     * @throws NumberFormatException The exception that will be launched
     */
    public static double validateJTextField(JTextField text, Component aThis) throws NumberFormatException {
        try {
            double dev = validateValue(text.getText(), aThis);
            return dev;
        } catch (NumberFormatException ex) {
            text.setText(null);
            throw ex;
        }
    }

    /**
     * Removes all the components of x
     *
     * @param x The component to clean
     */
    public static void cleanComponent(JComponent x) {
        if (x != null) {
            x.removeAll();
            x.repaint();
            x = null;
        }
    }

    /**
     * Return true if the list is not empty, false otherwise
     *
     * @param x The combobox
     * @return true if the list is not empty, false otherwise
     */
    public static boolean exist(JComboBox x) {
        if (x.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(x,
                    "Empty list.", "Message", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * It joins the characters and builds a string
     *
     * @param x The x value
     * @return An string
     */
    public static String passString(char[] x) {
        String dev = "";
        for (char c : x) {
            dev += c;
        }
        return dev;
    }
}
