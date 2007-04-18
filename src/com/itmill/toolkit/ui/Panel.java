/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Scrollable;
import com.itmill.toolkit.terminal.Sizeable;

/**
 * Panel - a simple single component container.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Panel extends AbstractComponentContainer implements Sizeable,
		Scrollable, ComponentContainer.ComponentAttachListener,
		ComponentContainer.ComponentDetachListener {

	/**
	 * Layout of the panel.
	 */
	private Layout layout;

	/**
	 * Width of the panel or -1 if unspecified.
	 */
	private int width = -1;

	/**
	 * Height of the panel or -1 if unspecified.
	 */
	private int height = -1;

	/**
	 * Width unit.
	 */
	private int widthUnit = Sizeable.UNITS_PIXELS;

	/**
	 * Height unit.
	 */
	private int heightUnit = Sizeable.UNITS_PIXELS;

	/**
	 * Scroll X position.
	 */
	private int scrollOffsetX = 0;

	/**
	 * Scroll Y position.
	 */
	private int scrollOffsetY = 0;

	/**
	 * Scrolling mode.
	 */
	private boolean scrollable = false;

	/**
	 * Creates a new empty panel. Ordered layout is used.
	 */
	public Panel() {
		this(new OrderedLayout());
	}

	/**
	 * Creates a new empty panel with given layout. Layout must be non-null.
	 * 
	 * @param layout
	 *            the layout used in the panel.
	 */
	public Panel(Layout layout) {
		setLayout(layout);
	}

	/**
	 * Creates a new empty panel with caption. Ordered layout is used.
	 * 
	 * @param caption
	 *            the caption used in the panel.
	 */
	public Panel(String caption) {
		this(caption, new OrderedLayout());
	}

	/**
	 * Creates a new empty panel with caption.
	 * 
	 * @param caption
	 *            the caption of the panel.
	 * @param layout
	 *            the layout used in the panel.
	 */
	public Panel(String caption, Layout layout) {
		this(layout);
		setCaption(caption);
	}

	/**
	 * Gets the current layout of the panel.
	 * 
	 * @return the Current layout of the panel.
	 */
	public Layout getLayout() {
		return this.layout;
	}

	/**
	 * Sets the layout of the panel. All the components are moved to new layout.
	 * 
	 * @param layout
	 *            the New layout of the panel.
	 */
	public void setLayout(Layout layout) {

		// Only allow non-null layouts
		if (layout == null)
			layout = new OrderedLayout();

		// Sets the panel to be parent for the layout
		layout.setParent(this);
		dependsOn(layout);

		// If panel already contains a layout, move the contents to new one
		// and detach old layout from the panel
		if (this.layout != null) {
			layout.moveComponentsFrom(this.layout);
			removeDirectDependency(this.layout);
			this.layout.setParent(null);
		}

		// Removes the event listeners from the old layout
		if (this.layout != null) {
			this.layout
					.removeListener((ComponentContainer.ComponentAttachListener) this);
			this.layout
					.removeListener((ComponentContainer.ComponentDetachListener) this);
		}

		// Sets the new layout
		this.layout = layout;

		// Adds the event listeners for new layout
		layout.addListener((ComponentContainer.ComponentAttachListener) this);
		layout.addListener((ComponentContainer.ComponentDetachListener) this);
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param target
	 *            the Paint Event.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		layout.paint(target);
		target.addVariable(this, "height", getHeight());
		target.addVariable(this, "width", getWidth());
		if (isScrollable()) {
			target.addVariable(this, "scrollleft", getScrollOffsetX());
			target.addVariable(this, "scrolldown", getScrollOffsetY());
		}
	}

	/**
	 * Gets the component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "panel";
	}

	/**
	 * Adds the component into this container.
	 * 
	 * @param c
	 *            the component to be added.
	 * @see com.itmill.toolkit.ui.AbstractComponentContainer#addComponent(com.itmill.toolkit.ui.Component)
	 */
	public void addComponent(Component c) {
		layout.addComponent(c);
		// No repaint request is made as we except the underlaying container to
		// request repaints
	}

	/**
	 * Removes the component from this container.
	 * 
	 * @param c
	 *            The component to be added.
	 * @see com.itmill.toolkit.ui.AbstractComponentContainer#removeComponent(com.itmill.toolkit.ui.Component)
	 */
	public void removeComponent(Component c) {
		layout.removeComponent(c);
		// No repaint request is made as we except the underlaying container to
		// request repaints
	}

	/**
	 * Gets the component container iterator for going trough all the components
	 * in the container.
	 * 
	 * @return the Iterator of the components inside the container.
	 * @see com.itmill.toolkit.ui.ComponentContainer#getComponentIterator()
	 */
	public Iterator getComponentIterator() {
		return layout.getComponentIterator();
	}

	/**
	 * Gets the height in pixels.
	 * 
	 * @return The height in pixels or negative value if not assigned.
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the Width in pixel.
	 * 
	 * @return The width in pixels or negative value if not assigned.
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the height in pixels. Use negative value to let the client decide
	 * the height.
	 * 
	 * @param height
	 *            the height to set.
	 * @see com.itmill.toolkit.terminal.Sizeable#setHeight(int)
	 */
	public void setHeight(int height) {
		this.height = height;
		requestRepaint();
	}

	/**
	 * Sets the width in pixels. Use negative value to allow the client decide
	 * the width.
	 * 
	 * @param width
	 *            the width to set.
	 * @see com.itmill.toolkit.terminal.Sizeable#setWidth(int)
	 */
	public void setWidth(int width) {
		this.width = width;
		requestRepaint();
	}

	/**
	 * Called when one or more variables handled by the implementing class are
	 * changed.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#changeVariables(Object,
	 *      Map)
	 */
	public void changeVariables(Object source, Map variables) {
		super.changeVariables(source, variables);

		// Get new size
		Integer newWidth = (Integer) variables.get("width");
		Integer newHeight = (Integer) variables.get("height");
		if (newWidth != null && newWidth.intValue() != getWidth())
			setWidth(newWidth.intValue());
		if (newHeight != null && newHeight.intValue() != getHeight())
			setHeight(newHeight.intValue());

		// Scrolling
		Integer newScrollX = (Integer) variables.get("scrollleft");
		Integer newScrollY = (Integer) variables.get("scrolldown");
		if (newScrollX != null && newScrollX.intValue() != getScrollOffsetX())
			setScrollOffsetX(newScrollX.intValue());
		if (newScrollY != null && newScrollY.intValue() != getScrollOffsetY())
			setScrollOffsetY(newScrollY.intValue());
	}

	/**
	 * Gets the height property units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeightUnits()
	 */
	public int getHeightUnits() {
		return heightUnit;
	}

	/**
	 * Gets the width property units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidthUnits()
	 */
	public int getWidthUnits() {
		return widthUnit;
	}

	/**
	 * Sets the height units. Panel supports only Sizeable.UNITS_PIXELS and this
	 * is ignored.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setHeightUnits(int)
	 */
	public void setHeightUnits(int units) {
		// Ignored
	}

	/**
	 * Sets the width units. Panel supports only Sizeable.UNITS_PIXELS, and this
	 * is ignored.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setWidthUnits(int)
	 */
	public void setWidthUnits(int units) {
		// Ignored
	}

	/* Scrolling functionality */

	/* Documented in interface */
	public int getScrollOffsetX() {
		return scrollOffsetX;
	}

	/* Documented in interface */
	public int getScrollOffsetY() {
		return scrollOffsetY;
	}

	/* Documented in interface */
	public boolean isScrollable() {
		return scrollable;
	}

	/* Documented in interface */
	public void setScrollable(boolean isScrollingEnabled) {
		if (scrollable != isScrollingEnabled) {
			scrollable = isScrollingEnabled;
			requestRepaint();
		}
	}

	/* Documented in interface */
	public void setScrollOffsetX(int pixelsScrolledLeft) {
		if (pixelsScrolledLeft < 0)
			throw new IllegalArgumentException(
					"Scroll offset must be at least 0");
		if (this.scrollOffsetX != pixelsScrolledLeft) {
			scrollOffsetX = pixelsScrolledLeft;
			requestRepaint();
		}
	}

	/* Documented in interface */
	public void setScrollOffsetY(int pixelsScrolledDown) {
		if (pixelsScrolledDown < 0)
			throw new IllegalArgumentException(
					"Scroll offset must be at least 0");
		if (this.scrollOffsetY != pixelsScrolledDown) {
			scrollOffsetY = pixelsScrolledDown;
			requestRepaint();
		}
	}

	/* Documented in superclass */
	public void replaceComponent(Component oldComponent, Component newComponent) {

		layout.replaceComponent(oldComponent, newComponent);
	}

	/**
	 * A new component is attached to container.
	 * 
	 * @see com.itmill.toolkit.ui.ComponentContainer.ComponentAttachListener#componentAttachedToContainer(com.itmill.toolkit.ui.ComponentContainer.ComponentAttachEvent)
	 */
	public void componentAttachedToContainer(ComponentAttachEvent event) {
		if (event.getContainer() == layout)
			fireComponentAttachEvent(event.getAttachedComponent());
	}

	/**
	 * A component has been detached from container.
	 * 
	 * @see com.itmill.toolkit.ui.ComponentContainer.ComponentDetachListener#componentDetachedFromContainer(com.itmill.toolkit.ui.ComponentContainer.ComponentDetachEvent)
	 */
	public void componentDetachedFromContainer(ComponentDetachEvent event) {
		if (event.getContainer() == layout)
			fireComponentDetachEvent(event.getDetachedComponent());
	}

	/**
	 * Notifies the component that it is connected to an application.
	 * 
	 * @see com.itmill.toolkit.ui.Component#attach()
	 */
	public void attach() {
		if (layout != null)
			layout.attach();
	}

	/**
	 * Notifies the component that it is detached from the application.
	 * 
	 * @see com.itmill.toolkit.ui.Component#detach()
	 */
	public void detach() {
		if (layout != null)
			layout.detach();
	}

	/**
	 * Removes all components from this container.
	 * 
	 * @see com.itmill.toolkit.ui.ComponentContainer#removeAllComponents()
	 */
	public void removeAllComponents() {
		layout.removeAllComponents();
	}

}
