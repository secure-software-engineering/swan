package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.ui.JBColor;
import de.fraunhofer.iem.mois.assist.data.Category;

import javax.swing.*;
import java.awt.*;

public class CategoryRenderer extends JLabel implements ListCellRenderer<Category> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Category> list, Category category, int index, boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {
            setBackground(new JBColor(new Color(9, 80, 208),new Color(9, 80, 208)));
            setForeground(JBColor.white);
        } else {
            setBackground(JBColor.WHITE);
            setForeground(JBColor.BLACK);
        }

        setIcon(category.getIcon());
        setText(category.toString());
        setBorder(BorderFactory.createEmptyBorder(3, 4, 2, 0));
        setOpaque(true);

        return this;
    }
}
