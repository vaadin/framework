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

package com.vaadin.server;

import java.io.Serializable;

public class EncodeResult implements Serializable {
    private final Object encodedValue;
    private final Object diff;

    public EncodeResult(Object encodedValue) {
        this(encodedValue, null);
    }

    public EncodeResult(Object encodedValue, Object diff) {
        this.encodedValue = encodedValue;
        this.diff = diff;
    }

    public Object getEncodedValue() {
        return encodedValue;
    }

    public Object getDiff() {
        return diff;
    }

    public Object getDiffOrValue() {
        Object diff = getDiff();
        if (diff != null) {
            return diff;
        } else {
            return getEncodedValue();
        }
    }
}
