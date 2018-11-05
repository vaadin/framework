package com.vaadin.test.cdi.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.navigator.PushStateNavigation;

@Theme("valo")
@SuppressWarnings("serial")
@CDIUI("subpath")
@PreserveOnRefresh
@PushStateNavigation
public class SubPathUI extends RootPathUI {
}
