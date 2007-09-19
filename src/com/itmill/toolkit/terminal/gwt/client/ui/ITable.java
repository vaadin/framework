package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HasWidgets;
import com.itmill.toolkit.terminal.gwt.client.Paintable;

public interface ITable extends HasWidgets, Paintable {
	final int SELECT_MODE_NONE = 0;
	final int SELECT_MODE_SINGLE = 1;
	final int SELECT_MODE_MULTI = 2;
}
