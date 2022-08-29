/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui;

import com.intellij.ui.JBColor;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import icons.IconUtils;
import icons.PluginIcons;
import de.fraunhofer.iem.swan.assist.util.Pair;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Renders how methods should be displayed in the list.
 */

public class MethodTreeRenderer extends JLabel implements TreeCellRenderer {

    private JLabel text = new JLabel();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        String HIGHLIGHT_COLOR = "gray";
        if (selected){
            text.setForeground(JBColor.WHITE);
             HIGHLIGHT_COLOR = "white";
        } else
            text.setForeground(JBColor.BLACK);

        if (value instanceof DefaultMutableTreeNode) {
            Object object = ((DefaultMutableTreeNode) value).getUserObject();

            if (object instanceof MethodWrapper) {

                MethodWrapper method = (MethodWrapper) object;

                String methodName = Formatter.trimProperty(method.getMethodName(false));

                if(methodName.contains("<init>"))
                    methodName = Formatter.trimProperty(method.getClassName(false));

                text.setText("<html><font color='" + HIGHLIGHT_COLOR + "'>" + Formatter.trimProperty(method.getReturnType(false)) + "</font> <b>" + methodName + "</b> ( )</html>");
                text.setIcon(IconUtils.getNodeIcon(method.getTypesList(false)));

                if (method.getUpdateOperation() != null && method.getUpdateOperation().equals(Constants.METHOD_ADDED) && !selected)
                    text.setForeground(new JBColor(new Color(1, 128, 0), new Color(1, 128, 0)));
                else if (method.getUpdateOperation() != null && method.getUpdateOperation().equals(Constants.METHOD_DELETED) && !selected)
                    text.setForeground(new JBColor(new Color(178, 34, 34), new Color(178, 34, 34)));

                String cweList =": ";
                if(method.getCWEList().size()>0)
                    cweList = " relevant for <b>"+StringUtils.join(method.getCWEList(), ", ")+"</b>: ";

                text.setToolTipText("<html><i>Potential</i> <b>"+StringUtils.join(method.getTypesList(true), ", ")+"</b>"+cweList+ method.getSignature(true)+"</html>");

            } else if (object instanceof Category) {

                Category category = (Category) object;

                text.setIcon(IconUtils.getIcon(category.toString()));

                if (category.isCwe()) {

                    ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");
                    text.setText("<html>" + category.toString() + " <font color='" + HIGHLIGHT_COLOR + "'>" + resourceBundle.getString(category.toString() + ".Name") + "</font></html>");
                    text.setToolTipText("<html>" + "<b>" + category.toString() + "</b> " + resourceBundle.getString(category.toString() + ".FullName") + "</html>");
                } else
                    text.setText(category.toString());
            } else if(object instanceof Pair){

                Pair classPair = (Pair)object;
                String classname = classPair.getKey().toString();

                text.setToolTipText("<html>" +classPair.getValue()+ " methods in <b>"+classname+"</b></html>");
                text.setText("<html>" + classname.substring(classname.lastIndexOf(".") + 1) + "  <font color='" + HIGHLIGHT_COLOR + "'>(" + classPair.getValue() +")</font></html>");
                text.setIcon(PluginIcons.CLASS);
            }else {

                text.setText(value.toString().replace("color='gray'","color='"+ HIGHLIGHT_COLOR +"'"));
                text.setIcon(null);
                text.setToolTipText(null);
            }
        }
        return text;
    }
}
