/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.IOException;
import java.io.Writer;

/**
 * A {@link RequestHandler} that presents an informative page if the browser in
 * use is unsupported. Recognizes Chrome Frame and allow it to be used.
 * 
 * <p>
 * This handler is usually added to the application by
 * {@link LegacyCommunicationManager}.
 * </p>
 */
@SuppressWarnings("serial")
public class UnsupportedBrowserHandler extends SynchronizedRequestHandler {

    /** Cookie used to ignore browser checks */
    public static final String FORCE_LOAD_COOKIE = "vaadinforceload=1";

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {

        // Check if the browser is supported
        // If Chrome Frame is available we'll assume it's ok
        WebBrowser b = session.getBrowser();
        if (b.isTooOldToFunctionProperly() && !b.isChromeFrameCapable()) {
            // bypass if cookie set
            String c = request.getHeader("Cookie");
            if (c == null || !c.contains(FORCE_LOAD_COOKIE)) {
                writeBrowserTooOldPage(request, response);
                return true; // request handled
            }
        }

        return false; // pass to next handler
    }

    /**
     * Writes a page encouraging the user to upgrade to a more current browser.
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    protected void writeBrowserTooOldPage(VaadinRequest request,
            VaadinResponse response) throws IOException {
        Writer page = response.getWriter();
        WebBrowser b = VaadinSession.getCurrent().getBrowser();

        page.write("<html><body><h1>I'm sorry, but your browser is not supported</h1>"
                + "<p>The version ("
                + b.getBrowserMajorVersion()
                + "."
                + b.getBrowserMinorVersion()
                + ") of the browser you are using "
                + " is outdated and not supported.</p>"
                + "<p>You should <b>consider upgrading</b> to a more up-to-date browser.</p> "
                + "<p>The most popular browsers are <b>"
                + " <a href=\"https://www.google.com/chrome\">Chrome</a>,"
                + " <a href=\"http://www.mozilla.com/firefox\">Firefox</a>,"
                + (b.isWindows() ? " <a href=\"http://windows.microsoft.com/en-US/internet-explorer/downloads/ie\">Internet Explorer</a>,"
                        : "")
                + " <a href=\"http://www.opera.com/browser\">Opera</a>"
                + " and <a href=\"http://www.apple.com/safari\">Safari</a>.</b><br/>"
                + "Upgrading to the latest version of one of these <b>will make the web safer, faster and better looking.</b></p>"
                + (b.isIE() ? "<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js\"></script>"
                        + "<p>If you can not upgrade your browser, please consider trying <a onclick=\"CFInstall.check({mode:'overlay'});return false;\" href=\"http://www.google.com/chromeframe\">Chrome Frame</a>.</p>"
                        : "") //
                + "<p><sub><a onclick=\"document.cookie='"
                + FORCE_LOAD_COOKIE
                + "';window.location.reload();return false;\" href=\"#\">Continue without updating</a> (not recommended)</sub></p>"
                + "</body>\n" + "</html>");

        page.close();
    }
}
