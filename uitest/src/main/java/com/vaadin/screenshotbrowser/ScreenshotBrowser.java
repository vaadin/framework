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
package com.vaadin.screenshotbrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

public class ScreenshotBrowser extends UI {
    private static final File screenshotDir = findScreenshotDir();

    /*-
     * Groups:
     * 1 - test class
     * 2 - test method
     * 3 - platform
     * 4 - browser name
     * 5 - browser version
     * 6 - additional qualifiers
     * 7 - identifier
     */
    private static final Pattern screenshotNamePattern = Pattern
            .compile("(.+?)-(.+?)_(.+?)_(.+?)_(.+?)(_.+)?_(.+?)\\.png\\.html");

    public static enum Action {
        ACCEPT {
            @Override
            public void apply(File screenshotFile) {
                File targetFile = new File(getReferenceDir(),
                        screenshotFile.getName());

                // Delete previous file as well as any alternatives
                if (targetFile.exists()) {
                    for (int i = 1; true; i++) {
                        File alternative = getAlternative(targetFile, i);
                        if (alternative.exists()) {
                            alternative.delete();
                        } else {
                            break;
                        }
                    }
                    targetFile.delete();
                }

                screenshotFile.renameTo(targetFile);
            }
        },
        IGNORE {
            @Override
            public void apply(File screenshotFile) {
                screenshotFile.delete();
            }
        },
        ALTERNATIVE {
            @Override
            public void apply(File screenshotFile) {
                File baseFile = new File(getReferenceDir(),
                        screenshotFile.getName());

                // Iterate until we find the first alternative id not yet used
                File targetFile = baseFile;
                int alternativeNumber = 1;
                while (targetFile.exists()) {
                    targetFile = getAlternative(baseFile, alternativeNumber++);
                }

                screenshotFile.renameTo(targetFile);
            }
        };

        public void commit(File htmlFile) {
            String screenshotName = htmlFile.getName().substring(0,
                    htmlFile.getName().length() - ".html".length());

            apply(new File(htmlFile.getParentFile(), screenshotName));

            htmlFile.delete();
        }

        private static File getReferenceDir() {
            return new File(screenshotDir, "reference");
        }

        private static File getAlternative(File baseFile,
                int alternativeNumber) {
            assert alternativeNumber >= 1;
            String alternativeName = baseFile.getName().replaceFirst("\\.png",
                    "_" + alternativeNumber + ".png");
            return new File(baseFile.getParentFile(), alternativeName);
        }

        protected abstract void apply(File screenshotFile);
    }

    public static class ComparisonFailure {
        private final Matcher matcher;
        private final File file;

        private Action action;

        public ComparisonFailure(File file) {
            this.file = file;
            matcher = screenshotNamePattern.matcher(file.getName());
            if (!matcher.matches()) {
                throw new RuntimeException("Could not parse screenshot name "
                        + file.getAbsolutePath());
            }
        }

        public File getFile() {
            return file;
        }

        public String getName() {
            return matcher.group();
        }

        public String getTestClass() {
            return matcher.group(1);
        }

        public String getTestMethod() {
            return matcher.group(2);
        }

        public String getBrowser() {
            return matcher.group(4) + " " + matcher.group(5);
        }

        public String getQualifiers() {
            return matcher.group(6);
        }

        public String getIdentifier() {
            return matcher.group(7);
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public Action getAction() {
            return action;
        }
    }

    private class Viewer extends CustomComponent {
        private BrowserFrame preview = new BrowserFrame();
        private VerticalLayout left = new VerticalLayout();
        private HorizontalLayout root = new HorizontalLayout(left, preview);

        private Collection<ComparisonFailure> items;

        public Viewer() {
            preview.setWidth("1500px");
            preview.setHeight("100%");

            left.setMargin(true);
            left.setSpacing(true);
            left.setSizeFull();

            left.addComponent(
                    createActionButton("Accept changes", 'j', Action.ACCEPT));
            left.addComponent(
                    createActionButton("Ignore changes", 'k', Action.IGNORE));
            left.addComponent(createActionButton("Use as alternative", 'l',
                    Action.ALTERNATIVE));
            left.addComponent(
                    new Button("Clear action", createSetActionListener(null)));

            left.addComponent(createSpacer());
            left.addComponent(
                    new Button("Commit actions", new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            commitActions();
                        }
                    }));

