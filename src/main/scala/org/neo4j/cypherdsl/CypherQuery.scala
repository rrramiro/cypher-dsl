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

import org.neo4j.cypherdsl.expression._
import org.neo4j.cypherdsl.grammar._
import org.neo4j.cypherdsl.query._
import org.neo4j.cypherdsl.query.clause._
import java.util.HashMap
import java.util.Map
import org.neo4j.cypherdsl.query.Query.checkEmpty
import org.neo4j.cypherdsl.query.Query.checkNull
import java.lang.StringBuilder
import scala.collection.JavaConversions.asJavaIterable
import scala.language.implicitConversions

/**
 * DSL for creating Cypher queries. Once created you can serialize to a string,
 * or retrieve the internal Query model for further processing.
 * <p/>
 * It is possible to iteratively construct Cypher queries by calling toQuery()
 * and then use the Query as continuation point. When a new CypherQuery is created
 * by using the newQuery() method the Query is cloned, so that the original Query
 * is not modified. This can be used effectively to create a base query which can then
 * be used many times. Typical examples would be to create a query which is reused many times
 * for paging purposes, and also to provide a base query using START and MATCH which is then
 * expanded using WHERE and RETURN clauses.
 */
object CypherQuery {

  /**
   * Use this constructor if you want to use the instantiation block style
   * of using the DSL.
   * <p/>
   * Example:
   * <pre>
   *     new CypherQuery()
   *     {{
   *         starts(node("n",1)).returns(identifier("n"));
   *     }}.toString()
   * </pre>
   */
  def apply(): CypherQuery = {
    new CypherQuery(new Query())
  }

  //  def apply(q: Query): CypherQuery = {
  //    try {
  //      new CypherQuery(q.clone().asInstanceOf[Query])
  //    }
  //    catch {
  //      case e: CloneNotSupportedException => {
  //        throw new IllegalStateException("Query was not cloneable")
  //      }
  //    }
  //  }

  /**
   * Start building a new Cypher query, starting with a START clause
   *
   * @param startExpressions list of start expressions
   * @return Grammar for Match clause
   */
  def start(startExpressions: StartExpression*): StartNext = {
    val query: CypherQuery = CypherQuery.apply
    return query.starts(startExpressions)
  }

  /**
   * Start building a new Cypher query, starting with a MATCH clause
   *
   * @param paths
   * @return
   */
  def `match`(paths: PathExpression*): StartNext = {
    val query: CypherQuery = CypherQuery.apply
    return query.matches(paths: _*)
  }

  /**
   * Start building a new Cypher query, starting with a CREATE clause
   *
   * @param paths
   * @return
   */
  def create(paths: PathExpression*): UpdateNext = {
    val query: CypherQuery = CypherQuery.apply
    return query.creates(paths: _*)
  }

  /**
   * Start building a new Cypher query, starting with a MERGE clause
   *
   * @param paths
   * @return
   */
  def merge(paths: PathExpression*): UpdateNext = {
    val query: CypherQuery = CypherQuery.apply
    return query.merges(paths: _*)
  }

