package de.fraunhofer.iem.swan.assist.ui;

import com.intellij.ui.JBColor;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import icons.IconUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Rendering options for a category show in the list.
 *
 * @author Oshando Johnson
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

        setIcon(IconUtils.getIcon(category.toString()));
        setText(Formatter.toTitleCase(category.toString()));
        setBorder(BorderFactory.createEmptyBorder(3, 4, 2, 0));
        setOpaque(true);

        return this;
    }
}
