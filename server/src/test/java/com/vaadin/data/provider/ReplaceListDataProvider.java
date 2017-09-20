package com.vaadin.data.provider;

import java.util.List;
import java.util.stream.Stream;

/**
 * A dummy data provider for testing item replacement and stale elements.
 */
public class ReplaceListDataProvider
        extends AbstractDataProvider<StrBean, Void> {

    private final List<StrBean> backend;

    public ReplaceListDataProvider(List<StrBean> items) {
        backend = items;
    }

    @Override
    public void refreshItem(StrBean item) {
        if (replaceItem(item)) {
            super.refreshItem(item);
        }
    }

    private boolean replaceItem(StrBean item) {
        for (int i = 0; i < backend.size(); ++i) {
            if (getId(backend.get(i)).equals(getId(item))) {
                if (backend.get(i).equals(item)) {
                    return false;
                }
                backend.remove(i);
                backend.add(i, item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query<StrBean, Void> t) {
        return backend.size();
    }

    @Override
    public Stream<StrBean> fetch(Query<StrBean, Void> query) {
        return backend.stream().skip(query.getOffset()).limit(query.getLimit());
    }

    public boolean isStale(StrBean item) {
        Object id = getId(item);
        boolean itemExistsInBackEnd = backend.contains(item);
        boolean backEndHasInstanceWithSameId = backend.stream().map(this::getId)
                .filter(i -> id.equals(i)).count() == 1;
        return !itemExistsInBackEnd && backEndHasInstanceWithSameId;
    }

    @Override
    public Object getId(StrBean item) {
        return item.getId();
    }
}
