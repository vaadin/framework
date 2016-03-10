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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

/**
 * User Interface Description Language Target.
 * 
 * TODO document better: role of this class, UIDL format, attributes, variables,
 * etc.
 * 
 * @author Vaadin Ltd.
 * @since 5.0
 */
@SuppressWarnings("serial")
public class JsonPaintTarget implements PaintTarget {

    /* Document type declarations */

    private final static String UIDL_ARG_NAME = "name";

    private final Stack<String> mOpenTags;

    private final Stack<JsonTag> openJsonTags;

    // these match each other element-wise
    private final Stack<ClientConnector> openPaintables;
    private final Stack<String> openPaintableTags;

    private final PrintWriter uidlBuffer;

    private boolean closed = false;

    private final LegacyCommunicationManager manager;

    private int changes = 0;

    private final Set<Object> usedResources = new HashSet<Object>();

    private boolean customLayoutArgumentsOpen = false;

    private JsonTag tag;

    private boolean cacheEnabled = false;

    private final Set<Class<? extends ClientConnector>> usedClientConnectors = new HashSet<Class<? extends ClientConnector>>();

    /**
     * Creates a new JsonPaintTarget.
     * 
     * @param manager
     * @param outWriter
     *            A character-output stream.
     * @param cachingRequired
     *            true if this is not a full repaint, i.e. caches are to be
     *            used.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public JsonPaintTarget(LegacyCommunicationManager manager,
            Writer outWriter, boolean cachingRequired) throws PaintException {

        this.manager = manager;

        // Sets the target for UIDL writing
        uidlBuffer = new PrintWriter(outWriter);

        // Initialize tag-writing
        mOpenTags = new Stack<String>();
        openJsonTags = new Stack<JsonTag>();

        openPaintables = new Stack<ClientConnector>();
        openPaintableTags = new Stack<String>();

        cacheEnabled = cachingRequired;
    }

    @Override
    public void startTag(String tagName) throws PaintException {
        startTag(tagName, false);
    }

    /**
     * Prints the element start tag.
     * 
     * <pre>
     *   Todo:
     *    Checking of input values
     * 
     * </pre>
     * 
     * @param tagName
     *            the name of the start tag.
     * @throws PaintException
     *             if the paint operation failed.
     * 
     */
    public void startTag(String tagName, boolean isChildNode)
            throws PaintException {
        // In case of null data output nothing:
        if (tagName == null) {
            throw new NullPointerException();
        }

        // Ensures that the target is open
        if (closed) {
            throw new PaintException(
                    "Attempted to write to a closed PaintTarget.");
        }

        if (tag != null) {
            openJsonTags.push(tag);
        }
        // Checks tagName and attributes here
        mOpenTags.push(tagName);

        tag = new JsonTag(tagName);

        customLayoutArgumentsOpen = false;

    }

    /**
     * Prints the element end tag.
     * 
     * If the parent tag is closed before every child tag is closed an
     * PaintException is raised.
     * 
     * @param tag
     *            the name of the end tag.
     * @throws Paintexception
     *             if the paint operation failed.
     */

    @Override
    public void endTag(String tagName) throws PaintException {
        // In case of null data output nothing:
        if (tagName == null) {
            throw new NullPointerException();
        }

        // Ensure that the target is open
        if (closed) {
            throw new PaintException(
                    "Attempted to write to a closed PaintTarget.");
        }

        if (openJsonTags.size() > 0) {
            final JsonTag parent = openJsonTags.pop();

            String lastTag = "";

            lastTag = mOpenTags.pop();
            if (!tagName.equalsIgnoreCase(lastTag)) {
                throw new PaintException("Invalid UIDL: wrong ending tag: '"
                        + tagName + "' expected: '" + lastTag + "'.");
            }

            parent.addData(tag.getJSON());

            tag = parent;
        } else {
            changes++;
            uidlBuffer.print(((changes > 1) ? "," : "") + tag.getJSON());
            tag = null;
        }
    }

    /**
     * Substitutes the XML sensitive characters with predefined XML entities.
     * 
     * @param xml
     *            the String to be substituted.
     * @return A new string instance where all occurrences of XML sensitive
     *         characters are substituted with entities.
     */
    static public String escapeXML(String xml) {
        if (xml == null || xml.length() <= 0) {
            return "";
        }
        return escapeXML(new StringBuilder(xml)).toString();
    }

