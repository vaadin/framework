package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;

public class GridLayoutExtraSpacing extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().getPage().getStyles().add(
                ".v-gridlayout {background: red;} .v-csslayout {background: white;}");

        final GridLayout gl = new GridLayout(4, 4);

        final CheckBox cb = new CheckBox("spacing");
        cb.addValueChangeListener(event -> gl.setSpacing(cb.getValue()));
        cb.setValue(true);
        addComponent(cb);

        final CheckBox cb2 = new CheckBox("hide empty rows/columns");
        cb2.addValueChangeListener(
                event -> gl.setHideEmptyRowsAndColumns(cb2.getValue()));
        addComponent(cb2);
        gl.setWidth("1000px");
        gl.setHeight("500px");

        CssLayout ta = new CssLayout();
        ta.setSizeFull();
        // Only on last row
        gl.addComponent(ta, 0, 3, 3, 3);

        gl.setRowExpandRatio(3, 1);
        addComponent(gl);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
