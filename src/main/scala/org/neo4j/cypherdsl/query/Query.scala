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


import java.io.Serializable
import java.lang.StringBuilder
import java.util.ArrayList

import org.neo4j.cypherdsl.{AsString, Literal}
import org.neo4j.cypherdsl.expression.Expression
import org.neo4j.cypherdsl.query.clause.{Clause, WhereClause}

import scala.collection.JavaConversions._

/**
 * Model for a Cypher Query. The model is serializable and cloneable, to make it easy to
 * save on disk or transfer over the wire. Being cloneable also helps with query builder continuation.
 */
object Query {
  private val QUERY_PREFIX: String = "CYPHER "
  private val DEFAULT_CYPHER_VERSION: String = "2.0"

  def isEmpty(string: String): Boolean = {
    return string == null || string.length == 0
  }

  def checkNull(`object`: AnyRef, name: String) {
    if (`object` == null) {
      throw new IllegalArgumentException(name + " may not be null")
    }
    if (`object`.getClass.isArray) {
      val array: Array[AnyRef] = `object`.asInstanceOf[Array[AnyRef]]
      for (obj <- array) {
        if (obj == null) {
          throw new IllegalArgumentException(name + " may not be null")
        }
      }
    }
  }

  def checkEmpty(string: String, name: String) {
    if (isEmpty(string)) {
      throw new IllegalArgumentException(name + " may not be null or empty string")
    }
  }

  def checkEmpty(value: Expression, name: String) {
    if (value.isInstanceOf[Literal[_]] && isEmpty(value.toString)) {
      throw new IllegalArgumentException(name + " may not be null or empty string")
    }
  }

  def checkEmpty(strings: Array[String], name: String) {
    for (string <- strings) {
      if (isEmpty(string)) {
        throw new IllegalArgumentException(name + " may not be null or empty string")
      }
    }
  }
}

class Query(clauses: ArrayList[Clause] = new ArrayList[Clause]) extends AsString with Serializable with Cloneable {

  def add(clause: Clause) {
    if (!clauses.isEmpty && clause.isInstanceOf[WhereClause]) {
      val previousWhere: WhereClause = lastClause(classOf[WhereClause])
      if (previousWhere != null) {
        previousWhere.mergeWith(clause.asInstanceOf[WhereClause])
        return
      }
    }
    clauses.add(clause)
  }

  //Todo use Options
  def lastClause[T <: Clause](`type`: Class[T]): T = {
    val clause: Clause = clauses.get(clauses.size - 1)
    return if (`type`.isInstance(clause)) `type`.cast(clause) else (null.asInstanceOf[T])
  }

  def asString(builder: StringBuilder) {
    asString(builder, Query.DEFAULT_CYPHER_VERSION)
  }

  def asString(builder: StringBuilder, cypherVersion: String) {
    builder.append(Query.QUERY_PREFIX)
    builder.append(cypherVersion)

    for (clause <- clauses) {
      clause.asString(builder)
    }
  }


  @throws(classOf[CloneNotSupportedException])
  override def clone: AnyRef = {
    return new Query(clauses.clone.asInstanceOf[ArrayList[Clause]])
  }

  override def toString: String = {
    val builder: StringBuilder = new StringBuilder
    asString(builder)
    return builder.toString
  }
}
