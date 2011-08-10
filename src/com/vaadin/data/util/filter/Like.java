package com.vaadin.data.util.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

public class Like implements Filter {
    private final Object propertyId;
    private final String value;
    private boolean caseSensitive;

    public Like(String propertyId, String value) {
        this(propertyId, value, true);
    }

    public Like(String propertyId, String value, boolean caseSensitive) {
        this.propertyId = propertyId;
        this.value = value;
        setCaseSensitive(caseSensitive);
    }

    public Object getPropertyId() {
        return propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException {
        if (!item.getItemProperty(getPropertyId()).getType()
                .isAssignableFrom(String.class)) {
            // We can only handle strings
            return false;
        }
        String colValue = (String) item.getItemProperty(getPropertyId())
                .getValue();

        String pattern = getValue().replace("%", ".*");
        if (isCaseSensitive()) {
            return colValue.matches(pattern);
        }
        return colValue.toUpperCase().matches(pattern.toUpperCase());
    }

    public boolean appliesToProperty(Object propertyId) {
        return getPropertyId() != null && getPropertyId().equals(propertyId);
    }

    @Override
    public int hashCode() {
        return getPropertyId().hashCode() + getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Only objects of the same class can be equal
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        final Like o = (Like) obj;

        // Checks the properties one by one
        boolean propertyIdEqual = (null != getPropertyId()) ? getPropertyId()
                .equals(o.getPropertyId()) : null == o.getPropertyId();
        boolean valueEqual = (null != getValue()) ? getValue().equals(
                o.getValue()) : null == o.getValue();
        return propertyIdEqual && valueEqual;
    }
}
