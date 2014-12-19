
name := "neo4j-cypher-dsl-scala"

version := "2.0.4-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
	"com.mysema.querydsl" % "querydsl-core" % "3.4.2",
    "cglib" % "cglib" % "2.2.2",
    "com.mysema.querydsl" % "querydsl-lucene3" % "3.4.2" exclude("org.apache.lucene", "lucene-core"),
    "com.mysema.querydsl" % "querydsl-apt" % "3.4.2", //provided
    "org.neo4j" % "neo4j-kernel" % "2.0.4",
    "org.neo4j" % "neo4j-lucene-index" % "2.0.4",
    "org.neo4j" % "neo4j-cypher" % "2.0.4",
    "org.codehaus.jackson" % "jackson-core-asl" % "1.9.2",
    "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.2",
    "org.neo4j" % "neo4j-kernel" % "2.0.4" % "test" classifier "tests",
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "joda-time" % "joda-time" % "2.3",
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)