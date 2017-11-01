package com.vaadin.tests.layouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.provider.Query;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class MovingComponentsWhileOldParentInvisible extends TestBase {

    // initial dummy contents
    private HasComponents cc = new AbsoluteLayout();

    private Label lab;

    @Override
    protected void setup() {
        lab = new Label("Label inside the component container");
        lab.setWidth(null);

        ComboBox<Class<? extends HasComponents>> componentContainerSelect = new ComboBox<>(
                "Container");
        componentContainerSelect.setPageLength(0);
        componentContainerSelect.setId("componentContainerSelect");
        componentContainerSelect.setWidth("300px");
        // componentContainer.addContainerProperty(CAPTION, String.class, "");
        // componentContainer.addContainerProperty(CLASS, Class.class, "");

        componentContainerSelect.setItems(getComponentContainers());
        componentContainerSelect.addValueChangeListener(event -> {
            HasComponents oldCC = cc;
            cc = createComponentContainer(event.getValue());
            addToCC(lab);
            replaceComponent(oldCC, cc);
        });

        componentContainerSelect.setValue(componentContainerSelect
                .getDataProvider().fetch(new Query<>()).iterator().next());
        Button but1 = new Button("Move in and out of component container",
                event -> {
                    cc.setVisible(!cc.isVisible());
                    if (!cc.isVisible()) {
                        getLayout().addComponent(lab);
                        lab.setValue(
                                lab.getValue().replace("inside", "outside"));
                    } else {
                        addToCC(lab);
                        lab.setValue(
                                lab.getValue().replace("outside", "inside"));
                    }
                });

        addComponent(componentContainerSelect);
        addComponent(cc);
        addComponent(but1);
    }

    protected void addToCC(Label lab2) {
        if (cc instanceof ComponentContainer) {
            ((ComponentContainer) cc).addComponent(lab);
        } else if (cc instanceof SingleComponentContainer) {
            ((SingleComponentContainer) cc).setContent(lab);
        } else {
            throw new RuntimeException(
                    "Don't know how to add to " + cc.getClass().getName());
        }
    }

    private Collection<Class<? extends HasComponents>> getComponentContainers() {
        List<Class<? extends HasComponents>> list = new ArrayList<>();
        list.add(AbsoluteLayout.class);
        list.add(Accordion.class);
        list.add(CssLayout.class);
        list.add(FormLayout.class);
        list.add(GridLayout.class);
        list.add(HorizontalLayout.class);
        list.add(HorizontalSplitPanel.class);
        list.add(Panel.class);
        list.add(TabSheet.class);
        list.add(VerticalLayout.class);
        list.add(VerticalSplitPanel.class);
        return list;
    }

    protected HasComponents createComponentContainer(
            Class<? extends HasComponents> value) {
        try {
            HasComponents cc = value.newInstance();
            cc.setWidth("300px");
            cc.setHeight("300px");
            return cc;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getDescription() {
        return "Client side layouts can easily have a bug where its internal data structures gets messed up when child components from it are moved forth and back when it is invisible (registered, but renders are ignored until becomes visible again). Things are especially easy to mess up when the layout uses wrapper widget over each component (like VOrderedLayout and VGridLayout does). This tests Vertical (Ordered), Grid and CssLayout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5372;
    }

}
