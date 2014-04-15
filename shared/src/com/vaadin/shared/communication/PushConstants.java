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
package com.vaadin.shared.communication;

import java.io.Serializable;

/**
 * Shared constants used by push.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class PushConstants implements Serializable {

    /**
     * The size, in <b>bytes</b>, of the receiving buffer used by some servers.
     * <p>
     * Should not be set to a value equal to or greater than 32768 due to a
     * Jetty 9.1 issue (see #13087)
     */
    public static final int WEBSOCKET_BUFFER_SIZE = 16384;

    /**
     * The maximum size, in <b>characters</b>, of a websocket message fragment.
     * This is a conservative maximum chosen so that the size in bytes will not
     * exceed {@link PushConstants#WEBSOCKET_BUFFER_SIZE} given a UTF-8 encoded
     * message.
     */
    public static final int WEBSOCKET_FRAGMENT_SIZE = WEBSOCKET_BUFFER_SIZE / 4 - 1;

    /**
     * The character used to mark message boundaries when messages may be split
     * into multiple fragments.
     */
    public static final char MESSAGE_DELIMITER = '|';
}
