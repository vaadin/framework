package com.vaadin.tests.containers.filesystemcontainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Ordered;
import com.vaadin.v7.data.util.FilesystemContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TreeTable;

public class FileSystemContainerInTreeTable extends TestBase {

    private Log log = new Log(5);
    private TreeTable treeTable;

    @Override
    protected void setup() {
        setTheme("reindeer-tests");

        final File folder;
        try {
            File tempFile = File.createTempFile("fsc-tt", "");
            tempFile.delete();
            folder = new File(tempFile.getParent(), tempFile.getName());
            folder.mkdir();
            System.out.println(folder.getPath());
            folder.deleteOnExit();

            populate(folder, 3, 10);

            FilesystemContainer fsc = new FilesystemContainer(folder);

            treeTable = new TreeTable();
            treeTable.addStyleName("table-equal-rowheight");
            treeTable.setWidth("450px");
            treeTable.setHeight("550px");
            treeTable.setContainerDataSource(fsc);
            treeTable.setItemIconPropertyId(FilesystemContainer.PROPERTY_ICON);
            treeTable.setVisibleColumns("Name");
            treeTable.setColumnWidth("Name", 400);
            treeTable.addExpandListener(
                    event -> logExpandCollapse(event.getItemId(), "expanded"));
            treeTable.addCollapseListener(
                    event -> logExpandCollapse(event.getItemId(), "collapsed"));

            addComponent(log);
            addComponent(treeTable);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setSpacing(true);
            buttonLayout.addComponent(new Button("Create dir11", event -> {
                new File(folder, "dir11").mkdir();
                log.log("Row dir11 created");
            }));
            buttonLayout.addComponent(new Button("Delete dir11", event -> {
                new File(folder, "dir11").delete();
                log.log("Row dir11 deleted");
            }));
            // to clean up explicitly before ending an automated test
            buttonLayout.addComponent(
                    new Button("Clean all files", event -> folder.delete()));
            addComponent(buttonLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populate(File folder, int subDirectories, int files)
            throws IOException {
        for (int i = 1; i <= files; i++) {
            File f = new File(folder, "file" + i + ".txt");
            f.createNewFile();
        }

        for (int i = 1; i <= subDirectories; i++) {
            File f = new File(folder, "dir" + i);
            f.mkdir();
            populate(f, 0, 2);
        }
    }

    protected int indexOfId(Table source, Object itemId) {
        Container.Ordered c = (Ordered) source.getContainerDataSource();
        if (c instanceof Container.Indexed) {
            return ((Container.Indexed) source).indexOfId(itemId);
        } else {
            List<Object> list = new ArrayList<>(source.getItemIds());
            return list.indexOf(itemId);
        }
    }

    protected void logExpandCollapse(Object itemId, String operation) {
        File file = (File) itemId;
        // do not use the variable part (path) of file name
        log.log("Row " + file.getName() + " " + operation + ". Row index: "
                + indexOfId(treeTable, itemId));

    }

    @Override
    protected String getDescription() {
        return "TreeTable partial updates can only be used with a container that notifies the TreeTable of item set changes";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7837;
    }

}
