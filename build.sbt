//import org.apache.commons.io.FileUtils
//import collection.JavaConversions._

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
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "org.scala-lang" % "scala-reflect" % "2.11.4"
)

//sourceGenerators in Compile <+= (
//  javaSource in Test,
//  sourceManaged in Compile,
//  classDirectory in Compile,
//  dependencyClasspath in Compile,
//  compilers in Compile, streams) map {
//    (sourceDir, generatedSourceDir, classDir, classpath, compilers, streams) => {
//      val commonJavacOptions = Seq("-source", "1.7", "-target", "1.7", "-encoding", "utf-8", "-Xlint:unchecked")
//      FileUtils.forceMkdir(classDir)
//      FileUtils.forceMkdir(generatedSourceDir)
//      FileUtils.cleanDirectory(generatedSourceDir)
//      compilers.javac.apply(
//        sources = FileUtils.listFiles(sourceDir, Array("java"), true).toSeq,
//        classpath = classpath.map(_.data),
//        outputDirectory = classDir,
//        options = commonJavacOptions ++ Seq(
//            "-proc:only", "-AfullyAnnotationConfigured=true", "-processor", "com.mysema.query.apt.QuerydslAnnotationProcessor",
//            "-s", generatedSourceDir.getCanonicalPath
//        )
//      )(streams.log)
//      FileUtils.listFiles(sourceDir, Array("java"), true).toSeq
//    }
//}