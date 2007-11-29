/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.terminal.gwt.server;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ApplicationResource;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.VariableOwner;

/**
 * User Interface Description Language Target.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public class JsonPaintTarget implements PaintTarget {

    /* Document type declarations */

    private final static String UIDL_ARG_NAME = "name";

    private final static String UIDL_ARG_VALUE = "value";

    private final static String UIDL_ARG_ID = "id";

    private Stack mOpenTags;

    private Stack openJsonTags;

    private boolean mTagArgumentListOpen;

    private PrintWriter uidlBuffer;

    private boolean closed = false;

    private CommunicationManager manager;

    private boolean trackPaints = false;

    private int numberOfPaints = 0;

    private int changes = 0;

    Set preCachedResources = new HashSet();

    private boolean customLayoutArgumentsOpen = false;

    private JsonTag tag;

    private int errorsOpen;

    private boolean cacheEnabled = false;

    /**
     * Creates a new XMLPrintWriter, without automatic line flushing.
     * 
     * @param variableMap
     * @param manager
     * @param outWriter
     *                A character-output stream.
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public JsonPaintTarget(CommunicationManager manager, PrintWriter outWriter,
            boolean cachingRequired) throws PaintException {

        this.manager = manager;

        // Sets the target for UIDL writing
        uidlBuffer = outWriter;

        // Initialize tag-writing
        mOpenTags = new Stack();
        openJsonTags = new Stack();
        mTagArgumentListOpen = false;

        // Adds document declaration

        // Adds UIDL start tag and its attributes

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
     *                the name of the start tag.
     * @throws PaintException
     *                 if the paint operation failed.
     * 
     */
    public void startTag(String tagName, boolean isChildNode)
            throws PaintException {
        // In case of null data output nothing:
        if (tagName == null) {
            throw new NullPointerException();
        }

        // Increments paint tracker
        if (isTrackPaints()) {
            numberOfPaints++;
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

        mTagArgumentListOpen = true;

        customLayoutArgumentsOpen = "customlayout".equals(tagName);

        if ("error".equals(tagName)) {
            errorsOpen++;
        }
    }

    /**
     * Prints the element end tag.
     * 
     * If the parent tag is closed before every child tag is closed an
     * PaintException is raised.
     * 
     * @param tag
     *                the name of the end tag.
     * @throws Paintexception
     *                 if the paint operation failed.
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
            JsonTag parent = (JsonTag) openJsonTags.pop();

            String lastTag = "";

            lastTag = (String) mOpenTags.pop();
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
     *                the String to be substituted.
     * @return A new string instance where all occurrences of XML sensitive
     *         characters are substituted with entities.
     */
    static public String escapeXML(String xml) {
        if (xml == null || xml.length() <= 0) {
            return "";
        }
        return escapeXML(new StringBuffer(xml)).toString();
    }

    /**
     * Substitutes the XML sensitive characters with predefined XML entities.
     * 
     * @param xml
     *                the String to be substituted.
     * @return A new StringBuffer instance where all occurrences of XML
     *         sensitive characters are substituted with entities.
     * 
     */
    static public StringBuffer escapeXML(StringBuffer xml) {
        if (xml == null || xml.length() <= 0) {
            return new StringBuffer("");
        }

        StringBuffer result = new StringBuffer(xml.length() * 2);

        for (int i = 0; i < xml.length(); i++) {
            char c = xml.charAt(i);
            String s = toXmlChar(c);
            if (s != null) {
                result.append(s);
            } else {
                result.append(c);
            }
        }
        return result;
    }

    static public String escapeJSON(String s) {
        if (s == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
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
                    String ss = Integer.toHexString(ch);
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
     *                the Character to be replaced with an entity.
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
     *                 if the paint operation failed.
     * 
     */
    public void addText(String str) throws PaintException {
        tag.addData("\"" + escapeJSON(str) + "\"");
    }

    /**
     * Adds a boolean attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *                the Attribute name.
     * @param value
     *                the Attribute value.
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, boolean value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + (value ? "true" : "false"));
    }

    /**
     * Adds a resource attribute to component. Attributes must be added before
     * any content is written.
     * 
     * @param name
     *                the Attribute name.
     * @param value
     *                the Attribute value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, Resource value) throws PaintException {

        if (value instanceof ExternalResource) {
            addAttribute(name, ((ExternalResource) value).getURL());

        } else if (value instanceof ApplicationResource) {
            ApplicationResource r = (ApplicationResource) value;
            Application a = r.getApplication();
            if (a == null) {
                throw new PaintException(
                        "Application not specified for resorce "
                                + value.getClass().getName());
            }
            String uri;
            if (a.getURL() != null) {
                uri = a.getURL().getPath();
            } else {
                uri = "";
            }
            if (uri.length() > 0 && uri.charAt(uri.length() - 1) != '/') {
                uri += "/";
            }
            uri += a.getRelativeLocation(r);
            addAttribute(name, uri);

        } else if (value instanceof ThemeResource) {
            String uri = "theme://" + ((ThemeResource) value).getResourceId();
            addAttribute(name, uri);
        } else {
            throw new PaintException("Ajax adapter does not "
                    + "support resources of type: "
                    + value.getClass().getName());
        }

    }

    /**
     * Adds a integer attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *                the Attribute name.
     * @param value
     *                the Attribute value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, int value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    /**
     * Adds a long attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *                the Attribute name.
     * @param value
     *                the Attribute value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, long value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    /**
     * Adds a float attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *                the Attribute name.
     * @param value
     *                the Attribute value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, float value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    /**
     * Adds a double attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *                the Attribute name.
     * @param value
     *                the Attribute value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, double value) throws PaintException {
        tag.addAttribute("\"" + name + "\":" + String.valueOf(value));
    }

    /**
     * Adds a string attribute to component. Atributes must be added before any
     * content is written.
     * 
     * @param name
     *                the String attribute name.
     * @param value
     *                the String attribute value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addAttribute(String name, String value) throws PaintException {
        // In case of null data output nothing:
        if ((value == null) || (name == null)) {
            throw new NullPointerException(
                    "Parameters must be non-null strings");
        }

        tag.addAttribute("\"" + name + "\": \"" + escapeJSON(value) + "\"");

        if (customLayoutArgumentsOpen && "template".equals(name)) {
            getPreCachedResources().add("layouts/" + value + ".html");
        }

        if (name.equals("locale")) {
            manager.requireLocale(value);
        }

    }

    public void addAttribute(String name, Object[] values) {
        // In case of null data output nothing:
        if ((values == null) || (name == null)) {
            throw new NullPointerException(
                    "Parameters must be non-null strings");
        }
        StringBuffer buf = new StringBuffer();
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

    /**
     * Adds a string type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, String value)
            throws PaintException {
        tag.addVariable(new StringVariable(owner, name, escapeJSON(value)));
    }

    /**
     * Adds a int type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, int value)
            throws PaintException {
        tag.addVariable(new IntVariable(owner, name, value));
    }

    /**
     * Adds a long type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, long value)
            throws PaintException {
        tag.addVariable(new LongVariable(owner, name, value));
    }

    /**
     * Adds a float type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, float value)
            throws PaintException {
        tag.addVariable(new FloatVariable(owner, name, value));
    }

    /**
     * Adds a double type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, double value)
            throws PaintException {
        tag.addVariable(new DoubleVariable(owner, name, value));
    }

    /**
     * Adds a boolean type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
    public void addVariable(VariableOwner owner, String name, boolean value)
            throws PaintException {
        tag.addVariable(new BooleanVariable(owner, name, value));
    }

    /**
     * Adds a string array type variable.
     * 
     * @param owner
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * @param value
     *                the Variable initial value.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
     */
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
     *                the Listener for variable changes.
     * @param name
     *                the Variable name.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
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
     *                the name of the tag.
     * @param sectionData
     *                the section data to be printed.
     * @throws PaintException
     *                 if the paint operation failed.
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
     *                the Xml to be added.
     * @throws PaintException
     *                 if the paint operation failed.
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
     *                the name of the tag.
     * @param sectionData
     *                the section data.
     * @param namespace
     *                the namespace to be added.
     * @throws PaintException
     *                 if the paint operation failed.
     * 
     * @see com.itmill.toolkit.terminal.PaintTarget#addXMLSection(String,
     *      String, String)
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
        mTagArgumentListOpen = false;
        customLayoutArgumentsOpen = false;

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
     * <code>getUIDL</code> can be called. Subsequent attempts to write to
     * paint target. If the target was already closed, call to this function is
     * ignored. will generate an exception.
     * 
     * @throws PaintException
     *                 if the paint operation failed.
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

    /**
     * @see com.itmill.toolkit.terminal.PaintTarget#startTag(com.itmill.toolkit.terminal.Paintable,
     *      java.lang.String)
     */
    public boolean startTag(Paintable paintable, String tagName)
            throws PaintException {
        startTag(tagName, true);
        boolean isPreviouslyPainted = manager.hasPaintableId(paintable);
        String id = manager.getPaintableId(paintable);
        paintable.addListener(manager);
        addAttribute("id", id);
        return cacheEnabled && isPreviouslyPainted;
    }

    /**
     * @see com.itmill.toolkit.terminal.PaintTarget#addCharacterData(java.lang.String)
     */
    public void addCharacterData(String text) throws PaintException {
        if (text != null) {
            tag.addData(text);
        }
    }

    /**
     * 
     * @return
     */
    public boolean isTrackPaints() {
        return trackPaints;
    }

    /**
     * Gets the number of paints.
     * 
     * @return the number of paints.
     */
    public int getNumberOfPaints() {
        return numberOfPaints;
    }

    /**
     * Sets the tracking to true or false.
     * 
     * This also resets the number of paints.
     * 
     * @param enabled
     *                is the tracking is enabled or not.
     * @see #getNumberOfPaints()
     */
    public void setTrackPaints(boolean enabled) {
        trackPaints = enabled;
        numberOfPaints = 0;
    }

    /**
     * This is basically a container for UI components variables, that will be
     * added at the end of JSON object.
     * 
     * @author mattitahvonen
     * 
     */
    class JsonTag {
        boolean firstField = false;

        Vector variables = new Vector();

        Vector children = new Vector();

        Vector attr = new Vector();

        StringBuffer data = new StringBuffer();

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
         *                json string, object or array
         */
        public void addData(String s) {
            children.add(s);
        }

        public String getData() {
            StringBuffer buf = new StringBuffer();
            Iterator it = children.iterator();
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
            StringBuffer buf = new StringBuffer();
            buf.append(startField());
            buf.append("{");
            for (Iterator iter = attr.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
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
            StringBuffer buf = new StringBuffer();
            buf.append(startField());
            buf.append("\"v\":{");
            Iterator iter = variables.iterator();
            while (iter.hasNext()) {
                Variable element = (Variable) iter.next();
                buf.append(element.getJsonPresentation());
                if (iter.hasNext()) {
                    buf.append(",");
                }
            }
            buf.append("}");
            return buf.toString();
        }

        class TagCounter {
            int count;

            public TagCounter() {
                count = 0;
            }

            public void increment() {
                count++;
            }

            public String postfix(String s) {
                if (count > 0) {
                    return s + count;
                }
                return s;
            }
        }
    }

    abstract class Variable {

        String name;

        public abstract String getJsonPresentation();
    }

    class BooleanVariable extends Variable {
        boolean value;

        public BooleanVariable(VariableOwner owner, String name, boolean v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            return "\"" + name + "\":" + (value == true ? "true" : "false");
        }

    }

    class StringVariable extends Variable {
        String value;

        public StringVariable(VariableOwner owner, String name, String v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            return "\"" + name + "\":\"" + value + "\"";
        }

    }

    class IntVariable extends Variable {
        int value;

        public IntVariable(VariableOwner owner, String name, int v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class LongVariable extends Variable {
        long value;

        public LongVariable(VariableOwner owner, String name, long v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class FloatVariable extends Variable {
        float value;

        public FloatVariable(VariableOwner owner, String name, float v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class DoubleVariable extends Variable {
        double value;

        public DoubleVariable(VariableOwner owner, String name, double v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            return "\"" + name + "\":" + value;
        }
    }

    class ArrayVariable extends Variable {
        String[] value;

        public ArrayVariable(VariableOwner owner, String name, String[] v) {
            value = v;
            this.name = name;
        }

        public String getJsonPresentation() {
            String pres = "\"" + name + "\":[";
            for (int i = 0; i < value.length;) {
                pres += "\"" + value[i] + "\"";
                i++;
                if (i < value.length) {
                    pres += ",";
                }
            }
            pres += "]";
            return pres;
        }
    }

    public Set getPreCachedResources() {
        return preCachedResources;
    }

    public void setPreCachedResources(Set preCachedResources) {
        throw new UnsupportedOperationException();
    }
}
