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
import java.io.IOException
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.neo4j.cypher.EntityNotFoundException
import org.neo4j.cypher.MissingIndexException
import org.neo4j.cypher.ExecutionEngine
import org.neo4j.graphdb.NotFoundException
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.factory.GraphDatabaseSettings
import org.neo4j.test.ImpermanentGraphDatabase
import org.neo4j.test.TestGraphDatabaseFactory

object AbstractCypherTest {
  val CYPHER: String = "CYPHER " + "2.0" + " "
  private var graphdb: ImpermanentGraphDatabase = null
  protected var engine: ExecutionEngine = null

  @BeforeClass
  @throws(classOf[IOException])
  def classSetup {
    graphdb = new TestGraphDatabaseFactory().newImpermanentDatabase.asInstanceOf[ImpermanentGraphDatabase]
    graphdb.cleanContent
    engine = new ExecutionEngine(graphdb)
  }

  @AfterClass def teardown {
    graphdb.shutdown
  }
}

abstract class AbstractCypherTest {
  private var tx: Transaction = null

  @Before
  @throws(classOf[Exception])
  def setUp {
    tx = AbstractCypherTest.graphdb.beginTx
  }

  @After def cleanContent {
    if (tx != null) {
      tx.failure
      tx.finish
      tx = null
    }
    AbstractCypherTest.graphdb.cleanContent
  }

  protected def assertQueryEquals(expected: String, query: String, params: Map[String, Any] = Map.empty) {
    assertEquals(expected, query)
//    try {
//      AbstractCypherTest.engine.execute(query) //params
//    }
//    catch {
//      case mie: MissingIndexException => {
//      }
//      case enfe: EntityNotFoundException => {
//      }
//      case nfe: NotFoundException => {
//      }
//    }
  }

  def engine = AbstractCypherTest.engine
}