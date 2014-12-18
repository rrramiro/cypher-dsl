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

import org.neo4j.cypherdsl.CypherQuery
import org.neo4j.cypherdsl.expression.BooleanExpression
import org.neo4j.cypherdsl.expression.CollectionExpression
import org.neo4j.cypherdsl.expression.Expression
import org.neo4j.cypherdsl.CypherQuery.literal
import java.lang.StringBuilder

/**
 * Common methods for all expressions
 */
abstract class AbstractExpression extends Expression {

  def eq(expression: String): BooleanExpression = {
    return new Value(new Operator(this, "="), literal(expression))
  }

  override def eq(expression: Number): BooleanExpression = {
    return new Value(new Operator(this, "="), literal(expression))
  }


  def eq(expression: Expression): BooleanExpression = {
    return new Value(new Operator(this, "="), expression)
  }

  def ne(expression: String): BooleanExpression = {
    return new Value(new Operator(this, "<>"), literal(expression))
  }

  def ne(expression: Number): BooleanExpression = {
    return new Value(new Operator(this, "<>"), literal(expression))
  }

  def ne(expression: Expression): BooleanExpression = {
    return new Value(new Operator(this, "<>"), expression)
  }

  def and(expression: BooleanExpression): BooleanExpression = {
    return new CypherQuery.And(this.asInstanceOf[BooleanExpression], expression)
  }

  def or(expression: BooleanExpression): BooleanExpression = {
    return new CypherQuery.Or(this.asInstanceOf[BooleanExpression], expression)
  }

  def in(collection: CollectionExpression): BooleanExpression = {
    return new Value(new Operator(this, " IN "), collection)
  }

  def union(expression: CollectionExpression): CollectionExpression = {
    return new Value(new Operator(this, "+"), expression)
  }

  @throws(classOf[CloneNotSupportedException])
  override def clone: AnyRef = {
    return super.clone
  }

  override def toString: String = {
    val builder: StringBuilder = new StringBuilder
    asString(builder)
    return builder.toString
  }
}
