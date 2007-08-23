package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ISplitPanel extends SimplePanel implements Paintable {
	public static final int ORIENTATION_HORIZONTAL = 0;
	public static final int ORIENTATION_VERTICAL = 1;
	
	private int orientation;
	private HorizontalSplitPanel sph;
	private VerticalSplitPanel spv;
	private Widget firstChild;
	private Widget secondChild;
	
	public ISplitPanel() {
		this(ORIENTATION_HORIZONTAL);
	}
	
	public ISplitPanel(int orientation) {
		super();
		setOrientetion(orientation);
	}
	
	private void setOrientetion(int orientation) {
		this.orientation = orientation;
		if(orientation == ORIENTATION_HORIZONTAL) {
			this.sph = new HorizontalSplitPanel();
			this.setWidget(sph);
			if(spv != null) {
				// TODO cleanup contained widgets
				this.spv = null;
			}
		} else {
			this.spv = new VerticalSplitPanel();
			this.setWidget(spv);
			if(sph != null) {
				// TODO cleanup contained widgets
				this.sph = null;
			}
		}
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		client.updateComponent(this, uidl, true);
		
		setSplitPosition(uidl.getStringAttribute("position"));
		
		setWidth(uidl.getStringAttribute("width"));
		setHeight(uidl.getStringAttribute("height"));
		
		
		Paintable newFirstChild = (Paintable) client.getWidget(uidl.getChildUIDL(0));
		Paintable newSecondChild = (Paintable) client.getWidget(uidl.getChildUIDL(1));
		if(firstChild != newFirstChild) {
			if(firstChild != null)
				client.unregisterPaintable((Paintable) firstChild);
			setFirstWidget((Widget) newFirstChild);
		}
		if(secondChild != newSecondChild) {
			if(secondChild != null)
				client.unregisterPaintable((Paintable) secondChild);
			setSecondWidget((Widget) newSecondChild);
		}
		newFirstChild.updateFromUIDL(uidl.getChildUIDL(0), client);
		newSecondChild.updateFromUIDL(uidl.getChildUIDL(1), client);
	}
	
	private void setSplitPosition(String pos) {
		if(orientation == ORIENTATION_HORIZONTAL) {
			this.sph.setSplitPosition(pos);
		} else {
			this.spv.setSplitPosition(pos);
		}
	}

	private void setFirstWidget(Widget w) {
		firstChild = w;
		if(orientation == ORIENTATION_HORIZONTAL) {
			this.sph.setLeftWidget(w);
		} else {
			this.spv.setTopWidget(w);
		}
	}
	
	private void setSecondWidget(Widget w) {
		secondChild  = w;
		if(orientation == ORIENTATION_HORIZONTAL) {
			this.sph.setRightWidget(w);
		} else {
			this.spv.setBottomWidget(w);
		}
	}

	public void setHeight(String height) {
		super.setHeight(height);
		if(orientation == ORIENTATION_HORIZONTAL) {
			sph.setHeight(height);
		} else {
			spv.setHeight(height);
		}
	}

	public void setWidth(String width) {
		super.setWidth(width);
		if(orientation == ORIENTATION_HORIZONTAL) {
			sph.setWidth(width);
		} else {
			spv.setWidth(width);
		}
	}
	
}
