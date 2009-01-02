/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import java.util.Date;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;

public class FeatureGridLayout extends Feature {

    public FeatureGridLayout() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final GridLayout gl = new GridLayout(3, 3);
        final DateField cal = new DateField("Test component 1", new Date());
        cal.setStyle("calendar");
        gl.addComponent(cal, 1, 0, 2, 1);
        for (int i = 2; i < 7; i++) {
            gl.addComponent(new TextField("Test component " + i));
        }
        l.addComponent(gl);

        // Properties
        propertyPanel = new PropertyPanel(gl);
        final Form ap = propertyPanel.createBeanPropertySet(new String[] {
                "width", "height" });
        ap.addField("new line", new Button("New Line", gl, "newLine"));
        ap.addField("space", new Button("Space", gl, "space"));
        propertyPanel.addProperties("GridLayout Features", ap);

        setJavadocURL("ui/GridLayout.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "GridLayout gl = new GridLayout(2,2);\n"
                + "gl.addComponent(new Label(\"Label 1 in GridLayout\"));\n"
                + "gl.addComponent(new Label(\"Label 2 in GridLayout\"));\n"
                + "gl.addComponent(new Label(\"Label 3 in GridLayout\"));\n"
                + "gl.addComponent(new Label(\"Label 4 in GridLayout\"));\n";
    }

    /**
     * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
    @Override
    protected String getDescriptionXHTML() {
        return "This feature provides a container that lays out components "
                + "into a grid of given width and height."
                + "<br /><br />On the demo tab you can try out how the different "
                + "properties affect the presentation of the component.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "GridLayout";
    }
}