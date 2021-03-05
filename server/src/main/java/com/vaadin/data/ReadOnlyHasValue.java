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

import com.vaadin.server.SerializableConsumer;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Label;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Generic {@link HasValue} to use any type of component with Vaadin data
 * binding.
 * <p>
 * Example:
 *
 * <pre>
 * Label label = new Label();
 * ReadOnlyHasValue&lt;String&gt; hasValue = new ReadOnlyHasValue&lt;&gt;(
 *         label::setCaption);
 * binder.forField(hasValue).bind(SomeBean::getName);
 * </pre>
 *
 * @param <V>
 *            the value type
 * @since 8.4
 */
public class ReadOnlyHasValue<V> implements HasValue<V>, Serializable {
    private V value;
    private final SerializableConsumer<V> valueProcessor;
    private final V emptyValue;
    private LinkedHashSet<ValueChangeListener<V>> listenerList;

    /**
     * Creates new {@code ReadOnlyHasValue}.
     *
     * @param valueProcessor
     *            the value valueProcessor, e.g. {@link Label#setValue}
     * @param emptyValue
     *            the value to be used as empty, {@code null} by default
     */
    public ReadOnlyHasValue(SerializableConsumer<V> valueProcessor,
            V emptyValue) {
        this.valueProcessor = valueProcessor;
        this.emptyValue = emptyValue;
    }

    /**
     * Creates new {@code ReadOnlyHasValue} with {@code null} as an empty value.
     *
     * @param valueProcessor
     *            the value valueProcessor, e.g. {@link Label#setValue}
     */
    public ReadOnlyHasValue(SerializableConsumer<V> valueProcessor) {
        this(valueProcessor, null);
    }

    @Override
    public void setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        valueProcessor.accept(value);
        if (listenerList != null && !Objects.equals(oldValue, value)) {
            for (ValueChangeListener<V> valueChangeListener : listenerList) {
                valueChangeListener.valueChange(
                        new ValueChangeEvent<>(null, this, oldValue, false));
            }
        }
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<V> listener) {
        Objects.requireNonNull(listener, "Listener must not be null.");
        if (listenerList == null) {
            listenerList = new LinkedHashSet<>();
        }
        listenerList.add(listener);

        return () -> listenerList.remove(listener);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        if (requiredIndicatorVisible)
            throw new IllegalArgumentException("Not Writable");
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (!readOnly)
            throw new IllegalArgumentException("Not Writable");
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public V getEmptyValue() {
        return emptyValue;
    }
}
