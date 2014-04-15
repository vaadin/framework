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
/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id: Selectors.java,v 1.1 2000/02/14 16:58:31 plehegar Exp $
 */
package com.vaadin.sass.internal.parser;

import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.Selector;

/**
 * @version $Revision: 1.1 $
 * @author  Philippe Le Hegaret
 */
class Selectors implements SelectorList {

    Selector[] selectors = new Selector[5];
    int      current;

    public Selector item(int index) {
	if ((index < 0) || (index >= current)) {
	    return null;
	}
	return selectors[index];
    }

    public Selector itemSelector(int index) {
	if ((index < 0) || (index >= current)) {
	    return null;
	}
	return selectors[index];
    }

    public int getLength() {
	return current;
    }

    void addSelector(Selector selector) {
	if (current == selectors.length) {
	    Selector[] old = selectors;
	    selectors = new Selector[old.length + old.length];
	    System.arraycopy(old, 0, selectors, 0, old.length);
	}
	selectors[current++] = selector;
    }
}
