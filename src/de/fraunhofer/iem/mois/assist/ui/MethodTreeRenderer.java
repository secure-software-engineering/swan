package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.ui.JBColor;
import de.fraunhofer.iem.mois.assist.data.Category;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.util.Constants;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class MethodTreeRenderer extends JLabel implements TreeCellRenderer {

    private JLabel text = new JLabel();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        if (selected)
            text.setForeground(JBColor.WHITE);
        else
            text.setForeground(JBColor.BLACK);


        if (value instanceof DefaultMutableTreeNode) {
            Object object = ((DefaultMutableTreeNode) value).getUserObject();

            if (object instanceof Method) {

                Method method = (Method) object;
                text.setText("<html>" + method.getClassName(false) + " ( ) <font color='gray'>" + method.getReturnType(false) + "</font></html>");
                text.setIcon(null);

                if (method.getUpdateOperation() != null && method.getUpdateOperation().equals(Constants.METHOD_ADDED))
                    text.setForeground(new JBColor(new Color(1,128,0),new Color(1,128,0)));
                else if (method.getUpdateOperation() != null && method.getUpdateOperation().equals(Constants.METHOD_DELETED))
                    text.setForeground(new JBColor(new Color(178,34,34),new Color(178,34,34)));

                text.setToolTipText(method.getClassName(true));

            } else if (object instanceof Category) {

                Category category = (Category) object;

                text.setIcon(category.getIcon());
                text.setText(category.toString());
            } else {

                text.setText(value.toString());
                text.setIcon(null);
            }
        }

        return text;
    }
}
