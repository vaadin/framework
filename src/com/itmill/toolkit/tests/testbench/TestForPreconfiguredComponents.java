package com.itmill.toolkit.tests.testbench;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.TwinColSelect;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * @author IT Mill Ltd.
 */
public class TestForPreconfiguredComponents extends CustomComponent implements
        Handler {

    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Josie", "Linus" };

    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Jones", "Beck", "Sheridan", "Picard", "Hill", "Fielding",
            "Einstein" };

    private final OrderedLayout main = new OrderedLayout();

    private final Action[] actions = new Action[] { new Action("edit"),
            new Action("delete") };

    private Panel al;

    private Tree contextTree;

    public TestForPreconfiguredComponents() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main
                .addComponent(new Label(
                        "In Toolkit 5 we introduce new componens. Previously we"
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
        test
                .setCaption("OptionGroup + multiselect manually (configured from select)");
        main.addComponent(test);

        Button b = new Button("refresh view", this, "createNewView");
        main.addComponent(b);

    }

    public static void fillSelect(AbstractSelect s, int items) {
        for (int i = 0; i < items; i++) {
            String name = firstnames[(int) (Math.random() * (firstnames.length - 1))]
                    + " "
                    + lastnames[(int) (Math.random() * (lastnames.length - 1))];
            s.addItem(name);
        }
    }

    public Tree createTestTree() {
        Tree t = new Tree("Tree");
        String[] names = new String[100];
        for (int i = 0; i < names.length; i++) {
            names[i] = firstnames[(int) (Math.random() * (firstnames.length - 1))]
                    + " "
                    + lastnames[(int) (Math.random() * (lastnames.length - 1))];
        }

        // Create tree
        t = new Tree("Organization Structure");
        for (int i = 0; i < 100; i++) {
            t.addItem(names[i]);
            String parent = names[(int) (Math.random() * (names.length - 1))];
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
        Panel ol = new Panel();
        ol.setLayout(new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));

        ol.addComponent(t);

        final OrderedLayout ol2 = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        final Panel status = new Panel("Events");
        final Button clear = new Button("clear event log");
        clear.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                status.removeAllComponents();
                status.addComponent(ol2);
            }
        });
        ol2.addComponent(clear);
        final Button commit = new Button("commit changes");
        ol2.addComponent(commit);
        status.addComponent(ol2);

        status.setHeight(300);
        status.setWidth(400);

        ol.addComponent(status);

        t.addListener(new Listener() {
            public void componentEvent(Event event) {
                status.addComponent(new Label(event.getClass().getName()));
                status.addComponent(new Label("selected: "
                        + event.getSource().toString()));
            }
        });

        return ol;
    }

    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        if (action == actions[1]) {
            al.addComponent(new Label("Delete selected on " + target));
            contextTree.removeItem(target);

        } else {
            al.addComponent(new Label("Edit selected on " + target));
        }
    }
}
