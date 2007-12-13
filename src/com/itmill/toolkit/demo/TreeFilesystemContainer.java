/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import java.io.File;

import com.itmill.toolkit.data.util.FilesystemContainer;
import com.itmill.toolkit.data.util.FilesystemContainer.FileItem;
import com.itmill.toolkit.demo.util.SampleDirectory;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Component.Event;
import com.itmill.toolkit.ui.Component.Listener;

/**
 * Browsable file explorer using Toolkit Tree component. Demonstrates: how to
 * use <code>com.itmill.toolkit.ui.Component.Tree</code> datasource container,
 * how to create <code>com.itmill.toolkit.data.util.FilesystemContainer</code>,
 * how to read <code>com.itmill.toolkit.ui.Component.Event</code> objects, how
 * to receive and handle any event by implementing
 * <code>com.itmill.toolkit.ui.Component.Listener</code>.
 * 
 * @since 4.0.0
 * 
 */
public class TreeFilesystemContainer extends com.itmill.toolkit.Application
        implements Listener {

    // Filesystem explorer panel and it's components
    private final Panel explorerPanel = new Panel("Filesystem explorer");

    private final Tree filesystem = new Tree();

    // File properties panel and it's components
    private final Panel propertyPanel = new Panel("File properties");

    private final Label fileProperties = new Label();

    public void init() {
        final Window w = new Window("Tree FilesystemContainer demo");
        setMainWindow(w);
        final ExpandLayout main = new ExpandLayout();
        w.setLayout(main);
        main.setMargin(true);
        main.setSpacing(true);

        propertyPanel.getSize().setHeight(120);
        main.addComponent(propertyPanel);
        explorerPanel.getSize().setHeight(100);
        explorerPanel.getSize().setHeightUnits(Sizeable.UNITS_PERCENTAGE);
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
