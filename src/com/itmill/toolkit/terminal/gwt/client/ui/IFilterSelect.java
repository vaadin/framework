package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * 
 * TODO needs major refactoring to be easily expandable TODO add new items TODO
 * null selections
 */
public class IFilterSelect extends Composite implements Paintable,
		KeyboardListener, ClickListener {

	public class FilterSelectSuggestion implements Suggestion, Command {

		private String key;
		private String caption;
		private String iconUri;

		public FilterSelectSuggestion(UIDL uidl) {
			this.key = uidl.getStringAttribute("key");
			this.caption = uidl.getStringAttribute("caption");
			if (uidl.hasAttribute("icon")) {
				this.iconUri = uidl.getStringAttribute("icon");
			}
		}

		public String getDisplayString() {
			StringBuffer sb = new StringBuffer();
			if (iconUri != null) {
				sb.append("<img src=\"");
				sb.append(iconUri);
				sb.append("\" alt=\"icon\" class=\"i-icon\" />");
			}
			sb.append(caption);
			return sb.toString();
		}

		public String getReplacementString() {
			return caption;
		}

		public int getOptionKey() {
			return Integer.parseInt(key);
		}

		public String getIconUri() {
			return iconUri;
		}

		public void execute() {
			IFilterSelect.this.onSuggestionSelected(this);
		}
	}

	/**
	 * @author mattitahvonen
	 * 
	 */
	public class SuggestionPopup extends PopupPanel implements PositionCallback {
		private static final int EXTRASPACE = 8;

		private static final String Z_INDEX = "30000";

		private SuggestionMenu menu;

		private Element up = DOM.createDiv();
		private Element down = DOM.createDiv();
		private Element status = DOM.createDiv();

		private boolean isPagingEnabled = true;

		SuggestionPopup() {
			super(true);
			this.menu = new SuggestionMenu();
			setWidget(menu);
			setStyleName(CLASSNAME + "-suggestpopup");
			DOM.setStyleAttribute(getElement(), "zIndex", Z_INDEX);

			Element root = getElement();

			DOM.setInnerHTML(up, "<span>Prev</span>");
			DOM.sinkEvents(up, Event.ONCLICK);
			DOM.setInnerHTML(down, "<span>Next</span>");
			DOM.sinkEvents(down, Event.ONCLICK);
			DOM.insertChild(root, up, 0);
			DOM.appendChild(root, down);
			DOM.appendChild(root, status);
			DOM.setElementProperty(status, "className", CLASSNAME + "-status");
		}

		public void showSuggestions(Collection currentSuggestions,
				int currentPage, int totalSuggestions) {
			menu.setSuggestions(currentSuggestions);
			int x = IFilterSelect.this.getAbsoluteLeft();
			int y = IFilterSelect.this.tb.getAbsoluteTop();
			y += IFilterSelect.this.tb.getOffsetHeight();
			this.setPopupPosition(x, y);
			int first = currentPage * PAGELENTH + 1;
			int last = first + currentSuggestions.size() - 1;
			DOM.setInnerText(status, first + "-" + last + "/"
					+ totalSuggestions);
			setPrevButtonActive(first > 1);
			setNextButtonActive(last < totalSuggestions);
			setPopupPositionAndShow(this);

		}

		private void setNextButtonActive(boolean b) {
			if (b) {
				DOM.sinkEvents(down, Event.ONCLICK);
				DOM.setElementProperty(down, "className", CLASSNAME
						+ "-nextpage");
			} else {
				DOM.sinkEvents(down, 0);
				DOM.setElementProperty(down, "className", CLASSNAME
						+ "-nextpage-off");
			}
		}

		private void setPrevButtonActive(boolean b) {
			if (b) {
				DOM.sinkEvents(up, Event.ONCLICK);
				DOM
						.setElementProperty(up, "className", CLASSNAME
								+ "-prevpage");
			} else {
				DOM.sinkEvents(up, 0);
				DOM.setElementProperty(up, "className", CLASSNAME
						+ "-prevpage-off");
			}

		}

		public void selectNextItem() {
			MenuItem cur = menu.getSelectedItem();
			int index = 1 + menu.getItems().indexOf(cur);
			if (menu.getItems().size() > index)
				menu.selectItem((MenuItem) menu.getItems().get(index));
			else if (!clientSideFiltering && hasNextPage())
				filterOptions(currentPage + 1);
		}

		public void selectPrevItem() {
			MenuItem cur = menu.getSelectedItem();
			int index = -1 + menu.getItems().indexOf(cur);
			if (index > -1)
				menu.selectItem((MenuItem) menu.getItems().get(index));
			else if (index == -1) {
				if (currentPage > 0)
					filterOptions(currentPage - 1);
			} else {
				menu.selectItem((MenuItem) menu.getItems().get(
						menu.getItems().size() - 1));
			}
		}

		public void onBrowserEvent(Event event) {
			Element target = DOM.eventGetTarget(event);
			if (DOM.compare(target, up)
					|| DOM.compare(target, DOM.getChild(up, 0))) {
				filterOptions(currentPage - 1, lastFilter);
			} else if (DOM.compare(target, down)
					|| DOM.compare(target, DOM.getChild(down, 0))) {
				filterOptions(currentPage + 1, lastFilter);
			}
			tb.setFocus(true);
		}

		public void setPagingEnabled(boolean paging) {
			if (isPagingEnabled == paging)
				return;
			if (paging) {
				DOM.setStyleAttribute(this.down, "display", "block");
				DOM.setStyleAttribute(this.up, "display", "block");
				DOM.setStyleAttribute(this.status, "display", "block");
			} else {
				DOM.setStyleAttribute(this.down, "display", "none");
				DOM.setStyleAttribute(this.up, "display", "none");
				DOM.setStyleAttribute(this.status, "display", "none");
			}
			isPagingEnabled = paging;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.user.client.ui.PopupPanel$PositionCallback#setPosition(int,
		 *      int)
		 */
		public void setPosition(int offsetWidth, int offsetHeight) {
			ApplicationConnection.getConsole().log("callback");
			// reset menu size and retrieve its "natural"; size
			menu.setHeight("");
			menu.setWidth("");
			offsetHeight = getOffsetHeight();
			if (!isPagingEnabled && offsetHeight > Window.getClientHeight()) {
				offsetHeight = Window.getClientHeight();
				menu.setHeight((offsetHeight - EXTRASPACE) + "px");
				DOM.setStyleAttribute(menu.getElement(), "overflow", "auto");
				// add scrollbar width
				menu
						.setWidth((menu.getOffsetWidth() * 2 - DOM
								.getElementPropertyInt(menu.getElement(),
										"clientWidth"))
								+ "px");
			}
			if (offsetHeight + getPopupTop() > Window.getClientHeight()) {
				int top = Window.getClientHeight() - offsetHeight - EXTRASPACE
						/ 2;
				setPopupPosition(getPopupLeft(), top);
			}
		}
	}

	public class SuggestionMenu extends MenuBar {

		SuggestionMenu() {
			super(true);
			setStyleName(CLASSNAME + "-suggestmenu");
		}

		public void setSuggestions(Collection suggestions) {
			this.clearItems();
			Iterator it = suggestions.iterator();
			while (it.hasNext()) {
				FilterSelectSuggestion s = (FilterSelectSuggestion) it.next();
				MenuItem mi = new MenuItem(s.getDisplayString(), true, s);
				this.addItem(mi);
				if (s == currentSuggestion)
					selectItem(mi);
			}
		}

		public void doSelectedItemAction() {
			MenuItem item = this.getSelectedItem();
			if (item != null) {
				doItemAction(item, true);
			} else if (allowNewItem) {
				String newItemValue = tb.getText();
				if(!newItemValue.equals("")) {
					client.updateVariable(paintableId, "newitem", newItemValue, true);
				}
			}
			suggestionPopup.hide();
		}
	}

	public static final int FILTERINGMODE_OFF = 0;
	public static final int FILTERINGMODE_STARTSWITH = 1;
	public static final int FILTERINGMODE_CONTAINS = 2;

	private static final String CLASSNAME = "i-filterselect";

	public static final int PAGELENTH = 15;

	private final FlowPanel panel = new FlowPanel();

	private final TextBox tb = new TextBox();

	private final SuggestionPopup suggestionPopup = new SuggestionPopup();

	private final HTML popupOpener = new HTML("");

	private final Image selectedItemIcon = new Image();

	private ApplicationConnection client;

	private String paintableId;

	private int currentPage;

	private Collection currentSuggestions = new ArrayList();

	private boolean immediate;

	private String selectedOptionKey;

	private boolean filtering = false;

	private String lastFilter;

	private int totalSuggestions;

	private FilterSelectSuggestion currentSuggestion;

	private boolean clientSideFiltering;

	private ArrayList allSuggestions;
	private int totalMatches;
	private boolean allowNewItem;

	public IFilterSelect() {
		selectedItemIcon.setVisible(false);
		panel.add(selectedItemIcon);
		panel.add(tb);
		panel.add(popupOpener);
		initWidget(panel);
		setStyleName(CLASSNAME);
		tb.addKeyboardListener(this);
		tb.setStyleName(CLASSNAME + "-input");
		popupOpener.setStyleName(CLASSNAME + "-button");
		popupOpener.addClickListener(this);
	}

	public boolean hasNextPage() {
		if (totalSuggestions > (this.currentPage + 1) * PAGELENTH)
			return true;
		else
			return false;
	}

	public void filterOptions(int page) {
		filterOptions(page, tb.getText());
	}

	public void filterOptions(int page, String filter) {
		if (filter.equals(lastFilter) && currentPage == page) {
			if (!suggestionPopup.isAttached())
				suggestionPopup.showSuggestions(currentSuggestions,
						currentPage, totalMatches);
			return;
		}
		if (!filter.equals(lastFilter)) {
			// we are on subsequant page and text has changed -> reset page
			page = 0;
		}
		if (clientSideFiltering) {
			currentSuggestions.clear();
			for (Iterator it = allSuggestions.iterator(); it.hasNext();) {
				FilterSelectSuggestion s = (FilterSelectSuggestion) it.next();
				String string = s.getDisplayString().toLowerCase();
				if (string.startsWith(filter.toLowerCase())) {
					currentSuggestions.add(s);
				}
			}
			lastFilter = filter;
			currentPage = page;
			suggestionPopup.showSuggestions(currentSuggestions, page,
					currentSuggestions.size());
		} else {
			filtering = true;
			client.updateVariable(paintableId, "filter", filter, false);
			client.updateVariable(paintableId, "page", page, true);
			lastFilter = filter;
			currentPage = page;
		}
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.paintableId = uidl.getId();
		this.client = client;

		if (client.updateComponent(this, uidl, true))
			return;

		if (uidl.hasAttribute("immediate"))
			immediate = true;
		else
			immediate = false;

		if (true) {
			this.suggestionPopup.setPagingEnabled(true);
			clientSideFiltering = false;
		} else {
			this.suggestionPopup.setPagingEnabled(false);
			clientSideFiltering = true;
		}

		allowNewItem = uidl.hasAttribute("allownewitem");

		currentSuggestions.clear();
		UIDL options = uidl.getChildUIDL(0);
		totalSuggestions = uidl.getIntAttribute("totalitems");
		totalMatches = uidl.getIntAttribute("totalMatches");

		String captions = "";
		if (clientSideFiltering) {
			allSuggestions = new ArrayList();
		}
		for (Iterator i = options.getChildIterator(); i.hasNext();) {
			UIDL optionUidl = (UIDL) i.next();
			FilterSelectSuggestion suggestion = new FilterSelectSuggestion(
					optionUidl);
			currentSuggestions.add(suggestion);
			if (clientSideFiltering) {
				allSuggestions.add(suggestion);
			}
			if (optionUidl.hasAttribute("selected")) {
				tb.setText(suggestion.getReplacementString());
				currentSuggestion = suggestion;
			}

			// Collect captions so we can calculate minimum width for textarea
			if (captions.length() > 0)
				captions += "|";
			captions += suggestion.getReplacementString();
		}

		if (filtering && lastFilter.equals(uidl.getStringVariable("filter"))) {
			suggestionPopup.showSuggestions(currentSuggestions, currentPage,
					totalMatches);
			filtering = false;
		}

		// Calculate minumum textarea width
		int minw = minWidth(captions);
		if (Util.isIE()) {
			Element spacer = DOM.createDiv();
			DOM.setStyleAttribute(spacer, "width", minw + "px");
			DOM.setStyleAttribute(spacer, "height", "0");
			DOM.setStyleAttribute(spacer, "overflow", "hidden");
			DOM.appendChild(panel.getElement(), spacer);
		} else {
			DOM.setStyleAttribute(tb.getElement(), "minWidth", minw + "px");
		}

		// Set columns (width) is given
		if (uidl.hasAttribute("cols"))
			DOM.setStyleAttribute(getElement(), "width", uidl
					.getIntAttribute("cols")
					+ "em");

	}

	public void onSuggestionSelected(FilterSelectSuggestion suggestion) {
		currentSuggestion = suggestion;
		String newKey = String.valueOf(suggestion.getOptionKey());
		tb.setText(suggestion.getReplacementString());
		setSelectedItemIcon(suggestion.getIconUri());
		if (!newKey.equals(selectedOptionKey)) {
			selectedOptionKey = newKey;
			client.updateVariable(paintableId, "selected",
					new String[] { selectedOptionKey }, immediate);
			lastFilter = tb.getText();
		}
		suggestionPopup.hide();
	}

	private void setSelectedItemIcon(String iconUri) {
		if (iconUri == null) {
			selectedItemIcon.setVisible(false);
		} else {
			selectedItemIcon.setUrl(iconUri);
			selectedItemIcon.setVisible(true);
		}
	}

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		if (suggestionPopup.isAttached()) {
			switch (keyCode) {
			case KeyboardListener.KEY_DOWN:
				suggestionPopup.selectNextItem();
				break;
			case KeyboardListener.KEY_UP:
				suggestionPopup.selectPrevItem();
				break;
			case KeyboardListener.KEY_PAGEDOWN:
				if (hasNextPage())
					filterOptions(currentPage + 1, lastFilter);
				break;
			case KeyboardListener.KEY_PAGEUP:
				if (currentPage > 0)
					filterOptions(currentPage - 1, lastFilter);
				break;
			case KeyboardListener.KEY_ENTER:
			case KeyboardListener.KEY_TAB:
				suggestionPopup.menu.doSelectedItemAction();
				break;
			}
		}
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {

	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		switch (keyCode) {
		case KeyboardListener.KEY_ENTER:
		case KeyboardListener.KEY_TAB:
			; // NOP
			break;
		case KeyboardListener.KEY_DOWN:
		case KeyboardListener.KEY_UP:
		case KeyboardListener.KEY_PAGEDOWN:
		case KeyboardListener.KEY_PAGEUP:
			if (suggestionPopup.isAttached()) {
				break;
			} else {
				// open popup as from gadget
				filterOptions(0, "");
				tb.selectAll();
				break;
			}
		default:
			filterOptions(currentPage);
			break;
		}
	}

	/**
	 * Listener for popupopener
	 */
	public void onClick(Widget sender) {
		filterOptions(0, "");
		tb.setFocus(true);
		tb.selectAll();
	}

	/*
	 * Calculate minumum width for FilterSelect textarea
	 */
	private native int minWidth(String captions) /*-{
		if(!captions || captions.length <= 0)
			return 0;
		captions = captions.split("|");
		var d = $wnd.document.createElement("div");
		var html = "";
		for(var i=0; i < captions.length; i++) {
			html += "<div>" + captions[i] + "</div>";
			// TODO apply same CSS classname as in suggestionmenu
		}
		d.style.position = "absolute";
		d.style.top = "0";
		d.style.left = "0";
		d.style.visibility = "hidden";
		d.innerHTML = html;
		$wnd.document.body.appendChild(d);
		var w = d.offsetWidth;
		$wnd.document.body.removeChild(d);
		return w;
	}-*/;
}
