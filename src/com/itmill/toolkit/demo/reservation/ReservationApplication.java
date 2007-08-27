package com.itmill.toolkit.demo.reservation;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CalendarField;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ReservationApplication extends Application {

    private SampleDB db;
    
    private Window mainWindow;

    private CalendarField reservedFrom;
    private CalendarField reservedTo;
    private TextField description;
    private Button reservationButton;
    
    private Integer selectedResource = null;
    
    public void init() {
	mainWindow = new Window("Reservr");
	setMainWindow(mainWindow);
	setTheme("example");

	db = new SampleDB(true);
	db.generateResources();
	db.generateDemoUser();
		
	ResourcePanel resourcePanel = new ResourcePanel("Resources");
	resourcePanel.setResourceContainer(db.getResources(null));
	mainWindow.addComponent(resourcePanel);
		
	Panel reservationPanel = new Panel(new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));
	mainWindow.addComponent(reservationPanel);
	
	OrderedLayout infoLayout = new OrderedLayout();
	reservationPanel.addComponent(infoLayout);
	description = new TextField();
	description.setColumns(30);
	description.setRows(5);
	infoLayout.addComponent(description);
	reservationButton = new Button("Make reservation",this,"makeReservation");
	infoLayout.addComponent(reservationButton);

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
	refreshReservations(null);
	reservedFrom.addListener(new ValueChangeListener() {
	    public void valueChange(ValueChangeEvent event) {
		Date fd = (Date)reservedFrom.getValue();
		Date td = (Date)reservedTo.getValue();
		if (fd == null) {
		    reservedTo.setValue(null);
		    reservedTo.setEnabled(false);
		    return;
		} else {
		    reservedTo.setEnabled(true);
		}
		reservedTo.setMinimumDate(fd);
		if (td == null||td.before(fd)) {
		    reservedTo.setValue(fd);
		}
	    }	    
	});
	reservedFrom.setImmediate(true);
	reservedFrom.setValue(now);

	

	mainWindow.addComponent(new Button("close",this,"close"));
	Table tbl = new Table();
	tbl.setContainerDataSource(db.getUsers());
	mainWindow.addComponent(tbl);

    }


    public void makeReservation() {
	Integer rid = getSelectedResourceId();
	if (rid!=null) {
	    db.addReservation(rid.intValue(), 0, (Date)reservedFrom.getValue(), (Date)reservedTo.getValue(), (String)description.getValue());
	    refreshReservations(new int[] {rid.intValue()});
	}
    }
    
    private void refreshReservations(int[] resourceIds) {
	Container reservations = db.getReservations(resourceIds);;
	System.err.println("Got " + (reservations!=null?reservations.size():0) + " reservations");
	reservedFrom.setContainerDataSource(reservations);
	reservedTo.setContainerDataSource(reservations);
    }
    private void initCalendarFieldPropertyIds(CalendarField cal) {
	cal.setItemStartPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_FROM);
	cal.setItemEndPropertyId(SampleDB.Reservation.PROPERTY_ID_RESERVED_TO);
	cal.setItemTitlePropertyId(SampleDB.Reservation.PROPERTY_ID_DESCRIPTION);
    }
    
    private void setSelectedResourceId(Integer id) {
	selectedResource = id;
	if (id == null) {
	    reservationButton.setEnabled(false);
	} else {
	    reservationButton.setEnabled(true);
	}
    }
    private Integer getSelectedResourceId() {
	return selectedResource;
    }
    
    private class ResourcePanel extends Panel implements Button.ClickListener {
	
	private HashMap categoryLayouts = new HashMap();
	private HashMap categoryResources = new HashMap();
	
	public ResourcePanel(String caption) {
	    super(caption,new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));
	}
	
	public void setResourceContainer(Container resources) {
	    this.removeAllComponents();
	    categoryLayouts.clear();
	    categoryResources.clear();
	    if (resources!=null&&resources.size()>0) {
		for (Iterator it = resources.getItemIds().iterator();it.hasNext();) {
		    Item resource = (Item)resources.getItem(it.next());
		    Integer id = (Integer)resource.getItemProperty(SampleDB.Resource.PROPERTY_ID_ID).getValue();
		    String category = (String)resource.getItemProperty(SampleDB.Resource.PROPERTY_ID_CATEGORY).getValue();
		    String name = (String)resource.getItemProperty(SampleDB.Resource.PROPERTY_ID_NAME).getValue();
		    String description = (String)resource.getItemProperty(SampleDB.Resource.PROPERTY_ID_DESCRIPTION).getValue();
		    Button rButton = new Button(name,this);
		    rButton.setStyle("link");
		    rButton.setDescription(description);
		    rButton.setData(id);
		    Layout resourceLayout = (Layout)categoryLayouts.get(category);
		    LinkedList resourceList = (LinkedList)categoryResources.get(category);
		    if (resourceLayout==null) {
			resourceLayout = new OrderedLayout();
			this.addComponent(resourceLayout);
			categoryLayouts.put(category, resourceLayout);
			resourceList = new LinkedList();
			categoryResources.put(category, resourceList);
			Button cButton = new Button(category + " (any)",this);
			cButton.setStyle("link");
			cButton.setData(category);
			resourceLayout.addComponent(cButton);
		    } 
		    resourceLayout.addComponent(rButton);
		    resourceList.add(id);
		}
	    }
	}

	public void buttonClick(ClickEvent event) {
	    Object source = event.getSource();
	    if (source instanceof Button) {
		Object data = ((Button)source).getData();
		if (data instanceof Integer) {
		    Integer resourceId = (Integer)data;
		    setSelectedResourceId(resourceId);
		    refreshReservations(new int[] {resourceId.intValue()});
		} else {
		    setSelectedResourceId(null);
		    String category = (String)data;
		    LinkedList resources = (LinkedList)categoryResources.get(category);
		    int[] rids = new int[resources.size()];
		    for (int i = 0; i< rids.length;i++) {
			rids[i] = ((Integer)resources.get(i)).intValue();
		    }
		    refreshReservations(rids);
		}
	    }
	    
	}
	
    }
}
