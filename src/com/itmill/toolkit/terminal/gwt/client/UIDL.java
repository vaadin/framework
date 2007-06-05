package com.itmill.toolkit.terminal.gwt.client;

import java.util.Iterator;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class UIDL {

	JSONObject json;
	
	public UIDL(JSONObject json) {
		this.json = json;
	}
	
	public int getId() {
		String id = getAttribute("id");
		if (id==null) return -1;
		return Integer.parseInt(id);
	}
	
	public String getTag() {
		Set keys = json.keySet();
		if (keys.size() != 1) 
			throw new IllegalStateException("Expected JSON Object to contain exactly one key, now contains "+ json.keySet());
		return "" + keys.iterator().next();
	}
	
	public String getAttribute(String name) {
		JSONObject attrs = (JSONObject) json.get("attr");
		if (attrs == null) return null;
		return ""+ attrs.get(name);
	}
	
	public Iterator getChildIterator() {
		
		return new Iterator() {
			
			JSONArray children = (JSONArray) ((JSONObject)json.get(getTag())).get("children");
			int index=0;
		
			public void remove() {
				throw new UnsupportedOperationException();
			}
		
			public Object next() {
				
				if (children != null)
					return new UIDL((JSONObject)children.get(index++));
				return null;
			}
		
			public boolean hasNext() {
				return children != null && children.size() > index;
			}
		
		};
	}
	
	public String toString() {
		String s = "<"+getTag()+ " ";
		
		s +="id="+getId();
		s += ">\n";

		Iterator i = getChildIterator();
		while (i.hasNext()) {
			UIDL c = (UIDL) i.next();
			s += c.toString();
		}

		s += "</"+getTag()+">\n";
		
		return s;
	}
}
