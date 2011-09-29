package com.vaadin.tests.layouts;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Window;

public class MovingComponentsWhileOldParentInvisible extends TestBase {

    private ComponentContainer cc = new AbsoluteLayout(); // initial dummy
                                                          // contents
    private Label lab;

    @Override
    protected void setup() {
        lab = new Label("Label inside the component container");
        lab.setWidth(null);

        ComboBox componentContainerSelect = new ComboBox("Container") {
            {
                pageLength = 0;
            }
        };
        componentContainerSelect.setDebugId("componentContainerSelect");
        componentContainerSelect.setWidth("300px");
        componentContainerSelect.setImmediate(true);
        componentContainerSelect.setNullSelectionAllowed(false);
        // componentContainer.addContainerProperty(CAPTION, String.class, "");
        // componentContainer.addContainerProperty(CLASS, Class.class, "");

        for (Class<? extends ComponentContainer> cls : VaadinClasses
                .getComponentContainers()) {
            if (cls == LoginForm.class || cls == CustomLayout.class
                    || CustomComponent.class.isAssignableFrom(cls)
                    || cls == PopupView.class || cls == Window.class) {
                // Does not support addComponent
                continue;
            }
            componentContainerSelect.addItem(cls);
        }
        componentContainerSelect.addListener(new ValueChangeListener() {

            @SuppressWarnings("unchecked")
            public void valueChange(ValueChangeEvent event) {
                ComponentContainer oldCC = cc;
                cc = createComponentContainer((Class<? extends ComponentContainer>) event
                        .getProperty().getValue());
                cc.addComponent(lab);

                replaceComponent(oldCC, cc);
            }
        });

        componentContainerSelect.setValue(componentContainerSelect.getItemIds()
                .iterator().next());
        Button but1 = new Button("Move in and out of component container",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        cc.setVisible(!cc.isVisible());
                        if (!cc.isVisible()) {
                            getLayout().addComponent(lab);
                            lab.setValue(((String) lab.getValue()).replace(
                                    "inside", "outside"));
                        } else {
                            cc.addComponent(lab);
                            lab.setValue(((String) lab.getValue()).replace(
                                    "outside", "inside"));
                        }
                    }
                });

        addComponent(componentContainerSelect);
        addComponent(cc);
        addComponent(but1);
    }

    protected ComponentContainer createComponentContainer(
            Class<? extends ComponentContainer> value) {
        try {
            ComponentContainer cc = value.newInstance();
            cc.setWidth("300px");
            cc.setHeight("300px");
            return cc;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
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
