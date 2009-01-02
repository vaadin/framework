/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;

public class FeatureTextField extends Feature {

    public FeatureTextField() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        // Test component
        final TextField tf = new TextField("Caption");
        l.addComponent(tf);

        // Properties
        propertyPanel = new PropertyPanel(tf);
        final Form f = propertyPanel.createBeanPropertySet(new String[] {
                "columns", "rows", "wordwrap", "writeThrough", "readThrough",
                "nullRepresentation", "nullSettingAllowed", "secret" });
        propertyPanel.addProperties("Text field properties", f);

        setJavadocURL("ui/TextField.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "TextField tf = new TextField(\"Caption\");\n"
                + "tf.setValue(\"Contents\");";
    }

    /**
     * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
    @Override
    protected String getDescriptionXHTML() {
        return "TextField combines the logic of both the single line text-entry field and the multi-line "
                + "text-area into one component. "
                + "As with all Data-components of IT Mill Toolkit, the TextField can also be bound to an "
                + "underlying data source, both directly or in a buffered (asynchronous) "
                + "mode. In buffered mode its background color will change to indicate "
                + "that the value has changed but is not committed."
                + "<br /><br />Furthermore a validators may be bound to the component to "
                + "check and validate the given input before it is actually committed."
                + "<br /><br />On the demo tab you can try out how the different properties affect the "
                + "presentation of the component.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "TextField";
    }

}
