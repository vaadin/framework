package com.itmill.toolkit.demo.reservation;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.AbstractComponent;

public class GoogleMap extends AbstractComponent implements Sizeable,
	Container.Viewer {
    private String TAG_MARKERS = "markers";
    private String TAG_MARKER = "marker";
    private int width = 400;
    private int height = 300;
    private int zoomLevel = 15;
    private Point2D.Double mapCenter;

    private Container dataSource;
    private Object itemMarkerHtmlPropertyId = new Object();
    private Object itemMarkerXPropertyId = new Object();
    private Object itemMarkerYPropertyId = new Object();

    public String getTag() {
	return "googlemap";
    }

    public void paintContent(PaintTarget target) throws PaintException {
	super.paintContent(target);
	if (null != mapCenter) {
	    target.addAttribute("centerX", mapCenter.getX());
	    target.addAttribute("centerY", mapCenter.getY());
	}
	target.addAttribute("zoom", zoomLevel);
	target.addAttribute("width", width);
	target.addAttribute("height", height);

	if (this.dataSource != null) {
	    target.startTag(TAG_MARKERS);
	    Collection itemIds = this.dataSource.getItemIds();
	    for (Iterator it = itemIds.iterator(); it.hasNext();) {
		Object itemId = it.next();
		Item item = this.dataSource.getItem(itemId);
		Property p = item.getItemProperty(getItemMarkerXPropertyId());
		Double x = (Double) (p != null ? p.getValue() : null);
		p = item.getItemProperty(getItemMarkerYPropertyId());
		Double y = (Double) (p != null ? p.getValue() : null);
		if (x == null || y == null) {
		    continue;
		}
		target.startTag(TAG_MARKER);
		target.addAttribute("x", x.doubleValue());
		target.addAttribute("y", y.doubleValue());
		p = item.getItemProperty(getItemMarkerHtmlPropertyId());
		String h = (String) (p != null ? p.getValue() : null);
		target.addAttribute("html", h);
		target.endTag(TAG_MARKER);
	    }
	    target.endTag(TAG_MARKERS);
	}
    }

    public void setZoomLevel(int zoomLevel) {
	this.zoomLevel = zoomLevel;
	requestRepaint();
    }

    public int getZoomLevel() {
	return this.zoomLevel;
    }

    // Sizeable methods:

    public int getHeight() {
	return this.height;
    }

    public int getHeightUnits() {
	return Sizeable.UNITS_PIXELS;
    }

    public int getWidth() {
	return this.width;
    }

    public int getWidthUnits() {
	return Sizeable.UNITS_PIXELS;
    }

    public void setHeight(int height) {
	this.height = height;
	requestRepaint();
    }

    public void setHeightUnits(int units) {
	throw new UnsupportedOperationException();
    }

    public void setWidth(int width) {
	this.width = width;
	requestRepaint();
    }

    public void setWidthUnits(int units) {
	throw new UnsupportedOperationException();
    }

    public void setMapCenter(Point2D.Double center) {
	this.mapCenter = center;
    }
    
    public Point2D.Double getMapCenter() {
	return this.mapCenter;
    }
    
    // Container.Viewer methods:

    public Container getContainerDataSource() {
	return this.dataSource;
    }

    public void setContainerDataSource(Container newDataSource) {

	this.dataSource = newDataSource;

	requestRepaint();
    }

    // Item methods

    public Object getItemMarkerHtmlPropertyId() {
	return itemMarkerHtmlPropertyId;
    }

    public void setItemMarkerHtmlPropertyId(Object itemMarkerHtmlPropertyId) {
	this.itemMarkerHtmlPropertyId = itemMarkerHtmlPropertyId;
	requestRepaint();
    }

    public Object getItemMarkerXPropertyId() {
	return itemMarkerXPropertyId;
    }

    public void setItemMarkerXPropertyId(Object itemMarkerXPropertyId) {
	this.itemMarkerXPropertyId = itemMarkerXPropertyId;
	requestRepaint();
    }

    public Object getItemMarkerYPropertyId() {
	return itemMarkerYPropertyId;
    }

    public void setItemMarkerYPropertyId(Object itemMarkerYPropertyId) {
	this.itemMarkerYPropertyId = itemMarkerYPropertyId;
	requestRepaint();
    }

    // Marker add

    public Object addMarker(String html, Point2D.Double location) {
	if (location == null) {
	    throw new IllegalArgumentException("Location must be non-null");
	}
	if (this.dataSource == null) {
	    initDataSource();
	}
	Object markerId = this.dataSource.addItem();
	if (markerId == null) {
	    return null;
	}
	Item marker = this.dataSource.getItem(markerId);
	Property p = marker.getItemProperty(getItemMarkerXPropertyId());
	p.setValue(new Double(location.x));
	p = marker.getItemProperty(getItemMarkerYPropertyId());
	p.setValue(new Double(location.y));
	p = marker.getItemProperty(getItemMarkerHtmlPropertyId());
	p.setValue(html);

	requestRepaint();

	return markerId;
    }

    public void removeMarker(Object markerId) {
	if (this.dataSource != null) {
	    this.dataSource.removeItem(markerId);
	    requestRepaint();
	}
    }

    public Item getMarkerItem(Object markerId) {
	if (this.dataSource != null) {
	    return this.dataSource.getItem(markerId);
	} else {
	    return null;
	}
    }

    // dataSource init helper:
    private void initDataSource() {
	this.dataSource = new IndexedContainer();
	this.dataSource.addContainerProperty(this.itemMarkerHtmlPropertyId,
		String.class, null);
	this.dataSource.addContainerProperty(this.itemMarkerXPropertyId,
		Double.class, new Double(0));
	this.dataSource.addContainerProperty(this.itemMarkerYPropertyId,
		Double.class, new Double(0));
    }
    
    public void clear() {
	setContainerDataSource(null);
    }
}