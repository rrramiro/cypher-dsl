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

import org.neo4j.cypherdsl.expression.NumericExpression
import org.neo4j.cypherdsl.expression.ReferenceExpression
import org.neo4j.cypherdsl.expression.StringExpression
import org.neo4j.cypherdsl.query.AbstractExpression
import org.neo4j.cypherdsl.query.Value
import java.util.regex.Pattern
import org.neo4j.cypherdsl.CypherQuery.identifier
import org.neo4j.cypherdsl.query.Query.checkNull
import java.lang.StringBuilder

/**
 * Represents an identifier. If the identifier is a node or relationship,
 * then you can use the property() method to get a representation of individual properties.
 */
object Identifier {
  private val simpleName: Pattern = Pattern.compile("\\p{Alpha}\\w*")

  private class IdentifierExpression(name: String) extends AbstractExpression {
    def asString(builder: StringBuilder) {
      if (simpleName.matcher(name).matches) {
        builder.append(name)
      }
      else {
        builder.append('`').append(name).append('`')
      }
    }
  }

}

private[cypherdsl] case class Identifier(name: String) extends Value(new Identifier.IdentifierExpression(name)) with ReferenceExpression {

  /**
   * If this identifier represents a node or relationship,
   * then you can use this method to denote a property.
   * <p/>
   * Corresponds to:
   * <pre>
   * id.name
   * </pre>
   *
   * @param name
   * @return
   */
  def property(name: String): Property = {
    return property(identifier(name))
  }

  /**
   * If this identifier represents a node or relationship,
   * then you can use this method to denote a property.
   * <p/>
   * Corresponds to:
   * <pre>
   * id.name
   * </pre>
   *
   * @param name
   * @return
   */
  def property(name: Identifier): Property = {
    checkNull(name, "Name")
    return new Property(this, name)
  }

  /**
   * If this identifier represents a node or relationship,
   * then you can use this method to denote a string property.
   * <p/>
   * Corresponds to:
   * <pre>
   * id.name
   * </pre>
   *
   * @param name
   * @return
   */
  def string(name: String): StringExpression = {
    return string(identifier(name))
  }

  /**
   * If this identifier represents a node or relationship,
   * then you can use this method to denote a string property.
   * <p/>
   * Corresponds to:
   * <pre>
   * id.name
   * </pre>
   *
   * @param name
   * @return
   */
  def string(name: Identifier): StringExpression = {
    checkNull(name, "Name")
    return new Property(this, name)
  }

  /**
   * If this identifier represents a node or relationship,
   * then you can use this method to denote a numeric property.
   * <p/>
   * Corresponds to:
   * <pre>
   * id.name
   * </pre>
   *
   * @param name
   * @return
   */
  def number(name: String): NumericExpression = {
    return number(identifier(name))
  }

  /**
   * If this identifier represents a node or relationship,
   * then you can use this method to denote a numeric property.
   * <p/>
   * Corresponds to:
   * <pre>
   * id.name
   * </pre>
   *
   * @param name
   * @return
   */
  def number(name: Identifier): NumericExpression = {
    checkNull(name, "Name")
    return new Property(this, name)
  }
}
