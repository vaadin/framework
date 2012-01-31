package com.vaadin.tests;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Label.ContentMode;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemStyleGenerator;
import com.vaadin.ui.VerticalLayout;

public class Components extends Application.LegacyApplication {

    private static final Object CAPTION = "c";
    private Map<Class<? extends AbstractComponentTest>, String> tests = new HashMap<Class<? extends AbstractComponentTest>, String>();
    private Tree naviTree;
    private HorizontalSplitPanel sp;
    private LegacyWindow mainWindow;
    private final Embedded applicationEmbedder = new Embedded();
    private String baseUrl;
    private List<Class<? extends Component>> componentsWithoutTests = new ArrayList<Class<? extends Component>>();

    {
        for (Class<?> c : VaadinClasses.getBasicComponentTests()) {
            String testClass = c.getSimpleName();
            tests.put((Class<? extends AbstractComponentTest>) c, testClass);
        }

        List<Class<? extends Component>> componentsWithoutTest = VaadinClasses
                .getComponents();
        Set<String> availableTests = new HashSet<String>();
        for (String testName : tests.values()) {
            availableTests.add(testName);
        }

        for (Class<? extends Component> component : componentsWithoutTest) {
            String baseName = component.getSimpleName();
            if (availableTests.contains(baseName + "es")) {
                continue;
            }
            if (availableTests.contains(baseName + "es2")) {
                continue;
            }
            if (availableTests.contains(baseName + "s2")) {
                continue;
            }
            if (availableTests.contains(baseName + "s")) {
                continue;
            }
            if (availableTests.contains(baseName + "Test")) {
                continue;
            }

            componentsWithoutTests.add(component);
        }

    }

    class MissingTest extends AbstractComponentTest<AbstractComponent> {
        @Override
        protected Class<AbstractComponent> getTestClass() {
            return null;
        }
    }

    @Override
    public void init() {
        mainWindow = new LegacyWindow();
        setTheme("tests-components");
        mainWindow.getContent().setSizeFull();
        setMainWindow(mainWindow);
        sp = new HorizontalSplitPanel();
        sp.setSizeFull();
        VerticalLayout naviLayout = new VerticalLayout();
        naviLayout
                .addComponent(new Label(
                        "Click to open a test case.<br/>Right click to open test in a new window<br/><br/>",
                        ContentMode.XHTML));
        naviLayout.addComponent(createMenu());
        naviLayout.addComponent(createMissingTestsList());

        sp.setFirstComponent(naviLayout);
        sp.setSplitPosition(250, Sizeable.UNITS_PIXELS);
        VerticalLayout embeddingLayout = new VerticalLayout();
        embeddingLayout.setSizeFull();
        embeddingLayout
                .addComponent(new Label(
                        "<b>Do not use the embedded version for creating automated tests. Open the test in a new window before recording.</b><br/>",
                        ContentMode.XHTML));
        applicationEmbedder.setSizeFull();
        embeddingLayout.addComponent(applicationEmbedder);
        embeddingLayout.setExpandRatio(applicationEmbedder, 1);
        sp.setSecondComponent(embeddingLayout);
        mainWindow.addComponent(sp);

        applicationEmbedder.setType(Embedded.TYPE_BROWSER);
        baseUrl = getURL().toString().replace(getClass().getName(), "")
                .replaceAll("//$", "/");
    }

    private Component createMissingTestsList() {
        String missingTests = "";
        for (Class<? extends Component> component : componentsWithoutTests) {
            String cls = "missing";
            if (component.getAnnotation(Deprecated.class) != null) {
                cls = "missing-deprecated";
            }
            missingTests += "<font class=\"" + cls + "\">"
                    + component.getSimpleName() + "</font><br/>";
        }
        return new Label("<b>Components without a test:</B><br/>"
                + missingTests, ContentMode.XHTML);
    }

    private Component createMenu() {
        naviTree = new Tree();
        naviTree.setItemStyleGenerator(new ItemStyleGenerator() {

            public String getStyle(Object itemId) {
                Class<?> cls = (Class<?>) itemId;
                if (!isAbstract(cls)) {
                    return "blue";
                }
                return null;
            }
        });
        HierarchicalContainer hc = new HierarchicalContainer();
        naviTree.setContainerDataSource(hc);
        DefaultItemSorter sorter = new DefaultItemSorter() {
            @SuppressWarnings("rawtypes")
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof Class && o2 instanceof Class && o1 != null
                        && o2 != null) {
                    Class<?> c1 = (Class) o1;
                    Class<?> c2 = (Class) o2;
                    boolean a1 = isAbstract(c1);
                    boolean a2 = isAbstract(c2);

                    if (a1 && !a2) {
                        return 1;
                    } else if (!a1 && a2) {
                        return -1;
                    }

                }
                return super.compare(o1, o2);
            }
        };
        hc.setItemSorter(sorter);
        naviTree.addContainerProperty(CAPTION, String.class, "");
        naviTree.setItemCaptionPropertyId(CAPTION);
        for (Class<? extends AbstractComponentTest> cls : tests.keySet()) {
            addTreeItem(cls);
        }
        hc.sort(new Object[] { CAPTION }, new boolean[] { true });
        naviTree.setSelectable(false);
        for (Object o : naviTree.rootItemIds()) {
            expandAndSetChildrenAllowed(o);
        }

        naviTree.addListener(new ItemClickListener() {

            public void itemClick(ItemClickEvent event) {
                Class<?> cls = (Class<?>) event.getItemId();
                if (!isAbstract(cls)) {
                    String url = baseUrl + cls.getName()
                            + "?restartApplication";
                    if (event.getButton() == ItemClickEvent.BUTTON_LEFT) {
                        openEmbedded(url);
                        naviTree.setValue(event.getItemId());
                    } else if (event.getButton() == ItemClickEvent.BUTTON_RIGHT) {
                        openInNewTab(url);
                    }
                }
            }

        });
        return naviTree;
    }

    protected void openInNewTab(String url) {
        getMainWindow().open(new ExternalResource(url), "_blank");
    }

    protected void openEmbedded(String url) {
        applicationEmbedder.setSource(new ExternalResource(url));
    }

    private void expandAndSetChildrenAllowed(Object o) {
        Collection<?> children = naviTree.getChildren(o);
        if (children == null || children.size() == 0) {
            naviTree.setChildrenAllowed(o, false);
        } else {
            naviTree.expandItem(o);
            for (Object c : children) {
                expandAndSetChildrenAllowed(c);
            }
        }

    }

    protected boolean isAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers());
    }

    @SuppressWarnings("unchecked")
    private void addTreeItem(Class<? extends AbstractComponentTest> cls) {
        String name = tests.get(cls);
        if (name == null) {
            name = cls.getSimpleName();
        }

        Class<? extends AbstractComponentTest> superClass = (Class<? extends AbstractComponentTest>) cls
                .getSuperclass();

        // This cast is needed only to make compilation through Ant work ..
        if (((Class<?>) cls) != AbstractComponentTest.class) {
            addTreeItem(superClass);
        }
        if (naviTree.containsId(cls)) {
            return;
        }

        Item i = naviTree.addItem(cls);
        i.getItemProperty(CAPTION).setValue(name);
        naviTree.setParent(cls, superClass);
    }

    protected Component createTestComponent(
            Class<? extends AbstractComponentTest> cls) {
        try {
            AbstractComponentTest t = cls.newInstance();
            t.init();
            ComponentContainer c = t.getMainWindow().getContent();
            t.getMainWindow().setContent(null);
            return c;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
