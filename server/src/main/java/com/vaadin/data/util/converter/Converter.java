/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import java.util.Locale;
import java.util.function.Function;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Result;
import com.vaadin.legacy.data.util.converter.LegacyConverter.ConversionException;

/**
 * Interface that implements conversion between a model and a presentation type.
 * <p>
 * Converters must not have any side effects (never update UI from inside a
 * converter).
 *
 * @param <PRESENTATION>
 *            The presentation type.
 * @param <MODEL>
 *            The model type.
 * @author Vaadin Ltd.
 * @since 8.0
 */
public interface Converter<PRESENTATION, MODEL> extends Serializable {

    /**
     * Converts the given value from model type to presentation type.
     * <p>
     * A converter can optionally use locale to do the conversion.
     *
     * @param value
     *            The value to convert. Can be null
     * @param locale
     *            The locale to use for conversion. Can be null.
     * @return The converted value compatible with the source type
     */
    public Result<MODEL> convertToModel(PRESENTATION value, Locale locale);

    /**
     * Converts the given value from presentation type to model type.
     * <p>
     * A converter can optionally use locale to do the conversion.
     *
     * @param value
     *            The value to convert. Can be null
     * @param locale
     *            The locale to use for conversion. Can be null.
     * @return The converted value compatible with the source type
     */
    public PRESENTATION convertToPresentation(MODEL value, Locale locale);

    /**
     * Returns a converter that returns its input as-is in both directions.
     *
     * @param <T>
     *            the input and output type
     * @return an identity converter
     */
    public static <T> Converter<T, T> identity() {
        return from(t -> Result.ok(t), t -> t);
    }

    /**
     * Constructs a converter from two functions. Any {@code Exception}
     * instances thrown from the {@code toModel} function are converted into
     * error-bearing {@code Result} objects using the given {@code onError}
     * function.
     *
     * @param <P>
     *            the presentation type
     * @param <M>
     *            the model type
     * @param toModel
     *            the function to convert to model
     * @param toPresentation
     *            the function to convert to presentation
     * @param onError
     *            the function to provide error messages
     * @return the new converter
     *
     * @see Result
     * @see Function
     */
    public static <P, M> Converter<P, M> from(Function<P, M> toModel,
            Function<M, P> toPresentation,
            Function<Exception, String> onError) {

        return from(val -> Result.of(() -> toModel.apply(val), onError),
                toPresentation);
    }

    /**
     * Constructs a converter from a filter and a function.
     *
     * @param <P>
     *            the presentation type
     * @param <M>
     *            the model type
     * @param toModel
     *            the function to convert to model
     * @param toPresentation
     *            the function to convert to presentation
     * @return the new converter
     *
     * @see Function
     */
    public static <P, M> Converter<P, M> from(Function<P, Result<M>> toModel,
            Function<M, P> toPresentation) {
        return new Converter<P, M>() {

            @Override
            public Result<M> convertToModel(P value, Locale locale)
                    throws ConversionException {
                return toModel.apply(value);
            }

            @Override
            public P convertToPresentation(M value, Locale locale)
                    throws ConversionException {
                return toPresentation.apply(value);
            }
        };
    }

    /**
     * Returns a converter that chains together this converter with the given
     * type-compatible converter.
     * <p>
     * The chained converters will form a new converter capable of converting
     * from the presentation type of this converter to the model type of the
     * other converter.
     * <p>
     * In most typical cases you should not need this method but instead only
     * need to define one converter for a binding using
     * {@link Binding#withConverter(Converter)}.
     *
     * @param <T>
     *            the model type of the resulting converter
     * @param other
     *            the converter to chain, not null
     * @return a chained converter
     */
    public default <T> Converter<PRESENTATION, T> chain(
            Converter<MODEL, T> other) {
        return new Converter<PRESENTATION, T>() {
            @Override
            public Result<T> convertToModel(PRESENTATION value, Locale locale) {
                Result<MODEL> model = Converter.this.convertToModel(value,
                        locale);
                return model.flatMap(v -> other.convertToModel(v, locale));
            }

            @Override
            public PRESENTATION convertToPresentation(T value, Locale locale) {
                MODEL model = other.convertToPresentation(value, locale);
                return Converter.this.convertToPresentation(model, locale);
            }
        };
    }
}
