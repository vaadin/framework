package com.vaadin.v7.tests.components.grid;

import java.util.Collection;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Label;

public class GridWithLabelEditor extends AbstractReindeerTestUI {

    public class LabelEditor extends Label implements Field<String> {

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
