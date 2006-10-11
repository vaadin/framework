/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.enably.tk.event;

import com.enably.tk.terminal.*;

/** Implements the MillStone action framework. This class contains
 * subinterfaces for action handling and listing, and for action handler
 * registrations and unregistration.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Action {

	/** Action title */	
	private String caption;
	
	/** Action icon */
	private Resource icon = null;

	/** Constructs a new action with the given caption.
	 * 
	 * @param caption caption for the new action.
	 */
	public Action(String caption) {
		this.caption = caption;
	}

	/** Constructs a new action with the given caption string and icon.
	 * 
	 * @param caption caption for the new action.
	 * @param icon icon for the new action
	 */
	public Action(String caption, Resource icon) {
		this.caption = caption;
		this.icon = icon;
	}

	/** Returns the action's caption.
	 * 
	 * @return the action's caption as a <code>String</code>
	 */
	public String getCaption() {
		return caption;
	}

	/** Returns the action's icon.
	 * 
	 * @return Icon
	 */
	public Resource getIcon() {
		return icon;
	}

	/** Interface implemented by classes who wish to handle actions.
	 * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
	public interface Handler {
	
		/** Returns the list of actions applicable to this handler.
		 *
		 * @param target The target handler to list actions for. For item 
		 * containers this is the item id.
		 * @param sender The party that would be sending the actions. 
		 * Most of this is the action container.
		 */
		public Action[] getActions(Object target, Object sender);
		
		/** Handles an action for the given target. The handler method
		 * may just discard the action if it's not suitable.
		 * 
		 * @param action The action to be handled
		 * @param sender The sender of the action. This is most often the 
		 * action container.
		 * @param target The target of the <code>action</code>. For item 
		 * containers this is the item id.
		 */
		public void handleAction(Action action, Object sender, Object target);
	}
	
	/** Interface implemented by all components where actions can be
	 * registered. This means that the components lets others to register
	 * as action handlers to it. When the component receives an action
	 * targeting its contents it should loop all action handlers registered
	 * to it and let them hanle the action.
	 * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
	 */
	public interface Container {
		
		/** Registers a new action handler for this container
		 * 
		 * @param actionHandler the new handler to be added.
		 */
		public void addActionHandler(Action.Handler actionHandler);

		/** Remove a previously registered action handler for the contents
		 * of this container.
		 * 
		 * @param actionHandler the handler to be removed
		 */
		public void removeActionHandler(Action.Handler actionHandler);
	}
	
	/** Sets the caption.
	 * @param caption The caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/** Sets the icon.
	 * @param icon The icon to set
	 */
	public void setIcon(Resource icon) {
		this.icon = icon;
	}

}
