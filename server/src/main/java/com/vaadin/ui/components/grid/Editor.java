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

import java.io.Serializable;

import com.vaadin.data.Binder;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;

/**
 * An editor in a Grid.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 */
public interface Editor<T> extends Serializable {

    /**
     * Sets the underlying Binder to this Editor.
     *
     * @param binder
     *            the binder for updating editor fields; not {@code null}
     * @return this editor
     */
    public Editor<T> setBinder(Binder<T> binder);

    /**
     * Returns the underlying Binder from Editor.
     *
     * @return the binder; not {@code null}
     */
    public Binder<T> getBinder();

    /**
     * Sets the Editor buffered mode. When the editor is in buffered mode, edits
     * are only committed when the user clicks the save button. In unbuffered
     * mode valid changes are automatically committed.
     *
     * @param buffered
     *            {@code true} if editor should be buffered; {@code false} if
     *            not
     * @return this editor
     */
    public Editor<T> setBuffered(boolean buffered);

    /**
     * Enables or disabled the Editor. A disabled editor cannot be opened.
     *
     * @param enabled
     *            {@code true} if editor should be enabled; {@code false} if not
     * @return this editor
     */
    public Editor<T> setEnabled(boolean enabled);

    /**
     * Returns whether Editor is buffered or not.
     *
     * @see #setBuffered(boolean)
     *
     * @return {@code true} if editor is buffered; {@code false} if not
     */
    public boolean isBuffered();

    /**
     * Returns whether Editor is enabled or not.
     *
     * @return {@code true} if editor is enabled; {@code false} if not
     */
    public boolean isEnabled();

    /**
     * Returns whether Editor is open or not.
     *
     * @return {@code true} if editor is open; {@code false} if not
     */
    public boolean isOpen();

    /**
     * Saves any changes from the Editor fields to the edited bean.
     *
     * @return {@code true} if save succeeded; {@code false} if not
     */
    public boolean save();

    /**
     * Close the editor discarding any unsaved changes.
     */
    public void cancel();

    /**
     * Sets the caption of the save button in buffered mode.
     *
     * @param saveCaption
     *            the save button caption
     * @return this editor
     */
    public Editor<T> setSaveCaption(String saveCaption);

    /**
     * Sets the caption of the cancel button in buffered mode.
     *
     * @param cancelCaption
     *            the cancel button caption
     * @return this editor
     */
    public Editor<T> setCancelCaption(String cancelCaption);

    /**
     * Gets the caption of the save button in buffered mode.
     *
     * @return the save button caption
     */
    public String getSaveCaption();

    /**
     * Gets the caption of the cancel button in buffered mode.
     *
     * @return the cancel button caption
     */
    public String getCancelCaption();

    /**
     * Sets the error message generator for this editor.
     * <p>
     * The default message is a concatenation of column field validation
     * failures and bean validation failures.
     *
     * @param errorGenerator
     *            the function to generate error messages; not {@code null}
     * @return this editor
     *
     * @see EditorErrorGenerator
     */
    public Editor<T> setErrorGenerator(EditorErrorGenerator<T> errorGenerator);

    /**
     * Gets the error message generator of this editor.
     *
     * @return the function that generates error messages; not {@code null}
     *
     * @see EditorErrorGenerator
     */
    public EditorErrorGenerator<T> getErrorGenerator();

    /**
     * Adds an editor save {@code listener}.
     * 
     * @param listener
     *            save listener
     * @return a registration object for removing the listener
     */
    public Registration addSaveListener(EditorSaveListener<T> listener);

    /**
     * Adds an editor cancel {@code listener}.
     * 
     * @param listener
     *            cancel listener
     * @return a registration object for removing the listener
     */
    public Registration addCancelListener(EditorCancelListener<T> listener);

    /**
     * Gets the Grid instance which this editor belongs to.
     * 
     * @return the grid which owns the editor
     */
    public Grid<T> getGrid();
}
