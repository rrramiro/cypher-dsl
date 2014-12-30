package org.neo4j.api.libs.cypher


import org.scalatest._

class CypherSpec extends FlatSpec with Matchers {
  it should "init values for a node" in {
    val valuesObj = Cypher.values(
      "property1" -> "text",
      "property2" -> 22,
      "property3" -> 'c',
      "property4" -> 23L,
      "property5" -> 85.6F,
      "property6" -> 25.6D,
      "property7" -> true,
      "property8" -> 5.asInstanceOf[Byte],
      "property9" -> 5.asInstanceOf[Short]
    )
    valuesObj("property1") should be(CyString("text"))
    valuesObj("property2") should be(CyNumber(22))
    valuesObj("property3") should be(CyString("c"))
    valuesObj("property7") should be(CyBoolean(value = true))
    valuesObj("property9") should be(CyNumber(5))
  }

  it should "generate a cypher query" in {
    case class Actor(name: String, movies: Movie*)
    case class Movie(title: String, year: Int)

    implicit val movieWrites = new WritesCyPath[Movie] {
      override def writes(o: Movie): CyPaths = Cypher.node("Movie").values("title" -> o.title, "year" -> o.year).build
    }
    implicit val actorWrites = new WritesCyPath[Actor] {
      override def writes(o: Actor): CyPaths = Cypher.node("Actor").values("name" -> o.name).out(
        "PLAY_IN", o.movies: _*
      )
    }

    val actor = Actor("Johnny Depp", Movie("Pirates of the Caribbean: The Curse of the Black Pearl", 2003), Movie("Pirates of the Caribbean: Dead Man's Chest", 2006))
    val cypher = Cypher.stringify(Cypher.toCypher(actor))
    cypher should be( s"""CYPHER 2.0 CREATE (n1:Actor {name:"Johnny Depp"}),(n2:Movie {title:"Pirates of the Caribbean: The Curse of the Black Pearl",year:2003}),(n3:Movie {title:"Pirates of the Caribbean: Dead Man's Chest",year:2006}),(n1)-[:PLAY_IN]->(n2),(n1)-[:PLAY_IN]->(n3)""")
  }

  it should "generate a cypher query with references" in {
    case class Actor(name: String, movies: Movie*)
    case class Movie(imdbId: Long)

    implicit val movieWrites = WritesCyPath {
      (o: Movie) => Cypher.nodeReference("id", o.imdbId.toString)
    }

    implicit val actorWrites = WritesCyPath {
      (o: Actor) =>
        Cypher.node("Actor").values("name" -> o.name).out(
          "PLAY_IN", o.movies: _*
        )
    }

    val actor = Actor("Johnny Depp", Movie(45664287692L), Movie(892265456642L))
    val cypher = Cypher.stringify(Cypher.toCypher(actor))
    cypher should be( s"""CYPHER 2.0 START n2=node:node_auto_index(id="45664287692"),n3=node:node_auto_index(id="892265456642") CREATE (n1:Actor {name:"Johnny Depp"}),(n1)-[:PLAY_IN]->(n2),(n1)-[:PLAY_IN]->(n3)""")
  }

}
