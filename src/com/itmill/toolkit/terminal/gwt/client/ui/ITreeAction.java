package com.itmill.toolkit.terminal.gwt.client.ui;


/**
 * This class is used for "row actions" in ITree and ITable
 */
public class ITreeAction extends IAction {
	
	String targetKey = "";
	String actionKey = "";
	
	public ITreeAction(IActionOwner owner) {
		super(owner);
	}
	
	public ITreeAction(IActionOwner owner, String target, String action) {
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

	public String getHTML() {
		StringBuffer sb = new StringBuffer();
		if(iconUrl != null) {
			sb.append("<img src=\""+iconUrl+"\" alt=\"icon\" />");
		}
		sb.append(caption);
		return sb.toString();
	}
}