            left.addComponent(createSpacer());
            left.addComponent(new Button("Refresh", new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    refreshTableContainer();
                }
            }));

            Label expandSpacer = createSpacer();
            left.addComponent(expandSpacer);
            left.setExpandRatio(expandSpacer, 1);

            left.addComponent(new Label(
                    "Press the j, k or l keys to quickly select an action for the selected item."));

            root.setExpandRatio(left, 1);
            root.setSizeFull();

            setCompositionRoot(root);
            setHeight("850px");
            setWidth("100%");
        }

        private Button createActionButton(String caption, char shortcut,
                Action action) {
            Button button = new Button(
                    caption + " <strong>" + shortcut + "</strong>",
                    createSetActionListener(action));
            button.setCaptionAsHtml(true);
            return button;
        }

        private Label createSpacer() {
            // Poor man's spacer, non-breaking space
            return new Label("\u00a0");
        }

        private ClickListener createSetActionListener(final Action action) {
            return new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    setActions(action);
                }
            };
        }

        public void setActions(final Action action) {
            for (ComparisonFailure comparisonFailure : items) {
                comparisonFailure.setAction(action);
            }
            table.refreshRowCache();
        }

        public void setItems(Collection<ComparisonFailure> items) {
            this.items = items;

            if (items.size() == 1) {
                ComparisonFailure failure = items.iterator().next();
                preview.setSource(new FileResource(failure.getFile()));
            } else {
                preview.setSource(new ExternalResource("about:blank"));
            }
        }
    }

    private final Table table = new Table();
    private final Viewer viewer = new Viewer();

    @Override
    protected void init(VaadinRequest request) {
        table.setWidth("100%");
        table.setHeight("100%");

        table.setMultiSelect(true);
        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                @SuppressWarnings("unchecked")
                Collection<ComparisonFailure> selectedRows = (Collection<ComparisonFailure>) table
                        .getValue();

                viewer.setItems(selectedRows);
            }
        });

        table.addShortcutListener(
                createShortcutListener(KeyCode.J, Action.ACCEPT));
        table.addShortcutListener(
                createShortcutListener(KeyCode.K, Action.IGNORE));
        table.addShortcutListener(
                createShortcutListener(KeyCode.L, Action.ALTERNATIVE));

        refreshTableContainer();

        VerticalLayout mainLayout = new VerticalLayout(table, viewer);
        mainLayout.setExpandRatio(table, 1);
        mainLayout.setSizeFull();

        setSizeFull();
        setContent(mainLayout);

        table.focus();
    }

    private void commitActions() {
        for (ComparisonFailure comparisonFailure : getContainer()
                .getItemIds()) {
            Action action = comparisonFailure.getAction();
            if (action != null) {
                action.commit(comparisonFailure.getFile());
            }
        }

        refreshTableContainer();
    }

    private ShortcutListener createShortcutListener(int keyCode,
            final Action action) {
        return new ShortcutListener(action.toString(), keyCode, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                viewer.setActions(action);
                selectNextWithoutAction();
            }
        };
    }

    private void selectNextWithoutAction() {
        Collection<?> selected = (Collection<?>) table.getValue();
        BeanItemContainer<ComparisonFailure> container = getContainer();

        // Find where to start
        ComparisonFailure candidate;
        if (selected == null || selected.isEmpty()) {
            candidate = container.firstItemId();
        } else {
            candidate = (ComparisonFailure) selected.iterator().next();
        }

        // Find first one without action
        while (candidate != null && candidate.getAction() != null) {
            candidate = container.nextItemId(candidate);
        }

        // Select it
        if (candidate == null) {
            table.setValue(Collections.emptySet());
        } else {
            table.setValue(Collections.singleton(candidate));
        }
    }

    @SuppressWarnings("unchecked")
    private BeanItemContainer<ComparisonFailure> getContainer() {
        return (BeanItemContainer<ComparisonFailure>) table
                .getContainerDataSource();
    }

    private void refreshTableContainer() {
        File errorsDir = new File(screenshotDir, "errors");

        File[] failures = errorsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".html");
            }
        });

        BeanItemContainer<ComparisonFailure> container = new BeanItemContainer<>(
                ComparisonFailure.class);
        for (File failure : failures) {
            container.addBean(new ComparisonFailure(failure));
        }

        table.setContainerDataSource(container);
        table.setVisibleColumns("testClass", "testMethod", "browser",
                "qualifiers", "identifier", "action");
        if (container.size() > 0) {
            table.select(container.firstItemId());
        }
    }

    private static File findScreenshotDir() {
        File propertiesFile = new File(
                "../work/eclipse-run-selected-test.properties");
        if (!propertiesFile.exists()) {
            throw new RuntimeException(
                    "File " + propertiesFile.getAbsolutePath() + " not found.");
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);

            Properties properties = new Properties();
            properties.load(in);
            String screenShotDirName = properties
                    .getProperty("com.vaadin.testbench.screenshot.directory");
            if (screenShotDirName == null
                    || screenShotDirName.startsWith("<")) {
                throw new RuntimeException(
                        "com.vaadin.testbench.screenshot.directory has not been configred in "
                                + propertiesFile.getAbsolutePath());
            }
            File screenshotDir = new File(screenShotDirName);
            if (!screenshotDir.isDirectory()) {
                throw new RuntimeException(screenshotDir.getAbsolutePath()
                        + " is not a directory");
            }
            return screenshotDir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
