/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractComponent;

public class GoogleMap extends AbstractComponent implements Sizeable,
        Container.Viewer {
    private final String TAG_MARKERS = "markers";
    private final String TAG_MARKER = "marker";
    private int zoomLevel = 15;
    private Point2D.Double mapCenter;

    private Container dataSource;
    private Object itemMarkerHtmlPropertyId = new Object();
    private Object itemMarkerXPropertyId = new Object();
    private Object itemMarkerYPropertyId = new Object();

    @Override
    public String getTag() {
        return "googlemap";
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        // Add size info as variables
        if (getHeight() > -1) {
            target.addVariable(this, "height", getHeight()
                    + UNIT_SYMBOLS[getHeightUnits()]);
        }
        if (getWidth() > -1) {
            target.addVariable(this, "width", getWidth()
                    + UNIT_SYMBOLS[getWidthUnits()]);
        }
        if (null != mapCenter) {
            target.addAttribute("centerX", mapCenter.getX());
            target.addAttribute("centerY", mapCenter.getY());
        }
        target.addAttribute("zoom", zoomLevel);

        if (dataSource != null) {
            target.startTag(TAG_MARKERS);
            final Collection itemIds = dataSource.getItemIds();
            for (final Iterator it = itemIds.iterator(); it.hasNext();) {
                final Object itemId = it.next();
                final Item item = dataSource.getItem(itemId);
                Property p = item.getItemProperty(getItemMarkerXPropertyId());
                final Double x = (Double) (p != null ? p.getValue() : null);
                p = item.getItemProperty(getItemMarkerYPropertyId());
                final Double y = (Double) (p != null ? p.getValue() : null);
                if (x == null || y == null) {
                    continue;
                }
                target.startTag(TAG_MARKER);
                target.addAttribute("x", x.doubleValue());
                target.addAttribute("y", y.doubleValue());
                p = item.getItemProperty(getItemMarkerHtmlPropertyId());
                final String h = (String) (p != null ? p.getValue() : null);
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
        return zoomLevel;
    }

    public void setMapCenter(Point2D.Double center) {
        mapCenter = center;
    }

    public Point2D.Double getMapCenter() {
        return mapCenter;
    }

    // Container.Viewer methods:

    public Container getContainerDataSource() {
        return dataSource;
    }

    public void setContainerDataSource(Container newDataSource) {

        dataSource = newDataSource;

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
        if (dataSource == null) {
            initDataSource();
        }
        final Object markerId = dataSource.addItem();
        if (markerId == null) {
            return null;
        }
        final Item marker = dataSource.getItem(markerId);
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
        if (dataSource != null) {
            dataSource.removeItem(markerId);
            requestRepaint();
        }
    }

    public Item getMarkerItem(Object markerId) {
        if (dataSource != null) {
            return dataSource.getItem(markerId);
        } else {
            return null;
        }
    }

    // dataSource init helper:
    private void initDataSource() {
        dataSource = new IndexedContainer();
        dataSource.addContainerProperty(itemMarkerHtmlPropertyId, String.class,
                null);
        dataSource.addContainerProperty(itemMarkerXPropertyId, Double.class,
                new Double(0));
        dataSource.addContainerProperty(itemMarkerYPropertyId, Double.class,
                new Double(0));
    }

    public void clear() {
        setContainerDataSource(null);
    }

}