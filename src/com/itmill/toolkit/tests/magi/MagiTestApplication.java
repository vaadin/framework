/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.validator.StringLengthValidator;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.terminal.gwt.server.WebApplicationContext;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.InlineDateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.PopupDateField;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class MagiTestApplication extends com.itmill.toolkit.Application {
    Window main = new Window("Application window");

    TheButton butts1;
    TheButtons butts2;
    TheButtons2 butts3;

    Label mylabel1;
    Label mylabel2;
    Label mylabel3;

    StreamResource strres;
    OrderedLayout ol;
    int getwincount = 0;

    public void init() {
        setTheme("tests-magi");
        
        setMainWindow(main);
    }

    public DownloadStream handleURI(URL context, String relativeUri) {
        // Let default implementation handle requests for
        // application resources.
        if (relativeUri.startsWith("APP"))
            return super.handleURI(context, relativeUri);
        if (relativeUri.startsWith("win-"))
            return super.handleURI(context, relativeUri);
        
        String example;
        String param = null;

        final int slashPos = relativeUri.indexOf("/");
        if (slashPos > 0) {
            example = relativeUri.substring(0, slashPos);
            param = relativeUri.substring(slashPos + 1);
        } else {
            example = relativeUri;
        }

        /* Remove existing components and windows. */
        main.removeAllComponents();
        final Set childwindows = main.getChildWindows();
        for (final Iterator cwi = childwindows.iterator(); cwi.hasNext();) {
            final Window child = (Window) cwi.next();
            main.removeWindow(child);
        }
        main.setLayout(new OrderedLayout());

        if (example.equals("index")) {
            final Object examples[] = { "defaultbutton", "label",
                    "labelcontent", "tree", "embedded", "textfield",
                    "textfieldvalidation", "datefield", "button",
                    "select/select", "select/native", "select/optiongroup",
                    "select/twincol", "filterselect", "validator", "table",
                    "upload", "link", "gridlayout", "orderedlayout",
                    "formlayout", "panel", "expandlayout", "tabsheet",
                    "alignment", "alignment/grid", "window", "window/opener",
                    "window/multiple", "classresource", "usererror",
                    "progress/window", "progress/thread", "progress",
                    "customlayout", "spacing", "margin", "clientinfo",
                    "fillinform/templates"};
            for (int i = 0; i < examples.length; i++) {
                main.addComponent(new Label("<a href='/tk/testbench2/"
                        + examples[i] + "'>" + examples[i] + "</a>",
                        Label.CONTENT_XHTML));
            }
            return null;
        }

        if (example.equals("defaultbutton")) {
            example_defaultButton(main, param);
        } else if (example.equals("label")) {
            example_Label(main, param);
        } else if (example.equals("labelcontent")) {
            example_LabelContent(main, param);
        } else if (example.equals("tree")) {
            example_Tree(main, param);
        } else if (example.equals("embedded")) {
            example_Embedded(main, param);
        } else if (example.equals("textfield")) {
            example_TextField(main, param);
        } else if (example.equals("textfieldvalidation")) {
            example_TextFieldValidation(main, param);
        } else if (example.equals("usererror")) {
            example_UserError(main, param);
        } else if (example.equals("datefield")) {
            example_DateField(main, param);
        } else if (example.equals("button")) {
            example_Button(main, param);
        } else if (example.equals("checkbox")) {
            example_CheckBox(main, param);
        } else if (example.equals("select")) {
            example_Select(main, param);
        } else if (example.equals("filterselect")) {
            example_FilterSelect(main, param);
        } else if (example.equals("validator")) {
            example_Validator(main, param);
        } else if (example.equals("table")) {
            example_Table(main, param);
        } else if (example.equals("upload")) {
            example_Upload(main, param);
        } else if (example.equals("link")) {
            example_Link(main, param);
        } else if (example.equals("gridlayout")) {
            example_GridLayout(main, param);
        } else if (example.equals("orderedlayout")) {
            example_OrderedLayout(main, param);
        } else if (example.equals("formlayout")) {
            example_FormLayout(main, param);
        } else if (example.equals("tabsheet")) {
            example_TabSheet(main, param);
        } else if (example.equals("panel")) {
            example_Panel(main, param);
        } else if (example.equals("expandlayout")) {
            example_ExpandLayout(main, param);
        } else if (example.equals("alignment")) {
            example_Alignment(main, param);
        } else if (example.equals("window")) {
            example_Window(main, param);
        } else if (example.equals("classresource")) {
            example_ClassResource(main, param);
        } else if (example.equals("progress")) {
            example_ProgressIndicator(main, param);
        } else if (example.equals("customlayout")) {
            example_CustomLayout(main, param);
        } else if (example.equals("spacing")) {
            example_Spacing(main, param);
        } else if (example.equals("margin")) {
            example_Margin(main, param);
        } else if (example.equals("clientinfo")) {
            example_ClientInfo(main, param);
        } else if (example.equals("fillinform")) {
            example_FillInForm(main, param);
        } else {
            ; // main.addComponent(new Label("Unknown test '"+example+"'."));
        }

        return null;
    }

    /*
     * public Window getWindow(String name) { Window superwin =
     * super.getWindow(name); if (superwin != null) return superwin;
     * 
     * main.addComponent(new Label("Request 2 for window '"+name+"'.")); if
     * (name.equals("panel")) { Window window = new Window("Other Window " +
     * getwincount++); example_Panel(window, null); return window; } return
     * null; }
     */
    public void handleButton(Button.ClickEvent event) {
        ol.addStyleName("myLayout2");
    }

    void example_defaultButton(Window main, String param) {
        main.addComponent(new DefaultButtonExample(main));
    }

    void example_Label(Window main, String param) {
        /* Some container for the Label. */
        final Panel panel = new Panel("Panel Containing a Label");
        main.addComponent(panel);

        panel.addComponent(new Label(
                "This is a Label inside a Panel. There is enough "
                        + "text in the label to make the text wrap if it "
                        + "exceeds the width of the panel."));
    }

    void example_LabelContent(Window main, String param) {
        final GridLayout labelgrid = new GridLayout(2, 1);
        labelgrid.addStyleName("labelgrid");
        labelgrid.addComponent(new Label("CONTENT_DEFAULT"));
        labelgrid.addComponent(new Label(
                "This is a label in default mode: <plain text>",
                Label.CONTENT_DEFAULT));
        labelgrid.addComponent(new Label("CONTENT_PREFORMATTED"));
        labelgrid
                .addComponent(new Label(
                        "This is a preformatted label.\nThe newline character \\n breaks the line.",
                        Label.CONTENT_PREFORMATTED));
        labelgrid.addComponent(new Label("CONTENT_RAW"));
        labelgrid
                .addComponent(new Label(
                        "This is a label in raw mode.<br>It can contain, for example, unbalanced markup.",
                        Label.CONTENT_RAW));
        labelgrid.addComponent(new Label("CONTENT_TEXT"));
        labelgrid.addComponent(new Label(
                "This is a label in (plain) text mode", Label.CONTENT_TEXT));
        labelgrid.addComponent(new Label("CONTENT_XHTML"));
        labelgrid.addComponent(new Label(
                "<i>This</i> is an <b>XHTML<b> formatted label",
                Label.CONTENT_XHTML));
        labelgrid.addComponent(new Label("CONTENT_XML"));
        labelgrid.addComponent(new Label(
                "This is an <myelement>XML</myelement> formatted label",
                Label.CONTENT_XML));
        main.addComponent(labelgrid);

        final ClassResource labelimage = new ClassResource("smiley.jpg", this);
        main.addComponent(new Label("Here we have an image <img src=\""
                + getRelativeLocation(labelimage) + "\"/> within some text.",
                Label.CONTENT_XHTML));
    }

    void example_Tree(Window main, String param) {
        final Object[][] planets = new Object[][] {
                new Object[] { "Mercury" },
                new Object[] { "Venus" },
                new Object[] { "Earth", "The Moon" },
                new Object[] { "Mars", "Phobos", "Deimos" },
                new Object[] { "Jupiter", "Io", "Europa", "Ganymedes", "Callisto" },
                new Object[] { "Saturn", "Titan", "Tethys", "Dione", "Rhea", "Iapetus" },
                new Object[] { "Uranus", "Miranda", "Ariel", "Umbriel", "Titania", "Oberon" },
                new Object[] { "Neptune", "Triton", "Proteus", "Nereid", "Larissa" } };

        final Tree tree = new Tree();

        /* Add planets as root items in the tree. */
        for (int i = 0; i < planets.length; i++) {
            final String planet = (String) (planets[i][0]);
            tree.addItem(planet);

            if (planets[i].length == 1) {
                /* The planet has no moons so make it a leaf. */
                tree.setChildrenAllowed(planet, false);
            } else {
                /* Add children (moons) under the planets. */
                for (int j = 1; j < planets[i].length; j++) {
                    final String moon = (String) planets[i][j];

                    /* Add the item as a regular item. */
                    tree.addItem(moon);

                    /* Set it to be a child. */
                    tree.setParent(moon, planet);

                    /* Make the moons look like leaves. */
                    tree.setChildrenAllowed(moon, false);
                }

                /* Expand the subtree. */
                tree.expandItemsRecursively(planet);
            }
        }
        
        // Horizontal layout with the tree on the left and a details panel on the right. 
        final ExpandLayout horlayout = new ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        horlayout.addStyleName("treeexample");
        horlayout.setSizeFull();
        
        final Panel treepanel = new Panel("The Planets and Major Moons");
        treepanel.addComponent(tree);
        horlayout.addComponent(treepanel);
        
        final Panel detailspanel = new Panel("Details");
        horlayout.addComponent(detailspanel);
        horlayout.expand(detailspanel);
        
        final OrderedLayout detailslayout = new OrderedLayout();
        detailspanel.setLayout(detailslayout);
        
        // When a tree item (planet or moon) is clicked, open the item in Details view.
        tree.setImmediate(true);
        tree.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                String planet = (String) tree.getValue();
                detailspanel.setCaption("Details on " + planet);
                detailslayout.removeAllComponents();
                
                // Put some stuff in the Details view.
                detailslayout.addComponent(new Label("Where is the cat?"));
                detailslayout.addComponent(new Label("The cat is in " + planet + "."));
            }
        });

        main.setLayout(horlayout);
    }

    void example_Select(Window main, String param) {
        final OrderedLayout layout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        layout.addStyleName("aligntop");

        if (param.equals("twincol")) {
            final SelectExample select1 = new SelectExample(this, param,
                    "Select some items", true);
            layout.addComponent(select1);
        } else if (param.equals("filter")) {
            final SelectExample select1 = new SelectExample(this, param,
                    "Enter containing substring", false);
            layout.addComponent(select1);
        } else {
            final SelectExample select1 = new SelectExample(this, param,
                    "Single Selection Mode", false);
            final SelectExample select2 = new SelectExample(this, param,
                    "Multiple Selection Mode", true);
            layout.addComponent(select1);
            layout.addComponent(select2);
        }
        main.addComponent(layout);
    }

    void example_FilterSelect(Window main, String param) {
        final Select select = new Select("Enter containing substring");
        main.addComponent(select);

        select
                .setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);

        /* Fill the component with some items. */
        final String[] planets = new String[] { "Mercury", "Venus", "Earth",
                "Mars", "Jupiter", "Saturn", "Uranus", "Neptune" };

        for (int i = 0; i < planets.length; i++) {
            for (int j = 0; j < planets.length; j++) {
                select.addItem(planets[j] + " to " + planets[i]);
            }
        }
    }

    void example_TextField(Window main, String param) {
        /* Add a single-line text field. */
        final TextField subject = new TextField("Subject");
        subject.setColumns(40);
        main.addComponent(subject);

        /* Add a multi-line text field. */
        final TextField message = new TextField("Message");
        message.setRows(7);
        message.setColumns(40);
        main.addComponent(message);
    }

    void example_TextFieldValidation(Window main, String param) {
        // Create a text field with a label
        final TextField username = new TextField("Username");
        main.addComponent(username);

        // Set visible length to 16 characters
        username.setColumns(16);

        // Set content length to minimum of 6 and maximum of 16 characters.
        // The string also may not be null.
        username.addValidator(new StringLengthValidator(
                "Must be 6 to 16 characters long", 6, 16, false));

        // Setting component immediate causes a ValueChangeEvent to occur
        // when the TextField loses focus.
        username.setImmediate(true);

        // Listen for ValueChangeEvents and handle them
        username.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // Get the source of the event
                final TextField username = (TextField) (event.getProperty());

                try {
                    // Validate the field value.
                    username.validate();
                } catch (final Validator.InvalidValueException e) {
                    // The value was not ok. The error was set.
                }
            }
        });
    }

    void example_UserError(final Window main, String param) {
        if (param != null) {
            if (param.equals("form")) {

                final FormLayout layout = new FormLayout();
                main.addComponent(layout);

                final TextField textfield = new TextField("Enter name");
                layout.addComponent(textfield);
                textfield.setComponentError(null);

                final Button button = new Button("Click me!");
                layout.addComponent(button);

                button.addListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (((String) textfield.getValue()).length() == 0) {
                            textfield.setComponentError(new UserError(
                                    "Must not be empty"));
                        } else {
                            textfield.setComponentError(null);
                        }
                    }
                });
            }
        } else {
            final TextField textfield = new TextField("Enter name");
            main.addComponent(textfield);
            textfield.setComponentError(null);

            final Button button = new Button("Click me!");
            main.addComponent(button);

            button.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (((String) textfield.getValue()).length() == 0) {
                        textfield.setComponentError(new UserError(
                                "Must not be empty"));
                    } else {
                        textfield.setComponentError(null);
                    }
                }
            });
        }
    }

    void example_DateField(Window main, String param) {
        OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        
        /* Create a DateField with the calendar style. */
        final DateField popupdate = new PopupDateField("Popup calendar field");

        /* Set resolution of the date/time display. */
        popupdate.setResolution(DateField.RESOLUTION_MIN);

        /* Set the date and time to present. */
        popupdate.setValue(new java.util.Date());

        /* Create a DateField with the calendar style. */
        final DateField inlinedate = new InlineDateField("Inline calendar field");
        
        /* Set locale of the DateField to American English. */
        inlinedate.setLocale(new Locale("en", "US"));

        /* Set the date and time to present. */
        inlinedate.setValue(new java.util.Date());

        /* Set resolution of the date/time display. */
        inlinedate.setResolution(DateField.RESOLUTION_MIN);

        layout.addComponent(popupdate);
        layout.addComponent(inlinedate);
        layout.setSpacing(true);
        main.addComponent(layout);
    }

    void example_Validator(Window main, String param) {
        main.addComponent(new SSNField());
    }

    void example_Table(Window main, String param) {
        main.addComponent(new TableExample());
    }

    void example_Upload(Window main, String param) {
        main.addComponent(new MyUploader());
    }

    void example_Link(Window main, String param) {

        /* Create a link that opens the popup window. */
        final Link alink = new Link();

        /* Set the resource to be opened in the window. */
        alink.setResource(new ExternalResource("http://www.itmill.com/"));

        main.addComponent(alink);

        final ClassResource mydocument = new ClassResource("mydocument.pdf",
                this);
        main.addComponent(new Link("The document (pdf)", mydocument));
        main.addComponent(new Link("link to a resource", new ExternalResource(
                "http://www.itmill.com/")));
    }

    void example_Button(Window main, String param) {
        if (param != null) {
            if (param.equals("buttons")) {
                main.addComponent(new TheButton());
            }
            return;
        }

        // butts1 = new TheButton ();
        // main.addComponent(butts1);

        // butts2 = new TheButtons (main);
        // butts3 = new TheButtons2 (main);

        // Button checkbox = new Button ("This is a checkbox");

        // main.addComponent(checkbox);
        final Button button = new Button("My Button");
        main.addComponent(button);
    }

    void example_CheckBox(Window main, String param) {
        /* A check box with default state (not checked, i.e., false). */
        final CheckBox checkbox1 = new CheckBox("My CheckBox");
        main.addComponent(checkbox1);

        /* Another check box with explicitly set checked state. */
        final CheckBox checkbox2 = new CheckBox("Checked CheckBox");
        /*
         * @TODO: Build fails here, why? checkbox2.setValue(true);
         */
        main.addComponent(checkbox2);

        /*
         * Make some application logic. We use anynymous listener classes here.
         * The above references were defined as "final" to allow accessing them
         * from inside anonymous classes.
         */
        checkbox1.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                /* Copy the value to the other checkbox. */
                checkbox2.setValue(checkbox1.getValue());
            }
        });
        checkbox2.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                /* Copy the value to the other checkbox. */
                checkbox1.setValue(checkbox2.getValue());
            }
        });
    }

    void example_Panel(Window main, String param) {
        // Create a panel with a caption.
        final Panel panel = new Panel("Contact Information");

        // Create a layout inside the panel
        final OrderedLayout form = new FormLayout();

        // Set the layout as the root layout of the panel
        panel.setLayout(form);

        // Add some components
        form.addComponent(new TextField("Name"));
        form.addComponent(new TextField("Email"));

        // Add the panel to the main window
        final ClassResource icon = new ClassResource("smiley.jpg", main
                .getApplication());
        form.addComponent(new Embedded("Image", icon));
        panel.setIcon(icon);
        panel.addComponent(form);
        main.addComponent(panel);
    }

    void example_GridLayout(Window main, String param) {
        /* Create a 4 by 4 grid layout. */
        final GridLayout grid = new GridLayout(4, 4);
        grid.addStyleName("example-gridlayout");

        /* Fill out the first row using the cursor. */
        grid.addComponent(new Button("R/C 1"));
        for (int i = 0; i < 3; i++) {
            grid.addComponent(new Button("Col " + (grid.getCursorX() + 1)));
        }

        /* Fill out the first column using coordinates. */
        for (int i = 1; i < 4; i++) {
            grid.addComponent(new Button("Row " + i), 0, i);
        }

        /* Add some components of various shapes. */
        grid.addComponent(new Button("3x1 button"), 1, 1, 3, 1);
        grid.addComponent(new Label("1x2 cell"), 1, 2, 1, 3);
        final InlineDateField date = new InlineDateField("A 2x2 date field");
        date.setResolution(DateField.RESOLUTION_DAY);
        grid.addComponent(date, 2, 2, 3, 3);

        main.addComponent(grid);
    }

    void example_Alignment(Window main, String param) {
        if (param.equals("grid")) {
            /* Create a 3 by 3 grid layout. */
            final GridLayout layout = new GridLayout(3, 3);
            // OrderedLayout layout = new
            // OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
            main.setLayout(layout);
            layout.addStyleName("example-alignment");

            layout.setWidth(400, Sizeable.UNITS_PIXELS);
            layout.setHeight(400, Sizeable.UNITS_PIXELS);

            /* Define cells and their layouts to create. */
            
             Object cells[][] = {
                     {new Button("Top Left"),      new Integer(OrderedLayout.ALIGNMENT_LEFT),              new Integer(OrderedLayout.ALIGNMENT_TOP)},
                     {new Label("Top Center"),    new Integer(OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER), new Integer(OrderedLayout.ALIGNMENT_TOP)},
                     {new Label("Top Right"),     new Integer(OrderedLayout.ALIGNMENT_RIGHT),             new Integer(OrderedLayout.ALIGNMENT_TOP)},
                     {new Button("Center Left"),   new Integer(OrderedLayout.ALIGNMENT_LEFT),              new Integer(OrderedLayout.ALIGNMENT_VERTICAL_CENTER)},
                     {new Button("Center Center"), new Integer(OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER), new Integer(OrderedLayout.ALIGNMENT_VERTICAL_CENTER)},
                     {new Button("Center Right"),  new Integer(OrderedLayout.ALIGNMENT_RIGHT),             new Integer(OrderedLayout.ALIGNMENT_VERTICAL_CENTER)},
                     {new Button("Bottom Left"),   new Integer(OrderedLayout.ALIGNMENT_LEFT),              new Integer(OrderedLayout.ALIGNMENT_BOTTOM)},
                     {new Button("Bottom Center"), new Integer(OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER), new Integer(OrderedLayout.ALIGNMENT_BOTTOM)},
                     {new Button("Bottom Right"),  new Integer(OrderedLayout.ALIGNMENT_RIGHT),             new Integer(OrderedLayout.ALIGNMENT_BOTTOM)}};
             
             for (int i=0; i<9; i++) {
                 ExpandLayout celllayout = new ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
                 celllayout.addComponent((Component) cells[i][0]);
                 celllayout.setComponentAlignment((Component)cells[i][0], ((Integer)cells[i][1]).intValue(), ((Integer)cells[i][2]).intValue());
                 layout.addComponent(celllayout);
                 //layout.setComponentAlignment((Component)cells[i][0], ((Integer)cells[i][1]).intValue(), ((Integer)cells[i][2]).intValue());
             }
        } else {
            final Panel panel = new Panel("A Panel with a Layout");
            main.addComponent(panel);

            // panel.addComponent(new )
        }
    }

    void example_OrderedLayout(Window main, String param) {
        final OrderedLayout layout = new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL);
        layout.addComponent(new TextField("Name"));
        layout.addComponent(new TextField("Street address"));
        layout.addComponent(new TextField("Postal code"));
        main.addComponent(layout);
    }

    void example_FormLayout(Window main, String param) {
        final FormLayout layout = new FormLayout();
        layout.addComponent(new TextField("Name"));
        layout.addComponent(new TextField("Street address"));
        layout.addComponent(new TextField("Postal code"));
        main.addComponent(layout);
    }

    void example_ExpandLayout(Window main, String param) {
        if (param != null && param.equals("centered")) {
            Label widget = new Label("Here is text");
    
            ExpandLayout layout = new ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
            layout.addComponent(widget);
            layout.expand(widget);
            layout.setComponentAlignment(widget, OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
            layout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
            layout.setHeight(100, Sizeable.UNITS_PERCENTAGE);
    
            main.setLayout(layout);
            
            return;
        } else if (param != null && param.equals("window")) {
            Window window = new Window("Progress");
            window.setHeight(100, Sizeable.UNITS_PIXELS);
            window.setWidth(200, Sizeable.UNITS_PIXELS);
            main.addWindow(window);
            
            ProgressIndicator progress = new ProgressIndicator(new Float(0.4));
            progress.addStyleName("fullwidth");
            progress.setPollingInterval(1000000);
            progress.setIndeterminate(false);
    
            ExpandLayout layout = new ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
            layout.setHeight(100, Sizeable.UNITS_PERCENTAGE);
            layout.setComponentAlignment(progress, OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
            window.setLayout(layout);
            window.addComponent(progress);
            
            return;
        } else if (param != null && param.equals("size")) {
            ExpandLayout layout = new ExpandLayout();
            layout.setSizeFull();
            main.setLayout(layout);

            Button button = new Button("This is a button in middle of nowhere");
            layout.addComponent(button);
            layout.setComponentAlignment(button, OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
            
            return;
        }

        for (int w = 0; w < 2; w++) {
            final ExpandLayout layout = new ExpandLayout(
                    OrderedLayout.ORIENTATION_VERTICAL);

            /* Set the expanding layout as the root layout of a child window. */
            final Window window = new Window("A Child Window", layout);
            main.addWindow(window);

            /* Add some component above the expanding one. */
            layout.addComponent(new Label("Here be some component."));

            /* Create the expanding component. */
            final Table table = new Table("My Ever-Expanding Table");
            /*
             * FIXME Java 5 -> 1.4 for (int i=0; i<5; i++)
             * table.addContainerProperty("col "+(i+1), Integer.class, 0); for
             * (int j=0; j<20; j++) table.addItem(new
             * Object[]{1*j,2*j,3*j,4*j,5*j}, j);
             */
            layout.addComponent(table);

            /* Designate the table to be the expanding component. */
            layout.expand(table);

            /* Set it to use all available area. */
            table.setSizeFull();

            /* Add some component below the expanding one. */
            final Button button2 = new Button("Ok");
            layout.addComponent(button2);
            layout.setComponentAlignment(button2,
                    OrderedLayout.ALIGNMENT_RIGHT, 0);
        }
    }

    void example_TabSheet(Window main, String param) {
        if (param.equals("icon")) {
            final TabSheet tabsheet = new TabSheet();

            tabsheet.addTab(new Label("Contents of the first tab"),
                    "First Tab", new ClassResource("images/Mercury_small.png",
                            main.getApplication()));
            tabsheet.addTab(new Label("Contents of the second tab"),
                    "Second Tab", new ClassResource("images/Venus_small.png",
                            this));
            tabsheet.addTab(new Label("Contents of the third tab"),
                    "Third tab", new ClassResource("images/Earth_small.png",
                            this));

            main.addComponent(tabsheet);
            // main.addComponent(new Embedded("Emb", new ClassResource
            // ("images/Mercury_small.png", this)));
        } else if (param.equals("expanding")) {
            // Create the layout
            ExpandLayout expanding = new ExpandLayout(OrderedLayout.ORIENTATION_VERTICAL);
            
            // It is important to set the expanding layout as the root layout
            // of the containing window, in this case the main window, and not
            // use addComponent(), which would place the layout inside the
            // default root layout.
            main.setLayout(expanding);
            
            // Create a tab sheet that fills the expanding layout
            final TabSheet tabsheet = new TabSheet();
            tabsheet.addTab(new Label("Contents of the first tab"), "First Tab", null);
            tabsheet.addTab(new Label("Contents of the second tab"), "Second Tab", null);
            tabsheet.addTab(new Label("Contents of the third tab"), "Third tab", null);
            
            // Set the tabsheet to scale to full size inside its container
            tabsheet.setWidth(100, Sizeable.UNITS_PERCENTAGE);
            tabsheet.setHeight(100, Sizeable.UNITS_PERCENTAGE);

            // Add the tab sheet to the layout as usual
            expanding.addComponent(tabsheet);

            // Set the tab sheet to be the expanding component
            expanding.expand(tabsheet);
        } else if (param.equals("ordered")) {
            // Create the layout
            OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
            
            // It is important to set the expanding layout as the root layout
            // of the containing window, in this case the main window, and not
            // use addComponent(), which would place the layout inside the
            // default root layout.
            main.setLayout(layout);
            
            // Create a tab sheet that fills the expanding layout
            final TabSheet tabsheet = new TabSheet();
            tabsheet.addTab(new Label("Contents of the first tab"), "First Tab", null);
            tabsheet.addTab(new Label("Contents of the second tab"), "Second Tab", null);
            tabsheet.addTab(new Label("Contents of the third tab"), "Third tab", null);
            
            // Set the tabsheet to scale to full size inside its container
            tabsheet.setWidth(100, Sizeable.UNITS_PERCENTAGE);
            //tabsheet().setHeight(100, Sizeable.UNITS_PERCENTAGE);

            // Add the tab sheet to the layout as usual
            layout.addComponent(tabsheet);
        } else {
            main.addComponent(new TabSheetExample());
        }
    }

    void example_Embedded(Window main, String param) {
        final Embedded image = new Embedded("", new ClassResource("smiley.jpg",
                this));
        image.addStyleName("omaimage");
        main.addComponent(image);

        final EmbeddedButton button = new EmbeddedButton(new ClassResource(
                "smiley.jpg", this));
        main.addComponent(button);
    }

    void example_Window(Window main, String param) {
        if (param != null) {
            if (param.equals("opener")) {
                main.addComponent(new WindowOpener("Window Opener", main));
            } else if (param.equals("multiple")) {
                /* Create a new window. */
                final Window mywindow = new Window("Second Window");
                mywindow.setName("mywindow");
                mywindow.addComponent(new Label("This is a second window."));

                /* Add the window to the application. */
                main.getApplication().addWindow(mywindow);

                /* Add link to the second window in the main window. */
                main.addComponent(new Label("Second window: <a href='"
                        + mywindow.getURL() + "'>middle-click to open</a>",
                        Label.CONTENT_XHTML));
                main.addComponent(new Label(
                        "The second window can be accessed through URL: "
                                + mywindow.getURL()));
            }
            return;
        }

        /* Create a new window. */
        final Window mywindow = new Window("My Window");
        mywindow.setName("mywindow");

        /* Add some components in the window. */
        mywindow.addComponent(new Label("A text label in the window."));
        final Button okbutton = new Button("OK");
        mywindow.addComponent(okbutton);

        /* Set window size. */
        mywindow.setHeight(200);
        mywindow.setWidth(400);

        /* Set window position. */
        mywindow.setPositionX(200);
        mywindow.setPositionY(50);

        /* Add the window to the Application object. */
        main.addWindow(mywindow);

    }

    void example_ClassResource(Window main, String param) {
        final DateField df = new DateField();
        main.addComponent(df);
        df.setIcon(new ClassResource("smiley.jpg", main.getApplication()));
        main.addComponent(new Embedded("This is Embedded", new ClassResource(
                "smiley.jpg", main.getApplication())));
    }
    
    void example_ProgressIndicator(final Window main, String param) {
        if (param != null) {
            if (param.equals("thread")) {

                // Create the indicator
                final ProgressIndicator indicator = new ProgressIndicator(new Float(0.0));
                main.addComponent(indicator);
                
                // Set polling frequency to 0.5 seconds.
                indicator.setPollingInterval(1000);
                
                //indicator.addStyleName("invisible");
                final Label text = new Label("-- Not running --");
                main.addComponent(text);
                
                // Add a button to start the progress
                final Button button = new Button("Click to start");
                main.addComponent(button);
        
                // Another thread to do some work
                class WorkThread extends Thread {
                    public void run () {
                        double current = 0.0;
                        while (true) {
                            // Do some "heavy work"
                            try {
                                sleep(50); // Sleep for 50 milliseconds
                            } catch (InterruptedException e) {}
                            
                            // Grow the progress value until it reaches 1.0.
                            current += 0.01;
                            if (current>1.0)
                                indicator.setValue(new Float(1.0));
                            else 
                                indicator.setValue(new Float(current));
                            
                            // After the progress is full for a while, stop.
                            if (current > 1.2) {
                                // Restore the state to initial.
                                indicator.setValue(new Float(0.0));
                                button.setVisible(true);
                                break;
                            }
                        }
                    }
                }
                
                // Clicking the button creates and runs a work thread
                button.addListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        final WorkThread thread = new WorkThread();
                        thread.start();
                        
                        // The button hides until the work is done.
                        button.setVisible(false);
                    }
                });
            } else if (param.equals("window")) {
                // Create a table in the main window to hold items added in the second window
                final Table table = new Table();
                table.setPageLength(5);
                table.setWidth(100, Sizeable.UNITS_PERCENTAGE);
                table.addContainerProperty("Name", String.class, "");
                main.addComponent(table);

                // Create the second window
                final Window adderWindow = new Window("Add Items");
                adderWindow.setName("win-adder");
                main.getApplication().addWindow(adderWindow);

                // Create selection component to add items to the table
                final NativeSelect select = new NativeSelect("Select item to add");
                select.setImmediate(true);
                adderWindow.addComponent(select);
                
                // Add some items to the selection
                String items[] = new String[]{"-- Select --", "Mercury", "Venus", 
                        "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
                for (int i=0; i<items.length; i++)
                    select.addItem(items[i]);
                select.setNullSelectionItemId(items[0]);
                
                // When an item is selected in the second window, add
                // table in the main window
                select.addListener(new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        // If the selected value is something else but null selection item.
                        if (select.getValue() != null) {
                            // Add the selected item to the table in the main window
                            table.addItem(new Object[]{select.getValue()}, new Integer(table.size()));
                        }
                    }
                });

                // Link to open the selection window
                Link link = new Link("Click to open second window",
                                     new ExternalResource(adderWindow.getURL()),
                                     "_new", 50, 200, Link.TARGET_BORDER_DEFAULT);
                main.addComponent(link);

                // Enable polling to update the main window
                ProgressIndicator poller = new ProgressIndicator();
                poller.addStyleName("invisible");
                main.addComponent(poller);
            } else if (param.equals("centered")) {
/*                GridLayout grid = new GridLayout(3,3);
                main.setLayout(grid);
                grid().setWidth(100, Sizeable.UNITS_PERCENTAGE);
                
                ExpandLayout layout2 = new ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
                layout2().setWidth(50, Sizeable.UNITS_PERCENTAGE);

                ProgressIndicator poller = new ProgressIndicator(new Float(0.4));
                poller.setPollingInterval(1000000);
                poller.setIndeterminate(false);
                layout2.addComponent(poller);

                grid.addComponent(layout2, 1, 1);
                */

                //ExpandLayout layout2 = new ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
                
                /*ProgressIndicator poller = new ProgressIndicator(new Float(0.4));
                poller.setPollingInterval(1000000);
                poller.setIndeterminate(false);*/
                /*layout2.addComponent(poller);
                layout2().setWidth(50, Sizeable.UNITS_PERCENTAGE);*/
                
                //layout.setComponentAlignment(poller, OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
                /*GridLayout grid = new GridLayout(1,1);
                grid.addComponent(layout2, 0, 0);
                grid().setWidth(100, Sizeable.UNITS_PERCENTAGE);*/
                
                /*GridLayout layout = new GridLayout(1,1); //OrderedLayout.ORIENTATION_HORIZONTAL);
                layout.addComponent(poller);
                //layout.expand(poller);
                layout.setComponentAlignment(poller, OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
                layout().setWidth(100, Sizeable.UNITS_PERCENTAGE);
                layout().setHeight(100, Sizeable.UNITS_PERCENTAGE);*/
                
            }
        } else {
            ProgressIndicator poller = new ProgressIndicator(new Float(0.0));
            poller.setPollingInterval(1000000);
            poller.setIndeterminate(true);
            main.addComponent(poller);
        }
    }
    
    void example_CustomLayout(final Window main, String param) {
        Window sub = new Window("Login");
        sub.setModal(true);
        main.addWindow(sub);
        
        // Create the custom layout and set it as the root layout of
        // the containing window.
        CustomLayout custom = new CustomLayout("layoutname");
        sub.setLayout(custom);
        
        // Create components and bind them to the location tags
        // in the custom layout.
        TextField username = new TextField();
        custom.addComponent(username, "username");
        
        TextField password = new TextField();
        custom.addComponent(password, "password");
        
        Button ok = new Button("Login");
        custom.addComponent(ok, "okbutton");
    }

    void example_Spacing(final Window main, String param) {
        OrderedLayout containinglayout = new OrderedLayout();
        main.setLayout(containinglayout);

        GridLayout grid = new GridLayout(4,3);
        grid.addStyleName("spacingexample");
        containinglayout.addComponent(grid);
        grid.addComponent(new Label(""), 0, 0);
        grid.addComponent(new Label(""), 1, 0);
        
        grid.addComponent(new Label("No spacing:"),0,1);
        OrderedLayout layout1 = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        grid.addComponent(layout1,1,1);
        layout1.addStyleName("spacingexample");
        layout1.addComponent(new Button("Component 1"));
        layout1.addComponent(new Button("Component 2"));
        layout1.addComponent(new Button("Component 3"));
        
        grid.addComponent(new Label("Horizontal spacing:"), 0, 2);
        OrderedLayout layout2 = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        grid.addComponent(layout2,1,2);
        layout2.addStyleName("spacingexample");
        layout2.setSpacing(true);
        layout2.addComponent(new Button("Component 1"));
        layout2.addComponent(new Button("Component 2"));
        layout2.addComponent(new Button("Component 3"));

        grid.addComponent(new Label("No spacing:"),2,0);
        OrderedLayout layout3 = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        grid.addComponent(layout3,2,1,2,2);
        layout3.addStyleName("spacingexample");
        layout3.addComponent(new Button("Component 1"));
        layout3.addComponent(new Button("Component 2"));
        layout3.addComponent(new Button("Component 3"));

        grid.addComponent(new Label("Vertical spacing:"),3,0);
        OrderedLayout layout4 = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        grid.addComponent(layout4,3,1,3,2);
        layout4.addStyleName("spacingexample");
        layout4.setSpacing(true);
        layout4.addComponent(new Button("Component 1"));
        layout4.addComponent(new Button("Component 2"));
        layout4.addComponent(new Button("Component 3"));
}

    void example_Margin(final Window main, String param) {
        OrderedLayout hor = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        main.setLayout(hor);
        
        OrderedLayout containinglayout = new OrderedLayout();
        hor.addComponent(containinglayout);

        OrderedLayout layout1 = new OrderedLayout();
        containinglayout.addComponent(new Label("Regular layout margins:"));
        containinglayout.addComponent(layout1);
        layout1.addStyleName("marginexample1");
        layout1.addComponent(new Button("Component 1"));
        layout1.addComponent(new Button("Component 2"));
        layout1.addComponent(new Button("Component 3"));

        // Create a layout
        OrderedLayout layout2 = new OrderedLayout();
        containinglayout.addComponent(new Label("Layout with a special margin element:"));
        containinglayout.addComponent(layout2);
        
        // Set style name for the layout to allow styling it
        layout2.addStyleName("marginexample2");
        
        // Have margin on all sides around the layout
        layout2.setMargin(true);
        
        // Put something inside the layout
        layout2.addComponent(new Button("Component 1"));
        layout2.addComponent(new Button("Component 2"));
        layout2.addComponent(new Button("Component 3"));
    }

    void example_ClientInfo(final Window main, String param) {
        // Get the client identification string
        WebApplicationContext context2 = (WebApplicationContext) getContext();
        String browserApplication = context2.getBrowser().getBrowserApplication();
        
        // Add a browser-dependent style name for the main window
        if (browserApplication.indexOf("Firefox/2") != -1)
            main.addStyleName("firefox2");
        
        // Display the client identification string
        main.addComponent(new Label(browserApplication));
    }

    void example_FillInForm(final Window main, String param) {
    	if (param.equals("templates")) {    	
    		// Create a custom layout from the fill-in-form.html template.
    		CustomLayout fillinlayout = new CustomLayout("fill-in-form");
    	
    		// The style will set the display to be "inline".
    		fillinlayout.addStyleName("fillinlayout");
    	
    		// Create the fields that occur in the text.
    		TextField field1 = new TextField();
    		TextField field2 = new TextField();
    		fillinlayout.addComponent(field1, "q1");
    		fillinlayout.addComponent(field2, "q2");
    	
    		main.addComponent(fillinlayout);
    	} else {
    		String fillintext = "The <q1> is mightier than <q2>.";
    		int pos = 0;
    		while (pos < fillintext.length()) {
    			int nexttag = fillintext.indexOf("<", pos);
    			if (nexttag == -1) {
    				
    			}
    		}
    	}
    }
}
