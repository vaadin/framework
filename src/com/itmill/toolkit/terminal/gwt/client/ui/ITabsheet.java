package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ITabsheet extends FlowPanel implements Paintable,
		ContainerResizedListener {

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

		public void onTabSelected(SourcesTabEvents sender, final int tabIndex) {
			if (client != null && activeTabIndex != tabIndex) {
				addStyleDependentName("loading");
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						ITabsheet.this.client.updateVariable(id, "selected", ""
								+ tabKeys.get(tabIndex), true);
					}
				});
			}
		}

		public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
			return true;
		}

	};

	private String height;

	public ITabsheet() {
		setStyleName(CLASSNAME);

		tb = new TabBar();
		tp = new ITabsheetPanel();
		deco = DOM.createDiv();

		tp.setStyleName(CLASSNAME + "-content");
		addStyleDependentName("loading"); // Indicate initial progress
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

		if (client.updateComponent(this, uidl, false))
			return;

		// Add proper stylenames for all elements
		if (uidl.hasAttribute("style")) {
			String[] styles = uidl.getStringAttribute("style").split(" ");
			String decoBaseClass = CLASSNAME + "-deco";
			String decoClass = decoBaseClass;
			for (int i = 0; i < styles.length; i++) {
				tb.addStyleDependentName(styles[i]);
				tp.addStyleDependentName(styles[i]);
				decoClass += " " + decoBaseClass + "-" + styles[i];
			}
			DOM.setElementProperty(deco, "className", decoClass);
		}

		// Adjust width and height
		String h = uidl.hasAttribute("height") ? uidl
				.getStringAttribute("height") : null;
		String w = uidl.hasAttribute("width") ? uidl
				.getStringAttribute("width") : null;
		setWidth(w != null ? w : "auto");

		// Height calculations
		if (h != null) {
			setHeight(h);
		} else {
			this.height = null;
			tp.setHeight("auto");
			// We don't need overflow:auto when tabsheet height is not set
			// TODO reconsider, we might sometimes have wide, non-breaking
			// content
			DOM.setStyleAttribute(tp.getElement(), "overflow", "hidden");
		}

		// Render content
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
					renderContent(tab.getChildUIDL(0));
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

				// Add new tab (additional SPAN-element for loading indication)
				tb.insertTab("<span>"+caption+"</span>", true, tb.getTabCount());
				
				// Add placeholder content
				tp.add(new ILabel(""));

				if (tab.getBooleanAttribute("selected")) {
					activeTabIndex = index;
					renderContent(tab.getChildUIDL(0));
				}
				index++;
			}
		}

		// Open selected tab
		tb.selectTab(activeTabIndex);
		// tp.showWidget(activeTabIndex);

	}

	private void renderContent(final UIDL contentUIDL) {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				// Loading done, start drawing
				Widget content = client.getWidget(contentUIDL);
				tp.remove(activeTabIndex);
				tp.insert(content, activeTabIndex);
				tp.showWidget(activeTabIndex);
				((Paintable) content).updateFromUIDL(contentUIDL, client);
				removeStyleDependentName("loading");
			}
		});
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

	public void setHeight(String height) {
		this.height = height;
		iLayout();
	}

	public void iLayout() {
		if (height != null) {
			// Make content zero height
			tp.setHeight("0");
			DOM.setStyleAttribute(tp.getElement(), "overflow", "hidden");
			// First, calculate needed pixel height
			super.setHeight(height);
			int neededHeight = getOffsetHeight();
			super.setHeight("");
			// Then calculate the size the content area needs to be
			int pixelHeight = getOffsetHeight();
			tp.setHeight(neededHeight - pixelHeight + "px");
			DOM.setStyleAttribute(tp.getElement(), "overflow", "");
		}
		Util.runAncestorsLayout(this);
	}
}
