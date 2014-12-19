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
package org.neo4j.cypherdsl.query.neo4j

import org.neo4j.cypherdsl.CypherQuery.{identifier, literals}
import org.neo4j.cypherdsl.Identifier
import org.neo4j.cypherdsl.expression.StartExpression
import org.neo4j.cypherdsl.query.Query
import org.neo4j.cypherdsl.query.Query.checkNull
import org.neo4j.graphdb.Node

/**
 * START expressions that use Neo4j Node objects directly, thus avoiding use of
 * long identifiers.
 */
object StartExpressionNeo {
  def nodeById(name: String, nodes: Node*): StartExpression.StartNodes = {
    return nodeById(identifier(name), nodes: _*)
  }

  def nodeById(name: Identifier, nodes: Node*): StartExpression.StartNodes = {
    checkNull(name, "Name")
    for (node <- nodes) {
      Query.checkNull(node, "Node")
    }
    val ids: Array[Long] = new Array[Long](nodes.length)

    var i: Int = 0
    while (i < nodes.length) {
      val node: Node = nodes(i)
      ids(i) = node.getId
      i += 1
    }

    return new StartExpression.StartNodes(name, literals(ids: _*): _*)
  }
}

