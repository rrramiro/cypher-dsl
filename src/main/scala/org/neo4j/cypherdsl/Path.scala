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

import java.lang.StringBuilder

import org.neo4j.cypherdsl.CypherQuery.{identifier, identifiers}
import org.neo4j.cypherdsl.expression.{Expression, PathExpression}
import org.neo4j.cypherdsl.query.Direction._
import org.neo4j.cypherdsl.query._

import scala.language.implicitConversions

/**
 * Represents either a single node or a path from one node to another.
 */
case class Path(node: Expression, relationship: PathRelationship, nodePropertyValues: Expression, nodeLabels: Expression) extends AbstractExpression with PathExpression {

  def labels(labels: LabelValue*): Path = {
    return new Path(node, relationship, nodePropertyValues, new LabelValues(labels))
  }

  def labels(labels: Iterable[LabelValue]): Path = {
    return new Path(node, relationship, nodePropertyValues, new LabelValues(labels))
  }

  def label(label: String): Path = {
    return new Path(node, relationship, nodePropertyValues, new LabelValue(identifier(label)))
  }

  def label(label: Identifier): Path = {
    return new Path(node, relationship, nodePropertyValues, new LabelValue(label))
  }

  /**
   * If this node is used in a CREATE or CREATE UNIQUE clause,
   * then you can use this method to specify property values.
   * Use e.g. {@link CypherQuery.value( String,Object )} to create
   * the individual values to be passed in here.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n {prop1:value1,prop2:value2})
   * </pre>
   *
   * @param propertyValues
   * @return
   */
  def values(propertyValues: PropertyValue*): Path = {
    return new Path(node, relationship, new PropertyValues(propertyValues), nodeLabels)
  }

  //ToDo Remove this method
  @Deprecated
  def values(firstPropertyValue:(String, AnyRef), otherPropertyValues: (String, AnyRef)*): Path = {
    val propertyValues = firstPropertyValue :: otherPropertyValues.toList
    new Path(node, relationship, new PropertyValues(propertyValues.map(item => CypherQuery.value(item._1, item._2.toString))), nodeLabels)
  }


  /**
   * If this node is used in a CREATE or CREATE UNIQUE clause,
   * then you can use this method to specify property values.
   * Use e.g. {@link CypherQuery.value( String,Object )} to create
   * the individual values to be passed in here.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n {prop1:value1,prop2:value2})
   * </pre>
   *
   * @param propertyValues
   * @return
   */
  def values(propertyValues: Iterable[PropertyValue]): Path = {
    return new Path(node, relationship, new PropertyValues(propertyValues), nodeLabels)
  }

  /**
   * If this node is used in a CREATE or CREATE UNIQUE clause,
   * then you can use this method to specify property values which
   * should be taken from a map parameter.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n {propertyValues})
   * </pre>
   *
   * @param propertyValues
   * @return
   */
  def values(propertyValues: Parameter): Path = {
    return new Path(node, relationship, propertyValues, nodeLabels)
  }

  def relationship(direction: Direction.Value, relationships: String*): PathRelationship = {
    return new PathRelationship(this, direction, null, identifiers(relationships: _*), null, null, null)
  }

  /**
   * Declare a new outgoing relationship from this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-->(m)
   * </pre>
   *
   * @return
   */
  def out: PathRelationship = {
    return new PathRelationship(this, OUT, null, List.empty, null, null, null)
  }

  /**
   * Declare a new outgoing relationship from this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:relationship1|relationship2]->(m)
   * </pre>
   *
   * @return
   */
  def out(relationships: String*): PathRelationship = {
    return new PathRelationship(this, OUT, null, identifiers(relationships: _*), null, null, null)
  }

  /**
   * Declare a new outgoing relationship from this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:relationship1|relationship2]->(m)
   * </pre>
   *
   * @return
   */
  def out(relationship: Identifier, relationships: Identifier*): PathRelationship = {
    return new PathRelationship(this, OUT, null, relationship :: relationships.toList, null, null, null)
  }

  //  /**
  //   * Declare a new outgoing relationship from this node.
  //   * <p/>
  //   * Corresponds to:
  //   * <pre>
  //   * (n)-[:relationship1|relationship2]->(m)
  //   * </pre>
  //   *
  //   * @return
  //   */
  //  def out(relationships: Enum[_]*): PathRelationship = {
  //    val relationshipNames = new ListBuffer[Identifier]()
  //    for (relationship <- relationships) {
  //      relationshipNames.+=(identifier(relationship.name))
  //    }
  //    return new PathRelationship(this, OUT, null, relationshipNames, null, null, null)
  //  }

  /**
   * Declare a new outgoing relationship from this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)<--(m)
   * </pre>
   *
   * @return
   */
  def in: PathRelationship = {
    return new PathRelationship(this, IN, null, List.empty, null, null, null)
  }

  /**
   * Declare a new incoming relationship to this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)<-[:relationship1|relationship2]-(m)
   * </pre>
   *
   * @return
   */
  def in(relationships: String*): PathRelationship = {
    return new PathRelationship(this, IN, null, identifiers(relationships: _*), null, null, null)
  }

  /**
   * Declare a new incoming relationship to this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)<-[:relationship1|relationship2]-(m)
   * </pre>
   *
   * @return
   */
  def in(relationship: Identifier, relationships: Identifier*): PathRelationship = {
    return new PathRelationship(this, IN, null, relationship :: relationships.toList, null, null, null)
  }

  //  /**
  //   * Declare a new incoming relationship to this node.
  //   * <p/>
  //   * Corresponds to:
  //   * <pre>
  //   * (n)<-[:relationship1|relationship2]-(m)
  //   * </pre>
  //   *
  //   * @return
  //   */
  //  def in(relationships: Enum[_]*): PathRelationship = {
  //    val relationshipNames = new ListBuffer[Identifier]()
  //    for (relationship <- relationships) {
  //      relationshipNames.+=(identifier(relationship.name))
  //    }
  //    return new PathRelationship(this, IN, null, relationshipNames, null, null, null)
  //  }

  /**
   * Declare a new relationship on this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)--(m)
   * </pre>
   *
   * @return
   */
  def both: PathRelationship = {
    return new PathRelationship(this, BOTH, null, List.empty, null, null, null)
  }

  /**
   * Declare a new relationship on this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:relationship1|relationship2]-(m)
   * </pre>
   *
   * @return
   */
  def both(relationships: String*): PathRelationship = {
    return new PathRelationship(this, BOTH, null, identifiers(relationships: _*), null, null, null)
  }

  /**
   * Declare a new relationship on this node.
   * <p/>
   * Corresponds to:
   * <pre>
   * (n)-[:relationship1|relationship2]-(m)
   * </pre>
   *
   * @return
   */
  def both(relationship: Identifier, relationships: Identifier*): PathRelationship = {
    return new PathRelationship(this, BOTH, null, relationship :: relationships.toList, null, null, null)
  }

  //  /**
  //   * Declare a new relationship on this node.
  //   * <p/>
  //   * Corresponds to:
  //   * <pre>
  //   * (n)-[:relationship1|relationship2]-(m)
  //   * </pre>
  //   *
  //   * @return
  //   */
  //  def both(relationships: Enum[_]*): PathRelationship = {
  //    val relationshipNames = new ListBuffer[Identifier]()
  //    for (relationship <- relationships) {
  //      relationshipNames.+=(identifier(relationship.name))
  //    }
  //    return new PathRelationship(this, BOTH, null, relationshipNames, null, null, null)
  //  }

  def asString(builder: StringBuilder) {
    if (relationship != null) {
      relationship.asString(builder)
    }
    builder.append('(')
    if (node != null) {
      node.asString(builder)
      if (nodeLabels != null) {
        nodeLabels.asString(builder)
      }
      if (nodePropertyValues != null) {
        builder.append(' ')
        nodePropertyValues.asString(builder)
      }
    }
    else {
      if (nodeLabels != null) {
        nodeLabels.asString(builder)
      }
      if (nodePropertyValues != null) {
        nodePropertyValues.asString(builder)
      }
    }
    builder.append(')')
  }
}
