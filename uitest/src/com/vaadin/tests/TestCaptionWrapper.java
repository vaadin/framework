/* 
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Select;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TestCaptionWrapper extends CustomComponent implements Listener {

    VerticalLayout main = new VerticalLayout();

    final String eventListenerString = "Component.Listener feedback: ";
    Label eventListenerFeedback = new Label(eventListenerString
            + " <no events occured>");
    int count = 0;

    public TestCaptionWrapper() {
        setCompositionRoot(main);
    }

    @Override
    public void attach() {
        super.attach();
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();

        main.addComponent(new Label(
                "Each Layout and their contained components should "
                        + "have icon, caption, description, user error defined. "
                        + "Eeach layout should contain similar components."));

        main.addComponent(eventListenerFeedback);

        main.addComponent(new Label("OrderedLayout"));
        test(main);
        populateLayout(main);

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        final Panel panel = new Panel("Panel", panelLayout);
        test(panel);
        populateLayout(panelLayout);

        final TabSheet tabsheet = new TabSheet();
        test(tabsheet);
        final VerticalLayout tab1 = new VerticalLayout();
        tab1.addComponent(new Label("try tab2"));
        final VerticalLayout tab2 = new VerticalLayout();
        test(tab2);
        populateLayout(tab2);
        tabsheet.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif"));
        tabsheet.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif"));

        final VerticalLayout expandLayout = new VerticalLayout();
        test(expandLayout);
        populateLayout(expandLayout);

        final GridLayout gridLayout = new GridLayout();
        test(gridLayout);
        populateLayout(gridLayout);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        final Window window = new Window("TEST: Window", layout);
        test(window);
        populateLayout(layout);

    }

    void populateLayout(Layout layout) {

        final Button button = new Button("Button " + count++);
        test(layout, button);
        button.addListener(this);

        final DateField df = new DateField("DateField " + count++);
        test(layout, df);

        final CheckBox cb = new CheckBox("Checkbox " + count++);
        test(layout, cb);

        final Embedded emb = new Embedded("Embedded " + count++);
        test(layout, emb);

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        final Panel panel = new Panel("Panel " + count++, panelLayout);
        test(layout, panel);

        final Label label = new Label("Label " + count++);
        test(layout, label);

        final Link link = new Link("Link " + count++, new ExternalResource(
                "www.vaadin.com"));
        test(layout, link);

        final NativeSelect nativeSelect = new NativeSelect("NativeSelect "
                + count++);
        test(layout, nativeSelect);

        final OptionGroup optionGroup = new OptionGroup("OptionGroup "
                + count++);
        test(layout, optionGroup);

        final ProgressIndicator pi = new ProgressIndicator();
        test(layout, pi);

        final RichTextArea rta = new RichTextArea();
        test(layout, rta);

        final Select select = new Select("Select " + count++);
        test(layout, select);

        final Slider slider = new Slider("Slider " + count++);
        test(layout, slider);

        final Table table = new Table("Table " + count++);
        test(layout, table);

        final TextField tf = new TextField("Textfield " + count++);
        test(layout, tf);

        final Tree tree = new Tree("Tree " + count++);
        test(layout, tree);

        final TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect "
                + count++);
        test(layout, twinColSelect);

        final Upload upload = new Upload("Upload (non-functional)", null);
        test(layout, upload);

        // Custom components
        layout.addComponent(new Label("<B>Below are few custom components</B>",
                ContentMode.HTML));
        final TestForUpload tfu = new TestForUpload();
        layout.addComponent(tfu);

    }

    /**
     * Stresses component by configuring it
     * 
     * @param c
     */
    void test(AbstractComponent c) {
        final ClassResource res = new ClassResource("m.gif");
        final ErrorMessage errorMsg = new UserError("User error " + c);

        if ((c.getCaption() == null) || (c.getCaption().length() <= 0)) {
            c.setCaption("Caption " + c);
        }
        c.setDescription("Description " + c);
        c.setComponentError(errorMsg);
        c.setIcon(res);
    }

    /**
     * Stresses component by configuring it in a given layout
     * 
     * @param c
     */
    void test(Layout layout, AbstractComponent c) {
        test(c);
        layout.addComponent(c);
    }

    @Override
    public void componentEvent(Event event) {
        final String feedback = eventListenerString + " source="
                + event.getSource() + ", toString()=" + event.toString();
        System.out.println("eventListenerFeedback: " + feedback);
        eventListenerFeedback.setValue(feedback);
    }

}
