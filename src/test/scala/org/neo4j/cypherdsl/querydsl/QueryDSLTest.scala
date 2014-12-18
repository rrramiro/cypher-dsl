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

import com.mysema.query.alias.Alias._
import com.mysema.query.support.Expressions.constant
import com.mysema.query.support.Expressions.predicate
import org.junit.Assert.assertEquals
import org.neo4j.cypherdsl.CypherQuery.nodesById
import org.neo4j.cypherdsl.Order.DESCENDING
import org.neo4j.cypherdsl.query.Query
import org.neo4j.cypherdsl.CypherQuery.count
import org.neo4j.cypherdsl.CypherQuery.identifier
import org.neo4j.cypherdsl.CypherQuery.literal
import org.neo4j.cypherdsl.CypherQuery.lookup
import org.neo4j.cypherdsl.CypherQuery.node
import org.neo4j.cypherdsl.CypherQuery.order
import org.neo4j.cypherdsl.CypherQuery.query
import org.neo4j.cypherdsl.CypherQuery.start
import org.neo4j.cypherdsl.querydsl.CypherQueryDSL.string
import org.neo4j.cypherdsl.querydsl.CypherQueryDSL.toBooleanExpression
import org.neo4j.cypherdsl.querydsl.CypherQueryDSL.toQuery
import com.mysema.query.BooleanBuilder
import com.mysema.query.support.Expressions
import com.mysema.query.types.Ops
import com.mysema.query.types.Path
import com.mysema.query.types.expr.BooleanOperation
import com.mysema.query.types.expr.Param
import org.junit.Assert
import org.junit.Test
import org.neo4j.cypherdsl.{CypherQuery, Order}
import org.neo4j.cypherdsl.AbstractCypherTest.CYPHER
/**
 * Test for integration with QueryDSL
 */
