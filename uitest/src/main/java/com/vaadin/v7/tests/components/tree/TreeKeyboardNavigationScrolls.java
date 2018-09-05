package com.vaadin.v7.tests.components.tree;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.AlwaysFailValidator;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Tree;

public class TreeKeyboardNavigationScrolls extends TestBase {

    @Override
    protected void setup() {
        Tree tree = new Tree();
        tree.setContainerDataSource(generateHierarchicalContainer());
        tree.setImmediate(true);
        tree.addValidator(new AlwaysFailValidator("failed"));
        addComponent(tree);
    }

    private Container generateHierarchicalContainer() {
        HierarchicalContainer cont = new HierarchicalContainer();
        for (int i = 1; i < 6; i++) {
            cont.addItem(i);
            for (int j = 1; j < 3; j++) {
                String id = i
                        + " foo bar baz make this node really wide so that we don't have to fiddle with resizing the browser window -> "
                        + "what would you do if you had one of your legs on backwards? it's legs time! everybody get your legs! "
                        + "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore "
                        + "et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut "
                        + "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum "
                        + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui "
                        + "officia deserunt mollit anim id est laborum." + j;
                cont.addItem(id);
                cont.setChildrenAllowed(id, false);
                cont.setParent(id, i);
            }
        }
        return cont;
    }

    @Override
    protected String getDescription() {
        return "The tree scrolls right if the focused node is too wide when navigating with the keyboard";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7230;
    }

}
