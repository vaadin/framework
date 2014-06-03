/* 
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tests;

import java.io.File;

import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.FilesystemContainer.FileItem;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.SampleDirectory;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * Browsable file explorer using Vaadin Tree component. Demonstrates: how to use
 * <code>com.vaadin.ui.Component.Tree</code> datasource container, how to create
 * <code>com.vaadin.data.util.FilesystemContainer</code>, how to read
 * <code>com.vaadin.ui.Component.Event</code> objects, how to receive and handle
 * any event by implementing <code>com.vaadin.ui.Component.Listener</code>.
 * 
 * @since 4.0.0
 * 
 */
public class TreeFilesystemContainer extends
        com.vaadin.server.LegacyApplication implements Listener {

    // Filesystem explorer panel and it's components
    private final Panel explorerPanel = new Panel("Filesystem explorer");

    private final Tree filesystem = new Tree();

    // File properties panel and it's components
    private final Panel propertyPanel = new Panel("File properties");

    private final Label fileProperties = new Label();

    @Override
    public void init() {
        final LegacyWindow w = new LegacyWindow("Tree FilesystemContainer demo");
        setMainWindow(w);
        final VerticalLayout main = new VerticalLayout();
        w.setContent(main);
        main.setMargin(true);
        main.setSpacing(true);

        propertyPanel.setHeight("120px");
        main.addComponent(propertyPanel);
        explorerPanel.setHeight("100%");
        main.addComponent(explorerPanel);
        main.setExpandRatio(explorerPanel, 1);

        // Explorer panel contains tree
        VerticalLayout explorerLayout = new VerticalLayout();
        explorerLayout.setMargin(true);
        explorerPanel.setContent(explorerLayout);
        explorerLayout.addComponent(filesystem);

        // Property panel contains label
        VerticalLayout propertyLayout = new VerticalLayout();
        propertyLayout.setMargin(true);
        propertyPanel.setContent(propertyLayout);
        propertyLayout.addComponent(fileProperties);
        fileProperties.setCaption("No file selected.");
        propertyPanel.setEnabled(false);

        // Get sample directory
        final File sampleDir = SampleDirectory.getDirectory(
                VaadinSession.getCurrent(), w);
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
    @Override
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
