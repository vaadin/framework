package com.itmill.toolkit.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.ui.*;

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
public class TestBench extends com.itmill.toolkit.Application implements
		Property.ValueChangeListener {

	private Random seededRandom = new Random(1);

	// Add here packages which are used for finding testable classes
	String[] testablePackages = { "com.itmill.toolkit.tests",
			"com.itmill.toolkit.demo", "com.itmill.toolkit.demo.colorpicker",
			"com.itmill.toolkit.demo.reservation",
			"com.itmill.toolkit.demo.features" };

	HierarchicalContainer testables = new HierarchicalContainer();

	Window mainWindow = new Window("TestBench window");

	// Main layout consists of tree menu and body layout
	SplitPanel mainLayout = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);

	Tree menu;

	Panel bodyLayout = new Panel();

	HashMap itemCaptions = new HashMap();

	public void init() {

		// Add testable classes to hierarchical container
		for (int p = 0; p < testablePackages.length; p++) {
			testables.addItem(testablePackages[p]);
			try {
				List testableClasses = this
						.getTestableClassesForPackage(testablePackages[p]);
				for (Iterator it = testableClasses.iterator(); it.hasNext();) {
					Class t = (Class) it.next();
					System.out.println(t);
					// ignore TestBench itself
					if (t.equals(TestBench.class))
						continue;
					try {
						testables.addItem(t);
						itemCaptions.put(t, t.getSimpleName());
						testables.setParent(t, testablePackages[p]);
						continue;
					} catch (Exception e) {
						try {
							testables.addItem(t);
							itemCaptions.put(t, t.getSimpleName());
							testables.setParent(t, testablePackages[p]);
							continue;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					System.out.println(" Skipped " + t);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		menu = new Tree("Testables", testables);
		// simplify captions
		for (Iterator i = itemCaptions.keySet().iterator(); i.hasNext();) {
			Class testable = (Class) i.next();
			menu.setItemCaption(testable, testable.getSimpleName());
		}
		menu.setStyle("menu");
		menu.addListener(this);
		menu.setImmediate(true);

		mainLayout.setHeight(700);
		mainLayout.setHeightUnits(SplitPanel.UNITS_PIXELS);
		mainLayout.addComponent(menu);
		mainLayout.addComponent(bodyLayout);

		mainWindow.addComponent(mainLayout);

		setMainWindow(mainWindow);
	}

	private Component createTestable(Class c) {
		try {
			Application app = (Application) c.newInstance();
			System.out.println("Creating application " + c);
			app.init();
			return app.getMainWindow().getLayout();
		} catch (Exception e) {
			try {
				CustomComponent cc = (CustomComponent) c.newInstance();
				System.out.println("Creating component " + c);
				return cc;
			} catch (Exception e1) {
				e1.printStackTrace();
				return new Label(
						"Cannot create application / custom component: "
								+ e1.toString());
			}
		}
	}

	// Handle menu selection and update body
	public void valueChange(Property.ValueChangeEvent event) {
		bodyLayout.removeAllComponents();
		bodyLayout.setCaption(null);

		String title = ((Class) menu.getValue()).getSimpleName();
		bodyLayout.setCaption(title);
		bodyLayout.addComponent(createTestable((Class) menu.getValue()));
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
		ArrayList directories = new ArrayList();
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null)
				throw new ClassNotFoundException("Can't get class loader.");
			String path = packageName.replace('.', '/');
			// Ask for all resources for the path
			Enumeration resources = cld.getResources(path);
			while (resources.hasMoreElements()) {
				URL url = (URL) resources.nextElement();
				directories.add(new File(url.getFile()));
			}
		} catch (Exception x) {
			throw new Exception(packageName
					+ " does not appear to be a valid package.");
		}

		ArrayList classes = new ArrayList();
		// For every directory identified capture all the .class files
		for (Iterator it = directories.iterator(); it.hasNext();) {
			File directory = (File) it.next();
			if (directory.exists()) {
				// Get the list of the files contained in the package
				String[] files = directory.list();
				for (int j = 0; j < files.length; j++) {
					// we are only interested in .class files
					if (files[j].endsWith(".class")) {
						// removes the .class extension
						String p = packageName + '.'
								+ files[j].substring(0, files[j].length() - 6);
						Class c = Class.forName(p);
						if (c.getSuperclass() != null)
							if ((c.getSuperclass()
									.equals(com.itmill.toolkit.Application.class))) {
								classes.add(c);
							} else if ((c.getSuperclass()
									.equals(com.itmill.toolkit.ui.CustomComponent.class))) {
								classes.add(c);
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
