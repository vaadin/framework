package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * Abstract base class for ordered layouts. Use either vertical or horizontal
 * subclass.
 * 
 * @author IT Mill Ltd
 */
public abstract class IOrderedLayout extends ComplexPanel implements Container {

	public static final String CLASSNAME = "i-orderedlayout";

	public static final int ORIENTATION_VERTICAL = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;

	public static final int ALIGNMENT_LEFT = 1;
	public static final int ALIGNMENT_RIGHT = 2;
	public static final int ALIGNMENT_TOP = 4;
	public static final int ALIGNMENT_BOTTOM = 8;
	public static final int ALIGNMENT_HORIZONTAL_CENTER = 16;
	public static final int ALIGNMENT_VERTICAL_CENTER = 32;

	int orientationMode = ORIENTATION_VERTICAL;

	protected HashMap componentToCaption = new HashMap();

	protected ApplicationConnection client;

	/**
	 * Contains reference to Element where Paintables are wrapped. Normally a TR
	 * or a TBODY element.
	 */
	protected Element childContainer;

	/*
	 * Elements that provides the Layout interface implementation.
	 */
	protected Element size;
	protected Element margin;

	protected Element topMargin = null;
	protected Element bottomMargin = null;

	private static final String structure = "<div><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"table-layout:fixed;\"><tbody></tbody></table></div>";

	public IOrderedLayout(int orientation) {
		orientationMode = orientation;
		constructDOM();
		setStyleName(CLASSNAME);
	}

	protected void constructDOM() {
		size = DOM.createDiv();
		DOM.setInnerHTML(size, structure);
		margin = DOM.getFirstChild(size);
		Element tBody = DOM.getFirstChild(DOM.getFirstChild(margin));
		if (orientationMode == ORIENTATION_HORIZONTAL) {
			childContainer = DOM.createTR();
			DOM.appendChild(tBody, childContainer);
		} else
			childContainer = tBody;
		setElement(size);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

		this.client = client;

		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;

		// Set size
		if (uidl.hasAttribute("width")) {
			setWidth(uidl.getStringAttribute("width"));
			DOM.setStyleAttribute(DOM.getFirstChild(margin), "width", "100%");
		} else {
			setWidth("");
			DOM.setStyleAttribute(DOM.getFirstChild(margin), "width", "");
		}
		if (uidl.hasAttribute("height")) {
			setHeight(uidl.getStringAttribute("height"));
			// TODO override setHeight() method and move these there
			DOM.setStyleAttribute(margin, "height", "100%");
			DOM.setStyleAttribute(DOM.getFirstChild(margin), "height", "100%");
		} else {
			setHeight("");
			DOM.setStyleAttribute(margin, "height", "");
			DOM.setStyleAttribute(DOM.getFirstChild(margin), "height", "");
		}

		ArrayList uidlWidgets = new ArrayList();
		for (Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL uidlForChild = (UIDL) it.next();
			Widget child = client.getWidget(uidlForChild);
			uidlWidgets.add(child);
		}

		ArrayList oldWidgets = getPaintables();

		Iterator oldIt = oldWidgets.iterator();
		Iterator newIt = uidlWidgets.iterator();
		Iterator newUidl = uidl.getChildIterator();

		Widget oldChild = null;
		while (newIt.hasNext()) {
			Widget child = (Widget) newIt.next();
			UIDL childUidl = (UIDL) newUidl.next();

			if (oldChild == null && oldIt.hasNext()) {
				// search for next old Paintable which still exists in layout
				// and delete others
				while (oldIt.hasNext()) {
					oldChild = (Widget) oldIt.next();
					// now oldChild is an instance of Paintable
					if (uidlWidgets.contains(oldChild))
						break;
					else {
						removePaintable((Paintable) oldChild);
						oldChild = null;
					}
				}
			}
			if (oldChild == null) {
				// we are adding components to layout
				add(child);
			} else if (child == oldChild) {
				// child already attached and updated
				oldChild = null;
			} else if (hasChildComponent(child)) {
				// current child has been moved, re-insert before current
				// oldChild
				// TODO this might be optimized by moving only container element
				// to correct position
				removeCaption(child);
				int index = getWidgetIndex(oldChild);
				if (componentToCaption.containsKey(oldChild))
					index--;
				remove(child);
				this.insert(child, index);
			} else {
				// insert new child before old one
				int index = getWidgetIndex(oldChild);
				insert(child, index);
			}
			((Paintable) child).updateFromUIDL(childUidl, client);
		}
		// remove possibly remaining old Paintable object which were not updated
		while (oldIt.hasNext()) {
			oldChild = (Widget) oldIt.next();
			Paintable p = (Paintable) oldChild;
			if (!uidlWidgets.contains(p))
				removePaintable(p);
		}

		// Component alignments as a comma separated list.
		// See com.itmill.toolkit.ui.OrderedLayout.java for possible values.
		int[] alignments = uidl.getIntArrayAttribute("alignments");

		int alignmentIndex = 0;

		// Insert alignment attributes
		Iterator it = getPaintables().iterator();
		while (it.hasNext()) {

			// Calculate alignment info
			int alignment = alignments[alignmentIndex++];
			// Vertical alignment
			String vAlign = "top";
			if ((alignment & ALIGNMENT_TOP) == ALIGNMENT_TOP)
				vAlign = "top";
			else if ((alignment & ALIGNMENT_BOTTOM) == ALIGNMENT_BOTTOM)
				vAlign = "bottom";
			else if ((alignment & ALIGNMENT_VERTICAL_CENTER) == ALIGNMENT_VERTICAL_CENTER)
				vAlign = "middle";
			// Horizontal alignment
			String hAlign = "";
			if ((alignment & ALIGNMENT_LEFT) == ALIGNMENT_LEFT)
				hAlign = "left";
			else if ((alignment & ALIGNMENT_RIGHT) == ALIGNMENT_RIGHT)
				hAlign = "right";
			else if ((alignment & ALIGNMENT_HORIZONTAL_CENTER) == ALIGNMENT_HORIZONTAL_CENTER)
				hAlign = "center";

			Element td = DOM.getParent(((Widget) it.next()).getElement());
			DOM.setStyleAttribute(td, "vertical-align", vAlign);
			// TODO use one-cell table to implement horizontal alignments
			DOM.setStyleAttribute(td, "text-align", hAlign);
		}

		handleMargins(uidl);

	}

