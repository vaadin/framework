package com.vaadin.tests.components.grid.basics;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;

public class GridEventListenersTest extends GridBasicsTest {

	@Test
	public void testItemClickListener() {
		selectMenuPath("Component", "State", "Item click listener");
		selectMenuPath("Component", "State", "Selection model", "none");
		checkItemClickOnRow(0);
		checkItemClickOnRow(2);
		GridElement grid = getGridElement();
		grid.getHeaderCell(0, 7);
		checkItemClickOnRow(0);
		checkItemClickOnRow(2);
	}

	private void checkItemClickOnRow(int row) {
		GridElement grid = getGridElement();
		grid.getCell(row, 2).click();
		String logRow = getLogRow(0);
		Assert.assertTrue("Log row '" + logRow + "' did not contain index " + row, logRow.endsWith("Index " + row));
	}
}
