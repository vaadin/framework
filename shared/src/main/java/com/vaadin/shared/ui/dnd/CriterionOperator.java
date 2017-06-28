package com.vaadin.shared.ui.dnd;

public enum CriterionOperator {

    SMALLER_THAN("<"), GREATER_THAN(">"), EQUALS("=="), SMALLER_THAN_OR_EQUALS(
            "<="), GREATER_THAN_OR_EQUALS(">=");

    private String operator;

    CriterionOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
