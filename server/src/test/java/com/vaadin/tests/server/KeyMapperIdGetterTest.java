package com.vaadin.tests.server;

import com.vaadin.server.KeyMapper;

public class KeyMapperIdGetterTest extends KeyMapperTest {


    private static class BrokenBean {
        private final Object id = new Object();

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        public Object getId() {
            return id;
        }
    }

    protected Object createObject() {
        return new BrokenBean();
    }

    protected KeyMapper<Object> createKeyMapper() {

        KeyMapper<BrokenBean> keyMapper = new KeyMapper<>();
        keyMapper.useIdentifierGetter(BrokenBean::getId);
        return (KeyMapper) keyMapper;
    }

}
