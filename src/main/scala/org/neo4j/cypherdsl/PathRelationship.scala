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
import java.lang.StringBuilder

import org.neo4j.cypherdsl.CypherQuery.identifier
import org.neo4j.cypherdsl.expression.Expression
import org.neo4j.cypherdsl.query.{Direction, PropertyValue, PropertyValues, Query}

import scala.collection.JavaConversions.asJavaIterable
import scala.language.implicitConversions

/**
 * Represents a relationship in a path.
 */
class PathRelationship(leftNode: Path, direction: Direction, as: Identifier, relationships: Iterable[Identifier], relationshipPropertyValues: PropertyValues, minHops: Integer, maxHops: Integer) extends AsString with Serializable with Cloneable {

  /**
   * If this relationship is used in a CREATE or CREATE UNIQUE clause,
   * then you can use this method to specify property values.
   * Use e.g. {@link CypherQuery.value( String,Object )} to create
   * the individual values to be passed in here.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:relationship {prop1:value1,prop2:value2})
   * </pre>
   *
   * @param propertyValues
   * @return
   */
  def values(propertyValues: PropertyValue*): PathRelationship = {
    return new PathRelationship(leftNode, direction, as, relationships, new PropertyValues(propertyValues), minHops, maxHops)
  }

  /**
   * If this relationship is used in a CREATE or CREATE UNIQUE clause,
   * then you can use this method to specify property values.
   * Use e.g. {@link CypherQuery.value( String,Object )} to create
   * the individual values to be passed in here.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:relationship {prop1:value1,prop2:value2})
   * </pre>
   *
   * @param propertyValues
   * @return
   */
  def values(propertyValues: Iterable[PropertyValue]): PathRelationship = {
    return new PathRelationship(leftNode, direction, as, relationships, new PropertyValues(propertyValues), minHops, maxHops)
  }

  /**
   * Use this method to name a relationship
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[name]-(m)
   * </pre>
   *
   * @param name
   * @return
   */
  def as(name: String): PathRelationship = {
    return as(CypherQuery.identifier(name))
  }

  /**
   * Use this method to name a relationship
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[name]-(m)
   * </pre>
   *
   * @param name
   * @return
   */
  def as(name: Identifier): PathRelationship = {
    Query.checkNull(name, "Name")
    return new PathRelationship(leftNode, direction, name, relationships, relationshipPropertyValues, minHops, maxHops)
  }

  /**
   * Use this method to declare how many hops are allowed. You can either specify
   * min, max or both.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:*minHops,maxHops]-(m)
   * </pre>
   *
   * @param minHops
   * @param maxHops
   * @return
   */
  def hops(minHops: Integer, maxHops: Integer): PathRelationship = {
    if (minHops != null && minHops < 0) {
      throw new IllegalArgumentException("Minimum number of hops must be over zero")
    }
    if (maxHops != null && maxHops < 0) {
      throw new IllegalArgumentException("Maximum number of hops must be over zero")
    }
    return new PathRelationship(leftNode, direction, as, relationships, relationshipPropertyValues, minHops, maxHops)
  }

  /**
   * Declare the end node of this path. This must be called before using
   * this expression in any clause, as otherwise you would not have a complete
   * path.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)--()
   * </pre>
   *
   * @return
   */
  def node: Path = {
    return new Path(null, this, null, null)
  }

  /**
   * Declare the end node of this path. This must be called before using
   * this expression in any clause, as otherwise you would not have a complete
   * path.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)--(id)
   * </pre>
   *
   * @return
   */
  def node(id: String): Path = {
    return node(identifier(id))
  }

  /**
   * Declare the end node of this path. This must be called before using
   * this expression in any clause, as otherwise you would not have a complete
   * path.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)--(id)
   * </pre>
   *
   * @return
   */
  def node(id: Expression): Path = {
    return new Path(id, this, null, null)
  }

  def asString(builder: StringBuilder) {
    leftNode.asString(builder)
    builder.append(if ((direction == Direction.IN)) "<-" else "-")
    val hasRelationships: Boolean = relationships.iterator.hasNext
    if (as != null || hasRelationships || minHops != null || maxHops != null || relationshipPropertyValues != null) {
      builder.append('[')
      if (as != null) {
        as.asString(builder)
      }
      if (hasRelationships) {
        builder.append(':')
        var or: String = ""
        for (relationship <- relationships) {
          builder.append(or)
          relationship.asString(builder)
          or = "|"
        }
      }
      if (minHops != null || maxHops != null) {
        builder.append('*')
        if (minHops != null) {
          builder.append(minHops)
        }
        builder.append("..")
        if (maxHops != null) {
          builder.append(maxHops)
        }
      }
      if (relationshipPropertyValues != null) {
        builder.append(' ')
        relationshipPropertyValues.asString(builder)
      }
      builder.append(']')
    }
    builder.append(if ((direction == Direction.OUT)) "->" else "-")
  }
}
