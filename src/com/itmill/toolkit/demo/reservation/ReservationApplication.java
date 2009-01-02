/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.reservation;

import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;
import com.itmill.toolkit.ui.Window.Notification;

public class ReservationApplication extends Application {

    private SampleDB db;

    ResourceSelectorPanel resourcePanel;

    private CalendarField reservedFrom;

    private static final long DEFAULT_GAP_MILLIS = 3600000; // (almost) one

    // hour
    private long currentGapMillis = DEFAULT_GAP_MILLIS; // current length of

    // reservation
    private CalendarField reservedTo;

    private Label resourceName;

    private TextField description;

    private Button reservationButton;

    private Table allTable;

    private GoogleMap map;

    @Override
    public void init() {
        //
        db = new SampleDB();

        final Window mainWindow = new Window("Reservr ");
        setMainWindow(mainWindow);
        setTheme("reservr");
        mainWindow.getLayout().setWidth("100%");

        Label logo = new Label("Reservr");
        logo.setStyleName("logo");
        mainWindow.addComponent(logo);

        Label slogan = new Label("Representational vehicles on-the-road");
        slogan.setStyleName("slogan");
        mainWindow.addComponent(slogan);

        final TabSheet mainTabs = new TabSheet();
        mainWindow.addComponent(mainTabs);

        final OrderedLayout reservationTab = new OrderedLayout();
        reservationTab.setWidth("100%");
        mainTabs.addTab(reservationTab, "Make reservation", null);

        resourcePanel = new ResourceSelectorPanel("Resources");
        resourcePanel.setResourceContainer(db.getResources(null));
        resourcePanel.addListener(
                ResourceSelectorPanel.SelectedResourcesChangedEvent.class,
                this, "selectedResourcesChanged");
        reservationTab.addComponent(resourcePanel);

        final Panel reservationPanel = new Panel("Reservation",
                new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));
        reservationPanel.addStyleName(Panel.STYLE_LIGHT);
        reservationPanel.getLayout().setMargin(true);
        reservationTab.addComponent(reservationPanel);

        final OrderedLayout infoLayout = new OrderedLayout();
        infoLayout.setSpacing(true);
        infoLayout.setSizeUndefined();
        infoLayout.setMargin(false, true, false, false);
        reservationPanel.addComponent(infoLayout);
        resourceName = new Label("From the list above");
        resourceName.setCaption("Choose resource");
        infoLayout.addComponent(resourceName);
        description = new TextField();
        description.setWidth("250px");
        description.setRows(5);
        infoLayout.addComponent(description);
        reservationButton = new Button("Make reservation", this,
                "makeReservation");
        infoLayout.addComponent(reservationButton);
        infoLayout.setComponentAlignment(reservationButton,
                OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);

        map = new GoogleMap();
        map.setWidth("250px");
        map.setHeight("250px");
        map.setItemMarkerHtmlPropertyId(SampleDB.Resource.PROPERTY_ID_NAME);
        map.setItemMarkerXPropertyId(SampleDB.Resource.PROPERTY_ID_LOCATIONX);
        map.setItemMarkerYPropertyId(SampleDB.Resource.PROPERTY_ID_LOCATIONY);
        map.setContainerDataSource(db.getResources(null));
        infoLayout.addComponent(map);

        final Calendar from = Calendar.getInstance();
        from.add(Calendar.HOUR, 1);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        from.set(Calendar.MILLISECOND, 0);
        reservedFrom = new CalendarField("From");
        reservedFrom.setMinimumDate(from.getTime());
        reservedFrom.setValue(from.getTime());
        reservedFrom.setImmediate(true);
        initCalendarFieldPropertyIds(reservedFrom);
        reservationPanel.addComponent(reservedFrom);

        final Label arrowLabel = new Label("&raquo;");
        arrowLabel.setContentMode(Label.CONTENT_XHTML);
        arrowLabel.setStyleName("arrow");
        reservationPanel.addComponent(arrowLabel);

        final Calendar to = Calendar.getInstance();
        to.setTime(from.getTime());
        to.add(Calendar.MILLISECOND, (int) DEFAULT_GAP_MILLIS);
        reservedTo = new CalendarField("To");
        reservedTo.setMinimumDate(to.getTime());
        reservedTo.setValue(to.getTime());
        reservedTo.setImmediate(true);
        initCalendarFieldPropertyIds(reservedTo);
        reservationPanel.addComponent(reservedTo);

