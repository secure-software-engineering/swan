/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Icons for various buttons and toolbars.
 */
public interface PluginIcons {

    Icon DEV_ASSIST = IconLoader.findIcon("/icons/swan_assist.png", PluginIcons.class);
    Icon ACTIVE_FILTER_ACTION = IconLoader.findIcon("/icons/activeFilter.png", PluginIcons.class);

    Icon SOURCE = IconLoader.findIcon("/icons/sou.png", PluginIcons.class);
    Icon SANITIZER = IconLoader.findIcon("/icons/san.png", PluginIcons.class);
    Icon SINK = IconLoader.findIcon("/icons/sin.png", PluginIcons.class);
    Icon AUTHENTICATION_SAFE = IconLoader.findIcon("/icons/auth_safe.png", PluginIcons.class);
    Icon AUTHENTICATION_UNSAFE = IconLoader.findIcon("/icons/auth_unsafe.png", PluginIcons.class);
    Icon AUTHENTICATION_NOCHANGE = IconLoader.findIcon("/icons/auth_no.png", PluginIcons.class);
    Icon CWE = IconLoader.findIcon("/icons/cwe.png", PluginIcons.class);
    Icon DEFAULT = IconLoader.findIcon("/icons/default.png", PluginIcons.class);

}
