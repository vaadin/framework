/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.ITooltip;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

/**
 * 
 * TODO needs major refactoring (to be extensible etc)
 */
public class IFilterSelect extends Composite implements Paintable, Field,
        KeyboardListener, ClickListener, FocusListener, Focusable {

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
                sb.append("\" alt=\"\" class=\"i-icon\" />");
            }
            sb.append("<span>" + Util.escapeHTML(caption) + "</span>");
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

    public class SuggestionPopup extends IToolkitOverlay implements
            PositionCallback, PopupListener {

        private static final String Z_INDEX = "30000";

        private final SuggestionMenu menu;

        private final Element up = DOM.createDiv();
        private final Element down = DOM.createDiv();
        private final Element status = DOM.createDiv();

        private boolean isPagingEnabled = true;

        private long lastAutoClosed;

        private int popupOuterPadding = -1;

        private int topPosition;

        SuggestionPopup() {
            super(true, false, true);
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

        public void showSuggestions(
                Collection<FilterSelectSuggestion> currentSuggestions,
                int currentPage, int totalSuggestions) {

            // Add TT anchor point
            DOM.setElementProperty(getElement(), "id",
                    "TOOLKIT_COMBOBOX_OPTIONLIST");

            menu.setSuggestions(currentSuggestions);
            final int x = IFilterSelect.this.getAbsoluteLeft();
            topPosition = tb.getAbsoluteTop();
            topPosition += tb.getOffsetHeight();
            setPopupPosition(x, topPosition);

            final int first = currentPage * PAGELENTH
                    + (nullSelectionAllowed && currentPage > 0 ? 0 : 1);
            final int last = first + currentSuggestions.size() - 1;
            final int matches = totalSuggestions
                    - (nullSelectionAllowed ? 1 : 0);
            if (last > 0) {
                // nullsel not counted, as requested by user
                DOM.setInnerText(status, (matches == 0 ? 0 : first)
                        + "-"
                        + ("".equals(lastFilter) && nullSelectionAllowed
                                && currentPage == 0 ? last - 1 : last) + "/"
                        + matches);
            } else {
                DOM.setInnerText(status, "");
            }
            // We don't need to show arrows or statusbar if there is only one
            // page
            if (matches <= PAGELENTH) {
                setPagingEnabled(false);
            } else {
                setPagingEnabled(true);
            }
            setPrevButtonActive(first > 1);
            setNextButtonActive(last < matches);

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

            } else if (hasNextPage()) {
                lastIndex = index - 1; // save for paging
                filterOptions(currentPage + 1, lastFilter);
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
                    lastIndex = index + 1; // save for paging
                    filterOptions(currentPage - 1, lastFilter);
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

        @Override
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
                DOM.setStyleAttribute(down, "display", "");
                DOM.setStyleAttribute(up, "display", "");
                DOM.setStyleAttribute(status, "display", "");
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
         * @see
         * com.google.gwt.user.client.ui.PopupPanel$PositionCallback#setPosition
         * (int, int)
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

            final int desiredWidth = getMainWidth();
            int naturalMenuWidth = DOM.getElementPropertyInt(DOM
                    .getFirstChild(menu.getElement()), "offsetWidth");

            if (popupOuterPadding == -1) {
                popupOuterPadding = Util.measureHorizontalPaddingAndBorder(
                        getElement(), 2);
            }

            if (naturalMenuWidth < desiredWidth) {
                menu.setWidth((desiredWidth - popupOuterPadding) + "px");
                DOM.setStyleAttribute(DOM.getFirstChild(menu.getElement()),
                        "width", "100%");
                naturalMenuWidth = desiredWidth;
            }

            if (BrowserInfo.get().isIE()) {
                /*
                 * IE requires us to specify the width for the container
                 * element. Otherwise it will be 100% wide
                 */
                int rootWidth = naturalMenuWidth - popupOuterPadding;
                DOM.setStyleAttribute(getContainerElement(), "width", rootWidth
                        + "px");
            }

            if (offsetHeight + getPopupTop() > Window.getClientHeight()
                    + Window.getScrollTop()) {
                // popup on top of input instead
                top = getPopupTop() - offsetHeight
                        - IFilterSelect.this.getOffsetHeight();
                if (top < 0) {
                    top = 0;
                }
            } else {
                top = getPopupTop();
                /*
                 * Take popup top margin into account. getPopupTop() returns the
                 * top value including the margin but the value we give must not
                 * include the margin.
                 */
                int topMargin = (top - topPosition);
                top -= topMargin;
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

        /**
         * Updates style names in suggestion popup to help theme building.
         */
        public void updateStyleNames(UIDL uidl) {
            if (uidl.hasAttribute("style")) {
                setStyleName(CLASSNAME + "-suggestpopup");
                final String[] styles = uidl.getStringAttribute("style").split(
                        " ");
                for (int i = 0; i < styles.length; i++) {
                    addStyleDependentName(styles[i]);
                }
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
            if (currentSuggestions.size() > 0) {
                final int pixels = pagelenth * (getOffsetHeight() - 2)
                        / currentSuggestions.size();
                setHeight((pixels + 2) + "px");
            }
        }

        public void setSuggestions(
                Collection<FilterSelectSuggestion> suggestions) {
            clearItems();
            final Iterator<FilterSelectSuggestion> it = suggestions.iterator();
            while (it.hasNext()) {
                final FilterSelectSuggestion s = it.next();
                final MenuItem mi = new MenuItem(s.getDisplayString(), true, s);

                com.google.gwt.dom.client.Element child = mi.getElement()
                        .getFirstChildElement();
                while (child != null) {
                    if (child.getNodeName().toLowerCase().equals("img")) {
                        DOM
                                .sinkEvents((Element) child.cast(),
                                        (DOM.getEventsSunk((Element) child
                                                .cast()) | Event.ONLOAD));
                    }
                    child = child.getNextSiblingElement();
                }

                this.addItem(mi);
                if (s == currentSuggestion) {
                    selectItem(mi);
                }
            }
        }

        public void doSelectedItemAction() {
            final MenuItem item = getSelectedItem();
            final String enteredItemValue = tb.getText();
            // check for exact match in menu
            int p = getItems().size();
            if (p > 0) {
                for (int i = 0; i < p; i++) {
                    final MenuItem potentialExactMatch = (MenuItem) getItems()
                            .get(i);
                    if (potentialExactMatch.getText().equals(enteredItemValue)) {
                        selectItem(potentialExactMatch);
                        doItemAction(potentialExactMatch, true);
                        suggestionPopup.hide();
                        return;
                    }
                }
            }
            if (allowNewItem) {

                if (!prompting && !enteredItemValue.equals(lastNewItemString)) {
                    /*
                     * Store last sent new item string to avoid double sends
                     */
                    lastNewItemString = enteredItemValue;
                    client.updateVariable(paintableId, "newitem",
                            enteredItemValue, immediate);
                }
            } else if (item != null
                    && !"".equals(lastFilter)
                    && item.getText().toLowerCase().startsWith(
                            lastFilter.toLowerCase())) {
                doItemAction(item, true);
            } else {
                if (currentSuggestion != null) {
                    String text = currentSuggestion.getReplacementString();
                    /* TODO?
                    if (text.equals("")) {
                        addStyleDependentName(CLASSNAME_PROMPT);
                        tb.setText(inputPrompt);
                        prompting = true;
                    } else {
                        tb.setText(text);
                        prompting = false;
                        removeStyleDependentName(CLASSNAME_PROMPT);
                    }
                    */
                    selectedOptionKey = currentSuggestion.key;
                }
            }
            suggestionPopup.hide();
        }

        @Override
        public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONLOAD) {
                if (suggestionPopup.isVisible()) {
                    setWidth("");
                    DOM.setStyleAttribute(DOM.getFirstChild(getElement()),
                            "width", "");
                    suggestionPopup.setPopupPositionAndShow(suggestionPopup);
                }
            }
            super.onBrowserEvent(event);
        }
    }

    public static final int FILTERINGMODE_OFF = 0;
    public static final int FILTERINGMODE_STARTSWITH = 1;
    public static final int FILTERINGMODE_CONTAINS = 2;

    private static final String CLASSNAME = "i-filterselect";

    public static final int PAGELENTH = 10;

    private final FlowPanel panel = new FlowPanel();

    private final TextBox tb = new TextBox() {
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (client != null) {
                client.handleTooltipEvent(event, IFilterSelect.this);
            }
        }
    };

    private final SuggestionPopup suggestionPopup = new SuggestionPopup();

    private final HTML popupOpener = new HTML("");

    private final Image selectedItemIcon = new Image();

    private ApplicationConnection client;

    private String paintableId;

    private int currentPage;

    private final Collection<FilterSelectSuggestion> currentSuggestions = new ArrayList<FilterSelectSuggestion>();

    private boolean immediate;

    private String selectedOptionKey;

    private boolean filtering = false;

    private String lastFilter = "";
    private int lastIndex = -1; // last selected index when using arrows

    private FilterSelectSuggestion currentSuggestion;

    private int totalMatches;
    private boolean allowNewItem;
    private boolean nullSelectionAllowed;
    private boolean enabled;

    // shown in unfocused empty field, disappears on focus (e.g "Search here")
    private static final String CLASSNAME_PROMPT = "prompt";
    private static final String ATTR_INPUTPROMPT = "prompt";
    private String inputPrompt = "";
    private boolean prompting = false;

    // Set true when popupopened has been clicked. Cleared on each UIDL-update.
    // This handles the special case where are not filtering yet and the
    // selected value has changed on the server-side. See #2119
    private boolean popupOpenerClicked;
    private String width = null;
    private int textboxPadding = -1;
    private int componentPadding = -1;
    private int suggestionPopupMinWidth = 0;
    /*
     * Stores the last new item string to avoid double submissions. Cleared on
     * uidl updates
     */
    private String lastNewItemString;
    private boolean focused = false;

    public IFilterSelect() {
        selectedItemIcon.setVisible(false);
        selectedItemIcon.setStyleName("i-icon");
        selectedItemIcon.addLoadListener(new LoadListener() {
            public void onError(Widget sender) {
            }

            public void onLoad(Widget sender) {
                updateRootWidth();
                updateSelectedIconPosition();
            }
        });

        panel.add(selectedItemIcon);
        tb.sinkEvents(ITooltip.TOOLTIP_EVENTS);
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
        if (totalMatches > (currentPage + 1) * PAGELENTH) {
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
            if ("".equals(filter)) {
                // let server decide
                page = -1;
            } else {
                page = 0;
            }
        }

        filtering = true;
        client.updateVariable(paintableId, "filter", filter, false);
        client.updateVariable(paintableId, "page", page, true);
        lastFilter = filter;
        currentPage = page;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        paintableId = uidl.getId();
        this.client = client;

        boolean readonly = uidl.hasAttribute("readonly");
        boolean disabled = uidl.hasAttribute("disabled");

        if (disabled || readonly) {
            tb.setEnabled(false);
            enabled = false;
        } else {
            tb.setEnabled(true);
            enabled = true;
        }

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            tb.setTabIndex(uidl.getIntAttribute("tabindex"));
        }

        immediate = uidl.hasAttribute("immediate");

        nullSelectionAllowed = uidl.hasAttribute("nullselect");

        currentPage = uidl.getIntVariable("page");

        if (uidl.hasAttribute(ATTR_INPUTPROMPT)) {
            // input prompt changed from server
            inputPrompt = uidl.getStringAttribute(ATTR_INPUTPROMPT);
        } else {
            inputPrompt = "";
        }

        suggestionPopup.setPagingEnabled(true);
        suggestionPopup.updateStyleNames(uidl);

        allowNewItem = uidl.hasAttribute("allownewitem");
        lastNewItemString = null;

        currentSuggestions.clear();
        final UIDL options = uidl.getChildUIDL(0);
        totalMatches = uidl.getIntAttribute("totalMatches");

        String captions = inputPrompt;

        for (final Iterator i = options.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            final FilterSelectSuggestion suggestion = new FilterSelectSuggestion(
                    optionUidl);
            currentSuggestions.add(suggestion);
            if (optionUidl.hasAttribute("selected")) {
                if (!filtering || popupOpenerClicked) {
                    tb.setText(suggestion.getReplacementString());
                    selectedOptionKey = "" + suggestion.getOptionKey();
                }
                currentSuggestion = suggestion;
                setSelectedItemIcon(suggestion.getIconUri());
            }

            // Collect captions so we can calculate minimum width for textarea
            if (captions.length() > 0) {
                captions += "|";
            }
            captions += suggestion.getReplacementString();
        }

        if ((!filtering || popupOpenerClicked) && uidl.hasVariable("selected")
                && uidl.getStringArrayVariable("selected").length == 0) {
            // select nulled
            if (!filtering || !popupOpenerClicked) {
                setPromptingOn();
            }
            selectedOptionKey = null;
        }

        if (filtering
                && lastFilter.toLowerCase().equals(
                        uidl.getStringVariable("filter"))) {
            suggestionPopup.showSuggestions(currentSuggestions, currentPage,
                    totalMatches);
            filtering = false;
            if (!popupOpenerClicked && lastIndex != -1) {
                // we're paging w/ arrows
                if (lastIndex == 0) {
                    // going up, select last item
                    int lastItem = PAGELENTH - 1;
                    List items = suggestionPopup.menu.getItems();
                    /*
                     * The first page can contain less than 10 items if the null
                     * selection item is filtered away
                     */
                    if (lastItem >= items.size()) {
                        lastItem = items.size() - 1;
                    }
                    suggestionPopup.menu.selectItem((MenuItem) items
                            .get(lastItem));
                } else {
                    // going down, select first item
                    suggestionPopup.menu
                            .selectItem((MenuItem) suggestionPopup.menu
                                    .getItems().get(0));
                }
                lastIndex = -1; // reset
            }
        }

        // Calculate minumum textarea width
        suggestionPopupMinWidth = minWidth(captions);

        popupOpenerClicked = false;

        updateRootWidth();
    }

    private void setPromptingOn() {
        prompting = true;
        addStyleDependentName(CLASSNAME_PROMPT);
        tb.setText(inputPrompt);
    }

    private void setPromptingOff(String text) {
        tb.setText(text);
        prompting = false;
        removeStyleDependentName(CLASSNAME_PROMPT);
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

        String text = suggestion.getReplacementString();
        if ("".equals(newKey) && !focused) {
            setPromptingOn();
        } else {
            setPromptingOff(text);
        }
        setSelectedItemIcon(suggestion.getIconUri());
        if (!newKey.equals(selectedOptionKey)) {
            selectedOptionKey = newKey;
            client.updateVariable(paintableId, "selected",
                    new String[] { selectedOptionKey }, immediate);
            // currentPage = -1; // forget the page
        }
        suggestionPopup.hide();
    }

    private void setSelectedItemIcon(String iconUri) {
        if (iconUri == null) {
            selectedItemIcon.setVisible(false);
            updateRootWidth();
        } else {
            selectedItemIcon.setUrl(iconUri);
            selectedItemIcon.setVisible(true);
            updateRootWidth();
            updateSelectedIconPosition();
        }
    }

    private void updateSelectedIconPosition() {
        // Position icon vertically to middle
        int availableHeight = getOffsetHeight();
        int iconHeight = Util.getRequiredHeight(selectedItemIcon);
        int marginTop = (availableHeight - iconHeight) / 2;
        DOM.setStyleAttribute(selectedItemIcon.getElement(), "marginTop",
                marginTop + "px");
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
            case KeyboardListener.KEY_SHIFT:
            case KeyboardListener.KEY_CTRL:
            case KeyboardListener.KEY_ALT:
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
                    filterOptions(-1, "");
                    lastFilter = "";
                    tb.selectAll();
                    break;
                }
            case KeyboardListener.KEY_ESCAPE:
                if (currentSuggestion != null) {
                    String text = currentSuggestion.getReplacementString();
                    setPromptingOff(text);
                    selectedOptionKey = currentSuggestion.key;
                } else {
                    setPromptingOn();
                    selectedOptionKey = null;
                }
                lastFilter = "";
                suggestionPopup.hide();
                break;
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
            // Popup's auto close feature
            if (!suggestionPopup.isJustClosed()) {
                filterOptions(-1, "");
                popupOpenerClicked = true;
                lastFilter = "";
            } else if (selectedOptionKey == null) {
                tb.setText(inputPrompt);
                prompting = true;
            }
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            tb.setFocus(true);
            tb.selectAll();

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
        focused = true;
        if (prompting) {
            setPromptingOff("");
        }
        addStyleDependentName("focus");
    }

    public void onLostFocus(Widget sender) {
        focused = false;
        if (!suggestionPopup.isAttached() || suggestionPopup.isJustClosed()) {
            // typing so fast the popup was never opened, or it's just closed
            suggestionPopup.menu.doSelectedItemAction();
        }
        if (selectedOptionKey == null) {
            setPromptingOn();
        }
        removeStyleDependentName("focus");
    }

    public void focus() {
        focused = true;
        if (prompting) {
            setPromptingOff("");
        }
        tb.setFocus(true);
    }

    @Override
    public void setWidth(String width) {
        if (width == null || width.equals("")) {
            this.width = null;
        } else {
            this.width = width;
        }
        Util.setWidthExcludingPaddingAndBorder(this, width, 4);
        updateRootWidth();
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        Util.setHeightExcludingPaddingAndBorder(tb, height, 3);
    }

    private void updateRootWidth() {
        if (width == null) {
            /*
             * When the width is not specified we must specify width for root
             * div so the popupopener won't wrap to the next line and also so
             * the size of the combobox won't change over time.
             */
            int tbWidth = Util.getRequiredWidth(tb);
            int openerWidth = Util.getRequiredWidth(popupOpener);
            int iconWidth = Util.getRequiredWidth(selectedItemIcon);

            int w = tbWidth + openerWidth + iconWidth;
            if (suggestionPopupMinWidth > w) {
                setTextboxWidth(suggestionPopupMinWidth);
                w = suggestionPopupMinWidth;
            } else {
                /*
                 * Firefox3 has its own way of doing rendering so we need to
                 * specify the width for the TextField to make sure it actually
                 * is rendered as wide as FF3 says it is
                 */
                tb.setWidth((tbWidth - getTextboxPadding()) + "px");
            }
            super.setWidth((w) + "px");
            // Freeze the initial width, so that it won't change even if the
            // icon size changes
            width = w + "px";

        } else {
            /*
             * When the width is specified we also want to explicitly specify
             * widths for textbox and popupopener
             */
            setTextboxWidth(getMainWidth() - getComponentPadding());

        }
    }

    private int getMainWidth() {
        int componentWidth;
        if (BrowserInfo.get().isIE6()) {
            // Required in IE when textfield is wider than this.width
            DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            componentWidth = getOffsetWidth();
            DOM.setStyleAttribute(getElement(), "overflow", "");
        } else {
            componentWidth = getOffsetWidth();
        }
        return componentWidth;
    }

    private void setTextboxWidth(int componentWidth) {
        int padding = getTextboxPadding();
        int popupOpenerWidth = Util.getRequiredWidth(popupOpener);
        int iconWidth = Util.getRequiredWidth(selectedItemIcon);
        int textboxWidth = componentWidth - padding - popupOpenerWidth
                - iconWidth;
        if (textboxWidth < 0) {
            textboxWidth = 0;
        }
        tb.setWidth(textboxWidth + "px");
    }

    private int getTextboxPadding() {
        if (textboxPadding < 0) {
            textboxPadding = Util.measureHorizontalPaddingAndBorder(tb
                    .getElement(), 4);
        }
        return textboxPadding;
    }

    private int getComponentPadding() {
        if (componentPadding < 0) {
            componentPadding = Util.measureHorizontalPaddingAndBorder(
                    getElement(), 3);
        }
        return componentPadding;
    }
}
