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

package com.vaadin.data;

import java.io.Serializable;

/**
 * <p>
 * This interface defines the combination of <code>Validatable</code> and
 * <code>Buffered</code> interfaces. The combination of the interfaces defines
 * if the invalid data is committed to datasource.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface BufferedValidatable extends Buffered, Validatable,
        Serializable {

    /**
     * Tests if the invalid data is committed to datasource. The default is
     * <code>false</code>.
     */
    public boolean isInvalidCommitted();

    /**
     * Sets if the invalid data should be committed to datasource. The default
     * is <code>false</code>.
     */
    public void setInvalidCommitted(boolean isCommitted);
}
