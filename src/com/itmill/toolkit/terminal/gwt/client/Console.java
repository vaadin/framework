package com.itmill.toolkit.terminal.gwt.client;

public interface Console {

	public abstract void log(String msg);

	public abstract void error(String msg);

	public abstract void printObject(Object msg);

	public abstract void dirUIDL(UIDL u);

}