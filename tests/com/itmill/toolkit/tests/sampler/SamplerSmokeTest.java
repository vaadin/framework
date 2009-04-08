package com.itmill.toolkit.tests.sampler;

import com.itmill.testingtools.runner.TestRunner;

public class SamplerSmokeTest extends TestRunner {

    public void testNew() throws Exception {
        selenium.open("/sampler?restartApplication");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IPanel[0]/IGridLayout[0]/AbsolutePanel[0]/ChildComponentContainer[1]/IButton[0]");
        waitForITMillToolkit();
        assertEquals(
                "Tooltips",
                selenium
                        .getText("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]"));
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[3]/IButton[0]");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[3]/IButton[0]");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[0]/IHorizontalLayout[0]/ChildComponentContainer[2]/IActiveLink[0]/domChild[0]/domChild[0]");
        waitForITMillToolkit();
        verifyTrue(selenium
                .isTextPresent("'m terribly sorry, but it seems the source could not be found.\nPlease try adding the source folder to the classpath for your server, or tell the administrator to do so!"));
        waitForITMillToolkit();
        selenium.click("PID141_window_close");
        waitForITMillToolkit();
    }
}
