package com.itmill.toolkit.terminal.gwt.client;

import com.itmill.toolkit.terminal.gwt.client.ui.Component;

public abstract class Variable {

	protected Component owner;
	protected boolean immediate = false;
	protected String name;
	protected String id;
	
	public Variable(Component owner, String name,  String id) {
		this.owner = owner;
		this.name = name;
		this.id = id;
	}
	
	abstract void update();

	public boolean isImmediate() {
		return immediate;
	}

	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract String getEncodedValue();

}
