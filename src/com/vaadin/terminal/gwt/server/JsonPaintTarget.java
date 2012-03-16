/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Root;

/**
 * User Interface Description Language Target.
 * 
 * TODO document better: role of this class, UIDL format, attributes, variables,
 * etc.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public class JsonPaintTarget implements PaintTarget {

    private static final Logger logger = Logger.getLogger(JsonPaintTarget.class
            .getName());

    /* Document type declarations */

    private final static String UIDL_ARG_NAME = "name";

    private final Stack<String> mOpenTags;

    private final Stack<JsonTag> openJsonTags;

    // these match each other element-wise
    private final Stack<Paintable> openPaintables;
    private final Stack<String> openPaintableTags;

    private final PrintWriter uidlBuffer;

    private boolean closed = false;

    private final AbstractCommunicationManager manager;

    private int changes = 0;

    private final Set<Object> usedResources = new HashSet<Object>();

    private boolean customLayoutArgumentsOpen = false;

    private JsonTag tag;

    private int errorsOpen;

    private boolean cacheEnabled = false;

    private final Collection<Paintable> paintedComponents = new HashSet<Paintable>();

    // private Collection<Paintable> identifiersCreatedDueRefPaint;

    private Collection<Paintable> deferredPaintables;

    private final Collection<Class<? extends Paintable>> usedPaintableTypes = new LinkedList<Class<? extends Paintable>>();

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
    public JsonPaintTarget(AbstractCommunicationManager manager,
            PrintWriter outWriter, boolean cachingRequired)
            throws PaintException {

        this.manager = manager;

        // Sets the target for UIDL writing
        uidlBuffer = outWriter;

        // Initialize tag-writing
        mOpenTags = new Stack<String>();
        openJsonTags = new Stack<JsonTag>();

        openPaintables = new Stack<Paintable>();
        openPaintableTags = new Stack<String>();

        deferredPaintables = new ArrayList<Paintable>();

        cacheEnabled = cachingRequired;
    }

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

        if ("error".equals(tagName)) {
            errorsOpen++;
        }

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

            // simple hack which writes error uidl structure into attribute
            if ("error".equals(lastTag)) {
                if (errorsOpen == 1) {
                    parent.addAttribute("\"error\":[\"error\",{}"
                            + tag.getData() + "]");
                } else {
                    // sub error
                    parent.addData(tag.getJSON());
                }
                errorsOpen--;
            } else {
                parent.addData(tag.getJSON());
            }

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
    public void addText(String str) throws PaintException {
        tag.addData("\"" + escapeJSON(str) + "\"");
    }

    public void addAttribute(String name, boolean value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + (value ? "true" : "false"));
    }

    @SuppressWarnings("deprecation")
    public void addAttribute(String name, Resource value) throws PaintException {

        if (value instanceof ExternalResource) {
            addAttribute(name, ((ExternalResource) value).getURL());

        } else if (value instanceof ApplicationResource) {
            final ApplicationResource r = (ApplicationResource) value;
            final Application a = r.getApplication();
            if (a == null) {
                throw new PaintException(
                        "Application not specified for resorce "
                                + value.getClass().getName());
            }
            final String uri = a.getRelativeLocation(r);
            addAttribute(name, uri);

        } else if (value instanceof ThemeResource) {
            final String uri = "theme://"
                    + ((ThemeResource) value).getResourceId();
            addAttribute(name, uri);
        } else {
            throw new PaintException("Ajax adapter does not "
                    + "support resources of type: "
                    + value.getClass().getName());
        }

    }

    public void addAttribute(String name, int value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    public void addAttribute(String name, long value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    public void addAttribute(String name, float value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    public void addAttribute(String name, double value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    public void addAttribute(String name, String value) throws PaintException {
        // In case of null data output nothing:
        if ((value == null) || (name == null)) {
            throw new NullPointerException(
                    "Parameters must be non-null strings");
        }

        tag.addAttribute("\"" + name + "\": \"" + escapeJSON(value) + "\"");

        if (customLayoutArgumentsOpen && "template".equals(name)) {
            getUsedResources().add("layouts/" + value + ".html");
        }

        if (name.equals("locale")) {
            manager.requireLocale(value);
        }

    }

    public void addAttribute(String name, Paintable value)
            throws PaintException {
        final String id = getPaintIdentifier(value);
        addAttribute(name, id);
    }

    public void addAttribute(String name, Map<?, ?> value)
            throws PaintException {

        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(name);
        sb.append("\": ");
        sb.append("{");
        for (Iterator<?> it = value.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            Object mapValue = value.get(key);
            sb.append("\"");
            if (key instanceof Paintable) {
                Paintable paintable = (Paintable) key;
                sb.append(getPaintIdentifier(paintable));
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

    public void addVariable(VariableOwner owner, String name, String value)
            throws PaintException {
        tag.addVariable(new StringVariable(owner, name, escapeJSON(value)));
    }

    public void addVariable(VariableOwner owner, String name, Paintable value)
            throws PaintException {
        tag.addVariable(new StringVariable(owner, name,
                getPaintIdentifier(value)));
    }

    public void addVariable(VariableOwner owner, String name, int value)
            throws PaintException {
        tag.addVariable(new IntVariable(owner, name, value));
    }

    public void addVariable(VariableOwner owner, String name, long value)
            throws PaintException {
        tag.addVariable(new LongVariable(owner, name, value));
    }

    public void addVariable(VariableOwner owner, String name, float value)
            throws PaintException {
        tag.addVariable(new FloatVariable(owner, name, value));
    }

    public void addVariable(VariableOwner owner, String name, double value)
            throws PaintException {
        tag.addVariable(new DoubleVariable(owner, name, value));
    }

    public void addVariable(VariableOwner owner, String name, boolean value)
            throws PaintException {
        tag.addVariable(new BooleanVariable(owner, name, value));
    }

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
     * @see com.vaadin.terminal.PaintTarget#addXMLSection(String, String,
     *      String)
     */
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
    public PaintStatus startPaintable(Paintable paintable, String tagName)
            throws PaintException {
        boolean topLevelPaintable = openPaintables.isEmpty();

        System.out.println("startPaintable for "
                + paintable.getClass().getName() + "@"
                + Integer.toHexString(paintable.hashCode()));
        startTag(tagName, true);

        openPaintables.push(paintable);
        openPaintableTags.push(tagName);

        final String id = manager.getPaintableId(paintable);
        paintable.addListener(manager);
        addAttribute("id", id);

        // queue for painting later if already painting a paintable
        if (!topLevelPaintable) {
            // if (!deferredPaintables.contains(paintable)) {
            // notify manager: add to paint queue instead of painting now
            // manager.queuePaintable(paintable);
            // deferredPaintables.add(paintable);
            // }
            return PaintStatus.DEFER;
        }

        // not a nested paintable, paint the it now
        paintedComponents.add(paintable);
        // deferredPaintables.remove(paintable);

        if (paintable instanceof CustomLayout) {
            customLayoutArgumentsOpen = true;
        }
        return PaintStatus.PAINTING;
    }

    public void endPaintable(Paintable paintable) throws PaintException {
        System.out.println("endPaintable for " + paintable.getClass().getName()
                + "@" + Integer.toHexString(paintable.hashCode()));

        Paintable openPaintable = openPaintables.peek();
        if (paintable != openPaintable) {
            throw new PaintException("Invalid UIDL: closing wrong paintable: '"
                    + manager.getPaintableId(paintable) + "' expected: '"
                    + manager.getPaintableId(openPaintable) + "'.");
        }
        // remove paintable from the stack
        openPaintables.pop();
        String openTag = openPaintableTags.pop();
        endTag(openTag);
    }

    public String getPaintIdentifier(Paintable paintable) throws PaintException {
        // if (!manager.hasPaintableId(paintable)) {
        // if (identifiersCreatedDueRefPaint == null) {
        // identifiersCreatedDueRefPaint = new HashSet<Paintable>();
        // }
        // identifiersCreatedDueRefPaint.add(paintable);
        // }
        return manager.getPaintableId(paintable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.PaintTarget#addCharacterData(java.lang.String )
     */
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

    // /**
    // * Method to check if paintable is already painted into this target.
    // *
    // * @param p
    // * @return true if is not yet painted into this target and is connected to
    // * app
    // */
    // public boolean needsToBePainted(Paintable p) {
    // if (paintedComponents.contains(p)) {
    // return false;
    // } else if (((Component) p).getApplication() == null) {
    // return false;
    // } else {
    // return true;
    // }
    // }

    private static final Map<Class<? extends Paintable>, Class<? extends Paintable>> widgetMappingCache = new HashMap<Class<? extends Paintable>, Class<? extends Paintable>>();

    @SuppressWarnings("unchecked")
    public String getTag(Paintable paintable) {
        Class<? extends Paintable> class1;
        synchronized (widgetMappingCache) {
            class1 = widgetMappingCache.get(paintable.getClass());
        }
        if (class1 == null) {
            /*
             * Client widget annotation is searched from component hierarchy to
             * detect the component that presumably has client side
             * implementation. The server side name is used in the
             * transportation, but encoded into integer strings to optimized
             * transferred data.
             */
            class1 = paintable.getClass();
            while (!hasClientWidgetMapping(class1)) {
                Class<?> superclass = class1.getSuperclass();
                if (superclass != null
                        && Paintable.class.isAssignableFrom(superclass)) {
                    class1 = (Class<? extends Paintable>) superclass;
                } else {
                    logger.warning("No superclass of "
                            + paintable.getClass().getName()
                            + " has a @ClientWidget"
                            + " annotation. Component will not be mapped correctly on client side.");
                    break;
                }
            }
            synchronized (widgetMappingCache) {
                widgetMappingCache.put(paintable.getClass(), class1);
            }
        }

        usedPaintableTypes.add(class1);
        return manager.getTagForType(class1);

    }

    private boolean hasClientWidgetMapping(Class<? extends Paintable> class1) {
        if (Root.class == class1) {
            return true;
        }
        try {
            return class1.isAnnotationPresent(ClientWidget.class);
        } catch (NoClassDefFoundError e) {
            String stacktrace = getStacktraceString(e);
            if (stacktrace
                    .contains("com.ibm.oti.reflect.AnnotationParser.parseClass")) {
                // #7479 IBM JVM apparently tries to eagerly load the classes
                // referred to by annotations. Checking the annotation from byte
                // code to be sure that we are dealing the this case and not
                // some other class loading issue.
                if (bytecodeContainsClientWidgetAnnotation(class1)) {
                    return true;
                }
            } else {
                // throw exception forward
                throw e;
            }
        } catch (LinkageError e) {
            String stacktrace = getStacktraceString(e);
            if (stacktrace
                    .contains("org.jboss.modules.ModuleClassLoader.defineClass")) {
                // #7822 JBoss AS 7 apparently tries to eagerly load the classes
                // referred to by annotations. Checking the annotation from byte
                // code to be sure that we are dealing the this case and not
                // some other class loading issue.
                if (bytecodeContainsClientWidgetAnnotation(class1)) {
                    // Seems that JBoss still prints a stacktrace to the logs
                    // even though the LinkageError has been caught
                    return true;
                }
            } else {
                // throw exception forward
                throw e;
            }
        } catch (RuntimeException e) {
            if (e.getStackTrace()[0].getClassName().equals(
                    "org.glassfish.web.loader.WebappClassLoader")) {

                // See #3920
                // Glassfish 3 is darn eager to load the value class, even
                // though we just want to check if the annotation exists.

                // In some situations (depending on class loading order) it
                // would be enough to return true here, but it is safer to check
                // the annotation from byte code

                if (bytecodeContainsClientWidgetAnnotation(class1)) {
                    return true;
                }
            } else {
                // throw exception forward
                throw e;
            }
        }
        return false;
    }

    private static String getStacktraceString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String stacktrace = writer.toString();
        return stacktrace;
    }

    private boolean bytecodeContainsClientWidgetAnnotation(
            Class<? extends Paintable> class1) {

        try {
            String name = class1.getName().replace('.', '/') + ".class";

            InputStream stream = class1.getClassLoader().getResourceAsStream(
                    name);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(stream));
            try {
                String line;
                boolean atSourcefile = false;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("SourceFile")) {
                        atSourcefile = true;
                    }
                    if (atSourcefile) {
                        if (line.contains("ClientWidget")) {
                            return true;
                        }
                    }
                    // TODO could optimize to quit at the end attribute
                }
            } catch (IOException e1) {
                logger.log(Level.SEVERE,
                        "An error occurred while finding widget mapping.", e1);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    logger.log(Level.SEVERE, "Could not close reader.", e1);

                }
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE,
                    "An error occurred while finding widget mapping.", t);
        }

        return false;
    }

    Collection<Class<? extends Paintable>> getUsedPaintableTypes() {
        return usedPaintableTypes;
    }

    public void addVariable(VariableOwner owner, String name,
            StreamVariable value) throws PaintException {
        String url = manager.getStreamVariableTargetUrl((Connector) owner,
                name, value);
        if (url != null) {
            addVariable(owner, name, url);
        } // else { //NOP this was just a cleanup by component }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.PaintTarget#isFullRepaint()
     */
    public boolean isFullRepaint() {
        return !cacheEnabled;
    }

}