    /**
     * Substitutes the XML sensitive characters with predefined XML entities.
     * 
     * @param xml
     *            the String to be substituted.
     * @return A new StringBuilder instance where all occurrences of XML
     *         sensitive characters are substituted with entities.
     * 
     */
    static StringBuilder escapeXML(StringBuilder xml) {
        if (xml == null || xml.length() <= 0) {
            return new StringBuilder("");
        }

        final StringBuilder result = new StringBuilder(xml.length() * 2);

        for (int i = 0; i < xml.length(); i++) {
            final char c = xml.charAt(i);
            final String s = toXmlChar(c);
            if (s != null) {
                result.append(s);
            } else {
                result.append(c);
            }
        }
        return result;
    }

    /**
     * Escapes the given string so it can safely be used as a JSON string.
     * 
     * @param s
     *            The string to escape
     * @return Escaped version of the string
     */
    static public String escapeJSON(String s) {
        // FIXME: Move this method to another class as other classes use it
        // also.
        if (s == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            switch (ch) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '/':
                sb.append("\\/");
                break;
            default:
                if (ch >= '\u0000' && ch <= '\u001F') {
                    final String ss = Integer.toHexString(ch);
                    sb.append("\\u");
                    for (int k = 0; k < 4 - ss.length(); k++) {
                        sb.append('0');
                    }
                    sb.append(ss.toUpperCase());
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Substitutes a XML sensitive character with predefined XML entity.
     * 
     * @param c
     *            the Character to be replaced with an entity.
     * @return String of the entity or null if character is not to be replaced
     *         with an entity.
     */
    private static String toXmlChar(char c) {
        switch (c) {
        case '&':
            return "&amp;"; // & => &amp;
        case '>':
            return "&gt;"; // > => &gt;
        case '<':
            return "&lt;"; // < => &lt;
        case '"':
            return "&quot;"; // " => &quot;
        case '\'':
            return "&apos;"; // ' => &apos;
        default:
            return null;
        }
    }

    /**
     * Prints XML-escaped text.
     * 
     * @param str
     * @throws PaintException
     *             if the paint operation failed.
     * 
     */

    @Override
    public void addText(String str) throws PaintException {
        tag.addData("\"" + escapeJSON(str) + "\"");
    }

    @Override
    public void addAttribute(String name, boolean value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + (value ? "true" : "false"));
    }

    @Override
    public void addAttribute(String name, Resource value) throws PaintException {
        if (value == null) {
            throw new NullPointerException();
        }
        ClientConnector ownerConnector = openPaintables.peek();
        ownerConnector.getUI().getSession().getGlobalResourceHandler(true)
                .register(value, ownerConnector);

        ResourceReference reference = ResourceReference.create(value,
                ownerConnector, name);
        addAttribute(name, reference.getURL());
    }

    @Override
    public void addAttribute(String name, int value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    @Override
    public void addAttribute(String name, long value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    @Override
    public void addAttribute(String name, float value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    @Override
    public void addAttribute(String name, double value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    @Override
    public void addAttribute(String name, String value) throws PaintException {
        // In case of null data output nothing:
        if ((value == null) || (name == null)) {
            throw new NullPointerException(
                    "Parameters must be non-null strings");
        }

        tag.addAttribute("\"" + name + "\":\"" + escapeJSON(value) + "\"");

        if (customLayoutArgumentsOpen && "template".equals(name)) {
            getUsedResources().add("layouts/" + value + ".html");
        }

    }

    @Override
    public void addAttribute(String name, Component value)
            throws PaintException {
        final String id = value.getConnectorId();
        addAttribute(name, id);
    }

    @Override
    public void addAttribute(String name, Map<?, ?> value)
            throws PaintException {

        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(name);
        sb.append("\":");
        sb.append("{");
        for (Iterator<?> it = value.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            Object mapValue = value.get(key);
            sb.append("\"");
            if (key instanceof ClientConnector) {
                sb.append(((ClientConnector) key).getConnectorId());
            } else {
                sb.append(escapeJSON(key.toString()));
            }
            sb.append("\":");
            if (mapValue instanceof Float || mapValue instanceof Integer
                    || mapValue instanceof Double
                    || mapValue instanceof Boolean
                    || mapValue instanceof Alignment) {
                sb.append(mapValue);
            } else {
                sb.append("\"");
                sb.append(escapeJSON(mapValue.toString()));
                sb.append("\"");
            }
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");

        tag.addAttribute(sb.toString());
    }

    @Override
    public void addAttribute(String name, Object[] values) {
        // In case of null data output nothing:
        if ((values == null) || (name == null)) {
            throw new NullPointerException(
                    "Parameters must be non-null strings");
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("\"" + name + "\":[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                buf.append(",");
            }
            buf.append("\"");
            buf.append(escapeJSON(values[i].toString()));
            buf.append("\"");
        }
        buf.append("]");
        tag.addAttribute(buf.toString());
    }

    @Override
    public void addVariable(VariableOwner owner, String name, String value)
            throws PaintException {
        tag.addVariable(new StringVariable(owner, name, escapeJSON(value)));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, Component value)
            throws PaintException {
        tag.addVariable(new StringVariable(owner, name, value.getConnectorId()));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, int value)
            throws PaintException {
        tag.addVariable(new IntVariable(owner, name, value));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, long value)
            throws PaintException {
        tag.addVariable(new LongVariable(owner, name, value));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, float value)
            throws PaintException {
        tag.addVariable(new FloatVariable(owner, name, value));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, double value)
            throws PaintException {
        tag.addVariable(new DoubleVariable(owner, name, value));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, boolean value)
            throws PaintException {
        tag.addVariable(new BooleanVariable(owner, name, value));
    }

    @Override
    public void addVariable(VariableOwner owner, String name, String[] value)
            throws PaintException {
        tag.addVariable(new ArrayVariable(owner, name, value));
    }

    /**
     * Adds a upload stream type variable.
     * 
     * TODO not converted for JSON
     * 
     * @param owner
     *            the Listener for variable changes.
     * @param name
     *            the Variable name.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */

    @Override
    public void addUploadStreamVariable(VariableOwner owner, String name)
            throws PaintException {
        startTag("uploadstream");
        addAttribute(UIDL_ARG_NAME, name);
        endTag("uploadstream");
    }

    /**
     * Prints the single text section.
     * 
     * Prints full text section. The section data is escaped
     * 
     * @param sectionTagName
     *            the name of the tag.
     * @param sectionData
     *            the section data to be printed.
     * @throws PaintException
     *             if the paint operation failed.
     */

    @Override
    public void addSection(String sectionTagName, String sectionData)
            throws PaintException {
        tag.addData("{\"" + sectionTagName + "\":\"" + escapeJSON(sectionData)
                + "\"}");
    }

    /**
     * Adds XML directly to UIDL.
     * 
     * @param xml
     *            the Xml to be added.
     * @throws PaintException
     *             if the paint operation failed.
     */

    @Override
    public void addUIDL(String xml) throws PaintException {

        // Ensure that the target is open
        if (closed) {
            throw new PaintException(
                    "Attempted to write to a closed PaintTarget.");
        }

        // Make sure that the open start tag is closed before
        // anything is written.

        // Escape and write what was given
        if (xml != null) {
            tag.addData("\"" + escapeJSON(xml) + "\"");
        }

    }

    /**
     * Adds XML section with namespace.
     * 
     * @param sectionTagName
     *            the name of the tag.
     * @param sectionData
     *            the section data.
     * @param namespace
     *            the namespace to be added.
     * @throws PaintException
     *             if the paint operation failed.
     * 
     * @see com.vaadin.server.PaintTarget#addXMLSection(String, String, String)
     */

    @Override
    public void addXMLSection(String sectionTagName, String sectionData,
            String namespace) throws PaintException {

        // Ensure that the target is open
        if (closed) {
            throw new PaintException(
                    "Attempted to write to a closed PaintTarget.");
        }

        startTag(sectionTagName);
        if (namespace != null) {
            addAttribute("xmlns", namespace);
        }

        if (sectionData != null) {
            tag.addData("\"" + escapeJSON(sectionData) + "\"");
        }
        endTag(sectionTagName);
    }

    /**
     * Gets the UIDL already printed to stream. Paint target must be closed
     * before the <code>getUIDL</code> can be called.
     * 
     * @return the UIDL.
     */
    public String getUIDL() {
        if (closed) {
            return uidlBuffer.toString();
        }
        throw new IllegalStateException(
                "Tried to read UIDL from open PaintTarget");
    }

    /**
     * Closes the paint target. Paint target must be closed before the
     * <code>getUIDL</code> can be called. Subsequent attempts to write to paint
     * target. If the target was already closed, call to this function is
     * ignored. will generate an exception.
     * 
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void close() throws PaintException {
        if (tag != null) {
            uidlBuffer.write(tag.getJSON());
        }
        flush();
        closed = true;
    }

    /**
     * Method flush.
     */
    private void flush() {
        uidlBuffer.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.PaintTarget#startPaintable(com.vaadin.terminal
     * .Paintable, java.lang.String)
     */

    @Override
    public PaintStatus startPaintable(Component connector, String tagName)
            throws PaintException {
        boolean topLevelPaintable = openPaintables.isEmpty();

        if (getLogger().isLoggable(Level.FINE)) {
            getLogger().log(
                    Level.FINE,
                    "startPaintable for {0}@{1}",
                    new Object[] { connector.getClass().getName(),
                            Integer.toHexString(connector.hashCode()) });
        }
        startTag(tagName, true);

        openPaintables.push(connector);
        openPaintableTags.push(tagName);

        addAttribute("id", connector.getConnectorId());

        // Only paint top level paintables. All sub paintables are marked as
        // queued and painted separately later.
        if (!topLevelPaintable) {
            return PaintStatus.CACHED;
        }

        if (connector instanceof CustomLayout) {
            customLayoutArgumentsOpen = true;
        }
        return PaintStatus.PAINTING;
    }

    @Override
    public void endPaintable(Component paintable) throws PaintException {
        if (getLogger().isLoggable(Level.FINE)) {
            getLogger().log(
                    Level.FINE,
                    "endPaintable for {0}@{1}",
                    new Object[] { paintable.getClass().getName(),
                            Integer.toHexString(paintable.hashCode()) });
        }

        ClientConnector openPaintable = openPaintables.peek();
        if (paintable != openPaintable) {
            throw new PaintException("Invalid UIDL: closing wrong paintable: '"
                    + paintable.getConnectorId() + "' expected: '"
                    + openPaintable.getConnectorId() + "'.");
        }
        // remove paintable from the stack
        openPaintables.pop();
        String openTag = openPaintableTags.pop();
        endTag(openTag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.PaintTarget#addCharacterData(java.lang.String )
     */

    @Override
    public void addCharacterData(String text) throws PaintException {
        if (text != null) {
            tag.addData(text);
        }
    }

    /**
     * This is basically a container for UI components variables, that will be
     * added at the end of JSON object.
     * 
     * @author mattitahvonen
     * 
     */
    class JsonTag implements Serializable {
        boolean firstField = false;

        Vector<Object> variables = new Vector<Object>();

        Vector<Object> children = new Vector<Object>();

        Vector<Object> attr = new Vector<Object>();

        StringBuilder data = new StringBuilder();

        public boolean childrenArrayOpen = false;

        private boolean childNode = false;

        private boolean tagClosed = false;

        public JsonTag(String tagName) {
            data.append("[\"" + tagName + "\"");
        }

        private void closeTag() {
            if (!tagClosed) {
                data.append(attributesAsJsonObject());
                data.append(getData());
                // Writes the end (closing) tag
                data.append("]");
                tagClosed = true;
            }
        }

        public String getJSON() {
            if (!tagClosed) {
                closeTag();
            }
            return data.toString();
        }

        public void openChildrenArray() {
            if (!childrenArrayOpen) {
                // append("c : [");
                childrenArrayOpen = true;
                // firstField = true;
            }
        }

        public void closeChildrenArray() {
            // append("]");
            // firstField = false;
        }

        public void setChildNode(boolean b) {
            childNode = b;
        }

        public boolean isChildNode() {
            return childNode;
        }

        public String startField() {
            if (firstField) {
                firstField = false;
                return "";
            } else {
                return ",";
            }
        }

        /**
         * 
         * @param s
         *            json string, object or array
         */
        public void addData(String s) {
            children.add(s);
        }

        public String getData() {
            final StringBuilder buf = new StringBuilder();
            final Iterator<Object> it = children.iterator();
            while (it.hasNext()) {
                buf.append(startField());
                buf.append(it.next());
            }
            return buf.toString();
        }

        public void addAttribute(String jsonNode) {
            attr.add(jsonNode);
        }

        private String attributesAsJsonObject() {
            final StringBuilder buf = new StringBuilder();
            buf.append(startField());
            buf.append("{");
            for (final Iterator<Object> iter = attr.iterator(); iter.hasNext();) {
                final String element = (String) iter.next();
                buf.append(element);
                if (iter.hasNext()) {
                    buf.append(",");
                }
            }
            buf.append(tag.variablesAsJsonObject());
            buf.append("}");
            return buf.toString();
        }

        public void addVariable(Variable v) {
            variables.add(v);
        }

        private String variablesAsJsonObject() {
            if (variables.size() == 0) {
                return "";
            }
            final StringBuilder buf = new StringBuilder();
            buf.append(startField());
            buf.append("\"v\":{");
            final Iterator<Object> iter = variables.iterator();
            while (iter.hasNext()) {
                final Variable element = (Variable) iter.next();
                buf.append(element.getJsonPresentation());
                if (iter.hasNext()) {
                    buf.append(",");
                }
            }
            buf.append("}");
            return buf.toString();
        }
    }

    abstract class Variable implements Serializable {

        String name;

        public abstract String getJsonPresentation();
    }

    class BooleanVariable extends Variable implements Serializable {
        boolean value;

        public BooleanVariable(VariableOwner owner, String name, boolean v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            return "\"" + name + "\":" + (value == true ? "true" : "false");
        }

    }

    class StringVariable extends Variable implements Serializable {
        String value;

        public StringVariable(VariableOwner owner, String name, String v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            return "\"" + name + "\":\"" + value + "\"";
        }

    }

    class IntVariable extends Variable implements Serializable {
        int value;

        public IntVariable(VariableOwner owner, String name, int v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class LongVariable extends Variable implements Serializable {
        long value;

        public LongVariable(VariableOwner owner, String name, long v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class FloatVariable extends Variable implements Serializable {
        float value;

        public FloatVariable(VariableOwner owner, String name, float v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class DoubleVariable extends Variable implements Serializable {
        double value;

        public DoubleVariable(VariableOwner owner, String name, double v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class ArrayVariable extends Variable implements Serializable {
        String[] value;

        public ArrayVariable(VariableOwner owner, String name, String[] v) {
            value = v;
            this.name = name;
        }

        @Override
        public String getJsonPresentation() {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(name);
            sb.append("\":[");
            for (int i = 0; i < value.length;) {
                sb.append("\"");
                sb.append(escapeJSON(value[i]));
                sb.append("\"");
                i++;
                if (i < value.length) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public Set<Object> getUsedResources() {
        return usedResources;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getTag(ClientConnector clientConnector) {
        Class<? extends ClientConnector> clientConnectorClass = clientConnector
                .getClass();
        while (clientConnectorClass.isAnonymousClass()) {
            clientConnectorClass = (Class<? extends ClientConnector>) clientConnectorClass
                    .getSuperclass();
        }
        Class<?> clazz = clientConnectorClass;
        while (!usedClientConnectors.contains(clazz)
                && clazz.getSuperclass() != null
                && ClientConnector.class.isAssignableFrom(clazz)) {
            usedClientConnectors.add((Class<? extends ClientConnector>) clazz);
            clazz = clazz.getSuperclass();
        }
        return manager.getTagForType(clientConnectorClass);
    }

    public Collection<Class<? extends ClientConnector>> getUsedClientConnectors() {
        return usedClientConnectors;
    }

    @Override
    public void addVariable(VariableOwner owner, String name,
            StreamVariable value) throws PaintException {
        String url = manager.getStreamVariableTargetUrl(
                (ClientConnector) owner, name, value);
        if (url != null) {
            addVariable(owner, name, url);
        } // else { //NOP this was just a cleanup by component }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.PaintTarget#isFullRepaint()
     */

    @Override
    public boolean isFullRepaint() {
        return !cacheEnabled;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(JsonPaintTarget.class.getName());
    }

}
