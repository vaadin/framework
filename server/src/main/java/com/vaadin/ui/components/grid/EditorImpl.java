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
package com.vaadin.ui.components.grid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BinderValidationStatusHandler;
import com.vaadin.data.PropertySet;
import com.vaadin.event.EventRouter;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.editor.EditorClientRpc;
import com.vaadin.shared.ui.grid.editor.EditorServerRpc;
import com.vaadin.shared.ui.grid.editor.EditorState;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.Column;

import elemental.json.JsonObject;

/**
 * Implementation of {@code Editor} interface.
 *
 * @param <T>
 *            the grid bean type
 * @since 8.0
 */
public class EditorImpl<T> extends AbstractGridExtension<T>
        implements Editor<T> {

    private class EditorStatusHandler
            implements BinderValidationStatusHandler<T> {

        @Override
        public void statusChange(BinderValidationStatus<T> status) {
            boolean ok = status.isOk();
            if (saving) {
                rpc.confirmSave(ok);
                saving = false;
            }

            if (ok) {
                if (binder.getBean() != null) {
                    refresh(binder.getBean());
                }
                rpc.setErrorMessage(null, Collections.emptyList());
            } else {
                List<Component> fields = status.getFieldValidationErrors()
                        .stream().map(error -> error.getField())
                        .filter(columnFields.values()::contains)
                        .map(field -> (Component) field)
                        .collect(Collectors.toList());

                Map<Component, Column<T, ?>> fieldToColumn = new HashMap<>();
                columnFields.entrySet().stream()
                        .filter(entry -> fields.contains(entry.getValue()))
                        .forEach(entry -> fieldToColumn.put(entry.getValue(),
                                entry.getKey()));

                String message = errorGenerator.apply(fieldToColumn, status);

                List<String> columnIds = fieldToColumn.values().stream()
                        .map(column -> getInternalIdForColumn(column))
                        .collect(Collectors.toList());

                rpc.setErrorMessage(message, columnIds);
            }
        }

    }

    private Binder<T> binder;
    private Map<Column<T, ?>, Component> columnFields = new HashMap<>();
    private T edited;
    private boolean saving = false;
    private EditorClientRpc rpc;
    private EventRouter eventRouter = new EventRouter();

    private EditorErrorGenerator<T> errorGenerator = (fieldToColumn,
            status) -> {
        String message = status.getFieldValidationErrors().stream()
                .filter(e -> e.getMessage().isPresent()
                        && fieldToColumn.containsKey(e.getField()))
                .map(e -> fieldToColumn.get(e.getField()).getCaption() + ": "
                        + e.getMessage().get())
                .collect(Collectors.joining("; "));

        String beanMessage = status.getBeanValidationErrors().stream()
                .map(e -> e.getErrorMessage())
                .collect(Collectors.joining("; "));

        message = Stream.of(message, beanMessage).filter(s -> !s.isEmpty())
                .collect(Collectors.joining("; "));

        return message;
    };

    /**
     * Constructor for internal implementation of the Editor.
     *
     * @param propertySet
     *            the property set to use for configuring the default binder
     */
    public EditorImpl(PropertySet<T> propertySet) {
        rpc = getRpcProxy(EditorClientRpc.class);
        registerRpc(new EditorServerRpc() {

            @Override
            public void save() {
                saving = true;
                EditorImpl.this.save();
            }

            @Override
            public void cancel(boolean afterBeingSaved) {
                doCancel(afterBeingSaved);
            }

            @Override
            public void bind(String key) {
                // When in buffered mode, the editor is not allowed to move.
                // Binder with failed validation returns true for hasChanges.
                if (isOpen() && (isBuffered() || getBinder().hasChanges())) {
                    rpc.confirmBind(false);
                    return;
                }
                doClose();
                doEdit(getData(key));
                rpc.confirmBind(true);
            }
        });

        setBinder(Binder.withPropertySet(propertySet));
    }

    @Override
    public void generateData(T item, JsonObject jsonObject) {
    }

    @Override
    public Editor<T> setBinder(Binder<T> binder) {
        this.binder = binder;

        binder.setValidationStatusHandler(new EditorStatusHandler());
        return this;
    }

    @Override
    public Binder<T> getBinder() {
        return binder;
    }

    @Override
    public Editor<T> setBuffered(boolean buffered) {
        if (isOpen()) {
            throw new IllegalStateException(
                    "Cannot modify Editor when it is open.");
        }
        getState().buffered = buffered;

        return this;
    }

    @Override
    public Editor<T> setEnabled(boolean enabled) {
        if (isOpen()) {
            throw new IllegalStateException(
                    "Cannot modify Editor when it is open.");
        }
        getState().enabled = enabled;

        return this;
    }

    @Override
    public boolean isBuffered() {
        return getState(false).buffered;
    }

    @Override
    public boolean isEnabled() {
        return getState(false).enabled;
    }

    /**
     * Handles editor component generation and adding them to the hierarchy of
     * the Grid.
     *
     * @param bean
     *            the edited item; can't be {@code null}
     */
    protected void doEdit(T bean) {
        Objects.requireNonNull(bean, "Editor can't edit null");
        if (!isEnabled()) {
            throw new IllegalStateException(
                    "Editing is not allowed when Editor is disabled.");
        }

        if (!isBuffered()) {
            binder.setBean(bean);
        } else {
            binder.readBean(bean);
        }
        edited = bean;

        getParent().getColumns().stream().filter(Column::isEditable)
                .forEach(c -> {
                    Binding<T, ?> binding = c.getEditorBinding();

                    assert binding
                            .getField() instanceof Component : "Grid should enforce that the binding field is a component";

                    Component component = (Component) binding.getField();
                    addComponentToGrid(component);
                    columnFields.put(c, component);
                    getState().columnFields.put(getInternalIdForColumn(c),
                            component.getConnectorId());
                });
    }

    @Override
    public boolean save() {
        if (isOpen() && isBuffered()) {
            binder.validate();
            if (binder.writeBeanIfValid(edited)) {
                refresh(edited);
                eventRouter.fireEvent(new EditorSaveEvent<>(this, edited));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOpen() {
        return edited != null;
    }

    @Override
    public void cancel() {
        doCancel(false);
        rpc.cancel();
    }

    private void doCancel(boolean afterBeingSaved) {
        T editedBean = edited;
        doClose();
        if (!afterBeingSaved) {
            eventRouter.fireEvent(new EditorCancelEvent<>(this, editedBean));
        }
    }

    /**
     * Handles clean up for closing the Editor.
     */
    protected void doClose() {
        edited = null;

        for (Component c : columnFields.values()) {
            removeComponentFromGrid(c);
        }
        columnFields.clear();
        getState().columnFields.clear();
    }

    @Override
    public Editor<T> setSaveCaption(String saveCaption) {
        Objects.requireNonNull(saveCaption);
        getState().saveCaption = saveCaption;

        return this;
    }

    @Override
    public Editor<T> setCancelCaption(String cancelCaption) {
        Objects.requireNonNull(cancelCaption);
        getState().cancelCaption = cancelCaption;

        return this;
    }

    @Override
    public String getSaveCaption() {
        return getState(false).saveCaption;
    }

    @Override
    public String getCancelCaption() {
        return getState(false).cancelCaption;
    }

    @Override
    protected EditorState getState() {
        return getState(true);
    }

    @Override
    protected EditorState getState(boolean markAsDirty) {
        return (EditorState) super.getState(markAsDirty);
    }

    @Override
    public Editor<T> setErrorGenerator(EditorErrorGenerator<T> errorGenerator) {
        Objects.requireNonNull(errorGenerator, "Error generator can't be null");
        this.errorGenerator = errorGenerator;
        return this;
    }

    @Override
    public EditorErrorGenerator<T> getErrorGenerator() {
        return errorGenerator;
    }

    @Override
    public Registration addSaveListener(EditorSaveListener<T> listener) {
        return eventRouter.addListener(EditorSaveEvent.class, listener,
                EditorSaveListener.class.getDeclaredMethods()[0]);
    }

    @Override
    public Registration addCancelListener(EditorCancelListener<T> listener) {
        return eventRouter.addListener(EditorCancelEvent.class, listener,
                EditorCancelListener.class.getDeclaredMethods()[0]);
    }

    @Override
    public Grid<T> getGrid() {
        return getParent();
    }
}
