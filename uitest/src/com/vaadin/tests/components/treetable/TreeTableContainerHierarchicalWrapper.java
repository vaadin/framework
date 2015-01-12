package com.vaadin.tests.components.treetable;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;

@SuppressWarnings("serial")
public class TreeTableContainerHierarchicalWrapper extends AbstractTestUI {

    TreeTable treetable = new TreeTable();
    BeanItemContainer<Bean> beanContainer = new BeanItemContainer<Bean>(
            Bean.class);
    ContainerHierarchicalWrapper hierarchicalWrapper = new ContainerHierarchicalWrapper(
            beanContainer);

    @Override
    protected void setup(VaadinRequest request) {
        treetable = new TreeTable();
        treetable.setImmediate(true);
        treetable.setWidth("100%");
        treetable.setHeight(null);
        treetable.setPageLength(0);
        treetable.setContainerDataSource(hierarchicalWrapper);

        treetable.addExpandListener(new ExpandListener() {
            @Override
            public void nodeExpand(ExpandEvent event) {
                Bean parent = ((Bean) event.getItemId());
                if (!hierarchicalWrapper.hasChildren(parent)) {
                    for (int i = 1; i <= 5; i++) {
                        Bean newChild = new Bean(parent.getId() + "-" + i);
                        beanContainer.addBean(newChild);
                        hierarchicalWrapper.setParent(newChild, parent);
                    }
                }

            }
        });

        for (int i = 0; i < 3; i++) {
            beanContainer.addBean(new Bean("Item " + i));
        }

        addComponent(treetable);
    }

    public class Bean {
        public static final String PROP_ID = "id";
        private String id;

        public Bean() {
            // empty
        }

        public Bean(String id) {
            this.id = id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Tests that TreeTable with ContainerHierarchicalWrapper is updated correctly when the setParent() is called for the item just added";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15421;
    }

}
