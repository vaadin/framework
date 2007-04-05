package com.itmill.toolkit.demo;

import java.io.File;

import com.itmill.toolkit.data.util.FilesystemContainer;
import com.itmill.toolkit.data.util.FilesystemContainer.FileItem;
import com.itmill.toolkit.demo.util.SampleDirectory;
import com.itmill.toolkit.ui.*;
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
	private Panel explorerPanel = new Panel("Filesystem explorer");

	private Tree filesystem = new Tree();

	// File properties panel and it's components
	private Panel propertyPanel = new Panel("File properties");

	private Label fileProperties = new Label();

	public void init() {
		Window main = new Window("Tree demo");
		setMainWindow(main);

		// set the application to use Corporate -theme
		setTheme("corporate");

		// Main window contains heading and two panels
		main.addComponent(new Label("<h3>TreeFilesystemContainer demo</h3>",
				Label.CONTENT_XHTML));
		main.addComponent(propertyPanel);
		main.addComponent(explorerPanel);

		// Explorer panel contains tree
		explorerPanel.addComponent(filesystem);
		explorerPanel.setWidth(500);

		// Property panel contains label
		propertyPanel.addComponent(fileProperties);
		fileProperties.setCaption("No file selected.");
		propertyPanel.setEnabled(false);
		propertyPanel.setWidth(500);

		// Get sample directory
		File sampleDir = SampleDirectory.getDirectory(this);
		// Populate tree with FilesystemContainer
		FilesystemContainer fsc = new FilesystemContainer(sampleDir, true);
		filesystem.setContainerDataSource(fsc);
		// "this" handles all filesystem events
		// e.g. node clicked, expanded etc.
		filesystem.addListener((Listener) this);
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
				FileItem fileItem = (FileItem) filesystem.getItem(filesystem
						.getValue());
				fileProperties.setIcon(fileItem.getIcon());
				fileProperties.setCaption(fileItem.getName() + ", size "
						+ fileItem.getSize() + " bytes.");
			}
			// here we could check for other type of events for filesystem
			// component
		}
		// here we could check for other component's events
	}

}
