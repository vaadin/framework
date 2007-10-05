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
import com.itmill.toolkit.ui.CalendarField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;

public class ReservationApplication extends Application {

    private SampleDB db;

    ResourceSelectorPanel resourcePanel;

    private CalendarField reservedFrom;
    private static final long DEFAULT_GAP_MILLIS = 3600000; // one hour
    private long currentGapMillis = DEFAULT_GAP_MILLIS;
    private CalendarField reservedTo;

    private Label resourceName;
    private Label statusLabel;
    private TextField description;
    private Button reservationButton;

    private Table allTable;
    private CalendarField allCalendar;

    private GoogleMap map;

    public void init() {
	db = new SampleDB(true);
	db.generateResources();
	db.generateDemoUser();

	Window mainWindow = new Window("Reservr");
	setMainWindow(mainWindow);
	setTheme("reservr");

	TabSheet mainTabs = new TabSheet();
	mainWindow.addComponent(mainTabs);

	mainWindow.addComponent(new Button("close", this, "close"));

	OrderedLayout reservationTab = new OrderedLayout();
	mainTabs.addTab(reservationTab, "Make reservation", null);

	resourcePanel = new ResourceSelectorPanel("Resources");
	resourcePanel.setResourceContainer(db.getResources(null));
	resourcePanel.addListener(
		ResourceSelectorPanel.SelectedResourcesChangedEvent.class,
		this, "selectedResourcesChanged");
	reservationTab.addComponent(resourcePanel);

	Panel reservationPanel = new Panel("Reservation", new OrderedLayout(
		OrderedLayout.ORIENTATION_HORIZONTAL));
	reservationTab.addComponent(reservationPanel);

	OrderedLayout infoLayout = new OrderedLayout();
	reservationPanel.addComponent(infoLayout);
	resourceName = new Label("From the list above");
	resourceName.setCaption("Choose resource");
	infoLayout.addComponent(resourceName);
	description = new TextField();
	description.setColumns(55);
	description.setRows(5);
	infoLayout.addComponent(description);
	reservationButton = new Button("Make reservation", this,
		"makeReservation");
	infoLayout.addComponent(reservationButton);
	statusLabel = new Label();
	infoLayout.addComponent(statusLabel);

	map = new GoogleMap();
	map.setWidth(360);
	map.setHeight(270);
	map.setItemMarkerHtmlPropertyId(SampleDB.Resource.PROPERTY_ID_NAME);
	map.setItemMarkerXPropertyId(SampleDB.Resource.PROPERTY_ID_LOCATIONX);
	map.setItemMarkerYPropertyId(SampleDB.Resource.PROPERTY_ID_LOCATIONY);
	map.setContainerDataSource(db.getResources(null));
	infoLayout.addComponent(map);

	Calendar from = Calendar.getInstance();
	reservedFrom = new CalendarField();
	reservedFrom.setMinimumDate(from.getTime());
	reservedFrom.setValue(from.getTime());
	reservedFrom.setImmediate(true);
	initCalendarFieldPropertyIds(reservedFrom);
	reservationPanel.addComponent(reservedFrom);	
	
	Calendar to = Calendar.getInstance();
	to.add(Calendar.MILLISECOND, (int)currentGapMillis);
	reservedTo = new CalendarField();
	reservedTo.setMinimumDate(from.getTime());
	reservedTo.setValue(to.getTime());
	reservedTo.setImmediate(true);
	initCalendarFieldPropertyIds(reservedTo);
	reservationPanel.addComponent(reservedTo);
	
	reservedFrom.addListener(new ValueChangeListener() {
	    public void valueChange(ValueChangeEvent event) {
		Date fd = (Date) reservedFrom.getValue();
		if (fd == null) {
		    reservedTo.setValue(null);
		    reservedTo.setEnabled(false);
		    refreshSelectedResources();
		    return;
		} else {
		    reservedTo.setEnabled(true);
		}
		reservedTo.setMinimumDate(fd);
		Calendar to = Calendar.getInstance();
		to.setTime(fd);
		to.add(Calendar.MILLISECOND, (int)currentGapMillis);
		reservedTo.setValue(to.getTime());
		refreshSelectedResources();
		resetStatus();
	    }
	});
	reservedTo.addListener(new ValueChangeListener() {
	    public void valueChange(ValueChangeEvent event) {
		Date from = (Date) reservedFrom.getValue();
		Date to = (Date) reservedTo.getValue();
		currentGapMillis = to.getTime() - from.getTime();
		if (currentGapMillis <= 0 ) {
		    Calendar t = Calendar.getInstance();
		    t.setTime(from);
		    t.add(Calendar.MILLISECOND, (int)DEFAULT_GAP_MILLIS);
		    reservedTo.setValue(t.getTime());
		}
		refreshSelectedResources();
		resetStatus();
	    }
	});

	OrderedLayout allLayout = new OrderedLayout(
		OrderedLayout.ORIENTATION_HORIZONTAL);
	allCalendar = new CalendarField();
	initCalendarFieldPropertyIds(allCalendar);
	allLayout.addComponent(allCalendar);
	allTable = new Table();
	allLayout.addComponent(allTable);
	mainTabs.addTab(allLayout, "All reservations", null);
	mainTabs.addListener(new TabSheet.SelectedTabChangeListener() {
	    public void selectedTabChange(SelectedTabChangeEvent event) {
		refreshReservations();
	    }
	});

	refreshReservations();
    }

