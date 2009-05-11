/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
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
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Select;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Component.Listener;

public class TestCaptionWrapper extends CustomComponent implements Listener {

    OrderedLayout main = new OrderedLayout();

    final String eventListenerString = "Component.Listener feedback: ";
    Label eventListenerFeedback = new Label(eventListenerString
            + " <no events occured>");
    int count = 0;

    public TestCaptionWrapper() {
        setCompositionRoot(main);
    }

    @Override
    public void attach() {
        super.attach();
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();

        main
                .addComponent(new Label(
                        "Each Layout and their contained components should "
                                + "have icon, caption, description, user error defined. "
                                + "Eeach layout should contain similar components."));

        main.addComponent(eventListenerFeedback);

        main.addComponent(new Label("OrderedLayout"));
        test(main);
        populateLayout(main);

        final Panel panel = new Panel("Panel");
        test(panel);
        populateLayout(panel.getLayout());

        final TabSheet tabsheet = new TabSheet();
        test(tabsheet);
        final OrderedLayout tab1 = new OrderedLayout();
        tab1.addComponent(new Label("try tab2"));
        final OrderedLayout tab2 = new OrderedLayout();
        test(tab2);
        populateLayout(tab2);
        tabsheet.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif",
                getApplication()));
        tabsheet.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif",
                getApplication()));

        final ExpandLayout expandLayout = new ExpandLayout();
        test(expandLayout);
        populateLayout(expandLayout);

        final GridLayout gridLayout = new GridLayout();
        test(gridLayout);
        populateLayout(gridLayout);

        final Window window = new Window("TEST: Window");
        test(window);
        populateLayout(window.getLayout());

    }

    void populateLayout(Layout layout) {

        final Button button = new Button("Button " + count++);
        test(layout, button);
        button.addListener(this);

        final DateField df = new DateField("DateField " + count++);
        test(layout, df);

        final CheckBox cb = new CheckBox("Checkbox " + count++);
        test(layout, cb);

        final Embedded emb = new Embedded("Embedded " + count++);
        test(layout, emb);

        final Panel panel = new Panel("Panel " + count++);
        test(layout, panel);

        final Label label = new Label("Label " + count++);
        test(layout, label);

        final Link link = new Link("Link " + count++, new ExternalResource(
                "www.itmill.com"));
        test(layout, link);

        final NativeSelect nativeSelect = new NativeSelect("NativeSelect "
                + count++);
        test(layout, nativeSelect);

        final OptionGroup optionGroup = new OptionGroup("OptionGroup "
                + count++);
        test(layout, optionGroup);

        final ProgressIndicator pi = new ProgressIndicator();
        test(layout, pi);

        final RichTextArea rta = new RichTextArea();
        test(layout, rta);

        final Select select = new Select("Select " + count++);
        test(layout, select);

        final Slider slider = new Slider("Slider " + count++);
        test(layout, slider);

        final Table table = new Table("Table " + count++);
        test(layout, table);

        final TextField tf = new TextField("Textfield " + count++);
        test(layout, tf);

        final Tree tree = new Tree("Tree " + count++);
        test(layout, tree);

        final TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect "
                + count++);
        test(layout, twinColSelect);

        final Upload upload = new Upload("Upload (non-functional)", null);
        test(layout, upload);

        // Custom components
        layout.addComponent(new Label("<B>Below are few custom components</B>",
                Label.CONTENT_XHTML));
        final TestForUpload tfu = new TestForUpload();
        layout.addComponent(tfu);

    }

    /**
     * Stresses component by configuring it
     * 
     * @param c
     */
    void test(AbstractComponent c) {
        final ClassResource res = new ClassResource("m.gif", getApplication());
        final ErrorMessage errorMsg = new UserError("User error " + c);

        if ((c.getCaption() == null) || (c.getCaption().length() <= 0)) {
            c.setCaption("Caption " + c);
        }
        c.setDescription("Description " + c);
        c.setComponentError(errorMsg);
        c.setIcon(res);
    }

    /**
     * Stresses component by configuring it in a given layout
     * 
     * @param c
     */
    void test(Layout layout, AbstractComponent c) {
        test(c);
        layout.addComponent(c);
    }

    public void componentEvent(Event event) {
        final String feedback = eventListenerString + " source="
                + event.getSource() + ", toString()=" + event.toString();
        System.out.println("eventListenerFeedback: " + feedback);
        eventListenerFeedback.setValue(feedback);
    }

}
