package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.table.TableItemDescriptionGeneratorUI;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableItemDescriptionGeneratorUI
        extends TableItemDescriptionGeneratorUI {

    @Override
    protected Table createTable() {
        return new TreeTable();
    }

}
