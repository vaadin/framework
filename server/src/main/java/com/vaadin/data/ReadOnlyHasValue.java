package com.vaadin.data;

import com.vaadin.server.SerializableConsumer;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Label;

import java.io.Serializable;

/**
 * Generic {@link HasValue} to use any type of component with Vaadin data binding.
 *
 * Example:
 * <pre>
 * {@code
 * Label label = new Label();
 * ReadOnlyHasValue<String> hasValue = new ReadOnlyHasValue<>(label::setCaption);
 * binder.forField(hasValue).bind(SomeBean::getName);
 * }
 * </pre>
 *
 * @param <V> the value type
 * @since
 */
public class ReadOnlyHasValue<V> implements HasValue<V>, Serializable {
    private V value;
    private final SerializableConsumer<V> setter;

    /**
     * Creates new {@code ReadOnlyHasValue}
     *
     * @param setter the value setter, e.g. {@link Label#setValue}
     */
    public ReadOnlyHasValue(SerializableConsumer<V> setter) {
        this.setter = setter;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
        setter.accept(value);
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<V> listener) {
        return () -> {
        };
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        if (requiredIndicatorVisible) throw new IllegalArgumentException("Not Writable");
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (!readOnly) throw new IllegalArgumentException("Not Writable");
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
