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
package com.vaadin.data.util.converter;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

/**
 * A converter that converts from {@link String} to {@link Collection} of tokens
 * and back.
 * <p>
 * Allows to break a string into tokens using delimiter. Each token can be
 * converted to its own model using provided converter.
 * <p>
 * Default constructor uses <code>", "</code> as delimiter string and
 * {@link String} for token types. Other constructors allow to configure
 * delimiter and token types.
 * 
 * @since 7.5.0
 * 
 * @author Vaadin Ltd
 */
public class StringToCollectionConverter implements
        Converter<String, Collection> {

    private final String delimiter;
    private final Converter<String, ?> tokenConverter;
    private final Class<?> tokenType;
    private final CollectionFactory factory;

    /**
     * Creates converter with <code>", "</code> as delimiter and {@link String}
     * as token model type in collection.
     */
    public StringToCollectionConverter() {
        this(", ", null, String.class);
    }

    /**
     * Creates converter with given {@code delimiter} and {@link String} as
     * token model type in collection.
     * 
     * @param delimiter
     *            custom delimiter
     */
    public StringToCollectionConverter(String delimiter) {
        this(delimiter, null, String.class);
    }

    /**
     * Creates converter with given {@code tokenConverter} for convert tokens
     * and expected {@code tokenType}.
     * <p>
     * If {@code tokenConverter} is null then no conversation is done and
     * {@link String} is used as token type in resulting model collection.
     * 
     * @param tokenConverter
     *            converter for token
     * @param tokenType
     *            expected token model type
     */
    public StringToCollectionConverter(Converter<String, ?> tokenConverter,
            Class<?> tokenType) {
        this(", ", tokenConverter, tokenType);
    }

    /**
     * Creates converter with given {@code tokenConverter} for convert tokens
     * and expected {@code tokenType}.
     * <p>
     * If {@code tokenConverter} is null then no conversation is done and
     * {@link String} is used as token type in resulting model collection.
     * 
     * @param tokenConverter
     *            converter for token
     * @param tokenType
     *            expected token model type
     * @param delimiter
     *            delimiter in presentation string
     */
    public StringToCollectionConverter(String delimiter,
            Converter<String, ?> tokenConverter, Class<?> tokenClass) {
        this(delimiter, tokenConverter, tokenClass,
                new DefaultCollectionFactory());
    }

    /**
     * Creates converter with given {@code tokenConverter} for convert tokens
     * and expected {@code tokenType}.
     * <p>
     * If {@code tokenConverter} is null then no conversation is done and
     * {@link String} is used as token type in resulting model collection.
     * 
     * @param tokenConverter
     *            converter for token
     * @param tokenType
     *            expected token model type
     * @param delimiter
     *            delimiter in presentation string
     * @param factory
     *            factory to create resulting collection
     */
    public StringToCollectionConverter(String delimiter,
            Converter<String, ?> tokenConverter, Class<?> tokenClass,
            CollectionFactory factory) {
        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException(
                    "Delimiter should be non-empty string");
        }
        this.delimiter = delimiter;
        this.tokenConverter = tokenConverter;
        tokenType = tokenClass;
        this.factory = factory;
    }

    @Override
    public Class<Collection> getModelType() {
        return Collection.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    @Override
    public Collection convertToModel(String value,
            Class<? extends Collection> targetType, Locale locale)
            throws Converter.ConversionException {
        int index = value.indexOf(delimiter);
        int previous = 0;
        Collection result = factory.createCollection(targetType);
        Converter converter = tokenConverter;
        while (index != -1) {
            collectToken(value.substring(previous, index), result, converter,
                    locale);
            previous = index + delimiter.length();
            index = value.indexOf(delimiter, previous);
        }
        collectToken(value.substring(previous), result, converter, locale);
        return result;
    }

    @Override
    public String convertToPresentation(Collection value,
            Class<? extends String> targetType, Locale locale)
            throws Converter.ConversionException {
        StringBuilder builder = new StringBuilder();
        Converter converter = tokenConverter;
        for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
            if (converter == null) {
                builder.append(iterator.next());
            } else {
                builder.append(converter.convertToPresentation(iterator.next(),
                        targetType, locale));
            }
            builder.append(delimiter);
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - delimiter.length());
        } else {
            return builder.toString();
        }
    }

    private void collectToken(String token, Collection collection,
            Converter converter, Locale locale) {
        if (converter == null) {
            collection.add(token);
        } else {
            collection.add(converter.convertToModel(token, tokenType, locale));
        }
    }

    /**
     * Default collection factory implementation.
     * 
     * @author Vaadin Ltd
     */
    public static class DefaultCollectionFactory implements CollectionFactory {

        @Override
        public Collection<?> createCollection(Class<? extends Collection> type) {
            if (type.isAssignableFrom(ArrayList.class)) {
                return new ArrayList();
            } else if (type.isAssignableFrom(HashSet.class)) {
                return new HashSet();
            } else if (!type.isInterface()
                    && !Modifier.isAbstract(type.getModifiers())) {
                try {
                    return type.newInstance();
                } catch (InstantiationException ignore) {
                } catch (IllegalAccessException ignore) {
                }
            }
            return new ArrayList();
        }

    }

    /**
     * Collection factory. Defines a strategy to create collection by collection
     * class.
     * 
     * @author Vaadin Ltd
     */
    public interface CollectionFactory extends Serializable {

        /**
         * Create collection by its {@code type}.
         * 
         * @param type
         *            collection type
         * @return instantiated collection with given {@code type}
         */
        Collection<?> createCollection(Class<? extends Collection> type);
    }
}
