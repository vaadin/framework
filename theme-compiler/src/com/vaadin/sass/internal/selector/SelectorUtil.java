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

package com.vaadin.sass.internal.selector;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.flute.parser.selectors.AndConditionImpl;
import org.w3c.flute.parser.selectors.AttributeConditionImpl;
import org.w3c.flute.parser.selectors.ChildSelectorImpl;
import org.w3c.flute.parser.selectors.ClassConditionImpl;
import org.w3c.flute.parser.selectors.ConditionFactoryImpl;
import org.w3c.flute.parser.selectors.DirectAdjacentSelectorImpl;
import org.w3c.flute.parser.selectors.ElementSelectorImpl;
import org.w3c.flute.parser.selectors.IdConditionImpl;
import org.w3c.flute.parser.selectors.PseudoClassConditionImpl;
import org.w3c.flute.parser.selectors.PseudoElementSelectorImpl;
import org.w3c.flute.parser.selectors.SelectorFactoryImpl;

import com.vaadin.sass.internal.parser.SelectorListImpl;

public class SelectorUtil {

    public static String toString(CompositeSelector compositeSelector) {
        StringBuilder builder = new StringBuilder();
        if (compositeSelector != null) {
            if (compositeSelector.getFirst() != null) {
                builder.append(toString(compositeSelector.getFirst())).append(
                        " ");
            }
            if (compositeSelector.getSecond() != null) {
                builder.append(toString(compositeSelector.getSecond()));
            }
        }
        return builder.toString();
    }

