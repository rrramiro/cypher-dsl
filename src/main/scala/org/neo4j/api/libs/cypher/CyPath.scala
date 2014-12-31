package org.neo4j.api.libs.cypher

import org.neo4j.api.libs.cypher.Cypher.{CyNodeWrapper, CyValueWrapper}
import org.neo4j.cypherdsl.query.Direction

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class CyLabels(labels: String*) {

}


sealed trait CyPath {

}


/**
 * Represents a Cypher null value.
 */
case object CyPathNull extends CyPath {

}

trait CyRelationshipBuilder {
  var cyRelationships: ListBuffer[CyRelationship] = new ListBuffer[CyRelationship]

  def out(label: String, nodes: CyNodeWrapper*) = {
    this.cyRelationships ++= Cypher.out(label, nodes: _*).relationships
    this
  }

  def in(label: String, nodes: CyNodeWrapper*) = {
    this.cyRelationships ++= Cypher.in(label, nodes: _*).relationships
    this
  }

  def both(label: String, nodes: CyNodeWrapper*) = {
    this.cyRelationships ++= Cypher.both(label, nodes: _*).relationships
    this
  }

  def build: CyPaths
}

class CyNodeReferenceBuilder(val key: String, val id: String) extends CyRelationshipBuilder {
  def build = CyPathsImpl(CyNodeReference(key, id), CyRelationships(cyRelationships))
}

class CyNodeBuilder extends CyRelationshipBuilder {

  var cyLabels: ListBuffer[String] = new ListBuffer[String]
  var cyValues = mutable.Seq[(String, CyValue)]()


  def labels(labels: String*) = {
    this.cyLabels ++= labels
    this
  }

  def values(fields: (String, CyValueWrapper)*) = {
    this.cyValues ++= Cypher.values(fields: _*).fieldSet
    this
  }

  def build = CyPathsImpl(CyNode(CyLabels(cyLabels: _*), new CyValues(cyValues)), CyRelationships(cyRelationships))
}

sealed trait CyPaths {
  val cyNode: CyPath
  val cyRelationships: CyRelationships
}

case class CyPathsImpl(cyNode: CyPath, cyRelationships: CyRelationships) extends CyPaths

case object CyPathsNull extends CyPaths {
  val cyNode = CyPathNull
  val cyRelationships = CyRelationships(List.empty)
}

case class CyNode(cyLabels: CyLabels, cyValues: CyValues) extends CyPath

case class CyNodeReference(key: String, refId: String, index: String = "node_auto_index") extends CyPath

case class CyRelationships(relationships: Seq[CyRelationship])

case class CyRelationship(direction: Direction.Value, cyLabels: CyLabels, nodes: CyPaths)

