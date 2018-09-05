package com.vaadin.tests.minitutorials.v7_5;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.minitutorials.v7_4.GridExampleBean;
import com.vaadin.tests.minitutorials.v7_4.GridExampleHelper;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.DetailsGenerator;
import com.vaadin.v7.ui.Grid.RowReference;

public class ShowingExtraDataForRows extends UI {
    @Override
    protected void init(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.setContainerDataSource(GridExampleHelper.createContainer());

        grid.setDetailsGenerator(new DetailsGenerator() {
            @Override
            public Component getDetails(RowReference rowReference) {
                // Find the bean to generate details for
                final GridExampleBean bean = (GridExampleBean) rowReference
                        .getItemId();

                // A basic label with bean data
                Label label = new Label("Extra data for " + bean.getName());

                // An image with extra details about the bean
                Image image = new Image();
                image.setWidth("300px");
                image.setHeight("150px");
                image.setSource(new ExternalResource(
                        "http://dummyimage.com/300x150/000/fff&text="
                                + bean.getCount()));

                // A button just for the sake of the example
                Button button = new Button("Click me",
                        new Button.ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent event) {
                                Notification.show(
                                        "Button clicked for " + bean.getName());
                            }
                        });

                // Wrap up all the parts into a vertical layout
                VerticalLayout layout = new VerticalLayout(label, image,
                        button);
                layout.setSpacing(true);
                layout.setMargin(true);
                return layout;
            }
        });

        grid.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    Object itemId = event.getItemId();
                    grid.setDetailsVisible(itemId,
                            !grid.isDetailsVisible(itemId));
                }
            }
        });

        setContent(grid);
    }
}
