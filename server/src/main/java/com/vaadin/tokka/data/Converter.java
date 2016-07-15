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

package com.vaadin.tokka.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com.vaadin.tokka.data.util.Result;

/**
 * A bidirectional converter of values between two types. The
 * <i>presentation</i> type is assumed to be used for input and output in the
 * user interface, and the <i>model</i> type is understood to be the
 * corresponding type used on the data model layer. In general, some values of
 * the presentation type might not be convertible to the model type, so the
 * {@code Converter} interface is not quite symmetrical.
 * <p>
 * Unless otherwise specified, {@code Converter} method arguments cannot be
 * null.
 * 
 * @author Vaadin Ltd.
 *
 * @param <P>
 *            the presentation value type
 * @param <M>
 *            the model value type
 */
public interface Converter<P, M> extends Serializable {

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
     * function. The {@code toPresentation} function should always succeed.
     * <p>
     * For example, the following converter converts between strings and
     * integers:
     * 
     * <pre>
     * Converter&lt;String, Integer&gt; c = Converter.from(
     *         String::valueOf, Integer::valueOf,
     *         e -> "value is not a valid number");
     * </pre>
     * 
     * @param <P>
     *            the presentation type
     * @param <M>
     *            the model type
     * @param toModel
     *            the function to convert to model, not null
     * @param toPresentation
     *            the function to convert to presentation, not null
     * @param onError
     *            the function to provide error messages, not null
     * @return the new converter
     * 
     * @see Result
     * @see Function
     */
    public static <P, M> Converter<P, M> from(Function<P, M> toModel,
            Function<M, P> toPresentation,
            Function<Exception, String> onError) {

        Objects.requireNonNull(toModel, "toModel cannot be null");
        Objects.requireNonNull(toPresentation, "toPresentation cannot be null");
        Objects.requireNonNull(onError, "onError cannot be null");

        return from(val -> Result.of(() -> toModel.apply(val), onError),
                toPresentation);
    }

    /**
     * Constructs a converter from two functions. The {@code toModel} function
     * returns a {@code Result} object to represent the success or failure of
     * the conversion. The {@code toPresentation} function should always
     * succeed.
     * <p>
     * For example, the following converter converts between strings and
     * integers:
     * 
     * <pre>
     * Converter<String, Integer> stringToInt = Converter.from(
     *         str -> {
     *             try {
     *                 return Result.ok(Integer.valueOf(str));
     *             } catch (NumberFormatException e) {
     *                 return Result.error("not a valid number: " + str);
     *             }
     *         },
     *         String::valueOf);
     * </pre>
     * 
     * @param <P>
     *            the presentation type
     * @param <M>
     *            the model type
     * @param toModel
     *            the function to convert to model, not null
     * @param toPresentation
     *            the function to convert to presentation, not null
     * @return the new converter
     * 
     * @see Result
     * @see Function
     * @see Result#of(Supplier, Function)
     */
    public static <P, M> Converter<P, M> from(Function<P, Result<M>> toModel,
            Function<M, P> toPresentation) {
        Objects.requireNonNull(toModel, "toModel cannot be null");
        Objects.requireNonNull(toPresentation, "toPresentation cannot be null");

        return new Converter<P, M>() {

            @Override
            public Result<M> toModel(P t) {
                return toModel.apply(t);
            }

            @Override
            public P toPresentation(M u) {
                return toPresentation.apply(u);
            }
        };
    }

    /**
     * Converts a value from the presentation type to the model type. Because
     * this conversion can typically fail, the method returns a {@code Result}
     * object instead of the plain value.
     * <p>
     * This method should never throw exceptions in case of invalid input, only
     * in actual exceptional conditions.
     * <p>
     * The behavior in case of a null value is implementation-defined and should
     * be documented.
     * 
     * @param value
     *            the value to convert, null handling is implementation-defined
     * @return the result of the conversion
     * 
     * @see #toPresentation(M)
     * @see Result
     * @see Validator
     */
    public Result<M> toModel(P value);

    /**
     * Converts a value from the model type to the presentation type. Unlike
     * {@link #toModel(Object) toModel}, the conversion is expected not to fail;
     * a failure should imply either a bug in the application logic or invalid
     * data in the model.
     * <p>
     * The behavior in case of a null value is implementation-defined and should
     * be documented.
     * 
     * @param value
     *            the value to convert, null handling is implementation-defined
     * @return the converted value
     * 
     * @see #toModel(P)
     */
    public P toPresentation(M value);

    /**
     * Returns a converter that chains together this converter with the given
     * type-compatible converter. In the presentation-to-model direction, the
     * other converter is applied to the result of this converter, and vice
     * versa in the other direction.
     * 
     * @param <T>
     *            the model type of the resulting converter
     * @param other
     *            the converter to chain, not null
     * @return a chained converter
     */
    public default <T> Converter<P, T> chain(Converter<M, T> other) {
        return Converter.from(
                (P value) -> this.toModel(value).flatMap(other::toModel),
                (T value) -> this.toPresentation(other.toPresentation(value)));
    }
}
