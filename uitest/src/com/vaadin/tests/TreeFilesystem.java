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

import com.vaadin.data.Item;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.util.SampleDirectory;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.VerticalLayout;

/**
 * Browsable file explorer using Vaadin Tree component. Demonstrates: how to add
 * items hierarchically into <code>com.vaadin.ui.Component.Tree</code>, how to
 * receive ExpandEvent and implement
 * <code>com.vaadin.ui.Tree.ExpandListener</code>.
 * 
 * @since 4.0.0
 * 
 */
public class TreeFilesystem extends com.vaadin.server.LegacyApplication
        implements Tree.ExpandListener {

    // Filesystem explorer panel and it's components
    private final Panel explorerPanel = new Panel("Filesystem explorer");

    private final Tree tree = new Tree();

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow("Tree filesystem demo");
        setMainWindow(main);

        // Main window contains heading and panel
        main.addComponent(new Label("<h2>Tree demo</h2>", ContentMode.HTML));

        // configure file structure panel
        VerticalLayout explorerLayout = new VerticalLayout();
        explorerLayout.setMargin(true);
        explorerPanel.setContent(explorerLayout);
        main.addComponent(explorerPanel);
        explorerLayout.addComponent(tree);
        explorerPanel.setHeight("400px");

        // "this" handles tree's expand event
        tree.addListener(this);

        // Get sample directory
        final File sampleDir = SampleDirectory.getDirectory(
                VaadinSession.getCurrent(), main);
        // populate tree's root node with example directory
        if (sampleDir != null) {
            populateNode(sampleDir.getAbsolutePath(), null);
        }
    }

    /**
     * Handle tree expand event, populate expanded node's childs with new files
     * and directories.
     */
    @Override
    public void nodeExpand(ExpandEvent event) {
        final Item i = tree.getItem(event.getItemId());
        if (!tree.hasChildren(i)) {
            // populate tree's node which was expanded
            populateNode(event.getItemId().toString(), event.getItemId());
        }
    }

    /**
     * Populates files to tree as items. In this example items are of String
     * type that consist of file path. New items are added to tree and item's
     * parent and children properties are updated.
     * 
     * @param file
     *            path which contents are added to tree
     * @param parent
     *            for added nodes, if null then new nodes are added to root node
     */
    private void populateNode(String file, Object parent) {
        final File subdir = new File(file);
        final File[] files = subdir.listFiles();
        for (int x = 0; x < files.length; x++) {
            try {
                // add new item (String) to tree
                final String path = files[x].getCanonicalPath().toString();
                tree.addItem(path);
                // set parent if this item has one
                if (parent != null) {
                    tree.setParent(path, parent);
                }
                // check if item is a directory and read access exists
                if (files[x].isDirectory() && files[x].canRead()) {
                    // yes, childrens therefore exists
                    tree.setChildrenAllowed(path, true);
                } else {
                    // no, childrens therefore do not exists
                    tree.setChildrenAllowed(path, false);
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
