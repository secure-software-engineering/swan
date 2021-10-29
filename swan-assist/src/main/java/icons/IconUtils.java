/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package icons;

import com.intellij.openapi.util.IconLoader;
import de.fraunhofer.iem.swan.data.Constants;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Determines icon for method
 */
public class IconUtils {

    /**
     * Returns icon based on identifier
     * @param id Method Type
     * @return Icon for method
     */
    public static Icon getIcon(String id) {

        switch (id) {
            case Constants.SINK:
                return PluginIcons.SINK;
            case Constants.SOURCE:
                return PluginIcons.SOURCE;
            case Constants.SANITIZER:
                return PluginIcons.SANITIZER;
            case Constants.AUTHENTICATION_SAFE:
                return PluginIcons.AUTHENTICATION_SAFE;
            case Constants.AUTHENTICATION_UNSAFE:
                return PluginIcons.AUTHENTICATION_UNSAFE;
            case Constants.AUTHENTICATION_NOCHANGE:
                return PluginIcons.AUTHENTICATION_NOCHANGE;
            default:
                return PluginIcons.CWE;
        }
    }

    /**
     * Returns icon for method based on its categories
     * @param categoryList List of categories
     * @return Method's icon
     */
    public static Icon getNodeIcon(ArrayList<String> categoryList) {

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

        Icon icon;

        try{
             icon = IconLoader.findIcon("/icons/" + joinedList + ".png");
        }catch (Exception e){
            icon = PluginIcons.DEFAULT;
        }

        return icon;
    }
}
