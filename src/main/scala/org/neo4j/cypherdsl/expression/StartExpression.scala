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
package org.neo4j.cypherdsl.expression

import org.neo4j.cypherdsl.Identifier
import org.neo4j.cypherdsl.query.AbstractExpression
import java.lang.StringBuilder

/**
 * Provides the possible expressions for the START clause.
 */
object StartExpression {

  class AllNodes extends AbstractExpression {
    def asString(builder: StringBuilder) {
      builder.append("*")
    }
  }

  class StartNodes(name: Identifier, nodes: Expression*) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=node(")

      var i: Int = 0
      while (i < nodes.length) {
        val node: Expression = nodes(i)
        if (i > 0) {
          builder.append(',')
        }
        node.asString(builder)
        i += 1
      }

      builder.append(')')
    }
  }

  class StartNodesLookup(name: Identifier, index: Identifier, key: ReferenceExpression, value: Expression) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=node:")
      index.asString(builder)
      builder.append('(')
      key.asString(builder)
      builder.append("=")
      value.asString(builder)
      builder.append(')')
    }
  }

  class StartNodesQuery(name: Identifier, index: Identifier, query: String) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=node:")
      index.asString(builder)
      if (query.contains("\"")) {
        builder.append("('").append(query).append("')")
      }
      else {
        builder.append("(\"").append(query).append("\")")
      }
    }
  }

  class StartNodesQueryParam(name: Identifier, index: Identifier, param: String) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=node:")
      index.asString(builder)
      builder.append("({").append(param).append("})")
    }
  }

  class StartRelationships(name: Identifier, relationships: Array[Expression]) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=relationship(")

      var i: Int = 0
      while (i < relationships.length) {
        val rel: Expression = relationships(i)
        if (i > 0) {
          builder.append(',')
        }
        rel.asString(builder)
        i += 1
      }
      builder.append(')')
    }
  }

  class StartRelationshipsParameters(name: Identifier, parameter: String) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=relationship({").append(parameter).append("})")
    }
  }

  class StartRelationshipsIndex(name: Identifier, index: Identifier, key: Identifier, value: StringExpression) extends StartExpression(name) {

    def asString(builder: StringBuilder) {
      name.asString(builder)
      builder.append("=relationship:")
      index.asString(builder)
      builder.append('(')
      key.asString(builder)
      builder.append('=')
      value.asString(builder)
      builder.append(')')
    }
  }

}

abstract class StartExpression(name: Identifier) extends AbstractExpression