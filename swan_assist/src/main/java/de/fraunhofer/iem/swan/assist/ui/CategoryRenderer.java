/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui;

import com.intellij.ui.JBColor;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import icons.IconUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Renders how categories should be displayed in the list.
 */
public class CategoryRenderer extends JLabel implements ListCellRenderer<Category> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Category> list, Category category, int index, boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {
            setBackground(new JBColor(new Color(9, 80, 208), new Color(9, 80, 208)));
            setForeground(JBColor.white);
        } else {
            setBackground(JBColor.WHITE);
            setForeground(JBColor.BLACK);
        }

        if (category.isCwe()) {

            ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");

            setText("<html>" + Formatter.toTitleCase(category.toString()) + " <font color='gray'>" + resourceBundle.getString(category.toString() + ".Name") + "</font></html>");
            setToolTipText("<html>" + "<b>" + Formatter.toTitleCase(category.toString()) + "</b> " + resourceBundle.getString(category.toString() + ".FullName") + "</html>");
        } else {
            setText(Formatter.toTitleCase(category.toString()));
        }

        setIcon(IconUtils.getIcon(category.toString()));
        setBorder(BorderFactory.createEmptyBorder(3, 4, 2, 0));
        setOpaque(true);

        return this;
    }
}
