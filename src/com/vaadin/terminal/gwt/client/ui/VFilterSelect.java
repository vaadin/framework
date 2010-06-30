/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

/**
 * Client side implementation of the Select component.
 * 
 * TODO needs major refactoring (to be extensible etc)
 */
public class VFilterSelect extends Composite implements Paintable, Field,
        KeyDownHandler, KeyUpHandler, ClickHandler, FocusHandler, BlurHandler,
        Focusable {

    /**
     * Represents a suggestion in the suggestion popup box
     */
    public class FilterSelectSuggestion implements Suggestion, Command {

        private final String key;
        private final String caption;
        private String iconUri;

        /**
         * Constructor
         * 
         * @param uidl
         *            The UIDL recieved from the server
         */
        public FilterSelectSuggestion(UIDL uidl) {
            key = uidl.getStringAttribute("key");
            caption = uidl.getStringAttribute("caption");
            if (uidl.hasAttribute("icon")) {
                iconUri = client.translateVaadinUri(uidl
                        .getStringAttribute("icon"));
            }
        }

        /**
         * Gets the visible row in the popup as a HTML string. The string
         * contains an image tag with the rows icon (if an icon has been
         * specified) and the caption of the item
         */
        public String getDisplayString() {
            final StringBuffer sb = new StringBuffer();
            if (iconUri != null) {
                sb.append("<img src=\"");
                sb.append(iconUri);
                sb.append("\" alt=\"\" class=\"v-icon\" />");
            }
            sb.append("<span>" + Util.escapeHTML(caption) + "</span>");
            return sb.toString();
        }

        /**
         * Get a string that represents this item. This is used in the text box.
         */
        public String getReplacementString() {
            return caption;
        }

        /**
         * Get the option key which represents the item on the server side.
         * 
         * @return The key of the item
         */
        public int getOptionKey() {
            return Integer.parseInt(key);
        }

        /**
         * Get the URI of the icon. Used when constructing the displayed option.
         * 
         * @return
         */
        public String getIconUri() {
            return iconUri;
        }

        /**
         * Executes a selection of this item.
         */
        public void execute() {
            onSuggestionSelected(this);
        }
    }

    /**
     * Represents the popup box with the selection options. Wraps a suggestion
     * menu.
     */
    public class SuggestionPopup extends VOverlay implements PositionCallback,
            CloseHandler<PopupPanel> {

        private static final String Z_INDEX = "30000";

        private final SuggestionMenu menu;

        private final Element up = DOM.createDiv();
        private final Element down = DOM.createDiv();
        private final Element status = DOM.createDiv();

        private boolean isPagingEnabled = true;

        private long lastAutoClosed;

        private int popupOuterPadding = -1;

        private int topPosition;

        /**
         * Default constructor
         */
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

            addCloseHandler(this);
        }

        /**
         * Shows the popup where the user can see the filtered options
         * 
         * @param currentSuggestions
         *            The filtered suggestions
         * @param currentPage
         *            The current page number
         * @param totalSuggestions
         *            The total amount of suggestions
         */
        public void showSuggestions(
                Collection<FilterSelectSuggestion> currentSuggestions,
                int currentPage, int totalSuggestions) {

            // Add TT anchor point
            DOM.setElementProperty(getElement(), "id",
                    "VAADIN_COMBOBOX_OPTIONLIST");

            menu.setSuggestions(currentSuggestions);
            final int x = VFilterSelect.this.getAbsoluteLeft();
            topPosition = tb.getAbsoluteTop();
            topPosition += tb.getOffsetHeight();
            setPopupPosition(x, topPosition);

            int nullOffset = (nullSelectionAllowed && "".equals(lastFilter) ? 1
                    : 0);
            boolean firstPage = (currentPage == 0);
            final int first = currentPage * pageLength + 1
                    - (firstPage ? 0 : nullOffset);
            final int last = first + currentSuggestions.size() - 1
                    - (firstPage && "".equals(lastFilter) ? nullOffset : 0);
            final int matches = totalSuggestions - nullOffset;
            if (last > 0) {
                // nullsel not counted, as requested by user
                DOM.setInnerText(status, (matches == 0 ? 0 : first) + "-"
                        + last + "/" + matches);
            } else {
                DOM.setInnerText(status, "");
            }
            // We don't need to show arrows or statusbar if there is only one
            // page
            if (totalSuggestions <= pageLength || pageLength == 0) {
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

        /**
         * Should the next page button be visible to the user?
         * 
         * @param active
         */
        private void setNextButtonActive(boolean active) {
            if (active) {
                DOM.sinkEvents(down, Event.ONCLICK);
                DOM.setElementProperty(down, "className", CLASSNAME
                        + "-nextpage");
            } else {
                DOM.sinkEvents(down, 0);
                DOM.setElementProperty(down, "className", CLASSNAME
                        + "-nextpage-off");
            }
        }

        /**
         * Should the previous page button be visible to the user
         * 
         * @param active
         */
        private void setPrevButtonActive(boolean active) {
            if (active) {
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

        /**
         * Selects the next item in the filtered selections
         */
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

        /**
         * Selects the previous item in the filtered selections
         */
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

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
         * .user.client.Event)
         */
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

        /**
         * Should paging be enabled. If paging is enabled then only a certain
         * amount of items are visible at a time and a scrollbar or buttons are
         * visible to change page. If paging is turned of then all options are
         * rendered into the popup menu.
         * 
         * @param paging
         *            Should the paging be turned on?
         */
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
                menu.fixHeightTo(pageLength);
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
                        - VFilterSelect.this.getOffsetHeight();
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
                left = VFilterSelect.this.getAbsoluteLeft()
                        + VFilterSelect.this.getOffsetWidth()
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
         * Was the popup just closed?
         * 
         * @return true if popup was just closed
         */
        public boolean isJustClosed() {
            final long now = (new Date()).getTime();
            return (lastAutoClosed > 0 && (now - lastAutoClosed) < 200);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.logical.shared.CloseHandler#onClose(com.google
         * .gwt.event.logical.shared.CloseEvent)
         */
        public void onClose(CloseEvent<PopupPanel> event) {
            if (event.isAutoClosed()) {
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

    /**
     * The menu where the suggestions are rendered
     */
    public class SuggestionMenu extends MenuBar implements SubPartAware {

        /**
         * Default constructor
         */
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

        /**
         * Sets the suggestions rendered in the menu
         * 
         * @param suggestions
         *            The suggestions to be rendered in the menu
         */
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
                        client.addPngFix((Element) child.cast());
                    }
                    child = child.getNextSiblingElement();
                }

                this.addItem(mi);
                if (s == currentSuggestion) {
                    selectItem(mi);
                }
            }
        }

        /**
         * Send the current selection to the server. Triggered when a selection
         * is made or on a blur event.
         */
        public void doSelectedItemAction() {
            // do not send a value change event if null was and stays selected
            final String enteredItemValue = tb.getText();
            if (nullSelectionAllowed && "".equals(enteredItemValue)
                    && selectedOptionKey != null
                    && !"".equals(selectedOptionKey)) {
                if (nullSelectItem) {
                    reset();
                    return;
                }
                // null is not visible on pages != 0, and not visible when
                // filtering: handle separately
                client.updateVariable(paintableId, "filter", "", false);
                client.updateVariable(paintableId, "page", 0, false);
                client.updateVariable(paintableId, "selected", new String[] {},
                        immediate);
                suggestionPopup.hide();
                return;
            }

            selecting = filtering;
            if (!filtering) {
                doPostFilterSelectedItemAction();
            }
        }

        /**
         * Triggered after a selection has been made
         */
        public void doPostFilterSelectedItemAction() {
            final MenuItem item = getSelectedItem();
            final String enteredItemValue = tb.getText();

            selecting = false;

            // check for exact match in menu
            int p = getItems().size();
            if (p > 0) {
                for (int i = 0; i < p; i++) {
                    final MenuItem potentialExactMatch = (MenuItem) getItems()
                            .get(i);
                    if (potentialExactMatch.getText().equals(enteredItemValue)) {
                        selectItem(potentialExactMatch);
                        // do not send a value change event if null was and
                        // stays selected
                        if (!"".equals(enteredItemValue)
                                || (selectedOptionKey != null && !""
                                        .equals(selectedOptionKey))) {
                            doItemAction(potentialExactMatch, true);
                        }
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
                    && (filteringmode == FILTERINGMODE_CONTAINS ? item
                            .getText().toLowerCase().contains(
                                    lastFilter.toLowerCase()) : item.getText()
                            .toLowerCase().startsWith(lastFilter.toLowerCase()))) {
                doItemAction(item, true);
            } else {
                // currentSuggestion has key="" for nullselection
                if (currentSuggestion != null
                        && !currentSuggestion.key.equals("")) {
                    // An item (not null) selected
                    String text = currentSuggestion.getReplacementString();
                    tb.setText(text);
                    selectedOptionKey = currentSuggestion.key;
                } else {
                    // Null selected
                    tb.setText("");
                    selectedOptionKey = null;
                }
            }
            suggestionPopup.hide();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.terminal.gwt.client.ui.MenuBar#onBrowserEvent(com.google
         * .gwt.user.client.Event)
         */
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

        private static final String SUBPART_PREFIX = "item";

        public Element getSubPartElement(String subPart) {
            int index = Integer.parseInt(subPart.substring(SUBPART_PREFIX
                    .length()));

            MenuItem item = (MenuItem) getItems().get(index);

            return item.getElement();
        }

        public String getSubPartName(Element subElement) {
            if (!getElement().isOrHasChild(subElement)) {
                return null;
            }

            Element menuItemRoot = subElement;
            while (menuItemRoot != null
                    && !menuItemRoot.getTagName().equalsIgnoreCase("td")) {
                menuItemRoot = menuItemRoot.getParentElement().cast();
            }
            // "menuItemRoot" is now the root of the menu item

            final int itemCount = getItems().size();
            for (int i = 0; i < itemCount; i++) {
                if (((MenuItem) getItems().get(i)).getElement() == menuItemRoot) {
                    String name = SUBPART_PREFIX + i;
                    return name;
                }
            }
            return null;
        }
    }

    public static final int FILTERINGMODE_OFF = 0;
    public static final int FILTERINGMODE_STARTSWITH = 1;
    public static final int FILTERINGMODE_CONTAINS = 2;

    private static final String CLASSNAME = "v-filterselect";

    protected int pageLength = 10;

    private final FlowPanel panel = new FlowPanel();

    /**
     * The text box where the filter is written
     */
    private final TextBox tb = new TextBox() {
        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.TextBoxBase#onBrowserEvent(com.google
         * .gwt.user.client.Event)
         */
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (client != null) {
                client.handleTooltipEvent(event, VFilterSelect.this);
            }
        }
    };

    private final SuggestionPopup suggestionPopup = new SuggestionPopup();

    /**
     * Used when measuring the width of the popup
     */
    private final HTML popupOpener = new HTML("") {
        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
         * .user.client.Event)
         */
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (client != null) {
                client.handleTooltipEvent(event, VFilterSelect.this);
            }
        }
    };

    private final Image selectedItemIcon = new Image();

    private ApplicationConnection client;

    private String paintableId;

    private int currentPage;

    private final Collection<FilterSelectSuggestion> currentSuggestions = new ArrayList<FilterSelectSuggestion>();

    private boolean immediate;

    private String selectedOptionKey;

    private boolean filtering = false;
    private boolean selecting = false;
    private boolean tabPressed = false;
    private boolean initDone = false;

    private String lastFilter = "";
    private int lastIndex = -1; // last selected index when using arrows

    private FilterSelectSuggestion currentSuggestion;

    private int totalMatches;
    private boolean allowNewItem;
    private boolean nullSelectionAllowed;
    private boolean nullSelectItem;
    private boolean enabled;
    private boolean readonly;

    private int filteringmode = FILTERINGMODE_OFF;

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
    private int popupWidth = -1;
    /*
     * Stores the last new item string to avoid double submissions. Cleared on
     * uidl updates
     */
    private String lastNewItemString;
    private boolean focused = false;
    private int horizPaddingAndBorder = 2;

    /**
     * Default constructor
     */
    public VFilterSelect() {
        selectedItemIcon.setStyleName("v-icon");
        selectedItemIcon.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                updateRootWidth();
                updateSelectedIconPosition();

                /*
                 * We need to re-calculate the widths in IE at load time to
                 * ensure that the text is not placed behind the icon. #3991
                 */
                if (BrowserInfo.get().isIE()) {
                    int tbWidth = Util.getRequiredWidth(tb);
                    int openerWidth = Util.getRequiredWidth(popupOpener);
                    int iconWidth = selectedItemIcon.isAttached() ? Util
                            .measureMarginLeft(tb.getElement())
                            - Util.measureMarginLeft(selectedItemIcon
                                    .getElement()) : 0;

                    int w = tbWidth + openerWidth + iconWidth;
                    tb.setWidth((tbWidth - getTextboxPadding()) + "px");
                    setTextboxWidth(w);
                }
            }
        });

        tb.sinkEvents(VTooltip.TOOLTIP_EVENTS);
        popupOpener.sinkEvents(VTooltip.TOOLTIP_EVENTS);
        panel.add(tb);
        panel.add(popupOpener);
        initWidget(panel);
        setStyleName(CLASSNAME);
        tb.addKeyDownHandler(this);
        tb.addKeyUpHandler(this);
        tb.setStyleName(CLASSNAME + "-input");
        tb.addFocusHandler(this);
        tb.addBlurHandler(this);
        popupOpener.setStyleName(CLASSNAME + "-button");
        popupOpener.addClickHandler(this);
    }

    /**
     * Does the Select have more pages?
     * 
     * @return true if a next page exists, else false if the current page is the
     *         last page
     */
    public boolean hasNextPage() {
        if (totalMatches > (currentPage + 1) * pageLength) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Filters the options at a certain page. Uses the text box input as a
     * filter
     * 
     * @param page
     *            The page which items are to be filtered
     */
    public void filterOptions(int page) {
        filterOptions(page, tb.getText());
    }

    /**
     * Filters the options at certain page using the given filter
     * 
     * @param page
     *            The page to filter
     * @param filter
     *            The filter to apply to the components
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        paintableId = uidl.getId();
        this.client = client;

        readonly = uidl.hasAttribute("readonly");
        enabled = !uidl.hasAttribute("disabled");

        tb.setEnabled(enabled);
        tb.setReadOnly(readonly);

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            tb.setTabIndex(uidl.getIntAttribute("tabindex"));
        }

        if (uidl.hasAttribute("filteringmode")) {
            filteringmode = uidl.getIntAttribute("filteringmode");
        }

        immediate = uidl.hasAttribute("immediate");

        nullSelectionAllowed = uidl.hasAttribute("nullselect");

        nullSelectItem = uidl.hasAttribute("nullselectitem")
                && uidl.getBooleanAttribute("nullselectitem");

        currentPage = uidl.getIntVariable("page");

        if (uidl.hasAttribute("pagelength")) {
            pageLength = uidl.getIntAttribute("pagelength");
        }

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
                    setPromptingOff(suggestion.getReplacementString());
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
                if (!focused) {
                    /*
                     * client.updateComponent overwrites all styles so we must
                     * ALWAYS set the prompting style at this point, even though
                     * we think it has been set already...
                     */
                    prompting = false;
                    setPromptingOn();
                } else {
                    // we have focus in field, prompting can't be set on,
                    // instead just clear the input
                    tb.setValue("");
                }
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
                MenuItem activeMenuItem;
                if (lastIndex == 0) {
                    // going up, select last item
                    int lastItem = pageLength - 1;
                    List items = suggestionPopup.menu.getItems();
                    /*
                     * The first page can contain less than 10 items if the null
                     * selection item is filtered away
                     */
                    if (lastItem >= items.size()) {
                        lastItem = items.size() - 1;
                    }
                    activeMenuItem = (MenuItem) items.get(lastItem);
                    suggestionPopup.menu.selectItem(activeMenuItem);
                } else {
                    // going down, select first item
                    activeMenuItem = (MenuItem) suggestionPopup.menu.getItems()
                            .get(0);
                    suggestionPopup.menu.selectItem(activeMenuItem);
                }

                tb.setText(activeMenuItem.getText());
                tb.setSelectionRange(lastFilter.length(), activeMenuItem
                        .getText().length()
                        - lastFilter.length());

                lastIndex = -1; // reset
            }
            if (selecting) {
                suggestionPopup.menu.doPostFilterSelectedItemAction();
            }
        }

        // Calculate minumum textarea width
        suggestionPopupMinWidth = minWidth(captions);

        popupOpenerClicked = false;

        if (!initDone) {
            updateRootWidth();
        }

        initDone = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();

        /*
         * We need to recalculate the root width when the select is attached, so
         * #2974 won't happen.
         */
        updateRootWidth();
    }

    /**
     * Turns prompting on. When prompting is turned on a command prompt is shown
     * in the text box if nothing has been entered.
     */
    private void setPromptingOn() {
        if (!prompting) {
            prompting = true;
            addStyleDependentName(CLASSNAME_PROMPT);
        }
        tb.setText(inputPrompt);
    }

    /**
     * Turns prompting off. When prompting is turned on a command prompt is
     * shown in the text box if nothing has been entered.
     * 
     * @param text
     *            The text the text box should contain.
     */
    private void setPromptingOff(String text) {
        tb.setText(text);
        if (prompting) {
            prompting = false;
            removeStyleDependentName(CLASSNAME_PROMPT);
        }
    }

    /**
     * Triggered when a suggestion is selected
     * 
     * @param suggestion
     *            The suggestion that just got selected.
     */
    public void onSuggestionSelected(FilterSelectSuggestion suggestion) {
        selecting = false;

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
        if (!(newKey.equals(selectedOptionKey) || ("".equals(newKey) && selectedOptionKey == null))) {
            selectedOptionKey = newKey;
            client.updateVariable(paintableId, "selected",
                    new String[] { selectedOptionKey }, immediate);
            // currentPage = -1; // forget the page
        }
        suggestionPopup.hide();
    }

    /**
     * Sets the icon URI of the selected item. The icon is shown on the left
     * side of the item caption text. Set the URI to null to remove the icon.
     * 
     * @param iconUri
     *            The URI of the icon
     */
    private void setSelectedItemIcon(String iconUri) {
        if (iconUri == null || iconUri == "") {
            panel.remove(selectedItemIcon);
            updateRootWidth();
        } else {
            panel.insert(selectedItemIcon, 0);
            selectedItemIcon.setUrl(iconUri);
            updateRootWidth();
            updateSelectedIconPosition();
        }
    }

    /**
     * Positions the icon vertically in the middle. Should be called after the
     * icon has loaded
     */
    private void updateSelectedIconPosition() {
        // Position icon vertically to middle
        int availableHeight = getOffsetHeight();
        int iconHeight = Util.getRequiredHeight(selectedItemIcon);
        int marginTop = (availableHeight - iconHeight) / 2;
        DOM.setStyleAttribute(selectedItemIcon.getElement(), "marginTop",
                marginTop + "px");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event) {
        if (enabled && !readonly) {
            if (suggestionPopup.isAttached()) {
                popupKeyDown(event);
            } else {
                inputFieldKeyDown(event);
            }
        }
    }

    /**
     * Triggered when a key is pressed in the text box
     * 
     * @param event
     *            The KeyDownEvent
     */
    private void inputFieldKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_DOWN:
        case KeyCodes.KEY_UP:
        case KeyCodes.KEY_PAGEDOWN:
        case KeyCodes.KEY_PAGEUP:
            if (!suggestionPopup.isAttached()) {
                // open popup as from gadget
                filterOptions(-1, "");
                lastFilter = "";
                tb.selectAll();
            }
            break;
        case KeyCodes.KEY_TAB:
            if (suggestionPopup.isAttached()) {
                filterOptions(currentPage, tb.getText());
            }
            break;
        }

    }

    /**
     * Triggered when a key was pressed in the suggestion popup
     * 
     * @param event
     *            The KeyDownEvent of the key
     */
    private void popupKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_DOWN:
            suggestionPopup.selectNextItem();
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            break;
        case KeyCodes.KEY_UP:
            suggestionPopup.selectPrevItem();
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            break;
        case KeyCodes.KEY_PAGEDOWN:
            if (hasNextPage()) {
                filterOptions(currentPage + 1, lastFilter);
            }
            break;
        case KeyCodes.KEY_PAGEUP:
            if (currentPage > 0) {
                filterOptions(currentPage - 1, lastFilter);
            }
            break;
        case KeyCodes.KEY_TAB:
            if (suggestionPopup.isAttached()) {
                tabPressed = true;
                filterOptions(currentPage);
            }
            // onBlur() takes care of the rest
            break;
        case KeyCodes.KEY_ENTER:
            if (suggestionPopup.isAttached()) {
                filterOptions(currentPage);
            }
            if (currentSuggestions.size() == 1 && !allowNewItem) {
                // If there is only one suggestion, select that
                suggestionPopup.menu.selectItem((MenuItem) suggestionPopup.menu
                        .getItems().get(0));
            }
            suggestionPopup.menu.doSelectedItemAction();
            break;
        }

    }

    /**
     * Triggered when a key was depressed
     * 
     * @param event
     *            The KeyUpEvent of the key depressed
     */
    public void onKeyUp(KeyUpEvent event) {
        if (enabled && !readonly) {
            switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_ENTER:
            case KeyCodes.KEY_TAB:
            case KeyCodes.KEY_SHIFT:
            case KeyCodes.KEY_CTRL:
            case KeyCodes.KEY_ALT:
            case KeyCodes.KEY_DOWN:
            case KeyCodes.KEY_UP:
            case KeyCodes.KEY_PAGEDOWN:
            case KeyCodes.KEY_PAGEUP:
                ; // NOP
                break;
            case KeyCodes.KEY_ESCAPE:
                reset();
                break;
            default:
                filterOptions(currentPage);
                break;
            }
        }
    }

    /**
     * Resets the Select to its initial state
     */
    private void reset() {
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
    }

    /**
     * Listener for popupopener
     */
    public void onClick(ClickEvent event) {
        if (enabled && !readonly) {
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

    /**
     * Calculate minimum width for FilterSelect textarea
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    public void onFocus(FocusEvent event) {
        focused = true;
        if (prompting && !readonly) {
            setPromptingOff("");
        }
        addStyleDependentName("focus");

        if (client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(paintableId, EventId.FOCUS, "", true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */
    public void onBlur(BlurEvent event) {
        focused = false;
        if (!readonly) {
            // much of the TAB handling takes place here
            if (tabPressed) {
                tabPressed = false;
                suggestionPopup.menu.doSelectedItemAction();
                suggestionPopup.hide();
            } else if (!suggestionPopup.isAttached()
                    || suggestionPopup.isJustClosed()) {
                suggestionPopup.menu.doSelectedItemAction();
            }
            if (selectedOptionKey == null) {
                setPromptingOn();
            }
        }
        removeStyleDependentName("focus");

        if (client.hasEventListeners(this, EventId.BLUR)) {
            client.updateVariable(paintableId, EventId.BLUR, "", true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Focusable#focus()
     */
    public void focus() {
        focused = true;
        if (prompting && !readonly) {
            setPromptingOff("");
        }
        tb.setFocus(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
     */
    @Override
    public void setWidth(String width) {
        if (width == null || width.equals("")) {
            this.width = null;
        } else {
            this.width = width;
        }
        horizPaddingAndBorder = Util.setWidthExcludingPaddingAndBorder(this,
                width, horizPaddingAndBorder);

        if (initDone) {
            updateRootWidth();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        Util.setHeightExcludingPaddingAndBorder(tb, height, 3);
    }

    /**
     * Calculates the width of the select if the select has undefined width.
     * Should be called when the width changes or when the icon changes.
     */
    private void updateRootWidth() {
        if (width == null) {
            /*
             * When the width is not specified we must specify width for root
             * div so the popupopener won't wrap to the next line and also so
             * the size of the combobox won't change over time.
             */
            int tbWidth = Util.getRequiredWidth(tb);

            if (popupWidth < 0) {
                /*
                 * Only use the first page popup width so the textbox will not
                 * get resized whenever the popup is resized.
                 */
                popupWidth = Util.getRequiredWidth(popupOpener);
            }

            /*
             * Note: iconWidth is here calculated as a negative pixel value so
             * you should consider this in further calculations.
             */
            int iconWidth = selectedItemIcon.isAttached() ? Util
                    .measureMarginLeft(tb.getElement())
                    - Util.measureMarginLeft(selectedItemIcon.getElement()) : 0;

            int w = tbWidth + popupWidth + iconWidth;

            /*
             * When the select has a undefined with we need to check that we are
             * only setting the text box width relative to the first page width
             * of the items. If this is not done the text box width will change
             * when the popup is used to view longer items than the text box is
             * wide.
             */
            if ((!initDone || currentPage + 1 < 0)
                    && suggestionPopupMinWidth > w) {
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

    /**
     * Get the width of the select in pixels where the text area and icon has
     * been included.
     * 
     * @return The width in pixels
     */
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

    /**
     * Sets the text box width in pixels.
     * 
     * @param componentWidth
     *            The width of the text box in pixels
     */
    private void setTextboxWidth(int componentWidth) {
        int padding = getTextboxPadding();
        int popupOpenerWidth = Util.getRequiredWidth(popupOpener);
        int iconWidth = selectedItemIcon.isAttached() ? Util
                .getRequiredWidth(selectedItemIcon) : 0;
        int textboxWidth = componentWidth - padding - popupOpenerWidth
                - iconWidth;
        if (textboxWidth < 0) {
            textboxWidth = 0;
        }
        tb.setWidth(textboxWidth + "px");
    }

    /**
     * Gets the horizontal padding of the text box in pixels. The measurement
     * includes the border width.
     * 
     * @return The padding in pixels
     */
    private int getTextboxPadding() {
        if (textboxPadding < 0) {
            textboxPadding = Util.measureHorizontalPaddingAndBorder(tb
                    .getElement(), 4);
        }
        return textboxPadding;
    }

    /**
     * Gets the horizontal padding of the select. The measurement includes the
     * border width.
     * 
     * @return The padding in pixels
     */
    private int getComponentPadding() {
        if (componentPadding < 0) {
            componentPadding = Util.measureHorizontalPaddingAndBorder(
                    getElement(), 3);
        }
        return componentPadding;
    }
}
