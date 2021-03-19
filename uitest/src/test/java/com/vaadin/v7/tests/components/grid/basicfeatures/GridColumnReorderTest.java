package com.vaadin.v7.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;

/**
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridColumnReorderTest extends GridBasicClientFeaturesTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement()
                .getHeaderCell(0, firstIndex).getText();
        final String secondHeaderText = getGridElement()
                .getHeaderCell(0, secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners",
                "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0,
                firstIndex);
        assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        assertEquals(firstHeaderText, headerCell.getText());

        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer
                .parseInt(columnReorderElement.getAttribute("columns"));
        assertEquals(1, eventIndex);

        // trigger another event
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer
                .parseInt(columnReorderElement.getAttribute("columns"));
        assertEquals(2, eventIndex);
    }

    @Test
    public void testColumnReorder_onReorder_columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement()
                .getHeaderCell(0, firstIndex).getText();
        final String secondHeaderText = getGridElement()
                .getHeaderCell(0, secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners",
                "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0,
                firstIndex);
        assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        assertEquals(firstHeaderText, headerCell.getText());

        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer
                .parseInt(columnReorderElement.getAttribute("columns"));
        assertEquals(1, eventIndex);

        // trigger another event
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer
                .parseInt(columnReorderElement.getAttribute("columns"));
        assertEquals(2, eventIndex);
    }

    @Test
    public void testColumnReorder_draggingSortedColumn_sortIndicatorShownOnDraggedElement() {
        // given
        toggleColumnReorder();
        toggleSortableColumn(0);
        sortColumn(0);

        // when
        startDragButDontDropOnDefaultColumnHeader(0);

        // then
        WebElement draggedElement = getDraggedHeaderElement();
        assertTrue(draggedElement.getAttribute("class").contains("sort"));
    }

    @Test
    public void testColumnReorder_draggingSortedColumn_sortStays() {
        // given
        toggleColumnReorder();
        toggleSortableColumn(0);
        sortColumn(0);

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertColumnIsSorted(1);
    }

    @Test
    public void testColumnReorder_draggingFocusedHeader_focusShownOnDraggedElement() {
        // given
        toggleColumnReorder();
        focusDefaultHeader(0);

        // when
        startDragButDontDropOnDefaultColumnHeader(0);

        // then
        WebElement draggedElement = getDraggedHeaderElement();
        assertTrue(draggedElement.getAttribute("class").contains("focused"));
    }

    @Test
    public void testColumnReorder_draggingFocusedHeader_focusIsKeptOnHeader() {
        // given
        toggleColumnReorder();
        focusDefaultHeader(0);

        // when
        dragAndDropDefaultColumnHeader(0, 3, CellSide.LEFT);

        // then
        WebElement defaultColumnHeader = getDefaultColumnHeader(2);
        String attribute = defaultColumnHeader.getAttribute("class");
        assertTrue(attribute.contains("focused"));
    }

    @Test
    public void testColumnReorder_draggingFocusedCellColumn_focusIsKeptOnCell() {
        // given
        toggleColumnReorder();
        focusCell(2, 2);

        // when
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        assertFocusedCell(2, 0);
    }

    @Test
    public void testColumnReorderWithHiddenColumn_draggingFocusedCellColumnOverHiddenColumn_focusIsKeptOnCell() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Columns", "Column 1", "Hidden");
        focusCell(2, 2);
        assertFocusedCell(2, 2);

        // when
        dragAndDropDefaultColumnHeader(1, 0, CellSide.LEFT);

        // then
        assertFocusedCell(2, 2);

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertFocusedCell(2, 2);
    }

    @Test
    public void testColumnReorder_dragColumnFromRightToLeftOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(1, 3);

        // when
        dragAndDropDefaultColumnHeader(4, 1, CellSide.LEFT);

        // then
        assertFocusedCell(1, 4);
    }

    @Test
    public void testColumnReorder_dragColumnFromLeftToRightOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(4, 2);

        // when
        dragAndDropDefaultColumnHeader(0, 4, CellSide.LEFT);

        // then
        assertFocusedCell(4, 1);
    }

    @Test
    public void testColumnReorder_draggingHeaderRowThatHasColumnHeadersSpanned_cantDropInsideSpannedHeaderFromOutside() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        int horizontalOffset = (getGridElement().getHeaderCell(1, 1).getSize()
                .getWidth() / 2) - 10;
        dragAndDropColumnHeader(1, 3, 1, horizontalOffset);

        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_anotherRowHasColumnHeadersSpanned_cantDropInsideSpannedHeaderFromOutside() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        int horizontalOffset = (getGridElement().getHeaderCell(1, 1).getSize()
                .getWidth() / 2) + 10;
        dragAndDropColumnHeader(0, 0, 2, horizontalOffset);

        // then
        assertColumnHeaderOrder(1, 2, 0, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideSpannedHeader_cantBeDroppedOutsideSpannedArea() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(0, 2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(0, 2, 1, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideTwoCrossingSpanningHeaders_cantTouchThis() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2",
                "Join column cells 0, 1");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        dragAndDropColumnHeader(0, 3, 0, CellSide.LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);

        // when
        dragAndDropColumnHeader(0, 2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellsInsideSpannedHeaderAndBlockedByOtherSpannedCells_cantTouchThose() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2",
                "Join column cells 0, 1");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        dragAndDropColumnHeader(0, 3, 0, CellSide.LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);

        // when then
        dragAndDropColumnHeader(0, 1, 3, CellSide.LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);

        dragAndDropColumnHeader(1, 2, 1, CellSide.LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);

        dragAndDropColumnHeader(2, 1, 2, CellSide.RIGHT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellsInsideSpannedHeaderAndBlockedByOtherSpannedCells_reorderingLimited() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);

        // when then
        dragAndDropColumnHeader(0, 0, 4, CellSide.RIGHT);
        // joined cells push drag to apply after the join
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);

        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        scrollGridHorizontallyTo(0);
        // new join doesn't affect column order
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);

        dragAndDropColumnHeader(0, 0, 4, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        // joined cells push drag to apply before any joins
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);

        dragAndDropColumnHeader(0, 2, 4, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        // could drag within the first join but not out of the second
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);

        dragAndDropColumnHeader(0, 3, 4, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);
        // second join not involved so drag within first join succeeds
        assertColumnHeaderOrder(1, 2, 3, 5, 4, 0);

        dragAndDropColumnHeader(0, 4, 2, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);
        // back to previous order
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);

        dragAndDropColumnHeader(2, 3, 4, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);
        // second join not involved so drag within first join succeeds
        assertColumnHeaderOrder(1, 2, 3, 5, 4, 0);

        dragAndDropColumnHeader(2, 4, 2, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);
        // back to previous order
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);
    }

    @Test
    public void testColumnReorder_cellsInsideTwoAdjacentSpannedHeaders_reorderingLimited() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);

        // move first column after the join
        dragAndDropColumnHeader(0, 0, 4, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 0);

        // move current second column after the join
        dragAndDropColumnHeader(0, 1, 4, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 3, 4, 5, 0, 2);

        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        // column headers 3 and 4 combined, no effect to order
        assertColumnHeaderOrder(1, 3, 4, 5, 0, 2);

        // when then
        dragAndDropColumnHeader(0, 1, 4, CellSide.LEFT);
        // drag stops to the farthest point within the join
        assertColumnHeaderOrder(1, 4, 3, 5, 0, 2);

        dragAndDropColumnHeader(0, 2, 4, CellSide.LEFT);
        // second attempt with the already dragged column makes no difference
        assertColumnHeaderOrder(1, 4, 3, 5, 0, 2);

        dragAndDropColumnHeader(0, 2, 0, CellSide.LEFT);
        // drag stops to the earliest point within the join
        assertColumnHeaderOrder(1, 3, 4, 5, 0, 2);
    }

    @Test
    public void testColumnReorder_footerHasSpannedCells_cantDropInside() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(0, 3, 1, CellSide.RIGHT);

        // then
        // joined cells push the drag to apply after the join
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideASpannedFooter_cantBeDroppedOutsideSpannedArea() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(0, 2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(0, 2, 1, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideTwoCrossingSpanningFooters_cantTouchThis() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Row 1",
                "Join column cells 0, 1");
        selectMenuPath("Component", "Footer", "Row 2", "Join columns 1, 2");
        dragAndDropColumnHeader(0, 3, 0, CellSide.LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);

        // when
        dragAndDropColumnHeader(0, 2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellsInsideTwoAdjacentSpannedHeaderAndFooter_reorderingLimited() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(0, 0, 5, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        dragAndDropColumnHeader(0, 1, 5, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");
        assertColumnHeaderOrder(1, 3, 4, 5, 2);

        // when then
        dragAndDropColumnHeader(0, 1, 3, CellSide.RIGHT);
        assertColumnHeaderOrder(1, 4, 3, 5, 2);

        dragAndDropColumnHeader(0, 2, 4, CellSide.RIGHT);
        assertColumnHeaderOrder(1, 4, 3, 5, 2);

        dragAndDropColumnHeader(0, 2, 0, CellSide.RIGHT);
        assertColumnHeaderOrder(1, 3, 4, 5, 2);
    }

    @Test
    public void testColumnReorder_draggingASpannedCell_dragWorksNormally() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(1, 1, 4, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_twoEqualSpannedCells_bothCanBeDragged() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(1, 1, 4, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);

        // when
        dragAndDropColumnHeader(2, 3, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(1, 2, 0, 3, 4);
    }

    @Test
    public void testColumReorder_twoCrossingSpanningHeaders_neitherCanBeDragged() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3",
                "Join column cells 0, 1");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(1, 1, 4, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        // when
        dragAndDropColumnHeader(2, 0, 3, CellSide.RIGHT);

        // then
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
    }

    @Test
    public void testColumnReorder_spannedCellHasAnotherSpannedCellInside_canBeDraggedNormally() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(1, 3, 1, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 3, 4, 5, 1);

        // when
        dragAndDropColumnHeader(1, 1, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(3, 4, 5, 0, 1);
    }

    @Test
    public void testColumnReorder_spannedCellInsideAnotherSpanned_canBeDraggedWithBoundaries() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(1, 3, 1, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 3, 4, 5, 1);

        // when
        dragAndDropColumnHeader(2, 1, 3, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);

        // then
        assertColumnHeaderOrder(0, 5, 3, 4, 1);

        // when
        dragAndDropColumnHeader(2, 2, 0, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
    }

    @Test
    public void testColumnReorder_cellInsideAndNextToSpannedCells_canBeDraggedWithBoundaries() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(1, 3, 1, CellSide.LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 3, 4, 5, 1);

        // when
        dragAndDropColumnHeader(2, 3, 0, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        assertColumnHeaderOrder(0, 5, 3, 4, 1);

        // when
        dragAndDropColumnHeader(2, 1, 4, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
    }

    @Test
    public void testColumnReorder_multipleSpannedCells_dragWorksNormally() {
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);

        // when
        dragAndDropColumnHeader(1, 3, 1, CellSide.RIGHT);
        scrollGridHorizontallyTo(0);

        // then
        // attempted to drag out of join, no change
        // it's not possible to drag the whole joined block
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);

        // when
        scrollGridHorizontallyTo(100);
        dragAndDropColumnHeader(2, 4, 2, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        // attempted to drag out of join, moved as leftmost within
        assertColumnHeaderOrder(0, 1, 2, 4, 3, 5);

        // when
        dragAndDropColumnHeader(0, 0, 3, CellSide.LEFT);
        scrollGridHorizontallyTo(0);

        // then
        // dragged from outside of joins to between the joins, no problem
        assertColumnHeaderOrder(1, 2, 0, 4, 3, 5);
    }

    private void toggleSortableColumn(int index) {
        selectMenuPath("Component", "Columns", "Column " + index, "Sortable");
    }

    private void startDragButDontDropOnDefaultColumnHeader(int index) {
        new Actions(getDriver())
                .clickAndHold(getGridHeaderRowCells().get(index))
                .moveByOffset(100, 0).perform();
    }

    private void sortColumn(int index) {
        getGridHeaderRowCells().get(index).click();
    }

    private void focusDefaultHeader(int index) {
        getGridHeaderRowCells().get(index).click();
    }

    private WebElement getDraggedHeaderElement() {
        return findElement(By.className("dragged-column-header"));
    }
}
