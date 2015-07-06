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
package com.vaadin.tests.server.component.gridlayout;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.server.component.DeclarativeMarginTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.declarative.DesignContext;

public class GridLayoutDeclarativeTest extends
        DeclarativeMarginTestBase<GridLayout> {

    @Test
    public void testMargins() {
        testMargins("v-grid-layout");
    }

    @Test
    public void testSimpleGridLayout() {
        Button b1 = new Button("Button 0,0");
        Button b2 = new Button("Button 0,1");
        Button b3 = new Button("Button 1,0");
        Button b4 = new Button("Button 1,1");
        b1.setCaptionAsHtml(true);
        b2.setCaptionAsHtml(true);
        b3.setCaptionAsHtml(true);
        b4.setCaptionAsHtml(true);
        String design = "<v-grid-layout><row>" //
                + "<column expand=1>" + writeChild(b1) + "</column>" //
                + "<column expand=3>" + writeChild(b2) + "</column>" //
                + "</row><row>" //
                + "<column>" + writeChild(b3) + "</column>" //
                + "<column>" + writeChild(b4) + "</column>" //
                + "</row></v-grid-layout>";
        GridLayout gl = new GridLayout(2, 2);
        gl.addComponent(b1);
        gl.addComponent(b2);
        gl.addComponent(b3);
        gl.addComponent(b4);
        gl.setColumnExpandRatio(0, 1.0f);
        gl.setColumnExpandRatio(1, 3.0f);
        testWrite(design, gl);
        testRead(design, gl);
    }

    @Test
    public void testOneBigComponentGridLayout() {
        Button b1 = new Button("Button 0,0 -> 1,1");
        b1.setCaptionAsHtml(true);
        String design = "<v-grid-layout><row>" //
                + "<column colspan=2 rowspan=2>" + writeChild(b1) + "</column>" //
                + "</row><row expand=2>" //
                + "</row></v-grid-layout>";
        GridLayout gl = new GridLayout(2, 2);
        gl.addComponent(b1, 0, 0, 1, 1);
        gl.setRowExpandRatio(1, 2);
        testWrite(design, gl);
        testRead(design, gl);
    }

    @Test
    public void testMultipleSpannedComponentsGridLayout() {
        GridLayout gl = new GridLayout(5, 5);
        Button b1 = new Button("Button 0,0 -> 0,2");
        b1.setCaptionAsHtml(true);
        gl.addComponent(b1, 0, 0, 2, 0);

        Button b2 = new Button("Button 0,3 -> 3,3");
        b2.setCaptionAsHtml(true);
        gl.addComponent(b2, 3, 0, 3, 3);

        Button b3 = new Button("Button 0,4 -> 1,4");
        b3.setCaptionAsHtml(true);
        gl.addComponent(b3, 4, 0, 4, 1);

        Button b4 = new Button("Button 1,0 -> 3,1");
        b4.setCaptionAsHtml(true);
        gl.addComponent(b4, 0, 1, 1, 3);

        Button b5 = new Button("Button 2,2");
        b5.setCaptionAsHtml(true);
        gl.addComponent(b5, 2, 2);

        Button b6 = new Button("Button 3,4 -> 4,4");
        b6.setCaptionAsHtml(true);
        gl.addComponent(b6, 4, 3, 4, 4);

        Button b7 = new Button("Button 4,1 -> 4,2");
        b7.setCaptionAsHtml(true);
        gl.addComponent(b7, 2, 4, 3, 4);

        /*
         * Buttons in the GridLayout
         */

        // 1 1 1 2 3
        // 4 4 - 2 3
        // 4 4 5 2 -
        // 4 4 - 2 6
        // - - 7 7 6

        String design = "<v-grid-layout><row>" //
                + "<column colspan=3>" + writeChild(b1) + "</column>" //
                + "<column rowspan=4>" + writeChild(b2) + "</column>" //
                + "<column rowspan=2>" + writeChild(b3) + "</column>" //
                + "</row><row>" //
                + "<column rowspan=3 colspan=2>" + writeChild(b4) + "</column>" //
                + "</row><row>" //
                + "<column>" + writeChild(b5) + "</column>" //
                + "</row><row>" //
                + "<column />" // Empty placeholder
                + "<column rowspan=2>" + writeChild(b6) + "</column>" //
                + "</row><row>" //
                + "<column colspan=2 />" // Empty placeholder
                + "<column colspan=2>" + writeChild(b7) + "</column>" //
                + "</row></v-grid-layout>";
        testWrite(design, gl);
        testRead(design, gl);
    }

    @Test
    public void testManyExtraGridLayoutSlots() {
        GridLayout gl = new GridLayout(5, 5);
        Button b1 = new Button("Button 0,4 -> 4,4");
        b1.setCaptionAsHtml(true);
        gl.addComponent(b1, 4, 0, 4, 4);
        gl.setColumnExpandRatio(2, 2.0f);

        String design = "<v-grid-layout><row>" //
                + "<column colspan=4 rowspan=5 expand='0,0,2,0' />" //
                + "<column rowspan=5>" + writeChild(b1) + "</column>" //
                + "</row><row>" //
                + "</row><row>" //
                + "</row><row>" //
                + "</row><row>" //
                + "</row></v-grid-layout>";
        testWrite(design, gl);
        testRead(design, gl);
    }

    @Test
    public void testManyEmptyColumnsWithOneExpand() {
        GridLayout gl = new GridLayout(5, 5);
        Button b1 = new Button("Button 0,4 -> 4,4");
        b1.setCaptionAsHtml(true);
        gl.addComponent(b1, 0, 0, 0, 4);
        gl.setColumnExpandRatio(4, 2.0f);

        String design = "<v-grid-layout><row>" //
                + "<column rowspan=5>" + writeChild(b1) + "</column>" //
                + "<column colspan=4 rowspan=5 expand='0,0,0,2' />" //
                + "</row><row>" //
                + "</row><row>" //
                + "</row><row>" //
                + "</row><row>" //
                + "</row></v-grid-layout>";
        testWrite(design, gl);
        testRead(design, gl);
    }

    @Test
    public void testEmptyGridLayout() {
        GridLayout gl = new GridLayout();
        String design = "<v-grid-layout />";
        testWrite(design, gl);
        testRead(design, gl);
    }

    private String writeChild(Component childComponent) {
        return new DesignContext().createElement(childComponent).toString();
    }

    @Override
    public GridLayout testRead(String design, GridLayout expected) {
        expected.setCursorX(0);
        expected.setCursorY(expected.getRows());

        GridLayout result = super.testRead(design, expected);
        for (int row = 0; row < expected.getRows(); ++row) {
            Assert.assertTrue(Math.abs(expected.getRowExpandRatio(row)
                    - result.getRowExpandRatio(row)) < 0.00001);
        }
        for (int col = 0; col < expected.getColumns(); ++col) {
            Assert.assertTrue(Math.abs(expected.getColumnExpandRatio(col)
                    - result.getColumnExpandRatio(col)) < 0.00001);
        }
        return result;
    }

    @Test
    public void testNestedGridLayouts() {
        String design = "<!DOCTYPE html>" + //
                "<html>" + //
                " <body> " + //
                "  <v-grid-layout> " + //
                "   <row> " + //
                "    <column> " + //
                "     <v-grid-layout> " + //
                "      <row> " + //
                "       <column> " + //
                "        <v-button>" + //
                "          Button " + //
                "        </v-button> " + //
                "       </column> " + //
                "      </row> " + //
                "     </v-grid-layout> " + //
                "    </column> " + //
                "   </row> " + //
                "  </v-grid-layout>  " + //
                " </body>" + //
                "</html>";
        GridLayout outer = new GridLayout();
        GridLayout inner = new GridLayout();
        Button b = new Button("Button");
        b.setCaptionAsHtml(true);
        inner.addComponent(b);
        outer.addComponent(inner);
        testRead(design, outer);
        testWrite(design, outer);

    }
}
