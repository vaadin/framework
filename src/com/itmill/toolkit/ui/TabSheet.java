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

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.*;

/** 
 * Tabsheet component. 
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class TabSheet extends AbstractComponentContainer {

	/** 
	 * Linked list of component tabs. 
	 */
	private LinkedList tabs = new LinkedList();

	/** 
	 * Tab -> caption mapping. 
	 */
	private Hashtable tabCaptions = new Hashtable();

	/** 
	 * Tab -> icon mapping .
	 */
	private Hashtable tabIcons = new Hashtable();

	/** 
	 * Selected tab. 
	 */
	private Component selected = null;
	private KeyMapper keyMapper = new KeyMapper();

	/** 
	 * Holds the value of property tabsHIdden. 
	 */
	private boolean tabsHidden;

	/** 
	 * Constructs a new Tabsheet.
	 * Tabsheet is immediate by default.
	 */
	public TabSheet() {
		super();
		setImmediate(true);
	}

	/** 
	 * Gets the component container iterator for going trough all the components in the container.
	 * @return the Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return java.util.Collections.unmodifiableList(tabs).iterator();
	}

	/** 
	 * Removes the component from this container.
	 * @param c the component to be removed.
	 */
	public void removeComponent(Component c) {
		if (c != null && tabs.contains(c)) {
			super.removeComponent(c);
			keyMapper.remove(c);
			tabs.remove(c);
			tabCaptions.remove(c);
			if (c.equals(selected)) {
				if (tabs.isEmpty())
					selected = null;
				else {
					selected = (Component) tabs.getFirst();
					fireSelectedTabChange();
				}
			}
			requestRepaint();
		}
	}

	/** 
	 * Adds the component into this container.
	 * The component is added as a tab where its default tab-caption is 
	 * the caption of the component.
	 * @param c the component to be added.
	 */
	public void addComponent(Component c) {
		addTab(c, c.getCaption(), getIcon());
	}

	/** 
	 * Adds the new tab into TabSheet.
	 * @param c the component to be added onto tab.
	 * @param caption the caption of the tab.
	 * @param icon the Set the icon of the tab.
	 */
	public void addTab(Component c, String caption, Resource icon) {
		if (c != null) {
			tabs.addLast(c);
			tabCaptions.put(c, caption != null ? caption : "");
			if (icon != null)
				tabIcons.put(c, icon);
			if (selected == null) {
				selected = c;
				fireSelectedTabChange();
			}
			super.addComponent(c);
			requestRepaint();
		}
	}

	/** 
	 * Gets the component UIDL tag.
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "tabsheet";
	}

	/** 
	 * Moves all components from another container to this container.
	 * The components are removed from the other container.
	 * @param source the container components are removed from.
	 */
	public void moveComponentsFrom(ComponentContainer source) {
		for (Iterator i = source.getComponentIterator(); i.hasNext();) {
			Component c = (Component) i.next();
			String caption = null;
			Resource icon = null;
			if (TabSheet.class.isAssignableFrom(source.getClass())) {
				caption = ((TabSheet) source).getTabCaption(c);
				icon = ((TabSheet) source).getTabIcon(c);
			}
			source.removeComponent(c);
			addTab(c, caption, icon);

		}
	}

	/** 
	 * Paints the content of this component.
	 * @param event the Paint Event.
	 * @throws PaintException if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		if (areTabsHidden())
			target.addAttribute("hidetabs", true);

		target.startTag("tabs");

		for (Iterator i = getComponentIterator(); i.hasNext();) {
			Component c = (Component) i.next();
            if (!c.isVisible()) 
                continue;
			target.startTag("tab");
			Resource icon = getTabIcon(c);
			if (icon != null)
				target.addAttribute("icon", icon);
			String caption = getTabCaption(c);
			if (!c.isEnabled()) {
				target.addAttribute("disabled", true);				
			}
			
			if (caption != null && caption.length() > 0)
				target.addAttribute("caption", caption);
			target.addAttribute("key", keyMapper.key(c));
			if (c.equals(selected)) {
				target.addAttribute("selected", true);
				c.paint(target);
			}
			target.endTag("tab");
		}

		target.endTag("tabs");

		if (selected != null)
			target.addVariable(this, "selected", keyMapper.key(selected));
	}

	/** 
	 * Are tabs hidden.
	 * @return the Property visibility.
	 */
	public boolean areTabsHidden() {
		return this.tabsHidden;
	}

	/** 
	 * Setter for property tabsHidden.
	 * @param tabsHidden True if the tabs should be hidden.
	 */
	public void hideTabs(boolean tabsHidden) {
		this.tabsHidden = tabsHidden;
		requestRepaint();
	}

	/** 
	 * Gets the caption for a component.
	 * @param c the component. 
	 */
	public String getTabCaption(Component c) {
		String caption = (String) tabCaptions.get(c);
		if (caption == null)
			caption = "";
		return caption;
	}

	/** 
	 * Sets the caption for a component.
	 * @param c the component.
	 * @param caption the caption to set. 
	 */
	public void setTabCaption(Component c, String caption) {
		tabCaptions.put(c, caption);
		requestRepaint();
	}

	/** 
	 * Gets the icon for a component. 
	 * @param c the component.
	 */
	public Resource getTabIcon(Component c) {
		return (Resource) tabIcons.get(c);
	}

	/** ]
	 * Sets the icon for a component.
	 * @param c
	 * @param icon 
	 */
	public void setTabIcon(Component c, Resource icon) {
		if (icon == null)
			tabIcons.remove(c);
		else
			tabIcons.put(c, icon);
		requestRepaint();
	}

	/** 
	 * Sets the selected tab. 
	 * @param c 
	 */
	public void setSelectedTab(Component c) {
		if (c != null && tabs.contains(c) && !selected.equals(c)) {
			selected = c;
			fireSelectedTabChange();
			requestRepaint();
		}
	}

	/** 
	 * Gets the selected tab.
	 * @return the selected tab. 
	 */
	public Component getSelectedTab() {
		return selected;
	}

	/**
	 * Invoked when the value of a variable has changed.
	 * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object, java.util.Map)
	 */
	public void changeVariables(Object source, Map variables) {
		if (variables.containsKey("selected"))
			setSelectedTab(
				(Component) keyMapper.get((String) variables.get("selected")));
	}

	/* Documented in superclass */
	public void replaceComponent(
		Component oldComponent,
		Component newComponent) {

		// Gets the captions
		String oldCaption = getTabCaption(oldComponent);
		Resource oldIcon = getTabIcon(oldComponent);
		String newCaption = getTabCaption(newComponent);
		Resource newIcon = getTabIcon(newComponent);

		// Gets the locations			
		int oldLocation = -1;
		int newLocation = -1;
		int location = 0;
		for (Iterator i = tabs.iterator(); i.hasNext();) {
			Component component = (Component) i.next();

			if (component == oldComponent)
				oldLocation = location;
			if (component == newComponent)
				newLocation = location;

			location++;
		}

		if (oldLocation == -1)
			addComponent(newComponent);
		else if (newLocation == -1) {
			removeComponent(oldComponent);
			keyMapper.remove(oldComponent);
			addComponent(newComponent);
			tabs.remove(newComponent);
			tabs.add(oldLocation, newComponent);
			setTabCaption(newComponent, oldCaption);
			setTabIcon(newComponent, oldIcon);
		} else {
			if (oldLocation > newLocation) {
				tabs.remove(oldComponent);
				tabs.add(newLocation, oldComponent);
				tabs.remove(newComponent);
				tabs.add(oldLocation, newComponent);
			} else {
				tabs.remove(newComponent);
				tabs.add(oldLocation, newComponent);
				tabs.remove(oldComponent);
				tabs.add(newLocation, oldComponent);
			}
			setTabCaption(newComponent, oldCaption);
			setTabIcon(newComponent, oldIcon);
			setTabCaption(oldComponent, newCaption);
			setTabIcon(oldComponent, newIcon);

			requestRepaint();
		}
	}

	/* Click event ************************************************ */

	private static final Method SELECTED_TAB_CHANGE_METHOD;
	static {
		try {
			SELECTED_TAB_CHANGE_METHOD =
				SelectedTabChangeListener.class.getDeclaredMethod(
					"selectedTabChange",
					new Class[] { SelectedTabChangeEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException();
		}
	}

	/** 
	 * Selected Tab Change event. This event is thrown, when the selected tab
	 * in the tab sheet is changed.
	 * 
	 * @author IT Mill Ltd.
     * @version @VERSION@
	 * @since 3.0
	 */
	public class SelectedTabChangeEvent extends Component.Event {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3258129141914940469L;

        /** 
         * New instance of selected tab change event
		* @param source the Source of the event.
		*/
		public SelectedTabChangeEvent(Component source) {
			super(source);
		}

		/** 
		 * Select where the event occurred
		 * @return the Source of the event.
		 */
		public Select getSelect() {
			return (Select) getSource();
		}
	}

	/** 
	 * Selected Tab Change Event listener
	 * @author IT Mill Ltd.
	 * 
	 * @version @VERSION@
	 * @since 3.0
	 */
	public interface SelectedTabChangeListener {

		/**
		 * Visible tab in tab sheet has has been changed.
		 * @param event the Selected tab change event.
		 */
		public void selectedTabChange(SelectedTabChangeEvent event);
	}

	/** 
	 * Adds the selected tab change listener
	 * @param listener the Listener to be added.
	 */
	public void addListener(SelectedTabChangeListener listener) {
		addListener(
			SelectedTabChangeEvent.class,
			listener,
			SELECTED_TAB_CHANGE_METHOD);
	}

	/** 
	 * Removes the selected tab change listener
	 * @param listener the Listener to be removed.
	 */
	public void removeListener(SelectedTabChangeListener listener) {
		removeListener(
			SelectedTabChangeEvent.class,
			listener,
			SELECTED_TAB_CHANGE_METHOD);
	}

	/** 
	 * Emits the options change event. 
	 */
	protected void fireSelectedTabChange() {
		fireEvent(new SelectedTabChangeEvent(this));
	}
}
