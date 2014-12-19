/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypherdsl

import java.io.Serializable

package object expression {

  /**
   * Expression that evaluates to a boolean
   */
  trait BooleanExpression extends ScalarExpression {
    def and(expression: BooleanExpression): BooleanExpression

    def or(expression: BooleanExpression): BooleanExpression
  }

  /**
   * Expression that evaluates to a collection
   */
  trait CollectionExpression extends Expression {
    def union(expression: CollectionExpression): CollectionExpression
  }

  /**
   * Common interface for all expressions
   */
  trait Expression extends AsString with Serializable with Cloneable {
    def eq(literal: String): BooleanExpression

    def eq(literal: Number): BooleanExpression

    def eq(expression: Expression): BooleanExpression

    def ne(literal: String): BooleanExpression

    def ne(literal: Number): BooleanExpression

    def ne(expression: Expression): BooleanExpression
  }

  /**
   * Expression that evaluates to a single label
   */
  trait LabelExpression extends ScalarExpression {
  }

  /**
   * Expression that evaluates to a single node
   */
  trait NodeExpression extends PropertyContainerExpression {
  }

  /**
   * Expression that evaluates to a number
   */
  trait NumericExpression extends ScalarExpression {
    def add(expression: Number): NumericExpression

    def add(expression: NumericExpression): NumericExpression

    def subtract(expression: Number): NumericExpression

    def subtract(expression: NumericExpression): NumericExpression

    def times(expression: Number): NumericExpression

    def times(expression: NumericExpression): NumericExpression

    def divideBy(expression: Number): NumericExpression

    def divideBy(expression: NumericExpression): NumericExpression

    def mod(expression: Number): NumericExpression

    def mod(expression: NumericExpression): NumericExpression

    def gt(expression: Number): BooleanExpression

    def gt(expression: NumericExpression): BooleanExpression

    def lt(expression: Number): BooleanExpression

    def lt(expression: NumericExpression): BooleanExpression

    def gte(expression: Number): BooleanExpression

    def gte(expression: NumericExpression): BooleanExpression

    def lte(expression: Number): BooleanExpression

    def lte(expression: NumericExpression): BooleanExpression
  }

  /**
   * Expression that evaluates to a path
   */
  trait PathExpression extends Expression with CollectionExpression with BooleanExpression {
  }

  /**
   * Expression that evaluates to a node or relationship
   */
  trait PropertyContainerExpression extends ScalarExpression {
  }

  /**
   * Expression that evaluates to a node, relationship or property
   */
  trait ReferenceExpression extends Expression {
  }

  /**
   * Expression that evaluates to a relationship
   */
  trait RelationshipExpression extends PropertyContainerExpression {
  }

  /**
   * Expression that evaluates to a single value
   */
  trait ScalarExpression extends Expression {
    def in(collection: CollectionExpression): BooleanExpression
  }

  /**
   * Expression that evaluates to a string
   */
  trait StringExpression extends ScalarExpression {
    def gt(expression: String): BooleanExpression

    def gt(expression: StringExpression): BooleanExpression

    def gte(expression: String): BooleanExpression

    def gte(expression: StringExpression): BooleanExpression

    def lt(expression: String): BooleanExpression

    def lt(expression: StringExpression): BooleanExpression

    def lte(expression: String): BooleanExpression

    def lte(expression: StringExpression): BooleanExpression

    def regexp(regexp: String): BooleanExpression

    def regexp(regexp: StringExpression): BooleanExpression

    def regexp(regexp: String, caseSensitive: Boolean): BooleanExpression

    def concat(expression: String): StringExpression

    def concat(expression: StringExpression): StringExpression
  }

}
