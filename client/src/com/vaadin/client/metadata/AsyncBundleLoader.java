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
package com.vaadin.client.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.client.Profiler;

public abstract class AsyncBundleLoader {
    public enum State {
        NOT_STARTED, LOADING, LOADED, ERROR;
    }

    private State state = State.NOT_STARTED;

    private Throwable error = null;

    private List<BundleLoadCallback> callbacks = new ArrayList<BundleLoadCallback>();

    private final String packageName;

    private final String[] indentifiers;

    public AsyncBundleLoader(String packageName, String[] indentifiers) {
        this.packageName = packageName;
        this.indentifiers = indentifiers;
    }

    protected abstract void load(TypeDataStore store);

    public List<BundleLoadCallback> setError(Throwable error) {
        assert state == State.LOADING;
        state = State.ERROR;
        this.error = error;

        return clearCallbacks();
    }

    public Throwable getError() {
        return error;
    }

    public State getState() {
        return state;
    }

    public List<BundleLoadCallback> getCallback() {
        return Collections.unmodifiableList(callbacks);
    }

    public void load(BundleLoadCallback callback, TypeDataStore store) {
        assert state == State.NOT_STARTED;
        Profiler.enter("AsyncBundleLoader.load");
        state = State.LOADING;
        addCallback(callback);
        load(store);
        Profiler.leave("AsyncBundleLoader.load");
    }

    public void addCallback(BundleLoadCallback callback) {
        assert state == State.LOADING;
        if (callback != null) {
            callbacks.add(callback);
        }
    }

    public List<BundleLoadCallback> setLoaded() {
        assert state == State.LOADING;
        state = State.LOADED;

        return clearCallbacks();
    }

    private List<BundleLoadCallback> clearCallbacks() {
        List<BundleLoadCallback> callbacks = this.callbacks;
        this.callbacks = null;
        return callbacks;
    }

    public String getName() {
        return packageName;
    }

    public String[] getIndentifiers() {
        return indentifiers;
    }

}
