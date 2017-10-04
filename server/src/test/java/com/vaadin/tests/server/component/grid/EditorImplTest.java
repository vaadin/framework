/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.Binder;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.grid.editor.EditorServerRpc;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.EditorCancelEvent;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorImpl;
import com.vaadin.ui.components.grid.EditorSaveEvent;
import com.vaadin.ui.components.grid.EditorSaveListener;

/**
 * @author Vaadin Ltd
 *
 */
public class EditorImplTest {

    public static class TestEditorImpl extends EditorImpl<Object> implements
            EditorSaveListener<Object>, EditorCancelListener<Object> {

        @Override
        public void doEdit(Object bean) {
            super.doEdit(bean);
        }

        public TestEditorImpl() {
            super(new PropertySet<Object>() {
                @Override
                public Stream<PropertyDefinition<Object, ?>> getProperties() {
                    return null;
                }

                @Override
                public Optional<PropertyDefinition<Object, ?>> getProperty(
                        String name) {
                    return null;
                }
            });

        }

        @Override
        public Grid<Object> getParent() {
            return new Grid<>();
        }

        EditorCancelEvent<Object> cancelEvent;

        EditorSaveEvent<Object> saveEvent;

        EditorServerRpc rpc;

        @Override
        public boolean isBuffered() {
            return true;
        }

        @Override
        protected void refresh(Object item) {
        }

        @Override
        public void onEditorCancel(EditorCancelEvent<Object> event) {
            assertNull(cancelEvent);
            cancelEvent = event;
        }

        @Override
        public void onEditorSave(EditorSaveEvent<Object> event) {
            assertNull(saveEvent);
            saveEvent = event;
        }

        @Override
        protected <T extends ServerRpc> void registerRpc(T implementation) {
            if (implementation instanceof EditorServerRpc) {
                rpc = (EditorServerRpc) implementation;
            }
            super.registerRpc(implementation);
        }
    }

    private Object beanToEdit = new Object();

    private TestEditorImpl editor;
    private Binder<Object> binder;

    @Before
    public void setUp() {
        editor = new TestEditorImpl();
        editor.addSaveListener(editor);
        editor.addCancelListener(editor);
        binder = Mockito.mock(Binder.class);
        editor.setBinder(binder);
        editor.setEnabled(true);
        editor.doEdit(beanToEdit);
    }

    @Test
    public void save_eventIsFired() {
        Mockito.when(binder.writeBeanIfValid(Mockito.any())).thenReturn(true);

        editor.save();

        assertNotNull(editor.saveEvent);
        assertNull(editor.cancelEvent);

        assertEquals(editor, editor.saveEvent.getSource());
        assertEquals(beanToEdit, editor.saveEvent.getBean());
    }

    @Test
    public void cancel_eventIsFired() {
        editor.cancel();

        assertNull(editor.saveEvent);
        assertNotNull(editor.cancelEvent);

        assertEquals(editor, editor.cancelEvent.getSource());

        assertEquals(beanToEdit, editor.cancelEvent.getBean());
    }

    @Test
    public void saveFromClient_eventIsFired() {
        Mockito.when(binder.writeBeanIfValid(Mockito.any())).thenReturn(true);

        editor.rpc.save();

        assertNotNull(editor.saveEvent);
        assertNull(editor.cancelEvent);

        assertEquals(editor, editor.saveEvent.getSource());

        assertEquals(beanToEdit, editor.saveEvent.getBean());
    }

    @Test
    public void cancelFromClient_eventIsFired() {
        editor.rpc.cancel(false);

        assertNull(editor.saveEvent);
        assertNotNull(editor.cancelEvent);

        assertEquals(editor, editor.cancelEvent.getSource());

        assertEquals(beanToEdit, editor.cancelEvent.getBean());
    }

    @Test
    public void cancelAfterSaveFromClient_eventIsNotFired() {
        editor.rpc.cancel(true);

        assertNull(editor.saveEvent);
        assertNull(editor.cancelEvent);
    }
}
