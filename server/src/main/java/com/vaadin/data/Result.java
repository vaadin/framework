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

package com.vaadin.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializableSupplier;

/**
 * Represents the result of an operation that might fail, such as type
 * conversion. A result may contain either a value, signifying a successful
 * operation, or an error message in case of a failure.
 * <p>
 * Result instances are created using the factory methods {@link #ok(R)} and
 * {@link #error(String)}, denoting success and failure respectively.
 * <p>
 * Unless otherwise specified, {@code Result} method arguments cannot be null.
 *
 * @param <R>
 *            the result value type
 *
 * @since 8.0
 */
public interface Result<R> extends Serializable {

    /**
     * Returns a successful result wrapping the given value.
     *
     * @param <R>
     *            the result value type
     * @param value
     *            the result value, can be null
     * @return a successful result
     */
    public static <R> Result<R> ok(R value) {
        return new SimpleResult<>(value, null);
    }

    /**
     * Returns a failure result wrapping the given error message.
     *
     * @param <R>
     *            the result value type
     * @param message
     *            the error message
     * @return a failure result
     */
    public static <R> Result<R> error(String message) {
        Objects.requireNonNull(message, "message cannot be null");
        return new SimpleResult<>(null, message);
    }

    /**
     * Returns a Result representing the result of invoking the given supplier.
     * If the supplier returns a value, returns a {@code Result.ok} of the
     * value; if an exception is thrown, returns the message in a
     * {@code Result.error}.
     *
     * @param <R>
     *            the result value type
     * @param supplier
     *            the supplier to run
     * @param onError
     *            the function to provide the error message
     * @return the result of invoking the supplier
     */
    public static <R> Result<R> of(SerializableSupplier<R> supplier,
            SerializableFunction<Exception, String> onError) {
        Objects.requireNonNull(supplier, "supplier cannot be null");
        Objects.requireNonNull(onError, "onError cannot be null");

        try {
            return ok(supplier.get());
        } catch (Exception e) {
            return error(onError.apply(e));
        }
    }

    /**
     * If this Result has a value, returns a Result of applying the given
     * function to the value. Otherwise, returns a Result bearing the same error
     * as this one. Note that any exceptions thrown by the mapping function are
     * not wrapped but allowed to propagate.
     *
     * @param <S>
     *            the type of the mapped value
     * @param mapper
     *            the mapping function
     * @return the mapped result
     */
    public default <S> Result<S> map(SerializableFunction<R, S> mapper) {
        return flatMap(value -> ok(mapper.apply(value)));
    }

    /**
     * If this Result has a value, applies the given Result-returning function
     * to the value. Otherwise, returns a Result bearing the same error as this
     * one. Note that any exceptions thrown by the mapping function are not
     * wrapped but allowed to propagate.
     *
     * @param <S>
     *            the type of the mapped value
     * @param mapper
     *            the mapping function
     * @return the mapped result
     */
    public <S> Result<S> flatMap(SerializableFunction<R, Result<S>> mapper);

    /**
     * Invokes either the first callback or the second one, depending on whether
     * this Result denotes a success or a failure, respectively.
     *
     * @param ifOk
     *            the function to call if success
     * @param ifError
     *            the function to call if failure
     */
    public void handle(SerializableConsumer<R> ifOk,
            SerializableConsumer<String> ifError);

    /**
     * Applies the {@code consumer} if result is not an error.
     *
     * @param consumer
     *            consumer to apply in case it's not an error
     */
    public default void ifOk(SerializableConsumer<R> consumer) {
        handle(consumer, error -> {
        });
    }

    /**
     * Applies the {@code consumer} if result is an error.
     *
     * @param consumer
     *            consumer to apply in case it's an error
     */
    public default void ifError(SerializableConsumer<String> consumer) {
        handle(value -> {
        }, consumer);
    }

    /**
     * Checks if the result denotes an error.
     *
     * @return <code>true</code> if the result denotes an error,
     *         <code>false</code> otherwise
     */
    public boolean isError();

    /**
     * Returns an Optional of the result message, or an empty Optional if none.
     *
     * @return the optional message
     */
    public Optional<String> getMessage();

    /**
     * Return the value, if the result denotes success, otherwise throw an
     * exception to be created by the provided supplier.
     *
     * @param <X>
     *            Type of the exception to be thrown
     * @param exceptionProvider
     *            The provider which will return the exception to be thrown
     *            based on the given error message
     * @return the value
     * @throws X
     *             if this result denotes an error
     */
    public <X extends Throwable> R getOrThrow(
            SerializableFunction<String, ? extends X> exceptionProvider)
            throws X;
}
