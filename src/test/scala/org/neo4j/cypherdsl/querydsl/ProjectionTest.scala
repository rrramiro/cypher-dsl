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
package org.neo4j.cypherdsl.querydsl

import java.io.IOException
import java.util.Map

import org.junit.{Before, Ignore, Rule, Test}
import org.neo4j.cypher.javacompat.{ExecutionEngine, ExecutionResult}
import org.neo4j.cypherdsl.CypherQuery.{as, identifier, lookup, node, start}
import org.neo4j.cypherdsl.grammar.Execute
import org.neo4j.cypherdsl.query.neo4j.StartExpressionNeo.nodeById
import org.neo4j.graphdb.{GraphDatabaseService, Node}
import org.neo4j.test._

/**
 * Set up a query using the CypherQuery builder, and then use it to execute a query to a test database and project
 * the results.
 */


class Friend {
  var name: String = null
  var friend: String = null

  override def toString: String = {
    return name + " is friend with " + friend
  }
}


class ProjectionTest extends GraphHolder {
  var data: TestData[Map[String, Node]] = TestData.producedThrough(GraphDescription.createGraphFor(this, true))
  private var graphdbRef: ImpermanentGraphDatabase = null
  private var engine: ExecutionEngine = null

  @Rule
  def getData = data

  @Test
  @Ignore
  @GraphDescription.Graph(value = Array("John friend Sara", "John friend Joe", "Sara friend Maria", "Joe friend Steve"), autoIndexNodes = true)
  @throws(classOf[Exception])
  def testCypherExecution {
    data.get
    val query: String = start(lookup("john", "node_auto_index", "name", "John")).`match`(node("john").out("friend").node.out("friend").node("fof")).returns(identifier("john").property("name"), identifier("fof").property("name"), identifier("john")).toString
    System.out.println(query)
    val result: ExecutionResult = engine.execute(query)
    var john: Node = null
    import scala.collection.JavaConversions._
    for (stringObjectMap <- result) {
      john = (stringObjectMap.get("john").asInstanceOf[Node])
    }
    System.out.println(result.toString)

    {
      val q: Execute = start(nodeById("john", john)).`match`(node("john").out("friend").node.out("friend").node("fof")).returns(identifier("john").property("name"), identifier("fof").property("name"))
      System.out.println(q)
      System.out.println(engine.execute(q.toString).toString)
    }

    {
      val projection: Projection[Friend] = Projection[Friend](classOf[Friend])
      val friends: Iterable[Friend] = projection.iterable(engine.execute(start(nodeById("john", john)).`match`(node("john").out("friend").node.out("friend").node("fof")).returns(as(identifier("john").property("name"), "name"), as(identifier("fof").property("name"), "friend")).toString))
      System.out.println(friends)
    }
  }

  @Before
  @throws(classOf[IOException])
  def setup {
    graphdbRef = new TestGraphDatabaseFactory().newImpermanentDatabase.asInstanceOf[ImpermanentGraphDatabase]
    graphdbRef.cleanContent
    engine = new ExecutionEngine(graphdbRef)
  }

  def graphdb: GraphDatabaseService = {
    return graphdbRef
  }
}
