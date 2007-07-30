package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ISlider extends Widget implements Paintable {
	
	public static final String CLASSNAME = "i-slider";
	
	ApplicationConnection client;
	
	String id;
	
	private boolean immediate;
	private boolean disabled;
	private boolean readonly;
	
	private int handleSize;
	private double min;
	private double max;
	private int resolution;
	private Double value;
	private boolean vertical;
	private int size = -1;
	private boolean arrows;
	
	/* DOM element for slider's base */
	private Element base;
	
	/* DOM element for slider's handle */
	private Element handle;
	
	/* DOM element for decrement arrow */
	private Element smaller;
	
	/* DOM element for increment arrow */
	private Element bigger;
	
	/* Temporary dragging/animation variables */
	private boolean dragging = false;
	private Timer anim;
	
	public ISlider() {
		super();
		
		setElement(DOM.createDiv());
		base = DOM.createDiv();
		handle = DOM.createDiv();
		smaller = DOM.createDiv();
		bigger = DOM.createDiv();
		
		setStyleName(CLASSNAME);
		DOM.setAttribute(base, "className", CLASSNAME+"-base");
		DOM.setAttribute(handle, "className", CLASSNAME+"-handle");
		DOM.setAttribute(smaller, "className", CLASSNAME+"-smaller");
		DOM.setAttribute(bigger, "className", CLASSNAME+"-bigger");
		
		DOM.appendChild(getElement(), bigger);
		DOM.appendChild(getElement(), smaller);
		DOM.appendChild(getElement(), base);
		DOM.appendChild(base, handle);
		
		// Hide initially
		DOM.setStyleAttribute(smaller, "display", "none");
		DOM.setStyleAttribute(bigger, "display", "none");
		
		DOM.sinkEvents(base, Event.ONMOUSEDOWN);
		DOM.sinkEvents(handle, Event.MOUSEEVENTS);
		DOM.sinkEvents(smaller, Event.ONMOUSEDOWN);
		DOM.sinkEvents(bigger, Event.ONMOUSEDOWN);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		this.client = client;
		
		// Ensure correct implementation (handle own caption)
		if (client.updateComponent(this, uidl, false))
			return;
		
		immediate = uidl.getBooleanAttribute("immediate");
		disabled = uidl.getBooleanAttribute("disabled");
		readonly = uidl.getBooleanAttribute("readonly");
		
		vertical = uidl.hasAttribute("vertical");
		arrows = uidl.hasAttribute("arrows");
		
		if(arrows) {
			DOM.setStyleAttribute(smaller, "display", "block");
			DOM.setStyleAttribute(bigger, "display", "block");
			if(vertical) {
				int arrowSize = Integer.parseInt(DOM.getAttribute(smaller, "offsetWidth"));
				DOM.setStyleAttribute(bigger, "marginLeft", arrowSize+"px");
				DOM.setStyleAttribute(bigger, "marginRight", arrowSize+"px");
			}
		}
		
		if(vertical)
			addStyleName(CLASSNAME+"-vertical");
		else
			removeStyleName(CLASSNAME+"-vertical");
		
		min = uidl.getDoubleAttribute("min");
		max = uidl.getDoubleAttribute("max");
		resolution = uidl.getIntAttribute("resolution");
		value = new Double(uidl.getDoubleVariable("value"));
		
		handleSize = uidl.getIntAttribute("hsize");
		
		if(uidl.hasAttribute("size"))
			size = uidl.getIntAttribute("size");
		
		buildBase();
		
		if(!vertical) {
			// Draw handle with a delay to allow base to gain maximum width
			Timer delay = new Timer() {
				public void run() {
					buildHandle();
					setValue(value, true);
				}
			};
			delay.schedule(100);
		} else {
			buildHandle();
			setValue(value, true);
		}
	}

	private void buildBase() {
		if(vertical) {
			if(size > -1)
				DOM.setStyleAttribute(base, "height", size + "px");
			else DOM.setStyleAttribute(base, "height", "120px");
		} else {
			if(size > -1)
				DOM.setStyleAttribute(base, "width", size + "px");
			else {
				Element p = DOM.getParent(getElement());
				if(Integer.parseInt(DOM.getAttribute(p, "offsetWidth")) > 50)
					DOM.setStyleAttribute(base, "width", "auto");
				else {
					// Set minimum of 50px width and adjust after all 
					// components have (supposedly) been drawn completely.
					DOM.setStyleAttribute(base, "width", "50px");
					Timer adjust = new Timer() {
						public void run() {
							Element p = DOM.getParent(getElement());
							if(Integer.parseInt(DOM.getAttribute(p, "offsetWidth")) > 50)
								DOM.setStyleAttribute(base, "width", "auto");
						}
					};
					adjust.schedule(100);
				}
			}
		}
		// Allow absolute positioning of handle
		DOM.setStyleAttribute(base, "position", "relative");
		
		// TODO attach listeners for clicking on base, focusing and arrow keys
	}
	
	private void buildHandle() {
		// Allow absolute positioning
		DOM.setStyleAttribute(handle, "position", "absolute");
		
		if(vertical) {
			// TODO
		} else {
			int t = Integer.parseInt(DOM.getAttribute(base, "offsetHeight")) - Integer.parseInt(DOM.getAttribute(handle, "offsetHeight"));
			DOM.setStyleAttribute(handle, "top", (t/2)+"px");
			DOM.setStyleAttribute(handle, "left", "0px");
			int w = (int) (Double.parseDouble(DOM.getAttribute(base, "offsetWidth")) / 100 * handleSize);
			DOM.setStyleAttribute(handle, "width", w+"px");
		}
		
	}
	
	private void setValue(Double value, boolean animate) {
		if(vertical) {
			// TODO
		} else {
			int handleWidth = Integer.parseInt(DOM.getAttribute(handle, "offsetWidth"));
			int baseWidth = Integer.parseInt(DOM.getAttribute(base, "offsetWidth"));
			int range = baseWidth - handleWidth;
			double v = value.doubleValue();
			double valueRange = max - min;
			final double pos = range * ((v - min) / valueRange);
			
			String styleLeft = DOM.getStyleAttribute(handle, "left");
			int left = Integer.parseInt(styleLeft.substring(0, styleLeft.length()-2));

			if((int)pos != left && animate) {
				if(anim != null)
					anim.cancel();
				anim = new Timer() {
					private int left;
					private int goal = (int)pos;
					private int dir = 0;
					public void run() {
						String styleLeft = DOM.getStyleAttribute(handle, "left");
						left = Integer.parseInt(styleLeft.substring(0, styleLeft.length()-2));
						
						// Determine direction
						if(dir == 0)
							dir = (goal-left)/Math.abs(goal-left);
						
						if((dir > 0 && left >= goal) || (dir < 0 && left <= goal)) {
							this.cancel();
							return;
						}
						int increment = (goal - left) / 2;
						DOM.setStyleAttribute(handle, "left", (left+increment)+"px");
					}
				};
				anim.scheduleRepeating(50);
			} else DOM.setStyleAttribute(handle, "left", pos+"px");
			DOM.setAttribute(handle, "title", ""+v);
		}
		
		this.value = value;
	}

	public void onBrowserEvent(Event event) {
		Element targ = DOM.eventGetTarget(event);
		if(dragging || DOM.compare(targ, handle)) {
			processHandleEvent(event);
		} else if(DOM.compare(targ, smaller)) {
			if(DOM.eventGetType(event) == Event.ONMOUSEDOWN)
				decrease();
		} else if(DOM.compare(targ, bigger)) {
			if(DOM.eventGetType(event) == Event.ONMOUSEDOWN)
				increase();
		} else {
			processBaseEvent(event);
		}
	}
	
	private void processHandleEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			if(!disabled && !readonly) {
				anim.cancel();
				dragging = true;
				DOM.setCapture(handle);
				DOM.eventPreventDefault(event); // prevent selecting text
				DOM.eventCancelBubble(event, true);
			}
			break;
		case Event.ONMOUSEMOVE:
			if (dragging) {
				setValueByEvent(event, false);
			}
			break;
		case Event.ONMOUSEUP:
			dragging = false;
			DOM.releaseCapture(handle);
			break;
		default:
			break;
		}
	}
		
	private void processBaseEvent(Event event) {
		if(DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if(!disabled && !readonly && !dragging) {
				setValueByEvent(event, true);
				DOM.eventCancelBubble(event, true);
			}
		}
	}
	
	private void setValueByEvent(Event event, boolean animate) {
		int x = DOM.eventGetClientX(event);
		int y = DOM.eventGetClientY(event);
		double v = min; // Fallback to min
		if(vertical) {
			// TODO
		} else {
			double handleW = Integer.parseInt(DOM.getAttribute(handle, "offsetWidth"));
			double baseX = DOM.getAbsoluteLeft(base) + handleW/2;
			double baseW = Integer.parseInt(DOM.getAttribute(base, "offsetWidth"));
			v = ((x-baseX)/(baseW-handleW)) * (max-min) + min;
		}
		
		if(v < min)
			v = min;
		else if(v > max)
			v = max;
		
		if(resolution > 0) {
			v = (int)(v * (double)Math.pow(10, resolution));
			v = v / (double)Math.pow(10, resolution);
			setValue(new Double(v), animate);
		} else
			setValue(new Double((int)v), animate);
		
	}
	
	private void decrease() {
		double diff = (max-min)/max*10 + (max-min)/10;
		double v = value.doubleValue()-diff;
		if(resolution > 0) {
			v = (int)(v * (double)Math.pow(10, resolution));
			v = v / (double)Math.pow(10, resolution);
		} else v = (int)v;
		if(v < min)
			v = min;
		setValue(new Double(v), true);
	}
	
	private void increase() {
		double diff = (max-min)/max*10 + (max-min)/10;
		double v = value.doubleValue()+diff;
		if(resolution > 0) {
			v = (int)(v * (double)Math.pow(10, resolution));
			v = v / (double)Math.pow(10, resolution);
		} else v = (int)v;
		if(v > max)
			v = max;
		setValue(new Double(v), true);
	}

}
