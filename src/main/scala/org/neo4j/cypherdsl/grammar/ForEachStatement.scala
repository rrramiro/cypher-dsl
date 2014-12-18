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
package org.neo4j.cypherdsl.grammar

import org.neo4j.cypherdsl.AsString
import org.neo4j.cypherdsl.SetProperty
import org.neo4j.cypherdsl.expression.PathExpression
import org.neo4j.cypherdsl.expression.ReferenceExpression
import org.neo4j.cypherdsl.query.clause._
import scala.collection.JavaConversions.asJavaIterable
import scala.language.implicitConversions
import java.lang.StringBuilder
import java.lang.Iterable

/**
 * Represents a single statement to be executed with FOREACH
 */
class ForEachStatement(forEachClause: ForEachClause) extends ForEachStatements with AsString {

  def create(paths: PathExpression*): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new CreateClause(paths)))
  }

  def create(paths: Iterable[PathExpression]): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new CreateClause(paths)))
  }

  def set(setProperties: SetProperty*): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new SetClause(setProperties)))
  }

  def set(setProperties: Iterable[SetProperty]): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new SetClause(setProperties)))
  }

  def delete(expressions: ReferenceExpression*): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new DeleteClause(expressions)))
  }

  def delete(expressions: Iterable[ReferenceExpression]): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new DeleteClause(expressions)))
  }

  def createUnique(expressions: PathExpression*): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new CreateUniqueClause(expressions)))
  }

  def createUnique(expressions: Iterable[PathExpression]): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(new CreateUniqueClause(expressions)))
  }

  def forEach(statement: ForEachStatement): ForEachStatement = {
    return new ForEachStatement(forEachClause.add(statement.getClause))
  }

  def asString(builder: StringBuilder) {
    forEachClause.asString(builder)
  }

  def getClause: Clause = {
    return forEachClause
  }
}
