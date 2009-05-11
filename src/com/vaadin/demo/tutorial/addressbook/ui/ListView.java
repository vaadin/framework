package com.vaadin.demo.tutorial.addressbook.ui;

import com.vaadin.ui.SplitPanel;

public class ListView extends SplitPanel {
	public ListView(PersonList personList, PersonForm personForm) {
		addStyleName("view");
		setFirstComponent(personList);
		setSecondComponent(personForm);
		setSplitPosition(40);
	}
}