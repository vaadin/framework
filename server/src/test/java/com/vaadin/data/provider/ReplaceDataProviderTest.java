package com.vaadin.data.provider;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class ReplaceDataProviderTest {

    public static class BeanWithEquals extends Bean {

        BeanWithEquals(int id) {
            super(id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BeanWithEquals that = (BeanWithEquals) o;

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    public static class Bean {
        protected final int id;
        private final String fluff;

        Bean(int id) {
            this.id = id;
            this.fluff = "Fluff #" + id;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public String getFluff() {
            return fluff;
        }

    }

    @Test
    public void testBeanEquals() {
        doTest(BeanWithEquals::new);
    }

    @Test
    public void testBeanSame() {
        doTest(Bean::new);
    }

    private <SOME_BEAN> void doTest(IntFunction<SOME_BEAN> beanConstructor) {

        DataCommunicator<SOME_BEAN> dataCommunicator = new DataCommunicator<>();

        List<SOME_BEAN> beans1 = createCollection(beanConstructor);

        ListDataProvider<SOME_BEAN> dataProvider = new ListDataProvider<>(
                beans1);

        dataCommunicator.setDataProvider(dataProvider, null);
        dataCommunicator.pushData(1, beans1);

        SOME_BEAN bean1_17 = beans1.get(17);
        String key1_17 = dataCommunicator.getKeyMapper().key(bean1_17);

        assertSame(bean1_17, dataCommunicator.getKeyMapper().get(key1_17));

        List<SOME_BEAN> beans2 = createCollection(beanConstructor);

        dataProvider = new ListDataProvider<>(beans2);
        dataCommunicator.setDataProvider(dataProvider, null);
        dataCommunicator.pushData(1, beans2);

        SOME_BEAN bean2_17 = beans2.get(17);
        String key2_17 = dataCommunicator.getKeyMapper().key(bean2_17);

        assertSame(bean2_17, dataCommunicator.getKeyMapper().get(key2_17));
        assertNotEquals(key2_17, key1_17);
        assertNull(dataCommunicator.getKeyMapper().get(key1_17));
    }

    private <SOME_BEAN> List<SOME_BEAN> createCollection(
            IntFunction<SOME_BEAN> beanConstructor) {
        return IntStream.range(1, 100).mapToObj(beanConstructor)
                .collect(Collectors.toList());
    }
}
