/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import java.util.Iterator;

import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Tree;

public class FeatureTree extends Feature implements Action.Handler {

    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Josie", "Linus" };

    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Jones", "Beck", "Sheridan", "Picard", "Hill", "Fielding",
            "Einstein" };

    private Tree t;

    private boolean actionsActive = false;

    private final Button actionHandlerSwitch = new Button("Activate actions",
            this, "toggleActions");

    public FeatureTree() {
        super();
    }

    public void toggleActions() {
        if (actionsActive) {
            t.removeActionHandler(this);
            actionsActive = false;
            actionHandlerSwitch.setCaption("Activate Actions");
        } else {
            t.addActionHandler(this);
            actionsActive = true;
            actionHandlerSwitch.setCaption("Deactivate Actions");
        }
    }

    public void expandAll() {
        for (final Iterator i = t.rootItemIds().iterator(); i.hasNext();) {
            t.expandItemsRecursively(i.next());
        }
    }

    public void collapseAll() {
        for (final Iterator i = t.rootItemIds().iterator(); i.hasNext();) {
            t.collapseItemsRecursively(i.next());
        }
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

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

        l.addComponent(t);

        // Actions
        l.addComponent(actionHandlerSwitch);

        // Expand and Collapse buttons
        l.addComponent(new Button("Expand All", this, "expandAll"));
        l.addComponent(new Button("Collapse All", this, "collapseAll"));

        // Properties
        propertyPanel = new PropertyPanel(t);
        final Form ap = propertyPanel
                .createBeanPropertySet(new String[] { "selectable" });
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("menu").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("menu");
        propertyPanel.addProperties("Tree Properties", ap);

        setJavadocURL("ui/Tree.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "// Create tree\n"
                + "t = new Tree(\"Organization Structure\");\n"
                + "for (int i = 0; i < 100; i++) {\n"
                + "	t.addItem(names[i]);\n"
                + "	String parent = names[(int) (Math.random() * (names.length - 1))];\n"
                + "	if (t.containsId(parent)) \n"
                + "		t.setParent(names[i],parent);\n"
                + "}\n\n"
                + "// Forbid childless people to have children (makes them leaves)\n"
                + "for (int i = 0; i < 100; i++)\n"
                + "	if (!t.hasChildren(names[i]))\n"
                + "		t.setChildrenAllowed(names[i], false);\n";
    }

    @Override
    protected String getDescriptionXHTML() {
        return "A tree is a natural way to represent datasets that have"
                + " hierarchical relationships, such as filesystems, message "
                + "threads or, as in this example, organization structure. IT Mill Toolkit features a versatile "
                + "and powerful Tree component that works much like the tree components "
                + "of most modern operating systems."
                + "<br /><br />The most prominent use of the Tree component is to "
                + "use it for displaying a hierachical menu, like the "
                + "menu on the left side of the screen for instance "
                + "or to display filesystems or other hierarchical datasets."
                + "<br /><br />The tree component uses <code>Container</code> "
                + "datasources much like the Table component, "
                + "with the addition that it also utilizes the hierarchy "
                + "information maintained by the container."
                + "<br /><br />On the demo tab you can try out how the different properties "
                + "affect the presentation of the tree component.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "Tree";
    }

    private final Action ACTION1 = new Action("Action 1");

    private final Action ACTION2 = new Action("Action 2");

    private final Action ACTION3 = new Action("Action 3");

    private final Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        t.setDescription("Last action clicked was '" + action.getCaption()
                + "' on item '" + target + "'");
    }
}
