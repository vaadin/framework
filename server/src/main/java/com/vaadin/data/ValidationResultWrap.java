/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializableFunction;

/**
 * Internal implementation of a {@code Result} that collects all possible
 * ValidationResults into one list. This class intercepts the normal chaining of
 * Converters and Validators, catching and collecting results.
 *
 * @param <R>
 *            the result data type
 *
 * @since 8.2
 */
class ValidationResultWrap<R> implements Result<R> {

    private final List<ValidationResult> resultList;
    private final Result<R> wrappedResult;

    ValidationResultWrap(Result<R> result, List<ValidationResult> resultList) {
        this.resultList = resultList;
        this.wrappedResult = result;
    }

    ValidationResultWrap(R value, ValidationResult result) {
        if (result.isError()) {
            wrappedResult = new SimpleResult<>(null, result.getErrorMessage());
        } else {
            wrappedResult = new SimpleResult<>(value, null);
        }
        this.resultList = new ArrayList<>();
        this.resultList.add(result);
    }

    List<ValidationResult> getValidationResults() {
        return Collections.unmodifiableList(resultList);
    }

    Result<R> getWrappedResult() {
        return wrappedResult;
    }

    @Override
    public <S> Result<S> flatMap(SerializableFunction<R, Result<S>> mapper) {
        Result<S> result = wrappedResult.flatMap(mapper);
        if (!(result instanceof ValidationResultWrap)) {
            return new ValidationResultWrap<S>(result, resultList);
        }

        List<ValidationResult> currentResults = new ArrayList<>(resultList);
        ValidationResultWrap<S> resultWrap = (ValidationResultWrap<S>) result;
        currentResults.addAll(resultWrap.getValidationResults());

        return new ValidationResultWrap<>(resultWrap.getWrappedResult(),
                currentResults);
    }

    @Override
    public void handle(SerializableConsumer<R> ifOk,
            SerializableConsumer<String> ifError) {
        wrappedResult.handle(ifOk, ifError);
    }

    @Override
    public boolean isError() {
        return wrappedResult.isError();
    }

    @Override
    public Optional<String> getMessage() {
        return wrappedResult.getMessage();
    }

    @Override
    public <X extends Throwable> R getOrThrow(
            SerializableFunction<String, ? extends X> exceptionProvider)
            throws X {
        return wrappedResult.getOrThrow(exceptionProvider);
    }

}
