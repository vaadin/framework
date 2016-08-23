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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An internal implementation of {@code Result}.
 *
 * @param <R>
 *            the result value type
 */
class SimpleResult<R> implements Result<R> {

    private final R value;
    private final String message;

    /**
     * Creates a new {@link Result} instance using {@code value} for a non error
     * {@link Result} and {@code message} for an error {@link Result}.
     * <p>
     * If {@code message} is null then {@code value} is ignored and result is an
     * error.
     *
     * @param value
     *            the value of the result, may be {@code null}
     * @param message
     *            the error message of the result, may be {@code null}
     */
    SimpleResult(R value, String message) {
        // value != null => message == null
        assert value == null
                || message == null : "Message must be null if value is provided";
        this.value = value;
        this.message = message;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> Result<S> flatMap(Function<R, Result<S>> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        if (isError()) {
            // Safe cast; valueless
            return (Result<S>) this;
        } else {
            return mapper.apply(value);
        }
    }

    @Override
    public void handle(Consumer<R> ifOk, Consumer<String> ifError) {
        Objects.requireNonNull(ifOk, "ifOk cannot be null");
        Objects.requireNonNull(ifError, "ifError cannot be null");
        if (isError()) {
            ifError.accept(message);
        } else {
            ifOk.accept(value);
        }
    }

    @Override
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    public boolean isError() {
        return message != null;
    }

    @Override
    public String toString() {
        if (isError()) {
            return "error(" + message + ")";
        } else {
            return "ok(" + value + ")";
        }
    }

}
