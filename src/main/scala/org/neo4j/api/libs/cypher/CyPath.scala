package org.neo4j.api.libs.cypher

import org.neo4j.api.libs.cypher.Cyon.{CyNodeWrapper, CyValueWrapper}


import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import org.neo4j.cypherdsl.query.Direction

case class CyLabels(labels: String*) {

}


sealed trait CyPath {

}


/**
 * Represents a Cyon null value.
 */
case object CyPathNull extends CyPath {

}

trait CyRelationshipBuilder {
  var cyRelationships: ListBuffer[CyRelationship] = new ListBuffer[CyRelationship]

  def out(label: String, nodes: CyNodeWrapper*) = {
    this.cyRelationships ++= Cyon.out(label, nodes: _*).relationships
    this
  }

  def in(label: String, nodes: CyNodeWrapper*) = {
    this.cyRelationships ++= Cyon.in(label, nodes: _*).relationships
    this
  }

  def both(label: String, nodes: CyNodeWrapper*) = {
    this.cyRelationships ++= Cyon.both(label, nodes: _*).relationships
    this
  }

  def build: CyPaths
}

class CyNodeReferenceBuilder(val key: String, val id: String) extends CyRelationshipBuilder{
  def build = CyPaths(CyNodeReference(key, id), CyRelationships(cyRelationships))
}

class CyNodeBuilder extends CyRelationshipBuilder {

  var cyLabels: ListBuffer[String] = new ListBuffer[String]
  var cyValues = mutable.Seq[(String, CyValue)]()


  def labels(labels: String*) = {
    this.cyLabels ++= labels
    this
  }

  def values(fields: (String, CyValueWrapper)*) = {
    this.cyValues ++= Cyon.values(fields: _*).fieldSet
    this
  }

  def build = CyPaths(CyNode(CyLabels(cyLabels), new CyValues(cyValues)), CyRelationships(cyRelationships))
}

case class CyPaths(cyNode: CyPath, cyRelationships: CyRelationships) extends CyPath

case class CyNode(cyLabels: CyLabels, cyValues: CyValues) extends CyPath

case class CyNodeReference(key: String, refId: String, index: String = "node_auto_index") extends CyPath

case class CyRelationships(relationships: Seq[CyRelationship])

case class CyRelationship(direction: Direction.Value, cyLabels: CyLabels, node: CyPath)

