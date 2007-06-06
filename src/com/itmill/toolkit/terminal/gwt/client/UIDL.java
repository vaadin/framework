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

public class UIDL {

	JSONArray json;

	public UIDL(JSONArray json) {
		this.json = json;
	}

	public String getId() {
		return getStringAttribute("id");
	}

	public String getTag() {
		return json.get(0).toString();
	}

	public String getStringAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		return ((JSONString) val).stringValue();
	}

	public String getAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		if (val == null)
			return null;
		return val.toString();
	}

	public Set getAttributeNames() {
		HashSet attrs = new HashSet(((JSONObject) json.get(1)).keySet());
		attrs.remove("v");
		return attrs;
	}

	public int getIntAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		double num = ((JSONNumber) val).getValue();
		return (int) num;
	}

	public long getLongAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		double num = ((JSONNumber) val).getValue();
		return (long) num;
	}

	public boolean getBooleanAttribute(String name) {
		JSONValue val = ((JSONObject) json.get(1)).get(name);
		return ((JSONBoolean) val).booleanValue();
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
						throw new IllegalStateException(
								"Illegal child of type "
										+ c.getClass().toString() + " in tag "
										+ getTag() + " at index " + index);
				}
				return null;
			}

			public boolean hasNext() {
				return json.size() > index;
			}

		};
	}

	public String toString() {
		String s = "<" + getTag();

		for (Iterator i = getAttributeNames().iterator(); i.hasNext();) {
			String name = i.next().toString();
			s += " " + name + "=\"" + getAttribute(name) + "\"";
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

	public String getStringVariable(String name) {
		JSONObject v = (JSONObject) ((JSONObject) json.get(1)).get("v");
		if (v == null)
			throw new IllegalArgumentException(
					"No variables defined in tag. (requested: " + name + ")");
		JSONString t = (JSONString) v.get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return t.stringValue();
	}

	public int getIntVariable(String name) {
		JSONObject v = (JSONObject) ((JSONObject) json.get(1)).get("v");
		if (v == null)
			throw new IllegalArgumentException(
					"No variables defined in tag. (requested: " + name + ")");
		JSONNumber t = (JSONNumber) v.get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return (int) t.getValue();
	}

	public boolean getBooleanVariable(String name) {
		JSONObject v = (JSONObject) ((JSONObject) json.get(1)).get("v");
		if (v == null)
			throw new IllegalArgumentException(
					"No variables defined in tag. (requested: " + name + ")");
		JSONBoolean t = (JSONBoolean) v.get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return t.booleanValue();
	}

	public JSONArray getArrayVariable(String name) {
		JSONObject v = (JSONObject) ((JSONObject) json.get(1)).get("v");
		if (v == null)
			throw new IllegalArgumentException(
					"No variables defined in tag. (requested: " + name + ")");
		JSONArray t = (JSONArray) v.get(name);
		if (t == null)
			throw new IllegalArgumentException("No such variable: " + name);
		return t;
	}

	public String[] getStingArrayVariable(String name) {
		JSONArray a = getArrayVariable(name);
		String[] s = new String[a.size()];
		for (int i = 0; i < a.size(); i++)
			s[i] = ((JSONString) a.get(i)).stringValue();
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
}
