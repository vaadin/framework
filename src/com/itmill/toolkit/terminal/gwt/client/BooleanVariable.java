package com.itmill.toolkit.terminal.gwt.client;

import com.itmill.toolkit.terminal.gwt.client.ui.Component;

public class BooleanVariable extends Variable {

	private boolean value = true;

	public BooleanVariable(Component owner, String name, String id) {
		super(owner, name, id);
	}
	

	public void update() {
		owner.getClient().updateVariable(this);
		if(immediate)
			owner.getClient().flushVariables();

	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}


	public String getEncodedValue() {
		return ( value ? "true" : "false" );
	}

}
