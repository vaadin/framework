package com.itmill.toolkit.demo.reservation;

import java.util.Date;
import java.util.Iterator;
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
    private CalendarField reservedTo;

    private Label resourceName;
    private Label statusLabel;
    private TextField description;
    private Button reservationButton;

    Table allReservations;

    public void init() {
	db = new SampleDB(true);
	db.generateResources();
	db.generateDemoUser();

	Window mainWindow = new Window("Reservr");
	setMainWindow(mainWindow);
	setTheme("example");

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

	Panel reservationPanel = new Panel(new OrderedLayout(
		OrderedLayout.ORIENTATION_HORIZONTAL));
	reservationTab.addComponent(reservationPanel);

	OrderedLayout infoLayout = new OrderedLayout();
	reservationPanel.addComponent(infoLayout);
	resourceName = new Label("Choose resource");
	resourceName.setCaption("Selected resource");
	infoLayout.addComponent(resourceName);
	description = new TextField();
	description.setColumns(30);
	description.setRows(5);
	infoLayout.addComponent(description);
	reservationButton = new Button("Make reservation", this,
		"makeReservation");
	infoLayout.addComponent(reservationButton);
	statusLabel = new Label();
	infoLayout.addComponent(statusLabel);

	// TODO Use calendar, set following hour
	Date now = new Date();
	reservedFrom = new CalendarField();
	reservedFrom.setMinimumDate(now);
	initCalendarFieldPropertyIds(reservedFrom);
	reservationPanel.addComponent(reservedFrom);
	reservedTo = new CalendarField();
	reservedTo.setMinimumDate(now);
	initCalendarFieldPropertyIds(reservedTo);
	reservationPanel.addComponent(reservedTo);
	reservedFrom.addListener(new ValueChangeListener() {
	    public void valueChange(ValueChangeEvent event) {
		Date fd = (Date) reservedFrom.getValue();
		Date td = (Date) reservedTo.getValue();
		if (fd == null) {
		    reservedTo.setValue(null);
		    reservedTo.setEnabled(false);
		    refreshSelectedResources();
		    return;
		} else {
		    reservedTo.setEnabled(true);
		}
		reservedTo.setMinimumDate(fd);
		if (td == null || td.before(fd)) {
		    reservedTo.setValue(fd);
		}
		refreshSelectedResources();
		resetStatus();
	    }
	});
	reservedFrom.setImmediate(true);
	reservedFrom.setValue(now);

	allReservations = new Table();
	mainTabs.addTab(allReservations, "All reservations", null);
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
	allReservations.setContainerDataSource(db.getReservations(null));
    }

    private void refreshSelectedResources() {
	Item resource = null;
	try {
	    resource = getActiveResource();
	} catch (ResourceNotAvailableException e) {
	    resourceName.setValue("Not available");
	    reservationButton.setEnabled(false);
	    return;
	}
	if (resource == null) {
	    resourceName.setValue("Choose resource");
	    reservationButton.setEnabled(false);
	} else {
	    resourceName.setValue((String) resource.getItemProperty(
		    SampleDB.Resource.PROPERTY_ID_NAME).getValue());
	    reservationButton.setEnabled(true);
	}
	
    }

    private void initCalendarFieldPropertyIds(CalendarField cal) {
	cal
		.setItemStartPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_FROM);
	cal.setItemEndPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_TO);
	cal
		.setItemTitlePropertyId(SampleDB.Reservation.PROPERTY_ID_DESCRIPTION);
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
