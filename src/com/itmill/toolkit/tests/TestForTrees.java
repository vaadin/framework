/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * Some test cases for trees. Events panel logs events that happen server side.
 * 
 * @author IT Mill Ltd.
 */
public class TestForTrees extends CustomComponent implements Handler {

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

    public TestForTrees() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main
                .addComponent(new Label(
                        "Some test cases for trees. Events panel logs events that happen server side."));

        main.addComponent(new Button("commit"));

        Tree t;

        t = createTestTree();
        t.setCaption("Default settings");
        main.addComponent(createTestBench(t));

        t = createTestTree();
        t.setCaption("Multiselect settings");
        t.setMultiSelect(true);
        main.addComponent(createTestBench(t));

        t = createTestTree();
        t.setCaption("Multiselect and immediate");
        t.setImmediate(true);
        t.setMultiSelect(true);
        main.addComponent(createTestBench(t));

        t = createTestTree();
        t.setCaption("immediate");
        t.setImmediate(true);
        main.addComponent(createTestBench(t));

        t = createTestTree();
        t.setCaption("with actions");
        t.setImmediate(true);
        t.addActionHandler(this);
        final OrderedLayout ol = (OrderedLayout) createTestBench(t);
        al = new Panel("action log");
        ol.addComponent(al);
        main.addComponent(ol);
        contextTree = t;

        final Button b = new Button("refresh view", this, "createNewView");
        main.addComponent(b);

    }

    public Tree createTestTree() {
        Tree t = new Tree("Tree");
        final String[] names = new String[100];
        for (int i = 0; i < names.length; i++) {
            names[i] = firstnames[(int) (Math.random() * (firstnames.length - 1))]
                    + " "
                    + lastnames[(int) (Math.random() * (lastnames.length - 1))];
        }

        // Create tree
        t = new Tree("Organization Structure");
        for (int i = 0; i < 100; i++) {
            t.addItem(names[i]);
            final String parent = names[(int) (Math.random() * (names.length - 1))];
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

    public Component createTestBench(Tree t) {
        final OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        ol.addComponent(t);

        final Panel status = new Panel("Events");
        final Button clear = new Button("c");
        clear.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                status.removeAllComponents();
                status.addComponent(clear);
            }
        });
        status.addComponent(clear);

        status.setHeight("300px");
        status.setWidth("400px");

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
