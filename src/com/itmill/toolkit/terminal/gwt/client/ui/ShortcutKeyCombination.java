package com.itmill.toolkit.terminal.gwt.client.ui;

public class ShortcutKeyCombination {
	
	public static final int SHIFT = 16;
	public static final int CTRL = 17;
	public static final int ALT = 18;
	
	
	
	int keyCode = 0;
	boolean altKey = false;
	boolean ctrlKey = false;
	boolean shiftKey = false;
	boolean metaKey = false;
	
	public ShortcutKeyCombination() {
	}
	
	ShortcutKeyCombination(int kc, int[] modifiers) {
		keyCode = kc;
		if(modifiers != null) {
			for (int i = 0; i < modifiers.length; i++) {
				switch (modifiers[i]) {
				case ALT:
					altKey = true;
					break;
				case CTRL:
					ctrlKey = true;
					break;
				case SHIFT:
					shiftKey = true;
					break;
				default:
					break;
				}
			}
		}
	}
	
	public boolean equals(ShortcutKeyCombination other) {
		if( this.keyCode == other.keyCode &&
				this.altKey == other.altKey &&
				this.ctrlKey == other.ctrlKey &&
				this.shiftKey == other.shiftKey)
			return true;
		return false;
	}
}