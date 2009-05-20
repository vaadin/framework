/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class UIDL {

    JSONArray json;

    public UIDL(JSONArray json) {
        this.json = json;
    }

    public String getId() {
        final JSONValue val = ((JSONObject) json.get(1)).get("id");
        if (val == null) {
            return null;
        }
        return ((JSONString) val).stringValue();
    }

    public String getTag() {
        return ((JSONString) json.get(0)).stringValue();
    }

    public String getStringAttribute(String name) {
        final JSONValue val = ((JSONObject) json.get(1)).get(name);
        if (val == null) {
            return null;
        }
        return ((JSONString) val).stringValue();
    }

    public Set<String> getAttributeNames() {
        final HashSet<String> attrs = new HashSet<String>(((JSONObject) json
                .get(1)).keySet());
        attrs.remove("v");
        return attrs;
    }

    public int getIntAttribute(String name) {
        final JSONValue val = ((JSONObject) json.get(1)).get(name);
        if (val == null) {
            return 0;
        }
        final double num = val.isNumber().doubleValue();
        return (int) num;
    }

    public long getLongAttribute(String name) {
        final JSONValue val = ((JSONObject) json.get(1)).get(name);
        if (val == null) {
            return 0;
        }
        final double num = val.isNumber().doubleValue();
        return (long) num;
    }

    public float getFloatAttribute(String name) {
        final JSONValue val = ((JSONObject) json.get(1)).get(name);
        if (val == null) {
            return 0;
        }
        final double num = val.isNumber().doubleValue();
        return (float) num;
    }

    public double getDoubleAttribute(String name) {
        final JSONValue val = ((JSONObject) json.get(1)).get(name);
        if (val == null) {
            return 0;
        }
        final double num = val.isNumber().doubleValue();
        return num;
    }

    public boolean getBooleanAttribute(String name) {
        final JSONValue val = ((JSONObject) json.get(1)).get(name);
        if (val == null) {
            return false;
        }
        return val.isBoolean().booleanValue();
    }

    public String[] getStringArrayAttribute(String name) {
        final JSONArray a = (JSONArray) ((JSONObject) json.get(1)).get(name);
        final String[] s = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            s[i] = ((JSONString) a.get(i)).stringValue();
        }
        return s;
    }

    public int[] getIntArrayAttribute(String name) {
        final JSONArray a = (JSONArray) ((JSONObject) json.get(1)).get(name);
        final int[] s = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            s[i] = Integer.parseInt(((JSONString) a.get(i)).stringValue());
        }
        return s;
    }

    public HashSet<String> getStringArrayAttributeAsSet(String string) {
        final JSONArray a = getArrayVariable(string);
        final HashSet<String> s = new HashSet<String>();
        for (int i = 0; i < a.size(); i++) {
            s.add(((JSONString) a.get(i)).stringValue());
        }
        return s;
    }

    /**
     * Get attributes value as string whateever the type is
     * 
     * @param name
     * @return string presentation of attribute
     */
    private String getAttribute(String name) {
        return json.get(1).isObject().get(name).toString();
    }

    public boolean hasAttribute(String name) {
        return ((JSONObject) json.get(1)).get(name) != null;
    }

    public UIDL getChildUIDL(int i) {

        final JSONValue c = json.get(i + 2);
        if (c == null) {
            return null;
        }
        if (c.isArray() != null) {
            return new UIDL(c.isArray());
        }
        throw new IllegalStateException("Child node " + i
                + " is not of type UIDL");
    }

    public String getChildString(int i) {

        final JSONValue c = json.get(i + 2);
        if (c.isString() != null) {
            return ((JSONString) c).stringValue();
        }
        throw new IllegalStateException("Child node " + i
                + " is not of type String");
    }

    public Iterator<Object> getChildIterator() {

        return new Iterator<Object>() {

            int index = 2;

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Object next() {

                if (json.size() > index) {
                    final JSONValue c = json.get(index++);
                    if (c.isString() != null) {
                        return c.isString().stringValue();
                    } else if (c.isArray() != null) {
                        return new UIDL(c.isArray());
                    } else if (c.isObject() != null) {
                        return new XML(c.isObject());
                    } else {
                        throw new IllegalStateException("Illegal child " + c
                                + " in tag " + getTag() + " at index " + index);
                    }
                }
                return null;
            }

            public boolean hasNext() {
                return json.size() > index;
            }

        };
    }

    public int getNumberOfChildren() {
        return json.size() - 2;
    }

    @Override
    public String toString() {
        String s = "<" + getTag();

        Set<String> attributeNames = getAttributeNames();
        for (String name : attributeNames) {
            s += " " + name + "=";
            final JSONValue v = ((JSONObject) json.get(1)).get(name);
            if (v.isString() != null) {
                s += v;
            } else {
                s += "\"" + v + "\"";
            }
        }

        s += ">\n";

        final Iterator<Object> i = getChildIterator();
        while (i.hasNext()) {
            final Object c = i.next();
            s += c.toString();
        }

        s += "</" + getTag() + ">\n";

        return s;
    }

    public String getChildrenAsXML() {
        String s = "";
        final Iterator<Object> i = getChildIterator();
        while (i.hasNext()) {
            final Object c = i.next();
            s += c.toString();
        }
        return s;
    }

    public VUIDLBrowser print_r() {
        return new VUIDLBrowser();
    }

    private class VUIDLBrowser extends Tree {
        public VUIDLBrowser() {

            DOM.setStyleAttribute(getElement(), "position", "");

            final TreeItem root = new TreeItem(getTag());
            addItem(root);
            root.addItem("");
            addOpenHandler(new OpenHandler<TreeItem>() {
                boolean isLoaded;

                public void onOpen(OpenEvent<TreeItem> event) {
                    TreeItem item = event.getTarget();
                    if (item == root && !isLoaded) {
                        removeItem(root);
                        addItem(dir());
                        final Iterator<TreeItem> it = treeItemIterator();
                        while (it.hasNext()) {
                            it.next().setState(true);
                        }
                        isLoaded = true;
                    }
                }
            });
        }

        @Override
        protected boolean isKeyboardNavigationEnabled(TreeItem currentItem) {
            return false;
        }

    }

    public TreeItem dir() {

        String nodeName = getTag();
        Set<String> attributeNames = getAttributeNames();
        for (String name : attributeNames) {
            final String value = getAttribute(name);
            nodeName += " " + name + "=" + value;
        }
        final TreeItem item = new TreeItem(nodeName);

        try {
            TreeItem tmp = null;
            Set<String> keySet = getVariableHash().keySet();
            for (String name : keySet) {
                String value = "";
                try {
                    value = getStringVariable(name);
                } catch (final Exception e) {
                    try {
                        final JSONArray a = getArrayVariable(name);
                        value = a.toString();
                    } catch (final Exception e2) {
                        try {
                            final int intVal = getIntVariable(name);
                            value = String.valueOf(intVal);
                        } catch (final Exception e3) {
                            value = "unknown";
                        }
                    }
                }
                if (tmp == null) {
                    tmp = new TreeItem("variables");
                }
                tmp.addItem(name + "=" + value);
            }
            if (tmp != null) {
                item.addItem(tmp);
            }
        } catch (final Exception e) {
            // Ignored, no variables
        }

        final Iterator<Object> i = getChildIterator();
        while (i.hasNext()) {
            final Object child = i.next();
            try {
                final UIDL c = (UIDL) child;
                item.addItem(c.dir());

            } catch (final Exception e) {
                item.addItem(child.toString());
            }
        }
        return item;
    }

    private JSONObject getVariableHash() {
        final JSONObject v = (JSONObject) ((JSONObject) json.get(1)).get("v");
        if (v == null) {
            throw new IllegalArgumentException("No variables defined in tag.");
        }
        return v;
    }

    public boolean hasVariable(String name) {
        final JSONObject variables = (JSONObject) ((JSONObject) json.get(1))
                .get("v");
        if (variables == null) {
            return false;
        }
        return variables.keySet().contains(name);
    }

    public String getStringVariable(String name) {
        final JSONString t = (JSONString) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return t.stringValue();
    }

    public int getIntVariable(String name) {
        final JSONNumber t = (JSONNumber) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return (int) t.doubleValue();
    }

    public long getLongVariable(String name) {
        final JSONNumber t = (JSONNumber) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return (long) t.doubleValue();
    }

    public float getFloatVariable(String name) {
        final JSONNumber t = (JSONNumber) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return (float) t.doubleValue();
    }

    public double getDoubleVariable(String name) {
        final JSONNumber t = (JSONNumber) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return t.doubleValue();
    }

    public boolean getBooleanVariable(String name) {
        final JSONBoolean t = (JSONBoolean) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return t.booleanValue();
    }

    private JSONArray getArrayVariable(String name) {
        final JSONArray t = (JSONArray) getVariableHash().get(name);
        if (t == null) {
            throw new IllegalArgumentException("No such variable: " + name);
        }
        return t;
    }

    public String[] getStringArrayVariable(String name) {
        final JSONArray a = getArrayVariable(name);
        final String[] s = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            s[i] = ((JSONString) a.get(i)).stringValue();
        }
        return s;
    }

    public Set<String> getStringArrayVariableAsSet(String name) {
        final JSONArray a = getArrayVariable(name);
        final HashSet<String> s = new HashSet<String>();
        for (int i = 0; i < a.size(); i++) {
            s.add(((JSONString) a.get(i)).stringValue());
        }
        return s;
    }

    public int[] getIntArrayVariable(String name) {
        final JSONArray a = getArrayVariable(name);
        final int[] s = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            final JSONValue v = a.get(i);
            s[i] = v.isNumber() != null ? (int) v.isNumber().doubleValue()
                    : Integer.parseInt(v.toString());
        }
        return s;
    }

    public class XML {
        JSONObject x;

        private XML(JSONObject x) {
            this.x = x;
        }

        public String getXMLAsString() {
            final StringBuffer sb = new StringBuffer();
            Set<String> keySet = x.keySet();
            for (String tag : keySet) {
                sb.append("<");
                sb.append(tag);
                sb.append(">");
                sb.append(x.get(tag).isString().stringValue());
                sb.append("</");
                sb.append(tag);
                sb.append(">");
            }
            return sb.toString();
        }
    }

    public int getChildCount() {
        return json.size() - 2;
    }

    public UIDL getErrors() {
        final JSONArray a = (JSONArray) ((JSONObject) json.get(1)).get("error");
        return new UIDL(a);
    }

}
