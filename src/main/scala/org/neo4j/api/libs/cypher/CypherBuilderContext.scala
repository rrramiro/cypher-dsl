package org.neo4j.api.libs.cypher

import java.util.concurrent.atomic.AtomicInteger

import org.neo4j.cypherdsl.expression.{StartExpression, PathExpression}
import org.neo4j.cypherdsl.query.PropertyValue
import org.neo4j.cypherdsl.{Identifier, CypherQuery}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by Ramiro Calle on 22/12/2014.
 */
class CypherBuilderContext {
  val counter = new AtomicInteger(0)
  val paths : ListBuffer[PathExpression] = new ListBuffer[PathExpression]
  val refs : ListBuffer[StartExpression] = new ListBuffer[StartExpression]
  val nodeMap = mutable.Map[CyPath, Identifier]()

  private def generateId: Identifier = CypherQuery.identifier(s"n${counter.incrementAndGet()}")

  def createNodeOrGetId(cyPath: CyPath): Identifier = nodeMap.getOrElseUpdate(cyPath, createNodeOrReference(cyPath))

  def createNodeOrReference(cyPath: CyPath): Identifier =  {
    val idNode = generateId
    cyPath match {
      case cyNode: CyNode =>
        paths +=  CypherQuery.node(idNode)
          .labels(cyNode.cyLabels.labels.map { l => CypherQuery.label(l)}: _*)
          .values(cyNode.cyValues.value.map { case (key, prop) => new PropertyValue(CypherQuery.identifier(key), cyValueToLiteral(prop))})
      case cyRef: CyNodeReference =>
        refs += CypherQuery.lookup(idNode, CypherQuery.identifier(cyRef.index), CypherQuery.identifier(cyRef.key), CypherQuery.literal(cyRef.refId))
      case _ =>
        throw new Exception("Unknown type")
    }
    idNode
  }

  private def cyValueToLiteral(cyValue: CyValue) = {
    cyValue.value match {
      case text: String => CypherQuery.literal(text)
      case bool: Boolean => CypherQuery.literal(bool)
      case number: BigDecimal => CypherQuery.literal(number)
      case _ => throw new Exception("unkmown type")
    }
  }

  def asString: String = CypherQuery.start(refs.toList: _*).create(paths.toList: _*).toString

}
