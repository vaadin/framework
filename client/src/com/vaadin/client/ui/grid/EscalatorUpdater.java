/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui.grid;


/**
 * A functional interface that allows client code to define how a certain row in
 * Escalator will be displayed. The contents of an escalator's header, body and
 * footer are rendered by their respective updaters.
 * <p>
 * The updater is responsible for internally handling all remote communication,
 * should the displayed data need to be fetched remotely.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see RowContainer#setEscalatorUpdater(EscalatorUpdater)
 * @see Escalator#getHeader()
 * @see Escalator#getBody()
 * @see Escalator#getFooter()
 */
public interface EscalatorUpdater {
    /** An {@link EscalatorUpdater} that doesn't render anything. */
    public static final EscalatorUpdater NULL = new EscalatorUpdater() {
        @Override
        public void updateCells(final Row row,
                final Iterable<Cell> cellsToUpdate) {
            // NOOP
        }
    };

    /**
     * Renders a row contained in a row container.
     * <p>
     * <em>Note:</em> If rendering of cells is deferred (e.g. because
     * asynchronous data retrieval), this method is responsible for explicitly
     * displaying some placeholder data (empty content is valid). Because the
     * cells (and rows) in an escalator are recycled, failing to reset a cell
     * will lead to invalid data being displayed in the escalator.
     * <p>
     * For performance reasons, the escalator will never autonomously clear any
     * data in a cell.
     * 
     * @param row
     *            information about the row to update. <em>Note:</em> You should
     *            not store nor reuse this reference
     * @param cellsToUpdate
     *            a collection of cells which need to be updated. <em>Note:</em>
     *            You should neither store nor reuse the reference to the list,
     *            nor to the individual cells
     */
    public void updateCells(Row row, Iterable<Cell> cellsToUpdate);
}
