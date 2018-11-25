package icons;

import de.fraunhofer.iem.mois.data.Constants;

import javax.swing.*;

/**
 * @author Oshando Johnson on 24.10.18
 */
public class IconUtils {

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
}
