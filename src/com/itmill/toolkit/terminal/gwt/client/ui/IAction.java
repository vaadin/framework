package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.Command;
import com.itmill.toolkit.terminal.gwt.client.Client;

/**
 *
 */
public class IAction implements Command {
	
	IActionOwner owner;
	
	String targetKey = "";
	String actionKey = "";
	
	String iconUrl = null;
	
	String caption = "";
	
	public IAction(IActionOwner owner) {
		this.owner = owner;
	}
	
	public IAction(IActionOwner owner, String target, String action) {
		this(owner);
		this.targetKey = target;
		this.actionKey = action;
	}
	
	
	/**
	 * Sends message to server that this action has been fired.
	 * Messages are "standard" Toolkit messages whose value is comma
	 * separated pair of targetKey (row, treeNod ...) and actions id.
	 * 
	 * Variablename is always "action".
	 * 
	 * Actions are always sent immediatedly to server.
	 */
	public void execute() {
		owner.getClient().updateVariable(
				owner.getPaintableId(), 
				"action", 
				targetKey + "," + actionKey, 
				true);
		owner.getClient().getContextMenu().hide();
	}

	public String getActionKey() {
		return actionKey;
	}

	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}

	public String getTargetKey() {
		return targetKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	public String getHTMLRepresentation() {
		StringBuffer sb = new StringBuffer();
		if(iconUrl != null) {
			sb.append("<img src=\""+iconUrl+"\" alt=\"icon\" />");
		}
		sb.append(caption);
		return sb.toString();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}

/**
 * Action owner must provide a set of actions for context menu
 * and IAction objects.
 */
interface IActionOwner {
	
	/**
	 * @return Array of IActions
	 */
	public IAction[] getActions();

	public Client getClient();
	
	public String getPaintableId();
}