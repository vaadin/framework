package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.remote.DesiredCapabilities;

@RunWith(value = Parameterized.class)
public abstract class MultiBrowserTest extends AbstractTest {

    private DesiredCapabilities desiredCapabilities;
    private static List<DesiredCapabilities> browsers = new ArrayList<DesiredCapabilities>();
    static {
        DesiredCapabilities ie8 = DesiredCapabilities.internetExplorer();
        ie8.setVersion("8");
        DesiredCapabilities ie9 = DesiredCapabilities.internetExplorer();
        ie9.setVersion("9");
        DesiredCapabilities ie10 = DesiredCapabilities.internetExplorer();
        ie10.setVersion("10");
        DesiredCapabilities firefox17esr = DesiredCapabilities.firefox();
        firefox17esr.setVersion("17");
        DesiredCapabilities safari6 = DesiredCapabilities.safari();
        safari6.setVersion("6");
        DesiredCapabilities chrome21 = DesiredCapabilities.chrome();
        // chrome21.setVersion("21");
        DesiredCapabilities opera12 = DesiredCapabilities.opera();
        opera12.setVersion("12");

        browsers.add(ie8);
        browsers.add(ie9);
        browsers.add(ie10);
        browsers.add(firefox17esr);
        // browsers.add(safari6);
        browsers.add(chrome21);
        browsers.add(opera12);
    }

    public MultiBrowserTest(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> params = new ArrayList<Object[]>();
        for (DesiredCapabilities c : browsers) {
            params.add(new Object[] { c });
        }
        return params;
    }
}
