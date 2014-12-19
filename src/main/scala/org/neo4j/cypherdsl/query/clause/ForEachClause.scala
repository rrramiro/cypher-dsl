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
package org.neo4j.cypherdsl.query.clause

import java.lang.{Iterable, StringBuilder}
import java.util.{ArrayList, List}

import org.neo4j.cypherdsl.{AsString, Identifier, SetProperty}
import org.neo4j.cypherdsl.expression.{Expression, PathExpression, ReferenceExpression}
import org.neo4j.cypherdsl.grammar.{ForEachStatement, ForEachStatements}

import scala.collection.JavaConversions.{asJavaIterable, iterableAsScalaIterable}
import scala.language.implicitConversions

/**
 * FOR EACH clause
 */
class ForEachClause(id: Identifier, in: Expression) extends Clause with AsString with ForEachStatements {
  private final val forEachStatements: List[AsString] = new ArrayList[AsString]


  def create(paths: PathExpression*): ForEachStatement = {
    return new ForEachStatement(add(new CreateClause(paths: _*)))
  }

  def create(paths: Iterable[PathExpression]): ForEachStatement = {
    return new ForEachStatement(add(new CreateClause(paths.toList: _*)))
  }

  def set(setProperties: SetProperty*): ForEachStatement = {
    return new ForEachStatement(add(new SetClause(setProperties: _*)))
  }

  def set(setProperties: Iterable[SetProperty]): ForEachStatement = {
    return new ForEachStatement(add(new SetClause(setProperties.toList: _*)))
  }

  def delete(expressions: ReferenceExpression*): ForEachStatement = {
    return new ForEachStatement(add(new DeleteClause(expressions: _*)))
  }

  def delete(expressions: Iterable[ReferenceExpression]): ForEachStatement = {
    return new ForEachStatement(add(new DeleteClause(expressions.toList: _*)))
  }

  def createUnique(expressions: PathExpression*): ForEachStatement = {
    return new ForEachStatement(add(new CreateUniqueClause(expressions)))
  }

  def createUnique(expressions: Iterable[PathExpression]): ForEachStatement = {
    return new ForEachStatement(add(new CreateUniqueClause(expressions)))
  }

  def forEach(statement: ForEachStatement): ForEachStatement = {
    return new ForEachStatement(add(statement))
  }

  def add(clause: AsString): ForEachClause = {
    forEachStatements.add(clause)
    return this
  }

  def asString(builder: StringBuilder) {
    builder.append(" FOREACH(")
    id.asString(builder)
    builder.append(" in ")
    in.asString(builder)
    builder.append("|")
    var comma: String = ""
    import scala.collection.JavaConversions._
    for (forEachStatement <- forEachStatements) {
      builder.append(comma)
      forEachStatement.asString(builder)
      comma = ","
    }
    builder.append(')')
  }
}
