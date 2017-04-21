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
package com.vaadin.test.webcomponent.app;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelRunner;
import com.vaadin.testbench.parallel.ParallelTest;

@RunOnHub("tb3-hub.intra.itmill.com")
@RunWith(ParallelRunner.class)
@BrowserFactory(VaadinBrowserFactory.class)
public class WebComponentAppIT extends ParallelTest {

    @BrowserConfiguration
    public final List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsers().map(BrowserUtil.getBrowserFactory()::create)
                .collect(Collectors.toList());
    }

    protected Stream<Browser> getBrowsers() {
        // Firefox currently excluded because it fails on sliderBar.click() with
        // "e.elementFromPoint is not a function"
        return Stream.of(Browser.CHROME, Browser.IE11);
    }

    /**
     * Tries to automatically determine the IP address of the machine the test
     * is running on. Only considers site local addresses and not public IPs,
     * i.e. 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16.
     *
     * @return An IP address of one of the network interfaces in the machine.
     * @throws RuntimeException
     *             if there was an error or no IP was found
     */
    public static String findHostname() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nwInterface = interfaces.nextElement();
                if (!nwInterface.isUp() || nwInterface.isLoopback()
                        || nwInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nwInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        throw new RuntimeException(
                "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) ip address found.");
    }

    @Test
    public void componentVisible() throws Exception {
        getDriver().get("http://" + findHostname() + ":8080/app");

        PaperSliderElement slider = wrap(PaperSliderElement.class,
                findElement(By.id("slider")));
        Assert.assertEquals(23.0, slider.getValue(), 0.0);

        // Click in the middle of the bar to set the value to 50
        WebElement bar = slider.getSliderBar();
        bar.click();
        Assert.assertEquals(50.0, slider.getValue(), 0.0);
    }

}
