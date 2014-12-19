package org.neo4j.api.libs.cypher

import org.neo4j.api.libs.cypher.Cyon.CyValueWrapper

/**
 * Created by Ramiro Calle on 19/12/2014.
 */
case class CyNode(values: CyValues, labels: String*){
  def values(fields: (String, CyValueWrapper)*) = new CyNode(Cyon.values(fields: _*), labels: _*)
}

