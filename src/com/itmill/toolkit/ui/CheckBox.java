package com.itmill.toolkit.ui;

import com.itmill.toolkit.data.Property;

public class CheckBox extends Button {
	public CheckBox() {
		super();
		setSwitchMode(true);
	}

	public CheckBox(String caption, boolean initialState) {
		super(caption, initialState);
		setSwitchMode(true);
	}

	public CheckBox(String caption, ClickListener listener) {
		super(caption, listener);
		setSwitchMode(true);
	}

	public CheckBox(String caption, Object target, String methodName) {
		super(caption, target, methodName);
		setSwitchMode(true);
	}

	public CheckBox(String caption, Property dataSource) {
		super(caption, dataSource);
		setSwitchMode(true);
	}

	public CheckBox(String caption) {
		super(caption);
		setSwitchMode(true);
	}
	
}
