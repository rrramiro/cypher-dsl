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

import org.junit.Assert.assertEquals
import org.neo4j.cypherdsl.CypherQuery.allNodes
import org.neo4j.cypherdsl.CypherQuery.as
import org.neo4j.cypherdsl.CypherQuery.identifier
import org.neo4j.cypherdsl.CypherQuery.identifiers
import org.neo4j.cypherdsl.CypherQuery.literal
import org.neo4j.cypherdsl.CypherQuery.lookup
import org.neo4j.cypherdsl.CypherQuery.nodesById
import org.neo4j.cypherdsl.CypherQuery.nodesByParameter
import org.neo4j.cypherdsl.CypherQuery.not
import org.neo4j.cypherdsl.CypherQuery.param
import org.neo4j.cypherdsl.CypherQuery.query
import org.neo4j.cypherdsl.CypherQuery.queryByParameter
import org.neo4j.cypherdsl.CypherQuery.relationshipLookup
import org.neo4j.cypherdsl.CypherQuery.relationshipsById
import org.neo4j.cypherdsl.CypherQuery.relationshipsByParameter
import org.neo4j.cypherdsl.CypherQuery.start
import org.junit.Assert
import org.junit.Test
import java.lang.StringBuilder
import AbstractCypherTest.CYPHER

/**
 * Tests for all parts of the Cypher DSL.
 */
class CypherQueryTest extends AbstractCypherTest {

  @Test def testStartNodes {
    assertQueryEquals(CYPHER + "START john=node(0) RETURN john", start(nodesById("john", 0)).returns(identifier("john")).toString)
    assertQueryEquals(CYPHER + "START family=node(0,1) RETURN family", start(nodesById("family", 0, 1)).returns(identifier("family")).toString)
    assertQueryEquals(CYPHER + "START john=node({name}) RETURN john", start(nodesByParameter("john", "name")).returns(identifier("john")).toString)
    assertQueryEquals(CYPHER + "START family=node(*) RETURN family", start(allNodes("family")).returns(identifier("family")).toString)
    assertQueryEquals(CYPHER + "START john=node:nodes(name=\"John\") RETURN john", start(lookup("john", "nodes", "name", "John")).returns(identifier("john")).toString)
    assertQueryEquals(CYPHER + "START john=node:nodes(\"name:John\") RETURN john", start(query("john", "nodes", "name:John")).returns(identifier("john")).toString)
    assertQueryEquals(CYPHER + "START john=node:nodes('name:\"John doe\"') RETURN john", start(query("john", "nodes", "name:\"John doe\"")).returns(identifier("john")).toString)
    assertEquals(CYPHER + "START john=node:nodes({param}) RETURN john", start(queryByParameter("john", "nodes", "param")).returns(identifier("john")).toString)
    try {
      start(nodesById("", 0))
      Assert.fail("Expected exception")
    }
    catch {
      case e: Exception => {
      }
    }
    try {
      start(nodesById("john", -1))
      Assert.fail("Expected exception")
    }
    catch {
      case e: Exception => {
      }
    }
    try {
      start(lookup("john", "foo", null, "bar"))
      Assert.fail("Expected exception")
    }
    catch {
      case e: Exception => {
      }
    }
  }

  @Test def testStartRelationships {
    assertQueryEquals(CYPHER + "START knows=relationship(0) RETURN knows", start(relationshipsById("knows", 0)).returns(identifier("knows")).toString)
    assertQueryEquals(CYPHER + "START likes=relationship(0,1) RETURN likes", start(relationshipsById("likes", 0, 1)).returns(identifier("likes")).toString)
    assertQueryEquals(CYPHER + "START knows=relationship({name}) RETURN knows", start(relationshipsByParameter("knows", "name")).returns(identifier("knows")).toString)
    assertQueryEquals(CYPHER + "START likes=relationship({websitea}) RETURN likes", start(relationshipsByParameter("likes", "websitea")).returns(identifier("likes")).toString)
    assertQueryEquals(CYPHER + "START knows=relationship:relationships(type=\"Starred\") RETURN knows", start(relationshipLookup("knows", "relationships", "type", "Starred")).returns(identifier("knows")).toString)
    try {
      start(relationshipsById("", 0))
      Assert.fail("Expected exception")
    }
    catch {
      case e: Exception => {
      }
    }
    try {
      start(relationshipsById("john", -1))
      Assert.fail("Expected exception")
    }
    catch {
      case e: Exception => {
      }
    }
    try {
      start(relationshipLookup("likes", "websitea", null, "websiteb"))
      Assert.fail("Expected exception")
    }
    catch {
      case e: Exception => {
      }
    }
  }

  @Test def testWhere {
    assertQueryEquals(CYPHER + "START n=node(0) WHERE n.name={name} RETURN n", start(nodesById("n", 0)).where(identifier("n").string("name").eq(param("name"))).returns(identifier("n")).toString)
    assertQueryEquals(CYPHER + "START n=node(0) WHERE (n.age>30 and n.name=\"Tobias\") or not(n.name=\"Tobias\")" + " RETURN n", start(nodesById("n", 0)).where(identifier("n").number("age").gt(30).and(identifier("n").string("name").eq("Tobias")).or(not(identifier("n").string("name").eq("Tobias")))).returns(identifier("n")).toString)
  }

  @Test def testReturn {
    assertQueryEquals(CYPHER + "START john=node(0) RETURN john", start(nodesById("john", 0)).returns(identifier("john")).toString)
    assertQueryEquals(CYPHER + "START mom=node(0),dad=node(1) RETURN mom,dad", start(nodesById("mom", 0), nodesById("dad", 1)).returns(identifiers("mom", "dad")).toString)
    assertQueryEquals(CYPHER + "START mom=node(0),dad=node(1) RETURN mom.age AS momsAge,dad.age AS dadsAge", start(nodesById("mom", 0), nodesById("dad", 1)).returns(as(identifier("mom").property("age"), "momsAge"), as(identifier("dad").property("age"), "dadsAge")).toString)
  }

  @Test def testLiteral {
    val builder: StringBuilder = new StringBuilder
    literal("x\\x\"x").asString(builder)
    assertEquals("\"x\\\\x\\\"x\"", builder.toString)
  }
}
