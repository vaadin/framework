package com.vaadin.tests.util;

import java.util.ArrayList;
import java.util.Random;

import com.vaadin.automatedtests.util.MultiListener;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.demo.featurebrowser.ButtonExample;
import com.vaadin.demo.featurebrowser.ClientCachingExample;
import com.vaadin.demo.featurebrowser.ComboBoxExample;
import com.vaadin.demo.featurebrowser.EmbeddedBrowserExample;
import com.vaadin.demo.featurebrowser.JavaScriptAPIExample;
import com.vaadin.demo.featurebrowser.LabelExample;
import com.vaadin.demo.featurebrowser.LayoutExample;
import com.vaadin.demo.featurebrowser.NotificationExample;
import com.vaadin.demo.featurebrowser.RichTextExample;
import com.vaadin.demo.featurebrowser.SelectExample;
import com.vaadin.demo.featurebrowser.TableExample;
import com.vaadin.demo.featurebrowser.TreeExample;
import com.vaadin.demo.featurebrowser.ValueInputExample;
import com.vaadin.demo.featurebrowser.WindowingExample;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.StressComponentsInTable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

public class RandomComponents {

    private Random seededRandom = new Random(1);

    public RandomComponents() {

    }

    public void setRandom(Random rand) {
        seededRandom = rand;
    }

    /**
     * Get random component container
     * 
     * @param caption
     * @return
     */
    public ComponentContainer getRandomComponentContainer(String caption) {
        ComponentContainer result = null;
        final int randint = seededRandom.nextInt(5);
        switch (randint) {

        case 0:
            result = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
            ((OrderedLayout) result).setCaption("OrderedLayout_horizontal_"
                    + caption);
            break;
        case 1:
            result = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
            ((OrderedLayout) result).setCaption("OrderedLayout_vertical_"
                    + caption);
            break;
        case 2:
            GridLayout gl;
            if (seededRandom.nextInt(1) > 0) {
                gl = new GridLayout();
            } else {
                gl = new GridLayout(seededRandom.nextInt(3) + 1, seededRandom
                        .nextInt(3) + 1);
            }
            gl.setCaption("GridLayout_" + caption);
            gl.setDescription(gl.getCaption());
            for (int x = 0; x < gl.getColumns(); x++) {
                for (int y = 0; y < gl.getRows(); y++) {
                    // gl.addComponent(getExamplePicture("x=" + x + ", y=" + y),
                    // x, y);
                    gl.addComponent(new Label("x=" + x + ", y=" + y));
                }
            }
            result = gl;
            break;
        case 3:
            result = new Panel();
            ((Panel) result).setCaption("Panel_" + caption);
            break;
        case 4:
            final TabSheet ts = new TabSheet();
            ts.setCaption("TabSheet_" + caption);
            // randomly select one of the tabs
            final int selectedTab = seededRandom.nextInt(3);
            final ArrayList tabs = new ArrayList();
            for (int i = 0; i < 3; i++) {
                String tabCaption = "tab" + i;
                if (selectedTab == i) {
                    tabCaption = "tabX";
                }
                tabs.add(new OrderedLayout());
                ts.addTab((ComponentContainer) tabs.get(tabs.size() - 1),
                        tabCaption, null);
            }
            ts.setSelectedTab((ComponentContainer) tabs.get(selectedTab));
            result = ts;
            break;
        }

        return result;
    }

    public AbstractComponent getRandomComponent(int caption) {
        AbstractComponent result = null;
        int randint = seededRandom.nextInt(23);
        MultiListener l = new MultiListener();
        switch (randint) {
        case 0:
            // Label
            result = new Label();
            result.setCaption("Label component " + caption);
            break;
        case 1:
            // Button
            result = new Button();
            result.setCaption("Button component " + caption);
            // some listeners
            ((Button) result).addListener((Button.ClickListener) l);
            break;
        case 2:
            // TextField
            result = new TextField();
            result.setCaption("TextField component " + caption);
            break;
        case 3:
            // Select
            result = new Select("Select component " + caption);
            result.setCaption("Select component " + caption);
            result.setImmediate(true);
            ((Select) result).setNewItemsAllowed(true);
            // items
            ((Select) result).addItem("first");
            ((Select) result).addItem("first");
            ((Select) result).addItem("first");
            ((Select) result).addItem("second");
            ((Select) result).addItem("third");
            ((Select) result).addItem("fourth");
            // some listeners
            ((Select) result).addListener((ValueChangeListener) l);
            ((Select) result).addListener((PropertySetChangeListener) l);
            ((Select) result).addListener((ItemSetChangeListener) l);
            break;
        case 4:
            // Link
            result = new Link("", new ExternalResource("http://www.vaadin.com"));
            result.setCaption("Link component " + caption);
            break;
        case 5:
            // Link
            result = new Panel();
            result.setCaption("Panel component " + caption);
            ((Panel) result)
                    .addComponent(new Label(
                            "Panel is a container for other components, by default it draws a frame around it's "
                                    + "extremities and may have a caption to clarify the nature of the contained components' purpose."
                                    + " Panel contains an layout where the actual contained components are added, "
                                    + "this layout may be switched on the fly."));
            ((Panel) result).setWidth(250);
            break;
        case 6:
            // Datefield
            result = new DateField();
            ((DateField) result).setStyleName("calendar");
            ((DateField) result).setValue(new java.util.Date());
            result.setCaption("Calendar component " + caption);
            break;
        case 7:
            // Datefield
            result = new DateField();
            ((DateField) result).setValue(new java.util.Date());
            result.setCaption("Calendar component " + caption);
            break;
        case 8:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new ButtonExample());
            break;
        case 9:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new ClientCachingExample());
            break;
        case 10:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new ComboBoxExample());
            break;
        case 11:
            result = new OrderedLayout();
            // TODO: disabled gwt bug with mixed up iframe's
            ((OrderedLayout) result).addComponent(new EmbeddedBrowserExample());
            break;
        case 12:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new JavaScriptAPIExample());
            break;
        case 13:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new LabelExample());
            break;
        case 14:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new LayoutExample());
            break;
        case 15:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new NotificationExample());
            break;
        case 16:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new RichTextExample());
            break;
        case 17:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new SelectExample());
            break;
        case 18:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new ValueInputExample());
            break;
        case 19:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new WindowingExample());
            break;
        case 20:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new TreeExample());
            break;
        case 21:
            result = new OrderedLayout();
            ((OrderedLayout) result).addComponent(new TableExample());
            break;
        case 22:
            result = new OrderedLayout();
            ((OrderedLayout) result)
                    .addComponent(new StressComponentsInTable());
            break;
        }

        return result;
    }

    /**
     * Add demo components to given container
     * 
     * @param container
     */
    public void fillLayout(ComponentContainer container, int numberOfComponents) {
        for (int i = 0; i < numberOfComponents; i++) {
            container.addComponent(getRandomComponent(i));
        }
    }

    public AbstractComponent getExamplePicture(String caption) {
        final ThemeResource res = new ThemeResource("test.png");
        final Embedded em = new Embedded("Embedded " + caption, res);
        return em;
    }

}
