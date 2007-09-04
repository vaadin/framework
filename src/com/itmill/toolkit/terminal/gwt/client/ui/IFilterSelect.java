package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IFilterSelect extends Composite implements Paintable, KeyboardListener, ClickListener {
	
	public class FilterSelectSuggestion implements Suggestion, Command {
		
		private String key;
		private String caption;

		public FilterSelectSuggestion(UIDL uidl) {
			this.key = uidl.getStringAttribute("key");
			this.caption = uidl.getStringAttribute("caption");
		}

		public String getDisplayString() {
			return caption;
		}

		public String getReplacementString() {
			return caption;
		}

		public int getOptionKey() {
			return Integer.parseInt(key);
		}
		
		public void execute() {
			IFilterSelect.this.onSuggestionSelected(this);
		}
	}

	public class SuggestionPopup extends PopupPanel {
		private SuggestionMenu menu;
		
		private Element up = DOM.createDiv();
		private Element down = DOM.createDiv();
		private Element status = DOM.createDiv();

		SuggestionPopup() {
			super(true);
			this.menu = new SuggestionMenu();
			setWidget(menu);
			setStyleName(CLASSNAME + "-suggestpopup");
			
			Element root = getElement();
			
			
			DOM.setInnerText(up, "prev");
			DOM.sinkEvents(up, Event.ONCLICK);
			DOM.setInnerText(down, "next");
			DOM.sinkEvents(down, Event.ONCLICK);
			DOM.insertChild(root, up, 0);
			DOM.appendChild(root, down);
			DOM.appendChild(root, status);
			DOM.setElementProperty(status, "className", CLASSNAME + "-status");
		}
		
		public void showSuggestions(Collection currentSuggestions, int currentPage, int totalSuggestions) {
			menu.setSuggestions(currentSuggestions);
			int x = IFilterSelect.this.tb.getAbsoluteLeft();
			int y = IFilterSelect.this.tb.getAbsoluteTop();
			y += IFilterSelect.this.tb.getOffsetHeight();
			this.setPopupPosition(x, y);
			int first = currentPage*PAGELENTH + 1;
			int last = first + currentSuggestions.size() - 1 ;
			DOM.setInnerText(status, first + "-" + last + "/" + totalSuggestions);
			setPrevButtonActive(first > 1);
			setNextButtonActive(last < totalSuggestions);
			
			show();
		}
		
		private void setNextButtonActive(boolean b) {
			if(b) {
				DOM.sinkEvents(down, Event.ONCLICK);
				DOM.setElementProperty(down, "className", CLASSNAME + "-nextpage-on");
			} else {
				DOM.sinkEvents(down, 0);
				DOM.setElementProperty(down, "className", CLASSNAME + "-nextpage-off");
			}
		}

		private void setPrevButtonActive(boolean b) {
			if(b) {
				DOM.sinkEvents(up, Event.ONCLICK);
				DOM.setElementProperty(up, "className", CLASSNAME + "-prevpage-on");
			} else {
				DOM.sinkEvents(up, 0);
				DOM.setElementProperty(up, "className", CLASSNAME + "-prevpage-off");
			}
				
		}

		public void selectNextItem() {
			MenuItem cur = menu.getSelectedItem();
			int index = 1 + menu.getItems().indexOf(cur);
			if(menu.getItems().size() > index)
				menu.selectItem((MenuItem) menu.getItems().get(index));
			else
				filterOptions(currentPage + 1);
		}
		
		public void selectPrevItem() {
			MenuItem cur = menu.getSelectedItem();
			int index = -1 + menu.getItems().indexOf(cur);
			if(index > -1)
				menu.selectItem((MenuItem) menu.getItems().get(index));
			else if (index == -1) {
				filterOptions(currentPage - 1);
			} else {
				menu.selectItem((MenuItem) menu.getItems().get(menu.getItems().size()-1));
			}
		}
		
		public void onBrowserEvent(Event event) {
			Element target = DOM.eventGetTarget(event);
			if(DOM.compare(target, up)) {
				filterOptions(currentPage - 1);
			} else if (DOM.compare(target, down)) {
				filterOptions(currentPage + 1);
			}
			tb.setFocus(true);
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
			while(it.hasNext()) {
				FilterSelectSuggestion s = (FilterSelectSuggestion) it.next();
				MenuItem mi = new MenuItem(
						s.getDisplayString(),
						true,
						s);
				this.addItem(mi);
				if(s == currentSuggestion)
					selectItem(mi);
			}
		}

		public void doSelectedItemAction() {
			MenuItem item = this.getSelectedItem();
			if(item != null) {
				doItemAction(item, true);
			}
			else {
				suggestionPopup.hide();
			}
		}
		
	}

	private static final String CLASSNAME = "i-filterselect";
	
	public static final int PAGELENTH = 20;

	private final FlowPanel panel = new FlowPanel();
	
	private final TextBox tb = new TextBox();
	
	private final SuggestionPopup suggestionPopup = new SuggestionPopup();
	
	private final HTML popupOpener = new HTML("v");
	
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
	
	public IFilterSelect() {
		panel.add(tb);
		panel.add(popupOpener);
		initWidget(panel);
		setStyleName(CLASSNAME);
		tb.addKeyboardListener(this);
		popupOpener.setStyleName(CLASSNAME + "-popupopener");
		popupOpener.addClickListener(this);
	}

	public void filterOptions(int page) {
		String filter = tb.getText();
		if (filter.equals(lastFilter) && currentPage == page) {
			return;
		}
		if(!filter.equals(lastFilter)) {
			// we are on subsequant page and text has changed -> reset page
			page = 0;
		}
		filtering  = true;
		client.updateVariable(paintableId, "filter", filter, false);
		client.updateVariable(paintableId, "page", page, true);
		lastFilter = filter;
		currentPage = page;
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.paintableId = uidl.getId();
		this.client = client;
		
		if(uidl.hasAttribute("immediate"))
			immediate = true;
		else 
			immediate = false;
		
		currentSuggestions.clear();
		UIDL options = uidl.getChildUIDL(0);
		totalSuggestions = options.getIntAttribute("totalMatches");
		for(Iterator i = options.getChildIterator(); i.hasNext();) {
			UIDL optionUidl = (UIDL) i.next();
			FilterSelectSuggestion suggestion = new FilterSelectSuggestion(optionUidl);
			currentSuggestions.add(suggestion);
		}
		if(filtering && lastFilter.equals(uidl.getStringVariable("filter"))) {
			suggestionPopup.showSuggestions(
					currentSuggestions, 
					currentPage, 
					totalSuggestions);
			filtering = false;
		}
	}
	
	public void onSuggestionSelected(FilterSelectSuggestion suggestion) {
			currentSuggestion = suggestion;
			String newKey = String.valueOf(suggestion.getOptionKey());
			tb.setText(suggestion.getReplacementString());
			if(newKey.equals(selectedOptionKey))
				return;
			selectedOptionKey = newKey;
			client.updateVariable(
					paintableId, 
					"selected", 
					new String[] {selectedOptionKey} , 
					immediate);
			lastFilter = tb.getText();
			suggestionPopup.hide();
		}
	
	public void onBrowserEvent(Event event) {
		client.console.log("pöö");
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
            	  if(totalSuggestions > currentPage*(PAGELENTH+1))
            		  filterOptions(currentPage + 1);
            	  break;
              case KeyboardListener.KEY_PAGEUP:
            	  if(currentPage > 0)
            		  filterOptions(currentPage - 1);
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
		filterOptions(currentPage);
	}

	/**
	 * Listener for popupopener
	 */
	public void onClick(Widget sender) {
		filterOptions(0);
		this.suggestionPopup.showSuggestions(currentSuggestions, currentPage, totalSuggestions);
		tb.setFocus(true);
		tb.selectAll();
	}
}
