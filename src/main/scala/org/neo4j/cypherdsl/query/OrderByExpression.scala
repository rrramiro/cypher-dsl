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


import java.lang.StringBuilder

import org.neo4j.cypherdsl._
import org.neo4j.cypherdsl.expression.Expression

/**
 * Provides the possible expressions for the ORDER BY clause.
 */
case class OrderByExpression(expression: Expression, order: Order.Value) extends AbstractExpression {


  def order(order: Order.Value): OrderByExpression = {
    new OrderByExpression(expression, order)
  }

  def asString(builder: StringBuilder) {
    expression.asString(builder)

    order match {
      case Order.ASCENDING =>
        builder.append(' ').append("ASCENDING")
      case Order.DESCENDING =>
        builder.append(' ').append("DESCENDING")
      case _ =>
    }
  }

  @throws(classOf[CloneNotSupportedException])
  override def clone: AnyRef = {
    super.clone
  }
}
