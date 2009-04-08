package com.itmill.toolkit.tests.sampler;

import com.itmill.testingtools.runner.TestRunner;

public class SamplerSmokeTest2 extends TestRunner {

    public void testNew() throws Exception {
        selenium.open("/sampler?restartApplication");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IPanel[0]/IGridLayout[0]/AbsolutePanel[0]/ChildComponentContainer[16]/IButton[0]");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[0]/IHorizontalLayout[0]/ChildComponentContainer[6]/IHorizontalLayout[0]/ChildComponentContainer[1]/IButton[0]");
        waitForITMillToolkit();
        selenium
                .type(
                        "itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/ITextField[0]",
                        "Peter");
        waitForITMillToolkit();
        selenium
                .type(
                        "itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/ITextField[1]",
                        "Person");
        waitForITMillToolkit();
        selenium
                .type(
                        "itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/IFilterSelect[0]/domChild[1]",
                        "finland");
        waitForITMillToolkit();
        selenium
                .type(
                        "itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/IPasswordField[0]",
                        "mypass");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/IPopupCalendar[0]/domChild[1]");
        waitForITMillToolkit();
        selenium
                .click("//table[@id='PID_TOOLKIT_POPUPCAL']/tbody/tr[4]/td[2]/span");
        waitForITMillToolkit();
        selenium
                .type(
                        "itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/ITextField[2]",
                        "45");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[0]/IForm[0]/IFormLayout[0]/IFormLayout$IFormLayoutTable[0]/IHorizontalLayout[0]/ChildComponentContainer[1]/IButton[0]");
        waitForITMillToolkit();
        selenium
                .click("itmilltoolkit=sampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IHorizontalLayout[0]/ChildComponentContainer[0]/IVerticalLayout[0]/ChildComponentContainer[1]/IVerticalLayout[0]/ChildComponentContainer[1]/IButton[0]");
        waitForITMillToolkit();
        verifyTrue(selenium
                .isTextPresent("First name: Peter\nLast name: Person\nCountry:"));
        verifyTrue(selenium.isTextPresent("Shoe size: 45\nPassword: mypass"));
    }
}
