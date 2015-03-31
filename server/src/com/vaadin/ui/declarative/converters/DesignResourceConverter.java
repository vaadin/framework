/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui.declarative.converters;

import java.io.File;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * A converter for {@link Resource} implementations supported by
 * {@link DesignAttributeHandler}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignResourceConverter implements Converter<String, Resource> {

    @Override
    public Resource convertToModel(String value,
            Class<? extends Resource> targetType, Locale locale)
            throws Converter.ConversionException {
        if (value.startsWith("http://") || value.startsWith("https://")
                || value.startsWith("ftp://") || value.startsWith("ftps://")) {
            return new ExternalResource(value);
        } else if (value.startsWith("theme://")) {
            return new ThemeResource(value.substring(8));
        } else if (value.startsWith("font://")) {
            return FontAwesome.valueOf(value.substring(7));
        } else {
            return new FileResource(new File(value));
        }
    }

    @Override
    public String convertToPresentation(Resource value,
            Class<? extends String> targetType, Locale locale)
            throws Converter.ConversionException {
        if (value instanceof ExternalResource) {
            return ((ExternalResource) value).getURL();
        } else if (value instanceof ThemeResource) {
            return "theme://" + ((ThemeResource) value).getResourceId();
        } else if (value instanceof FontAwesome) {
            return "font://" + ((FontAwesome) value).name();
        } else if (value instanceof FileResource) {
            String path = ((FileResource) value).getSourceFile().getPath();
            if (File.separatorChar != '/') {
                // make sure we use '/' as file separator in templates
                return path.replace(File.separatorChar, '/');
            } else {
                return path;
            }
        } else {
            throw new Converter.ConversionException("unknown Resource type - "
                    + value.getClass().getName());
        }
    }

    @Override
    public Class<Resource> getModelType() {
        return Resource.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
