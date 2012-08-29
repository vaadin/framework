/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

public interface TreeImages extends com.google.gwt.user.client.ui.TreeImages {

    /**
     * An image indicating an open branch.
     * 
     * @return a prototype of this image
     * @gwt.resource com/vaadin/terminal/gwt/public/default/tree/img/expanded
     *               .png
     */
    @Override
    AbstractImagePrototype treeOpen();

    /**
     * An image indicating a closed branch.
     * 
     * @return a prototype of this image
     * @gwt.resource com/vaadin/terminal/gwt/public/default/tree/img/collapsed
     *               .png
     */
    @Override
    AbstractImagePrototype treeClosed();

}
