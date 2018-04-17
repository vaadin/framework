package com.vaadin.tests.server;

import com.vaadin.server.KeyMapper;

/**
 * The test checks the same functionality as {@link KeyMapperTest} does, but
 * uses custom {@code identifierGetter} instead of default trivial one.
 * {@code BrokenBean} intentionally has broken {@code hashCode} and
 * {@code equals}, and the test should pass despite of that, because
 * {@code BrokenBean.getId()} is used for bean identification.
 */
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
        keyMapper.setIdentifierGetter(BrokenBean::getId);
        return (KeyMapper) keyMapper;
    }

}
