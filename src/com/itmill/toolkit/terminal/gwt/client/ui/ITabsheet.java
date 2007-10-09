package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITabsheet extends FlowPanel implements Paintable {

	public static final String CLASSNAME = "i-tabsheet";

	String id;

	ApplicationConnection client;

	ArrayList tabKeys = new ArrayList();

	ArrayList captions = new ArrayList();

	int activeTabIndex = 0;

	private TabBar tb;
	private ITabsheetPanel tp;
	private Element deco;

	private TabListener tl = new TabListener() {

		public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
			if (client != null && activeTabIndex != tabIndex)
				ITabsheet.this.client.updateVariable(id, "selected", ""
						+ tabKeys.get(tabIndex), true);
		}

		public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
			// TODO give user indication of progress
			return true;
		}

	};

	public ITabsheet() {
		setStyleName(CLASSNAME);

		tb = new TabBar();
		tp = new ITabsheetPanel();
		deco = DOM.createDiv();

		tp.setStyleName(CLASSNAME + "-content");
		tb.setStyleName(CLASSNAME + "-tabs");
		DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");

		add(tb);
		add(tp);
		DOM.appendChild(getElement(), deco);

		tb.addTabListener(tl);

		clearTabs();
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;
		id = uidl.getId();

		// Use cached sub-tree if available
		if (uidl.getBooleanAttribute("cached"))
			return;

		// Adjust width and height
		String h = uidl.hasAttribute("height") ? uidl
				.getStringAttribute("height") : null;
		String w = uidl.hasAttribute("width") ? uidl
				.getStringAttribute("width") : null;
		setWidth(w != null ? w : "auto");

		// Try to calculate the height as close as possible
		if (h != null) {
			// First, calculate needed pixel height
			setHeight(h);
			int neededHeight = getOffsetHeight();
			setHeight("");
			// Then calculate the size the content area needs to be
			tp.setHeight("0");
			DOM.setStyleAttribute(tp.getElement(), "overflow", "hidden");
			int height = getOffsetHeight();
			tp.setHeight(neededHeight - height + "px");
			DOM.setStyleAttribute(tp.getElement(), "overflow", "");
		} else {
			tp.setHeight("auto");
			// We don't need overflow:auto when tabsheet height is not set
			DOM.setStyleAttribute(tp.getElement(), "overflow", "hidden");
		}

		UIDL tabs = uidl.getChildUIDL(0);
		boolean keepCurrentTabs = tabKeys.size() == tabs.getNumberOfChildren();
		for (int i = 0; keepCurrentTabs && i < tabKeys.size(); i++)
			keepCurrentTabs = tabKeys.get(i).equals(
					tabs.getChildUIDL(i).getStringAttribute("key"))
					&& captions.get(i).equals(
							tabs.getChildUIDL(i).getStringAttribute("caption"));
		if (keepCurrentTabs) {
			int index = 0;
			for (Iterator it = tabs.getChildIterator(); it.hasNext();) {
				UIDL tab = (UIDL) it.next();
				if (tab.getBooleanAttribute("selected")) {
					activeTabIndex = index;
					UIDL contentUIDL = tab.getChildUIDL(0);
					Widget content = client.getWidget(contentUIDL);
					((Paintable) content).updateFromUIDL(contentUIDL, client);
					tp.remove(index);
					tp.insert(content, index);
				}
				index++;
			}
		} else {
			tabKeys.clear();
			captions.clear();
			clearTabs();

			int index = 0;
			for (Iterator it = tabs.getChildIterator(); it.hasNext();) {
				UIDL tab = (UIDL) it.next();
				String key = tab.getStringAttribute("key");
				String caption = tab.getStringAttribute("caption");

				captions.add(caption);
				tabKeys.add(key);

				tb.addTab(caption);

				if (tab.getBooleanAttribute("selected")) {
					Widget content = client.getWidget(tab.getChildUIDL(0));
					tp.add(content);
					activeTabIndex = index;
					((Paintable) content).updateFromUIDL(tab.getChildUIDL(0),
							client);
				} else
					tp.add(new ILabel(""));

				index++;
			}
		}

		// Open selected tab
		tb.selectTab(activeTabIndex);
		tp.showWidget(activeTabIndex);

	}

	private void clearTabs() {
		int i = tb.getTabCount();
		while (i > 0)
			tb.removeTab(--i);
		tp.clear();

		// Get rid of unnecessary 100% cell heights in TabBar (really ugly hack)
		Element tr = DOM.getChild(DOM.getChild(tb.getElement(), 0), 0);
		Element rest = DOM.getChild(
				DOM.getChild(tr, DOM.getChildCount(tr) - 1), 0);
		DOM.removeElementAttribute(rest, "style");
	}
}
