/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.reservation.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.Composite;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IGoogleMap extends Composite implements Paintable {

    public static final String CLASSNAME = "i-googlemap";

    private final MapWidget widget = new MapWidget();

    public IGoogleMap() {
        initWidget(widget);
        setWidth("200px");
        setHeight("200px");
        setStyleName(CLASSNAME);
        widget.addControl(new SmallMapControl());

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        widget.clearOverlays();
        LatLng pos = null;
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL u = (UIDL) it.next();
            if (u.getTag().equals("markers")) {

                for (final Iterator m = u.getChildIterator(); m.hasNext();) {
                    final UIDL umarker = (UIDL) m.next();
                    final String html = "<span>"
                            + umarker.getStringAttribute("html") + "</span>";
                    final double x = umarker.getDoubleAttribute("x");
                    final double y = umarker.getDoubleAttribute("y");
                    pos = new LatLng(x, y);
                    final Marker marker = new Marker(pos);
                    widget.addOverlay(marker);
                    if (html != null) {
                        addMarkerPopup(marker, html);
                    }
                }
            }
        }
        if (uidl.hasAttribute("width")) {
            widget.setWidth(uidl.getStringAttribute("width"));
        }
        if (uidl.hasAttribute("height")) {
            widget.setHeight(uidl.getStringAttribute("height"));
        }
        if (uidl.hasAttribute("zoom")) {
            widget.setZoomLevel(uidl.getIntAttribute("zoom"));
        }
        if (uidl.hasAttribute("centerX") && uidl.hasAttribute("centerY")) {
            final LatLng center = new LatLng(
                    uidl.getDoubleAttribute("centerX"), uidl
                            .getDoubleAttribute("centerY"));
            widget.setCenter(center);
        } else if (pos != null) {
            // use last marker position
            widget.setCenter(pos);
        }

    }

    private void addMarkerPopup(Marker marker, final String html) {
        marker.addMarkerClickHandler(new MarkerClickHandler() {

            public void onClick(MarkerClickEvent event) {
                widget.getInfoWindow().open(event.getSender().getPoint(),
                        new InfoWindowContent(html));

            }

        });

    }

}
