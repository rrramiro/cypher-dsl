package org.neo4j.api.libs.cypher

import org.neo4j.api.libs.cypher.Cyon.CyValueWrapper

object Direction extends Enumeration {
  type Direction = Value
  val IN, OUT, BOTH = Value
}

case class CyPaths(paths: CyPath*){

}

sealed trait CyPath


case class CyNode(cyValues: CyValues, cyLabels: CyLabels) extends CyPath {
  def values(fields: (String, CyValueWrapper)*) = new CyNode(Cyon.values(fields: _*), cyLabels)

  def out(cyLabels: CyLabels, cyValues: CyValues, nodes: CyNode*) = CyPaths(nodes.map{
    node => CyRelationship(cyValues, cyLabels, Direction.OUT, this, node)
  })

  def in(cyLabels: CyLabels, cyValues: CyValues, nodes: CyNode*) = CyPaths(nodes.map{
    node => CyRelationship(cyValues, cyLabels, Direction.IN, node, this)
  })

  def both(cyLabels: CyLabels, cyValues: CyValues, nodes: CyNode*) = CyPaths(nodes.map{
    node => CyRelationship(cyValues, cyLabels, Direction.BOTH, this, node)
  })

}


case class CyRelationship(cyValues: CyValues, cyLabels: CyLabels, direction: Direction.Value, left: CyNode, right: CyNode) extends CyPath
