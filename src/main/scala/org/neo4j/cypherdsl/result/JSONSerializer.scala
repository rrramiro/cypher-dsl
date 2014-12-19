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
package org.neo4j.cypherdsl.result

import java.lang.Iterable
import java.util.Map

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.{ArrayNode, ObjectNode}
import org.neo4j.graphdb.{Node, Path}

/**
 * Serializer that converts Cypher execution results to JSON.
 */
class JSONSerializer {
  private final val mapper: ObjectMapper = new ObjectMapper

  def toJSON(result: Iterable[Map[String, AnyRef]]): ArrayNode = {
    val root: ArrayNode = mapper.createArrayNode
    import scala.collection.JavaConversions._
    for (stringObjectMap <- result) {
      val entry: ObjectNode = root.objectNode
      import scala.collection.JavaConversions._
      for (stringObjectEntry <- stringObjectMap.entrySet) {
        if (stringObjectEntry.getValue.isInstanceOf[Path]) {
          entry.put(stringObjectEntry.getKey, stringObjectEntry.getValue.toString)
        }
        else if (stringObjectEntry.getValue.isInstanceOf[Node]) {
          val node: Node = stringObjectEntry.getValue.asInstanceOf[Node]
          val nodeNode: ObjectNode = entry.objectNode
          nodeNode.put("_id", node.getId)
          import scala.collection.JavaConversions._
          for (propertyName <- node.getPropertyKeys) {
            addProperty(nodeNode, propertyName, node.getProperty(propertyName))
          }
          entry.put(stringObjectEntry.getKey, nodeNode)
        }
        else {
          addProperty(entry, stringObjectEntry.getKey, stringObjectEntry.getValue)
        }
      }
      root.add(entry)
    }
    return root
  }

  private def addProperty(node: ObjectNode, name: String, value: AnyRef) {
    if (value.isInstanceOf[String]) {
      node.put(name, value.toString)
    }
    else if (value.isInstanceOf[Long]) {
      val number: Long = value.asInstanceOf[Long]
      node.put(name, number)
    }
    else if (value.isInstanceOf[Integer]) {
      val number: Integer = value.asInstanceOf[Integer]
      node.put(name, number)
    }
    else {
      throw new IllegalArgumentException("Unknown value type:" + value.getClass)
    }
  }
}