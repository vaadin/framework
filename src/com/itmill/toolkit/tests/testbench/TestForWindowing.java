/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.testbench;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Slider;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class TestForWindowing extends CustomComponent {

    private Select s2;

    public TestForWindowing() {

        final OrderedLayout main = new OrderedLayout();

        main.addComponent(new Label(
                "Click the button to create a new inline window."));

        final Button create = new Button("Create a new window",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        Window w = new Window("Testing Window");

                        AbstractSelect s1 = new OptionGroup();
                        s1.setCaption("1. Select output format");
                        s1.addItem("Excel sheet");
                        s1.addItem("CSV plain text");
                        s1.setValue("Excel sheet");

                        s2 = new Select();
                        s2.addItem("Separate by comma (,)");
                        s2.addItem("Separate by colon (:)");
                        s2.addItem("Separate by semicolon (;)");
                        s2.setEnabled(false);

                        s1.addListener(new ValueChangeListener() {

                            public void valueChange(ValueChangeEvent event) {
                                String v = (String) event.getProperty()
                                        .getValue();
                                if (v.equals("CSV plain text")) {
                                    s2.setEnabled(true);
                                } else {
                                    s2.setEnabled(false);
                                }
                            }

                        });

                        w.addComponent(s1);
                        w.addComponent(s2);

                        Slider s = new Slider();
                        s.setCaption("Volume");
                        s.setMax(13);
                        s.setMin(12);
                        s.setResolution(2);
                        s.setImmediate(true);
                        // s.setOrientation(Slider.ORIENTATION_VERTICAL);
                        // s.setArrows(false);

                        w.addComponent(s);

                        getApplication().getMainWindow().addWindow(w);

                    }

                });

        main.addComponent(create);

        setCompositionRoot(main);

    }

}
