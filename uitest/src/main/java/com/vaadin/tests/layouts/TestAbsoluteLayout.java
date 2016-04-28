package com.vaadin.tests.layouts;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TestAbsoluteLayout extends TestBase {

    private static class MFieldFactory extends DefaultFieldFactory {

        @Override
        public Field<?> createField(Item item, Object propertyId,
                Component uiContext) {
            if (propertyId.equals("CSSString")) {
                TextArea f = new TextArea();
                f.setRows(5);
                f.setHeight("8em");
                f.setCaption("CSS string");
                return f;
            } else if (((String) propertyId).contains("Units")) {
                NativeSelect s = new NativeSelect() {
                };
                s.addContainerProperty("caption", String.class, "");
                s.setItemCaptionPropertyId("caption");
                s.setNullSelectionAllowed(false);
                for (int i = 0; i < Layout.Unit.values().length; i++) {
                    Item unitItem = s.addItem(i);
                    unitItem.getItemProperty("caption").setValue(
                            Layout.Unit.values()[i]);
                }
                return s;
            }

            return super.createField(item, propertyId, uiContext);
        }

        private static MFieldFactory instance;

        public static DefaultFieldFactory get() {
            if (instance == null) {
                instance = new MFieldFactory();
            }
            return instance;
        }
    }

    @Override
    protected String getDescription() {
        return "This is absolute layout tester.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        AbsoluteLayout layout = new AbsoluteLayout();
        setTheme("tests-tickets");
        layout.setStyleName("cyan");
        layout.setWidth("1000px");
        layout.setHeight("500px");

        layout.addComponent(new Label("Hello World"));

        Button button = new Button("Centered button,z-index:10;");
        button.setSizeFull();
        layout.addComponent(button,
                "top:40%;bottom:40%;right:20%;left:20%;z-index:10;");

        Label label = new Label(
                "Exotic positioned label. Fullsize, top:100px; left:2cm; right: 3.5in; bottom:12.12mm ");
        label.setStyleName("yellow");
        label.setSizeFull();
        layout.addComponent(label,
                "top:100px; left:2cm; right: 3.5in; bottom:12.12mm");

        label = new Label("fullize, bottom:80%;left:80%;");
        label.setStyleName("green");
        label.setSizeFull();
        layout.addComponent(label, "bottom:80%;left:80%;");

        label = new Label("bottomright");
        label.setSizeUndefined();
        label.setStyleName("green");
        layout.addComponent(label, "bottom:0px; right:0px;");

        getLayout().setSizeFull();
        getLayout().addComponent(layout);

        getMainWindow().addWindow(new EditorWindow(layout));

    }

    public class EditorWindow extends Window {
        private final AbsoluteLayout l;
        private Form componentEditor;
        private Form positionEditor;

        public EditorWindow(AbsoluteLayout lo) {
            super("AbsoluteLayout editor aka köyhän miehen wysiwyg");
            l = lo;

            setHeight("600px");

            Button componentChooser = new Button("choose component to edit");
            componentChooser.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    VerticalLayout layout = new VerticalLayout();
                    layout.setMargin(true);
                    final Window chooser = new Window("Choose component",
                            layout);
                    layout.setSizeUndefined();
                    chooser.setModal(true);

                    NativeSelect select = new NativeSelect(
                            "Choose component to edit");

                    select.setNullSelectionAllowed(false);

                    IndexedContainer container = new IndexedContainer();
                    container.addContainerProperty("caption", String.class, "");
                    Iterator<Component> componentIterator = l
                            .getComponentIterator();
                    while (componentIterator.hasNext()) {
                        AbstractComponent next = (AbstractComponent) componentIterator
                                .next();
                        Item item = container.addItem(next);

                        String caption = next.getClass().getSimpleName();

                        caption += "; cap: " + next.getCaption() + "; debugid"
                                + getId();

                        if (next instanceof Property) {
                            caption += " value:"
                                    + ((Property<?>) next).getValue();
                        }

                        item.getItemProperty("caption").setValue(caption);
                    }
                    select.setContainerDataSource(container);
                    select.setItemCaptionPropertyId("caption");
                    select.setImmediate(true);

                    select.addListener(new ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            editcomponent((Component) event.getProperty()
                                    .getValue());
                            getMainWindow().removeWindow(chooser);
                        }

                    });

                    layout.addComponent(select);

                    getMainWindow().addWindow(chooser);

                }
            });

            addComponent(componentChooser);

            Button addComp = new Button("add component");
            addComp.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    VerticalLayout layout = new VerticalLayout();
                    layout.setMargin(true);
                    final Window chooser = new Window(
                            "Choose component type to add", layout);
                    layout.setSizeUndefined();
                    chooser.setModal(true);

                    NativeSelect select = new NativeSelect(
                            "Choose component to edit");

                    select.setNullSelectionAllowed(false);

                    IndexedContainer container = new IndexedContainer();

                    URL resource = AbstractComponent.class.getResource(".");
                    File directory = new File(resource.getFile());
                    if (directory.exists()) {
                        // Get the list of the files contained in the
                        // package
                        final String[] files = directory.list();
                        for (int j = 0; j < files.length; j++) {
                            // we are only interested in .class files
                            if (files[j].endsWith(".class")) {
                                // removes the .class extension
                                String p = resource.toString()
                                        + files[j].substring(0,
                                                files[j].length() - 6);
                                p = p.replaceAll(".*classes/", "");
                                p = p.replaceAll("/", ".");
                                Class<?> c;
                                try {
                                    c = Class.forName(p);
                                    if (AbstractComponent.class
                                            .isAssignableFrom(c)
                                            && !p.toLowerCase().contains(
                                                    "layout")
                                            && !p.toLowerCase().contains(
                                                    "abstract")) {
                                        container.addItem(c);
                                    }
                                } catch (ClassNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    select.setContainerDataSource(container);
                    select.setImmediate(true);

                    select.addListener(new ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            Class<?> c = (Class<?>) event.getProperty()
                                    .getValue();

                            try {
                                Component newInstance = (Component) c
                                        .newInstance();
                                l.addComponent(newInstance);
                                editcomponent(newInstance);
                                getMainWindow().removeWindow(chooser);
                            } catch (InstantiationException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                    });

                    layout.addComponent(select);

                    getMainWindow().addWindow(chooser);

                }
            });

            addComponent(addComp);

            componentEditor = new Form();
            componentEditor.setBuffered(true);
            componentEditor.setCaption("Component properties:");
            componentEditor.setFormFieldFactory(MFieldFactory.get());
            addComponent(componentEditor);

            positionEditor = new Form();
            positionEditor.setCaption("Component position");
            positionEditor.setBuffered(true);
            positionEditor.setFormFieldFactory(MFieldFactory.get());
            addComponent(positionEditor);

            Button b = new Button("Commit changes", new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    positionEditor.commit();
                    componentEditor.commit();
                }
            });
            addComponent(b);

        }

        private void editcomponent(Component value) {

            BeanItem<Component> beanItem = new BeanItem<Component>(value);
            String c = "Component properties for "
                    + value.getClass().getSimpleName();
            ArrayList<String> fields = new ArrayList<String>(
                    Arrays.asList(new String[] { "width", "widthUnits",
                            "height", "heightUnits", "caption", "styleName" }));
            if (value instanceof Label) {
                c += "(" + ((Label) value).getValue() + ")";
                fields.add("value");
            }

            componentEditor.setItemDataSource(beanItem, fields);

            BeanItem<ComponentPosition> positionItem = new BeanItem<ComponentPosition>(
                    l.getPosition(value));
            componentEditor.setCaption(c);

            positionEditor.setItemDataSource(positionItem);

        }
    }

}
