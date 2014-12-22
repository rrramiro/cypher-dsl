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

import java.lang.{Iterable, StringBuilder}

import org.neo4j.cypherdsl.expression.{PathExpression, ReferenceExpression}
import org.neo4j.cypherdsl.query.clause._
import org.neo4j.cypherdsl.{AsString, SetProperty}

import scala.collection.JavaConversions.{asJavaIterable, iterableAsScalaIterable}
import scala.language.implicitConversions

/**
 * Represents a single statement to be executed with FOREACH
 */
class ForEachStatement(forEachClause: ForEachClause) extends ForEachStatements with AsString {

  def create(paths: PathExpression*): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new CreateClause(paths: _*)))
  }

  def create(paths: Iterable[PathExpression]): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new CreateClause(paths.toList: _*)))
  }

  def set(setProperties: SetProperty*): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new SetClause(setProperties: _*)))
  }

  def set(setProperties: Iterable[SetProperty]): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new SetClause(setProperties.toList: _*)))
  }

  def delete(expressions: ReferenceExpression*): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new DeleteClause(expressions: _*)))
  }

  def delete(expressions: Iterable[ReferenceExpression]): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new DeleteClause(expressions.toList: _*)))
  }

  def createUnique(expressions: PathExpression*): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new CreateUniqueClause(expressions)))
  }

  def createUnique(expressions: Iterable[PathExpression]): ForEachStatement = {
    new ForEachStatement(forEachClause.add(new CreateUniqueClause(expressions)))
  }

  def forEach(statement: ForEachStatement): ForEachStatement = {
    new ForEachStatement(forEachClause.add(statement.getClause))
  }

  def asString(builder: StringBuilder) {
    forEachClause.asString(builder)
  }

  def getClause: Clause = {
    forEachClause
  }
}
