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

import java.io.Serializable;
import java.util.Map;

import com.vaadin.server.StreamVariable.StreamingStartEvent;
import com.vaadin.ui.Component;

/**
 * This interface defines the methods for painting XML to the UIDL stream.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface PaintTarget extends Serializable {

    /**
     * Prints single XMLsection.
     * 
     * Prints full XML section. The section data is escaped from XML tags and
     * surrounded by XML start and end-tags.
     * 
     * @param sectionTagName
     *            the name of the tag.
     * @param sectionData
     *            the scetion data.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addSection(String sectionTagName, String sectionData)
            throws PaintException;

    /**
     * Result of starting to paint a Component (
     * {@link PaintTarget#startPaintable(Component, String)}).
     * 
     * @since 7.0
     */
    public enum PaintStatus {
        /**
         * Painting started, addVariable() and addAttribute() etc. methods may
         * be called.
         */
        PAINTING,
        /**
         * A previously unpainted or painted {@link Component} has been queued
         * be created/update later in a separate change in the same set of
         * changes.
         */
        CACHED
    }

    /**
     * Prints element start tag of a paintable section. Starts a paintable
     * section using the given tag. The PaintTarget may implement a caching
     * scheme, that checks the paintable has actually changed or can a cached
     * version be used instead. This method should call the startTag method.
     * <p>
     * If the {@link Component} is found in cache and this function returns true
     * it may omit the content and close the tag, in which case cached content
     * should be used.
     * </p>
     * <p>
     * This method may also add only a reference to the paintable and queue the
     * paintable to be painted separately.
     * </p>
     * <p>
     * Each paintable being painted should be closed by a matching
     * {@link #endPaintable(Component)} regardless of the {@link PaintStatus}
     * returned.
     * </p>
     * 
     * @param paintable
     *            the paintable to start.
     * @param tag
     *            the name of the start tag.
     * @return {@link PaintStatus} - ready to paint or already cached on the
     *         client (also used for sub paintables that are painted later
     *         separately)
     * @throws PaintException
     *             if the paint operation failed.
     * @see #startTag(String)
     * @since 7.0 (previously using startTag(Paintable, String))
     */
    public PaintStatus startPaintable(Component paintable, String tag)
            throws PaintException;

    /**
     * Prints paintable element end tag.
     * 
     * Calls to {@link #startPaintable(Component, String)}should be matched by
     * {@link #endPaintable(Component)}. If the parent tag is closed before
     * every child tag is closed a PaintException is raised.
     * 
     * @param paintable
     *            the paintable to close.
     * @throws PaintException
     *             if the paint operation failed.
     * @since 7.0 (previously using engTag(String))
     */
    public void endPaintable(Component paintable) throws PaintException;

    /**
     * Prints element start tag.
     * 
     * <pre>
     * Todo:
     * Checking of input values
     * </pre>
     * 
     * @param tagName
     *            the name of the start tag.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void startTag(String tagName) throws PaintException;

    /**
     * Prints element end tag.
     * 
     * If the parent tag is closed before every child tag is closed an
     * PaintException is raised.
     * 
     * @param tagName
     *            the name of the end tag.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void endTag(String tagName) throws PaintException;

    /**
     * Adds a boolean attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *            the Attribute name.
     * @param value
     *            the Attribute value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, boolean value) throws PaintException;

    /**
     * Adds a integer attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *            the Attribute name.
     * @param value
     *            the Attribute value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, int value) throws PaintException;

    /**
     * Adds a resource attribute to component. Atributes must be added before
     * any content is written.
     * 
     * @param name
     *            the Attribute name
     * @param value
     *            the Attribute value
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, Resource value) throws PaintException;

    /**
     * Adds details about {@link StreamVariable} to the UIDL stream. Eg. in web
     * terminals Receivers are typically rendered for the client side as URLs,
     * where the client side implementation can do an http post request.
     * <p>
     * The urls in UIDL message may use Vaadin specific protocol. Before
     * actually using the urls on the client side, they should be passed via
     * {@link com.vaadin.client.ApplicationConnection#translateVaadinUri(String)}.
     * <p>
     * Note that in current terminal implementation StreamVariables are cleaned
     * from the terminal only when:
     * <ul>
     * <li>a StreamVariable with same name replaces an old one
     * <li>the variable owner is no more attached
     * <li>the developer signals this by calling
     * {@link StreamingStartEvent#disposeStreamVariable()}
     * </ul>
     * Most commonly a component developer can just ignore this issue, but with
     * strict memory requirements and lots of StreamVariables implementations
     * that reserve a lot of memory this may be a critical issue.
     * 
     * @param owner
     *            the ReceiverOwner that can track the progress of streaming to
     *            the given StreamVariable
     * @param name
     *            an identifying name for the StreamVariable
     * @param value
     *            the StreamVariable to paint
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name,
            StreamVariable value) throws PaintException;

    /**
     * Adds a long attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *            the Attribute name.
     * @param value
     *            the Attribute value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, long value) throws PaintException;

    /**
     * Adds a float attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *            the Attribute name.
     * @param value
     *            the Attribute value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, float value) throws PaintException;

    /**
     * Adds a double attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *            the Attribute name.
     * @param value
     *            the Attribute value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, double value) throws PaintException;

    /**
     * Adds a string attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *            the Boolean attribute name.
     * @param value
     *            the Boolean attribute value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addAttribute(String name, String value) throws PaintException;

    /**
     * TODO
     * 
     * @param name
     * @param value
     * @throws PaintException
     */
    public void addAttribute(String name, Map<?, ?> value)
            throws PaintException;

    /**
     * Adds a Component type attribute. On client side the value will be a
     * terminal specific reference to corresponding component on client side
     * implementation.
     * 
     * @param name
     *            the name of the attribute
     * @param value
     *            the Component to be referenced on client side
     * @throws PaintException
     */
    public void addAttribute(String name, Component value)
            throws PaintException;

    /**
     * Adds a string type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, String value)
            throws PaintException;

    /**
     * Adds a int type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, int value)
            throws PaintException;

    /**
     * Adds a long type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, long value)
            throws PaintException;

    /**
     * Adds a float type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, float value)
            throws PaintException;

    /**
     * Adds a double type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, double value)
            throws PaintException;

    /**
     * Adds a boolean type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, boolean value)
            throws PaintException;

    /**
     * Adds a string array type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * @param value
     *            the Variable initial value.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, String[] value)
            throws PaintException;

    /**
     * Adds a Component type variable. On client side the variable value will be
     * a terminal specific reference to corresponding component on client side
     * implementation. When updated from client side, terminal will map the
     * client side component reference back to a corresponding server side
     * reference.
     * 
     * @param owner
     *            the Listener for variable changes
     * @param name
     *            the name of the variable
     * @param value
     *            the initial value of the variable
     * 
     * @throws PaintException
     *             if the paint oparation fails
     */
    public void addVariable(VariableOwner owner, String name, Component value)
            throws PaintException;

    /**
     * Adds a upload stream type variable.
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addUploadStreamVariable(VariableOwner owner, String name)
            throws PaintException;

    /**
     * Prints single XML section.
     * <p>
     * Prints full XML section. The section data must be XML and it is
     * surrounded by XML start and end-tags.
     * </p>
     * 
     * @param sectionTagName
     *            the tag name.
     * @param sectionData
     *            the section data to be printed.
     * @param namespace
     *            the namespace.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addXMLSection(String sectionTagName, String sectionData,
            String namespace) throws PaintException;

    /**
     * Adds UIDL directly. The UIDL must be valid in accordance with the
     * UIDL.dtd
     * 
     * @param uidl
     *            the UIDL to be added.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void addUIDL(java.lang.String uidl) throws PaintException;

    /**
     * Adds text node. All the contents of the text are XML-escaped.
     * 
     * @param text
     *            the Text to add
     * @throws PaintException
     *             if the paint operation failed.
     */
    void addText(String text) throws PaintException;

    /**
     * Adds CDATA node to target UIDL-tree.
     * 
     * @param text
     *            the Character data to add
     * @throws PaintException
     *             if the paint operation failed.
     * @since 3.1
     */
    void addCharacterData(String text) throws PaintException;

    public void addAttribute(String string, Object[] keys);

    /**
     * @return the "tag" string used in communication to present given
     *         {@link ClientConnector} type. Terminal may define how to present
     *         the connector.
     */
    public String getTag(ClientConnector paintable);

    /**
     * @return true if a full repaint has been requested. E.g. refresh in a
     *         browser window or such.
     */
    public boolean isFullRepaint();

}
