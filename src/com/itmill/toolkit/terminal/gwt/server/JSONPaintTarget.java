package com.itmill.toolkit.terminal.gwt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ApplicationResource;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.VariableOwner;

public class JSONPaintTarget implements PaintTarget {

	Stack tags = new Stack();
	Tag tag = null;

	public void addAttribute(String name, boolean value) throws PaintException {
		tag.attrs.put(name, new Boolean(value));
	}

	public void addAttribute(String name, int value) throws PaintException {
		tag.attrs.put(name, new Integer(value));
	}

	public void addAttribute(String name, Resource value) throws PaintException {
		if (value instanceof ExternalResource) {
			addAttribute(name, ((ExternalResource) value).getURL());

		} else if (value instanceof ApplicationResource) {
			ApplicationResource r = (ApplicationResource) value;
			Application a = r.getApplication();
			if (a == null)
				throw new PaintException(
						"Application not specified for resorce "
								+ value.getClass().getName());
			String uri = a.getURL().getPath();
			if (uri.charAt(uri.length() - 1) != '/')
				uri += "/";
			uri += a.getRelativeLocation(r);
			addAttribute(name, uri);

		} else if (value instanceof ThemeResource) {
			String uri = "theme://" + ((ThemeResource) value).getResourceId();
			addAttribute(name, uri);
		} else
			throw new PaintException("Ajax adapter does not "
					+ "support resources of type: "
					+ value.getClass().getName());
	}

	public void addAttribute(String name, long value) throws PaintException {
		tag.attrs.put(name, new Long(value));
	}

	public void addAttribute(String name, String value) throws PaintException {
		tag.attrs.put(name, value);
	}

	public void addCharacterData(String text) throws PaintException {
		if (tag.data != null || tag.xml != null || tag.children.size() > 0)
			throw new IllegalStateException("Character data can not be combined with XML or child-nodes");
		tag.data = text;
	}

	public void addSection(String sectionTagName, String sectionData)
			throws PaintException {
		Tag t = new Tag(sectionTagName);
		t.data = sectionData;
		if (tag.data != null || tag.xml != null)
			throw new IllegalStateException("Children can not be combined with XML or chardata");
		tag.children.add(t);
	}

	public void addText(String text) throws PaintException {
		if (tag.data != null || tag.xml != null || tag.children.size() > 0)
			throw new IllegalStateException("Text-data can not be combined with XML or child-nodes");
		tag.data = text;
	}

	public void addUIDL(String uidl) throws PaintException {
		if (tag.data != null || tag.xml != null || tag.children.size() > 0)
			throw new IllegalStateException("XML can not be combined with text or child-nodes");
		tag.xml = uidl;
	}

	public void addUploadStreamVariable(VariableOwner owner, String name)
			throws PaintException {
		// TODO Auto-generated method stub

	}

	public void addVariable(VariableOwner owner, String name, String value)
			throws PaintException {
		// TODO Auto-generated method stub

	}

	public void addVariable(VariableOwner owner, String name, int value)
			throws PaintException {
		// TODO Auto-generated method stub

	}

	public void addVariable(VariableOwner owner, String name, boolean value)
			throws PaintException {
		// TODO Auto-generated method stub

	}

	public void addVariable(VariableOwner owner, String name, String[] value)
			throws PaintException {
		// TODO Auto-generated method stub

	}

	public void addXMLSection(String sectionTagName, String sectionData,
			String namespace) throws PaintException {
		// TODO Auto-generated method stub

	}

	public void endTag(String tagName) throws PaintException {
		
	}

	public boolean startTag(Paintable paintable, String tag)
			throws PaintException {
		// TODO Auto-generated method stub
		return false;
	}

	public void startTag(String tagName) throws PaintException {
		// TODO Auto-generated method stub

	}

	private class Tag {
		HashMap attrs = new HashMap();

		ArrayList children = new ArrayList();

		ArrayList vars = new ArrayList();

		String xml = null;
		
		String data = null;
		
		String tag;
		
		Tag(String tag) {
			this.tag = tag;
		}
	}

	public void addAttribute(String string, Object[] keys) {
		// TODO Auto-generated method stub
		
	}

}
