package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashMap;

import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class VariableFactory {
	
	public static Variable getVariable(Node n, Component owner) {
		String nName = n.getNodeName();
		NamedNodeMap attr = n.getAttributes();
		if(nName.equals("boolean")) {
			String name = attr.getNamedItem("name").getNodeValue();
			String id = attr.getNamedItem("id").getNodeValue();	
			boolean b = attr.getNamedItem("value").getNodeValue().equals("true");
			BooleanVariable v = new BooleanVariable(owner,name,id);
			v.setValue(b);
			return v;
		} else {
			return null;
		}
	}
	
	public static HashMap getAllVariables(Node n, Component owner) {
		HashMap v = new HashMap();
		NodeList children  = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Variable var = getVariable(children.item(i), owner);
			if(var != null)
				v.put(var.getName(), var);
		}
		return v;
	}

}
