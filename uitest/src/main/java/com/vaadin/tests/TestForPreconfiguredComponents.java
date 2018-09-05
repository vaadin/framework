package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.TwinColSelect;

/**
 * @author Vaadin Ltd.
 */
public class TestForPreconfiguredComponents extends CustomComponent {

    private static final String[] firstnames = { "John", "Mary", "Joe", "Sarah",
            "Jeff", "Jane", "Peter", "Marc", "Josie", "Linus" };

    private static final String[] lastnames = { "Torvalds", "Smith", "Jones",
            "Beck", "Sheridan", "Picard", "Hill", "Fielding", "Einstein" };

    private final VerticalLayout main = new VerticalLayout();

    public TestForPreconfiguredComponents() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
                "In Toolkit 5 we introduce new components. Previously we"
                        + " usually used setStyle or some other methods on possibly "
                        + "multiple steps to configure component for ones needs. These new "
                        + "server side components are mostly just classes that in constructor "
                        + "set base class to state that programmer wants."));

        main.addComponent(new Button("commit"));

        Panel test = createTestBench(new CheckBox());
        test.setCaption("CheckBox (configured from button)");
        main.addComponent(test);

        AbstractSelect s = new TwinColSelect();
        fillSelect(s, 20);
        test = createTestBench(s);
        test.setCaption("TwinColSelect (configured from select)");
        main.addComponent(test);

        s = new NativeSelect();
        fillSelect(s, 20);
        test = createTestBench(s);
        test.setCaption("Native (configured from select)");
        main.addComponent(test);

        s = new OptionGroup();
        fillSelect(s, 20);
        test = createTestBench(s);
        test.setCaption("OptionGroup (configured from select)");
        main.addComponent(test);

        s = new OptionGroup();
        fillSelect(s, 20);
        s.setMultiSelect(true);
        test = createTestBench(s);
        test.setCaption(
                "OptionGroup + multiselect manually (configured from select)");
        main.addComponent(test);

        final Button b = new Button("refresh view", event -> createNewView());
        main.addComponent(b);
    }

    public static void fillSelect(AbstractSelect s, int items) {
        for (int i = 0; i < items; i++) {
            final String name = firstnames[(int) (Math.random()
                    * (firstnames.length - 1))] + " "
                    + lastnames[(int) (Math.random() * (lastnames.length - 1))];
            s.addItem(name);
        }
    }

    public Tree createTestTree() {
        Tree t = new Tree("Tree");
        final String[] names = new String[100];
        for (int i = 0; i < names.length; i++) {
            names[i] = firstnames[(int) (Math.random()
                    * (firstnames.length - 1))] + " "
                    + lastnames[(int) (Math.random() * (lastnames.length - 1))];
        }

        // Create tree
        t = new Tree("Organization Structure");
        for (int i = 0; i < 100; i++) {
            t.addItem(names[i]);
            final String parent = names[(int) (Math.random()
                    * (names.length - 1))];
            if (t.containsId(parent)) {
                t.setParent(names[i], parent);
            }
        }

        // Forbid childless people to have children (makes them leaves)
        for (int i = 0; i < 100; i++) {
            if (!t.hasChildren(names[i])) {
                t.setChildrenAllowed(names[i], false);
            }
        }
        return t;
    }

    public Panel createTestBench(Component t) {
        final HorizontalLayout ol = new HorizontalLayout();

        ol.addComponent(t);

        final HorizontalLayout ol2 = new HorizontalLayout();
        final VerticalLayout statusLayout = new VerticalLayout();
        final Panel status = new Panel("Events", statusLayout);
        final Button clear = new Button("clear event log");
        clear.addClickListener(event -> {
            statusLayout.removeAllComponents();
            statusLayout.addComponent(ol2);
        });
        ol2.addComponent(clear);
        final Button commit = new Button("commit changes");
        ol2.addComponent(commit);
        statusLayout.addComponent(ol2);

        status.setHeight("300px");
        status.setWidth("400px");

        ol.addComponent(status);

        t.addListener(event -> {
            statusLayout.addComponent(new Label(event.getClass().getName()));
            // TODO should not use LegacyField.toString()
            statusLayout.addComponent(
                    new Label("selected: " + event.getSource().toString()));
        });

        return new Panel(ol);
    }
}