	/**
	 * Retuns a list of Paintables currently rendered in layout
	 * 
	 * @return list of Paintable objects
	 */
	protected ArrayList getPaintables() {
		ArrayList al = new ArrayList();
		Iterator it = iterator();
		while (it.hasNext()) {
			Widget w = (Widget) it.next();
			if (w instanceof Paintable)
				al.add(w);
		}
		return al;
	}

	/**
	 * Removes Paintable from DOM and its reference from ApplicationConnection.
	 * 
	 * Also removes Paintable's Caption if one exists
	 * 
	 * @param p
	 *            Paintable to be removed
	 */
	public boolean removePaintable(Paintable p) {
		Caption c = (Caption) componentToCaption.get(p);
		if (c != null) {
			componentToCaption.remove(c);
			remove(c);
		}
		client.unregisterPaintable(p);
		return remove((Widget) p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.gwt.client.Layout#replaceChildComponent(com.google.gwt.user.client.ui.Widget,
	 *      com.google.gwt.user.client.ui.Widget)
	 */
	public void replaceChildComponent(Widget from, Widget to) {
		client.unregisterPaintable((Paintable) from);
		Caption c = (Caption) componentToCaption.get(from);
		if (c != null) {
			remove(c);
			componentToCaption.remove(c);
		}
		int index = getWidgetIndex(from);
		if (index >= 0) {
			remove(index);
			insert(to, index);
		}
	}

	protected void insert(Widget w, int beforeIndex) {
		if (w instanceof Caption) {
			Caption c = (Caption) w;
			// captions go into same container element as their
			// owners
			Element container = DOM.getParent(((UIObject) c.getOwner())
					.getElement());
			Element captionContainer = DOM.createDiv();
			DOM.insertChild(container, captionContainer, 0);
			insert(w, captionContainer, beforeIndex, false);
		} else {
			Element wrapper = createWidgetWrappper();
			DOM.insertChild(childContainer, wrapper, beforeIndex);
			insert(w, getWidgetContainerFromWrapper(wrapper), beforeIndex,
					false);
		}
	}

	protected Element getWidgetContainerFromWrapper(Element wrapper) {
		switch (orientationMode) {
		case ORIENTATION_HORIZONTAL:
			return wrapper;
		default:
			return DOM.getFirstChild(wrapper);
		}
	}

	/**
	 * creates an Element which will contain child widget
	 */
	protected Element createWidgetWrappper() {
		Element td = DOM.createTD();
		//DOM.setStyleAttribute(td, "overflow", "hidden");
		switch (orientationMode) {
		case ORIENTATION_HORIZONTAL:
			return td;
		default:
			Element tr = DOM.createTR();
			DOM.appendChild(tr, td);
			return tr;
		}
	}

	public boolean hasChildComponent(Widget component) {
		return getWidgetIndex(component) >= 0;
	}

	public void updateCaption(Paintable component, UIDL uidl) {

		Caption c = (Caption) componentToCaption.get(component);

		if (Caption.isNeeded(uidl)) {
			if (c == null) {
				int index = getWidgetIndex((Widget) component);
				c = new Caption(component);
				insert(c, index);
				componentToCaption.put(component, c);
			}
			c.updateCaption(uidl);
		} else {
			if (c != null) {
				remove(c);
				componentToCaption.remove(component);
			}
		}
	}

	public void removeCaption(Widget w) {
		Caption c = (Caption) componentToCaption.get(w);
		if (c != null) {
			this.remove(c);
			componentToCaption.remove(w);
		}
	}

	public void add(Widget w) {
		Element wrapper = createWidgetWrappper();
		DOM.appendChild(childContainer, wrapper);
		super.add(w, orientationMode == ORIENTATION_HORIZONTAL ? wrapper : DOM
				.getFirstChild(wrapper));
	}

	public boolean remove(int index) {
		return remove(getWidget(index));
	}

	public boolean remove(Widget w) {
		Element wrapper = DOM.getParent(w.getElement());
		boolean removed = super.remove(w);
		if (removed) {
			if (!(w instanceof Caption)) {
				DOM.removeChild(childContainer,
						orientationMode == ORIENTATION_HORIZONTAL ? wrapper
								: DOM.getParent(wrapper));
			}
			return true;
		}
		return false;
	}

	public Widget getWidget(int index) {
		return getChildren().get(index);
	}

	public int getWidgetCount() {
		return getChildren().size();
	}

	public int getWidgetIndex(Widget child) {
		return getChildren().indexOf(child);
	}

	protected void handleMargins(UIDL uidl) {
		// Modify layout margins
		String marginClasses = "";
		MarginInfo margins = new MarginInfo(uidl.getIntAttribute("margins"));

		// Top margin
		if (margins.hasTop()) {
			marginClasses += " " + StyleConstants.LAYOUT_MARGIN_TOP;
			if (topMargin == null) {
				// We need to insert a new row in to the table
				topMargin = createWidgetWrappper();
				DOM.appendChild(getWidgetContainerFromWrapper(topMargin), DOM
						.createDiv());
				DOM.setElementProperty(topMargin, "className", CLASSNAME
						+ "-toppad");
				if (orientationMode == ORIENTATION_HORIZONTAL) {
					DOM.setElementAttribute(DOM.getFirstChild(topMargin),
							"colspan", "" + getPaintables().size());
				}
				DOM.insertChild(childContainer, topMargin, 0);
			}
		} else {
			if (topMargin != null)
				DOM.removeChild(childContainer, DOM
						.getFirstChild(childContainer));
			topMargin = null;
		}

		// Right margin
		if (margins.hasRight())
			marginClasses += " " + StyleConstants.LAYOUT_MARGIN_RIGHT;

		// Bottom margin
		if (margins.hasBottom()) {
			marginClasses += " " + StyleConstants.LAYOUT_MARGIN_BOTTOM;
			if (bottomMargin == null) {
				// We need to insert a new row in to the table
				bottomMargin = createWidgetWrappper();
				DOM.appendChild(getWidgetContainerFromWrapper(bottomMargin),
						DOM.createDiv());
				DOM.setElementProperty(bottomMargin, "className", CLASSNAME
						+ "-bottompad");
				if (orientationMode == ORIENTATION_HORIZONTAL) {
					DOM.setElementAttribute(DOM.getFirstChild(bottomMargin),
							"colspan", "" + getPaintables().size());
				}
				DOM.appendChild(childContainer, bottomMargin);
			}
		} else {
			if (bottomMargin != null)
				DOM.removeChild(childContainer, DOM.getChild(childContainer,
						DOM.getChildCount(childContainer) - 1));
			bottomMargin = null;
		}

		// Left margin
		if (margins.hasLeft())
			marginClasses += " " + StyleConstants.LAYOUT_MARGIN_LEFT;

		// Add
		DOM.setElementProperty(margin, "className", marginClasses);
	}

}
