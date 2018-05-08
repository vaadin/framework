package com.vaadin.tests;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Tree;

/**
 * Some test cases for trees. Events panel logs events that happen server side.
 *
 * @author Vaadin Ltd.
 */
public class TestForTrees extends CustomComponent implements Handler {

    private static final String[] firstnames = { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Josie", "Linus" };

    private static final String[] lastnames = { "Torvalds",
            "Smith", "Jones", "Beck", "Sheridan", "Picard", "Hill", "Fielding",
            "Einstein" };

    private final VerticalLayout main = new VerticalLayout();

    private final Action[] actions = { new Action("edit"),
            new Action("delete") };

    private VerticalLayout al;

    private Tree contextTree;

    public TestForTrees() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
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
        final AbstractOrderedLayout ol = (AbstractOrderedLayout) createTestBench(
                t);
        al = new VerticalLayout();
        al.setMargin(true);
        ol.addComponent(new Panel("action log", al));
        main.addComponent(ol);
        contextTree = t;

        final Button b = new Button("refresh view", event -> createNewView());
        main.addComponent(b);
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

    public Component createTestBench(Tree t) {
        final HorizontalLayout ol = new HorizontalLayout();

        ol.addComponent(t);

        final VerticalLayout statusLayout = new VerticalLayout();
        statusLayout.setMargin(true);
        final Panel status = new Panel("Events", statusLayout);
        final Button clear = new Button("c");
        clear.addClickListener(event -> {
            statusLayout.removeAllComponents();
            statusLayout.addComponent(clear);
        });
        statusLayout.addComponent(clear);

        status.setHeight("300px");
        status.setWidth("400px");

        ol.addComponent(status);

        t.addListener((Listener) event -> {
            statusLayout.addComponent(new Label(event.getClass().getName()));
            // TODO should not use LegacyField.toString()
            statusLayout.addComponent(
                    new Label("selected: " + event.getSource().toString()));
        });

        return ol;
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (action == actions[1]) {
            al.addComponent(new Label("Delete selected on " + target));
            contextTree.removeItem(target);

        } else {
            al.addComponent(new Label("Edit selected on " + target));
        }
    }
}
