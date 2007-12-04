/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;

public class FeatureSelect extends Feature {

    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
            "Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
            "Linus" };

    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
            "McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
            "Fielding", "Einstein" };

    public FeatureSelect() {
        super();
    }

    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Select s = new Select("Select employee");
        for (int i = 0; i < 50; i++) {
            s
                    .addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (Math.random() * (lastnames.length - 1))]);
        }
        l.addComponent(s);

        // Properties
        propertyPanel = new PropertyPanel(s);
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("optiongroup").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("optiongroup");
        themes.addItem("twincol").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("twincol");

        setJavadocURL("ui/Select.html");

        return l;
    }

    protected String getExampleSrc() {
        return "Select s = new Select(\"Select Car\");\n"
                + "s.addItem(\"Audi\");\n" + "s.addItem(\"BMW\");\n"
                + "s.addItem(\"Chrysler\");\n" + "s.addItem(\"Volvo\");\n";

    }

    /**
     * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
    protected String getDescriptionXHTML() {
        return "The select component combines two different modes of item selection.  "
                + "Firstly it presents the single selection mode, which is usually represented as "
                + "either a drop-down menu or a radio-group of switches, secondly it "
                + "allows for multiple item selection, this is usually represented as either a "
                + "listbox of selectable items or as a group of checkboxes."
                + "<br/><br/>"
                + "Data source can be associated both with selected item and the list of selections. "
                + "This way you can easily present a selection based on items specified elsewhere in application. "
                + "<br/><br/>"
                + "On the demo tab you can try out how the different properties affect the"
                + " presentation of the component.";
    }

    protected String getImage() {
        return "icon_demo.png";
    }

    protected String getTitle() {
        return "Select";
    }

}
