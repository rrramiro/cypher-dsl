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

import java.lang.Iterable
import java.util.Map

import org.neo4j.cypherdsl.expression._
import org.neo4j.cypherdsl.query.Query

package object grammar {

  /**
   * Represents the CREATE clause
   */
  trait Create {
    def create(paths: PathExpression*): UpdateNext

    def create(paths: Iterable[PathExpression]): UpdateNext
  }

  /**
   * Represents the CREATE UNIQUE clause
   */
  trait CreateUnique {
    def createUnique(expressions: PathExpression*): UpdateNext

    def createUnique(expressions: Iterable[PathExpression]): UpdateNext
  }

  /**
   * Represents the DELETE clause
   */
  trait Delete {
    def delete(expressions: ReferenceExpression*): UpdateNext

    def delete(expressions: Iterable[ReferenceExpression]): UpdateNext
  }

  /**
   * Once the query has been constructed, the methods here can be used to either stringify it or extract the
   * Query model for further processing. Can also be used to specify parameters to be used for execution.
   * <p/>
   * Note that setting parameters will create a new set of parameters for each execution, so that you can reuse
   * this Execute instance for many executions.
   */
  trait Execute extends AsString {
    def toQuery: Query

    /**
     * Create a ExecuteWithParameters that has the given parameter set. The result
     * can be used to further specify parameters before execution.
     *
     * @param name
     * @param value
     * @return
     */
    def parameter(name: String, value: AnyRef): ExecuteWithParameters

    /**
     * Create a ExecuteWithParameters that has the given parameters set. The result
     * can be used to further specify parameters before execution. Note that the given
     * map overwrites any existing parameters that have already been set, if they have the same names.
     *
     * @param parameters
     * @return
     */
    def parameters(parameters: Map[String, AnyRef]): ExecuteWithParameters
  }

  /**
   * Use this to access the parameters that have been set for this query
   */
  trait ExecuteWithParameters extends Execute {
    /**
     * Get the parameters that have been set for this query.
     *
     * @return
     */
    def getParameters: Map[String, AnyRef]
  }

  /**
   * Represents the FOREACH clause
   */
  trait ForEach {
    def forEach(statement: ForEachStatement): UpdateNext
  }

  /**
   * Used to create statements for the FOREACH clause. When you
   * have set all statements you want to evaluate for this FOR EACH clause,
   * use the resulting ForEachStatement as input to the {@link ForEach.forEach()} method.
   */
  trait ForEachStatements {
    def create(paths: PathExpression*): ForEachStatement

    def create(paths: Iterable[PathExpression]): ForEachStatement

    def set(setProperties: SetProperty*): ForEachStatement

    def set(setProperties: Iterable[SetProperty]): ForEachStatement

    def delete(expressions: ReferenceExpression*): ForEachStatement

    def delete(expressions: Iterable[ReferenceExpression]): ForEachStatement

    def createUnique(expressions: PathExpression*): ForEachStatement

    def createUnique(expressions: Iterable[PathExpression]): ForEachStatement

    def forEach(statement: ForEachStatement): ForEachStatement
  }

  /**
   * Represents the LIMIT clause
   */
  trait Limit extends Execute with Union {
    def limit(nrToBeSkipped: Int): Execute

    def limit(parameterName: String): Execute
  }

  /**
   * Represents the MATCH clause.
   */
  trait Match extends Where with ForEach {
    def `match`(expression: PathExpression*): Match

    def `match`(expressions: Iterable[PathExpression]): Match

    def optional: Match
  }

  /**
   * Represents the MERGE clause
   */
  trait Merge {
    def merge(expressions: PathExpression*): UpdateNext

    def merge(expressions: Iterable[PathExpression]): UpdateNext
  }

  /**
   * Represents the ORDER BY clause.
   */
  trait OrderBy extends Skip with Return {
    def orderBy(orderByExpression: Expression*): OrderBy

    def orderBy(orderByExpressions: Iterable[Expression]): OrderBy
  }

  /**
   * Represents the DELETE clause
   */
  trait Remove {
    def remove(expressions: ReferenceExpression*): UpdateNext

    def remove(expressions: Iterable[ReferenceExpression]): UpdateNext
  }

  /**
   * Represents the RETURN clause.
   */
  trait Return extends Execute {
    def returns(returnExpression: Expression*): ReturnNext

    def returns(returnExpressions: Iterable[Expression]): ReturnNext
  }

  /**
   * This specifies what can come after a RETURN clause
   */
  trait ReturnNext extends Return with OrderBy {
  }

  /**
   * Represents the SET clause.
   */
  trait Set {
    def set(propertyValues: SetProperty*): UpdateNext

    def set(propertyValues: Iterable[SetProperty]): UpdateNext
  }

  /**
   * Represents the SKIP clause.
   */
  trait Skip extends Limit {
    def skip(nrToBeSkipped: Int): Limit

    def skip(parameterName: String): Limit
  }

  /**
   * Represents the START clause.
   */
  trait Start extends Match {
    def starts(startExpression: StartExpression*): StartNext

    def starts(startExpression: Iterable[StartExpression]): StartNext
  }

  /**
   * This specifies what can come after a START clause
   */
  trait StartNext extends Match with ForEach with With {
  }

  /**
   * Represents the UNION clause
   */
  trait Union {
    def union: UnionNext
  }

  /**
   * This specifies what can come after a UNION clause
   */
  trait UnionNext {
    def `match`(expression: PathExpression*): Match

    def `match`(expressions: Iterable[PathExpression]): Match

    def all: UnionNext
  }

  /**
   * This collects all the update clauses.
   */
  trait Update extends Create with Set with Delete with Remove with CreateUnique with Merge {
  }

  /**
   * This specifies what can come after an update clause
   */
  trait UpdateNext extends Update with ForEach with With with Return {
  }

  /**
   * Represents the WHERE clause. Several invocations of where() will cause the expressions to be AND'ed together
   */
  trait Where extends With with Update with Return {
    def where(expression: BooleanExpression): Where
  }

  /**
   * Represents the WITH clause
   */
  trait With {
    def `with`(withExpressions: Expression*): WithNext

    def `with`(withExpressions: Iterable[Expression]): WithNext
  }

  /**
   * This specifies what can come after a WITH clause
   */
  trait WithNext extends Start with Create with OrderBy {
  }

}