class QueryDSLTest {
//  @Test def testQueryDSL {
//    {
//      val person: Path[Person] = Expressions.path(classOf[Person], "n")
//      val personFirstName: Path[String] = Expressions.path(classOf[String], person, "firstName")
//      val personAge: Path[Integer] = Expressions.path(classOf[Integer], person, "age")
//      val expr: BooleanBuilder = new BooleanBuilder(predicate(Ops.EQ, personFirstName, constant("P"))).and(predicate(Ops.GT, personAge, constant(25)))
//      Assert.assertEquals(CYPHER + "START n=node(1,2,3) WHERE n.firstName=\"P\" and n.age>25 RETURN n", start(nodesById(identifier(person), 1, 2, 3)).where(toBooleanExpression(expr)).returns(identifier(person)).toString)
//    }
//    {
//      val person: Person = alias(classOf[Person], "n")
//      Assert.assertEquals(CYPHER + "START n=node(1,2,3) WHERE n.firstName=\"P\" and n.age>25 RETURN n", start(nodesById(person.toString, 1, 2, 3)).where(toBooleanExpression($(person.getFirstName).eq("P").and($(person.getAge).gt(25)))).returns(identifier(person.toString)).toString)
//    }
//    {
//      val person: Nothing = QPerson.person
//      Assert.assertEquals(CYPHER + "START person=node(1,2,3) WHERE person.firstName=\"P\" and person.age>25 " + "RETURN person", start(nodesById(identifier(person), 1, 2, 3)).where(toBooleanExpression(person.firstName.eq("P").and(person.age.gt(25)))).returns(identifier(person)).toString)
//    }
//    {
//      val person: Nothing = QPerson.person
//      Assert.assertEquals(CYPHER + "START person=node:node_auto_index(\"firstName:rickard\") RETURN person" + ".firstName ORDER BY person.firstName DESCENDING", start(query(identifier(person), identifier("node_auto_index"), toQuery(person.firstName.eq("Rickard")))).returns(string(person.firstName)).orderBy(order(string(person.firstName), Order.DESCENDING)).toString)
//    }
//    {
//      Assert.assertEquals(CYPHER + "START person=node:node_auto_index(\"firstName:rickard\") RETURN person" + ".firstName ORDER BY person.firstName DESCENDING", new CypherQuery(new Query()) {
//        QPerson person = QPerson.person;
//        starts( query( identifier( person ), identifier( "node_auto_index" ),
//          toQuery( person.firstName.eq( "Rickard" ) ) ) )
//          .returns( string( person.firstName ) )
//          .orderBy( order( string( person.firstName ), Order.DESCENDING ) )
//    }.toString)
//    }
//    {
//      val n: Nothing = new Nothing("n")
//      Assert.assertEquals(CYPHER + "START n=node(1,2,3) WHERE n.firstName=\"P\" and n.age>25 RETURN n", start(nodesById(identifier(n), 1, 2, 3)).where(toBooleanExpression(n.firstName.eq("P").and(n.age.gt(25)))).returns(identifier(n)).toString)
//    }
//    {
//      val n: Nothing = new Nothing("n")
//      Assert.assertEquals(CYPHER + "START n=node(1,2,3) WHERE n.firstName={name} and n.age>{age} RETURN n", start(nodesById(identifier(n), 1, 2, 3)).where(toBooleanExpression(n.firstName.eq(new Param[String](classOf[String], "name")).and(n.age.gt(new Param[Integer](classOf[Integer], "age"))))).returns(identifier(n)).toString)
//    }
//    {
//      val n: Nothing = new Nothing("n")
//      Assert.assertEquals(CYPHER + "START n=node(1,2,3) WHERE n.firstName=~\"(?i).*rick.*\" RETURN n", start(nodesById(identifier(n), 1, 2, 3)).where(toBooleanExpression(n.firstName.like("(?i).*rick.*"))).returns(identifier(n)).toString)
//    }
//    {
//      val n: Nothing = new Nothing("n")
//      Assert.assertEquals(CYPHER + "START n=node(1,2,3) WHERE has(n.firstName) RETURN n", start(nodesById(identifier(n), 1, 2, 3)).where(toBooleanExpression(BooleanOperation.create(Ops.EXISTS, n.firstName))).returns(identifier(n)).toString)
//    }
//  }
//
//  @Test def testCookbookExample {
//    val stuff: Nothing = QStuff.stuff
//    val place: Nothing = QPlace.place
//    val person: Nothing = QPerson.person
//    assertEquals(CYPHER + "START place=node:node_auto_index(name=\"CoffeShop1\") MATCH (place)<-[:favorite]-" + "(person)-[:favorite]->(stuff) RETURN stuff.name,count(*) ORDER BY count(*) DESCENDING,stuff.name", start(lookup(identifier(place), identifier("node_auto_index"), identifier(place.name), literal("CoffeShop1"))).`match`(node(identifier(place)).in("favorite").node(identifier(person)).out("favorite").node(identifier(stuff))).returns(string(stuff.name), count).orderBy(order(count, DESCENDING), string(stuff.name)).toString)
//    assertEquals(CYPHER + "START place=node:node_auto_index(name=\"CoffeShop1\") MATCH (place)<-[:favorite]-" + "(person)-[:favorite]->(stuff) RETURN stuff.name,count(*) ORDER BY count(*) DESCENDING,stuff.name", new CypherQuery(new Query()) {
//      QStuff stuff = QStuff.stuff;
//      QPlace place = QPlace.place;
//      QPerson person = QPerson.person;
//
//      starts( lookup( identifier( place ), identifier( "node_auto_index" ),
//        identifier( place.name ), literal( "CoffeShop1" ) ) ).
//      `match`( node( identifier( place ) ).in( "favorite" ).node( identifier( person ) ).out( "favorite" ).node( identifier( stuff ) ) ).
//      returns( string( stuff.name ), count() ).
//        orderBy( order( count(), DESCENDING ), string( stuff.name ) );
//    }.toString)
//  }
}
