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
package com.vaadin.tests.dnd;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class DragAndDropCardShuffle extends AbstractTestUIWithLog {

    // Create cards
    private final Label ace = new Label("A");
    private final Label jack = new Label("J");
    private final Label queen = new Label("Q");
    private final Label king = new Label("K");

    // Create desk
    private HorizontalLayout desk = new HorizontalLayout();

    private final List<DragSourceExtension<Label>> sources = new ArrayList<>();
    private final List<DropTargetExtension<Label>> targets = new ArrayList<>();

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<EffectAllowed> effectAllowed = new NativeSelect<>(
                "Effect Allowed (source)");
        effectAllowed.setItems(EffectAllowed.values());
        effectAllowed.setValue(EffectAllowed.UNINITIALIZED);
        effectAllowed.setEmptySelectionAllowed(false);
        effectAllowed.addValueChangeListener(event -> sources
                .forEach(source -> source.setEffectAllowed(event.getValue())));

        NativeSelect<DropEffect> dropEffect = new NativeSelect<>(
                "Drop Effect (target)");
        dropEffect.setItems(DropEffect.values());
        dropEffect.addValueChangeListener(event -> targets
                .forEach(target -> target.setDropEffect(event.getValue())));
        CheckBox enableMobileSupport = new CheckBox("Mobile Support", false);
        enableMobileSupport.addValueChangeListener(event -> {
            setMobileHtml5DndEnabled(event.getValue());

            removeExtensions();
            setupExtensions();
        });

        setupExtensions();

        desk.addComponents(ace, jack, queen, king);

        addComponents(new HorizontalLayout(effectAllowed, dropEffect,
                enableMobileSupport), desk);

        // Add styling
        setStyle();
    }

    private void setupExtensions() {
        // Create UI and add extensions

        ace.setStyleName("card");
        addDragSourceExtension(ace);
        addDropTargetExtension(ace);

        jack.setStyleName("card");
        addDragSourceExtension(jack);
        addDropTargetExtension(jack);

        queen.setStyleName("card");
        addDragSourceExtension(queen);
        addDropTargetExtension(queen);

        king.setStyleName("card");
        addDragSourceExtension(king);
        addDropTargetExtension(king);
    }

    private void removeExtensions() {
        ace.removeExtension(ace.getExtensions().iterator().next());
        ace.removeExtension(ace.getExtensions().iterator().next());

        jack.removeExtension(jack.getExtensions().iterator().next());
        jack.removeExtension(jack.getExtensions().iterator().next());

        queen.removeExtension(queen.getExtensions().iterator().next());
        queen.removeExtension(queen.getExtensions().iterator().next());

        king.removeExtension(king.getExtensions().iterator().next());
        king.removeExtension(king.getExtensions().iterator().next());
    }

    private void addDragSourceExtension(Label source) {
        // Create and attach extension
        DragSourceExtension<Label> dragSource = new DragSourceExtension<>(
                source);

        // Add listeners
        dragSource.addDragStartListener(event -> {
            log(event.getComponent().getValue() + " dragstart, effectsAllowed="
                    + event.getEffectAllowed());
        });

        dragSource.addDragEndListener(event -> {
            log(event.getComponent().getValue() + " dragend, dropEffect="
                    + event.getDropEffect());
        });

        sources.add(dragSource);
    }

    private void addDropTargetExtension(Label target) {
        // Create and attach extension
        DropTargetExtension<Label> dropTarget = new DropTargetExtension<>(
                target);

        // Add listener
        dropTarget.addDropListener(event -> {
            event.getDragSourceExtension().ifPresent(dragSource -> {
                if (dragSource.getParent() instanceof Label) {
                    Label source = (Label) dragSource.getParent();

                    // Swap source and target components
                    desk.replaceComponent(target, source);

                    log(event.getComponent().getValue() + " drop received "
                            + source.getValue() + ", dropEffect="
                            + event.getDropEffect());
                } else {
                    log(event.getComponent().getValue()
                            + " drop received something else than card");
                }
            });
        });

        targets.add(dropTarget);
    }

    private void setStyle() {
        Page.Styles styles = Page.getCurrent().getStyles();

        styles.add(".card {" + "width: 150px;" + "height: 200px;"
                + "border: 1px solid black;" + "border-radius: 7px;"
                + "padding-left: 10px;" + "color: red;" + "font-weight: bolder;"
                + "font-size: 25px;" + "background-color: gainsboro;" + "}");
        styles.add(".v-label-drag-center {border-style: dashed;}");
        styles.add(".v-label-dragged {opacity: .4;}");
    }

    @Override
    protected String getTestDescription() {
        return "Shuffle cards with pure HTML5 drag and drop";
    }
}
