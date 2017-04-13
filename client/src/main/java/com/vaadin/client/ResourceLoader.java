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

package com.vaadin.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Timer;

/**
 * ResourceLoader lets you dynamically include external scripts and styles on
 * the page and lets you know when the resource has been loaded.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class ResourceLoader {
    /**
     * Event fired when a resource has been loaded.
     */
    public static class ResourceLoadEvent {
        private final ResourceLoader loader;
        private final String resourceUrl;

        /**
         * Creates a new event.
         *
         * @param loader
         *            the resource loader that has loaded the resource
         * @param resourceUrl
         *            the url of the loaded resource
         */
        public ResourceLoadEvent(ResourceLoader loader, String resourceUrl) {
            this.loader = loader;
            this.resourceUrl = resourceUrl;
        }

        /**
         * Gets the resource loader that has fired this event.
         *
         * @return the resource loader
         */
        public ResourceLoader getResourceLoader() {
            return loader;
        }

        /**
         * Gets the absolute url of the loaded resource.
         *
         * @return the absolute url of the loaded resource
         */
        public String getResourceUrl() {
            return resourceUrl;
        }

    }

    /**
     * Event listener that gets notified when a resource has been loaded.
     */
    public interface ResourceLoadListener {
        /**
         * Notifies this ResourceLoadListener that a resource has been loaded.
         * Some browsers do not support any way of detecting load errors. In
         * these cases, onLoad will be called regardless of the status.
         *
         * @see ResourceLoadEvent
         *
         * @param event
         *            a resource load event with information about the loaded
         *            resource
         */
        public void onLoad(ResourceLoadEvent event);

        /**
         * Notifies this ResourceLoadListener that a resource could not be
         * loaded, e.g. because the file could not be found or because the
         * server did not respond. Some browsers do not support any way of
         * detecting load errors. In these cases, onLoad will be called
         * regardless of the status.
         *
         * @see ResourceLoadEvent
         *
         * @param event
         *            a resource load event with information about the resource
         *            that could not be loaded.
         */
        public void onError(ResourceLoadEvent event);
    }

    private static final ResourceLoader INSTANCE = GWT
            .create(ResourceLoader.class);

    private ApplicationConnection connection;

    private final Set<String> loadedResources = new HashSet<>();

    private final Map<String, Collection<ResourceLoadListener>> loadListeners = new HashMap<>();

    private final Element head;

    /**
     * Creates a new resource loader. You should generally not create you own
     * resource loader, but instead use {@link ResourceLoader#get()} to get an
     * instance.
     */
    protected ResourceLoader() {
        Document document = Document.get();
        head = document.getElementsByTagName("head").getItem(0);

        // detect already loaded scripts, html imports and stylesheets
        NodeList<Element> scripts = document.getElementsByTagName("script");
        for (int i = 0; i < scripts.getLength(); i++) {
            ScriptElement element = ScriptElement.as(scripts.getItem(i));
            String src = element.getSrc();
            if (src != null && src.length() != 0) {
                loadedResources.add(src);
            }
        }

        NodeList<Element> links = document.getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            LinkElement linkElement = LinkElement.as(links.getItem(i));
            String rel = linkElement.getRel();
            String href = linkElement.getHref();
            if ("stylesheet".equalsIgnoreCase(rel) && href != null
                    && href.length() != 0) {
                loadedResources.add(href);
            }
            if ("import".equalsIgnoreCase(rel) && href != null
                    && href.length() != 0) {
                loadedResources.add(href);
            }
        }
    }

    /**
     * Returns the default ResourceLoader
     *
     * @return the default ResourceLoader
     */
    public static ResourceLoader get() {
        return INSTANCE;
    }

    /**
     * Load a script and notify a listener when the script is loaded. Calling
     * this method when the script is currently loading or already loaded
     * doesn't cause the script to be loaded again, but the listener will still
     * be notified when appropriate.
     *
     * @param scriptUrl
     *            the url of the script to load
     * @param resourceLoadListener
     *            the listener that will get notified when the script is loaded
     */
    public void loadScript(final String scriptUrl,
            final ResourceLoadListener resourceLoadListener) {
        final String url = WidgetUtil.getAbsoluteUrl(scriptUrl);
        ResourceLoadEvent event = new ResourceLoadEvent(this, url);
        if (loadedResources.contains(url)) {
            if (resourceLoadListener != null) {
                resourceLoadListener.onLoad(event);
            }
            return;
        }

        if (addListener(url, resourceLoadListener, loadListeners)) {
            getLogger().info("Loading script from " + url);
            ScriptElement scriptTag = Document.get().createScriptElement();
            scriptTag.setSrc(url);
            scriptTag.setType("text/javascript");

            // async=false causes script injected scripts to be executed in the
            // injection order. See e.g.
            // https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script
            scriptTag.setPropertyBoolean("async", false);

            addOnloadHandler(scriptTag, new ResourceLoadListener() {
                @Override
                public void onLoad(ResourceLoadEvent event) {
                    fireLoad(event);
                }

                @Override
                public void onError(ResourceLoadEvent event) {
                    fireError(event);
                }
            }, event);
            head.appendChild(scriptTag);
        }
    }

    /**
     * Loads an HTML import and notify a listener when the HTML import is
     * loaded. Calling this method when the HTML import is currently loading or
     * already loaded doesn't cause the HTML import to be loaded again, but the
     * listener will still be notified when appropriate.
     *
     * @param htmlUrl
     *            url of HTML import to load
     * @param resourceLoadListener
     *            listener to notify when the HTML import is loaded
     */
    public void loadHtmlImport(final String htmlUrl,
            final ResourceLoadListener resourceLoadListener) {
        final String url = WidgetUtil.getAbsoluteUrl(htmlUrl);
        ResourceLoadEvent event = new ResourceLoadEvent(this, url);
        if (loadedResources.contains(url)) {
            if (resourceLoadListener != null) {
                resourceLoadListener.onLoad(event);
            }
            return;
        }

        if (addListener(url, resourceLoadListener, loadListeners)) {
            LinkElement linkTag = Document.get().createLinkElement();
            linkTag.setAttribute("rel", "import");
            linkTag.setAttribute("href", url);

            addOnloadHandler(linkTag, new ResourceLoadListener() {
                @Override
                public void onLoad(ResourceLoadEvent event) {
                    fireLoad(event);
                }

                @Override
                public void onError(ResourceLoadEvent event) {
                    fireError(event);
                }
            }, event);
            head.appendChild(linkTag);
        }
    }

    /**
     * Adds an onload listener to the given element, which should be a link or a
     * script tag. The listener is called whenever loading is complete or an
     * error occurred.
     *
     * @since 7.3
     * @param element
     *            the element to attach a listener to
     * @param listener
     *            the listener to call
     * @param event
     *            the event passed to the listener
     */
    public static native void addOnloadHandler(Element element,
            ResourceLoadListener listener, ResourceLoadEvent event)
    /*-{
        element.onload = $entry(function() {
            element.onload = null;
            element.onerror = null;
            element.onreadystatechange = null;
            listener.@com.vaadin.client.ResourceLoader.ResourceLoadListener::onLoad(Lcom/vaadin/client/ResourceLoader$ResourceLoadEvent;)(event);
        });
        element.onerror = $entry(function() {
            element.onload = null;
            element.onerror = null;
            element.onreadystatechange = null;
            listener.@com.vaadin.client.ResourceLoader.ResourceLoadListener::onError(Lcom/vaadin/client/ResourceLoader$ResourceLoadEvent;)(event);
        });
        element.onreadystatechange = function() {
            if ("loaded" === element.readyState || "complete" === element.readyState ) {
                element.onload(arguments[0]);
            }
        };
    }-*/;

    /**
     * Load a stylesheet and notify a listener when the stylesheet is loaded.
     * Calling this method when the stylesheet is currently loading or already
     * loaded doesn't cause the stylesheet to be loaded again, but the listener
     * will still be notified when appropriate.
     *
     * @param stylesheetUrl
     *            the url of the stylesheet to load
     * @param resourceLoadListener
     *            the listener that will get notified when the stylesheet is
     *            loaded
     */
    public void loadStylesheet(final String stylesheetUrl,
            final ResourceLoadListener resourceLoadListener) {
        final String url = WidgetUtil.getAbsoluteUrl(stylesheetUrl);
        final ResourceLoadEvent event = new ResourceLoadEvent(this, url);
        if (loadedResources.contains(url)) {
            if (resourceLoadListener != null) {
                resourceLoadListener.onLoad(event);
            }
            return;
        }

        if (addListener(url, resourceLoadListener, loadListeners)) {
            getLogger().info("Loading style sheet from " + url);
            LinkElement linkElement = Document.get().createLinkElement();
            linkElement.setRel("stylesheet");
            linkElement.setType("text/css");
            linkElement.setHref(url);

            if (BrowserInfo.get().isSafari()) {
                // Safari doesn't fire any events for link elements
                // See http://www.phpied.com/when-is-a-stylesheet-really-loaded/
                Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
                    private final Duration duration = new Duration();

                    @Override
                    public boolean execute() {
                        int styleSheetLength = getStyleSheetLength(url);
                        if (getStyleSheetLength(url) > 0) {
                            fireLoad(event);
                            return false; // Stop repeating
                        } else if (styleSheetLength == 0) {
                            // "Loaded" empty sheet -> most likely 404 error
                            fireError(event);
                            return true;
                        } else if (duration.elapsedMillis() > 60 * 1000) {
                            fireError(event);
                            return false;
                        } else {
                            return true; // Continue repeating
                        }
                    }
                }, 10);
            } else {
                addOnloadHandler(linkElement, new ResourceLoadListener() {
                    @Override
                    public void onLoad(ResourceLoadEvent event) {
                        // Chrome, IE, Edge all fire load for errors, must check
                        // stylesheet data
                        if (BrowserInfo.get().isChrome()
                                || BrowserInfo.get().isIE()
                                || BrowserInfo.get().isEdge()) {
                            int styleSheetLength = getStyleSheetLength(url);
                            // Error if there's an empty stylesheet
                            if (styleSheetLength == 0) {
                                fireError(event);
                                return;
                            }
                        }
                        fireLoad(event);
                    }

                    @Override
                    public void onError(ResourceLoadEvent event) {
                        fireError(event);
                    }
                }, event);
                if (BrowserInfo.get().isOpera()) {
                    // Opera onerror never fired, assume error if no onload in x
                    // seconds
                    new Timer() {
                        @Override
                        public void run() {
                            if (!loadedResources.contains(url)) {
                                fireError(event);
                            }
                        }
                    }.schedule(5 * 1000);
                }
            }

            head.appendChild(linkElement);
        }
    }

    private static native int getStyleSheetLength(String url)
    /*-{
        for(var i = 0; i < $doc.styleSheets.length; i++) {
            if ($doc.styleSheets[i].href === url) {
                var sheet = $doc.styleSheets[i];
                try {
                    var rules = sheet.cssRules
                    if (rules === undefined) {
                        rules = sheet.rules;
                    }

                    if (rules === null) {
                        // Style sheet loaded, but can't access length because of XSS -> assume there's something there
                        return 1;
                    }

                    // Return length so we can distinguish 0 (probably 404 error) from normal case.
                    return rules.length;
                } catch (err) {
                    return 1;
                }
            }
        }
        // No matching stylesheet found -> not yet loaded
        return -1;
    }-*/;

    private static boolean addListener(String url,
            ResourceLoadListener listener,
            Map<String, Collection<ResourceLoadListener>> listenerMap) {
        Collection<ResourceLoadListener> listeners = listenerMap.get(url);
        if (listeners == null) {
            listeners = new ArrayList<>();
            listeners.add(listener);
            listenerMap.put(url, listeners);
            return true;
        } else {
            listeners.add(listener);
            return false;
        }
    }

    private void fireError(ResourceLoadEvent event) {
        String resource = event.getResourceUrl();

        Collection<ResourceLoadListener> listeners = loadListeners
                .remove(resource);
        if (listeners != null && !listeners.isEmpty()) {
            for (ResourceLoadListener listener : listeners) {
                if (listener != null) {
                    listener.onError(event);
                }
            }
        }
    }

    private void fireLoad(ResourceLoadEvent event) {
        String resource = event.getResourceUrl();
        Collection<ResourceLoadListener> listeners = loadListeners
                .remove(resource);
        loadedResources.add(resource);
        if (listeners != null && !listeners.isEmpty()) {
            for (ResourceLoadListener listener : listeners) {
                if (listener != null) {
                    listener.onLoad(event);
                }
            }
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(ResourceLoader.class.getName());
    }
}
