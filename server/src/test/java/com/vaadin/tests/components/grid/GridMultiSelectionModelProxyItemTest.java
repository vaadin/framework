package com.vaadin.tests.components.grid;

import com.liferay.portal.kernel.atom.AtomEntryContent;
import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GridMultiSelectionModelProxyItemTest {

    private List<String> data = IntStream.range(0, 100).boxed()
            .map(i -> "String " + i).collect(Collectors.toList());
    private Grid<AtomicReference<String>> proxyGrid = new Grid<>();
    private MultiSelectionModel<AtomicReference<String>> model;
    private AtomicReference<Set<AtomicReference<String>>> selectionEvent = new AtomicReference<>();

    @Before
    public void setup() {
        proxyGrid.setDataProvider(new CallbackDataProvider<>(
                q -> data.stream().map(AtomicReference::new).skip(q.getOffset())
                        .limit(q.getLimit()),
                q -> data.size(), AtomicReference::get));
        model = (MultiSelectionModel<AtomicReference<String>>) proxyGrid
                .setSelectionMode(Grid.SelectionMode.MULTI);
        model.addSelectionListener(e -> {
            selectionEvent.set(e.getAllSelectedItems());
        });
    }

    @Test
    public void testSelectAllWithProxyDataProvider() {
        model.selectAll();
        assertEquals("Item count mismatch on first select all", 100,
                getSelectionEvent().size());
        model.deselect(model.getFirstSelectedItem().orElseThrow(
                () -> new IllegalStateException("Items should be selected")));
        assertEquals("Item count mismatch on deselect", 99,
                getSelectionEvent().size());
        model.selectAll();
        assertEquals("Item count mismatch on second select all", 100,
                getSelectionEvent().size());
    }

    @Test
    public void testUpdateSelectionWithDuplicateEntries() {
        List<String> selection = data.stream().filter(s -> s.contains("1"))
                .collect(Collectors.toList());
        model.updateSelection(selection.stream().map(AtomicReference::new)
                .collect(Collectors.toSet()), Collections.emptySet());
        assertEquals("Failure in initial selection", selection.size(),
                getSelectionEvent().size());

        String toRemove = model.getFirstSelectedItem().map(AtomicReference::get)
                .orElseThrow(() -> new IllegalStateException(
                        "Items should be selected"));
        model.updateSelection(
                Stream.of(toRemove).map(AtomicReference::new)
                        .collect(Collectors.toSet()),
                Stream.of(toRemove).map(AtomicReference::new)
                        .collect(Collectors.toSet()));
        assertNull(
                "Selection should not change when selecting and deselecting once",
                selectionEvent.get());

        Set<AtomicReference<String>> added = new LinkedHashSet<>();
        Set<AtomicReference<String>> removed = new LinkedHashSet<>();
        for (int i = 0; i < 20; ++i) {
            added.add(new AtomicReference<>(toRemove));
            removed.add(new AtomicReference<>(toRemove));
        }
        model.updateSelection(added, removed);
        assertNull(
                "Selection should not change when selecting and deselecting 20 times",
                selectionEvent.get());

        removed.add(new AtomicReference<>(toRemove));
        model.updateSelection(added, removed);
        assertEquals("Item should have been deselected", selection.size() - 1,
                getSelectionEvent().size());
    }

    private Set<AtomicReference<String>> getSelectionEvent() {
        Optional<Set<AtomicReference<String>>> eventOptional = Optional
                .of(selectionEvent.get());
        selectionEvent.set(null);
        return eventOptional.orElseThrow(() -> new IllegalStateException(
                "Expected selection event never happened"));
    }
}
