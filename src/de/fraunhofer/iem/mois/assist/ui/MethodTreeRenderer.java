package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.Formatter;
import de.fraunhofer.iem.mois.data.Category;
import icons.IconUtils;
import icons.PluginIcons;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Rendering options for a method in the list.
 *
 * @author Oshando Johnson
 */

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

            if (object instanceof MethodWrapper) {

                MethodWrapper method = (MethodWrapper) object;
                text.setText("<html>" + Formatter.trimProperty(method.getMethodName(false)) + " ( ) <font color='gray'>" + Formatter.trimProperty(method.getReturnType(false)) + "</font></html>");
                text.setIcon(getNodeIcon(method.getTypesList()));

                if (method.getUpdateOperation() != null && method.getUpdateOperation().equals(Constants.METHOD_ADDED))
                    text.setForeground(new JBColor(new Color(1, 128, 0), new Color(1, 128, 0)));
                else if (method.getUpdateOperation() != null && method.getUpdateOperation().equals(Constants.METHOD_DELETED))
                    text.setForeground(new JBColor(new Color(178, 34, 34), new Color(178, 34, 34)));

                text.setToolTipText(method.getMethodName(true));

            } else if (object instanceof Category) {

                Category category = (Category) object;

                text.setIcon(IconUtils.getIcon(category.toString()));
                text.setText(category.toString());
            } else {

                text.setText(value.toString());
                text.setIcon(null);
            }
        }

        return text;
    }

    private Icon getNodeIcon(ArrayList<String> categoryList) {

        ArrayList<String> iconList = new ArrayList<>();

        if (categoryList.size() == 1)
            return IconUtils.getIcon(categoryList.get(0));

        for (String category : categoryList) {
            if (!iconList.contains(category.substring(0, 3))) {
                iconList.add(category.substring(0, 3));
            }
        }

        Collections.sort(iconList, Collections.reverseOrder());
        String joinedList = StringUtils.join(iconList, "_").toLowerCase();

        Icon icon = IconLoader.findIcon("/icons/" + joinedList + ".png");

        if (icon == null)
            icon = PluginIcons.DEFAULT;

        return icon;
    }
}
