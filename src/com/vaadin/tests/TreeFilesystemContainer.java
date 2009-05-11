/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.io.File;

import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.FilesystemContainer.FileItem;
import com.vaadin.demo.util.SampleDirectory;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

/**
 * Browsable file explorer using Toolkit Tree component. Demonstrates: how to
 * use <code>com.vaadin.ui.Component.Tree</code> datasource container,
 * how to create <code>com.vaadin.data.util.FilesystemContainer</code>,
 * how to read <code>com.vaadin.ui.Component.Event</code> objects, how
 * to receive and handle any event by implementing
 * <code>com.vaadin.ui.Component.Listener</code>.
 * 
 * @since 4.0.0
 * 
 */
public class TreeFilesystemContainer extends com.vaadin.Application
        implements Listener {

    // Filesystem explorer panel and it's components
    private final Panel explorerPanel = new Panel("Filesystem explorer");

    private final Tree filesystem = new Tree();

    // File properties panel and it's components
    private final Panel propertyPanel = new Panel("File properties");

    private final Label fileProperties = new Label();

    @Override
    public void init() {
        final Window w = new Window("Tree FilesystemContainer demo");
        setMainWindow(w);
        final ExpandLayout main = new ExpandLayout();
        w.setLayout(main);
        main.setMargin(true);
        main.setSpacing(true);

        propertyPanel.setHeight(120);
        main.addComponent(propertyPanel);
        explorerPanel.setHeight(100);
        explorerPanel.setHeightUnits(Panel.UNITS_PERCENTAGE);
        main.addComponent(explorerPanel);
        main.expand(explorerPanel);

        // Explorer panel contains tree
        explorerPanel.addComponent(filesystem);

        // Property panel contains label
        propertyPanel.addComponent(fileProperties);
        fileProperties.setCaption("No file selected.");
        propertyPanel.setEnabled(false);

        // Get sample directory
        final File sampleDir = SampleDirectory.getDirectory(this);
        // Populate tree with FilesystemContainer
        final FilesystemContainer fsc = new FilesystemContainer(sampleDir, true);
        filesystem.setContainerDataSource(fsc);
        // "this" handles all filesystem events
        // e.g. node clicked, expanded etc.
        filesystem.addListener(this);
        // Value changes are immediate
        filesystem.setImmediate(true);
    }

    /**
     * Listener for any component events. This class has been registered as an
     * listener for component fsTree.
     */
    public void componentEvent(Event event) {
        // Check if event occured at fsTree component
        if (event.getSource() == filesystem) {
            // Check if event is about changing value
            if (event.getClass() == Field.ValueChangeEvent.class) {
                // Update property panel contents
                final FileItem fileItem = (FileItem) filesystem
                        .getItem(filesystem.getValue());
                fileProperties.setIcon(fileItem.getIcon());
                fileProperties.setCaption(fileItem.getName() + ", size "
                        + fileItem.getSize() + " bytes.");
                propertyPanel.setEnabled(true);
            }
            // here we could check for other type of events for filesystem
            // component
        }
        // here we could check for other component's events
    }

}
