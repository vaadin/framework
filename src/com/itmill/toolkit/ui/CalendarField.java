package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

// TODO use Calendar
// TODO lazyLoading
// TODO check date limit when updating variables
// TODO Allow item selection
public class CalendarField extends DateField implements Container.Viewer {

    private Date minDate;
    private Date maxDate;

    private Container dataSource;
    private Object itemStartPropertyId;
    private Object itemEndPropertyId;
    private Object itemTitlePropertyId;
    private Object itemNotimePropertyId;

    public CalendarField() {
	super();
	init();
    }

    public CalendarField(Property dataSource) throws IllegalArgumentException {
	super(dataSource);
	init();
    }

    public CalendarField(String caption, Date value) {
	super(caption, value);
	init();
    }

    public CalendarField(String caption, Property dataSource) {
	super(caption, dataSource);
	init();
    }

    public CalendarField(String caption) {
	super(caption);
	init();
    }

    /*
     * Gets the components UIDL tag string. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    public String getTag() {
	return "calendarfield";
    }

    public void init() {
	super.setResolution(RESOLUTION_HOUR);

    }

    /**
     * Sets the resolution of the CalendarField. Only RESOLUTION_DAY and
     * RESOLUTION_HOUR are supported.
     * 
     * @param resolution
     *                the resolution to set.
     * @see com.itmill.toolkit.ui.DateField#setResolution(int)
     */
    public void setResolution(int resolution) {
	if (resolution != RESOLUTION_DAY && resolution != RESOLUTION_HOUR) {
	    throw new IllegalArgumentException();
	}
	super.setResolution(resolution);
    }

    public void setMinimumDate(Date date) {
	this.minDate = date;
	requestRepaint();
    }

    public Date getMinimumDate() {
	return minDate;
    }

    public void setMaximumDate(Date date) {
	this.maxDate = date;
	requestRepaint();
    }

    public Date getMaximumDate() {
	return maxDate;
    }

    public Container getContainerDataSource() {
	return this.dataSource;
    }

    public void setContainerDataSource(Container newDataSource) {
	if (newDataSource==null||checkDataSource(newDataSource)) {
	    this.dataSource = newDataSource;
	} else {
	    // TODO error message
	    throw new IllegalArgumentException();
	}
	requestRepaint();
    }

    private boolean checkDataSource(Container dataSource) {
	/*
	 * if (!(dataSource instanceof Container.Sortable)) { // we really want
	 * the data source to be sortable return false; }
	 */
	// Check old propertyIds
	if (this.itemEndPropertyId != null) {
	    Class c = dataSource.getType(this.itemEndPropertyId);
	    if (!Date.class.isAssignableFrom(c)) {
		this.itemEndPropertyId = null;
	    }
	}
	if (this.itemNotimePropertyId != null) {
	    Class c = dataSource.getType(this.itemNotimePropertyId);
	    if (!Boolean.class.isAssignableFrom(c)) {
		this.itemNotimePropertyId = null;
	    }
	}
	if (this.itemStartPropertyId != null) {
	    Class c = dataSource.getType(this.itemStartPropertyId);
	    if (Date.class.isAssignableFrom(c)) {
		// All we _really_ need is one date
		return true;
	    } else {
		this.itemStartPropertyId = null;
	    }
	}
	// We need at least one Date
	Collection ids = dataSource.getContainerPropertyIds();
	for (Iterator it = ids.iterator(); it.hasNext();) {
	    Object id = it.next();
	    Class c = dataSource.getType(id);
	    if (Date.class.isAssignableFrom(c)) {
		this.itemStartPropertyId = id;
		return true;
	    }
	}

	return false;
    }

    public Object getItemStartPropertyId() {
	return itemStartPropertyId;
    }

    public void setItemStartPropertyId(Object propertyId) {
	// TODO nullcheck for property id
	if (this.dataSource != null
		&& !Date.class.isAssignableFrom(dataSource.getType(propertyId))) {
	    // TODO error message
	    throw new IllegalArgumentException();
	}
	this.itemStartPropertyId = propertyId;
    }

    public Object getItemEndPropertyId() {
	return itemEndPropertyId;
    }

    public void setItemEndPropertyId(Object propertyId) {
	// TODO nullcheck for property id
	if (this.dataSource != null
		&& !Date.class.isAssignableFrom(dataSource.getType(propertyId))) {
	    // TODO error message
	    throw new IllegalArgumentException();
	}
	this.itemEndPropertyId = propertyId;
    }

    public Object getItemTitlePropertyId() {
	return itemTitlePropertyId;
    }

    public void setItemTitlePropertyId(Object propertyId) {
	this.itemTitlePropertyId = propertyId;
    }

    public Object getitemNotimePropertyId() {
	return itemNotimePropertyId;
    }

    public void setItemNotimePropertyId(Object propertyId) {
	// TODO nullcheck for property id
	if (this.dataSource != null
		&& !Boolean.class.isAssignableFrom(dataSource.getType(propertyId))) {
	    // TODO error message
	    throw new IllegalArgumentException();
	}
	this.itemNotimePropertyId = propertyId;
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *                the Paint Event.
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException {
	super.paintContent(target);

	if (this.minDate != null) {
	    target.addAttribute("min", String.valueOf(this.minDate.getTime()));
	}
	if (this.maxDate != null) {
	    target.addAttribute("max", String.valueOf(this.maxDate.getTime()));
	}

	if (this.dataSource != null) {
	    target.startTag("items");

	    // send one month now, the rest via lazyloading
	    int month = new Date().getMonth();
	    Object value = getValue();
	    if (value != null && value instanceof Date) {
		month = ((Date) value).getMonth();
	    }

	    for (Iterator it = this.dataSource.getItemIds().iterator(); it
		    .hasNext();) {
		Object itemId = it.next();
		Item item = (Item) this.dataSource.getItem(itemId);
		Property p = item.getItemProperty(this.itemStartPropertyId);
		Date start = (Date) p.getValue();
		Date end = start; // assume same day
		if (this.itemEndPropertyId != null) {
		    p = item.getItemProperty(this.itemEndPropertyId);
		    end = (Date) p.getValue();
		    if (end == null) {
			end = start;
		    } else if (end.before(start)) {
			Date tmp = start;
			start = end;
			end = tmp;
		    }
		}

		if (start != null) {
		    if ((start.getMonth() <= month || end.getMonth() >= month)) {
			target.startTag("item");
			// TODO different id!
			target.addAttribute("id", itemId.hashCode());
			target.addAttribute("start", ""+start.getTime());
			if (end != start) {
			    target.addAttribute("end", ""+end.getTime());
			}
			if (this.itemTitlePropertyId != null) {
			    p = item.getItemProperty(this.itemTitlePropertyId);
			    Object val = p.getValue();
			    if (val != null) {
				target.addAttribute("title", val.toString());
			    }
			}
			if (this.itemNotimePropertyId != null) {
			    p = item
				    .getItemProperty(this.itemNotimePropertyId);
			    Object val = p.getValue();
			    if (val != null) {
				target.addAttribute("notime", ((Boolean) val)
					.booleanValue());
			    }
			}

			target.endTag("item");
		    }
		}
	    }

	    target.endTag("items");
	}
    }
}
