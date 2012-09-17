/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.sass.testcases.visitor;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.LexicalUnit;

import com.steadystate.css.parser.LexicalUnitImpl;
import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.parser.SCSSLexicalUnit;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.MixinDefNode;
import com.vaadin.sass.tree.MixinNode;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.VariableNode;
import com.vaadin.sass.visitor.MixinVisitor;

public class MixinVisitorTest {
    private MixinVisitor mixinVisitor = new MixinVisitor();

    @Test
    public void testTraversMixinWithoutArgs() {
        ScssStylesheet root = new ScssStylesheet();
        MixinDefNode mixinDefNoArgs = new MixinDefNode("no-args", null);
        BlockNode blockNode = new BlockNode(null);
        mixinDefNoArgs.appendChild(blockNode);
        root.appendChild(mixinDefNoArgs);

        BlockNode blockWithMixin = new BlockNode(null);
        MixinNode mixin = new MixinNode("no-args", null);
        blockWithMixin.appendChild(mixin);
        root.appendChild(blockWithMixin);

        try {
            mixinVisitor.traverse(root);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(1, root.getChildren().size());
        BlockNode child = (BlockNode) root.getChildren().get(0);
        BlockNode fromMixin = (BlockNode) child.getChildren().get(0);
        Assert.assertFalse(fromMixin.hasChildren());
    }

    @Test
    public void testTraverseMixinWithNonDefaultArgs() {
        ScssStylesheet root = new ScssStylesheet();
        ArrayList<VariableNode> args = new ArrayList<VariableNode>();
        args.add(new VariableNode("arg", null, false));
        MixinDefNode mixinDefWithNonDefaultArg = new MixinDefNode(
                "non-default-arg", args);
        BlockNode blockNode = new BlockNode(null);
        mixinDefWithNonDefaultArg.appendChild(blockNode);
        root.appendChild(mixinDefWithNonDefaultArg);

        BlockNode blockWithMixin = new BlockNode(null);
        ArrayList<LexicalUnit> includeArgs = new ArrayList<LexicalUnit>();
        LexicalUnit includeArg = LexicalUnitImpl.createPixel(null, 1f);
        includeArgs.add(includeArg);
        MixinNode mixin = new MixinNode("non-default-arg", includeArgs);
        blockWithMixin.appendChild(mixin);
        root.appendChild(blockWithMixin);

        try {
            mixinVisitor.traverse(root);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(1, root.getChildren().size());
        BlockNode child = (BlockNode) root.getChildren().get(0);
        BlockNode fromMixin = (BlockNode) child.getChildren().get(0);
        Assert.assertFalse(fromMixin.hasChildren());

    }

    @Test
    public void testTraverseMixinWithDefaultArgs() {
        ScssStylesheet root = new ScssStylesheet();
        ArrayList<VariableNode> args = new ArrayList<VariableNode>();
        args.add(new VariableNode("arg", LexicalUnitImpl.createPixel(null, 1f),
                false));
        MixinDefNode mixinDefWithNonDefaultArg = new MixinDefNode(
                "default-arg", args);
        BlockNode blockNode = new BlockNode(null);
        mixinDefWithNonDefaultArg.appendChild(blockNode);
        root.appendChild(mixinDefWithNonDefaultArg);

        BlockNode blockWithMixin = new BlockNode(null);
        MixinNode mixin = new MixinNode("default-arg", null);
        blockWithMixin.appendChild(mixin);
        root.appendChild(blockWithMixin);

        try {
            mixinVisitor.traverse(root);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(1, root.getChildren().size());
        BlockNode child = (BlockNode) root.getChildren().get(0);
        BlockNode fromMixin = (BlockNode) child.getChildren().get(0);
        Assert.assertFalse(fromMixin.hasChildren());

    }

    @Test
    public void testMixinWithoutArgument() {
        /*
         * ArrayList<String> args = new ArrayList<String>(); args.add("arg");
         * MixinDefNode mixinDefWithArgs = new MixinDefNode("with-args", args);
         * RuleNode ruleNode = new RuleNode("var",
         * com.vaadin.sass.parser.LexicalUnitImpl.createVariable(0, 0, null,
         * "arg"), false); mixinDefWithArgs.appendChild(ruleNode);
         */
        ScssStylesheet root = new ScssStylesheet();
        MixinDefNode mixinDefNoArgs = new MixinDefNode("table-base", null);
        BlockNode thBlockNode = new BlockNode(null);
        RuleNode textAlignRuleNode = new RuleNode("text-align",
                LexicalUnitImpl.createString(null, "center"), false, null);
        thBlockNode.appendChild(textAlignRuleNode);
        RuleNode fontWeightRuleNode = new RuleNode("font-weight",
                LexicalUnitImpl.createString(null, "bold"), false, null);
        thBlockNode.appendChild(fontWeightRuleNode);
        mixinDefNoArgs.appendChild(thBlockNode);

        BlockNode tdthBlockNode = new BlockNode(null);
        RuleNode paddingRuleNode = new RuleNode("padding",
                LexicalUnitImpl.createPixel(null, 2f), false, null);
        tdthBlockNode.appendChild(paddingRuleNode);
        mixinDefNoArgs.appendChild(tdthBlockNode);
        root.appendChild(mixinDefNoArgs);

        BlockNode dataBlock = new BlockNode(null);
        MixinNode mixinNode = new MixinNode("table-base", null);
        dataBlock.appendChild(mixinNode);
        root.appendChild(dataBlock);

        try {
            mixinVisitor.traverse(root);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(1, root.getChildren().size());
        dataBlock = (BlockNode) root.getChildren().get(0);
        BlockNode thBlock = (BlockNode) dataBlock.getChildren().get(0);
        Assert.assertEquals(2, thBlock.getChildren().size());
        BlockNode thtdBlock = (BlockNode) dataBlock.getChildren().get(1);
        Assert.assertEquals(1, thtdBlock.getChildren().size());

        /*
         * Assert.assertEquals(2, root.getChildren().size()); BlockNode
         * datathBlockNode = (BlockNode) root.getChildren().get(0);
         * Assert.assertEquals(LexicalUnit.SAC_IDENT, datathBlockNode
         * .getSelectorList().item(0).getSelectorType());
         * Assert.assertEquals("text-align", ((RuleNode) datathBlockNode
         * .getChildren().get(0)).getVariable()); Assert.assertEquals("center",
         * ((RuleNode) datathBlockNode.getChildren()
         * .get(0)).getValue().getStringValue());
         * Assert.assertEquals("font-weight", ((RuleNode) datathBlockNode
         * .getChildren().get(1)).getVariable()); Assert.assertEquals("bold",
         * ((RuleNode) datathBlockNode.getChildren()
         * .get(1)).getValue().getStringValue());
         * 
         * BlockNode datathdatatdBlockNode = (BlockNode)
         * root.getChildren().get(1); Assert.assertEquals(LexicalUnit.SAC_IDENT,
         * datathdatatdBlockNode .getSelectorList().item(0).getSelectorType());
         * Assert.assertEquals(LexicalUnit.SAC_IDENT, datathdatatdBlockNode
         * .getSelectorList().item(1).getSelectorType());
         * Assert.assertEquals("padding", ((RuleNode) datathdatatdBlockNode
         * .getChildren().get(0)).getVariable()); Assert.assertEquals(2.0f,
         * ((RuleNode) datathdatatdBlockNode
         * .getChildren().get(0)).getValue().getFloatValue(), 0);
         * Assert.assertEquals(LexicalUnit.SAC_PIXEL, ((RuleNode)
         * datathdatatdBlockNode.getChildren().get(0))
         * .getValue().getLexicalUnitType());
         */

    }

    @Test
    public void testMixinWithArgument() {
        ScssStylesheet root = new ScssStylesheet();
        ArrayList<VariableNode> argNameList = new ArrayList<VariableNode>();
        argNameList.add(new VariableNode("dist", null, false));
        MixinDefNode mixinDef = new MixinDefNode("left", argNameList);

        RuleNode floatRuleNode = new RuleNode("float",
                LexicalUnitImpl.createString(null, "left"), false, null);
        mixinDef.appendChild(floatRuleNode);
        RuleNode marginLeftRuleNode = new RuleNode("margin-left",
                com.vaadin.sass.parser.LexicalUnitImpl.createVariable(0, 0,
                        null, "dist"), false, null);
        mixinDef.appendChild(marginLeftRuleNode);
        root.appendChild(mixinDef);

        BlockNode dataBlock = new BlockNode(null);
        ArrayList<LexicalUnit> argValueList = new ArrayList<LexicalUnit>();
        LexicalUnit arg = LexicalUnitImpl.createPixel(null, 10f);
        argValueList.add(arg);
        MixinNode mixinNode = new MixinNode("left", argValueList);
        dataBlock.appendChild(mixinNode);
        root.appendChild(dataBlock);

        try {
            mixinVisitor.traverse(root);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(1, root.getChildren().size());
        BlockNode dataBlockNode = (BlockNode) root.getChildren().get(0);
        Assert.assertEquals("float", ((RuleNode) dataBlockNode.getChildren()
                .get(0)).getVariable());
        Assert.assertEquals("left", ((RuleNode) dataBlockNode.getChildren()
                .get(0)).getValue().getStringValue());
        Assert.assertEquals("margin-left", ((RuleNode) dataBlockNode
                .getChildren().get(1)).getVariable());
        Assert.assertEquals(SCSSLexicalUnit.SAC_PIXEL,
                ((RuleNode) dataBlockNode.getChildren().get(1)).getValue()
                        .getLexicalUnitType());
    }

    @Test
    public void testTopLevelInclude() {
        ScssStylesheet root = new ScssStylesheet();

        MixinDefNode defNode = new MixinDefNode("mixin", null);
        defNode.appendChild(new RuleNode("var", null, false, null));
        root.appendChild(defNode);

        MixinNode mixinNode = new MixinNode("mixin", null);
        root.appendChild(mixinNode);

        try {
            mixinVisitor.traverse(root);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(1, root.getChildren().size());
        RuleNode varRule = (RuleNode) root.getChildren().get(0);
        Assert.assertEquals("var", varRule.getVariable());

    }

}
