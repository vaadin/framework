package com.vaadin.tests.components.select;

import java.util.LinkedHashMap;

import com.vaadin.ui.TwinColSelect;

public class TwinColSelectTest extends AbstractSelectTestCase<TwinColSelect> {

    private Command<TwinColSelect, Integer> rowsCommand = new Command<TwinColSelect, Integer>() {
        @Override
        public void execute(TwinColSelect c, Integer value, Object data) {
            c.setRows(value);
        }
    };

    private Command<TwinColSelect, Integer> colsCommand = new Command<TwinColSelect, Integer>() {
        @Override
        public void execute(TwinColSelect c, Integer value, Object data) {
            c.setColumns(value);
        }
    };

    private Command<TwinColSelect, String> leftColumnCaptionCommand = new Command<TwinColSelect, String>() {

        @Override
        public void execute(TwinColSelect c, String value, Object data) {
            c.setLeftColumnCaption(value);
        }
    };

    private Command<TwinColSelect, String> rightColumnCaptionCommand = new Command<TwinColSelect, String>() {

        @Override
        public void execute(TwinColSelect c, String value, Object data) {
            c.setRightColumnCaption(value);
        }
    };

    @Override
    protected Class<TwinColSelect> getTestClass() {
        return TwinColSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRowsAction(CATEGORY_FEATURES);
        createColsAction(CATEGORY_FEATURES);
        createCaptionActions(CATEGORY_FEATURES);
    }

    private void createRowsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Rows", category, options, "0", rowsCommand);
    }

    private void createColsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Columns", category, options, "0", colsCommand);
    }

    private void createCaptionActions(String category) {
        createSelectAction("Left column caption", category,
                createCaptionOptions(), "-", leftColumnCaptionCommand);
        createSelectAction("Right column caption", category,
                createCaptionOptions(), "-", rightColumnCaptionCommand);
    }
}
