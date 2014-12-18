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
package org.neo4j.cypherdsl.query

import org.neo4j.cypherdsl.expression._
import org.neo4j.cypherdsl.CypherQuery.literal
import java.lang.StringBuilder

/**
 * Handles a single value that corresponds to any expression. Optionally
 * can be a part of binary operation.
 */
object Value{
  def apply(operator: Operator, value: Expression) = {
    Query.checkNull(operator, "Operator")
    Query.checkNull(value, "Value")
    new Value(operator, value)
  }

  def apply(value: Expression) = {
    Query.checkNull(value, "Value")
    new Value(null, value)
  }
}

case class Value(operator: Operator, value: Expression) extends AbstractExpression with ScalarExpression with NumericExpression with StringExpression with BooleanExpression with NodeExpression with RelationshipExpression with PathExpression with CollectionExpression {

  def add(expression: Number): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "+"), literal(expression))
  }

  def add(expression: NumericExpression): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "+"), expression)
  }

  def subtract(expression: Number): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "-"), literal(expression))
  }

  def subtract(expression: NumericExpression): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "-"), expression)
  }

  def times(expression: Number): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "*"), literal(expression))
  }

  def times(expression: NumericExpression): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "*"), expression)
  }

  def divideBy(expression: Number): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "/"), literal(expression))
  }

  def divideBy(expression: NumericExpression): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "/"), expression)
  }

  def mod(expression: Number): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "%"), literal(expression))
  }

  def mod(expression: NumericExpression): NumericExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "%"), expression)
  }

  def gt(expression: Number): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">"), literal(expression))
  }

  def lt(expression: Number): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<"), literal(expression))
  }

  def gte(expression: Number): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">="), literal(expression))
  }

  def lte(expression: Number): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<="), literal(expression))
  }

  def gt(expression: NumericExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">"), expression)
  }

  def lt(expression: NumericExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<"), expression)
  }

  def gte(expression: NumericExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">="), expression)
  }

  def lte(expression: NumericExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<="), expression)
  }

  def gt(expression: String): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">"), literal(expression))
  }

  def gt(expression: StringExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">"), expression)
  }

  def gte(expression: String): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">="), literal(expression))
  }

  def gte(expression: StringExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, ">="), expression)
  }

  def lt(expression: String): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<"), literal(expression))
  }

  def lt(expression: StringExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<"), expression)
  }

  def lte(expression: String): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<="), literal(expression))
  }

  def lte(expression: StringExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new Operator(this, "<="), expression)
  }

  /**
   * Create a case-sensitive regular expression. Corresponds to:
   * <pre>
   * property ~=/regex/
   * </pre>
   *
   * @param regexp
   * @return
   */
  def regexp(regexp: String): BooleanExpression = {
    return new Value(new Operator(this, "=~"), literal(regexp))
  }

  def regexp(regexp: StringExpression): BooleanExpression = {
    return new Value(new Operator(this, "=~"), regexp)
  }

  /**
   * Create a regular expression. Corresponds to:
   * <pre>
   * property ~=/regex/
   * </pre>
   *
   * @param regexpStr
   * @param caseSensitive
   * @return
   */
  def regexp(regexpStr: String, caseSensitive: Boolean): BooleanExpression = {
    if (caseSensitive) {
      return regexp(regexpStr)
    }
    else {
      return new Value(new Operator(this, "=~"), literal("(?i)" + regexpStr))
    }
  }

  def concat(expression: String): StringExpression = {
    return new Value(new Operator(this, "+"), literal(expression))
  }

  def concat(expression: StringExpression): StringExpression = {
    return new Value(new Operator(this, "+"), expression)
  }

  def asString(builder: StringBuilder) {
    if (operator != null) {
      operator.asString(builder)
    }
    value.asString(builder)
  }

  override def toString: String = {
    return value.toString
  }
}
