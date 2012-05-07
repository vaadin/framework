package com.vaadin.tests.containers;

import java.io.File;

import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class TableWithFileSystemContainer extends AbstractTestCase {

    private String testPath = "C:/temp/img";

    @Override
    public void init() {
        setMainWindow(new Window(""));
        Table table = new Table("Documents", new FilesystemContainer(new File(
                testPath)));
        table.setWidth("100%");
        getMainWindow().addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "The Table uses a FileSystemContainer as datasource. Scrolling to the end should show the last items, not throw an NPE.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3864;
    }

}
