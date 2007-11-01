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

	private final TabBar tb;

	private final ITabsheetPanel tp;

	private final Element deco;

	private final TabListener tl = new TabListener() {

		public void onTabSelected(SourcesTabEvents sender, final int tabIndex) {
			if (ITabsheet.this.client != null
					&& ITabsheet.this.activeTabIndex != tabIndex) {
				addStyleDependentName("loading");
				ITabsheet.this.tp.clear();
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						ITabsheet.this.client.updateVariable(ITabsheet.this.id,
								"selected", ""
										+ ITabsheet.this.tabKeys.get(tabIndex),
								true);
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

		this.tb = new TabBar();
		this.tp = new ITabsheetPanel();
		this.deco = DOM.createDiv();

		this.tp.setStyleName(CLASSNAME + "-content");
		addStyleDependentName("loading"); // Indicate initial progress
		this.tb.setStyleName(CLASSNAME + "-tabs");
		DOM.setElementProperty(this.deco, "className", CLASSNAME + "-deco");

		add(this.tb);
		add(this.tp);
		DOM.appendChild(getElement(), this.deco);

		this.tb.addTabListener(this.tl);

		clearTabs();
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;
		this.id = uidl.getId();

		if (client.updateComponent(this, uidl, false)) {
			return;
		}

		// Add proper stylenames for all elements
		if (uidl.hasAttribute("style")) {
			String[] styles = uidl.getStringAttribute("style").split(" ");
			String decoBaseClass = CLASSNAME + "-deco";
			String decoClass = decoBaseClass;
			for (int i = 0; i < styles.length; i++) {
				this.tb.addStyleDependentName(styles[i]);
				this.tp.addStyleDependentName(styles[i]);
				decoClass += " " + decoBaseClass + "-" + styles[i];
			}
			DOM.setElementProperty(this.deco, "className", decoClass);
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
			this.tp.setHeight("auto");
			// We don't need overflow:auto when tabsheet height is not set
			// TODO reconsider, we might sometimes have wide, non-breaking
			// content
			DOM.setStyleAttribute(this.tp.getElement(), "overflow", "hidden");
		}

		// Render content
		UIDL tabs = uidl.getChildUIDL(0);
		boolean keepCurrentTabs = this.tabKeys.size() == tabs
				.getNumberOfChildren();
		for (int i = 0; keepCurrentTabs && i < this.tabKeys.size(); i++) {
			keepCurrentTabs = this.tabKeys.get(i).equals(
					tabs.getChildUIDL(i).getStringAttribute("key"))
					&& this.captions.get(i).equals(
							tabs.getChildUIDL(i).getStringAttribute("caption"));
		}
		if (keepCurrentTabs) {
			int index = 0;
			for (Iterator it = tabs.getChildIterator(); it.hasNext();) {
				UIDL tab = (UIDL) it.next();
				if (tab.getBooleanAttribute("selected")) {
					this.activeTabIndex = index;
					renderContent(tab.getChildUIDL(0));
				}
				index++;
			}
		} else {
			this.tabKeys.clear();
			this.captions.clear();
			clearTabs();

			int index = 0;
			for (Iterator it = tabs.getChildIterator(); it.hasNext();) {
				UIDL tab = (UIDL) it.next();
				String key = tab.getStringAttribute("key");
				String caption = tab.getStringAttribute("caption");
				if (caption == null) {
					caption = "";
				}

				this.captions.add(caption);
				this.tabKeys.add(key);

				// Add new tab (additional SPAN-element for loading indication)
				this.tb.insertTab("<span>" + caption + "</span>", true, this.tb
						.getTabCount());

				// Add placeholder content
				this.tp.add(new ILabel(""));

				if (tab.getBooleanAttribute("selected")) {
					this.activeTabIndex = index;
					renderContent(tab.getChildUIDL(0));
				}
				index++;
			}
		}

		// Open selected tab
		this.tb.selectTab(this.activeTabIndex);
		// tp.showWidget(activeTabIndex);

	}

	private void renderContent(final UIDL contentUIDL) {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				// Loading done, start drawing
				Widget content = ITabsheet.this.client.getWidget(contentUIDL);
				ITabsheet.this.tp.remove(ITabsheet.this.activeTabIndex);
				ITabsheet.this.tp
						.insert(content, ITabsheet.this.activeTabIndex);
				ITabsheet.this.tp.showWidget(ITabsheet.this.activeTabIndex);
				((Paintable) content).updateFromUIDL(contentUIDL,
						ITabsheet.this.client);
				removeStyleDependentName("loading");
			}
		});
	}

	private void clearTabs() {
		int i = this.tb.getTabCount();
		while (i > 0) {
			this.tb.removeTab(--i);
		}
		this.tp.clear();

		// Get rid of unnecessary 100% cell heights in TabBar (really ugly hack)
		Element tr = DOM.getChild(DOM.getChild(this.tb.getElement(), 0), 0);
		Element rest = DOM.getChild(
				DOM.getChild(tr, DOM.getChildCount(tr) - 1), 0);
		DOM.removeElementAttribute(rest, "style");
	}

	public void setHeight(String height) {
		this.height = height;
		iLayout();
	}

	public void iLayout() {
		if (this.height != null) {
			// Make content zero height
			this.tp.setHeight("0");
			DOM.setStyleAttribute(this.tp.getElement(), "overflow", "hidden");
			// First, calculate needed pixel height
			super.setHeight(this.height);
			int neededHeight = getOffsetHeight();
			super.setHeight("");
			// Then calculate the size the content area needs to be
			int pixelHeight = getOffsetHeight();
			this.tp.setHeight(neededHeight - pixelHeight + "px");
			DOM.setStyleAttribute(this.tp.getElement(), "overflow", "");
		}
		Util.runAncestorsLayout(this);
	}
}
