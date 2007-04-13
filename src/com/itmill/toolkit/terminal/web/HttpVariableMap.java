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

package com.itmill.toolkit.terminal.web;

import com.itmill.toolkit.terminal.SystemError;
import com.itmill.toolkit.terminal.Terminal;
import com.itmill.toolkit.terminal.UploadStream;
import com.itmill.toolkit.terminal.VariableOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.WeakHashMap;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Class implementing the variable mappings.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class HttpVariableMap {

	// Id <-> (Owner,Name) mapping
	private Map idToNameMap = new HashMap();
	private Map idToTypeMap = new HashMap();
	private Map idToOwnerMap = new HashMap();
	private Map idToValueMap = new HashMap();
	private Map ownerToNameToIdMap = new WeakHashMap();
	private Object mapLock = new Object();

	// Id generator
	private long lastId = 0;

	/** 
	 * Converts the string to a supported class.
	 * @param type 
	 * @param value 
	 * @throws java.lang.ClassCastException
	 */
	private static Object convert(Class type, String value)
		throws java.lang.ClassCastException {
		try {

			// Boolean typed variables
			if (type.equals(Boolean.class))
				return new Boolean(
					!(value.equals("") || value.equals("false")));

			// Integer typed variables
			if (type.equals(Integer.class))
				return new Integer(value.trim());

			// String typed variables
			if (type.equals(String.class))
				return value;

			throw new ClassCastException("Unsupported type: " + type.getName());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** 
	 * Registers a new variable.
	 * @param name the name of the variable.
	 * @param type
	 * @param value
	 * @param owner the Listener for variable changes.
	 *
	 * @return id to assigned for this variable.
	 */
	public String registerVariable(
		String name,
		Class type,
		Object value,
		VariableOwner owner) {

		// Checks that the type of the class is supported
		if (!(type.equals(Boolean.class)
			|| type.equals(Integer.class)
			|| type.equals(String.class)
			|| type.equals(String[].class)
			|| type.equals(UploadStream.class)))
			throw new SystemError(
				"Unsupported variable type: " + type.getClass());

		synchronized (mapLock) {

			// Checks if the variable is already mapped
			HashMap nameToIdMap = (HashMap) ownerToNameToIdMap.get(owner);
			if (nameToIdMap == null) {
				nameToIdMap = new HashMap();
				ownerToNameToIdMap.put(owner, nameToIdMap);
			}
			String id = (String) nameToIdMap.get(name);

			if (id == null) {
				// Generates new id and register it
				id = "v" + String.valueOf(++lastId);
				nameToIdMap.put(name, id);
				idToOwnerMap.put(id, new WeakReference(owner));
				idToNameMap.put(id, name);
				idToTypeMap.put(id, type);
			}

			idToValueMap.put(id, value);

			return id;
		}
	}

	/** 
	 * Unregisters the variable.
	 * @param name the name of the variable.
	 * @param owner the Listener for variable changes.
	 */
	public void unregisterVariable(String name, VariableOwner owner) {

		synchronized (mapLock) {

			// Gets the id
			HashMap nameToIdMap = (HashMap) ownerToNameToIdMap.get(owner);
			if (nameToIdMap == null)
				return;
			String id = (String) nameToIdMap.get(name);
			if (id != null)
				return;

			// Removes all the mappings
			nameToIdMap.remove(name);
			if (nameToIdMap.isEmpty())
				ownerToNameToIdMap.remove(owner);
			idToNameMap.remove(id);
			idToTypeMap.remove(id);
			idToValueMap.remove(id);
			idToOwnerMap.remove(id);

		}
	}

	/**
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	private class ParameterContainer {

		/** 
		 * Constructs the mapping: listener to set of listened parameter names. 
		 */
		private HashMap parameters = new HashMap();

		/** 
		 * Parameter values. 
		 */
		private HashMap values = new HashMap();

		/** 
		 * Multipart parser used for parsing the request. 
		 */
		private ServletMultipartRequest parser = null;

		/** 
		 * Name - Value mapping of parameters that are not variables. 
		 */
		private HashMap nonVariables = new HashMap();

		/** 
		 * Creates a new parameter container and parse the parameters from the request using
		 * GET, POST and POST/MULTIPART parsing.
		 * @param req the HTTP request.
		 * @throws IOException if the writing failed due to input/output error.
		 */
		public ParameterContainer(HttpServletRequest req) throws IOException {
			// Parse GET / POST parameters
			for (Enumeration e = req.getParameterNames();
				e.hasMoreElements();
				) {
				String paramName = (String) e.nextElement();
				String[] paramValues = req.getParameterValues(paramName);
				addParam(paramName, paramValues);
			}

			// Parse multipart variables
			try {
				parser =
					new ServletMultipartRequest(
						req,
						MultipartRequest.MAX_READ_BYTES);
			} catch (IllegalArgumentException ignored) {
				parser = null;
			}

			if (parser != null) {
				for (Enumeration e = parser.getFileParameterNames();
					e.hasMoreElements();
					) {
					String paramName = (String) e.nextElement();
					addParam(paramName, null);
				}
				for (Enumeration e = parser.getParameterNames();
					e.hasMoreElements();
					) {
					String paramName = (String) e.nextElement();
					Enumeration val = parser.getURLParameters(paramName);

					// Create a linked list from enumeration to calculate elements
					LinkedList l = new LinkedList();
					while (val.hasMoreElements())
						l.addLast(val.nextElement());

					// String array event constructor
					String[] s = new String[l.size()];
					Iterator i = l.iterator();
					for (int j = 0; j < s.length; j++)
						s[j] = (String) i.next();

					addParam(paramName, s);
				}
			}

		}

		/** 
		 * Adds the parameter to container.
		 * @param name the name of the parameter.
		 * @param value 
		 */
		private void addParam(String name, String[] value) {

			// Support name="set:name=value" value="ignored" notation
			if (name.startsWith("set:")) {
				int equalsIndex = name.indexOf('=');
				value[0] = name.substring(equalsIndex + 1, name.length());
				name = name.substring(4, equalsIndex);
				String[] curVal = (String[]) values.get(name);
				if (curVal != null) {
					String[] newVal = new String[1 + curVal.length];
					newVal[curVal.length] = value[0];
					for (int i = 0; i < curVal.length; i++)
						newVal[i] = curVal[i];
					value = newVal;

					// Special case - if the set:-method is used for 
					// declaring array of length 2, where either of the  
					// following conditions are true:
					//    - the both items are the same 
					//    - the both items have the same length and 
					//      - the items only differ on last character
					//      - second last character is '.'
					//      - last char of one string is 'x' and other is 'y'
					// Browser is unporposely modifying the name. 
					if (value.length == 2
						&& value[0].length() == value[1].length()) {
						boolean same = true;
						for (int i = 0; i < value[0].length() - 1 && same; i++)
							if (value[0].charAt(i) != value[1].charAt(i))
								same = false;
						if (same
							&& ((value[0].charAt(value[0].length() - 1) == 'x'
								&& value[1].charAt(value[1].length() - 1) == 'y')
							|| (value[0].charAt(value[0].length() - 1) == 'y'
								&& value[1].charAt(value[1].length() - 1)
									== 'x'))) {
							value =
								new String[] {
									 value[0].substring(
										0,
										value[1].length() - 2)};
						} else
						if (same && value[0].equals(value[1]))
							value = new String[] { value[0] };
					}

					// Special case - if the set:-method is used for 
					// declaring array of length 3, where all of the 
					// following conditions are true:
					//    - two last items  have the same length
					//    - the first item is 2 chars shorter
					//    - the longer items only differ on last character
					//    - the shortest item is a prefix of the longer ones
					//    - second last character of longer ones is '.'
					//    - last char of one long string is 'x' and other is 'y'
					// Browser is unporposely modifying the name. (Mozilla, Firefox, ..)
					if (value.length == 3
						&& value[1].length() == value[2].length() &&
						value[0].length() +2 == value[1].length()) {
						boolean same = true;
						for (int i = 0; i < value[1].length() - 1 && same; i++)
							if (value[2].charAt(i) != value[1].charAt(i))
								same = false;
						for (int i = 0; i < value[0].length() && same; i++)
							if (value[0].charAt(i) != value[1].charAt(i))
								same = false;
						if (same
							&& (value[2].charAt(value[2].length() - 1) == 'x'
								&& value[1].charAt(value[1].length() - 1) == 'y')
							|| (value[2].charAt(value[2].length() - 1) == 'y'
								&& value[1].charAt(value[1].length() - 1)
									== 'x')) {
							value =
								new String[] {
									 value[0]};
						}
					}

				}
			}

			// Support for setting arrays in format
			// set-array:name=value1,value2,value3,...
			else if (name.startsWith("set-array:")) {
				int equalsIndex = name.indexOf('=');
				if (equalsIndex < 0)
					return;

				StringTokenizer commalist =
					new StringTokenizer(name.substring(equalsIndex + 1), ",");
				name = name.substring(10, equalsIndex);
				String[] curVal = (String[]) values.get(name);
				ArrayList elems = new ArrayList();

				// Adds old values if present.
				if (curVal != null) {
					for (int i = 0; i < curVal.length; i++)
						elems.add(curVal[i]);
				}
				while (commalist.hasMoreTokens()) {
					String token = commalist.nextToken();
					if (token != null && token.length() > 0)
						elems.add(token);
				}
				value = new String[elems.size()];
				for (int i = 0; i < value.length; i++)
					value[i] = (String) elems.get(i);

			}

			// Support name="array:name" value="val1,val2,val3" notation
			// All the empty elements are ignored
			else if (name.startsWith("array:")) {

				name = name.substring(6);
				StringTokenizer commalist = new StringTokenizer(value[0], ",");
				String[] curVal = (String[]) values.get(name);
				ArrayList elems = new ArrayList();

				// Adds old values if present.
				if (curVal != null) {
					for (int i = 0; i < curVal.length; i++)
						elems.add(curVal[i]);
				}
				while (commalist.hasMoreTokens()) {
					String token = commalist.nextToken();
					if (token != null && token.length() > 0)
						elems.add(token);
				}
				value = new String[elems.size()];
				for (int i = 0; i < value.length; i++)
					value[i] = (String) elems.get(i);
			}

			// Support declaring variables with name="declare:name"
			else if (name.startsWith("declare:")) {
				name = name.substring(8);
				value = (String[]) values.get(name);
				if (value == null)
					value = new String[0];
			}

			// Gets the owner
			WeakReference ref = (WeakReference) idToOwnerMap.get(name);
			VariableOwner owner = null;
			if (ref != null)
				owner = (VariableOwner) ref.get();

			// Adds the parameter to mapping only if they have owners
			if (owner != null) {
				Set p = (Set) parameters.get(owner);
				if (p == null)
					parameters.put(owner, p = new HashSet());
				p.add(name);
				if (value != null)
					values.put(name, value);
			}

			// If the owner can not be found
			else {

				// If parameter has been mapped before, remove the old owner mapping
				if (ref != null) {

					// The owner has been destroyed, so we remove the mappings
					idToNameMap.remove(name);
					idToOwnerMap.remove(name);
					idToTypeMap.remove(name);
					idToValueMap.remove(name);
				}

				// Adds the parameter to set of non-variables
				nonVariables.put(name, value);
			}

		}

		/** 
		 * Gets the set of all parameters connected to given variable owner.
		 * @param owner the Listener for variable changes.
		 * @return  the set of all parameters connected to variable owner.
		 */
		public Set getParameters(VariableOwner owner) {
			if (owner == null)
				return null;
			return (Set) parameters.get(owner);
		}

		/** 
		 * Gets the set of all variable owners owning parameters in this request.
		 * @return  
		 */
		public Set getOwners() {
			return parameters.keySet();
		}

		/** 
		 * Gets the value of a parameter.
		 * @param parameterName the name of the parameter.
		 * @return  the value of the parameter.
		 */
		public String[] getValue(String parameterName) {
			return (String[]) values.get(parameterName);
		}

		/** 
		 * Gets the servlet multipart parser.
		 * @return the parser.
		 */
		public ServletMultipartRequest getParser() {
			return parser;
		}

		/** 
		 * Gets the name - value[] mapping of non variable paramteres.
		 * @return  
		 */
		public Map getNonVariables() {
			return nonVariables;
		}
	}

	/** 
	 * Handles all variable changes in this request.
	 * @param req the Http request to handle.
	 * @param errorListener If the list is non null, only the listed listeners are
	 * served. Otherwise all the listeners are served.
	 * @return Name to Value[] mapping of unhandled variables.
	 * @throws IOException if the writing failed due to input/output error.
	 */
	public Map handleVariables(
		HttpServletRequest req,
		Terminal.ErrorListener errorListener)
		throws IOException {

		// Gets the parameters
		ParameterContainer parcon = new ParameterContainer(req);

		// Sorts listeners to dependency order
		List listeners = getDependencySortedListenerList(parcon.getOwners());

		// Handles all parameters for all listeners
		while (!listeners.isEmpty()) {
			VariableOwner listener = (VariableOwner) listeners.remove(0);
			boolean changed = false; // Has any of this owners variabes changed
			// Handle all parameters for listener
			Set params = parcon.getParameters(listener);
			if (params != null) { // Name value mapping
				Map variables = new HashMap();
				for (Iterator pi = params.iterator(); pi.hasNext();) {
					// Gets the name of the parameter
					String param = (String) pi.next();
					// Extracts more information about the parameter
					String varName = (String) idToNameMap.get(param);
					Class varType = (Class) idToTypeMap.get(param);
					Object varOldValue = idToValueMap.get(param);
					if (varName == null || varType == null)
						Log.warn(
							"VariableMap: No variable found for parameter "
								+ param
								+ " ("
								+ varName
								+ ","
								+ listener
								+ ")");
					else {

						ServletMultipartRequest parser = parcon.getParser();

						// Upload events
						if (varType.equals(UploadStream.class)) {
							if (parser != null
								&& parser.getFileParameter(
									param,
									MultipartRequest.FILENAME)
									!= null) {
								String filename =
									(String) parser.getFileParameter(
										param,
										MultipartRequest.FILENAME);
								String contentType =
									(String) parser.getFileParameter(
										param,
										MultipartRequest.CONTENT_TYPE);
								UploadStream upload =
									new HttpUploadStream(
										varName,
										parser.getFileContents(param),
										filename,
										contentType);
								variables.put(varName, upload);
								changed = true;
							}
						}

						// Normal variable change events
						else {
							// First try to parse the event without multipart
							String[] values = parcon.getValue(param);
							if (values != null) {

								if (varType.equals(String[].class)) {
									variables.put(varName, values);
									changed
										|= (!Arrays
											.equals(
												values,
												(String[]) varOldValue));
								} else {
									try {
										if (values.length == 1) {
											Object val =
												convert(varType, values[0]);
											variables.put(varName, val);
											changed
												|= ((val == null
													&& varOldValue != null)
													|| (val != null
														&& !val.equals(
															varOldValue)));
										} else if (
											values.length == 0
												&& varType.equals(
													Boolean.class)) {
											Object val = new Boolean(false);
											variables.put(varName, val);
											changed
												|= (!val.equals(varOldValue));
										} else {
											Log.warn(
												"Empty variable '"
													+ varName
													+ "' of type "
													+ varType.toString());
										}

									} catch (java.lang.ClassCastException e) {
										Log.except(
											"WebVariableMap conversion exception",
											e);
										errorListener.terminalError(
											new TerminalErrorImpl(e));
									}
								}
							}
						}
					}
				}

				// Do the valuechange if the listener is enabled
				if (listener.isEnabled() && changed) {
					try {
						listener.changeVariables(req, variables);
					} catch (Throwable t) {
						// Notify the error listener
						errorListener.terminalError(
							new VariableOwnerErrorImpl(listener, t));
					}
				}
			}
		}

		return parcon.getNonVariables();
	}

	/** 
	 * Implementation of VariableOwner.Error interface. 
	 */
	public class TerminalErrorImpl implements Terminal.ErrorEvent {
		private Throwable throwable;
		
/**
 * 
 * @param throwable
 */
		private TerminalErrorImpl(Throwable throwable) {
			this.throwable = throwable;
		}

		/**
		 * Gets the contained throwable.
		 * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
		 */
		public Throwable getThrowable() {
			return this.throwable;
		}

	}

	/** 
	 * Implementation of VariableOwner.Error interface. 
	 */
	public class VariableOwnerErrorImpl
		extends TerminalErrorImpl
		implements VariableOwner.ErrorEvent {

		private VariableOwner owner;
		
/**
 * 
 * @param owner the Listener for variable changes.
 * @param throwable
 */
		private VariableOwnerErrorImpl(
			VariableOwner owner,
			Throwable throwable) {
			super(throwable);
			this.owner = owner;
		}

		/**
		 * Gets the source VariableOwner.
		 * @see com.itmill.toolkit.terminal.VariableOwner.ErrorEvent#getVariableOwner()
		 */
		public VariableOwner getVariableOwner() {
			return this.owner;
		}

	}

	/** 
	 * Resolves the VariableOwners needed from the request and sort
	 * them to assure that the dependencies are met (as well as possible).
	 * 
	 * @param listeners 
	 * @return List of variable list changers, that are needed for handling
	 * all the variables in the request
	 */
	private List getDependencySortedListenerList(Set listeners) {

		LinkedList resultNormal = new LinkedList();
		LinkedList resultImmediate = new LinkedList();

		// Go trough the listeners and either add them to result or resolve
		// their dependencies
		HashMap deepdeps = new HashMap();
		LinkedList unresolved = new LinkedList();
		for (Iterator li = listeners.iterator(); li.hasNext();) {

			VariableOwner listener = (VariableOwner) li.next();
			if (listener != null) {
				Set dependencies = listener.getDirectDependencies();

				// The listeners with no dependencies are added to the front of the
				// list directly
				if (dependencies == null || dependencies.isEmpty()) {
					if (listener.isImmediate())
						resultImmediate.addFirst(listener);
					else
						resultNormal.addFirst(listener);
				}

				// Resolve deep dependencies for the listeners with dependencies
				// (the listeners will be added to the end of results in correct
				// dependency order later). Also the dependencies of all the
				// depended listeners are resolved.
				else if (deepdeps.get(listener) == null) {

					// Set the fifo for unresolved parents to contain only the
					// listener to be resolved
					unresolved.clear();
					unresolved.add(listener);

					// Resolve dependencies
					HashSet tmpdeepdeps = new HashSet();
					while (!unresolved.isEmpty()) {

						VariableOwner l =
							(VariableOwner) unresolved.removeFirst();
						if (!tmpdeepdeps.contains(l)) {
							tmpdeepdeps.add(l);
							if (deepdeps.containsKey(l)) {
								tmpdeepdeps.addAll((Set) deepdeps.get(l));
							} else {
								Set deps = l.getDirectDependencies();
								if (deps != null && !deps.isEmpty())
									for (Iterator di = deps.iterator();
										di.hasNext();
										) {
										Object d = di.next();
										if (d != null
											&& !tmpdeepdeps.contains(d))
											unresolved.addLast(d);
									}
							}
						}
					}

					tmpdeepdeps.remove(listener);
					deepdeps.put(listener, tmpdeepdeps);
				}
			}
		}

		// Adds the listeners with dependencies in sane order to the result
		for (Iterator li = deepdeps.keySet().iterator(); li.hasNext();) {
			VariableOwner l = (VariableOwner) li.next();
			boolean immediate = l.isImmediate();

			// Adds each listener after the last depended listener already in
			// the list
			int index = -1;
			for (Iterator di = ((Set) deepdeps.get(l)).iterator();
				di.hasNext();
				) {
				int k;
				Object depended = di.next();
				if (immediate) {
					k = resultImmediate.lastIndexOf(depended);				
				}else {
					k = resultNormal.lastIndexOf(depended);								
				}				
				if (k > index)
					index = k;
			}
			if (immediate) {
				resultImmediate.add(index + 1, l);
			} else {
				resultNormal.add(index + 1, l);			
			}
		}

		// Appends immediate listeners to normal listeners
		// This way the normal handlers are always called before
		// immediate ones
		resultNormal.addAll(resultImmediate);
		return resultNormal;
	}
}
