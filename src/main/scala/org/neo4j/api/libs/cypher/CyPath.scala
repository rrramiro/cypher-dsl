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


class CyNodeBuilder  {

  var cyLabels: ListBuffer[String] = new ListBuffer[String]
  var cyValues = mutable.Seq[(String, CyValue)]()
  var cyRelationships: ListBuffer[CyRelationship] = new ListBuffer[CyRelationship]

  def labels(labels: String*) = {
    this.cyLabels ++= labels
    this
  }

  def values(fields: (String, CyValueWrapper)*) = {
    this.cyValues ++= Cyon.values(fields: _*).fieldSet
    this
  }

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

  def build = CyPaths(CyNode(CyLabels(cyLabels: _*), new CyValues(cyValues)), CyRelationships(cyRelationships))

}

case class CyPaths(cyNode: CyNode, cyRelationships: CyRelationships) extends CyPath

case class CyNode(cyLabels: CyLabels, cyValues: CyValues) extends CyPath

case class CyRelationships(relationships: Seq[CyRelationship])

case class CyRelationship(direction: Direction.Value, cyLabels: CyLabels, node: CyPath)
