/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupListener;
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
 * TODO needs major refactoring (to be extensible etc)
 */
public class IFilterSelect extends Composite implements Paintable,
        KeyboardListener, ClickListener, FocusListener {

    public class FilterSelectSuggestion implements Suggestion, Command {

        private final String key;
        private final String caption;
        private String iconUri;

        public FilterSelectSuggestion(UIDL uidl) {
            key = uidl.getStringAttribute("key");
            caption = uidl.getStringAttribute("caption");
            if (uidl.hasAttribute("icon")) {
                iconUri = client.translateToolkitUri(uidl
                        .getStringAttribute("icon"));
            }
        }

        public String getDisplayString() {
            final StringBuffer sb = new StringBuffer();
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
            onSuggestionSelected(this);
        }
    }

    /**
     * @author mattitahvonen
     * 
     */
    public class SuggestionPopup extends ToolkitOverlay implements
            PositionCallback, PopupListener {
        private static final int EXTRASPACE = 8;

        private static final String Z_INDEX = "30000";

        private final SuggestionMenu menu;

        private final Element up = DOM.createDiv();
        private final Element down = DOM.createDiv();
        private final Element status = DOM.createDiv();

        private boolean isPagingEnabled = true;

        private long lastAutoClosed;

        SuggestionPopup() {
            super(true);
            menu = new SuggestionMenu();
            setWidget(menu);
            setStyleName(CLASSNAME + "-suggestpopup");
            DOM.setStyleAttribute(getElement(), "zIndex", Z_INDEX);

            final Element root = getContainerElement();

            DOM.setInnerHTML(up, "<span>Prev</span>");
            DOM.sinkEvents(up, Event.ONCLICK);
            DOM.setInnerHTML(down, "<span>Next</span>");
            DOM.sinkEvents(down, Event.ONCLICK);
            DOM.insertChild(root, up, 0);
            DOM.appendChild(root, down);
            DOM.appendChild(root, status);
            DOM.setElementProperty(status, "className", CLASSNAME + "-status");

            addPopupListener(this);
        }

        public void showSuggestions(Collection currentSuggestions,
                int currentPage, int totalSuggestions) {
            menu.setSuggestions(currentSuggestions);
            final int x = IFilterSelect.this.getAbsoluteLeft();
            int y = tb.getAbsoluteTop();
            y += tb.getOffsetHeight();
            setPopupPosition(x, y);
            final int first = currentPage * PAGELENTH + 1;
            final int last = first + currentSuggestions.size() - 1;
            DOM.setInnerText(status, (totalSuggestions == 0 ? 0 : first) + "-"
                    + last + "/" + totalSuggestions);
            setPrevButtonActive(first > 1);
            setNextButtonActive(last < totalSuggestions);

            // clear previously fixed width
            menu.setWidth("");
            DOM.setStyleAttribute(DOM.getFirstChild(menu.getElement()),
                    "width", "");

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
            final MenuItem cur = menu.getSelectedItem();
            final int index = 1 + menu.getItems().indexOf(cur);
            if (menu.getItems().size() > index) {
                final MenuItem newSelectedItem = (MenuItem) menu.getItems()
                        .get(index);
                menu.selectItem(newSelectedItem);
                tb.setText(newSelectedItem.getText());
                tb.setSelectionRange(lastFilter.length(), newSelectedItem
                        .getText().length()
                        - lastFilter.length());

            } else if (!clientSideFiltering && hasNextPage()) {
                filterOptions(currentPage + 1);
            }
        }

        public void selectPrevItem() {
            final MenuItem cur = menu.getSelectedItem();
            final int index = -1 + menu.getItems().indexOf(cur);
            if (index > -1) {
                final MenuItem newSelectedItem = (MenuItem) menu.getItems()
                        .get(index);
                menu.selectItem(newSelectedItem);
                tb.setText(newSelectedItem.getText());
                tb.setSelectionRange(lastFilter.length(), newSelectedItem
                        .getText().length()
                        - lastFilter.length());
            } else if (index == -1) {
                if (currentPage > 0) {
                    filterOptions(currentPage - 1);
                }
            } else {
                final MenuItem newSelectedItem = (MenuItem) menu.getItems()
                        .get(menu.getItems().size() - 1);
                menu.selectItem(newSelectedItem);
                tb.setText(newSelectedItem.getText());
                tb.setSelectionRange(lastFilter.length(), newSelectedItem
                        .getText().length()
                        - lastFilter.length());
            }
        }

        public void onBrowserEvent(Event event) {
            final Element target = DOM.eventGetTarget(event);
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
            if (isPagingEnabled == paging) {
                return;
            }
            if (paging) {
                DOM.setStyleAttribute(down, "display", "block");
                DOM.setStyleAttribute(up, "display", "block");
                DOM.setStyleAttribute(status, "display", "block");
            } else {
                DOM.setStyleAttribute(down, "display", "none");
                DOM.setStyleAttribute(up, "display", "none");
                DOM.setStyleAttribute(status, "display", "none");
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

            int top = -1;
            int left = -1;

            // reset menu size and retrieve its "natural" size
            menu.setHeight("");
            if (currentPage > 0) {
                // fix height to avoid height change when getting to last page
                menu.fixHeightTo(PAGELENTH);
            }
            offsetHeight = getOffsetHeight();

            final int desiredWidth = IFilterSelect.this.getOffsetWidth();
            int naturalMenuWidth = DOM.getElementPropertyInt(DOM
                    .getFirstChild(menu.getElement()), "offsetWidth");
            if (naturalMenuWidth < desiredWidth) {
                menu.setWidth(desiredWidth + "px");
                DOM.setStyleAttribute(DOM.getFirstChild(menu.getElement()),
                        "width", "100%");
                naturalMenuWidth = desiredWidth;
            }
            if (Util.isIE()) {
                DOM.setStyleAttribute(getElement(), "width", naturalMenuWidth
                        + "px");
            }

            if (offsetHeight + getPopupTop() > Window.getClientHeight()
                    + Window.getScrollTop()) {
                top = Window.getClientHeight() + Window.getScrollTop()
                        - offsetHeight - EXTRASPACE
                        - IFilterSelect.this.getOffsetHeight();
                if (top < 0) {
                    top = 0;
                }
            } else {
                top = getPopupTop();
            }

            // fetch real width (mac FF bugs here due GWT popups overflow:auto )
            offsetWidth = DOM.getElementPropertyInt(DOM.getFirstChild(menu
                    .getElement()), "offsetWidth");
            if (offsetWidth + getPopupLeft() > Window.getClientWidth()
                    + Window.getScrollLeft()) {
                left = IFilterSelect.this.getAbsoluteLeft()
                        + IFilterSelect.this.getOffsetWidth()
                        + Window.getScrollLeft() - offsetWidth;
                if (left < 0) {
                    left = 0;
                }
            } else {
                left = getPopupLeft();
            }
            setPopupPosition(left, top);

        }

        /**
         * @return true if popup was just closed
         */
        public boolean isJustClosed() {
            final long now = (new Date()).getTime();
            return (lastAutoClosed > 0 && (now - lastAutoClosed) < 200);
        }

        public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
            if (autoClosed) {
                lastAutoClosed = (new Date()).getTime();
            }
        }

    }

    public class SuggestionMenu extends MenuBar {

        SuggestionMenu() {
            super(true);
            setStyleName(CLASSNAME + "-suggestmenu");
        }

        /**
         * Fixes menus height to use same space as full page would use. Needed
         * to avoid height changes when quickly "scrolling" to last page
         */
        public void fixHeightTo(int pagelenth) {
            final int pixels = pagelenth * (getOffsetHeight() - 2)
                    / currentSuggestions.size();
            setHeight((pixels + 2) + "px");
        }

        public void setSuggestions(Collection suggestions) {
            clearItems();
            final Iterator it = suggestions.iterator();
            while (it.hasNext()) {
                final FilterSelectSuggestion s = (FilterSelectSuggestion) it
                        .next();
                final MenuItem mi = new MenuItem(s.getDisplayString(), true, s);
                this.addItem(mi);
                if (s == currentSuggestion) {
                    selectItem(mi);
                }
            }
        }

        public void doSelectedItemAction() {
            final MenuItem item = getSelectedItem();
            if (allowNewItem) {
                final String newItemValue = tb.getText();
                // check for exact match in menu
                int p = getItems().size();
                if (p > 0) {
                    for (int i = 0; i < p; i++) {
                        final MenuItem potentialExactMatch = (MenuItem) getItems()
                                .get(i);
                        if (potentialExactMatch.getText().equals(newItemValue)) {
                            selectItem(potentialExactMatch);
                            doItemAction(potentialExactMatch, true);
                            suggestionPopup.hide();
                            return;
                        }
                    }
                }

                if (!newItemValue.equals("")) {
                    client.updateVariable(paintableId, "newitem", newItemValue,
                            true);
                }
            } else if (item != null
                    && item.getText().toLowerCase().startsWith(
                            lastFilter.toLowerCase())) {
                doItemAction(item, true);
            }
            suggestionPopup.hide();
        }

    }

    public static final int FILTERINGMODE_OFF = 0;
    public static final int FILTERINGMODE_STARTSWITH = 1;
    public static final int FILTERINGMODE_CONTAINS = 2;

    private static final String CLASSNAME = "i-filterselect";

    public static final int PAGELENTH = 10;

    private final FlowPanel panel = new FlowPanel();

    private final TextBox tb = new TextBox();

    private final SuggestionPopup suggestionPopup = new SuggestionPopup();

    private final HTML popupOpener = new HTML("");

    private final Image selectedItemIcon = new Image();

    private ApplicationConnection client;

    private String paintableId;

    private int currentPage;

    private final Collection currentSuggestions = new ArrayList();

    private boolean immediate;

    private String selectedOptionKey;

    private boolean filtering = false;

    private String lastFilter = "";

    private int totalSuggestions;

    private FilterSelectSuggestion currentSuggestion;

    private boolean clientSideFiltering;

    private ArrayList allSuggestions;
    private int totalMatches;
    private boolean allowNewItem;
    private boolean nullSelectionAllowed;
    private boolean enabled;

    public IFilterSelect() {
        selectedItemIcon.setVisible(false);
        panel.add(selectedItemIcon);
        panel.add(tb);
        panel.add(popupOpener);
        initWidget(panel);
        setStyleName(CLASSNAME);
        tb.addKeyboardListener(this);
        tb.setStyleName(CLASSNAME + "-input");
        tb.addFocusListener(this);
        popupOpener.setStyleName(CLASSNAME + "-button");
        popupOpener.addClickListener(this);
    }

    public boolean hasNextPage() {
        if (totalSuggestions > (currentPage + 1) * PAGELENTH) {
            return true;
        } else {
            return false;
        }
    }

    public void filterOptions(int page) {
        filterOptions(page, tb.getText());
    }

    public void filterOptions(int page, String filter) {
        if (filter.equals(lastFilter) && currentPage == page) {
            if (!suggestionPopup.isAttached()) {
                suggestionPopup.showSuggestions(currentSuggestions,
                        currentPage, totalMatches);
            }
            return;
        }
        if (!filter.equals(lastFilter)) {
            // we are on subsequent page and text has changed -> reset page
            page = 0;
        }
        if (clientSideFiltering) {
            currentSuggestions.clear();
            for (final Iterator it = allSuggestions.iterator(); it.hasNext();) {
                final FilterSelectSuggestion s = (FilterSelectSuggestion) it
                        .next();
                final String string = s.getDisplayString().toLowerCase();
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
        paintableId = uidl.getId();
        this.client = client;

        if (uidl.hasAttribute("disabled") || uidl.hasAttribute("readonly")) {
            tb.setEnabled(false);
            enabled = false;
        } else {
            tb.setEnabled(true);
            enabled = true;
        }

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        immediate = uidl.hasAttribute("immediate");

        nullSelectionAllowed = uidl.hasAttribute("nullselect");

        if (true) {
            suggestionPopup.setPagingEnabled(true);
            clientSideFiltering = false;
        } else {
            suggestionPopup.setPagingEnabled(false);
            clientSideFiltering = true;
        }

        allowNewItem = uidl.hasAttribute("allownewitem");

        currentSuggestions.clear();
        final UIDL options = uidl.getChildUIDL(0);
        totalSuggestions = uidl.getIntAttribute("totalitems");
        totalMatches = uidl.getIntAttribute("totalMatches");

        String captions = "";
        if (clientSideFiltering) {
            allSuggestions = new ArrayList();
        }
        for (final Iterator i = options.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            final FilterSelectSuggestion suggestion = new FilterSelectSuggestion(
                    optionUidl);
            currentSuggestions.add(suggestion);
            if (clientSideFiltering) {
                allSuggestions.add(suggestion);
            }
            if (!filtering && optionUidl.hasAttribute("selected")) {
                tb.setText(suggestion.getReplacementString());
                currentSuggestion = suggestion;
            }

            // Collect captions so we can calculate minimum width for textarea
            if (captions.length() > 0) {
                captions += "|";
            }
            captions += suggestion.getReplacementString();
        }

        if (filtering
                && lastFilter.toLowerCase().equals(
                        uidl.getStringVariable("filter"))) {
            suggestionPopup.showSuggestions(currentSuggestions, currentPage,
                    totalMatches);
            filtering = false;
        }

        // Calculate minumum textarea width
        final int minw = minWidth(captions);
        final Element spacer = DOM.createDiv();
        DOM.setStyleAttribute(spacer, "width", minw + "px");
        DOM.setStyleAttribute(spacer, "height", "0");
        DOM.setStyleAttribute(spacer, "overflow", "hidden");
        DOM.appendChild(panel.getElement(), spacer);

        // Set columns (width) is given
        if (uidl.hasAttribute("cols")) {
            DOM.setStyleAttribute(getElement(), "width", uidl
                    .getIntAttribute("cols")
                    + "em");
        }

    }

    public void onSuggestionSelected(FilterSelectSuggestion suggestion) {
        currentSuggestion = suggestion;
        String newKey;
        if (suggestion.key.equals("")) {
            // "nullselection"
            newKey = "";
        } else {
            // normal selection
            newKey = String.valueOf(suggestion.getOptionKey());
        }
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
        if (enabled && suggestionPopup.isAttached()) {
            switch (keyCode) {
            case KeyboardListener.KEY_DOWN:
                suggestionPopup.selectNextItem();
                DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
                break;
            case KeyboardListener.KEY_UP:
                suggestionPopup.selectPrevItem();
                DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
                break;
            case KeyboardListener.KEY_PAGEDOWN:
                if (hasNextPage()) {
                    filterOptions(currentPage + 1, lastFilter);
                }
                break;
            case KeyboardListener.KEY_PAGEUP:
                if (currentPage > 0) {
                    filterOptions(currentPage - 1, lastFilter);
                }
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
        if (enabled) {
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
    }

    /**
     * Listener for popupopener
     */
    public void onClick(Widget sender) {
        if (enabled) {
            // ask suggestionPopup if it was just closed, we are using GWT
            // Popup's
            // auto close feature
            if (!suggestionPopup.isJustClosed()) {
                filterOptions(0, "");
            }
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            tb.selectAll();
            tb.setFocus(true);

        }
    }

    /*
     * Calculate minumum width for FilterSelect textarea
     */
    private native int minWidth(String captions)
    /*-{
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

    public void onFocus(Widget sender) {
        // NOP
    }

    public void onLostFocus(Widget sender) {
        if (currentSuggestion == null
                || !tb.getText().equals(
                        currentSuggestion.getReplacementString())) {
            if (currentSuggestion != null) {
                tb.setText(currentSuggestion.getDisplayString());
            } else {
                tb.setText("");
            }
        }
    }
}
