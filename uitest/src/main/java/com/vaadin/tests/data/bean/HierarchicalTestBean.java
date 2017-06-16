package com.vaadin.tests.data.bean;

public class HierarchicalTestBean {

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
