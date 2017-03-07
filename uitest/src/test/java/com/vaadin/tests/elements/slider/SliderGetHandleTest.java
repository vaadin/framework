/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.elements.slider;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SliderGetHandleTest extends MultiBrowserTest {

    @Test
    public void testGetHandle() {
        openTestURL();

        // get second slider, to check that getHandler get the handler
        // of the correct slider, not of the very first one
        SliderElement slider = $(SliderElement.class).get(1);
        WebElement handle = slider.getHandle();
        slider.isDisplayed();
        handle.isDisplayed();
        // Handle has 0*0 size in Valo theme and requires some special treatment
        ((JavascriptExecutor) driver).executeScript(
                "function simulate(f,c,d,e){var b,a=null;for(b in eventMatchers)if(eventMatchers[b].test(c)){a=b;break}if(!a)return!1;document.createEvent?(b=document.createEvent(a),a==\"HTMLEvents\"?b.initEvent(c,!0,!0):b.initMouseEvent(c,!0,!0,document.defaultView,0,d,e,d,e,!1,!1,!1,!1,0,null),f.dispatchEvent(b)):(a=document.createEventObject(),a.detail=0,a.screenX=d,a.screenY=e,a.clientX=d,a.clientY=e,a.ctrlKey=!1,a.altKey=!1,a.shiftKey=!1,a.metaKey=!1,a.button=1,f.fireEvent(\"on\"+c,a));return!0} var eventMatchers={HTMLEvents:/^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,MouseEvents:/^(?:click|dblclick|mouse(?:down|up|over|move|out))$/}; "
                        + "simulate(arguments[0],\"mousedown\",0,0); simulate(arguments[0],\"mousemove\",arguments[1],arguments[2]); simulate(arguments[0],\"mouseup\",arguments[1],arguments[2]); ",
                handle, handle.getLocation().x + 10, 0);
        // action to do the same test using a theme other than Valo
        // new Actions(driver).clickAndHold(handle).moveByOffset(10,
        // 0).release().perform();

        String initial = "" + (int) SliderGetHandle.INITIAL_VALUE;
        String actual = slider.getValue();
        Assert.assertNotEquals(initial, actual);
    }

}
