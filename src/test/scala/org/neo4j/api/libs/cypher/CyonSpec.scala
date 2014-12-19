package org.neo4j.api.libs.cypher


import org.scalatest._

import Cyon._
/**
 * Created by Ramiro Calle on 19/12/2014.
 */
class CyonSpec extends FlatSpec with Matchers {
  it should "init values for a node" in {
    val valuesObj = values(
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
    valuesObj("property1") should be (CyString("text"))
    valuesObj("property2") should be (CyNumber(22))
    valuesObj("property3") should be (CyString("c"))
    valuesObj("property7") should be (CyBoolean(true))
    valuesObj("property9") should be (CyNumber(5))
  }

  it should "init a node with labels" in {
    val newNode: CyNode = node("Label1", "Label2").values("prop1" -> "text")
    //val othernode = node.copy(values =  )
    newNode should be (CyNode(CyValues(List.empty), "Label1", "Label2"))
  }
}
