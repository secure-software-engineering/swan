package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Icons for various buttons and toolbars.
 * @author Oshando Johnson
 */

public interface PluginIcons {

    Icon SWAN_ASSIST = IconLoader.getIcon("/icons/swan_assist.png");
    Icon IMPORT_ACTION = IconLoader.getIcon("/icons/import.png");
    Icon REFRESH_SWAN = IconLoader.getIcon("/icons/refresh.png");
    Icon HELP_ACTION = IconLoader.getIcon("/icons/help.png");
    Icon FILTER_ACTION = IconLoader.getIcon("/icons/filter.png");
    Icon ADD_METHOD_ACTION = IconLoader.getIcon("/icons/add.png");
    Icon EXPORT_ACTION = IconLoader.getIcon("/icons/export.png");

    Icon NOTIFICATION_NONE = IconLoader.getIcon("/icons/notification_none.png");
    Icon NOTIFICATION_NEW = IconLoader.getIcon("/icons/notification.png");

    Icon SOURCE = IconLoader.getIcon("/icons/sou.png");
    Icon SANITIZER = IconLoader.getIcon("/icons/san.png");
    Icon SINK = IconLoader.getIcon("/icons/sin.png");
    Icon AUTHENTICATION_SAFE = IconLoader.getIcon("/icons/auth_safe.png");
    Icon AUTHENTICATION_UNSAFE = IconLoader.getIcon("/icons/auth_unsafe.png");
    Icon AUTHENTICATION_NOCHANGE = IconLoader.getIcon("/icons/auth_no.png");
    Icon CWE = IconLoader.getIcon("/icons/cwe.png");
    Icon DEFAULT = IconLoader.getIcon("/icons/default.png");


    Icon SELECTED = IconLoader.getIcon("/icons/selected.png");
}
