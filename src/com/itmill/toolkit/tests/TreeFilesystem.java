/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.io.File;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.demo.util.SampleDirectory;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Tree.ExpandEvent;

/**
 * Browsable file explorer using Toolkit Tree component. Demonstrates: how to
 * add items hierarchially into
 * <code>com.itmill.toolkit.ui.Component.Tree</code>, how to receive ExpandEvent
 * and implement <code>com.itmill.toolkit.ui.Tree.ExpandListener</code>.
 * 
 * @since 4.0.0
 * 
 */
public class TreeFilesystem extends com.itmill.toolkit.Application implements
        Tree.ExpandListener {

    // Filesystem explorer panel and it's components
    private final Panel explorerPanel = new Panel("Filesystem explorer");

    private final Tree tree = new Tree();

    public void init() {
        final Window main = new Window("Tree filesystem demo");
        setMainWindow(main);

        // Main window contains heading and panel
        main.addComponent(new Label("<h2>Tree demo</h2>", Label.CONTENT_XHTML));

        // configure file structure panel
        main.addComponent(explorerPanel);
        explorerPanel.addComponent(tree);
        explorerPanel.setHeight(400);

        // "this" handles tree's expand event
        tree.addListener(this);

        // Get sample directory
        final File sampleDir = SampleDirectory.getDirectory(this);
        // populate tree's root node with example directory
        if (sampleDir != null) {
            populateNode(sampleDir.getAbsolutePath(), null);
        }
    }

    /**
     * Handle tree expand event, populate expanded node's childs with new files
     * and directories.
     */
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
