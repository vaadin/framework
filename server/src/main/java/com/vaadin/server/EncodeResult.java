/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server;

import java.io.Serializable;

import elemental.json.JsonValue;

public class EncodeResult implements Serializable {
    private final JsonValue encodedValue;
    private final JsonValue diff;

    public EncodeResult(JsonValue encodedValue) {
        this(encodedValue, null);
    }

    public EncodeResult(JsonValue encodedValue, JsonValue diff) {
        this.encodedValue = encodedValue;
        this.diff = diff;
    }

    public JsonValue getEncodedValue() {
        return encodedValue;
    }

    public JsonValue getDiff() {
        return diff;
    }

    public JsonValue getDiffOrValue() {
        JsonValue diff = getDiff();
        if (diff != null) {
            return diff;
        } else {
            return getEncodedValue();
        }
    }
}
