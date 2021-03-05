/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.ui.components.grid;

import java.io.Serializable;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.ui.Component;

/**
 * An individual cell on a Grid header row.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public interface HeaderCell extends Serializable {

    /**
     * Returns the textual caption of this cell.
     *
     * @return the header caption
     */
    public String getText();

    /**
     * Sets the textual caption of this cell.
     *
     * @param text
     *            the header caption to set, not null
     */
    public void setText(String text);

    /**
     * Returns the HTML content displayed in this cell.
     *
     * @return the html
     *
     */
    public String getHtml();

    /**
     * Sets the HTML content displayed in this cell.
     *
     * @param html
     *            the html to set
     */
    public void setHtml(String html);

    /**
     * Returns the component displayed in this cell.
     *
     * @return the component
     */
    public Component getComponent();

    /**
     * Sets the component displayed in this cell.
     *
     * @param component
     *            the component to set
     */
    public void setComponent(Component component);

    /**
     * Returns the type of content stored in this cell.
     *
     * @return cell content type
     */
    public GridStaticCellType getCellType();

    /**
     * Gets the column id where this cell is.
     *
     * @return column id for this cell
     */
    public String getColumnId();

    /**
     * Returns the custom style name for this cell.
     *
     * @return the style name or null if no style name has been set
     */
    public String getStyleName();

    /**
     * Sets a custom style name for this cell.
     *
     * @param styleName
     *            the style name to set or null to not use any style name
     */
    public void setStyleName(String styleName);

    /**
     * Gets the tooltip for the cell.
     * <p>
     * The tooltip is shown in the mode returned by
     * {@link #getDescriptionContentMode()}.
     *
     * @return the tooltip text for this cell
     * @since 8.4
     */
    public String getDescription();

    /**
     * Sets the tooltip for the cell.
     * <p>
     * By default, tooltips are shown as plain text. For HTML tooltips, see
     * {@link #setDescription(String, ContentMode)} or
     * {@link #setDescriptionContentMode(ContentMode)}.
     *
     * @param description
     *            the tooltip to show when hovering the cell
     * @since 8.4
     */
    public void setDescription(String description);

    /**
     * Sets the tooltip for the cell to be shown with the given content mode.
     *
     * @see ContentMode
     * @param description
     *            the tooltip to show when hovering the cell
     * @param descriptionContentMode
     *            the content mode to use for the tooltip (HTML or plain text)
     * @since 8.4
     */
    public void setDescription(String description,
            ContentMode descriptionContentMode);

    /**
     * Gets the content mode for the tooltip.
     * <p>
     *
     * @see ContentMode
     * @return the content mode for the tooltip
     * @since 8.4
     */
    public ContentMode getDescriptionContentMode();

    /**
     * Sets the content mode for the tooltip.
     *
     * @see ContentMode
     * @param descriptionContentMode
     *            the content mode for the tooltip
     * @since 8.4
     */
    public void setDescriptionContentMode(ContentMode descriptionContentMode);
}
