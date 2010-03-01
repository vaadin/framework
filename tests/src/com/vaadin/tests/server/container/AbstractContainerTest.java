package com.vaadin.tests.server.container;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Sortable;

public abstract class AbstractContainerTest extends TestCase {

    protected void validateContainer(Container container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet, int expectedSize) {
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
        assertNull(container.getItem(itemIdNotInSet));
        assertNotNull(container.getItem(itemIdInSet));

        // getContainerProperty
        for (Object propId : container.getContainerPropertyIds()) {
            assertNull(container.getContainerProperty(itemIdNotInSet, propId));
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
            assertEquals(itemIdList.get(itemIdList.size() - 2), indexed
                    .prevItemId(last));

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

    protected static final Object PROP1 = "PROP1";
    protected static final Object PROP2 = "PROP2";
    protected static final Object PROP3 = "PROP3";

    protected void testBasicContainerOperations(Container container) {
        initializeContainer(container);

        // Basic container
        validateContainer(container, sampleData[0],
                sampleData[sampleData.length - 1], sampleData[10], "abc",
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

    protected void testContainerFiltering(Container.Filterable container) {
        initializeContainer(container);

        // Filter by "contains ab"
        container.addContainerFilter(PROP1, "ab", false, false);

        validateContainer(container, "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TabSheet",
                "com.vaadin.terminal.gwt.client.Focusable",
                "com.vaadin.data.Buffered", 20);

        // Filter by "contains da" (reversed as ad here)
        container.removeAllContainerFilters();
        container.addContainerFilter(PROP2, "ad", false, false);

        validateContainer(container, "com.vaadin.data.Buffered",
                "com.vaadin.terminal.gwt.server.ComponentSizeValidator",
                "com.vaadin.data.util.IndexedContainer",
                "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility", 37);
    }

    protected void testContainerSortingAndFiltering(Container.Sortable sortable) {
        Filterable filterable = (Filterable) sortable;

        initializeContainer(sortable);

        // Filter by "contains ab"
        filterable.addContainerFilter(PROP1, "ab", false, false);

        // Must be able to sort based on PROP1 for this test
        assertTrue(sortable.getSortableContainerPropertyIds().contains(PROP1));

        sortable.sort(new Object[] { PROP1 }, new boolean[] { true });

        validateContainer(sortable, "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TableFieldFactory",
                "com.vaadin.ui.TableFieldFactory",
                "com.vaadin.data.util.BeanItem", 20);
    }

    protected void testContainerSorting(Container.Filterable container) {
        Container.Sortable sortable = (Sortable) container;

        initializeContainer(container);

        // Must be able to sort based on PROP1 for this test
        assertTrue(sortable.getSortableContainerPropertyIds().contains(PROP1));
        assertTrue(sortable.getSortableContainerPropertyIds().contains(PROP2));

        sortable.sort(new Object[] { PROP1 }, new boolean[] { true });

        validateContainer(container, "com.vaadin.Application",
                "com.vaadin.util.SerializerHelper",
                "com.vaadin.terminal.ApplicationResource", "blah",
                sampleData.length);

        sortable.sort(new Object[] { PROP2 }, new boolean[] { true });

        validateContainer(container,
                "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
                "com.vaadin.data.util.ObjectProperty",
                "com.vaadin.ui.BaseFieldFactory", "blah", sampleData.length);

    }

    protected void initializeContainer(Container container) {
        container.removeAllItems();
        Object[] propertyIds = container.getContainerPropertyIds().toArray();
        for (Object propertyId : propertyIds) {
            container.removeContainerProperty(propertyId);
        }

        container.addContainerProperty(PROP1, String.class, "");
        container.addContainerProperty(PROP2, String.class, null);
        container.addContainerProperty(PROP3, Integer.class, null);

        for (int i = 0; i < sampleData.length; i++) {
            String id = sampleData[i];
            Item item = container.addItem(id);

            item.getItemProperty(PROP1).setValue(sampleData[i]);
            item.getItemProperty(PROP2).setValue(reverse(sampleData[i]));
            item.getItemProperty(PROP3).setValue(i);
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
            "com.vaadin.terminal.ApplicationResource",
            "com.vaadin.terminal.ClassResource",
            "com.vaadin.terminal.CompositeErrorMessage",
            "com.vaadin.terminal.DownloadStream",
            "com.vaadin.terminal.ErrorMessage",
            "com.vaadin.terminal.ExternalResource",
            "com.vaadin.terminal.FileResource",
            "com.vaadin.terminal.gwt.client.ApplicationConfiguration",
            "com.vaadin.terminal.gwt.client.ApplicationConnection",
            "com.vaadin.terminal.gwt.client.BrowserInfo",
            "com.vaadin.terminal.gwt.client.ClientExceptionHandler",
            "com.vaadin.terminal.gwt.client.ComponentDetail",
            "com.vaadin.terminal.gwt.client.ComponentDetailMap",
            "com.vaadin.terminal.gwt.client.ComponentLocator",
            "com.vaadin.terminal.gwt.client.Console",
            "com.vaadin.terminal.gwt.client.Container",
            "com.vaadin.terminal.gwt.client.ContainerResizedListener",
            "com.vaadin.terminal.gwt.client.CSSRule",
            "com.vaadin.terminal.gwt.client.DateTimeService",
            "com.vaadin.terminal.gwt.client.DefaultWidgetSet",
            "com.vaadin.terminal.gwt.client.Focusable",
            "com.vaadin.terminal.gwt.client.HistoryImplIEVaadin",
            "com.vaadin.terminal.gwt.client.LocaleNotLoadedException",
            "com.vaadin.terminal.gwt.client.LocaleService",
            "com.vaadin.terminal.gwt.client.MouseEventDetails",
            "com.vaadin.terminal.gwt.client.NullConsole",
            "com.vaadin.terminal.gwt.client.Paintable",
            "com.vaadin.terminal.gwt.client.RenderInformation",
            "com.vaadin.terminal.gwt.client.RenderSpace",
            "com.vaadin.terminal.gwt.client.StyleConstants",
            "com.vaadin.terminal.gwt.client.TooltipInfo",
            "com.vaadin.terminal.gwt.client.ui.Action",
            "com.vaadin.terminal.gwt.client.ui.ActionOwner",
            "com.vaadin.terminal.gwt.client.ui.AlignmentInfo",
            "com.vaadin.terminal.gwt.client.ui.CalendarEntry",
            "com.vaadin.terminal.gwt.client.ui.ClickEventHandler",
            "com.vaadin.terminal.gwt.client.ui.Field",
            "com.vaadin.terminal.gwt.client.ui.Icon",
            "com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout",
            "com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer",
            "com.vaadin.terminal.gwt.client.ui.layout.Margins",
            "com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler",
            "com.vaadin.terminal.gwt.client.ui.MenuBar",
            "com.vaadin.terminal.gwt.client.ui.MenuItem",
            "com.vaadin.terminal.gwt.client.ui.richtextarea.VRichTextArea",
            "com.vaadin.terminal.gwt.client.ui.richtextarea.VRichTextToolbar",
            "com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler",
            "com.vaadin.terminal.gwt.client.ui.SubPartAware",
            "com.vaadin.terminal.gwt.client.ui.Table",
            "com.vaadin.terminal.gwt.client.ui.TreeAction",
            "com.vaadin.terminal.gwt.client.ui.TreeImages",
            "com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout",
            "com.vaadin.terminal.gwt.client.ui.VAccordion",
            "com.vaadin.terminal.gwt.client.ui.VButton",
            "com.vaadin.terminal.gwt.client.ui.VCalendarPanel",
            "com.vaadin.terminal.gwt.client.ui.VCheckBox",
            "com.vaadin.terminal.gwt.client.ui.VContextMenu",
            "com.vaadin.terminal.gwt.client.ui.VCssLayout",
            "com.vaadin.terminal.gwt.client.ui.VCustomComponent",
            "com.vaadin.terminal.gwt.client.ui.VCustomLayout",
            "com.vaadin.terminal.gwt.client.ui.VDateField",
            "com.vaadin.terminal.gwt.client.ui.VDateFieldCalendar",
            "com.vaadin.terminal.gwt.client.ui.VEmbedded",
            "com.vaadin.terminal.gwt.client.ui.VFilterSelect",
            "com.vaadin.terminal.gwt.client.ui.VForm",
            "com.vaadin.terminal.gwt.client.ui.VFormLayout",
            "com.vaadin.terminal.gwt.client.ui.VGridLayout",
            "com.vaadin.terminal.gwt.client.ui.VHorizontalLayout",
            "com.vaadin.terminal.gwt.client.ui.VLabel",
            "com.vaadin.terminal.gwt.client.ui.VLink",
            "com.vaadin.terminal.gwt.client.ui.VListSelect",
            "com.vaadin.terminal.gwt.client.ui.VMarginInfo",
            "com.vaadin.terminal.gwt.client.ui.VMenuBar",
            "com.vaadin.terminal.gwt.client.ui.VNativeButton",
            "com.vaadin.terminal.gwt.client.ui.VNativeSelect",
            "com.vaadin.terminal.gwt.client.ui.VNotification",
            "com.vaadin.terminal.gwt.client.ui.VOptionGroup",
            "com.vaadin.terminal.gwt.client.ui.VOptionGroupBase",
            "com.vaadin.terminal.gwt.client.ui.VOrderedLayout",
            "com.vaadin.terminal.gwt.client.ui.VOverlay",
            "com.vaadin.terminal.gwt.client.ui.VPanel",
            "com.vaadin.terminal.gwt.client.ui.VPasswordField",
            "com.vaadin.terminal.gwt.client.ui.VPopupCalendar",
            "com.vaadin.terminal.gwt.client.ui.VPopupView",
            "com.vaadin.terminal.gwt.client.ui.VProgressIndicator",
            "com.vaadin.terminal.gwt.client.ui.VScrollTable",
            "com.vaadin.terminal.gwt.client.ui.VSlider",
            "com.vaadin.terminal.gwt.client.ui.VSplitPanel",
            "com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal",
            "com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical",
            "com.vaadin.terminal.gwt.client.ui.VTablePaging",
            "com.vaadin.terminal.gwt.client.ui.VTabsheet",
            "com.vaadin.terminal.gwt.client.ui.VTabsheetBase",
            "com.vaadin.terminal.gwt.client.ui.VTabsheetPanel",
            "com.vaadin.terminal.gwt.client.ui.VTextArea",
            "com.vaadin.terminal.gwt.client.ui.VTextField",
            "com.vaadin.terminal.gwt.client.ui.VTextualDate",
            "com.vaadin.terminal.gwt.client.ui.VTime",
            "com.vaadin.terminal.gwt.client.ui.VTree",
            "com.vaadin.terminal.gwt.client.ui.VTwinColSelect",
            "com.vaadin.terminal.gwt.client.ui.VUnknownComponent",
            "com.vaadin.terminal.gwt.client.ui.VUpload",
            "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility",
            "com.vaadin.terminal.gwt.client.ui.VVerticalLayout",
            "com.vaadin.terminal.gwt.client.ui.VView",
            "com.vaadin.terminal.gwt.client.ui.VWindow",
            "com.vaadin.terminal.gwt.client.UIDL",
            "com.vaadin.terminal.gwt.client.Util",
            "com.vaadin.terminal.gwt.client.ValueMap",
            "com.vaadin.terminal.gwt.client.VCaption",
            "com.vaadin.terminal.gwt.client.VCaptionWrapper",
            "com.vaadin.terminal.gwt.client.VDebugConsole",
            "com.vaadin.terminal.gwt.client.VErrorMessage",
            "com.vaadin.terminal.gwt.client.VTooltip",
            "com.vaadin.terminal.gwt.client.VUIDLBrowser",
            "com.vaadin.terminal.gwt.client.WidgetMap",
            "com.vaadin.terminal.gwt.client.WidgetSet",
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
            "com.vaadin.terminal.KeyMapper", "com.vaadin.terminal.Paintable",
            "com.vaadin.terminal.PaintException",
            "com.vaadin.terminal.PaintTarget",
            "com.vaadin.terminal.ParameterHandler",
            "com.vaadin.terminal.Resource", "com.vaadin.terminal.Scrollable",
            "com.vaadin.terminal.Sizeable",
            "com.vaadin.terminal.StreamResource",
            "com.vaadin.terminal.SystemError", "com.vaadin.terminal.Terminal",
            "com.vaadin.terminal.ThemeResource",
            "com.vaadin.terminal.UploadStream",
            "com.vaadin.terminal.URIHandler", "com.vaadin.terminal.UserError",
            "com.vaadin.terminal.VariableOwner",
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
            "com.vaadin.util.SerializerHelper" };

}