  /**
   * Continue building on existing Query object
   *
   * @param query a previously created query object
   * @return CypherQuery DSL that can be used to continue building the query
   */
  @throws(classOf[ClassCastException])
  def continueQuery[T](query: Query, asClause: Class[T]): T = {
    try {
      return new CypherQuery(query.clone.asInstanceOf[Query]).continueQuery(asClause)
    }
    catch {
      case e: CloneNotSupportedException => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Declare a Cypher query parameter.
   * This will be replaced with {name}.
   *
   * @param name of the parameter
   * @return Parameter instance
   */
  def param(name: String): Parameter = {
    checkEmpty(name, "Name")
    return new Parameter(name)
  }

  /**
   * Declare a label.
   *
   * @param label literal value
   * @return Label instance
   */
  def label(label: String): LabelValue = {
    return new LabelValue(identifier(label))
  }

  /**
   * Declare a label.
   *
   * @param label literal value
   * @return Label instance
   */
  def label(label: Identifier): LabelValue = {
    return new LabelValue(label)
  }

  /**
   * Declare a literal string value, such as "Foo".
   *
   * @param value literal value
   * @return Literal instance
   */
  def literal(value: String): StringExpression = {
    checkNull(value, "Value")
    return new Literal(value)
  }

  /**
   * Declare a literal numeric value, such 3 or 4.5.
   *
   * @param value literal value
   * @return Literal instance
   */
  def literal(value: Number): NumericExpression = {
    checkNull(value, "Value")
    return new Literal(value)
  }

    /**
     * Declare a literal boolean value, such as true or false.
     *
     * @param value literal value
     * @return Literal instance
     */
    def literal(value: Boolean): BooleanExpression = {
      return new Literal(value)
    }

//  /**
//   * Declare a literal value using an untyped object.
//   * <p/>
//   * If a string is passed in, then output will
//   * be quoted appropriately.
//   *
//   * @param value literal value
//   * @return Literal instance
//   */
//  def literal(value: AnyRef): ScalarExpression = {
//    checkNull(value, "Value")
//    return new Literal(value)
//  }

  /**
   * Declare an identifier. This is used to
   * refer to names declared elsewhere in the query.
   * <p/>
   * If you want to refer to properties, then create
   * this first, and then call e.g. id.property("propname")
   *
   * @param name
   * @return
   */
  def identifier(name: String): Identifier = {
    checkEmpty(name, "Identifier")
    return new Identifier(name)
  }

  /**
   * Declare a collection of expressions. Values may be Expressions or literal values
   * that are converted to Literal expressions by this method.
   * <p/>
   * Corresponds to:
   * <pre>
   * [value1,value2,value3]
   * </pre>
   *
   * @param values
   * @return
   */
  def collection(values: AnyRef*): CollectionExpression = {
    new Value(new ExpressionCollection(new Expressions(values.map {
      case expression: Expression =>
        expression
      case number: Number =>
        literal(number)
      case other =>
        literal(other.toString)
    })))
  }

  /**
   * Declare a list of identifiers.
   *
   * @param values
   * @return
   */
  def identifiers(values: String*) = values.map {
    identifier
  }

  /**
   * Declare a list of parameters.
   *
   * @param names
   * @return
   */
  def parameters(names: String*): Array[Parameter] = names.map {
    param
  }.toArray

  /**
   * Declare a list of literals using longs. This can be handy
   * for the nodesById method.
   *
   * @param values
   * @return
   */
  def literals(values: Long*): Array[NumericExpression] = values.map { value =>
    literal(value)
  }.toArray

  /**
   * Declare a value, which can be used for setting or matching
   * properties in the CREATE or CREATE UNIQUE clauses.
   *
   * @param id
   * @param valueParam
   * @return
   */
  def value(id: String, valueParam: Number): PropertyValue = {
    return new PropertyValue(identifier(id), literal(valueParam))
  }

  /**
   * Declare a value, which can be used for setting or matching
   * properties in the CREATE or CREATE UNIQUE clauses.
   *
   * @param id
   * @param valueParam
   * @return
   */
  def value(id: String, valueParam: String): PropertyValue = {
    return new PropertyValue(identifier(id), literal(valueParam))
  }

  /**
   * Declare a value, which can be used for setting or matching
   * properties in the CREATE or CREATE UNIQUE clauses.
   *
   * @param id
   * @param valueParam
   * @return
   */
  def value(id: String, valueParam: Boolean): PropertyValue = {
    return new PropertyValue(identifier(id), literal(valueParam.toString))
  }

  /**
   * Declare a value, which can be used for setting or matching
   * properties in the CREATE or CREATE UNIQUE clauses.
   *
   * @param id
   * @param value
   * @return
   */
  def value(id: String, value: Expression): PropertyValue = {
    return new PropertyValue(identifier(id), value)
  }

  /**
   * Declare a value, which can be used for setting or matching
   * properties in the CREATE or CREATE UNIQUE clauses.
   *
   * @param id
   * @param value
   * @return
   */
  def value(id: Identifier, value: Expression): PropertyValue = {
    return new PropertyValue(id, value)
  }

  /**
   * "and" a series of expressions together.
   *
   * @param expressions
   * @return
   */
  def and(expressions: BooleanExpression*): BooleanExpression = {
    Query.checkNull(expressions, "Expressions")
    new And(expressions: _*)
  }

  /**
   * "or" a series of expressions together.
   *
   * @param expressions
   * @return
   */
  def or(expressions: BooleanExpression*): BooleanExpression = {
    Query.checkNull(expressions, "Expressions")
    new Or(expressions: _*)
  }

  /**
   * Invert the boolean value of a predicate.
   * <p/>
   * Corresponds to:
   * <pre>
   * not(expression)
   * </pre>
   *
   * @param expression
   * @return
   */
  def not(expression: BooleanExpression): BooleanExpression = {
    Query.checkNull(expression, "Expression")
    return new Value(new FunctionExpression("not", expression))
  }

  /**
   * Corresponds to:
   * <pre>
   * has(property)
   * </pre>
   *
   * @param property
   * @return
   */
  def has(property: Property): BooleanExpression = {
    return new Value(new FunctionExpression("has", property))
  }

  /**
   * Corresponds to:
   * <pre>
   * has(expression)
   * </pre>
   *
   * @param expression
   * @return
   */
  def has(expression: Expression): BooleanExpression = {
    return new Value(new FunctionExpression("has", expression))
  }

  /**
   * Corresponds to:
   * <pre>
   * expression is null
   * </pre>
   *
   * @param expression
   * @return
   */
  def isNull(expression: Expression): BooleanExpression = {
    return new Value(new SuffixFunctionExpression(" is null", expression))
  }

  /**
   * Corresponds to:
   * <pre>
   * expression is not null
   * </pre>
   *
   * @param expression
   * @return
   */
  def isNotNull(expression: Expression): BooleanExpression = {
    return new Value(new SuffixFunctionExpression(" is not null", expression))
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node(id1,id2,id3)
   * </pre>
   *
   * @param name
   * @param ids
   * @return
   */
  def nodesById(name: String, ids: Long*): StartExpression.StartNodes = nodesById(identifier(name), ids: _*)

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node(id1,id2,id3)
   * </pre>
   *
   * @param name
   * @param ids
   * @return
   */
  @throws[IllegalArgumentException]
  def nodesById(name: Identifier, ids: Long*): StartExpression.StartNodes = {
    checkNull(name, "Name")
    checkNotNegativeId(ids: _*)
    new StartExpression.StartNodes(name, literals(ids: _*): _*)
  }

  @throws[IllegalArgumentException]
  def checkNotNegativeId(ids: Long*): Unit = {
    for (id <- ids if id < 0) throw new IllegalArgumentException("Id may not be below zero")
  }

  @deprecated def nodeByParameter(name: String, parameter: String): StartExpression.StartNodes = {
    return nodesByParameter(name, parameter)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node({parameter})
   * </pre>
   *
   * @param name
   * @param parameter
   * @return
   */
  def nodesByParameter(name: String, parameter: String): StartExpression.StartNodes = {
    return nodesByParameter(identifier(name), parameter)
  }

  @deprecated def nodeByparameter(name: Identifier, parameter: String): StartExpression.StartNodes = {
    return nodesByParameter(name, parameter)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node({parameter})
   * </pre>
   *
   * @param name
   * @param parameter
   * @return
   */
  def nodesByParameter(name: Identifier, parameter: String): StartExpression.StartNodes = {
    checkEmpty(name, "Name")
    checkEmpty(parameter, "Parameters")
    return new StartExpression.StartNodes(name, parameters(parameter): _*)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node(*)
   * </pre>
   *
   * @param name
   * @return
   */
  def allNodes(name: String): StartExpression.StartNodes = {
    return allNodes(identifier(name))
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node(*)
   * </pre>
   *
   * @param name
   * @return
   */
  def allNodes(name: Identifier): StartExpression.StartNodes = {
    checkNull(name, "Name")
    return new StartExpression.StartNodes(name, new StartExpression.AllNodes)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node:indexName(key="value")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param key
   * @param value
   * @return
   */
  def lookup(name: String, indexName: String, key: String, value: String): StartExpression.StartNodesLookup = {
    return lookup(identifier(name), identifier(indexName), identifier(key), literal(value))
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node:indexName(key="value")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param key
   * @param value
   * @return
   */
  def lookup(name: Identifier, indexName: Identifier, key: ReferenceExpression, value: StringExpression): StartExpression.StartNodesLookup = {
    checkEmpty(name, "Name")
    checkEmpty(indexName, "Index")
    return new StartExpression.StartNodesLookup(name, indexName, key, value)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node:indexName("query")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param queryString
   * @return
   */
  def query(name: String, indexName: String, queryString: String): StartExpression.StartNodesQuery = {
    return query(identifier(name), identifier(indexName), queryString)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node:indexName("query")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param query
   * @return
   */
  def query(name: Identifier, indexName: Identifier, query: String): StartExpression.StartNodesQuery = {
    checkNull(name, "Name")
    checkNull(indexName, "Index")
    checkEmpty(query, "Query")
    return new StartExpression.StartNodesQuery(name, indexName, query)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node:indexName({param}")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param param
   * @return
   */
  def queryByParameter(name: String, indexName: String, param: String): StartExpression.StartNodesQueryParam = {
    return queryByParameter(identifier(name), identifier(indexName), param)
  }

  /**
   * Declare start nodes. Corresponds to:
   * <pre>
   * name=node:indexName({param})
   * </pre>
   *
   * @param name
   * @param indexName
   * @param param
   * @return
   */
  def queryByParameter(name: Identifier, indexName: Identifier, param: String): StartExpression.StartNodesQueryParam = {
    checkNull(name, "Name")
    checkNull(indexName, "Index")
    checkEmpty(param, "Param")
    return new StartExpression.StartNodesQueryParam(name, indexName, param)
  }

  /**
   * Declare start relationships. Corresponds to:
   * <pre>
   * name=relationship(id1,id2,id3)
   * </pre>
   *
   * @param name
   * @param ids
   * @return
   */
  def relationshipsById(name: String, ids: Long*): StartExpression.StartRelationships = relationshipsById(identifier(name), ids: _*)

  /**
   * Declare start relationships. Corresponds to:
   * <pre>
   * name=relationship(id1,id2,id3)
   * </pre>
   *
   * @param name
   * @param ids
   * @return
   */
  def relationshipsById(name: Identifier, ids: Long*): StartExpression.StartRelationships = {
    checkNull(name, "Name")
    checkNotNegativeId(ids: _*)
    new StartExpression.StartRelationships(name, literals(ids: _*).asInstanceOf[Array[Expression]])
  }

  /**
   * Declare start relationships. Corresponds to:
   * <pre>
   * name=relationship({parameter})
   * </pre>
   *
   * @param name
   * @param parameter
   * @return
   */
  def relationshipsByParameter(name: String, parameter: String): StartExpression.StartRelationshipsParameters = {
    return relationshipsByParameter(identifier(name), parameter)
  }

  /**
   * Declare start relationships. Corresponds to:
   * <pre>
   * name=relationship({parameter})
   * </pre>
   *
   * @param name
   * @param parameter
   * @return
   */
  def relationshipsByParameter(name: Identifier, parameter: String): StartExpression.StartRelationshipsParameters = {
    checkNull(name, "Name")
    checkEmpty(parameter, "Parameter")
    return new StartExpression.StartRelationshipsParameters(name, parameter)
  }

  /**
   * Declare start relationships. Corresponds to:
   * <pre>
   * name=relationship:indexName(key="value")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param key
   * @param value
   * @return
   */
  def relationshipLookup(name: String, indexName: String, key: String, value: String): StartExpression.StartRelationshipsIndex = {
    return relationshipLookup(identifier(name), identifier(indexName), identifier(key), literal(value))
  }

  /**
   * Declare start relationships. Corresponds to:
   * <pre>
   * name=relationship:indexName(key="value")
   * </pre>
   *
   * @param name
   * @param indexName
   * @param key
   * @param value
   * @return
   */
  def relationshipLookup(name: Identifier, indexName: Identifier, key: Identifier, value: StringExpression): StartExpression.StartRelationshipsIndex = {
    checkNull(name, "Name")
    checkNull(indexName, "Index")
    checkNull(key, "Key")
    checkNull(value, "Value")
    return new StartExpression.StartRelationshipsIndex(name, indexName, key, value)
  }

  /**
   * Start declaring a path for CREATE, CREATE UNIQUE, MATCH or WHERE clauses.
   * <p/>
   * Corresponds to:
   * <pre>
   * ()
   * </pre>
   *
   * @return
   */
  def node: Path = {
    return new Path(null, null, null, null)
  }

  /**
   * Start declaring a path for CREATE, CREATE UNIQUE, MATCH or WHERE clauses.
   * <p/>
   * Corresponds to:
   * <pre>
   * (id)
   * </pre>
   *
   * @param id
   * @return
   */
  def node(id: String): Path = {
    return node(identifier(id))
  }

  /**
   * Start declaring a path for CREATE, CREATE UNIQUE, MATCH or WHERE clauses.
   * <p/>
   * Corresponds to:
   * <pre>
   * (expression)
   * </pre>
   *
   * @param expression
   * @return
   */
  def node(expression: Expression): Path = {
    return new Path(expression, null, null, null)
  }

  /**
   * Declare a named path for MATCH clauses
   * <p/>
   * Corresponds to:
   * <pre>
   * name=path
   * </pre>
   *
   * @param name
   * @return
   */
  def path(name: String, pathExp: PathExpression): PathExpression = {
    return path(identifier(name), pathExp)
  }

  /**
   * Declare a named path for MATCH clauses
   * <p/>
   * Corresponds to:
   * <pre>
   * name=path
   * </pre>
   *
   * @param name
   * @return
   */
  def path(name: Identifier, path: PathExpression): PathExpression = {
    checkNull(name, "Name")
    return new NamedPath(name, path)
  }

  /**
   * Use this to declare a shortestPath.
   * <p/>
   * Corresponds to:
   * <pre>
   * shortestPath(path)
   * </pre>
   *
   * @param path
   * @return
   */
  def shortestPath(path: PathExpression): PathExpression = {
    Query.checkNull(path, "Path")
    return new Value(new FunctionExpression("shortestPath", path))
  }

  /**
   * Use this to declare a allShortestPaths
   * <p/>
   * Corresponds to:
   * <pre>
   * allShortestPaths(path)
   * </pre>
   *
   * @param path
   * @return
   */
  def allShortestPaths(path: PathExpression): PathExpression = {
    Query.checkNull(path, "Path")
    return new Value(new FunctionExpression("allShortestPaths", path))
  }

  /**
   * Use this to rename identifiers for RETURN or WITH
   * <p/>
   * Corresponds to:
   * <pre>
   * expression AS name
   * </pre>
   *
   * @param expression
   * @param name
   * @return
   */
  def as(expression: Expression, name: String): Expression = {
    return new Value(new Operator(expression, " AS "), identifier(name))
  }

  /**
   * Use this to rename identifiers for RETURN or WITH
   * <p/>
   * Corresponds to:
   * <pre>
   * expression AS name
   * </pre>
   *
   * @param expression
   * @param name
   * @return
   */
  def as(expression: Expression, name: Identifier): Expression = {
    return new Value(new Operator(expression, " AS "), name)
  }

  /**
   * Use this to declare DISTINCT
   * <p/>
   * Corresponds to:
   * <pre>
   * DISTINCT expression
   * </pre>
   *
   * @param expression
   * @return
   */
  def distinct(expression: Expression): Expression = {
    return new Value(new Operator("DISTINCT "), expression)
  }

  /**
   * Declare a count(*) RETURN expression
   *
   * @return
   */
  def count: NumericExpression = {
    new Value(new FunctionExpression("count", new AbstractExpression() {
      override def asString(builder: StringBuilder): Unit = builder.append('*')
    }))
  }

  /**
   * Declare a count(expression) RETURN expression
   *
   * @return
   */
  def count(expression: Expression): NumericExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("count", expression))
  }

  /**
   * Declare a * RETURN expression
   */
  def all: All = {
    return new All
  }

  /**
   * Declare a sum(expression) RETURN expression
   *
   * @return
   */
  def sum(expression: NumericExpression): NumericExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("sum", expression))
  }

  /**
   * Declare a avg(expression) RETURN expression
   *
   * @return
   */
  def avg(expression: Expression): NumericExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("avg", expression))
  }

  /**
   * Declare a max(expression) RETURN expression
   *
   * @return
   */
  def max(expression: NumericExpression): NumericExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("max", expression))
  }

  /**
   * Declare a min(expression) RETURN expression
   *
   * @return
   */
  def min(expression: NumericExpression): NumericExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("min", expression))
  }

  /**
   * Declare a collect(expression) RETURN expression
   *
   * @return
   */
  def collect(expression: ScalarExpression): CollectionExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("collect", expression))
  }

  /**
   * Declare an ORDER clause expression. Typically used with identifier("n").property("property") as
   * parameter.
   *
   * @param expression
   * @return
   */
  def order(expression: Expression): OrderByExpression = {
    Query.checkNull(expression, "Expression")
    return new OrderByExpression(expression, null)
  }

  /**
   * Declare an ORDER clause expression, with either ASCENDING or DESCENDING order
   * explicitly set. Typically used with identifier("n").property("property") as
   * parameter.
   *
   * @param expression
   * @param order
   * @return
   */
  def order(expression: Expression, order: Order.Value): OrderByExpression = {
    Query.checkNull(expression, "Name")
    Query.checkNull(order, "Order")
    return new OrderByExpression(expression, order)
  }

  /**
   * Use this to create expressions for use with the FOR EACH clause. Use
   * the fluent API of ForEachStatements to create the statements to be evaluated
   * in the FOR EACH clause.
   *
   * @param id
   * @param in
   * @return
   */
  def in(id: String, in: Expression): ForEachStatements = {
    return new ForEachClause(identifier(id), in)
  }

  /**
   * Use this to create expressions for use with the FOR EACH clause. Use
   * the fluent API of ForEachStatements to create the statements to be evaluated
   * in the FOR EACH clause.
   *
   * @param id
   * @param in
   * @return
   */
  def in(id: Identifier, in: Expression): ForEachStatements = {
    return new ForEachClause(id, in)
  }

  /**
   * Use this to set properties in the SET clause.
   * <p/>
   * Corresponds to:
   * <pre>
   * property=value
   * </pre>
   *
   * @param property
   * @param value
   * @return
   */
  def property(property: Property, value: Expression): SetProperty = {
    return new SetProperty(property, value)
  }

  /**
   * Declare an ALL expression. Corresponds to:
   * <pre>
   * ALL(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def all(name: String, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    return all(identifier(name), iterable, predicateExpression)
  }

  /**
   * Declare an ALL expression. Corresponds to:
   * <pre>
   * ALL(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def all(name: Identifier, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    Query.checkNull(name, "Name")
    Query.checkNull(iterable, "Iterable")
    Query.checkNull(predicateExpression, "Predicate")
    return new Value(new IterablePredicateExpression("all", name, iterable, predicateExpression))
  }

  /**
   * Declare an ANY expression. Corresponds to:
   * <pre>
   * ANY(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def any(name: String, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    return any(identifier(name), iterable, predicateExpression)
  }

  /**
   * Declare an ANY expression. Corresponds to:
   * <pre>
   * ANY(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def any(name: Identifier, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    Query.checkNull(name, "Name")
    Query.checkNull(iterable, "Iterable")
    Query.checkNull(predicateExpression, "Predicate")
    return new Value(new IterablePredicateExpression("any", name, iterable, predicateExpression))
  }

  /**
   * Declare a NONE expression. Corresponds to:
   * <pre>
   * NONE(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def none(name: String, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    return none(identifier(name), iterable, predicateExpression)
  }

  /**
   * Declare a NONE expression. Corresponds to:
   * <pre>
   * NONE(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def none(name: Identifier, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    Query.checkNull(name, "Name")
    Query.checkNull(iterable, "Iterable")
    Query.checkNull(predicateExpression, "Predicate")
    return new Value(new IterablePredicateExpression("none", name, iterable, predicateExpression))
  }

  /**
   * Declare a SINGLE expression. Corresponds to:
   * <pre>
   * SINGLE(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def single(name: String, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    return single(identifier(name), iterable, predicateExpression)
  }

  /**
   * Declare a SINGLE expression. Corresponds to:
   * <pre>
   * SINGLE(name IN iterable WHERE expression)
   * </pre>
   *
   * @param name
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def single(name: Identifier, iterable: CollectionExpression, predicateExpression: BooleanExpression): BooleanExpression = {
    Query.checkNull(name, "Name")
    Query.checkNull(iterable, "Iterable")
    Query.checkNull(predicateExpression, "Predicate")
    return new Value(new IterablePredicateExpression("single", name, iterable, predicateExpression))
  }

  /**
   * Declare a length expression. Corresponds to:
   * <pre>
   * length(iterable)
   * </pre>
   *
   * @param expression
   * @return
   */
  def length(expression: CollectionExpression): NumericExpression = {
    checkNull(expression, "Expression")
    return new Value(new FunctionExpression("length", expression))
  }

  /**
   * Declare a type expression. Corresponds to:
   * <pre>
   * type(relationship)
   * </pre>
   *
   * @param relationshipExpression
   * @return
   */
  def `type`(relationshipExpression: RelationshipExpression): StringExpression = {
    checkNull(relationshipExpression, "Expression")
    return new Value(new FunctionExpression("type", relationshipExpression))
  }

  /**
   * Declare an id expression. Corresponds to:
   * <pre>
   * id(name)
   * </pre>
   *
   * @param name
   * @return
   */
  def id(name: String): NumericExpression = {
    checkNull(name, "Name")
    return new Value(new FunctionExpression("id", identifier(name)))
  }

  /**
   * Declare an id expression. Corresponds to:
   * <pre>
   * id(propertyContainer)
   * </pre>
   *
   * @param propertyContainerExpression
   * @return
   */
  def id(propertyContainerExpression: PropertyContainerExpression): NumericExpression = {
    checkNull(propertyContainerExpression, "Expression")
    return new Value(new FunctionExpression("id", propertyContainerExpression))
  }

  /**
   * Declare a coalesce expression. Corresponds to:
   * <pre>
   * coalesce(expression1,expression2,expression3)
   * </pre>
   *
   * @param expressions
   * @return
   */
  def coalesce(expressions: Expression*): Value = {
    if (expressions.length < 1) {
      throw new IllegalArgumentException("At least one expression must be provided to coalesce function")
    }
    return new Value(new FunctionExpression("coalesce", new Expressions(expressions)))
  }

  /**
   * Declare a head expression. Corresponds to:
   * <pre>
   * head(collection)
   * </pre>
   *
   * @param collectionExpression
   * @return
   */
  def head(collectionExpression: CollectionExpression): Expression = {
    checkNull(collectionExpression, "Expression")
    return new Value(new FunctionExpression("head", collectionExpression))
  }

  /**
   * Declare a last expression. Corresponds to:
   * <pre>
   * last(collection)
   * </pre>
   *
   * @param collectionExpression
   * @return
   */
  def last(collectionExpression: CollectionExpression): Expression = {
    checkNull(collectionExpression, "Expression")
    return new Value(new FunctionExpression("last", collectionExpression))
  }

  /**
   * Declare a nodes expression. Corresponds to:
   * <pre>
   * nodes(path)
   * </pre>
   *
   * @param pathExpression
   * @return
   */
  def nodes(pathExpression: PathExpression): CollectionExpression = {
    checkNull(pathExpression, "Expression")
    return new Value(new FunctionExpression("nodes", pathExpression))
  }

  /**
   * Declare a relationships expression. Corresponds to:
   * <pre>
   * relationships(path)
   * </pre>
   *
   * @param pathExpression
   * @return
   */
  def relationships(pathExpression: PathExpression): CollectionExpression = {
    checkNull(pathExpression, "Expression")
    return new Value(new FunctionExpression("relationships", pathExpression))
  }

  /**
   * Declare a labels expression. Corresponds to:
   * <pre>
   * labels(node)
   * </pre>
   *
   * @param nodeExpression
   * @return
   */
  def labels(nodeExpression: NodeExpression): CollectionExpression = {
    checkNull(nodeExpression, "Expression")
    return new Value(new FunctionExpression("labels", nodeExpression))
  }

  /**
   * Declare an extract expression. Corresponds to:
   * <pre>
   * extract(name IN iterable : expression)
   * </pre>
   *
   * @param iterable
   * @param expression
   * @return
   */
  def extract(name: String, iterable: CollectionExpression, expression: ScalarExpression): CollectionExpression = {
    return extract(identifier(name), iterable, expression)
  }

  /**
   * Declare an extract expression. Corresponds to:
   * <pre>
   * extract(name IN iterable : expression)
   * </pre>
   *
   * @param iterable
   * @param expression
   * @return
   */
  def extract(name: Identifier, iterable: CollectionExpression, expression: ScalarExpression): CollectionExpression = {
    Query.checkNull(name, "Name")
    Query.checkNull(iterable, "Iterable")
    Query.checkNull(expression, "Expression")
    return new Value(new Extract(name, iterable, expression))
  }

  /**
   * Declare a filter expression. Corresponds to:
   * <pre>
   * filter(name IN iterable : predicate)
   * </pre>
   *
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def filter(name: String, iterable: CollectionExpression, predicateExpression: BooleanExpression): CollectionExpression = {
    return filter(identifier(name), iterable, predicateExpression)
  }

  /**
   * Declare a filter expression. Corresponds to:
   * <pre>
   * filter(name IN iterable : predicate)
   * </pre>
   *
   * @param iterable
   * @param predicateExpression
   * @return
   */
  def filter(name: Identifier, iterable: CollectionExpression, predicateExpression: BooleanExpression): CollectionExpression = {
    Query.checkNull(name, "Name")
    Query.checkNull(iterable, "Iterable")
    Query.checkNull(predicateExpression, "Predicate")
    return new Value(new Filter(name, iterable, predicateExpression))
  }

  /**
   * Declare a tail expression. Corresponds to:
   * <pre>
   * tail(collectionExpression)
   * </pre>
   *
   * @param collectionExpression
   * @return
   */
  def tail(collectionExpression: CollectionExpression): CollectionExpression = {
    checkNull(collectionExpression, "Expression")
    return new Value(new FunctionExpression("tail", collectionExpression))
  }

  /**
   * Declare a range expression. Corresponds to:
   * <pre>
   * range(start,end)
   * </pre>
   *
   * @param start
   * @param end
   * @return
   */
  def range(start: Number, end: Number): CollectionExpression = {
    return range(literal(start), literal(end), null)
  }

  /**
   * Declare a range expression. Corresponds to:
   * <pre>
   * range(start,end,step)
   * </pre>
   *
   * @param start
   * @param end
   * @param step
   * @return
   */
  def range(start: Number, end: Number, step: Number): CollectionExpression = {
    return range(literal(start), literal(end), literal(step))
  }

  /**
   * Declare a range expression. Corresponds to:
   * <pre>
   * range(start,end)
   * </pre>
   *
   * @param start
   * @param end
   * @return
   */
  def range(start: NumericExpression, end: NumericExpression): CollectionExpression = {
    return range(start, end, null)
  }

  /**
   * Declare a range expression. Corresponds to:
   * <pre>
   * range(start,end,step)
   * </pre>
   *
   * @param start
   * @param end
   * @param step
   * @return
   */
  def range(start: NumericExpression, end: NumericExpression, step: NumericExpression): CollectionExpression = {
    if (step == null) {
      return new Value(new FunctionExpression("range", new Expressions(Array[Expression](start, end))))
    }
    else {
      return new Value(new FunctionExpression("range", new Expressions(Array[Expression](start, end, step))))
    }
  }

  /**
   * Declare extra parentheses. This can be useful to ensure that operators are performed in the order you desire.
   * <p/>
   * Corresponds to:
   * <pre>
   * (numericExpression)
   * </pre>
   */
  def p(numericExpression: NumericExpression): NumericExpression = {
    return new Value(new FunctionExpression("", numericExpression))
  }

  /**
   * Declare an abs expression. Corresponds to:
   * <pre>
   * abs(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def abs(numericalExpression: Number): NumericExpression = {
    return abs(literal(numericalExpression))
  }

  /**
   * Declare an abs expression. Corresponds to:
   * <pre>
   * abs(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def abs(numericalExpression: NumericExpression): NumericExpression = {
    return new Value(new FunctionExpression("abs", numericalExpression))
  }

  /**
   * Declare a round function. Corresponds to:
   * <pre>
   * round(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def round(numericalExpression: Number): NumericExpression = {
    return round(literal(numericalExpression))
  }

  /**
   * Declare a round function. Corresponds to:
   * <pre>
   * round(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def round(numericalExpression: NumericExpression): NumericExpression = {
    return new Value(new FunctionExpression("round", numericalExpression))
  }

  /**
   * Declare a sqrt expression. Corresponds to:
   * <pre>
   * sqrt(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def sqrt(numericalExpression: Number): NumericExpression = {
    return sqrt(literal(numericalExpression))
  }

  /**
   * Declare a sqrt expression. Corresponds to:
   * <pre>
   * sqrt(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def sqrt(numericalExpression: NumericExpression): NumericExpression = {
    return new Value(new FunctionExpression("sqrt", numericalExpression))
  }

  /**
   * Declare a sign expression. Corresponds to:
   * <pre>
   * sign(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def sign(numericalExpression: Number): NumericExpression = {
    return sign(literal(numericalExpression))
  }

  /**
   * Declare a sign expression. Corresponds to:
   * <pre>
   * sign(expression)
   * </pre>
   *
   * @param numericalExpression
   * @return
   */
  def sign(numericalExpression: NumericExpression): NumericExpression = {
    return new Value(new FunctionExpression("sign", numericalExpression))
  }

  sealed trait BooleanExpressionOperator

  class And(expressions: BooleanExpression*) extends Value(new Expressions(expressions.asInstanceOf[Array[Expression]])) with BooleanExpressionOperator {

    override def asString(builder: StringBuilder): Unit = {
      builder.append(this.value.asInstanceOf[Array[BooleanExpression]].map {
        case expression: BooleanExpressionOperator =>
          val builder2 = new StringBuilder()
          builder2.append('(')
          expression.asString(builder2)
          builder2.append(')')
          builder2.toString
        case expression: BooleanExpression =>
          val builder2 = new StringBuilder()
          expression.asString(builder2)
          builder2.toString
      }.mkString(" and "))
    }
  }

  class Or(expressions: BooleanExpression*) extends Value(new Expressions(expressions.asInstanceOf[Array[Expression]])) with BooleanExpressionOperator {

    override def asString(builder: StringBuilder) {
      builder.append(this.value.asInstanceOf[Array[BooleanExpression]].map {
        case expression: And =>
          val builder2 = new StringBuilder()
          builder2.append('(')
          expression.asString(builder2)
          builder2.append(')')
          builder2.toString
        case expression: BooleanExpression =>
          val builder2 = new StringBuilder()
          expression.asString(builder2)
          builder2.toString
      }.mkString(" or "))
    }
  }

}

case class CypherQuery(query: Query) {

  /**
   * START clause. Use this with Java initialization block style.
   *
   * @param startExpressions
   * @return
   */
  protected def starts(startExpressions: StartExpression*): StartNext = {
    query.add(new StartClause(startExpressions))
    return new Grammar
  }

  /**
   * START clause. Use this with Java initialization block style.
   *
   * @param startExpressions
   * @return
   */
  protected def starts(startExpressions: Iterable[StartExpression]): StartNext = {
    query.add(new StartClause(startExpressions))
    return new Grammar
  }

  /**
   * CREATE clause. Use this with Java initialization block style.
   *
   * @param paths
   * @return
   */
  protected def creates(paths: PathExpression*): UpdateNext = {
    query.add(new CreateClause(paths))
    return new Grammar
  }

  /**
   * MERGE clause. Use this with Java initialization block style.
   *
   * @param paths
   * @return
   */
  protected def merges(paths: PathExpression*): UpdateNext = {
    query.add(new MergeClause(paths))
    return new Grammar
  }

  /**
   * MATCH clause. Use this with Java initialization block style.
   *
   * @param paths
   * @return
   */
  protected def matches(paths: PathExpression*): StartNext = {
    query.add(new MatchClause(paths))
    return new Grammar
  }

  @throws(classOf[ClassCastException])
  protected def continueQuery[T](asClause: Class[T]): T = {
    return asClause.cast(new Grammar)
  }

  override def toString: String = {
    return query.toString
  }

  protected class Grammar extends StartNext with With with WithNext with Create with Set with Delete with Remove with CreateUnique with Merge with UpdateNext with Match with ReturnNext with OrderBy with Skip with Limit with Execute with Union with UnionNext {
    import java.lang.Iterable

    def `with`(withExpressions: Expression*): WithNext = {
      query.add(new WithClause(withExpressions))
      return this
    }

    def `with`(withExpressions: Iterable[Expression]): WithNext = {
      query.add(new WithClause(withExpressions))
      return this
    }

    def union: UnionNext = {
      query.add(new UnionClause)
      return this
    }

    def all: UnionNext = {
      val unionClause: UnionClause = query.lastClause(classOf[UnionClause])
      if (unionClause != null) {
        unionClause.all
      }
      return this
    }

    def create(paths: PathExpression*): UpdateNext = {
      query.add(new CreateClause(paths))
      return this
    }

    def create(paths: Iterable[PathExpression]): UpdateNext = {
      query.add(new CreateClause(paths))
      return this
    }

    def set(setProperties: SetProperty*): UpdateNext = {
      query.add(new SetClause(setProperties))
      return this
    }

    def set(setProperties: Iterable[SetProperty]): UpdateNext = {
      query.add(new SetClause(setProperties))
      return this
    }

    def delete(expressions: ReferenceExpression*): UpdateNext = {
      query.add(new DeleteClause(expressions))
      return this
    }

    def delete(expressions: Iterable[ReferenceExpression]): UpdateNext = {
      query.add(new DeleteClause(expressions))
      return this
    }

    def remove(expressions: ReferenceExpression*): UpdateNext = {
      query.add(new RemoveClause(expressions))
      return this
    }

    def remove(expressions: Iterable[ReferenceExpression]): UpdateNext = {
      query.add(new RemoveClause(expressions))
      return this
    }

    def createUnique(expressions: PathExpression*): UpdateNext = {
      query.add(new CreateUniqueClause(expressions))
      return this
    }

    def createUnique(expressions: Iterable[PathExpression]): UpdateNext = {
      query.add(new CreateUniqueClause(expressions))
      return this
    }

    def merge(expressions: PathExpression*): UpdateNext = {
      query.add(new MergeClause(expressions))
      return this
    }

    def merge(expressions: Iterable[PathExpression]): UpdateNext = {
      query.add(new MergeClause(expressions))
      return this
    }

    def forEach(statement: ForEachStatement): UpdateNext = {
      query.add(statement.getClause)
      return this
    }

    def starts(startExpression: StartExpression*): StartNext = {
      query.add(new StartClause(startExpression))
      return this
    }

    def starts(startExpression: Iterable[StartExpression]): StartNext = {
      query.add(new StartClause(startExpression))
      return this
    }

    def `match`(expressions: PathExpression*): Match = {
      query.add(new MatchClause(expressions))
      return this
    }

    def `match`(expressions: Iterable[PathExpression]): Match = {
      query.add(new MatchClause(expressions))
      return this
    }

    def optional: Match = {
      val matchClause: MatchClause = query.lastClause(classOf[MatchClause])
      if (matchClause != null) {
        matchClause.optional
      }
      return this
    }

    def where(expression: BooleanExpression): Where = {
      Query.checkNull(expression, "Expression")
      query.add(new WhereClause(expression))
      return this
    }

    def returns(returnExpressions: Expression*): ReturnNext = {
      query.add(new ReturnClause(returnExpressions))
      return this
    }

    def returns(returnExpressions: Iterable[Expression]): ReturnNext = {
      query.add(new ReturnClause(returnExpressions))
      return this
    }

    def orderBy(orderByExpressions: Expression*): OrderBy = {
      query.add(new OrderByClause(orderByExpressions))
      return this
    }

    def orderBy(orderByExpressions: Iterable[Expression]): OrderBy = {
      query.add(new OrderByClause(orderByExpressions))
      return this
    }

    def skip(skip: Int): Limit = {
      if (skip < 0) {
        throw new IllegalArgumentException("Skip may not be below zero")
      }
      query.add(new SkipClause(skip))
      return this
    }

    def skip(skip: String): Limit = {
      query.add(new SkipParameterClause(skip))
      return this
    }

    def limit(limit: Int): Execute = {
      if (limit < 0) {
        throw new IllegalArgumentException("Limit may not be below zero")
      }
      query.add(new LimitClause(limit))
      return this
    }

    def limit(limit: String): Execute = {
      query.add(new LimitParameterClause(limit))
      return this
    }

    def asString(builder: StringBuilder) {
      query.asString(builder)
    }

    def toQuery: Query = {
      return query
    }

    def parameter(name: String, value: AnyRef): ExecuteWithParameters = {
      return new ExecuteWithParams(query).parameter(name, value)
    }

    def parameters(parameters: Map[String, AnyRef]): ExecuteWithParameters = {
      return new ExecuteWithParams(query).parameters(parameters)
    }

    override def toString: String = {
      return CypherQuery.this.toString
    }
  }

  protected class ExecuteWithParams(query: Query) extends ExecuteWithParameters {
    private final val parameters: Map[String, AnyRef] = new HashMap[String, AnyRef]

    def toQuery: Query = {
      return query
    }

    def getParameters: Map[String, AnyRef] = {
      return parameters
    }

    def parameter(name: String, value: AnyRef): ExecuteWithParameters = {
      this.parameters.put(name, value)
      return this
    }

    def parameters(parameters: Map[String, AnyRef]): ExecuteWithParameters = {
      this.parameters.putAll(parameters)
      return this
    }

    def asString(builder: StringBuilder) {
      query.asString(builder)
    }

    override def toString: String = {
      return query.toString
    }
  }

}
