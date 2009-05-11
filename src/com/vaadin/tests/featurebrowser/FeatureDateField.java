/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import java.util.Locale;

import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Select;

public class FeatureDateField extends Feature {

    static private String[] localeNames;
    static {
        final Locale[] locales = Locale.getAvailableLocales();
        localeNames = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeNames[i] = locales[i].getDisplayName();
        }
    }

    public FeatureDateField() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        l.addComponent(new Label("Your default locale is: "
                + getApplication().getLocale().toString().replace('_', '-')));

        final DateField df = new DateField();
        df.setValue(new java.util.Date());
        l.addComponent(df);

        // Properties
        propertyPanel = new PropertyPanel(df);
        final Form ap = propertyPanel.createBeanPropertySet(new String[] {
                "resolution", "locale" });
        ap.replaceWithSelect("resolution", new Object[] {
                new Integer(DateField.RESOLUTION_YEAR),
                new Integer(DateField.RESOLUTION_MONTH),
                new Integer(DateField.RESOLUTION_DAY),
                new Integer(DateField.RESOLUTION_HOUR),
                new Integer(DateField.RESOLUTION_MIN),
                new Integer(DateField.RESOLUTION_SEC),
                new Integer(DateField.RESOLUTION_MSEC) }, new Object[] {
                "Year", "Month", "Day", "Hour", "Minute", "Second",
                "Millisecond" });
        ap.replaceWithSelect("locale", Locale.getAvailableLocales(),
                localeNames);
        ap.getField("resolution").setValue(
                new Integer(DateField.RESOLUTION_DAY));
        ap.getField("locale").setValue(Locale.getDefault());
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("text").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("text");
        themes.addItem("calendar").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("calendar");
        propertyPanel.addProperties("DateField Properties", ap);

        setJavadocURL("ui/DateField.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "DateField df = new DateField(\"Caption\");\n"
                + "df.setValue(new java.util.Date());\n";
    }

    @Override
    protected String getDescriptionXHTML() {
        return "Representing Dates and times and providing a way to select "
                + "or enter some specific date and/or time is an typical need in "
                + "data-entry user interfaces (UI). IT Mill Toolkit provides a DateField "
                + "component that is intuitive to use and yet controllable through "
                + "its properties."
                + "<br /><br />The calendar-style allows point-and-click selection "
                + "of dates while text-style shows only minimalistic user interface."
                + " Validators may be bound to the component to check and "
                + "validate the given input."
                + "<br /><br />On the demo tab you can try out how the different properties affect the "
                + "presentation of the component.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "DateField";
    }

}
