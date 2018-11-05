package com.vaadin.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Table;

public class TestSizeableIncomponents extends LegacyApplication {

    private IndexedContainer cont;
    private ComboBox select;
    private Button prev;
    private Button next;
    private VerticalLayout testPanelLayout;
    private Panel testPanel;

    @Override
    public void init() {

        initComponentList();

        LegacyWindow w = new LegacyWindow();
        setMainWindow(w);
        setTheme("tests-components");

        final VerticalLayout main = new VerticalLayout();
        w.setContent(main);

        select = new ComboBox();
        select.setImmediate(true);
        select.setFilteringMode(FilteringMode.CONTAINS);
        select.setWidth("400px");

        prev = new Button("<<-|");
        prev.addClickListener(event -> {
            Object cur = select.getValue();
            Testable prev = (Testable) cont.prevItemId(cur);
            if (prev == null) {
                getMainWindow().showNotification("No more test cases");
            } else {
                getMainWindow().showNotification(
                        "Selected test:" + prev.getTestableName());
                select.setValue(prev);
                select.markAsDirty();
            }
        });
        next = new Button("|->>");
        next.addClickListener(event -> {
            Object cur = select.getValue();
            Testable next = (Testable) cont.nextItemId(cur);
            if (next == null) {
                getMainWindow().showNotification("No more test cases");
            } else {
                getMainWindow().showNotification(
                        "Selected test:" + next.getTestableName());
                select.setValue(next);
                select.markAsDirty();
            }
        });

        HorizontalLayout controllers = new HorizontalLayout();
        controllers.addComponent(prev);
        controllers.addComponent(select);
        controllers.addComponent(next);
        main.addComponent(controllers);

        select.setContainerDataSource(cont);
        select.addValueChangeListener(event -> {
            Testable t = (Testable) select.getValue();
            if (t != null) {
                testPanelLayout.removeAllComponents();
                try {
                    Component c = t.getComponent();
                    if (c != null) {
                        testPanelLayout.addComponent(c);
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        testPanelLayout = new VerticalLayout();
        testPanel = new Panel(testPanelLayout);
        testPanel.setSizeFull();
        testPanel.setStyleName("testable");
        main.addComponent(testPanel);
        main.setExpandRatio(testPanel, 1);

    }

    private void initComponentList() {
        cont = new IndexedContainer();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URL dir = cl.getResource("com/vaadin/ui");

        String[] list2 = (new File(dir.getFile())).list();
        for (String f : list2) {
            if (f.endsWith(".class") && (f.indexOf("CustomComponent") == -1)
                    && (f.indexOf("Window") == -1)) {
                f = f.replaceAll(".class", "");
                String className = "com.vaadin.ui." + f;
                Class<?> c;
                try {
                    c = Class.forName(className);
                    Object o = c.newInstance();
                    if (o instanceof Component) {
                        Testable t = new Testable(c);
                        cont.addItem(t);
                        t = new Testable(c);
                        t.addConfiguration(new Configuration("100px*100px") {
                            @Override
                            void configure(Component c) {
                                c.setWidth("60px");
                                c.setHeight("60px");
                            }
                        });
                        t = new Testable(c);
                        t.addConfiguration(new Configuration("Width 50em") {
                            @Override
                            void configure(Component c) {
                                c.setWidth("50em");
                            }
                        });
                        cont.addItem(t);
                        t = new Testable(c);
                        t.addConfiguration(new Configuration("Height 7cm") {
                            @Override
                            void configure(Component c) {
                                c.setHeight("7cm");
                            }
                        });
                        cont.addItem(t);
                        t = new Testable(c) {
                            @Override
                            public Component getComponent()
                                    throws InstantiationException,
                                    IllegalAccessException {

                                Component c = super.getComponent();

                                VerticalLayout pl = new VerticalLayout();
                                pl.setMargin(true);
                                Panel p = new Panel(
                                        "Wrapper panel (400px*400px)", pl);
                                p.setContent(new VerticalLayout());
                                p.setWidth("400px");
                                p.setHeight("400px");
                                pl.addComponent(c);
                                p.addStyleName("testablew");
                                p.addStyleName("testable");
                                return p;
                            }

                        };
                        t.addConfiguration(new Configuration("100%*100%") {
                            @Override
                            void configure(Component c) {
                                c.setSizeFull();
                            }

                        });
                        cont.addItem(t);
                    }

                } catch (ClassNotFoundException | InstantiationException
                        | IllegalAccessException e) {
                    // e.printStackTrace();
                }
            }
        }

    }

    class Testable {

        private Class<?> classToTest;
        private List<Configuration> configurations = new ArrayList<>();

        Testable(Class<?> c) {
            classToTest = c;
        }

        public void addConfiguration(Configuration conf) {
            configurations.add(conf);
        }

        public String getTestableName() {
            StringBuilder sb = new StringBuilder();
            sb.append(classToTest.getName().replaceAll("com.vaadin.ui.", ""));
            sb.append('[');
            for (Iterator<Configuration> i = configurations.iterator(); i
                    .hasNext();) {
                sb.append((i.next()).getDescription());
                if (i.hasNext()) {
                    sb.append(',');
                }
            }
            sb.append(']');

            return sb.toString();
        }

        /**
         * Instantiates and populates component with test data to be ready for
         * testing.
         *
         * @return
         * @throws InstantiationException
         * @throws IllegalAccessException
         */
        public Component getComponent()
                throws InstantiationException, IllegalAccessException {
            Component c = (Component) classToTest.newInstance();

            if (c instanceof Button) {
                ((AbstractComponent) c).setCaption("test");
            }
            if (AbstractSelect.class.isAssignableFrom(c.getClass())) {
                if (c instanceof Table) {
                    Table new_name = (Table) c;
                    new_name.setContainerDataSource(
                            TestForTablesInitialColumnWidthLogicRendering
                                    .getTestTable(5, 100)
                                    .getContainerDataSource());

                } else {
                    AbstractSelect new_name = (AbstractSelect) c;
                    Container cont = TestForTablesInitialColumnWidthLogicRendering
                            .getTestTable(2, 8).getContainerDataSource();
                    new_name.setContainerDataSource(cont);
                    new_name.setItemCaptionPropertyId(
                            cont.getContainerPropertyIds().iterator().next());

                }
            } else if (c instanceof ComponentContainer) {
                ComponentContainer new_name = (ComponentContainer) c;
                new_name.addComponent(
                        new Label("component 1 in test container"));
                new_name.addComponent(new Button("component 2"));
            } else if (c instanceof Embedded) {
                Embedded em = (Embedded) c;
                em.setSource(new ThemeResource("test.png"));
            } else if (c instanceof Label) {
                ((Label) c).setValue("Test label");
            }

            for (Configuration conf : configurations) {
                conf.configure(c);
            }
            return c;
        }

        @Override
        public String toString() {
            return getTestableName();
        }
    }

    public abstract class Configuration {

        private String description = "";

        Configuration(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        abstract void configure(Component c);

        @Override
        public String toString() {
            return getDescription();
        }

    }
}