    public static String toString(SelectorList selectorList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < selectorList.getLength(); i++) {
            String selectorString = toString(selectorList.item(i));
            stringBuilder.append(selectorString);
            if (selectorList.getLength() > i + 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public static String toString(Selector selector) {
        if (selector == null) {
            return "";
        }
        if (selector.getSelectorType() == Selector.SAC_CONDITIONAL_SELECTOR) {
            StringBuilder stringBuilder = new StringBuilder();
            ConditionalSelector conditionalSelector = (ConditionalSelector) selector;
            String simpleSelectorString = toString(conditionalSelector
                    .getSimpleSelector());
            if (simpleSelectorString != null) {
                stringBuilder.append(simpleSelectorString);
            }
            String conditionString = getConditionString(conditionalSelector
                    .getCondition());
            stringBuilder.append(conditionString);
            return stringBuilder.toString();
        } else if (selector.getSelectorType() == Selector.SAC_DESCENDANT_SELECTOR) {
            return getDecendantSelectorString((DescendantSelector) selector,
                    " ");
        } else if (selector.getSelectorType() == Selector.SAC_CHILD_SELECTOR) {
            DescendantSelector childSelector = (DescendantSelector) selector;
            String seperator = " > ";
            if (childSelector.getSimpleSelector() instanceof PseudoElementSelectorImpl) {
                seperator = "::";
            }
            return getDecendantSelectorString((DescendantSelector) selector,
                    seperator);
        } else if (selector.getSelectorType() == Selector.SAC_ELEMENT_NODE_SELECTOR) {
            ElementSelectorImpl elementSelector = (ElementSelectorImpl) selector;
            return elementSelector.getLocalName() == null ? ""
                    : elementSelector.getLocalName();
        } else if (selector.getSelectorType() == Selector.SAC_DIRECT_ADJACENT_SELECTOR) {
            DirectAdjacentSelectorImpl directAdjacentSelector = (DirectAdjacentSelectorImpl) selector;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(toString(directAdjacentSelector.getSelector()));
            stringBuilder.append(" + ");
            stringBuilder.append(toString(directAdjacentSelector
                    .getSiblingSelector()));
            return stringBuilder.toString();
        } else if (selector.getSelectorType() == Selector.SAC_PSEUDO_ELEMENT_SELECTOR) {
            PseudoElementSelectorImpl pseudoElementSelectorImpl = (PseudoElementSelectorImpl) selector;
            return pseudoElementSelectorImpl.getLocalName();
        } else if (selector.getSelectorType() == CompositeSelector.SCSS_COMPOSITE_SELECTOR) {
            return toString((CompositeSelector) selector);
        } else {
            log("SU !Unknown selector type, type: "
                    + selector.getSelectorType() + ", " + selector.toString());
        }
        return "";
    }

    private static String getDecendantSelectorString(
            DescendantSelector selector, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        String ancestor = toString(selector.getAncestorSelector());
        String simpleSelector = toString(selector.getSimpleSelector());
        stringBuilder.append(ancestor);
        stringBuilder.append(separator);
        stringBuilder.append(simpleSelector);
        return stringBuilder.toString();
    }

    private static String getConditionString(Condition condition) {
        short conditionType = condition.getConditionType();
        if (conditionType == Condition.SAC_CLASS_CONDITION) {
            ClassConditionImpl classCondition = (ClassConditionImpl) condition;
            return "." + classCondition.getValue();
        } else if (conditionType == Condition.SAC_ID_CONDITION) {
            IdConditionImpl idCondition = (IdConditionImpl) condition;
            return "#" + idCondition.getValue();
        } else if (conditionType == Condition.SAC_AND_CONDITION) {
            AndConditionImpl andCondition = (AndConditionImpl) condition;
            return getConditionString(andCondition.getFirstCondition())
                    + getConditionString(andCondition.getSecondCondition());
        } else if (conditionType == Condition.SAC_ATTRIBUTE_CONDITION) {
            AttributeConditionImpl attributeCondition = (AttributeConditionImpl) condition;
            StringBuilder string = new StringBuilder();
            string.append('[');
            string.append(attributeCondition.getLocalName());
            String value = attributeCondition.getValue();
            if ("true".equals(value) || "false".equals(value)) {
                string.append("=").append(value).append(']');
            } else {
                string.append("=\"");
                string.append(attributeCondition.getValue());
                string.append("\"]");
            }
            return string.toString();
        } else if (conditionType == Condition.SAC_PSEUDO_CLASS_CONDITION) {
            PseudoClassConditionImpl pseudoClassCondition = (PseudoClassConditionImpl) condition;
            return ":" + pseudoClassCondition.getValue();
        } else {
            log("CU !condition type not identified, type: " + conditionType
                    + ", " + condition.toString());
            return "";
        }
    }

    public static boolean hasParentSelector(SelectorList selectorList) {
        String selectorString = toString(selectorList);
        return selectorString.contains("&");
    }

    public static SelectorList createNewSelectorListFromAnOldOneWithSomPartReplaced(
            SelectorList oldList, String toBeReplacedSelectorName,
            SelectorList candidateSelectorList) throws Exception {
        if (candidateSelectorList.getLength() != 1) {
            throw new Exception("Candidate selector should not be a list");
        }
        if (!(candidateSelectorList.item(0) instanceof SimpleSelector)) {
            throw new Exception(
                    "Candidate selector should only be a SimpleSelector");
        }
        SelectorListImpl newSelectorList = new SelectorListImpl();
        SimpleSelector candidateSelector = (SimpleSelector) candidateSelectorList
                .item(0);
        for (int i = 0; i < oldList.getLength(); i++) {
            Selector selector = oldList.item(i);
            newSelectorList.addSelector(createSelectorWithSomePartReplaced(
                    selector, toBeReplacedSelectorName, candidateSelector));
        }
        return newSelectorList;
    }

    private static Selector createSelectorWithSomePartReplaced(
            Selector selector, String toBeReplacedSelectorName,
            SimpleSelector candidateSelector) {
        if (!toString(selector).contains(toBeReplacedSelectorName)) {
            return selector;
        }
        SelectorFactory factory = new SelectorFactoryImpl();
        if (selector instanceof SimpleSelector) {
            return createSimpleSelectorWithSomePartReplaced(
                    (SimpleSelector) selector, toBeReplacedSelectorName,
                    candidateSelector);
        } else if (selector instanceof DescendantSelector) {
            DescendantSelector descendantSelector = (DescendantSelector) selector;
            Selector ancestor = descendantSelector.getAncestorSelector();
            SimpleSelector simpleSelector = descendantSelector
                    .getSimpleSelector();
            return factory.createDescendantSelector(
                    createSelectorWithSomePartReplaced(ancestor,
                            toBeReplacedSelectorName, candidateSelector),
                    createSimpleSelectorWithSomePartReplaced(simpleSelector,
                            toBeReplacedSelectorName, candidateSelector));
        } else if (selector instanceof DirectAdjacentSelectorImpl) {
            SiblingSelector siblingSelector = (SiblingSelector) selector;
            Selector ancestor = siblingSelector.getSelector();
            SimpleSelector simpleSelector = siblingSelector
                    .getSiblingSelector();
            return factory.createDirectAdjacentSelector(
                    Selector.SAC_DIRECT_ADJACENT_SELECTOR, ancestor,
                    simpleSelector);
        } else if (selector instanceof CompositeSelector) {
            CompositeSelector compositeSelector = (CompositeSelector) selector;
            Selector first = compositeSelector.getFirst();
            Selector second = compositeSelector.getSecond();
            return new CompositeSelector(createSelectorWithSomePartReplaced(
                    first, toBeReplacedSelectorName, candidateSelector),
                    createSelectorWithSomePartReplaced(second,
                            toBeReplacedSelectorName, candidateSelector));
        }
        return null;
    }

    private static SimpleSelector createSimpleSelectorWithSomePartReplaced(
            SimpleSelector simpleSelector, String toBeReplacedSelectorName,
            SimpleSelector candidateSelector) {
        if (simpleSelector == null
                || !toString(simpleSelector).contains(toBeReplacedSelectorName)) {
            return simpleSelector;
        }
        if (simpleSelector instanceof ElementSelector
                && candidateSelector instanceof ElementSelector) {
            return candidateSelector;
        }
        if (simpleSelector instanceof ConditionalSelector) {
            return createConditionSelectorWithSomePartReplaced(
                    (ConditionalSelector) simpleSelector,
                    toBeReplacedSelectorName, candidateSelector);
        }
        return simpleSelector;
    }

    private static ConditionalSelector createConditionSelectorWithSomePartReplaced(
            ConditionalSelector oldConditionSelector,
            String toBeReplacedSelectorName, SimpleSelector candidateSelector) {
        if (oldConditionSelector == null
                || !toString(oldConditionSelector).contains(
                        toBeReplacedSelectorName)) {
            return oldConditionSelector;
        }
        SelectorFactory selectorFactory = new SelectorFactoryImpl();
        if (candidateSelector instanceof ElementSelector) {
            return selectorFactory.createConditionalSelector(candidateSelector,
                    oldConditionSelector.getCondition());
        }
        if (candidateSelector instanceof ConditionalSelector) {
            // TODO some cases not covered.
            ConditionalSelector candidateConditionSelector = (ConditionalSelector) candidateSelector;
            Condition newCondition = createConditionWithSomePartReplaced(
                    oldConditionSelector.getCondition(),
                    toBeReplacedSelectorName,
                    candidateConditionSelector.getCondition());
            return selectorFactory.createConditionalSelector(
                    oldConditionSelector.getSimpleSelector(), newCondition);
        }
        return oldConditionSelector;
    }

    private static Condition createConditionWithSomePartReplaced(
            Condition oldCondition, String toBeReplaced, Condition candidate) {
        if (oldCondition == null
                || !getConditionString(oldCondition).contains(toBeReplaced)) {
            return oldCondition;
        }
        if (oldCondition.getConditionType() == Condition.SAC_AND_CONDITION) {
            ConditionFactory conditionFactory = new ConditionFactoryImpl();
            CombinatorCondition oldCombinatorCondition = (CombinatorCondition) oldCondition;
            Condition newFirstCondition = createConditionWithSomePartReplaced(
                    oldCombinatorCondition.getFirstCondition(), toBeReplaced,
                    candidate);
            Condition newSecondCondition = createConditionWithSomePartReplaced(
                    oldCombinatorCondition.getSecondCondition(), toBeReplaced,
                    candidate);
            return conditionFactory.createAndCondition(newFirstCondition,
                    newSecondCondition);
        } else {
            return candidate;
        }
    }

    public static boolean equals(Selector one, Selector another) {
        return one == null ? another == null : toString(one).equals(
                toString(another));
    }

    public static Selector createSelectorAndreplaceSelectorVariableWithValue(
            Selector selector, String variable, String value) throws Exception {

        SelectorFactoryImpl factory = new SelectorFactoryImpl();

        ElementSelector es = factory.createElementSelector(
                null,
                ((ElementSelector) selector).getLocalName().replaceAll(
                        variable, value));

        if (selector instanceof ConditionalSelector) {
            return factory.createConditionalSelector(es,
                    ((ConditionalSelector) selector).getCondition());
        } else if (selector instanceof DescendantSelector) {
            return factory.createDescendantSelector(es,
                    ((DescendantSelector) selector).getSimpleSelector());
        } else if (selector instanceof ChildSelectorImpl) {
            return factory.createChildSelector(es,
                    ((DescendantSelector) selector).getSimpleSelector());
        } else {
            throw new Exception("Invalid selector type");
        }
    }

    private static void log(String msg) {
        Logger.getLogger(SelectorUtil.class.getName()).log(Level.INFO, msg);
    }
}
