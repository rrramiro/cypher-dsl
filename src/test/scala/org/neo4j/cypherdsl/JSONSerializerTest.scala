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

import org.neo4j.cypherdsl.CypherQuery.as
import org.neo4j.cypherdsl.CypherQuery.count
import org.neo4j.cypherdsl.CypherQuery.identifier
import org.neo4j.cypherdsl.CypherQuery.lookup
import org.neo4j.cypherdsl.CypherQuery.node
import org.neo4j.cypherdsl.CypherQuery.path
import org.neo4j.cypherdsl.CypherQuery.shortestPath
import org.neo4j.cypherdsl.CypherQuery.start
import java.io.IOException
import java.util.Map
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.neo4j.cypher.javacompat.ExecutionEngine
import org.neo4j.cypherdsl.result.JSONSerializer
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Transaction
import org.neo4j.test._
import scala.collection.JavaConversions.asJavaIterable
import scala.language.implicitConversions

/**
 * Test of JSON serialization of results.
 */
class JSONSerializerTest extends GraphHolder {
  @Rule var data: TestData[Map[String, Node]] = TestData.producedThrough(GraphDescription.createGraphFor(this, true))
  private var graphdbRef: ImpermanentGraphDatabase = null
  private var engine: ExecutionEngine = null

  @Test
  @GraphDescription.Graph(value = Array("John friend Sara", "John friend Joe", "Sara friend Maria", "Joe friend Steve"), autoIndexNodes = true) def testJSONSerialization {
    data.get
    val serializer: JSONSerializer = new JSONSerializer
    val query: String = start(lookup("john", "node_auto_index", "name", "John")).`match`(node("john").out("friend").node.out("friend").node("fof")).returns(as(identifier("john").property("name"), "name"), as(identifier("fof").property("name"), "friend"), identifier("john"), as(count, "count")).toString
    try {
      val tx: Transaction = graphdbRef.beginTx
      try {
        val json: String = serializer.toJSON(engine.execute(query)).toString
        System.out.println(json)
        tx.success
      } finally {
        if (tx != null) tx.close()
      }
    }
  }

  @Test
  @GraphDescription.Graph(value = Array("John friend Sara", "John friend Joe", "Sara friend Maria", "Joe friend Steve"), autoIndexNodes = true) def testIterableJSONSerialization {
    data.get
    val serializer: JSONSerializer = new JSONSerializer
    val query: String = start(lookup("john", "node_auto_index", "name", "John"), lookup("maria", "node_auto_index", "name", "Maria")).`match`(path("p", shortestPath(node("john").out.hops(null, 3).node("maria")))).returns(identifier("p")).toString
    System.out.println(query)
    try {
      val tx: Transaction = graphdbRef.beginTx
      try {
        val json: String = serializer.toJSON(engine.execute(query)).toString
        System.out.println(json)
        tx.success
      } finally {
        if (tx != null) tx.close()
      }
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
