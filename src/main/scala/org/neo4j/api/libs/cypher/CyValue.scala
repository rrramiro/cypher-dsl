package org.neo4j.api.libs.cypher

import scala.collection._


/**
 * Generic cypher value
 */
sealed trait CyValue{
  type ValueType
  def value: ValueType
}

/**
 * Represents a Cyon null value.
 */
case object CyNull extends CyValue{
  def value = throw new Exception("CyNull")
}

///**
// * Represent a missing Cyon value.
// */
//class CyUndefined(err: => String) extends CyValue {
//  def error = err
//  override def asString = "CyUndefined(" + err + ")"
//}
//
//object CyUndefined {
//  def apply(err: => String) = new CyUndefined(err)
//  def unapply(o: Object): Boolean = o.isInstanceOf[CyUndefined]
//}

/**
 * Represent a Cyon boolean value.
 */
case class CyBoolean(value: Boolean) extends CyValue{
  type ValueType = Boolean
}


/**
 * Represent a Cyon number value.
 */
case class CyNumber(value: BigDecimal) extends CyValue{
  type ValueType = BigDecimal
}

/**
 * Represent a Cyon string value.
 */
case class CyString(value: String) extends CyValue{
  type ValueType = String
}


/**
 * Represent a Cyon object value.
 */
case class CyValues(value: Map[String, CyValue]) {

  def this(fields: Seq[(String, CyValue)]) = {
    this( fields.toMap)
  }

  def apply(key: String) = value(key)

  /**
   * Return all keys
   */
  def keys: Set[String] = value.keySet

  /**
   * Return all values
   */
  def values: Set[CyValue] = value.values.toSet

  def fieldSet: Set[(String, CyValue)] = value.toSet

  override def equals(other: Any): Boolean =
    other match {

      case that: CyValues =>
        (that canEqual this) &&
          fieldSet == that.fieldSet

      case _ => false
    }

  def canEqual(other: Any): Boolean = other.isInstanceOf[CyValues]

  override def hashCode: Int = fieldSet.hashCode()

}
