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
package com.vaadin.tests.themes.valo;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class Tables extends VerticalLayout implements View {

    final Container normalContainer = ValoThemeUI.generateContainer(200, false);
    final Container hierarchicalContainer = ValoThemeUI.generateContainer(200,
            true);

    CheckBox hierarchical = new CheckBox("Hierarchical");
    CheckBox footer = new CheckBox("Footer", true);
    CheckBox sized = new CheckBox("Sized");
    CheckBox expandRatios = new CheckBox("Expand ratios");
    CheckBox stripes = new CheckBox("Stripes", true);
    CheckBox verticalLines = new CheckBox("Vertical lines", true);
    CheckBox horizontalLines = new CheckBox("Horizontal lines", true);
    CheckBox borderless = new CheckBox("Borderless");
    CheckBox headers = new CheckBox("Header", true);
    CheckBox compact = new CheckBox("Compact");
    CheckBox small = new CheckBox("Small");
    CheckBox rowIndex = new CheckBox("Row index", false);
    CheckBox rowIcon = new CheckBox("Row icon", false);
    CheckBox rowCaption = new CheckBox("Row caption", false);
    CheckBox componentsInCells = new CheckBox("Components in Cells", false);

    Table table;

    public Tables() {
        setMargin(true);
        setSpacing(true);

        Label h1 = new Label("Tables");
        h1.addStyleName("h1");
        addComponent(h1);

        HorizontalLayout wrap = new HorizontalLayout();
        wrap.addStyleName("wrapping");
        wrap.setSpacing(true);
        addComponent(wrap);

        wrap.addComponents(hierarchical, footer, sized, expandRatios, stripes,
                verticalLines, horizontalLines, borderless, headers, compact,
                small, rowIndex, rowCaption, rowIcon, componentsInCells);

        ValueChangeListener update = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (table == null) {
                    table = new Table();
                    table.setContainerDataSource(normalContainer);
                    addComponent(table);
                }
                if (hierarchical.getValue() && table instanceof Table) {
                    removeComponent(table);
                    table = new TreeTable();
                    table.setContainerDataSource(hierarchicalContainer);
                    addComponent(table);
                } else if (!hierarchical.getValue()
                        && table instanceof TreeTable) {
                    removeComponent(table);
                    table = new Table();
                    table.setContainerDataSource(normalContainer);
                    addComponent(table);
                }

                configure(table, footer.getValue(), sized.getValue(),
                        expandRatios.getValue(), stripes.getValue(),
                        verticalLines.getValue(), horizontalLines.getValue(),
                        borderless.getValue(), headers.getValue(),
                        compact.getValue(), small.getValue(),
                        rowIndex.getValue(), rowCaption.getValue(),
                        rowIcon.getValue(), componentsInCells.getValue());
            }
        };

        hierarchical.addValueChangeListener(update);
        footer.addValueChangeListener(update);
        sized.addValueChangeListener(update);
        expandRatios.addValueChangeListener(update);
        stripes.addValueChangeListener(update);
        verticalLines.addValueChangeListener(update);
        horizontalLines.addValueChangeListener(update);
        borderless.addValueChangeListener(update);
        headers.addValueChangeListener(update);
        compact.addValueChangeListener(update);
        small.addValueChangeListener(update);
        rowIndex.addValueChangeListener(update);
        rowCaption.addValueChangeListener(update);
        rowIcon.addValueChangeListener(update);
        componentsInCells.addValueChangeListener(update);

        footer.setValue(false);

    }

    static void configure(Table table, boolean footer, boolean sized,
            boolean expandRatios, boolean stripes, boolean verticalLines,
            boolean horizontalLines, boolean borderless, boolean headers,
            boolean compact, boolean small, boolean rowIndex,
            boolean rowCaption, boolean rowIcon, boolean componentsInRows) {
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setSortEnabled(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setPageLength(6);
        table.addActionHandler(ValoThemeUI.getActionHandler());
        table.setDragMode(TableDragMode.MULTIROW);
        table.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Notification.show(event.getTransferable().toString());
            }
        });
        table.setColumnAlignment(ValoThemeUI.DESCRIPTION_PROPERTY, Align.RIGHT);
        table.setColumnAlignment(ValoThemeUI.INDEX_PROPERTY, Align.CENTER);

        table.removeContainerProperty("textfield");
        table.removeGeneratedColumn("textfield");
        table.removeContainerProperty("button");
        table.removeGeneratedColumn("button");
        table.removeContainerProperty("label");
        table.removeGeneratedColumn("label");
        table.removeContainerProperty("checkbox");
        table.removeGeneratedColumn("checkbox");
        table.removeContainerProperty("datefield");
        table.removeGeneratedColumn("datefield");
        table.removeContainerProperty("combobox");
        table.removeGeneratedColumn("combobox");
        table.removeContainerProperty("optiongroup");
        table.removeGeneratedColumn("optiongroup");
        table.removeContainerProperty("slider");
        table.removeGeneratedColumn("slider");
        table.removeContainerProperty("progress");
        table.removeGeneratedColumn("progress");

        if (componentsInRows) {
            table.addContainerProperty("textfield", TextField.class, null);
            table.addGeneratedColumn("textfield", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    TextField tf = new TextField();
                    tf.setInputPrompt("Type here…");
                    // tf.addStyleName("compact");
                    if ((Integer) itemId % 2 == 0) {
                        tf.addStyleName("borderless");
                    }
                    return tf;
                }
            });

            table.addContainerProperty("datefield", TextField.class, null);
            table.addGeneratedColumn("datefield", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    DateField tf = new DateField();
                    tf.addStyleName("compact");
                    if ((Integer) itemId % 2 == 0) {
                        tf.addStyleName("borderless");
                    }
                    return tf;
                }
            });

            table.addContainerProperty("combobox", TextField.class, null);
            table.addGeneratedColumn("combobox", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    ComboBox tf = new ComboBox();
                    tf.setInputPrompt("Select");
                    tf.addStyleName("compact");
                    if ((Integer) itemId % 2 == 0) {
                        tf.addStyleName("borderless");
                    }
                    return tf;
                }
            });

            table.addContainerProperty("button", Button.class, null);
            table.addGeneratedColumn("button", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    Button b = new Button("Button");
                    b.addStyleName("small");
                    return b;
                }
            });

            table.addContainerProperty("label", TextField.class, null);
            table.addGeneratedColumn("label", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    Label label = new Label("Label component");
                    label.setSizeUndefined();
                    label.addStyleName("bold");
                    return label;
                }
            });

            table.addContainerProperty("checkbox", TextField.class, null);
            table.addGeneratedColumn("checkbox", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    CheckBox cb = new CheckBox(null, true);
                    return cb;
                }
            });

            table.addContainerProperty("optiongroup", TextField.class, null);
            table.addGeneratedColumn("optiongroup", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    OptionGroup op = new OptionGroup();
                    op.addItem("Male");
                    op.addItem("Female");
                    op.addStyleName("horizontal");
                    return op;
                }
            });

            table.addContainerProperty("slider", TextField.class, null);
            table.addGeneratedColumn("slider", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    Slider s = new Slider();
                    s.setValue(30.0);
                    return s;
                }
            });

            table.addContainerProperty("progress", TextField.class, null);
            table.addGeneratedColumn("progress", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    ProgressBar bar = new ProgressBar();
                    bar.setValue(0.7f);
                    return bar;
                }
            });
        }
        table.setFooterVisible(footer);
        if (footer) {
            table.setColumnFooter(ValoThemeUI.CAPTION_PROPERTY, "caption");
            table.setColumnFooter(ValoThemeUI.DESCRIPTION_PROPERTY,
                    "description");
            table.setColumnFooter(ValoThemeUI.ICON_PROPERTY, "icon");
            table.setColumnFooter(ValoThemeUI.INDEX_PROPERTY, "index");
        }

        if (sized) {
            table.setWidth("400px");
            table.setHeight("300px");
        } else {
            table.setSizeUndefined();
        }

        if (expandRatios) {
            if (!sized) {
                table.setWidth("100%");
            }
        }
        table.setColumnExpandRatio(ValoThemeUI.CAPTION_PROPERTY,
                expandRatios ? 1.0f : 0);
        table.setColumnExpandRatio(ValoThemeUI.DESCRIPTION_PROPERTY,
                expandRatios ? 1.0f : 0);

        if (!stripes) {
            table.addStyleName("no-stripes");
        } else {
            table.removeStyleName("no-stripes");
        }

        if (!verticalLines) {
            table.addStyleName("no-vertical-lines");
        } else {
            table.removeStyleName("no-vertical-lines");
        }

        if (!horizontalLines) {
            table.addStyleName("no-horizontal-lines");
        } else {
            table.removeStyleName("no-horizontal-lines");
        }

        if (borderless) {
            table.addStyleName("borderless");
        } else {
            table.removeStyleName("borderless");
        }

        if (!headers) {
            table.addStyleName("no-header");
        } else {
            table.removeStyleName("no-header");
        }

        if (compact) {
            table.addStyleName("compact");
        } else {
            table.removeStyleName("compact");
        }

        if (small) {
            table.addStyleName("small");
        } else {
            table.removeStyleName("small");
        }

        if (!rowIndex && !rowCaption && rowIcon) {
            table.setRowHeaderMode(RowHeaderMode.HIDDEN);
        }

        if (rowIndex) {
            table.setRowHeaderMode(RowHeaderMode.INDEX);
        }

        if (rowCaption) {
            table.setRowHeaderMode(RowHeaderMode.PROPERTY);
            table.setItemCaptionPropertyId(ValoThemeUI.CAPTION_PROPERTY);
        } else {
            table.setItemCaptionPropertyId(null);
        }

        if (rowIcon) {
            table.setRowHeaderMode(RowHeaderMode.ICON_ONLY);
            table.setItemIconPropertyId(ValoThemeUI.ICON_PROPERTY);
        } else {
            table.setItemIconPropertyId(null);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
