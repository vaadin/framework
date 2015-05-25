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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.GenericFontIcon;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * A converter for {@link Resource} implementations supported by
 * {@link DesignAttributeHandler}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class DesignResourceConverter implements Converter<String, Resource> {

    @Override
    public Resource convertToModel(String value,
            Class<? extends Resource> targetType, Locale locale)
            throws Converter.ConversionException {
        if (!value.contains("://")) {
            // assume it'is "file://" protocol, one that is used to access a
            // file on a given path on the server, this will later be striped
            // out anyway
            value = "file://" + value;
        }

        String protocol = value.split("://")[0];
        try {
            ResourceConverterByProtocol converter = ResourceConverterByProtocol
                    .valueOf(protocol.toUpperCase(Locale.ENGLISH));
            return converter.parse(value);
        } catch (IllegalArgumentException iae) {
            throw new ConversionException("Unrecognized protocol: " + protocol,
                    iae);
        }
    }

    @Override
    public String convertToPresentation(Resource value,
            Class<? extends String> targetType, Locale locale)
            throws Converter.ConversionException {
        ResourceConverterByProtocol byType = ResourceConverterByProtocol
                .byType(value.getClass());
        if (byType != null) {
            return byType.format(value);
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

    private static interface ProtocolResourceConverter extends Serializable {
        public String format(Resource value);

        public Resource parse(String value);
    }

    private static enum ResourceConverterByProtocol implements
            ProtocolResourceConverter {

        HTTP, HTTPS, FTP, FTPS, THEME {

            @Override
            public Resource parse(String value) {
                // strip "theme://" from the url, use the rest as the resource
                // id
                return new ThemeResource(value.substring(8));
            }

            @Override
            public String format(Resource value)
                    throws Converter.ConversionException {
                return new ResourceReference(value, null, null).getURL();
            }
        },
        FONTICON {
            @Override
            public Resource parse(String value) {
                final String address = (value.split("://", 2))[1];
                final String[] familyAndCode = address.split("/", 2);
                final int codepoint = Integer.valueOf(familyAndCode[1], 16);

                if (FontAwesome.FONT_FAMILY.equals(familyAndCode[0])) {
                    try {
                        return FontAwesome.fromCodepoint(codepoint);
                    } catch (IllegalArgumentException iae) {
                        throw new ConversionException(
                                "Unknown codepoint in FontAwesome: "
                                        + codepoint, iae);
                    }
                }

                FontIcon generic = new GenericFontIcon(familyAndCode[0],
                        codepoint);
                return generic;

            }

            @Override
            public String format(Resource value)
                    throws Converter.ConversionException {
                FontIcon icon = (FontIcon) value;
                return new ResourceReference(icon, null, null).getURL();

            }
        },
        @Deprecated
        FONT {
            @Override
            public Resource parse(String value) {
                // Deprecated, 7.4 syntax is
                // font://"+FontAwesome.valueOf(foo) eg. "font://AMBULANCE"
                final String iconName = (value.split("://", 2))[1];

                try {
                    return FontAwesome.valueOf(iconName);
                } catch (IllegalArgumentException iae) {
                    throw new ConversionException("Unknown FontIcon constant: "
                            + iconName, iae);
                }
            }

            @Override
            public String format(Resource value)
                    throws Converter.ConversionException {
                throw new UnsupportedOperationException("Use "
                        + ResourceConverterByProtocol.FONTICON.toString()
                        + " instead");
            }
        },
        FILE {
            @Override
            public Resource parse(String value) {
                return new FileResource(new File(value.split("://")[1]));
            }

            @Override
            public String format(Resource value)
                    throws Converter.ConversionException {
                String path = ((FileResource) value).getSourceFile().getPath();
                if (File.separatorChar != '/') {
                    // make sure we use '/' as file separator in templates
                    return path.replace(File.separatorChar, '/');
                } else {
                    return path;
                }
            }

        };

        @Override
        public Resource parse(String value) {
            // default behavior for HTTP, HTTPS, FTP and FTPS
            return new ExternalResource(value);
        }

        @Override
        public String format(Resource value)
                throws Converter.ConversionException {
            // default behavior for HTTP, HTTPS, FTP and FTPS
            return ((ExternalResource) value).getURL();
        }

        private static Map<Class<? extends Resource>, ResourceConverterByProtocol> typeToConverter = new HashMap<Class<? extends Resource>, ResourceConverterByProtocol>();
        static {
            typeToConverter.put(ExternalResource.class, HTTP);
            // ^ any of non-specialized would actually work
            typeToConverter.put(ThemeResource.class, THEME);
            typeToConverter.put(FontIcon.class, FONTICON);
            typeToConverter.put(FileResource.class, FILE);

        }

        public static ResourceConverterByProtocol byType(
                Class<? extends Resource> resourceType) {
            for (Class<?> type : typeToConverter.keySet()) {
                if (type.isAssignableFrom(resourceType)) {
                    return typeToConverter.get(type);
                }
            }
            return null;
        }
    }

}
