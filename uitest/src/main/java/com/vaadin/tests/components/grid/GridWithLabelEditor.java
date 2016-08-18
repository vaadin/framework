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
package com.vaadin.tests.components.grid;

import java.util.Collection;

import com.vaadin.legacy.data.Validator;
import com.vaadin.legacy.data.Validator.InvalidValueException;
import com.vaadin.legacy.ui.LegacyField;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

public class GridWithLabelEditor extends AbstractTestUI {

    public class LabelEditor extends Label implements LegacyField<String> {

        @Override
        public void focus() {
            super.focus();
        }

        @Override
        public boolean isInvalidCommitted() {
            return false;
        }

        @Override
        public void setInvalidCommitted(boolean isCommitted) {
        }

        @Override
        public void commit() throws SourceException, InvalidValueException {
        }

        @Override
        public void discard() throws SourceException {
        }

        @Override
        public void setBuffered(boolean buffered) {
        }

        @Override
        public boolean isBuffered() {
            return false;
        }

        @Override
        public boolean isModified() {
            return false;
        }

        @Override
        public void addValidator(Validator validator) {
        }

        @Override
        public void removeValidator(Validator validator) {
        }

        @Override
        public void removeAllValidators() {
        }

        @Override
        public Collection<Validator> getValidators() {
            return null;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void validate() throws InvalidValueException {
        }

        @Override
        public boolean isInvalidAllowed() {
            return false;
        }

        @Override
        public void setInvalidAllowed(boolean invalidValueAllowed)
                throws UnsupportedOperationException {
        }

        @Override
        public int getTabIndex() {
            return -1;
        }

        @Override
        public void setTabIndex(int tabIndex) {
        }

        @Override
        public boolean isRequired() {
            return false;
        }

        @Override
        public void setRequired(boolean required) {
        }

        @Override
        public void setRequiredError(String requiredMessage) {
        }

        @Override
        public String getRequiredError() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void clear() {
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        addComponent(grid);

        grid.setEditorEnabled(true);
        grid.addColumn("Foo", String.class).setEditorField(new LabelEditor());
        grid.addRow("FooFoo");

        grid.editItem(grid.getContainerDataSource().firstItemId());
    }

}
