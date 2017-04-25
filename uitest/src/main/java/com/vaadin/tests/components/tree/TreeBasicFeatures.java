package com.vaadin.tests.components.tree;

import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.InMemoryHierarchicalDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Tree;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeBasicFeatures extends AbstractTestUIWithLog {

    static class HierarchicalTestBean {

        private final String id;
        private final int depth;
        private final int index;

        public HierarchicalTestBean(String parentId, int depth, int index) {
            id = (parentId == null ? "" : parentId) + "/" + depth + "/" + index;
            this.depth = depth;
            this.index = index;
        }

        public int getDepth() {
            return depth;
        }

        public int getIndex() {
            return index;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return depth + " | " + index;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            HierarchicalTestBean other = (HierarchicalTestBean) obj;
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!id.equals(other.id)) {
                return false;
            }
            return true;
        }

    }

    private Tree<HierarchicalTestBean> tree;
    private InMemoryHierarchicalDataProvider<HierarchicalTestBean> inMemoryDataProvider;

    @Override
    protected void setup(VaadinRequest request) {
        tree = new Tree<>();
        setupDataProvider();
        tree.setDataProvider(inMemoryDataProvider);
        addComponent(tree);

        tree.addSelectionListener(
                e -> log("SelectionEvent: " + e.getAllSelectedItems()));

        tree.addExpandListener(e -> log("ExpandEvent: " + e.getExpandedItem()));
        tree.addCollapseListener(
                e -> log("ExpandEvent: " + e.getCollapsedItem()));
    }

    private void setupDataProvider() {
        HierarchyData<HierarchicalTestBean> data = new HierarchyData<>();

        List<Integer> ints = Arrays.asList(0, 1, 2);

        ints.stream().forEach(index -> {
            HierarchicalTestBean bean = new HierarchicalTestBean(null, 0,
                    index);
            data.addItem(null, bean);
            ints.stream().forEach(childIndex -> {
                HierarchicalTestBean childBean = new HierarchicalTestBean(
                        bean.getId(), 1, childIndex);
                data.addItem(bean, childBean);
                ints.stream()
                        .forEach(grandChildIndex -> data.addItem(childBean,
                                new HierarchicalTestBean(childBean.getId(), 2,
                                        grandChildIndex)));
            });
        });

        inMemoryDataProvider = new InMemoryHierarchicalDataProvider<>(data);
    }

}
