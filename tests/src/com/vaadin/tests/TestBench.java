/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;

/**
 * TestBench finds out testable classes within given java packages and adds them
 * to menu from where they can be executed. Class is considered testable if it
 * is of class Application or CustomComponent.
 * 
 * Note: edit TestBench.testablePackages array
 * 
 * @author IT Mill Ltd.
 * 
 */
public class TestBench extends com.vaadin.Application implements
		Property.ValueChangeListener {

	// Add here packages which are used for finding testable classes
	String[] testablePackages = { "com.vaadin.tests",
			"com.vaadin.demo", "com.vaadin.demo.colorpicker",
			"com.vaadin.demo.reservation",
			"com.vaadin.demo.features",
			"com.vaadin.tests.tickets", "com.vaadin.tests.book" };

	HierarchicalContainer testables = new HierarchicalContainer();

	Window mainWindow = new Window("TestBench window");

	// Main layout consists of tree menu and body layout
	SplitPanel mainLayout = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);

	Tree menu;

	Panel bodyLayout = new Panel();

	HashMap itemCaptions = new HashMap();

	@Override
	public void init() {

		// Add testable classes to hierarchical container
		for (int p = 0; p < testablePackages.length; p++) {
			testables.addItem(testablePackages[p]);
			try {
				final List testableClasses = getTestableClassesForPackage(testablePackages[p]);
				for (final Iterator it = testableClasses.iterator(); it
						.hasNext();) {
					final Class t = (Class) it.next();
					// ignore TestBench itself
					if (t.equals(TestBench.class)) {
						continue;
					}
					try {
						testables.addItem(t);
						itemCaptions.put(t, t.getName());
						testables.setParent(t, testablePackages[p]);
						testables.setChildrenAllowed(t, false);
						continue;
					} catch (final Exception e) {
						try {
							testables.addItem(t);
							itemCaptions.put(t, t.getName());
							testables.setParent(t, testablePackages[p]);
							testables.setChildrenAllowed(t, false);
							continue;
						} catch (final Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		menu = new Tree("Testables", testables);

		for (final Iterator i = itemCaptions.keySet().iterator(); i.hasNext();) {
			final Class testable = (Class) i.next();
			// simplify captions
			final String name = testable.getName().substring(
					testable.getName().lastIndexOf('.') + 1);
			menu.setItemCaption(testable, name);
		}
		// expand all root items
		for (final Iterator i = menu.rootItemIds().iterator(); i.hasNext();) {
			menu.expandItemsRecursively(i.next());
		}

		menu.addListener(this);
		menu.setImmediate(true);
		menu.setNullSelectionAllowed(false);
		VerticalLayout lo = new VerticalLayout();
		lo.addComponent(menu);

		UriFragmentUtility uri = new UriFragmentUtility();
		lo.addComponent(uri);

		uri.addListener(new UriFragmentUtility.FragmentChangedListener() {
			public void fragmentChanged(FragmentChangedEvent source) {
				String fragment = source.getUriFragmentUtility().getFragment();
				if (fragment != null && !"".equals(fragment)) {
					// try to find a proper test class

					// exact match
					Iterator iterator = menu.getItemIds().iterator();
					while (iterator.hasNext()) {
						Object next = iterator.next();
						if (next instanceof Class) {
							Class c = (Class) next;
							String string = c.getName();
							if (string.equals(fragment)) {
								menu.setValue(c);
								mainLayout.setSplitPosition(0);
								return;
							}
						}
					}

					// simple name match
					iterator = menu.getItemIds().iterator();
					while (iterator.hasNext()) {
						Object next = iterator.next();
						if (next instanceof Class) {
							Class c = (Class) next;
							String string = c.getSimpleName();
							if (string.equals(fragment)) {
								menu.setValue(c);
								mainLayout.setSplitPosition(0);
								return;
							}
						}
					}
					// ticket match
					iterator = menu.getItemIds().iterator();
					while (iterator.hasNext()) {
						Object next = iterator.next();
						if (next instanceof Class) {
							Class c = (Class) next;
							String string = c.getSimpleName();
							if (string.startsWith("Ticket" + fragment)) {
								menu.setValue(c);
								mainLayout.setSplitPosition(0);
								return;
							}
						}
					}

					// just partly mach lowercase
					iterator = menu.getItemIds().iterator();
					while (iterator.hasNext()) {
						Object next = iterator.next();
						if (next instanceof Class) {
							Class c = (Class) next;
							String string = c.getSimpleName();
							if (string.toLowerCase().contains(
									fragment.toLowerCase())) {
								menu.setValue(c);
								mainLayout.setSplitPosition(0);
								return;
							}
						}
					}

					getMainWindow().showNotification(
							"No potential matc for #" + fragment);

				}

			}
		});

		mainLayout.addComponent(lo);

		bodyLayout.addStyleName("light");
		bodyLayout.setSizeFull();
		bodyLayout.setLayout(new ExpandLayout());

		mainLayout.addComponent(bodyLayout);

		mainLayout.setSplitPosition(30);

		mainWindow.setLayout(mainLayout);

		setMainWindow(mainWindow);
	}

	private Component createTestable(Class c) {
		try {
			final Application app = (Application) c.newInstance();
			app.init();
			Layout lo = app.getMainWindow().getLayout();
			lo.setParent(null);
			return lo;
		} catch (final Exception e) {
			try {
				final CustomComponent cc = (CustomComponent) c.newInstance();
				cc.setSizeFull();
				return cc;
			} catch (final Exception e1) {
				e1.printStackTrace();
				VerticalLayout lo = new VerticalLayout();
				lo.addComponent(new Label(
						"Cannot create application / custom component: "
								+ e1.toString()));

				Link l = new Link("Try opening via app runner",
						new ExternalResource("../run/" + c.getName()));
				lo.addComponent(l);

				return lo;
			}
		}
	}

	// Handle menu selection and update body
	public void valueChange(Property.ValueChangeEvent event) {
		bodyLayout.removeAllComponents();
		bodyLayout.setCaption(null);

		final Object o = menu.getValue();
		if (o != null && o instanceof Class) {
			final Class c = (Class) o;
			final String title = c.getName();
			bodyLayout.setCaption(title);
			bodyLayout.addComponent(createTestable(c));
		} else {
			// NOP node selected or deselected tree item
		}
	}

	/**
	 * Return all testable classes within given package. Class is considered
	 * testable if it's superclass is Application or CustomComponent.
	 * 
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List getTestableClassesForPackage(String packageName)
			throws Exception {
		final ArrayList directories = new ArrayList();
		try {
			final ClassLoader cld = Thread.currentThread()
					.getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			final String path = packageName.replace('.', '/');
			// Ask for all resources for the path
			final Enumeration resources = cld.getResources(path);
			while (resources.hasMoreElements()) {
				final URL url = (URL) resources.nextElement();
				directories.add(new File(url.getFile()));
			}
		} catch (final Exception x) {
			throw new Exception(packageName
					+ " does not appear to be a valid package.");
		}

		final ArrayList classes = new ArrayList();
		// For every directory identified capture all the .class files
		for (final Iterator it = directories.iterator(); it.hasNext();) {
			final File directory = (File) it.next();
			if (directory.exists()) {
				// Get the list of the files contained in the package
				final String[] files = directory.list();
				for (int j = 0; j < files.length; j++) {
					// we are only interested in .class files
					if (files[j].endsWith(".class")) {
						// removes the .class extension
						final String p = packageName + '.'
								+ files[j].substring(0, files[j].length() - 6);
						final Class c = Class.forName(p);
						if (c.getSuperclass() != null) {
							if ((c.getSuperclass()
									.equals(com.vaadin.Application.class))) {
								classes.add(c);
							} else if ((c.getSuperclass()
									.equals(com.vaadin.ui.CustomComponent.class))) {
								classes.add(c);
							}
						}

						// for (int i = 0; i < c.getInterfaces().length; i++) {
						// Class cc = c.getInterfaces()[i];
						// if (c.getInterfaces()[i].equals(Testable.class)) {
						// // Class is testable
						// classes.add(c);
						// }
						// }
					}
				}
			} else {
				throw new ClassNotFoundException(packageName + " ("
						+ directory.getPath()
						+ ") does not appear to be a valid package");
			}
		}

		return classes;
	}

}
