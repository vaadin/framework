package com.vaadin.tests.components.table;

import java.util.Map;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ExpandingContainerVisibleRowRaceCondition extends UI {

    static final String TABLE = "table";

    @Override
    public void init(VaadinRequest request) {
        final VerticalLayout rootLayout = new VerticalLayout();

        rootLayout.setSpacing(true);
        rootLayout.setSizeFull();
        rootLayout.setMargin(true);

        final Label sizeLabel = new Label();
        final ExpandingContainer container = new ExpandingContainer(sizeLabel);
        container.logDetails(false);

        Table table = new Table(null, container) {
            @Override
            public void changeVariables(Object source,
                    Map<String, Object> variables) {
                if (variables.containsKey("firstvisible")) {
                    int index = (Integer) variables.get("firstvisible");
                    container.checkExpand(index);
                }
                if (variables.containsKey("reqfirstrow")
                        || variables.containsKey("reqrows")) {
                    try {
                        int index = ((Integer) variables
                                .get("lastToBeRendered")).intValue();
                        container.checkExpand(index);
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                super.changeVariables(source, variables);
            }
        };
        table.setId(TABLE);
        table.setCacheRate(0);
        table.setSizeFull();
        table.setVisibleColumns(ExpandingContainer.PROPERTY_IDS
                .toArray(new String[ExpandingContainer.PROPERTY_IDS.size()]));

        table.setCurrentPageFirstItemIndex(120);

        rootLayout.addComponent(table);
        rootLayout.setExpandRatio(table, 1);

        rootLayout.addComponent(sizeLabel);

        setContent(rootLayout);

        container.checkExpand(300);
    }

}
