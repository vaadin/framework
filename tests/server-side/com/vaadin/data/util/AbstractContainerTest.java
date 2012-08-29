package com.vaadin.data.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;

public abstract class AbstractContainerTest extends TestCase {

    /**
     * Helper class for testing e.g. listeners expecting events to be fired.
     */
    protected abstract static class AbstractEventCounter {
        private int eventCount = 0;
        private int lastAssertedEventCount = 0;

        /**
         * Increment the event count. To be called by subclasses e.g. from a
         * listener method.
         */
        protected void increment() {
            ++eventCount;
        }

        /**
         * Check that no one event has occurred since the previous assert call.
         */
        public void assertNone() {
            Assert.assertEquals(lastAssertedEventCount, eventCount);
        }

        /**
         * Check that exactly one event has occurred since the previous assert
         * call.
         */
        public void assertOnce() {
            Assert.assertEquals(++lastAssertedEventCount, eventCount);
        }

        /**
         * Reset the counter and the expected count.
         */
        public void reset() {
            eventCount = 0;
            lastAssertedEventCount = 0;
        }
    }

    /**
     * Test class for counting item set change events and verifying they have
     * been received.
     */
    protected static class ItemSetChangeCounter extends AbstractEventCounter
            implements ItemSetChangeListener {

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
            increment();
        }

    }

    // #6043: for items that have been filtered out, Container interface does
    // not specify what to return from getItem() and getContainerProperty(), so
    // need checkGetItemNull parameter for the test to be usable for most
    // current containers
    protected void validateContainer(Container container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet,
            boolean checkGetItemNull, int expectedSize) {
        Container.Indexed indexed = null;
        if (container instanceof Container.Indexed) {
            indexed = (Container.Indexed) container;
        }

        List<Object> itemIdList = new ArrayList<Object>(container.getItemIds());

        // size()
        assertEquals(expectedSize, container.size());
        assertEquals(expectedSize, itemIdList.size());

        // first item, last item
        Object first = itemIdList.get(0);
        Object last = itemIdList.get(itemIdList.size() - 1);

        assertEquals(expectedFirstItemId, first);
        assertEquals(expectedLastItemId, last);

        // containsId
        assertFalse(container.containsId(itemIdNotInSet));
        assertTrue(container.containsId(itemIdInSet));

        // getItem
        if (checkGetItemNull) {
            assertNull(container.getItem(itemIdNotInSet));
        }
        assertNotNull(container.getItem(itemIdInSet));

        // getContainerProperty
        for (Object propId : container.getContainerPropertyIds()) {
            if (checkGetItemNull) {
                assertNull(container.getContainerProperty(itemIdNotInSet,
                        propId));
            }
            assertNotNull(container.getContainerProperty(itemIdInSet, propId));
        }

        if (indexed != null) {
            // firstItemId
            assertEquals(first, indexed.firstItemId());

            // lastItemId
            assertEquals(last, indexed.lastItemId());

            // nextItemId
            assertEquals(itemIdList.get(1), indexed.nextItemId(first));

            // prevItemId
            assertEquals(itemIdList.get(itemIdList.size() - 2),
                    indexed.prevItemId(last));

            // isFirstId
            assertTrue(indexed.isFirstId(first));
            assertFalse(indexed.isFirstId(last));

            // isLastId
            assertTrue(indexed.isLastId(last));
            assertFalse(indexed.isLastId(first));

            // indexOfId
            assertEquals(0, indexed.indexOfId(first));
            assertEquals(expectedSize - 1, indexed.indexOfId(last));

            // getIdByIndex
            assertEquals(indexed.getIdByIndex(0), first);
            assertEquals(indexed.getIdByIndex(expectedSize - 1), last);

        }

    }

    protected static final Object FULLY_QUALIFIED_NAME = "fullyQualifiedName";
    protected static final Object SIMPLE_NAME = "simpleName";
    protected static final Object REVERSE_FULLY_QUALIFIED_NAME = "reverseFullyQualifiedName";
    protected static final Object ID_NUMBER = "idNumber";

    protected void testBasicContainerOperations(Container container) {
        initializeContainer(container);

        // Basic container
        validateContainer(container, sampleData[0],
                sampleData[sampleData.length - 1], sampleData[10], "abc", true,
                sampleData.length);
    }

    protected void testContainerOrdered(Container.Ordered container) {
        Object id = container.addItem();
        assertNotNull(id);
        Item item = container.getItem(id);
        assertNotNull(item);

        assertEquals(id, container.firstItemId());
        assertEquals(id, container.lastItemId());

        // isFirstId
        assertTrue(container.isFirstId(id));
        assertTrue(container.isFirstId(container.firstItemId()));
        // isLastId
        assertTrue(container.isLastId(id));
        assertTrue(container.isLastId(container.lastItemId()));

        // Add a new item before the first
        // addItemAfter
        Object newFirstId = container.addItemAfter(null);
        assertNotNull(newFirstId);
        assertNotNull(container.getItem(newFirstId));

        // isFirstId
        assertTrue(container.isFirstId(newFirstId));
        assertTrue(container.isFirstId(container.firstItemId()));
        // isLastId
        assertTrue(container.isLastId(id));
        assertTrue(container.isLastId(container.lastItemId()));

        // nextItemId
        assertEquals(id, container.nextItemId(newFirstId));
        assertNull(container.nextItemId(id));
        assertNull(container.nextItemId("not-in-container"));

        // prevItemId
        assertEquals(newFirstId, container.prevItemId(id));
        assertNull(container.prevItemId(newFirstId));
        assertNull(container.prevItemId("not-in-container"));

        // addItemAfter(Object)
        Object newSecondItemId = container.addItemAfter(newFirstId);
        // order is now: newFirstId, newSecondItemId, id
        assertNotNull(newSecondItemId);
        assertNotNull(container.getItem(newSecondItemId));
        assertEquals(id, container.nextItemId(newSecondItemId));
        assertEquals(newFirstId, container.prevItemId(newSecondItemId));

        // addItemAfter(Object,Object)
        String fourthId = "id of the fourth item";
        Item fourth = container.addItemAfter(newFirstId, fourthId);
        // order is now: newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fourth);
        assertEquals(fourth, container.getItem(fourthId));
        assertEquals(newSecondItemId, container.nextItemId(fourthId));
        assertEquals(newFirstId, container.prevItemId(fourthId));

        // addItemAfter(Object,Object)
        Object fifthId = new Object();
        Item fifth = container.addItemAfter(null, fifthId);
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fifth);
        assertEquals(fifth, container.getItem(fifthId));
        assertEquals(newFirstId, container.nextItemId(fifthId));
        assertNull(container.prevItemId(fifthId));

    }

    protected void testContainerIndexed(Container.Indexed container,
            Object itemId, int itemPosition, boolean testAddEmptyItemAt,
            Object newItemId, boolean testAddItemAtWithId) {
        initializeContainer(container);

        // indexOfId
        Assert.assertEquals(itemPosition, container.indexOfId(itemId));

        // getIdByIndex
        Assert.assertEquals(itemId, container.getIdByIndex(itemPosition));

        // addItemAt
        if (testAddEmptyItemAt) {
            Object addedId = container.addItemAt(itemPosition);
            Assert.assertEquals(itemPosition, container.indexOfId(addedId));
            Assert.assertEquals(itemPosition + 1, container.indexOfId(itemId));
            Assert.assertEquals(addedId, container.getIdByIndex(itemPosition));
            Assert.assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 1));

            Object newFirstId = container.addItemAt(0);
            Assert.assertEquals(0, container.indexOfId(newFirstId));
            Assert.assertEquals(itemPosition + 2, container.indexOfId(itemId));
            Assert.assertEquals(newFirstId, container.firstItemId());
            Assert.assertEquals(newFirstId, container.getIdByIndex(0));
            Assert.assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 2));

            Object newLastId = container.addItemAt(container.size());
            Assert.assertEquals(container.size() - 1,
                    container.indexOfId(newLastId));
            Assert.assertEquals(itemPosition + 2, container.indexOfId(itemId));
            Assert.assertEquals(newLastId, container.lastItemId());
            Assert.assertEquals(newLastId,
                    container.getIdByIndex(container.size() - 1));
            Assert.assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 2));

            Assert.assertTrue(container.removeItem(addedId));
            Assert.assertTrue(container.removeItem(newFirstId));
            Assert.assertTrue(container.removeItem(newLastId));

            Assert.assertFalse(
                    "Removing non-existing item should indicate failure",
                    container.removeItem(addedId));
        }

        // addItemAt
        if (testAddItemAtWithId) {
            container.addItemAt(itemPosition, newItemId);
            Assert.assertEquals(itemPosition, container.indexOfId(newItemId));
            Assert.assertEquals(itemPosition + 1, container.indexOfId(itemId));
            Assert.assertEquals(newItemId, container.getIdByIndex(itemPosition));
            Assert.assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 1));
            Assert.assertTrue(container.removeItem(newItemId));
            Assert.assertFalse(container.containsId(newItemId));

            container.addItemAt(0, newItemId);
            Assert.assertEquals(0, container.indexOfId(newItemId));
            Assert.assertEquals(itemPosition + 1, container.indexOfId(itemId));
            Assert.assertEquals(newItemId, container.firstItemId());
            Assert.assertEquals(newItemId, container.getIdByIndex(0));
            Assert.assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 1));
            Assert.assertTrue(container.removeItem(newItemId));
            Assert.assertFalse(container.containsId(newItemId));

            container.addItemAt(container.size(), newItemId);
            Assert.assertEquals(container.size() - 1,
                    container.indexOfId(newItemId));
            Assert.assertEquals(itemPosition, container.indexOfId(itemId));
            Assert.assertEquals(newItemId, container.lastItemId());
            Assert.assertEquals(newItemId,
                    container.getIdByIndex(container.size() - 1));
            Assert.assertEquals(itemId, container.getIdByIndex(itemPosition));
            Assert.assertTrue(container.removeItem(newItemId));
            Assert.assertFalse(container.containsId(newItemId));
        }
    }

    protected void testContainerFiltering(Container.Filterable container) {
        initializeContainer(container);

        // Filter by "contains ab"
        container.addContainerFilter(new SimpleStringFilter(
                FULLY_QUALIFIED_NAME, "ab", false, false));

        validateContainer(container, "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TabSheet", "com.vaadin.client.Focusable",
                "com.vaadin.data.Buffered", isFilteredOutItemNull(), 20);

        // Filter by "contains da" (reversed as ad here)
        container.removeAllContainerFilters();
        container.addContainerFilter(new SimpleStringFilter(
                REVERSE_FULLY_QUALIFIED_NAME, "ad", false, false));

        validateContainer(container, "com.vaadin.data.Buffered",
                "com.vaadin.terminal.gwt.server.ComponentSizeValidator",
                "com.vaadin.data.util.IndexedContainer",
                "com.vaadin.client.ui.VUriFragmentUtility",
                isFilteredOutItemNull(), 37);
    }

    /**
     * Override in subclasses to return false if the container getItem() method
     * returns a non-null value for an item that has been filtered out.
     * 
     * @return
     */
    protected boolean isFilteredOutItemNull() {
        return true;
    }

    protected void testContainerSortingAndFiltering(Container.Sortable sortable) {
        Filterable filterable = (Filterable) sortable;

        initializeContainer(sortable);

        // Filter by "contains ab"
        filterable.addContainerFilter(new SimpleStringFilter(
                FULLY_QUALIFIED_NAME, "ab", false, false));

        // Must be able to sort based on PROP1 for this test
        assertTrue(sortable.getSortableContainerPropertyIds().contains(
                FULLY_QUALIFIED_NAME));

        sortable.sort(new Object[] { FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateContainer(sortable, "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TableFieldFactory",
                "com.vaadin.ui.TableFieldFactory",
                "com.vaadin.data.util.BeanItem", isFilteredOutItemNull(), 20);
    }

    protected void testContainerSorting(Container.Filterable container) {
        Container.Sortable sortable = (Sortable) container;

        initializeContainer(container);

        // Must be able to sort based on PROP1 for this test
        assertTrue(sortable.getSortableContainerPropertyIds().contains(
                FULLY_QUALIFIED_NAME));
        assertTrue(sortable.getSortableContainerPropertyIds().contains(
                REVERSE_FULLY_QUALIFIED_NAME));

        sortable.sort(new Object[] { FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateContainer(container, "com.vaadin.Application",
                "org.vaadin.test.LastClass",
                "com.vaadin.server.ApplicationResource", "blah", true,
                sampleData.length);

        sortable.sort(new Object[] { REVERSE_FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateContainer(container,
                "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
                "com.vaadin.data.util.ObjectProperty",
                "com.vaadin.ui.BaseFieldFactory", "blah", true,
                sampleData.length);

    }

    protected void initializeContainer(Container container) {
        Assert.assertTrue(container.removeAllItems());
        Object[] propertyIds = container.getContainerPropertyIds().toArray();
        for (Object propertyId : propertyIds) {
            container.removeContainerProperty(propertyId);
        }

        container.addContainerProperty(FULLY_QUALIFIED_NAME, String.class, "");
        container.addContainerProperty(SIMPLE_NAME, String.class, "");
        container.addContainerProperty(REVERSE_FULLY_QUALIFIED_NAME,
                String.class, null);
        container.addContainerProperty(ID_NUMBER, Integer.class, null);

        for (int i = 0; i < sampleData.length; i++) {
            String id = sampleData[i];
            Item item = container.addItem(id);

            item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(sampleData[i]);
            item.getItemProperty(SIMPLE_NAME).setValue(
                    getSimpleName(sampleData[i]));
            item.getItemProperty(REVERSE_FULLY_QUALIFIED_NAME).setValue(
                    reverse(sampleData[i]));
            item.getItemProperty(ID_NUMBER).setValue(i);
        }
    }

    protected static String getSimpleName(String name) {
        if (name.contains(".")) {
            return name.substring(name.lastIndexOf('.') + 1);
        } else {
            return name;
        }
    }

    protected static String reverse(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    protected final String[] sampleData = {
            "com.vaadin.annotations.AutoGenerated", "com.vaadin.Application",
            "com.vaadin.data.Buffered", "com.vaadin.data.BufferedValidatable",
            "com.vaadin.data.Container", "com.vaadin.data.Item",
            "com.vaadin.data.Property", "com.vaadin.data.util.BeanItem",
            "com.vaadin.data.util.BeanItemContainer",
            "com.vaadin.data.util.ContainerHierarchicalWrapper",
            "com.vaadin.data.util.ContainerOrderedWrapper",
            "com.vaadin.data.util.DefaultItemSorter",
            "com.vaadin.data.util.FilesystemContainer",
            "com.vaadin.data.util.Filter",
            "com.vaadin.data.util.HierarchicalContainer",
            "com.vaadin.data.util.IndexedContainer",
            "com.vaadin.data.util.ItemSorter",
            "com.vaadin.data.util.MethodProperty",
            "com.vaadin.data.util.ObjectProperty",
            "com.vaadin.data.util.PropertyFormatter",
            "com.vaadin.data.util.PropertysetItem",
            "com.vaadin.data.util.QueryContainer",
            "com.vaadin.data.util.TextFileProperty",
            "com.vaadin.data.Validatable",
            "com.vaadin.data.validator.AbstractStringValidator",
            "com.vaadin.data.validator.AbstractValidator",
            "com.vaadin.data.validator.CompositeValidator",
            "com.vaadin.data.validator.DoubleValidator",
            "com.vaadin.data.validator.EmailValidator",
            "com.vaadin.data.validator.IntegerValidator",
            "com.vaadin.data.validator.NullValidator",
            "com.vaadin.data.validator.RegexpValidator",
            "com.vaadin.data.validator.StringLengthValidator",
            "com.vaadin.data.Validator", "com.vaadin.event.Action",
            "com.vaadin.event.ComponentEventListener",
            "com.vaadin.event.EventRouter", "com.vaadin.event.FieldEvents",
            "com.vaadin.event.ItemClickEvent", "com.vaadin.event.LayoutEvents",
            "com.vaadin.event.ListenerMethod",
            "com.vaadin.event.MethodEventSource",
            "com.vaadin.event.MouseEvents", "com.vaadin.event.ShortcutAction",
            "com.vaadin.launcher.DemoLauncher",
            "com.vaadin.launcher.DevelopmentServerLauncher",
            "com.vaadin.launcher.util.BrowserLauncher",
            "com.vaadin.service.ApplicationContext",
            "com.vaadin.service.FileTypeResolver",
            "com.vaadin.server.ApplicationResource",
            "com.vaadin.server.ClassResource",
            "com.vaadin.server.CompositeErrorMessage",
            "com.vaadin.server.DownloadStream",
            "com.vaadin.server.ErrorMessage",
            "com.vaadin.server.ExternalResource",
            "com.vaadin.server.FileResource",
            "com.vaadin.client.ApplicationConfiguration",
            "com.vaadin.client.ApplicationConnection",
            "com.vaadin.client.BrowserInfo",
            "com.vaadin.client.ClientExceptionHandler",
            "com.vaadin.client.ComponentDetail",
            "com.vaadin.client.ComponentDetailMap",
            "com.vaadin.client.ComponentLocator", "com.vaadin.client.Console",
            "com.vaadin.client.Container",
            "com.vaadin.client.ContainerResizedListener",
            "com.vaadin.client.CSSRule", "com.vaadin.client.DateTimeService",
            "com.vaadin.client.DefaultWidgetSet",
            "com.vaadin.client.Focusable",
            "com.vaadin.client.HistoryImplIEVaadin",
            "com.vaadin.client.LocaleNotLoadedException",
            "com.vaadin.client.LocaleService",
            "com.vaadin.client.MouseEventDetails",
            "com.vaadin.client.NullConsole", "com.vaadin.client.Paintable",
            "com.vaadin.client.RenderInformation",
            "com.vaadin.client.RenderSpace",
            "com.vaadin.client.StyleConstants",
            "com.vaadin.client.TooltipInfo", "com.vaadin.client.ui.Action",
            "com.vaadin.client.ui.ActionOwner",
            "com.vaadin.client.ui.AlignmentInfo",
            "com.vaadin.client.ui.CalendarEntry",
            "com.vaadin.client.ui.ClickEventHandler",
            "com.vaadin.client.ui.Field", "com.vaadin.client.ui.Icon",
            "com.vaadin.client.ui.layout.CellBasedLayout",
            "com.vaadin.client.ui.layout.ChildComponentContainer",
            "com.vaadin.client.ui.layout.Margins",
            "com.vaadin.client.ui.LayoutClickEventHandler",
            "com.vaadin.client.ui.MenuBar", "com.vaadin.client.ui.MenuItem",
            "com.vaadin.client.ui.richtextarea.VRichTextArea",
            "com.vaadin.client.ui.richtextarea.VRichTextToolbar",
            "com.vaadin.client.ui.ShortcutActionHandler",
            "com.vaadin.client.ui.SubPartAware", "com.vaadin.client.ui.Table",
            "com.vaadin.client.ui.TreeAction",
            "com.vaadin.client.ui.TreeImages",
            "com.vaadin.client.ui.VAbsoluteLayout",
            "com.vaadin.client.ui.VAccordion", "com.vaadin.client.ui.VButton",
            "com.vaadin.client.ui.VCalendarPanel",
            "com.vaadin.client.ui.VCheckBox",
            "com.vaadin.client.ui.VContextMenu",
            "com.vaadin.client.ui.VCssLayout",
            "com.vaadin.client.ui.VCustomComponent",
            "com.vaadin.client.ui.VCustomLayout",
            "com.vaadin.client.ui.VDateField",
            "com.vaadin.client.ui.VDateFieldCalendar",
            "com.vaadin.client.ui.VEmbedded",
            "com.vaadin.client.ui.VFilterSelect", "com.vaadin.client.ui.VForm",
            "com.vaadin.client.ui.VFormLayout",
            "com.vaadin.client.ui.VGridLayout",
            "com.vaadin.client.ui.VHorizontalLayout",
            "com.vaadin.client.ui.VLabel", "com.vaadin.client.ui.VLink",
            "com.vaadin.client.ui.VListSelect",
            "com.vaadin.client.ui.VMarginInfo",
            "com.vaadin.client.ui.VMenuBar",
            "com.vaadin.client.ui.VNativeButton",
            "com.vaadin.client.ui.VNativeSelect",
            "com.vaadin.client.ui.VNotification",
            "com.vaadin.client.ui.VOptionGroup",
            "com.vaadin.client.ui.VOptionGroupBase",
            "com.vaadin.client.ui.VOrderedLayout",
            "com.vaadin.client.ui.VOverlay", "com.vaadin.client.ui.VPanel",
            "com.vaadin.client.ui.VPasswordField",
            "com.vaadin.client.ui.VPopupCalendar",
            "com.vaadin.client.ui.VPopupView",
            "com.vaadin.client.ui.VProgressIndicator",
            "com.vaadin.client.ui.VScrollTable",
            "com.vaadin.client.ui.VSlider", "com.vaadin.client.ui.VSplitPanel",
            "com.vaadin.client.ui.VSplitPanelHorizontal",
            "com.vaadin.client.ui.VSplitPanelVertical",
            "com.vaadin.client.ui.VTablePaging",
            "com.vaadin.client.ui.VTabsheet",
            "com.vaadin.client.ui.VTabsheetBase",
            "com.vaadin.client.ui.VTabsheetPanel",
            "com.vaadin.client.ui.VTextArea",
            "com.vaadin.client.ui.VTextField",
            "com.vaadin.client.ui.VTextualDate", "com.vaadin.client.ui.VTime",
            "com.vaadin.client.ui.VTree",
            "com.vaadin.client.ui.VTwinColSelect",
            "com.vaadin.client.ui.VUnknownComponent",
            "com.vaadin.client.ui.VUpload",
            "com.vaadin.client.ui.VUriFragmentUtility",
            "com.vaadin.client.ui.VVerticalLayout",
            "com.vaadin.client.ui.VView", "com.vaadin.client.ui.VWindow",
            "com.vaadin.client.UIDL", "com.vaadin.client.Util",
            "com.vaadin.client.ValueMap", "com.vaadin.client.VCaption",
            "com.vaadin.client.VCaptionWrapper",
            "com.vaadin.client.VDebugConsole",
            "com.vaadin.client.VErrorMessage", "com.vaadin.client.VTooltip",
            "com.vaadin.client.VUIDLBrowser", "com.vaadin.client.WidgetMap",
            "com.vaadin.client.WidgetSet",
            "com.vaadin.terminal.gwt.server.AbstractApplicationPortlet",
            "com.vaadin.terminal.gwt.server.AbstractApplicationServlet",
            "com.vaadin.terminal.gwt.server.AbstractCommunicationManager",
            "com.vaadin.terminal.gwt.server.AbstractWebApplicationContext",
            "com.vaadin.terminal.gwt.server.ApplicationPortlet",
            "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
            "com.vaadin.terminal.gwt.server.ApplicationRunnerServlet",
            "com.vaadin.terminal.gwt.server.ApplicationServlet",
            "com.vaadin.terminal.gwt.server.ChangeVariablesErrorEvent",
            "com.vaadin.terminal.gwt.server.CommunicationManager",
            "com.vaadin.terminal.gwt.server.ComponentSizeValidator",
            "com.vaadin.terminal.gwt.server.Constants",
            "com.vaadin.terminal.gwt.server.GAEApplicationServlet",
            "com.vaadin.terminal.gwt.server.HttpServletRequestListener",
            "com.vaadin.terminal.gwt.server.HttpUploadStream",
            "com.vaadin.terminal.gwt.server.JsonPaintTarget",
            "com.vaadin.terminal.gwt.server.PortletApplicationContext",
            "com.vaadin.terminal.gwt.server.PortletApplicationContext2",
            "com.vaadin.terminal.gwt.server.PortletCommunicationManager",
            "com.vaadin.terminal.gwt.server.PortletRequestListener",
            "com.vaadin.terminal.gwt.server.RestrictedRenderResponse",
            "com.vaadin.terminal.gwt.server.SessionExpiredException",
            "com.vaadin.terminal.gwt.server.SystemMessageException",
            "com.vaadin.terminal.gwt.server.WebApplicationContext",
            "com.vaadin.terminal.gwt.server.WebBrowser",
            "com.vaadin.terminal.gwt.widgetsetutils.ClassPathExplorer",
            "com.vaadin.terminal.gwt.widgetsetutils.WidgetMapGenerator",
            "com.vaadin.terminal.gwt.widgetsetutils.WidgetSetBuilder",
            "com.vaadin.server.KeyMapper", "com.vaadin.server.Paintable",
            "com.vaadin.server.PaintException",
            "com.vaadin.server.PaintTarget",
            "com.vaadin.server.ParameterHandler", "com.vaadin.server.Resource",
            "com.vaadin.server.Scrollable", "com.vaadin.server.Sizeable",
            "com.vaadin.server.StreamResource",
            "com.vaadin.server.SystemError", "com.vaadin.server.Terminal",
            "com.vaadin.server.ThemeResource",
            "com.vaadin.server.UploadStream", "com.vaadin.server.URIHandler",
            "com.vaadin.server.UserError", "com.vaadin.server.VariableOwner",
            "com.vaadin.tools.ReflectTools",
            "com.vaadin.tools.WidgetsetCompiler",
            "com.vaadin.ui.AbsoluteLayout", "com.vaadin.ui.AbstractComponent",
            "com.vaadin.ui.AbstractComponentContainer",
            "com.vaadin.ui.AbstractField", "com.vaadin.ui.AbstractLayout",
            "com.vaadin.ui.AbstractOrderedLayout",
            "com.vaadin.ui.AbstractSelect", "com.vaadin.ui.Accordion",
            "com.vaadin.ui.Alignment", "com.vaadin.ui.AlignmentUtils",
            "com.vaadin.ui.BaseFieldFactory", "com.vaadin.ui.Button",
            "com.vaadin.ui.CheckBox", "com.vaadin.ui.ClientWidget",
            "com.vaadin.ui.ComboBox", "com.vaadin.ui.Component",
            "com.vaadin.ui.ComponentContainer", "com.vaadin.ui.CssLayout",
            "com.vaadin.ui.CustomComponent", "com.vaadin.ui.CustomLayout",
            "com.vaadin.ui.DateField", "com.vaadin.ui.DefaultFieldFactory",
            "com.vaadin.ui.Embedded", "com.vaadin.ui.ExpandLayout",
            "com.vaadin.ui.Field", "com.vaadin.ui.FieldFactory",
            "com.vaadin.ui.Form", "com.vaadin.ui.FormFieldFactory",
            "com.vaadin.ui.FormLayout", "com.vaadin.ui.GridLayout",
            "com.vaadin.ui.HorizontalLayout", "com.vaadin.ui.InlineDateField",
            "com.vaadin.ui.Label", "com.vaadin.ui.Layout",
            "com.vaadin.ui.Link", "com.vaadin.ui.ListSelect",
            "com.vaadin.ui.LoginForm", "com.vaadin.ui.MenuBar",
            "com.vaadin.ui.NativeButton", "com.vaadin.ui.NativeSelect",
            "com.vaadin.ui.OptionGroup", "com.vaadin.ui.OrderedLayout",
            "com.vaadin.ui.Panel", "com.vaadin.ui.PopupDateField",
            "com.vaadin.ui.PopupView", "com.vaadin.ui.ProgressIndicator",
            "com.vaadin.ui.RichTextArea", "com.vaadin.ui.Select",
            "com.vaadin.ui.Slider", "com.vaadin.ui.SplitPanel",
            "com.vaadin.ui.Table", "com.vaadin.ui.TableFieldFactory",
            "com.vaadin.ui.TabSheet", "com.vaadin.ui.TextField",
            "com.vaadin.ui.Tree", "com.vaadin.ui.TwinColSelect",
            "com.vaadin.ui.Upload", "com.vaadin.ui.UriFragmentUtility",
            "com.vaadin.ui.VerticalLayout", "com.vaadin.ui.Window",
            "com.vaadin.util.SerializerHelper", "org.vaadin.test.LastClass" };

}
