/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.util.Locale;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestDateField extends CustomComponent {

    OrderedLayout main = new OrderedLayout();

    DateField df;

    public TestDateField() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label("DateField"));

        df = new DateField();
        main.addComponent(df);

        final ErrorMessage errorMsg = new UserError("User error " + df);
        df.setCaption("DateField caption " + df);
        df.setDescription("DateField description " + df);
        df.setComponentError(errorMsg);
        df.setImmediate(true);
        // FIXME: bug #1138 this makes datefield to render with unknown
        // component (UIDL tree debug)
        df.addStyleName("thisShouldBeHarmless");

        // Another test: locale
        final DateField df1 = new DateField("US locale");
        main.addComponent(df1);
        df1.setLocale(new Locale("en", "US"));

        final DateField df2 = new DateField("DE locale");
        main.addComponent(df2);
        df2.setLocale(new Locale("de", "DE"));

        final DateField df3 = new DateField("RU locale");
        main.addComponent(df3);
        df3.setLocale(new Locale("ru", "RU"));

        final DateField df4 = new DateField("FI locale");
        main.addComponent(df4);
        df4.setLocale(new Locale("fi", "FI"));
    }

    public void attach() {
        final ClassResource res = new ClassResource("m.gif", super
                .getApplication());
        df.setIcon(res);
        super.attach();
    }

}
