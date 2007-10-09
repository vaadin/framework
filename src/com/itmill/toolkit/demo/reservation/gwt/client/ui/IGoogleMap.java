package com.itmill.toolkit.demo.reservation.gwt.client.ui;

import java.util.Iterator;

import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.mapitz.gwt.googleMaps.client.GLatLng;
import com.mapitz.gwt.googleMaps.client.GMap2;
import com.mapitz.gwt.googleMaps.client.GMap2EventManager;
import com.mapitz.gwt.googleMaps.client.GMap2Widget;
import com.mapitz.gwt.googleMaps.client.GMarker;
import com.mapitz.gwt.googleMaps.client.GMarkerEventClickListener;
import com.mapitz.gwt.googleMaps.client.GMarkerEventManager;

public class IGoogleMap extends GMap2Widget implements Paintable {

	public static final String CLASSNAME = "i-googlemap";

	GMap2EventManager mapEventManager;
	GMarkerEventManager markerEventManager;
	GMap2 map;

	public IGoogleMap() {
		setStyleName(CLASSNAME);
		mapEventManager = GMap2EventManager.getInstance();
		map = this.getGmap();
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		map.clearOverlays();
		GLatLng pos = null;
		for (Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL u = (UIDL) it.next();
			if (u.getTag().equals("markers")) {

				for (Iterator m = u.getChildIterator(); m.hasNext();) {
					UIDL umarker = (UIDL) m.next();
					String html = "<span>" + umarker.getStringAttribute("html")
							+ "</span>";
					double x = umarker.getDoubleAttribute("x");
					double y = umarker.getDoubleAttribute("y");
					pos = new GLatLng(x, y);
					GMarker marker = new GMarker(pos);
					map.addOverlay(marker);
					if (html != null) {
						addMarkerPopup(marker, html);
					}
				}
			}
		}
		if (uidl.hasAttribute("width")) {
			setWidth("" + uidl.getIntAttribute("width"));
		}
		if (uidl.hasAttribute("height")) {
			setHeight("" + uidl.getIntAttribute("height"));
		}
		if (uidl.hasAttribute("zoom")) {
			map.setZoom(uidl.getIntAttribute("zoom"));
		}
		if (uidl.hasAttribute("centerX") && uidl.hasAttribute("centerY")) {
			GLatLng center = new GLatLng(uidl.getDoubleAttribute("centerX"),
					uidl.getDoubleAttribute("centerY"));
			map.setCenter(center);
		} else if (pos != null) {
			// use last marker position
			map.setCenter(pos);
		}

	}

	private void addMarkerPopup(GMarker marker, String html) {
		if (markerEventManager == null) {
			markerEventManager = GMarkerEventManager.getInstance();
		}

		markerEventManager.addOnClickListener(marker, new MarkerEventListener(
				html));

	}

	private class MarkerEventListener implements GMarkerEventClickListener {
		String html;

		public MarkerEventListener(String html) {
			this.html = html;
		}

		public void onClick(GMarker marker) {
			marker.openInfoWindowHtml(html);
		}

		public void onDblClick(GMarker marker) {
		}
	}
}
