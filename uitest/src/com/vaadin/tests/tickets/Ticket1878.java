package com.vaadin.tests.tickets;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.AlignmentHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket1878 extends LegacyApplication {

    private Layout orderedLayout;
    private Layout gridLayout;
    private Layout formLayout;
    private GridLayout mainLayout;
    private Button switchToGridButton;
    private Button switchToOrderedButton;
    private Button switchToFormsButton;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");

        mainLayout = new GridLayout(1, 2);
        w.setContent(mainLayout);
        orderedLayout = createOL();
        gridLayout = createGL();
        formLayout = createForms();

        switchToGridButton = new Button("Switch to GridLayout",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        changeLayout(switchToGridButton, gridLayout);
                    }

                });
        switchToOrderedButton = new Button("Switch to OrderedLayout",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        changeLayout(switchToOrderedButton, orderedLayout);
                    }

                });
        switchToOrderedButton.setEnabled(false);

        switchToFormsButton = new Button("Switch to Form", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                changeLayout(switchToFormsButton, formLayout);
            }

        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addComponent(switchToOrderedButton);
        buttonLayout.addComponent(switchToGridButton);
        buttonLayout.addComponent(switchToFormsButton);

        mainLayout.addComponent(buttonLayout);
        mainLayout.addComponent(orderedLayout);
        // w.setContent(orderedLayout);
    }

    private static Layout createOL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createLayout(l1, new HorizontalLayout(), "1000px", "150px", "100%",
                null, true);
        createLayout(l1, new HorizontalLayout(), "1000px", "150px", "50px",
                null, false);
        GridLayout l2 = new GridLayout(6, 1);
        createLayout(l2, new VerticalLayout(), "200px", "500px", true);
        createLayout(l2, new VerticalLayout(), "200px", "500px", "100%", null,
                true);
        createLayout(l2, new VerticalLayout(), "150px", "500px", true);
        createLayout(l2, new VerticalLayout(), "150px", "500px", "100%", null,
                true);
        createLayout(l2, new VerticalLayout(), "100px", "500px", true);
        createLayout(l2, new VerticalLayout(), "100px", "500px", "100%", null,
                true);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    private static Layout createGL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createLayout(l1, new GridLayout(8, 1), "1000px", "150px", "100%", null,
                true);
        createLayout(l1, new GridLayout(8, 1), "1000px", "150px", "50px", null,
                false);
        GridLayout l2 = new GridLayout(6, 1);
        createLayout(l2, new GridLayout(1, 8), "200px", "500px", true);
        createLayout(l2, new GridLayout(1, 8), "200px", "500px", "100%", null,
                true);
        createLayout(l2, new GridLayout(1, 8), "150px", "500px", true);
        createLayout(l2, new GridLayout(1, 8), "150px", "500px", "100%", null,
                true);
        createLayout(l2, new GridLayout(1, 8), "100px", "500px", true);
        createLayout(l2, new GridLayout(1, 8), "100px", "500px", "100%", null,
                true);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    public class FormObject {
        private String stringValue = "abc";
        private int intValue = 1;
        private long longValue = 2L;
        private Date dateValue = new Date(34587034750L);

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public Date getDateValue() {
            return dateValue;
        }

        public void setDateValue(Date dateValue) {
            this.dateValue = dateValue;
        }

    }

    private Layout createForms() {
        GridLayout layout = new GridLayout(1, 5);
        Form form;

        Random r = new Random();
        GridLayout l1 = new GridLayout(1, 3);
        form = createForm(l1, "200px", "500px");
        BeanItem<FormObject> item = new BeanItem<FormObject>(new FormObject());
        form.setItemDataSource(item);
        for (Iterator<?> i = item.getItemPropertyIds().iterator(); i.hasNext();) {
            Object property = i.next();
            Field<?> f = form.getField(property);

            f.setRequired(r.nextBoolean());
            if (r.nextBoolean()) {
                f.setIcon(new ThemeResource("icons/16/document-add.png"));
            }
            if (r.nextBoolean()) {
                f.setCaption(null);
            }

            f.addValidator(new StringLengthValidator("Error", 10, 8, false));
        }
        // createLayout(l1, new
        // ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL),
        // "1000px", "150px", "50px", null, false);

        // GridLayout l2 = new GridLayout(6, 1);
        // createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
        // "200px", "500px", true);
        // createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
        // "200px", "500px", "100%", null, true);
        // createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
        // "150px", "500px", true);
        // createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
        // "150px", "500px", "100%", null, true);
        // createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
        // "100px", "500px", true);
        // createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
        // "100px", "500px", "100%", null, true);
        layout.addComponent(l1);
        // layout.addComponent(l2);

        return layout;
    }

    private Form createForm(GridLayout parentLayout, String w, String h) {
        FormLayout formLayout = new FormLayout();
        Form form = new Form(formLayout);

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSizeFull();
        Panel p = new Panel("Form " + w + "x" + h, vl);

        p.setWidth(w);
        p.setHeight(h);

        parentLayout.addComponent(p);
        vl.addComponent(form);
        formLayout.setSizeFull();

        return form;
    }

    protected void changeLayout(Button b, Layout newLayout) {
        switchToOrderedButton.setEnabled(true);
        switchToGridButton.setEnabled(true);
        switchToFormsButton.setEnabled(true);

        b.setEnabled(false);
        Iterator<Component> i = mainLayout.getComponentIterator();
        i.next();
        Layout l = (Layout) i.next();

        mainLayout.replaceComponent(l, newLayout);
    }

    private static void createLayout(GridLayout parentLayout, Layout newLayout,
            String w, String h, boolean align) {
        createLayout(parentLayout, newLayout, w, h, null, null, align);
    }

    private static void createLayout(GridLayout parentLayout,
            final Layout newLayout, String w, String h, String componentWidth,
            String componentHeight, boolean align) {
        String dirText = "V";
        String type;
        if (newLayout instanceof VerticalLayout) {
            type = "OL";
        } else if (newLayout instanceof HorizontalLayout) {
            dirText = "H";
            type = "OL";
        } else {
            if (((GridLayout) newLayout).getColumns() != 1) {
                dirText = "H";
            }
            type = "GL";
        }
        String alignText = align ? "-A" : "";
        String cWidth = componentWidth == null ? "" : " - " + componentWidth;
        Panel p = new Panel(type + "/" + dirText + alignText + " " + w + "x"
                + h + cWidth, newLayout);

        p.setWidth(w);
        p.setHeight(h);

        newLayout.setSizeFull();

        String captions[] = new String[] { "TextField with caption", null };
        Resource icons[] = new Resource[] {
                new ThemeResource("icons/16/document-delete.png"), null };
        boolean required[] = new boolean[] { true, false };
        TextField fields[][] = new TextField[captions.length][icons.length];
        for (int caption = 0; caption < captions.length; caption++) {
            for (int icon = 0; icon < icons.length; icon++) {
                for (int req = 0; req < required.length; req++) {
                    TextField tf = createTextFieldWithError(captions[caption],
                            icons[icon], required[req]);

                    fields[caption][icon] = tf;
                    if (componentWidth != null) {
                        tf.setWidth(componentWidth);
                        tf.setValue(tf.getValue() + " w:" + componentWidth);
                    }

                    if (componentHeight != null) {
                        tf.setHeight(componentWidth);
                        tf.setValue(tf.getValue() + " h:" + componentHeight);
                    }

                    newLayout.addComponent(tf);

                    if (align) {
                        ((AlignmentHandler) newLayout).setComponentAlignment(
                                tf, Alignment.BOTTOM_RIGHT);
                    }
                }
            }
        }

        parentLayout.addComponent(p);

    }

    // private static void createGridLayout(GridLayout parentLayout, int dir,
    // String w, String h) {
    // createGridLayout(parentLayout, dir, w, h, null, null);
    // }

    // private static void createGridLayout(GridLayout parentLayout, int dir,
    // String w, String h, String componentWidth, String componentHeight) {
    // GridLayout gl;
    // if (dir == OrderedLayout.ORIENTATION_HORIZONTAL) {
    // gl = new GridLayout(8, 1);
    // } else {
    // gl = new GridLayout(1, 8);
    // }
    //
    // String dirText = (dir == OrderedLayout.ORIENTATION_HORIZONTAL ? "H"
    // : "V");
    // String cWidth = componentWidth == null ? "" : " - " + componentWidth;
    // Panel p = new Panel("GL/" + dirText + " " + w + "x" + h + cWidth, gl);
    //
    // p.setWidth(w);
    // p.setHeight(h);
    //
    // gl.setSizeFull();
    //
    // String captions[] = new String[] { "TextField with caption", null };
    // Resource icons[] = new Resource[] {
    // new ThemeResource("icons/16/document-delete.png"), null };
    // boolean required[] = new boolean[] { true, false };
    // TextField fields[][] = new TextField[captions.length][icons.length];
    // for (int caption = 0; caption < captions.length; caption++) {
    // for (int icon = 0; icon < icons.length; icon++) {
    // for (int req = 0; req < required.length; req++) {
    // TextField tf = createTextFieldWithError(captions[caption],
    // icons[icon], required[req]);
    //
    // fields[caption][icon] = tf;
    // if (componentWidth != null) {
    // tf.setWidth(componentWidth);
    // }
    //
    // if (componentHeight != null) {
    // tf.setHeight(componentWidth);
    // }
    //
    // p.addComponent(tf);
    // gl.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
    // OrderedLayout.ALIGNMENT_BOTTOM);
    // }
    // }
    // }
    //
    // parentLayout.addComponent(p);
    //
    // }

    private static TextField createTextFieldWithError(String caption,
            Resource icon, boolean required) {
        TextField tf = new TextField();
        tf.setCaption(caption);
        tf.setIcon(icon);
        tf.setRequired(required);
        tf.setComponentError(new UserError("Test error message"));
        return tf;
    }
}
