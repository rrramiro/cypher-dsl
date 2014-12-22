package org.neo4j.api.libs.cypher

import java.util.concurrent.atomic.AtomicInteger

import org.neo4j.cypherdsl.expression.PathExpression
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
  val nodeMap = mutable.Map[CyNode, Identifier]()
  private def generateId: Identifier = CypherQuery.identifier(s"n${counter.incrementAndGet()}")

  def createNodeOrGetId(cyNode: CyNode): Identifier = {
    nodeMap.getOrElseUpdate(cyNode, {
        val idNode = generateId
        paths +=  CypherQuery.node(idNode)
          .labels(cyNode.cyLabels.labels.map { l => CypherQuery.label(l)}: _*)
          .values(cyNode.cyValues.value.map { case (key, prop) => new PropertyValue(CypherQuery.identifier(key), cyValueToLiteral(prop))})
        idNode
      }
    )
  }

  private def cyValueToLiteral(cyValue: CyValue) = {
    cyValue.value match {
      case text: String => CypherQuery.literal(text)
      case bool: Boolean => CypherQuery.literal(bool)
      case number: BigDecimal => CypherQuery.literal(number)
      case _ => throw new Exception("unkmown type")
    }
  }

  def asString: String = CypherQuery.create(paths.toList: _*).toString

}