    public void makeReservation() {
	try {
	    Item resource = getActiveResource();
	    if (resource != null) {
		db.addReservation(resource, 0, (Date) reservedFrom.getValue(),
			(Date) reservedTo.getValue(), (String) description
				.getValue());
		statusLabel.setCaption("Success!");
		statusLabel
			.setValue("You have reserved the resource for the selected period.");
	    }
	} catch (ResourceNotAvailableException e) {
	    statusLabel.setCaption("Reservation failed");
	    statusLabel
		    .setValue("The selected resource was not available for the selected period.");
	}
	refreshReservations();
    }

    private Item getActiveResource() throws ResourceNotAvailableException {
	List rids = resourcePanel.getSelectedResources();
	if (rids != null && rids.size() > 0) {
	    for (Iterator it = rids.iterator(); it.hasNext();) {
		Item resource = (Item) it.next();
		int id = ((Integer) resource.getItemProperty(
			SampleDB.Resource.PROPERTY_ID_ID).getValue())
			.intValue();
		if (db.isAvailableResource(id, (Date) reservedFrom.getValue(),
			(Date) reservedTo.getValue()))
		    return resource;
	    }
	    throw new ResourceNotAvailableException("No available resource");
	} else {
	    return null;
	}
    }

    private void refreshReservations() {
	Container reservations = db.getReservations(resourcePanel
		.getSelectedResources());
	reservedFrom.setContainerDataSource(reservations);
	reservedTo.setContainerDataSource(reservations);
	refreshSelectedResources();
	Container allReservations = db.getReservations(null);
	allTable.setContainerDataSource(allReservations);
	allCalendar.setContainerDataSource(allReservations);

    }

    private void refreshSelectedResources() {
	Item resource = null;
	try {
	    resource = getActiveResource();
	} catch (ResourceNotAvailableException e) {
	    resourceName.setCaption("Not available");
	    resourceName.setValue("Please choose another time period or resource");
	    reservationButton.setEnabled(false);
	    return;
	}
	map.clear();
	if (resource == null) {
	    resourceName.setCaption("Choose resource");
	    resourceName.setValue("from the list above");
	    reservationButton.setEnabled(false);
	    map.setContainerDataSource(db.getResources(null));
	    map.setZoomLevel(1);

	} else {
	    LinkedList srs = resourcePanel.getSelectedResources();
	    for (Iterator it = srs.iterator(); it.hasNext();) {
		resource = (Item)it.next();
		String name = (String) resource.getItemProperty(
			SampleDB.Resource.PROPERTY_ID_NAME).getValue();
		String desc = (String) resource.getItemProperty(
			SampleDB.Resource.PROPERTY_ID_DESCRIPTION).getValue();
		resourceName.setCaption(name);
		resourceName.setValue(desc);
		Double x = (Double) resource.getItemProperty(
			SampleDB.Resource.PROPERTY_ID_LOCATIONX).getValue();
		Double y = (Double) resource.getItemProperty(
			SampleDB.Resource.PROPERTY_ID_LOCATIONY).getValue();
		if (x != null && y != null) {
		    map.addMarker(name + "<br/>" + desc, new Point2D.Double(x
			    .doubleValue(), y.doubleValue()));
		    
		}
		
	    }
	    map.setZoomLevel((srs.size()==1?16:9));
	    reservationButton.setEnabled(true);
	}

    }

    private void initCalendarFieldPropertyIds(CalendarField cal) {
	cal
		.setItemStartPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_FROM);
	cal.setItemEndPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_TO);
	cal.setItemTitlePropertyId(SampleDB.Resource.PROPERTY_ID_NAME);
	cal
		.setItemDescriptionPropertyId(SampleDB.Reservation.PROPERTY_ID_DESCRIPTION);
    }

    private void resetStatus() {
	statusLabel.setCaption(null);
	statusLabel.setValue(null);
    }

    public void selectedResourcesChanged(
	    ResourceSelectorPanel.SelectedResourcesChangedEvent event) {
	refreshReservations();
	resetStatus();
    }

}
