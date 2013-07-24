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

package com.vaadin.sass.internal.visitor;

import java.util.ArrayList;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.IVariableNode;
import com.vaadin.sass.internal.tree.FunctionDefNode;
import com.vaadin.sass.internal.tree.FunctionNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.util.DeepCopy;
import com.vaadin.sass.internal.visitor.MixinNodeHandler;
import com.vaadin.sass.internal.parser.ReturnNodeException;
import org.w3c.flute.parser.ParseException;
import com.vaadin.sass.internal.visitor.ReturnNodeHandler;


/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class FunctionNodeHandler extends MixinNodeHandler{

    public static void traverse(FunctionNode node) throws Exception {
        traverse(node, node.getName());
    }

    public static LexicalUnitImpl traverse(FunctionNode node, String name) throws Exception {

        FunctionDefNode functionDef = 
            ScssStylesheet.getFunctionDefinition(name);
        if (functionDef == null) {
            throw new ParseException("Function Definition: " + name
                    + " not found");
        }

        return evaluateFunction(functionDef, node);
    }

    public static String evaluateFunction(String functionName, LexicalUnitImpl parameters) {

        FunctionDefNode functionDef = ScssStylesheet.getFunctionDefinition(functionName);
        if (functionDef == null) {
            throw new ParseException("Function Definition: " + functionName
                    + " not found");
        }

        ArrayList<LexicalUnitImpl> params = new ArrayList<LexicalUnitImpl>();
        params.add(parameters);

        FunctionNode functionNode = new FunctionNode(functionName, params);

        LexicalUnitImpl result = evaluateFunction(functionDef, functionNode);

        return result.toString();
    }

    private static LexicalUnitImpl evaluateFunction(
        FunctionDefNode functionDef, FunctionNode node) {

        LexicalUnitImpl retval = null;

        MixinDefNode replacedNode = replaceMixinNode((MixinNode) node, 
            (MixinDefNode)functionDef);

        try {
            retval = evaluateFunctionElements((Node)replacedNode, null);
        } catch (ReturnNodeException e) {
            retval = e.getReturnValue();
        }

        return retval;
    }

    private static LexicalUnitImpl evaluateFunctionElements(Node node, 
        HashMap<LexicalUnitImpl> globalStack) {
        LexicalUnitImpl retVal = null;
        String alreadyRemoved = 
                " should have already been removed during traversal.";
        String notAllowed = " is not allowed within a function.";

        ArrayList<Node> children = node.getChildren();
        HashMap<LexicalUnitImpl> localStack = 
            (globalStack == null) ? null : globalStack.clone();

        for (int i = 0; i < children.length(); i++) {
            LexicalUnitImpl temp = null;

            String fontFace = null;
            String microsoftVariable = null;
            String name = null;

            Node child = (Node)children.get(i);

            if (child instanceof ReturnNode) {
                //process the final value
                retVal = ReturnNodeHandler.evaluateExpression(
                    child.getExpression());

                //return immediately as an exception
                throw new ReturnNodeException(retVal);
            } else if (child instanceof CommentNode) {
                //ignore and move on
            } else if (child instanceof FunctionNode) {
                //evaluate function
                temp = traverse(child, child.getName());
            } else if (child instanceof FontFaceNode) {
                //grab String value - maybe we can use it for something?
                fontFace = child.toString();
            } else if (child instanceof MicrosoftRuleNode) {
                //grab String variable value of the format:
                //name + ": " + value + ";"
                microsoftVariable = child.toString(); 
            } else if (child instanceof SimpleNode) {
                //'throw an error' or 'ignore and move on' ?
                
            } else if (child instanceof RuleNode) {
                //grab the variable value and name
                temp = child.getValue();
                name = child.getVariable();
            } else if (child instanceof VariableNode) {
                //grab the variable value and name
                if (!child.isGuarded()) {
                    temp = child.getExpr();
                    name = child.getName();
                }
            } else if (child instanceof BlockNode) {
                //this should have been removed during traversal
                throw new ParseException("BlockNode: " + 
                    child.toString() + alreadyRemoved);
            } else if (child instanceof ContentNode) {
                //this should have been removed during traversal
                throw new ParseException("ContentNode: " + 
                    child.toString() + alreadyRemoved);
            } else if (child instanceof ExtendNode) {
                //this should have been removed during traversal
                throw new ParseException("ExtendNode: " + 
                    child.toString() + alreadyRemoved);
            } else if (child instanceof FunctionDefNode) {
                //this should have been removed during traversal
                throw new ParseException("FunctionDefNode: " + 
                    child.getName() + alreadyRemoved);
            } else if (child instanceof ImportNode) {
                //this should have been removed during traversal
                throw new ParseException("ImportNode: " + 
                    child.getUri() + alreadyRemoved);
            } else if (child instanceof KeyFrameSelectorNode) {
                //this is not allowed in a function
                throw new ParseException("KeyFrameSelectorNode: " + 
                     notAllowed);
            } else if (child instanceof KeyframesNode) {
                //this is not allowed in a function
                throw new ParseException("KeyframesNode: " + 
                     notAllowed);
            } else if (child instanceof ListAppendNode) {
                //this should have been removed during traversal
                throw new ParseException("ListAppendNode: " + 
                    child.getNewVariable() + alreadyRemoved);
            } else if (child instanceof ListContainsNode) {
                //this should have been removed during traversal
                throw new ParseException("ListContainsNode: " + 
                    child.getNewVariable() + alreadyRemoved);
            } else if (child instanceof ListRemoveNode) {
                //this should have been removed during traversal
                throw new ParseException("ListRemoveNode: " + 
                    child.getNewVariable() + alreadyRemoved);
            } else if (child instanceof ListModifyNode) {
                //this should have been removed during traversal
                throw new ParseException("ListModifyNode: " + 
                    child.getNewVariable() + alreadyRemoved);
            } else if (child instanceof MediaNode) {
                //this is not allowed in a function
                throw new ParseException("MediaNode: " + 
                     notAllowed);
            } else if (child instanceof MixinDefNode) {
                //this should have been removed during traversal
                throw new ParseException("MixinDefNode: " + 
                    child.getName() + alreadyRemoved);
            } else if (child instanceof MixinNode) {
                //this should have been removed during traversal
                //FunctionNode, a subclass of MixinNode is handled above
                throw new ParseException("MixinNode: " + 
                    child.getName() + alreadyRemoved);
            } else if (child instanceof NestedPropertiesNode) {
                //this should have been removed during traversal
                throw new ParseException("NestedPropertiesNode: " + 
                    child.getName() + alreadyRemoved);
            } else if (child instanceof EachDefNode) {
                //this should have been removed during traversal
                throw new ParseException("EachDefNode: " + 
                    child.getVariableName() + alreadyRemoved);
            } else if (child instanceof ElseNode) {
                //this should have been removed during traversal
                throw new ParseException("ElseNode: " + 
                    alreadyRemoved);
            } else if (child instanceof ForNode) {
                //this should have been removed during traversal
                throw new ParseException("ForNode: " + 
                    child.getVariableName() + alreadyRemoved);
            } else if (child instanceof IfElseDefNode) {
                //this should have been removed during traversal
                throw new ParseException("IfElseDefNode: " + 
                    alreadyRemoved);
            } else if (child instanceof IfNode) {
                //this should have been removed during traversal
                throw new ParseException("IfNode: " + 
                    child.getExpression() + alreadyRemoved);
            } else if (child instanceof IfElseNode) {
                //this should have been removed during traversal
                throw new ParseException("IfElseNode: " + 
                    alreadyRemoved);
            } else if (child instanceof WhileDefNode) {
                //this should have been removed during traversal
                throw new ParseException("WhileDefNode: " + 
                    alreadyRemoved);
            } else if (child instanceof WhileNode) {
                //this should have been removed during traversal
                throw new ParseException("WhileNode: " + 
                    child.getExpression() + alreadyRemoved);
            } else {
                //this is not allowed in a function
                throw new ParseException("Node: unknown type" + 
                     notAllowed);
            }

        }

        // a function without a return statement returns void
        return null;
    }
}
