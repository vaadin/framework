package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

public class UIDL {

	JSONArray json;

	public UIDL(JSONArray json) {
		this.json = json;
	}

	public String getId() {
		JSONValue val = ((JSONObject) json.get(1)).get("id");
		if (val == null)
			return null;
		return ((JSONString) val).stringValue();
	}

	public String getTag() {
		return ((JSONString) json.get(0)).stringValue();
	}

	public String getStringAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return null;
		return ((JSONString) val).stringValue();
	}

	public Set getAttributeNames() {
		HashSet attrs = new HashSet(((JSONObject) json.get(1)).keySet());
		attrs.remove("v");
		return attrs;
	}

	public int getIntAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return 0;
		double num = ((JSONNumber) val).getValue();
		return (int) num;
	}

	public long getLongAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return 0;
		double num = ((JSONNumber) val).getValue();
		return (long) num;
	}
	
	public float getFloatAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return 0;
		double num = ((JSONNumber) val).getValue();
		return (float) num;
	}
	
	public double getDoubleAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return 0;
		double num = ((JSONNumber) val).getValue();
		return (double) num;
	}

	public boolean getBooleanAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return false;
		return ((JSONBoolean) val).booleanValue();
	}
	
	public String[] getStringArrayAttribute(String name) {
		JSONArray a = (JSONArray) ((JSONObject) json.get(1)).get(name);
		String[] s = new String[a.size()];
		for (int i = 0; i < a.size(); i++)
			s[i] = ((JSONString) a.get(i)).stringValue();
		return s;
	}

	public HashSet getStringArrayAttributeAsSet(String string) {
		JSONArray a = getArrayVariable(string);
		HashSet s = new HashSet();
		for (int i = 0; i < a.size(); i++)
			s.add(((JSONString) a.get(i)).stringValue());
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

		JSONValue c = json.get(i + 2);
		if (c == null)
			return null;
		if (c.isArray() != null)
			return new UIDL(c.isArray());
		throw new IllegalStateException("Child node " + i
				+ " is not of type UIDL");
	}

	public String getChildString(int i) {

		JSONValue c = json.get(i + 2);
		if (c.isString() != null)
			return ((JSONString) c).stringValue();
		throw new IllegalStateException("Child node " + i
				+ " is not of type String");
	}

	public Iterator getChildIterator() {

		return new Iterator() {

			int index = 2;

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public Object next() {

				if (json.size() > index) {
					JSONValue c = json.get(index++);
					if (c.isString() != null)
						return c.isString().stringValue();
					else if (c.isArray() != null)
						return new UIDL(c.isArray());
					else if (c.isObject() != null)
						return new XML(c.isObject());
					else
						throw new IllegalStateException("Illegal child " + c
								+ " in tag " + getTag() + " at index " + index);
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

	public String toString() {
		String s = "<" + getTag();

		for (Iterator i = getAttributeNames().iterator(); i.hasNext();) {
			String name = i.next().toString();
			s += " " + name + "=";
			JSONValue v = ((JSONObject) json.get(1)).get(name);
			if (v.isString() != null) s += v;
			else s += "\"" + v + "\"";
		}

		s += ">\n";

		Iterator i = getChildIterator();
		while (i.hasNext()) {
			Object c = i.next();
			s += c.toString();
		}

		s += "</" + getTag() + ">\n";

		return s;
	}

	public String getChildrenAsXML() {
		String s="";
		Iterator i = getChildIterator();
		while (i.hasNext()) {
			Object c = i.next();
			s += c.toString();
		}
		return s;
	}
	
	public UIDLBrowser print_r() {
		return new UIDLBrowser();
	}

	private class UIDLBrowser extends Tree {
		public UIDLBrowser() {
			final TreeItem root = new TreeItem(getTag());
			addItem(root);
			root.addItem("");
			addTreeListener(new TreeListener() {

				public void onTreeItemStateChanged(TreeItem item) {
					if (item == root) {
						removeItem(root);
						UIDLBrowser.this.removeTreeListener(this);
						addItem(dir());
						Iterator it = treeItemIterator();
						while (it.hasNext())
							((TreeItem) it.next()).setState(true);
					}
				}

				public void onTreeItemSelected(TreeItem item) {
				}

			});
		}
	}

	public TreeItem dir() {

		String nodeName = getTag();
		for (Iterator i = getAttributeNames().iterator(); i.hasNext();) {
			String name = i.next().toString();
			String value = getAttribute(name);
			nodeName += " " + name + "=" + value;
		}
		TreeItem item = new TreeItem(nodeName);

		try {
			TreeItem tmp = null;
			for (Iterator i = getVariableHash().keySet().iterator(); i
					.hasNext();) {
				String name = i.next().toString();
				String value = "";
				try {
					value = getStringVariable(name);
				} catch (Exception e) {
					try {
						JSONArray a = getArrayVariable(name);
						value = a.toString();
					} catch (Exception e2) {
						try {
							int intVal = getIntVariable(name);
							value = String.valueOf(intVal);
						} catch (Exception e3) {
							value = "unknown";
						}
					}
				}
				if (tmp == null)
					tmp = new TreeItem("variables");
				tmp.addItem(name + "=" + value);
			}
			if (tmp != null)
				item.addItem(tmp);
		} catch (Exception e) {
			// Ingonered, no variables
		}

		Iterator i = getChildIterator();
		while (i.hasNext()) {
			Object child = i.next();
			try {
				UIDL c = (UIDL) child;
				item.addItem(c.dir());

			} catch (Exception e) {
				item.addItem(child.toString());
			}
		}
		return item;
	}

	private JSONObject getVariableHash() {
		JSONObject v = (JSONObject) ((JSONObject) json.get(1)).get("v");
		if (v == null)
			throw new IllegalArgumentException("No variables defined in tag.");
		return v;
	}
	
	public boolean hasVariable(String name) {
		Object v = null;
		try {
			v = getVariableHash().get(name);
		} catch(IllegalArgumentException e) {}
		return v != null;
	}

	public String getStringVariable(String name) {
		JSONString t = (JSONString) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return t.stringValue();
	}

	public int getIntVariable(String name) {
		JSONNumber t = (JSONNumber) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return (int) t.getValue();
	}

	public long getLongVariable(String name) {
		JSONNumber t = (JSONNumber) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return (long) t.getValue();
	}
	
	public float getFloatVariable(String name) {
		JSONNumber t = (JSONNumber) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return (float) t.getValue();
	}
	
	public double getDoubleVariable(String name) {
		JSONNumber t = (JSONNumber) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return (double) t.getValue();
	}

	public boolean getBooleanVariable(String name) {
		JSONBoolean t = (JSONBoolean) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return t.booleanValue();
	}

	private JSONArray getArrayVariable(String name) {
		JSONArray t = (JSONArray) getVariableHash().get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return t;
	}

	public String[] getStringArrayVariable(String name) {
		JSONArray a = getArrayVariable(name);
		String[] s = new String[a.size()];
		for (int i = 0; i < a.size(); i++)
			s[i] = ((JSONString) a.get(i)).stringValue();
		return s;
	}

	public Set getStringArrayVariableAsSet(String name) {
		JSONArray a = getArrayVariable(name);
		HashSet s = new HashSet();
		for (int i = 0; i < a.size(); i++)
			s.add(((JSONString) a.get(i)).stringValue());
		return s;
	}

	public int[] getIntArrayVariable(String name) {
		JSONArray a = getArrayVariable(name);
		int[] s = new int[a.size()];
		for (int i = 0; i < a.size(); i++) {
			JSONValue v = a.get(i);
			s[i] = v.isNumber() != null ? (int) ((JSONNumber) v).getValue()
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
			return x.get("x").toString();
		}
	}

	public int getChidlCount() {
		return json.size()-2;
	}

	public UIDL getErrors() {
		JSONArray a = (JSONArray) ((JSONObject) json.get(1)).get("error");
		return new UIDL(a);
	}

}
