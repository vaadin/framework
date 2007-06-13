package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkTabsheet extends TabPanel implements Paintable {

	String id;

	Client client;

	ArrayList tabKeys = new ArrayList();

	ArrayList captions = new ArrayList();

	int activeTabIndex = 0;

	TabListener tl = new TabListener() {

		public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
			TkTabsheet.this.client.updateVariable(id, "selected", tabIndex,
					true);
		}

		public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
			return true;
		}

	};

	public TkTabsheet() {
		addTabListener(new TabListener() {

			public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
				if (client != null && activeTabIndex != tabIndex)
					TkTabsheet.this.client.updateVariable(id, "selected", ""
							+ tabKeys.get(tabIndex), true);
			}

			public boolean onBeforeTabSelected(SourcesTabEvents sender,
					int tabIndex) {
				return true;
			}

		});

	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();

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
					Widget content = client.createWidgetFromUIDL(tab
							.getChildUIDL(0));
					getTabBar().selectTab(index);
					DeckPanel dp = getDeckPanel();
					dp.remove(index);
					dp.insert(content, index);
					dp.showWidget(index);
				}
				index++;
			}
		} else {
			tabKeys.clear();
			captions.clear();
			clear();
			int index = 0;
			for (Iterator it = tabs.getChildIterator(); it.hasNext();) {
				UIDL tab = (UIDL) it.next();
				String key = tab.getStringAttribute("key");
				String caption = tab.getStringAttribute("caption");
				captions.add(caption);
				tabKeys.add(key);
				if (tab.getBooleanAttribute("selected")) {
					activeTabIndex = index;
					Widget content = client.createWidgetFromUIDL(tab
							.getChildUIDL(0));
					this.add(content, caption);
					this.selectTab(this.getWidgetIndex(content));
				} else {
					this.add(new Label(), caption);
				}
				index++;
			}
		}

	}
}
