package com.vaadin.demo.reservation.simple;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.demo.reservation.CalendarField;
import com.vaadin.demo.reservation.ResourceNotAvailableException;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class StdView extends VerticalLayout {

    private ComboBox resources = new ComboBox("Select resource");
    private CalendarField reservations = new CalendarField();
    private Button add = new Button("Add reservation");
    private SimpleReserver application;

    private EditorWindow editor = new EditorWindow();

    StdView(SimpleReserver app) {
        setWidth("250px");
        application = app;

        resources.setImmediate(true);
        resources.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
        resources
                .setContainerDataSource(application.getDb().getResources(null));
        resources.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        resources.setItemCaptionPropertyId(SampleDB.Resource.PROPERTY_ID_NAME);
        resources.addListener(new ComboBox.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                refreshReservations();
            }
        });
        addComponent(resources);

        initCalendarFieldPropertyIds(reservations);
        Calendar c = Calendar.getInstance();
        reservations.setValue(c.getTime());
        reservations.setEnabled(false);
        addComponent(reservations);
        reservations.setImmediate(true);

        add.setEnabled(false);
        addComponent(add);

        add.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (resources.getValue() != null) {
                    Item i = resources.getItem(resources.getValue());
                    editor.newReservationFor(i);
                }
            }
        });

        add.setDescription("Add new reservation for selected resource");

    }

    private static void initCalendarFieldPropertyIds(CalendarField cal) {
        cal.setItemStyleNamePropertyId(SampleDB.Resource.PROPERTY_ID_STYLENAME);
        cal
                .setItemStartPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_FROM);
        cal.setItemEndPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_TO);
        cal
                .setItemTitlePropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_BY);
        cal
                .setItemDescriptionPropertyId(SampleDB.Reservation.PROPERTY_ID_DESCRIPTION);
    }

    private void refreshReservations() {
        if (resources.getValue() == null) {
            reservations.setContainerDataSource(null);
            add.setEnabled(false);
            reservations.setEnabled(false);
        } else {
            List<Item> resource = new LinkedList<Item>();
            resource.add(resources.getItem(resources.getValue()));
            final Container res = application.getDb().getReservations(resource);
            reservations.setContainerDataSource(res);
            add.setEnabled(true);
            reservations.setEnabled(true);
        }
    }

    public class EditorWindow extends Window {

        Label resourceName = new Label();

        DateField start = new DateField("From:");
        DateField end = new DateField("To:");
        TextField desc = new TextField("Description:");
        Button save = new Button("Save");

        private Item res;

        private Calendar cal;

        EditorWindow() {
            super("Add reservation");

            cal = Calendar.getInstance();

            addComponent(resourceName);

            start.setResolution(DateField.RESOLUTION_MIN);
            start.setImmediate(true);
            start.setValue(new Date());
            start.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Date startTime = (Date) start.getValue();
                    Date endTime = (Date) end.getValue();
                    if (endTime.before(startTime)) {
                        cal.setTime(startTime);
                        cal.add(Calendar.HOUR_OF_DAY, 1);
                        end.setValue(cal.getTime());
                    }
                }
            });
            addComponent(start);

            end.setResolution(DateField.RESOLUTION_MIN);
            end.setImmediate(true);
            end.setValue(new Date());
            end.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Date startTime = (Date) start.getValue();
                    Date endTime = (Date) end.getValue();
                    if (endTime.before(startTime)) {
                        cal.setTime(endTime);
                        cal.add(Calendar.HOUR, -1);
                        start.setValue(cal.getTime());
                    }
                }
            });
            addComponent(end);
            addComponent(desc);
            addComponent(save);
            save.addListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    try {
                        application.getDb().addReservation(res,
                                application.getUser().toString(),
                                (Date) start.getValue(), (Date) end.getValue(),
                                (String) desc.getValue());
                        EditorWindow.this.close();
                        refreshReservations();
                    } catch (ResourceNotAvailableException e) {
                        getWindow()
                                .showNotification(
                                        "Resource is not available at that time "
                                                + "or is too close to another reservation.");
                    }
                }
            });
        }

        public void newReservationFor(Item resource) {
            res = resource;
            resourceName.setValue("Resourse: "
                    + res.getItemProperty(SampleDB.Resource.PROPERTY_ID_NAME)
                            .getValue());

            cal.setTime((Date) reservations.getValue());
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            start.setValue(cal.getTime());
            cal.add(Calendar.HOUR_OF_DAY, 1);
            end.setValue(cal.getTime());
            StdView.this.getWindow().addWindow(this);
        }
    }

    public void refreshData() {
        resources
                .setContainerDataSource(application.getDb().getResources(null));
        refreshReservations();

    }
}
