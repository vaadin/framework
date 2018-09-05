/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A {@link Predicate} that is also {@link Serializable}.
 *
 * @author Vaadin Ltd
 * @since 8.0
 * @param <T>
 *            the type of the input to the predicate
 *
 */
public interface SerializablePredicate<T> extends Predicate<T>, Serializable {
    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another. When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other
     *            a predicate that will be logically-ANDed with this predicate
     * @return a composed predicate that represents the short-circuiting logical
     *         AND of this predicate and the {@code other} predicate
     * @throws NullPointerException
     *             if other is null
     * @since 8.5
     */
    default SerializablePredicate<T> and(
            SerializablePredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     *         predicate
     * @since 8.5
     */
    default SerializablePredicate<T> negate() {
        return t -> !test(t);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another. When evaluating the composed predicate,
     * if this predicate is {@code true}, then the {@code other} predicate is
     * not evaluated.
     *
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other
     *            a predicate that will be logically-ORed with this predicate
     * @return a composed predicate that represents the short-circuiting logical
     *         OR of this predicate and the {@code other} predicate
     * @throws NullPointerException
     *             if other is null
     * @since 8.5
     */
    default SerializablePredicate<T> or(
            SerializablePredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

    /**
     * Returns a predicate that tests if two arguments are equal according to
     * {@link Objects#equals(Object, Object)}.
     *
     * @param <T>
     *            the type of arguments to the predicate
     * @param targetRef
     *            the object reference with which to compare for equality, which
     *            may be {@code null}
     * @return a predicate that tests if two arguments are equal according to
     *         {@link Objects#equals(Object, Object)}
     * @since 8.5
     */
    static <T> SerializablePredicate<T> isEqual(Serializable targetRef) {
        return (null == targetRef) ? Objects::isNull
                : object -> targetRef.equals(object);
    }

}