        reservedFrom.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                final Date fd = (Date) reservedFrom.getValue();
                if (fd == null) {
                    reservedTo.setValue(null);
                    reservedTo.setEnabled(false);
                    refreshSelectedResources(true);
                    return;
                } else {
                    reservedTo.setEnabled(true);
                }
                reservedTo.setMinimumDate(new Date(fd.getTime()
                        + DEFAULT_GAP_MILLIS));
                final Calendar to = Calendar.getInstance();
                to.setTime(fd);
                to.add(Calendar.MILLISECOND, (int) currentGapMillis);
                reservedTo.setValue(to.getTime());
                refreshSelectedResources(true);
            }
        });
        reservedTo.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                final Date from = (Date) reservedFrom.getValue();
                final Date to = (Date) reservedTo.getValue();
                currentGapMillis = to.getTime() - from.getTime();
                if (currentGapMillis <= 0) {
                    final Calendar t = Calendar.getInstance();
                    t.setTime(from);
                    t.add(Calendar.MILLISECOND, (int) DEFAULT_GAP_MILLIS);
                    reservedTo.setValue(t.getTime());
                }
                refreshSelectedResources(true);
            }
        });

        allTable = new Table();
        allTable.setWidth("100%");
        allTable.setHeight("450px");
        allTable.setColumnCollapsingAllowed(true);
        allTable.setColumnReorderingAllowed(true);
        mainTabs.addTab(allTable, "All reservations", null);
        mainTabs.addListener(new TabSheet.SelectedTabChangeListener() {
            public void selectedTabChange(SelectedTabChangeEvent event) {
                refreshReservations();
            }
        });

        resourcePanel.selectFirstCategory();
        refreshReservations();
        refreshSelectedResources(true);
    }

    public void makeReservation() {
        try {
            final Item resource = getActiveResource();
            if (resource != null) {
                db.addReservation(resource, 0, (Date) reservedFrom.getValue(),
                        (Date) reservedTo.getValue(), (String) description
                                .getValue());
                getMainWindow()
                        .showNotification(
                                "Success!",
                                "You have reserved the resource for the selected period.",
                                Notification.TYPE_WARNING_MESSAGE);
                refreshReservations();
                refreshSelectedResources(false);
            } else {
                getMainWindow().showNotification("Oops!",
                        "Please select a resource (or category) to reserve.",
                        Notification.TYPE_WARNING_MESSAGE);
            }
        } catch (final ResourceNotAvailableException e) {
            getMainWindow()
                    .showNotification(
                            "Not available!",
                            "The selected resource is already reserved for the selected period.",
                            Notification.TYPE_ERROR_MESSAGE);
            refreshReservations();
        }
    }

    private Item getActiveResource() throws ResourceNotAvailableException {
        final List rids = resourcePanel.getSelectedResources();
        if (rids != null && rids.size() > 0) {
            for (final Iterator it = rids.iterator(); it.hasNext();) {
                final Item resource = (Item) it.next();
                final int id = ((Integer) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_ID).getValue())
                        .intValue();
                if (db.isAvailableResource(id, (Date) reservedFrom.getValue(),
                        (Date) reservedTo.getValue())) {
                    return resource;
                }
            }
            throw new ResourceNotAvailableException("No available resources");
        } else {
            return null;
        }
    }

    private void refreshReservations() {
        final Container reservations = db.getReservations(resourcePanel
                .getSelectedResources());
        reservedFrom.setContainerDataSource(reservations);
        reservedTo.setContainerDataSource(reservations);
        final Container allReservations = db.getReservations(null);
        allTable.setContainerDataSource(allReservations);
        if (allReservations != null && allReservations.size() > 0) {
            allTable.setVisibleColumns(new Object[] {
                    SampleDB.Reservation.PROPERTY_ID_RESERVED_FROM,
                    SampleDB.Reservation.PROPERTY_ID_RESERVED_TO,
                    SampleDB.Resource.PROPERTY_ID_NAME,
                    SampleDB.Resource.PROPERTY_ID_DESCRIPTION,
                    SampleDB.Reservation.PROPERTY_ID_DESCRIPTION });
            allTable.setColumnHeaders(new String[] { "From", "To", "Resource",
                    "Description", "Message" });
        }
    }

    private void refreshSelectedResources(boolean alertIfNotAvailable) {
        Item resource = null;
        try {
            resource = getActiveResource();
        } catch (final ResourceNotAvailableException e) {
            if (alertIfNotAvailable) {
                getMainWindow().showNotification("Not available",
                        "Please choose another resource or time period.",
                        Notification.TYPE_HUMANIZED_MESSAGE);
            }
            refreshReservations();
            return;
        }
        map.clear();
        if (resource == null) {
            resourceName.setCaption("Choose resource above");
            resourceName.setValue("");
            map.setContainerDataSource(db.getResources(null));
            map.setZoomLevel(1);

        } else {
            // Display active resource name + desc
            String name = (String) resource.getItemProperty(
                    SampleDB.Resource.PROPERTY_ID_NAME).getValue();
            String desc = (String) resource.getItemProperty(
                    SampleDB.Resource.PROPERTY_ID_DESCRIPTION).getValue();
            resourceName.setCaption(name);
            resourceName.setValue(desc);
            // Put all resources on map (may be many if category was selected)
            final LinkedList srs = resourcePanel.getSelectedResources();
            for (final Iterator it = srs.iterator(); it.hasNext();) {
                resource = (Item) it.next();
                name = (String) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_NAME).getValue();
                desc = (String) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_DESCRIPTION).getValue();
                final Double x = (Double) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_LOCATIONX).getValue();
                final Double y = (Double) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_LOCATIONY).getValue();
                if (x != null && y != null) {
                    map.addMarker(name + "<br/>" + desc, new Point2D.Double(x
                            .doubleValue(), y.doubleValue()));

                }

            }
            map.setZoomLevel((srs.size() == 1 ? 14 : 9));
        }

    }

    private void initCalendarFieldPropertyIds(CalendarField cal) {
        cal.setItemStyleNamePropertyId(SampleDB.Resource.PROPERTY_ID_STYLENAME);
        cal
                .setItemStartPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_FROM);
        cal.setItemEndPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_TO);
        cal.setItemTitlePropertyId(SampleDB.Resource.PROPERTY_ID_NAME);
        cal
                .setItemDescriptionPropertyId(SampleDB.Reservation.PROPERTY_ID_DESCRIPTION);
    }

    public void selectedResourcesChanged(
            ResourceSelectorPanel.SelectedResourcesChangedEvent event) {
        refreshReservations();
        refreshSelectedResources(true);
    }

}
