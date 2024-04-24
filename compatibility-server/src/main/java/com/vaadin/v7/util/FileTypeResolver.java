/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.v7.util;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

/**
 * Utility class that can figure out mime-types and icons related to files.
 * <p>
 * Note : The icons are associated purely to mime-types, so a file may not have
 * a custom icon accessible with this class.
 * </p>
 *
 * @author Vaadin Ltd.
 * @since 3.0
 * @deprecated Only used for compatibility-server
 */
@Deprecated
@SuppressWarnings("serial")
public class FileTypeResolver extends com.vaadin.util.FileTypeResolver implements Serializable {

    /**
     * Default icon given if no icon is specified for a mime-type.
     */
    public static Resource DEFAULT_ICON = new ThemeResource(
            "../runo/icons/16/document.png");

    /**
     * MIME type to Icon mapping.
     */
    private static final Map<String, Resource> MIME_TO_ICON_MAP = new ConcurrentHashMap<>();

    static {
        // Initialize Icon2s
        ThemeResource folder = new ThemeResource("../runo/icons/16/folder.png");
        addIcon("inode/drive", folder);
        addIcon("inode/directory", folder);
    }

    /**
     * Gets the descriptive icon representing file, based on the filename. First
     * the mime-type for the given filename is resolved, and then the
     * corresponding icon is fetched from the internal icon storage. If it is
     * not found the default icon is returned.
     *
     * @param fileName
     *            the name of the file whose icon is requested.
     * @return the icon corresponding to the given file
     */
    public static Resource getIcon(String fileName) {
        return getIconByMimeType(getMIMEType(fileName));
    }

    private static Resource getIconByMimeType(String mimeType) {
        final Resource icon = MIME_TO_ICON_MAP.get(mimeType);
        if (icon != null) {
            return icon;
        }

        // If nothing is known about the file-type, general file
        // icon is used
        return DEFAULT_ICON;
    }

    /**
     * Gets the descriptive icon representing a file. First the mime-type for
     * the given file name is resolved, and then the corresponding icon is
     * fetched from the internal icon storage. If it is not found the default
     * icon is returned.
     *
     * @param file
     *            the file whose icon is requested.
     * @return the icon corresponding to the given file
     */
    public static Resource getIcon(File file) {
        return getIconByMimeType(getMIMEType(file));
    }

    /**
     * Adds a icon for the given mime-type. If the mime-type also has a
     * corresponding icon, it is replaced with the new icon.
     *
     * @param mimeType
     *            the mime-type whose icon is to be changed.
     * @param icon
     *            the new icon to be associated with <code>MIMEType</code>.
     */
    public static void addIcon(String mimeType, Resource icon) {
        MIME_TO_ICON_MAP.put(mimeType, icon);
    }

    /**
     * Gets the internal mime-type to icon mapping.
     *
     * @return unmodifiable map containing the current mime-type to icon mapping
     */
    public static Map<String, Resource> getMIMETypeToIconMapping() {
        return Collections.unmodifiableMap(MIME_TO_ICON_MAP);
    }

    private FileTypeResolver() {
    }
}
