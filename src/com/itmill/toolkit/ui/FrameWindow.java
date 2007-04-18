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

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;

/**
 * <p>
 * An application frame window component. This component implements a window
 * that contains a hierarchical set of frames. Each frame can contain a
 * web-page, window or a set of frames that divides the space horizontally or
 * vertically.
 * </p>
 * 
 * <p>
 * A <code>FrameWindow</code> can't contain any components directly (as it
 * contains only a set of frames) and thus the container interface methods do
 * nothing.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class FrameWindow extends Window {

	private Frameset frameset = new Frameset();

	/**
	 * Constructs a new frame window.
	 */
	public FrameWindow() {
	}

	/**
	 * Constructs a new frame window.
	 * 
	 * @param caption
	 *            th etitle of the window.
	 */
	public FrameWindow(String caption) {
		super(caption);
	}

	/**
	 * Gets the window's UIDL tag.
	 * 
	 * @return the window's UIDL tag as <code>String</code>.
	 */
	public String getTag() {
		return "framewindow";
	}

	/**
	 * Gets the main frameset of the window. This set contains all the top-level
	 * frames of the window. New contents are added by adding frames to this
	 * frameset.
	 * 
	 * @return the top-level frame set of this frame window.
	 */
	public Frameset getFrameset() {
		return frameset;
	}

	/**
	 * Paints the window contents.
	 * 
	 * @param target
	 *            the paint target event.
	 * @throws PaintException
	 *             if the paint operation fails.
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#paintContent(PaintTarget)
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		super.paintContent(target);

		// Paint frameset
		getFrameset().paint(target);
	}

	/**
	 * An individual frame that contains either a window or the contents of the
	 * url set to frame.
	 * 
	 * <p>
	 * The frames can be only created to framesets using the
	 * <code>newFrame</code> method of the frameset.
	 * </p>
	 */
	public class Frame {

		/**
		 * URL of the frame contents.
		 */
		private URL url;

		/**
		 * Name of the frame.
		 */
		private String name;

		/**
		 * Window connected to frame or null.
		 */
		private Window window;

		/**
		 * Window connected to frame or null.
		 */
		private Resource resource;

		/**
		 * String representation of the width.
		 */
		private String width = "*";

		/**
		 * Parent frameset.
		 */
		protected Frameset parentFrameset;

		/**
		 * Gets the URL of the frame.
		 * 
		 * @return the URl.
		 */
		public URL getURL() {
			return window == null ? url : window.getURL();
		}

		/**
		 * Gets the parent frameset.
		 * 
		 * @return the parent frameset.
		 */
		public Frameset getParentFrameset() {
			return parentFrameset;
		}

		/**
		 * Gets the Name of the frame.
		 * 
		 * @return the Name.
		 */
		public String getName() {
			return window == null ? name : window.getName();
		}

		/**
		 * Gets the Window connected to frame.
		 * 
		 * @return the window.
		 */
		public Window getWindow() {
			return window;
		}

		/**
		 * Gets the Resource connected to frame.
		 * 
		 * @return the resource.
		 */
		public Resource getResource() {
			return resource;
		}

		/**
		 * Sets the Absolute width/height of the frame in pixels.
		 * 
		 * @param widthInPixel
		 *            the width in Pixel.
		 */
		public void setAbsoluteSize(int widthInPixels) {
			width = String.valueOf(widthInPixels);
			requestRepaint();
		}

		/**
		 * Sets the frame size to be freely specified by the terminal.
		 */
		public void setFreeSize() {
			width = "*";
			requestRepaint();
		}

		/**
		 * Sets the frame width/height as a percentage of the containing
		 * frameset size.
		 * 
		 * @param widthInPercents
		 *            the frame width in percent.
		 */
		public void setRelativeSize(int widthInPercents) {
			if (widthInPercents < 0 || widthInPercents > 100)
				throw new IllegalArgumentException("Relative width must "
						+ "be between 0% and 100%");
			width = String.valueOf(widthInPercents) + "%";
			requestRepaint();
		}

		/**
		 * Paints the frame.
		 * 
		 * @param target
		 *            the paint target.
		 * @throws PaintException
		 *             if the paint operation fails.
		 */
		private void paint(PaintTarget target) throws PaintException {
			target.startTag("frame");
			if (getResource() != null)
				target.addAttribute("src", getResource());
			else
				target.addAttribute("src", getURL().toString());
			target.addAttribute("name", getName());
			target.endTag("frame");
		}
	}

	/**
	 * Vertical or horizontal set of frames.
	 */
	public class Frameset extends Frame {

		/**
		 * List of frames ordered from left to right or from top to bottom.
		 */
		private LinkedList frames = new LinkedList();

		/**
		 * <code>true</code> if the frames are on top of each other. If
		 * <code>false</code> the frames are side by side.
		 */
		private boolean vertical = false;

		/**
		 * Gets the list of frames.
		 * 
		 * @return the unmodifiable list of frames.
		 */
		public List getFrames() {
			return Collections.unmodifiableList(frames);
		}

		/**
		 * Creates the new frame containing a window.
		 * 
		 * <p>
		 * The new frame will be in the end of the frames list.
		 * </p>
		 * 
		 * @param window
		 *            the window connected to the frame.
		 * @return the new Frame.
		 */
		public Frame newFrame(Window window) {
			return newFrame(window, size());
		}

		/**
		 * Creates the new frame containing a window.
		 * 
		 * <p>
		 * The new frame will be put before the frame identified by the given
		 * index. The indexes of the frame previously in the given position and
		 * all the positions after it are incremented by one.
		 * </p>
		 * 
		 * @param window
		 *            the window connected to the frame.
		 * @param index
		 *            the given index.
		 */
		public Frame newFrame(Window window, int index) {
			Frame f = new Frame();
			f.window = window;
			f.parentFrameset = this;
			frames.add(index, f);
			if (getApplication() != null)
				getApplication().addWindow(window);
			requestRepaint();
			return f;
		}

		/**
		 * Creates the new frame containing a url.
		 * 
		 * <p>
		 * The new frame will be put in the end of the frames list.
		 * </p>
		 * 
		 * @param url
		 *            the URL of the frame contents.
		 * @param name
		 *            the Name of the frame.
		 * @return the new frame.
		 */
		public Frame newFrame(URL url, String name) {
			return newFrame(url, name, size());
		}

		/**
		 * Creates the new frame containing a resource.
		 * 
		 * <p>
		 * The new frame will be put in the end of the frames list.
		 * </p>
		 * 
		 * @param resource
		 *            the resource.
		 * @param name
		 *            the Name of the frame.
		 * @return the new frame.
		 */
		public Frame newFrame(Resource resource, String name) {
			return newFrame(resource, name, size());
		}

		/**
		 * Creates the new frame containing a url.
		 * 
		 * <p>
		 * The new frame will be put before the frame identified by the given
		 * index. The indexes of the frame previously in the given position and
		 * all the positions after it are incremented by one.
		 * </p>
		 * 
		 * @param url
		 *            the URL of the frame contents.
		 * @param name
		 *            the Name of the frame.
		 * @param index
		 *            the given index.
		 * @return the new frame.
		 */
		public Frame newFrame(URL url, String name, int index) {
			Frame f = new Frame();
			f.url = url;
			f.name = name;
			f.parentFrameset = this;
			frames.add(index, f);
			requestRepaint();
			return f;
		}

		/**
		 * Creates the new frame containing a resource.
		 * 
		 * <p>
		 * The new frame will be put before the frame identified by the given
		 * index. The indexes of the frame previously in the given position and
		 * all the positions after it are incremented by one.
		 * </p>
		 * 
		 * @param resource
		 *            the resource.
		 * @param name
		 *            the Name of the frame.
		 * @param index
		 *            the given index.
		 * @return the new frame.
		 */
		public Frame newFrame(Resource resource, String name, int index) {
			Frame f = new Frame();
			f.resource = resource;
			f.name = name;
			f.parentFrameset = this;
			frames.add(index, f);
			requestRepaint();
			return f;
		}

		/**
		 * Creates the new frameset.
		 * 
		 * <p>
		 * The new frame will be put before the frame identified by the given
		 * index. The indexes of the frame previously in the given position and
		 * all the positions after it are incremented by one.
		 * </p>
		 * 
		 * @param isVertical
		 *            is the frames are on top of each other.
		 * @param index
		 *            the given index.
		 * @return the new frameset.
		 */
		public Frameset newFrameset(boolean isVertical, int index) {
			Frameset f = new Frameset();
			f.setVertical(isVertical);
			f.parentFrameset = this;
			frames.add(index, f);
			requestRepaint();
			return f;
		}

		/**
		 * Removes the frame from this frameset.
		 * 
		 * @param frame
		 *            the frame to remove.
		 */
		public void removeFrame(Frame frame) {
			frames.remove(frame);
			frame.parentFrameset = null;
			requestRepaint();
		}

		/**
		 * Removes all frames from this frameset.
		 */
		public void removeAllFrames() {
			for (Iterator i = frames.iterator(); i.hasNext();)
				((Frame) i.next()).parentFrameset = null;
			frames.clear();
			requestRepaint();
		}

		/**
		 * Number of frames in this frameset.
		 * 
		 * @return the size.
		 */
		public int size() {
			return frames.size();
		}

		/**
		 * Sets the framaset to be vertical.
		 * 
		 * <p>
		 * By setting this true, the frames will be ordered on top of each other
		 * from top to bottom. Setting this false, the frames will be ordered
		 * side by side from left to right.
		 * </p>
		 * 
		 * @param isVertical
		 *            is the frames are on top of each other.
		 */
		public void setVertical(boolean isVertical) {
			this.vertical = isVertical;
			requestRepaint();
		}

		/**
		 * Checks if the frameset is vertical.
		 * 
		 * <p>
		 * If this is true, the frames will be ordered on top of each other from
		 * top to bottom, otherwise the frames will be ordered side by side from
		 * left to right.
		 * </p>
		 * 
		 * @return <code>true</code> if the frameset is Vertical, otherwise
		 *         <code>false</code>.
		 */
		public boolean isVertical() {
			return vertical;
		}

		/**
		 * Gets the frame by name.
		 * 
		 * @param name
		 *            the Name of the frame.
		 * @return the Frame having the given name or null if the frame is not
		 *         found.
		 */
		public Frame getFrame(String name) {
			if (name == null)
				return null;
			for (Iterator i = frames.iterator(); i.hasNext();) {
				Frame f = (Frame) i.next();
				if (name.equals(f.getName()))
					return f;
			}
			return null;
		}

		/**
		 * Gets the frame by index.
		 * 
		 * @param index
		 *            the given index.
		 * @return the Frame having the given index or null if the frame is not
		 *         found
		 */
		public Frame getFrame(int index) {
			if (index >= 0 && index < frames.size())
				return (Frame) frames.get(index);
			return null;
		}

		/**
		 * Paints the frameset.
		 * 
		 * @param target
		 *            the Paint Target.
		 * @throws PaintException
		 *             if the Paint operation fails.
		 */
		private void paint(PaintTarget target) throws PaintException {
			target.startTag("frameset");
			if (!frames.isEmpty()) {
				StringBuffer widths = null;
				for (Iterator i = frames.iterator(); i.hasNext();) {
					Frame f = (Frame) i.next();
					if (widths == null)
						widths = new StringBuffer();
					else
						widths.append(',');
					widths.append(f.width);
				}
				if (vertical)
					target.addAttribute("rows", widths.toString());
				else
					target.addAttribute("cols", widths.toString());
				for (Iterator i = frames.iterator(); i.hasNext();) {
					Frame f = (Frame) i.next();
					if (Frameset.class.isAssignableFrom(f.getClass()))
						((Frameset) f).paint(target);
					else
						f.paint(target);
				}
			}
			target.endTag("frameset");
		}

		/**
		 * Sets the application for all the frames in this frameset.
		 * 
		 * @param fromApplication
		 * @param toApplication
		 */
		private void setApplication(Application fromApplication,
				Application toApplication) {
			for (Iterator i = frames.iterator(); i.hasNext();) {
				Frame f = (Frame) i.next();
				if (f instanceof Frameset)
					((Frameset) f).setApplication(fromApplication,
							toApplication);
				else if (f.window != null) {
					if (toApplication == null) {
						fromApplication.removeWindow(f.window);
					} else
						toApplication.addWindow(f.window);
				}
			}
		}
	}

	/**
	 * Setting the application for frame window also sets the application for
	 * all the frames.
	 * 
	 * @see com.itmill.toolkit.ui.Window#setApplication(Application)
	 */
	public void setApplication(Application application) {
		Application fromApplication = getApplication();
		super.setApplication(application);
		Frameset fs = getFrameset();
		if (fs != null)
			fs.setApplication(fromApplication, application);
	}

	/**
	 * Frame windows does not support scrolling.
	 * 
	 * @return <code>true</code> if it is scrollable,otherwise
	 *         <code>false</code>.
	 */
	public boolean isScrollable() {
		return false;
	}

	/**
	 * Enables or disables scrolling.
	 * 
	 * @see com.itmill.toolkit.terminal.Scrollable#setScrollable(boolean)
	 */
	public void setScrollable(boolean isScrollingEnabled) {
	}

	/**
	 * Sets the scroll X offset.
	 * 
	 * @see com.itmill.toolkit.terminal.Scrollable#setScrollOffsetX(int)
	 */
	public void setScrollOffsetX(int pixelsScrolledLeft) {
	}

	/**
	 * Gets the scroll Y offset.
	 * 
	 * @see com.itmill.toolkit.terminal.Scrollable#setScrollOffsetY(int)
	 */
	public void setScrollOffsetY(int pixelsScrolledDown) {
	}

	/**
	 * Frame window does not support adding components directly.
	 * 
	 * <p>
	 * To add component to frame window, normal window must be first created and
	 * then attached to frame window as a frame.
	 * </p>
	 * 
	 * @param c
	 *            the component to be added.
	 * @see com.itmill.toolkit.ui.ComponentContainer#addComponent(Component)
	 * 
	 */
	public void addComponent(Component c) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
