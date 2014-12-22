package org.neo4j.api.libs.cypher

import org.neo4j.cypherdsl.query.Direction
import org.neo4j.cypherdsl.{CypherQuery, Identifier => CypherIdentifier}


/**
 * Helper functions to handle CyValues.
 */
object Cyon {

  def node(cyLabels: String*) = new CyNodeBuilder().labels(cyLabels: _*)

  def nodeReference(key: String, id: String) = new CyNodeReferenceBuilder(key, id)

  def labels(cyLabels: String*) = new CyLabels(cyLabels: _*)

  /**
   * Provided a Writes implicit for its type is available, convert any object into a CyValue.
   *
   * @param o Value to convert in Cyon.
   */
  def toCyon[T](o: T)(implicit tjs: WritesCyPath[T]): CyPaths = tjs.writes(o)

  def stringify(cyNode: CyPaths): String = {
    val context = new CypherBuilderContext
    asNode(cyNode.asInstanceOf[CyPaths], context)
    context.asString
  }

  private def asNode(cyPath: CyPaths, context: CypherBuilderContext): CypherIdentifier = {
    val idNode: CypherIdentifier = context.createNodeOrGetId(cyPath.cyNode)
    context.paths ++= cyPath.cyRelationships.relationships.map { rel =>
      CypherQuery.node(idNode).relationship(rel.direction, rel.cyLabels.labels: _*).node(asNode(rel.nodes, context))
    }
    idNode
  }

  sealed trait CyValueWrapper extends NotNull

  private case class CyValueWrapperImpl(field: CyValue) extends CyValueWrapper

  sealed trait CyNodeWrapper extends NotNull

  private case class CyNodeWrapperImpl(field: CyPaths) extends CyNodeWrapper

  import scala.language.implicitConversions

  implicit def toCyFieldCyValueWrapper[T](field: T)(implicit w: WritesCyValue[T]): CyValueWrapper = CyValueWrapperImpl(w.writes(field))

  implicit def toCyFieldCyNodeWrapper[T](field: T)(implicit w: WritesCyPath[T]): CyNodeWrapper = CyNodeWrapperImpl(w.writes(field))

  implicit def toSeqCyFieldCyNodeWrapper[T](fields: Seq[T])(implicit w: WritesCyPath[T]): Seq[CyNodeWrapper] = fields.map { field => CyNodeWrapperImpl(w.writes(field))}

  def values(fields: (String, CyValueWrapper)*): CyValues = new CyValues(fields.map { f =>
    (f._1, f._2.asInstanceOf[CyValueWrapperImpl].field)
  })

  def out(label: String, nodes: CyNodeWrapper*): CyRelationships = {
    CyRelationships(nodes.map { node =>
      CyRelationship(Direction.OUT, labels(label), node.asInstanceOf[CyNodeWrapperImpl].field)
    })
  }

  def in(label: String, nodes: CyNodeWrapper*): CyRelationships = {
    CyRelationships(nodes.map { node =>
      CyRelationship(Direction.IN, labels(label), node.asInstanceOf[CyNodeWrapperImpl].field)
    })
  }

  def both(label: String, nodes: CyNodeWrapper*): CyRelationships = {
    CyRelationships(nodes.map { node =>
      CyRelationship(Direction.BOTH, labels(label), node.asInstanceOf[CyNodeWrapperImpl].field)
    })
  }

}