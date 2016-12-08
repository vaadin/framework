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
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BinderValidationStatusHandler;
import com.vaadin.shared.ui.grid.editor.EditorClientRpc;
import com.vaadin.shared.ui.grid.editor.EditorServerRpc;
import com.vaadin.shared.ui.grid.editor.EditorState;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.Editor;
import com.vaadin.ui.Grid.EditorErrorGenerator;

import elemental.json.JsonObject;

/**
 * Implementation of {@code Editor} interface.
 *
 * @param <T>
 *            the grid bean type
 */
public class EditorImpl<T> extends AbstractGridExtension<T>
        implements Editor<T> {

    private class EditorStatusHandler
            implements BinderValidationStatusHandler<T> {

        @Override
        public void accept(BinderValidationStatus<T> status) {
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
                        .map(Column::getId).collect(Collectors.toList());

                rpc.setErrorMessage(message, columnIds);
            }
        }

    }

    private Binder<T> binder;
    private Map<Column<T, ?>, Component> columnFields = new HashMap<>();
    private T edited;
    private boolean saving = false;
    private EditorClientRpc rpc;
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
     */
    public EditorImpl() {
        rpc = getRpcProxy(EditorClientRpc.class);
        registerRpc(new EditorServerRpc() {

            @Override
            public void save() {
                saving = true;
                EditorImpl.this.save();
            }

            @Override
            public void cancel() {
                doClose();
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

        setBinder(new Binder<>());
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
                    Component component = c.getEditorComponentGenerator()
                            .apply(edited);
                    addComponentToGrid(component);
                    columnFields.put(c, component);
                    getState().columnFields.put(c.getId(),
                            component.getConnectorId());
                });
    }

    @Override
    public boolean save() {
        if (isOpen() && isBuffered()) {
            binder.validate();
            if (binder.writeBeanIfValid(edited)) {
                refresh(edited);
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
        doClose();
        rpc.cancel();
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
}