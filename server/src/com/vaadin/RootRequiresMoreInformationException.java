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

package com.vaadin;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;

/**
 * Exception that is thrown to indicate that creating or initializing the root
 * requires information detailed from the web browser ({@link BrowserDetails})
 * to be present.
 * 
 * This exception may not be thrown if that information is already present in
 * the current WrappedRequest.
 * 
 * @see Application#getRoot(WrappedRequest)
 * @see WrappedRequest#getBrowserDetails()
 * 
 * @since 7.0
 */
public class RootRequiresMoreInformationException extends Exception {
    // Nothing of interest here
}
