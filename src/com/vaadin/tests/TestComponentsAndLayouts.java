/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.io.File;
import java.sql.SQLException;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.QueryContainer;
import com.vaadin.demo.util.SampleDatabase;
import com.vaadin.demo.util.SampleDirectory;
import com.vaadin.event.Action;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Select;
import com.vaadin.ui.Slider;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

/**
 * Search for "TWEAK these" keyword and configure Custom/AbstractComponents to
 * various states and see how they work inside different Layouts.
 * 
 */
public class TestComponentsAndLayouts extends Application implements Listener,
        Action.Handler {

    // event listener feedback (see console)
    Label eventListenerFeedback = new Label(
            "See console for event listener log.");
    int eventCount = 0;

    Window window;

    // component counter
    int count = 0;

    SampleDatabase sampleDatabase;

    // Example Actions for table
    private final Action ACTION1 = new Action("Upload");
    private final Action ACTION2 = new Action("Download");
    private final Action ACTION3 = new Action("Show history");
    private final Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

    @Override
    public void init() {
        sampleDatabase = new SampleDatabase();
        createNewView();
    }

    public void createNewView() {
        final Window main = new Window("Main window");
        setMainWindow(main);

        // By default push all containers inside main window
        Layout target = main.getLayout();

        main
                .addComponent(new Label(
                        "Each Layout and their contained components should "
                                + "have icon, caption, description, user error defined. "
                                + "Eeach layout should contain similar components. "
                                + "All components are in immmediate mode. See source how to tweak this test."));
        main.addComponent(eventListenerFeedback);

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            window = new Window("Components inside Window (TEST: Window)");

            if (false) {
                // push every container and their components inside window
                target = window.getLayout();
            } else {
                // window is just one container to be tested
                populateLayout(window.getLayout());
            }
            getMainWindow().addWindow(window);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target
                    .addComponent(new Label(
                            "<hr /><h1>Components inside horizontal OrderedLayout</h3>",
                            Label.CONTENT_XHTML));
            final OrderedLayout ol = new OrderedLayout(
                    OrderedLayout.ORIENTATION_HORIZONTAL);
            populateLayout(ol);
            target.addComponent(ol);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target
                    .addComponent(new Label(
                            "<br/><br/><br/><hr /><h1>Components inside vertical OrderedLayout</h3>",
                            Label.CONTENT_XHTML));
            final OrderedLayout ol2 = new OrderedLayout(
                    OrderedLayout.ORIENTATION_VERTICAL);
            populateLayout(ol2);
            target.addComponent(ol2);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target.addComponent(new Label(
                    "<hr /><h1>Components inside TabSheet</h3>",
                    Label.CONTENT_XHTML));
            final TabSheet tabsheet = new TabSheet();
            final OrderedLayout tab1 = new OrderedLayout();
            tab1.addComponent(new Label("try tab2"));
            final OrderedLayout tab2 = new OrderedLayout();
            populateLayout(tab2);
            tabsheet.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif",
                    this));
            tabsheet.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif",
                    this));
            target.addComponent(tabsheet);
            // test(tabsheet);
            // test(tab1);
            // test(tab2);
            // test(expandLayout);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (true) {
            target.addComponent(new Label(
                    "<hr /><h1>Components inside Accordion</h3>",
                    Label.CONTENT_XHTML));
            final Accordion accordion = new Accordion();
            accordion.setHeight(500);
            final OrderedLayout acc1 = new OrderedLayout();
            acc1.addComponent(new Label("try acc2"));
            final OrderedLayout acc2 = new OrderedLayout();
            populateLayout(acc2);
            accordion.addTab(acc1, "Accordion acc1", new ClassResource("m.gif",
                    this));
            accordion.addTab(acc2, "Accordion acc2", new ClassResource("m.gif",
                    this));
            target.addComponent(accordion);
            // test(accordion);
            // test(acc1);
            // test(acc2);
            // test(expandLayout);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target.addComponent(new Label(
                    "<hr /><h1>Components inside GridLayout</h3>",
                    Label.CONTENT_XHTML));
            final GridLayout gridLayout = new GridLayout(4, 100);
            populateLayout(gridLayout);
            target.addComponent(gridLayout);
            // test(gridLayout);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target
                    .addComponent(new Label(
                            "<hr /><h1>Components inside ExpandLayout (height 250px)</h3>",
                            Label.CONTENT_XHTML));
            final ExpandLayout el = new ExpandLayout();
            el.setHeight(250, Component.UNITS_PIXELS);
            el.expand(new Label("This label will expand on expand layout"));
            populateLayout(el);
            target.addComponent(el);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target.addComponent(new Label(
                    "<hr /><h1>Components inside Panel</h3>",
                    Label.CONTENT_XHTML));
            final Panel panel = new Panel("Panel");
            populateLayout(panel.getLayout());
            target.addComponent(panel);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target
                    .addComponent(new Label(
                            "<hr /><h1>Components inside vertical SplitPanel (splitpanel is under 250height ExpandLayout)</h3>",
                            Label.CONTENT_XHTML));
            final ExpandLayout sp1l = new ExpandLayout();
            sp1l.setHeight(250, ExpandLayout.UNITS_PIXELS);
            final SplitPanel sp1 = new SplitPanel(
                    SplitPanel.ORIENTATION_VERTICAL);
            sp1l.addComponent(sp1);
            final OrderedLayout sp1first = new OrderedLayout();
            final OrderedLayout sp1second = new OrderedLayout();
            sp1.setFirstComponent(sp1first);
            populateLayout(sp1first);
            populateLayout(sp1second);
            sp1.setSecondComponent(sp1second);
            target.addComponent(sp1l);
        }

        // //////////////////////////////////////////////////////////////////////
        // ////
        if (false) {
            target
                    .addComponent(new Label(
                            "<hr /><h1>Components inside horizontal SplitPanel (splitpanel is under 250px height ExpandLayout)</h3>",
                            Label.CONTENT_XHTML));
            final ExpandLayout sp2l = new ExpandLayout();
            sp2l.setHeight(250, SplitPanel.UNITS_PIXELS);
            final SplitPanel sp2 = new SplitPanel(
                    SplitPanel.ORIENTATION_HORIZONTAL);
            sp2l.addComponent(sp2);
            final OrderedLayout sp2first = new OrderedLayout();
            final OrderedLayout sp2second = new OrderedLayout();
            sp2.setFirstComponent(sp2first);
            populateLayout(sp2first);
            populateLayout(sp2second);
            sp2.setSecondComponent(sp2second);
            target.addComponent(sp2l);
        }

    }

    void populateLayout(Layout layout) {
        final Button button = new Button("Button " + count++);
        test(layout, button);

        final DateField df = new DateField("DateField " + count++);
        test(layout, df);

        final CheckBox cb = new CheckBox("Checkbox " + count++);
        test(layout, cb);

        final ClassResource flashResource = new ClassResource(
                "itmill_spin.swf", this);
        final Embedded emb = new Embedded("Embedded " + count++, flashResource);
        emb.setType(Embedded.TYPE_OBJECT);
        emb.setMimeType("application/x-shockwave-flash");
        emb.setWidth(250);
        emb.setHeight(100);
        test(layout, emb);

        final Panel panel = new Panel("Panel " + count++);
        test(layout, panel);

        final Label label = new Label("Label " + count++);
        test(layout, label);

        final Link link = new Link("Link " + count++, new ExternalResource(
                "www.vaadin.com"));
        test(layout, link);

        final NativeSelect nativeSelect = new NativeSelect("NativeSelect "
                + count++);
        nativeSelect.setContainerDataSource(getContainer());
        test(layout, nativeSelect);

        final OptionGroup optionGroup = new OptionGroup("OptionGroup "
                + count++);
        optionGroup.setContainerDataSource(getSmallContainer());
        optionGroup.setItemCaptionPropertyId("UNIT");
        test(layout, optionGroup);

        // final ProgressIndicator pi = new ProgressIndicator();
        // pi.setCaption("ProgressIndicator");
        // test(layout, pi);

        final RichTextArea rta = new RichTextArea();
        test(layout, rta);

        final Select select = new Select("Select " + count++);
        select.setContainerDataSource(getSmallContainer());
        select.setItemCaptionPropertyId("UNIT");
        test(layout, select);

        final Slider slider = new Slider("Slider " + count++);
        test(layout, slider);

        final Table table = new Table("Table " + count++);
        table.setPageLength(10);
        table.setSelectable(true);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.addActionHandler(this);
        table.setContainerDataSource(getContainer());
        table.setVisibleColumns(new Object[] { "FIRSTNAME", "LASTNAME",
                "TITLE", "UNIT" });
        table.setItemCaptionPropertyId("ID");
        test(layout, table);

        final TabSheet tabsheet = new TabSheet();
        final OrderedLayout tab1 = new OrderedLayout();
        tab1.addComponent(new Label("tab1 " + count++));
        final OrderedLayout tab2 = new OrderedLayout();
        tab2.addComponent(new Label("tab2 " + count++));
        tabsheet.addTab(tab1, "Default (not configured) TabSheet tab1",
                new ClassResource("m.gif", this));
        tabsheet.addTab(tab2, "Configured TabSheet tab2", new ClassResource(
                "m.gif", this));
        test(layout, tabsheet);

        final Accordion accordion = new Accordion();
        final OrderedLayout acc1 = new OrderedLayout();
        acc1.addComponent(new Label("acc1 " + count++));
        final OrderedLayout acc2 = new OrderedLayout();
        acc2.addComponent(new Label("acc2 " + count++));
        accordion.addTab(acc1, "Default (not configured) Accordion acc1",
                new ClassResource("m.gif", this));
        accordion.addTab(acc2, "Configured Accordion acc2", new ClassResource(
                "m.gif", this));
        test(layout, accordion);

        final TextField tf = new TextField("Textfield " + count++);
        test(layout, tf);
        // do not configure acc1
        // test(acc1);
        test(acc2);

        final Tree tree = new Tree("Tree " + count++);
        final File sampleDir = SampleDirectory.getDirectory(this);
        final FilesystemContainer fsc = new FilesystemContainer(sampleDir, true);
        tree.setContainerDataSource(fsc);
        test(layout, tree);

        final TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect "
                + count++);
        twinColSelect.setContainerDataSource(getSmallContainer());
        twinColSelect.setItemCaptionPropertyId("UNIT");
        test(layout, twinColSelect);

        final Upload upload = new Upload("Upload (non-functional)", null);
        test(layout, upload);

        // Custom components
        layout.addComponent(new Label("<B>Below are few custom components</B>",
                Label.CONTENT_XHTML));
        final TestForUpload tfu = new TestForUpload();
        layout.addComponent(tfu);
        layout.addComponent(new Label("<br/><b>----------<br/></p>",
                Label.CONTENT_XHTML));
        test(tfu);

        // DISABLED
        // TableSelectTest tst = new TableSelectTest();
        // layout.addComponent(tst);
        // test(tst);
        // layout.addComponent(new Label("<HR />", Label.CONTENT_XHTML));

    }

    Container getContainer() {
        // populate container with test SQL table rows
        try {
            return new QueryContainer("SELECT * FROM employee", sampleDatabase
                    .getConnection());
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    Container getSmallContainer() {
        // populate container with test SQL table rows
        try {
            return new QueryContainer(
                    "SELECT DISTINCT UNIT AS UNIT FROM employee",
                    sampleDatabase.getConnection());
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // common component configuration
    void setComponentProperties(Component c) {
        // TWEAK these
        // c.setEnabled(false);
        // c.setVisible(false);
        // c.setStyleName("testStyleName");
        // c.setReadOnly(true);

        // try to add listener
        try {
            c.addListener(this);
        } catch (final Exception e) {
            System.err.println("Could not add listener for component " + c
                    + ", count was " + count);
        }
    }

    /**
     * Stresses component by configuring it
     * 
     * @param c
     */
    void test(AbstractComponent c) {
        // configure common component properties
        // setComponentProperties(c);

        // AbstractComponent specific configuration
        final ClassResource res = new ClassResource("m.gif", this);
        final ErrorMessage errorMsg = new UserError("User error " + c);
        if ((c.getCaption() == null) || (c.getCaption().length() <= 0)) {
            c.setCaption("Caption " + c);
        }

        // TWEAK these
        // c.setComponentError(errorMsg);
        // c.setIcon(res);
        // c.setImmediate(true);
        // c.addStyleName("addedTestStyleName");
        // c.setStyleName("singleTestStyleName");
        // c.setDescription("Description here..");
    }

    void test(CustomComponent c) {
        // configure common component properties
        setComponentProperties(c);

        // CustomComponent specific configuration
        // TWEAK these
        // c.setComponentType("foo");
        c.addStyleName("addedTestStyleName");
    }

    /**
     * Stresses component by configuring it in a given layout
     * 
     * @param c
     */
    void test(Layout layout, AbstractComponent c) {
        test(c);
        layout.addComponent(c);
        // add separator
        if (!(layout instanceof GridLayout)) {
            layout.addComponent(new Label("<br/><b>NEXT<br/></p>",
                    Label.CONTENT_XHTML));
        }
    }

    public void componentEvent(Event event) {
        eventCount++;
        final String feedback = "eventCount=" + eventCount + ", class="
                + event.getClass() + ", source=" + event.getSource()
                + ", toString()=" + event.toString();
        System.out.println("eventListenerFeedback: " + feedback);
        eventListenerFeedback.setValue("Events: " + eventCount);
    }

    // For sample actions
    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    // For sample actions
    public void handleAction(Action action, Object sender, Object target) {
        System.out.println("ACTION: " + action.getCaption() + " on item "
                + target);
    }

}
