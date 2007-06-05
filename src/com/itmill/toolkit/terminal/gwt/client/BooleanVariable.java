package com.itmill.toolkit.terminal.gwt.client;

public class BooleanVariable extends Variable {

	private boolean value = true;

	public BooleanVariable(Component owner, String name, String id) {
		super(owner, name, id);
	}
	

	void update() {
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
