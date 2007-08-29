package com.itmill.toolkit.terminal.gwt.client.ui;

public class IShortcutAction {

	private ShortcutKeyCombination sc;
	private String caption;
	private String key;

	public IShortcutAction(String key, ShortcutKeyCombination sc, String caption) {
		this.sc = sc;
		this.key = key;
		this.caption = caption;
	}
	
	public ShortcutKeyCombination getShortcutCombination() {
		return sc;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public String getKey() {
		return key;
	}

}
