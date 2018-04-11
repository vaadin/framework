package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.DetailsGenerator;
import com.vaadin.v7.ui.Grid.RowReference;
import com.vaadin.v7.ui.Grid.SelectionMode;

/**
 * This test cannot be migrated to V8 since there is no Grid::scrollTo method.
 * Will it be there in the future ? (may be this test should be just removed).
 *
 * @author Vaadin Ltd
 *
 */
public class GridDetailsLocation extends UI {

    private final DetailsGenerator detailsGenerator = new DetailsGenerator() {
        @Override
        public Component getDetails(RowReference rowReference) {
            Person person = (Person) rowReference.getItemId();
            Label label = new Label(
                    person.getFirstName() + " " + person.getLastName());
            // currently the decorator row doesn't change its height when the
            // content height is different.
            label.setHeight("30px");
            return label;
        }
    };

    private TextField numberTextField;
    private Grid grid;

    @Override
    protected void init(VaadinRequest request) {

        Layout layout = new VerticalLayout();

        grid = new Grid(PersonContainer.createWithTestData(1000));
        grid.setSelectionMode(SelectionMode.NONE);
        layout.addComponent(grid);

        final CheckBox checkbox = new CheckBox("Details generator");
        checkbox.addValueChangeListener(event -> {
            if (checkbox.getValue()) {
                grid.setDetailsGenerator(detailsGenerator);
            } else {
                grid.setDetailsGenerator(DetailsGenerator.NULL);
            }
        });
        layout.addComponent(checkbox);

        numberTextField = new TextField("Row");
        layout.addComponent(numberTextField);

        layout.addComponent(
                new Button("Toggle and scroll", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        toggle();
                        scrollTo();
                    }
                }));
        layout.addComponent(
                new Button("Scroll and toggle", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        scrollTo();
                        toggle();
                    }
                }));

        setContent(layout);
    }

    private void toggle() {
        Object itemId = getItemId();
        boolean isVisible = grid.isDetailsVisible(itemId);
        grid.setDetailsVisible(itemId, !isVisible);
    }

    private void scrollTo() {
        grid.scrollTo(getItemId());
    }

    private Object getItemId() {
        int row = Integer.parseInt(numberTextField.getValue());
        Object itemId = grid.getContainerDataSource().getIdByIndex(row);
        return itemId;
    }

}
