package com.vaadin.data.util;

public class ReadOnlyRowId extends RowId {
    private static final long serialVersionUID = -2626764781642012467L;
    private final Integer rowNum;

    public ReadOnlyRowId(int rowNum) {
        super();
        this.rowNum = rowNum;
    }

    @Override
    public int hashCode() {
        return rowNum.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ReadOnlyRowId)) {
            return false;
        }
        return rowNum.equals(((ReadOnlyRowId) obj).rowNum);
    }

    public int getRowNum() {
        return rowNum;
    }
}
