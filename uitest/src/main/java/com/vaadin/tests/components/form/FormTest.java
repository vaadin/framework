package com.vaadin.tests.components.form;

import java.util.LinkedHashMap;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.Product;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.AlignmentHandler;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FormTest extends AbstractFieldTest<Form> {

    private Command<Form, String> footerWidthCommand = new Command<Form, String>() {

        @Override
        public void execute(Form c, String value, Object data) {
            Layout footer = c.getFooter();
            if (footer != null) {
                footer.setWidth(value);
            }

        }
    };
    private Command<Form, String> footerHeightCommand = new Command<Form, String>() {

        @Override
        public void execute(Form c, String value, Object data) {
            Layout footer = c.getFooter();
            if (footer != null) {
                footer.setHeight(value);
            }

        }
    };

    private Command<Form, Class<? extends Layout>> formLayoutCommand = new Command<Form, Class<? extends Layout>>() {

        @Override
        public void execute(Form c, Class<? extends Layout> value, Object data) {
            if (value == null) {
                c.setLayout(null);
            } else {
                try {
                    Layout l = value.newInstance();
                    l.setSizeUndefined();
                    if (l instanceof GridLayout) {
                        ((GridLayout) l).setColumns(2);
                    }
                    c.setLayout(l);
                } catch (Exception e) {
                    log("Error creating footer of type " + value.getName()
                            + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    };
    private Command<Form, Class<? extends Layout>> formFooterCommand = new Command<Form, Class<? extends Layout>>() {

        @Override
        public void execute(Form c, Class<? extends Layout> value, Object data) {
            if (value == null) {
                c.setFooter(null);
            } else {
                try {
                    c.setFooter(value.newInstance());
                    Button b = new Button("Just a button");
                    c.getFooter().addComponent(b);
                } catch (Exception e) {
                    log("Error creating footer of type " + value.getName()
                            + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    };
    private Command<Form, Item> formItemDataSourceCommand = new Command<Form, Item>() {

        @Override
        public void execute(Form c, Item value, Object data) {
            c.setItemDataSource(value);
        }
    };
    private LinkedHashMap<String, Class<? extends Layout>> layoutOptions = new LinkedHashMap<String, Class<? extends Layout>>();
    {
        layoutOptions.put("VerticalLayout", VerticalLayout.class);
        layoutOptions.put("HorizontalLayout", HorizontalLayout.class);
        layoutOptions.put("GridLayout", GridLayout.class);
        layoutOptions.put("CSSLayout", CssLayout.class);
        layoutOptions.put("FormLayout", FormLayout.class);
    }

    @Override
    protected void createActions() {
        super.createActions();

        createFormLayoutSelect(CATEGORY_FEATURES);
        createFooterSelect(CATEGORY_FEATURES);
        createFooterPropertySelect(CATEGORY_FEATURES);
        createFormFactorySelect(CATEGORY_FEATURES);
        createDataSourceSelect(AbstractSelectTestCase.CATEGORY_DATA_SOURCE);
    }

    private void createDataSourceSelect(String category) {
        LinkedHashMap<String, Item> options = new LinkedHashMap<String, Item>();

        options.put("Person", createPersonItem());
        options.put("Product", createProductItem());

        createSelectAction("Form data source", category, options, "Person",
                formItemDataSourceCommand);

    }

    private BeanItem<Product> createProductItem() {
        return new BeanItem<Product>(new Product("Computer Monitor", 399.99f,
                "A monitor that can display both color and black and white."));
    }

    private BeanItem<Person> createPersonItem() {
        Person person = new Person("First", "Last", "foo@vaadin.com",
                "02-111 2222", "Ruukinkatu 2-4", 20540, "Turku");

        BeanItem<Person> personItem = new BeanItem<Person>(person);
        // add nested properties from address
        personItem.expandProperty("address");

        return personItem;
    }

    private void createFormFactorySelect(String category) {
        LinkedHashMap<String, FormFieldFactory> options = new LinkedHashMap<String, FormFieldFactory>();
        options.put("Default", DefaultFieldFactory.get());
        options.put("Custom FieldFactory", new FormFieldFactory() {

            @Override
            public Field<?> createField(Item item, Object propertyId,
                    Component uiContext) {
                Class<?> type = item.getItemProperty(propertyId).getType();
                Field<?> c = null;
                if (Number.class.isAssignableFrom(type)) {
                    TextField tf = new TextField();
                    tf.setCaption(DefaultFieldFactory
                            .createCaptionByPropertyId(propertyId));
                    tf.setWidth("3em");
                    c = tf;
                } else if ("city".equals(propertyId)) {
                    ComboBox cb = new ComboBox();
                    cb.setNullSelectionAllowed(false);
                    cb.addItem("Turku");
                    cb.addItem("New York");
                    cb.addItem("Moscow");
                    cb.setCaption(DefaultFieldFactory
                            .createCaptionByPropertyId(propertyId));
                    c = cb;
                    c.setWidth("200px");
                }

                if (c == null) {
                    c = DefaultFieldFactory.get().createField(item, propertyId,
                            uiContext);
                    c.setWidth("200px");
                }
                return c;
            }

            @Override
            public String toString() {
                return "Custom FieldFactory";
            }
        });

        Command<Form, FormFieldFactory> formFactoryCommand = new Command<Form, FormFieldFactory>() {

            @Override
            public void execute(Form c, FormFieldFactory value, Object data) {
                c.setFormFieldFactory(value);
                c.setItemDataSource(c.getItemDataSource());

            }
        };
        createSelectAction("FormFieldFactory", category, options, "Default",
                formFactoryCommand);
    }

    private void createFooterPropertySelect(String category) {
        String propertyCategory = "Footer properties";
        createCategory(propertyCategory, category);

        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("auto", "");
        options.put("200px", "200px");
        options.put("100%", "100%");

        createSelectAction("width", propertyCategory, options, "auto",
                footerWidthCommand);
        createSelectAction("height", propertyCategory, options, "auto",
                footerHeightCommand);

        LinkedHashMap<String, Alignment> alignmentOptions = new LinkedHashMap<String, Alignment>();
        for (AlignmentInfo horizontal : new AlignmentInfo[] {
                AlignmentInfo.LEFT, AlignmentInfo.CENTER, AlignmentInfo.RIGHT }) {
            for (AlignmentInfo vertical : new AlignmentInfo[] {
                    AlignmentInfo.TOP, AlignmentInfo.MIDDLE,
                    AlignmentInfo.BOTTOM }) {
                Alignment a = new Alignment(horizontal.getBitMask()
                        + vertical.getBitMask());
                alignmentOptions.put(
                        a.getHorizontalAlignment() + " "
                                + a.getVerticalAlignment(), a);

            }

        }
        Command<Form, Alignment> footerComponentAlignmentCommand = new Command<Form, Alignment>() {

            @Override
            public void execute(Form c, Alignment value, Object data) {
                Layout l = c.getFooter();
                if (l instanceof AlignmentHandler) {
                    ((AlignmentHandler) l).setComponentAlignment(l
                            .getComponentIterator().next(), value);
                }

            }
        };
        createSelectAction("Component alignment", propertyCategory,
                alignmentOptions, "left", footerComponentAlignmentCommand);
    }

    private void createFooterSelect(String category) {
        createSelectAction("Footer", category, layoutOptions, "-",
                formFooterCommand);

    }

    private void createFormLayoutSelect(String category) {

        createSelectAction("Layout", category, layoutOptions, "-",
                formLayoutCommand);

    }

    @Override
    protected Class<Form> getTestClass() {
        return Form.class;
    }

}
