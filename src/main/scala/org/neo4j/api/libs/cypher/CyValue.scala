package org.neo4j.api.libs.cypher

import java.io.InputStream
import scala.annotation.tailrec
import scala.collection._
import scala.collection.mutable.ListBuffer



/**
 * Generic cypher value
 */
sealed trait CyValue {
  override def toString = Cyon.stringify(this)
}

/**
 * Represents a Cyon null value.
 */
case object CyNull extends CyValue

/**
 * Represent a missing Cyon value.
 */
class CyUndefined(err: => String) extends CyValue {
  def error = err
  override def toString = "CyUndefined(" + err + ")"
}

object CyUndefined {
  def apply(err: => String) = new CyUndefined(err)
  def unapply(o: Object): Boolean = o.isInstanceOf[CyUndefined]
}

/**
 * Represent a Cyon boolean value.
 */
case class CyBoolean(value: Boolean) extends CyValue

/**
 * Represent a Cyon number value.
 */
case class CyNumber(value: BigDecimal) extends CyValue

/**
 * Represent a Cyon string value.
 */
case class CyString(value: String) extends CyValue



/**
 * Represent a Cyon object value.
 */
case class CyValues(value: Map[String, CyValue]) extends CyValue {

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

//  /**
//   * Merge this object with an other one. Values from other override value of the current object.
//   */
//  def ++(other: CyValues): CyValues =
//    CyValues(fields.filterNot(field => other.keys(field._1)) ++ other.fields)

//  /**
//   * removes one field from CyObject
//   */
//  def -(otherField: String): CyValues =
//    CyValues(fields.filterNot(_._1 == otherField))

//  /**
//   * adds one field from CyObject
//   */
//  def +(otherField: (String, CyValue)): CyValues =
//    CyValues(fields :+ otherField)

//  /**
//   * merges everything in depth and doesn't stop at first level as ++
//   * TODO : improve because coding is nasty there
//   */
//  def deepMerge(other: CyValues): CyValues = {
//    def step(fields: Vector[(String, CyValue)], others: Vector[(String, CyValue)]): Seq[(String, CyValue)] = {
//      others match {
//        case Vector() => fields
//        case Vector(sv) =>
//          var found = false
//          val newFields = fields match {
//            case Vector() => Vector(sv)
//            case _ => fields.foldLeft(Vector[(String, CyValue)]()) { (acc, field) =>
//              field match {
//                case (key, obj: CyValues) if (key == sv._1) =>
//                  found = true
//                  acc :+ key -> {
//                    sv._2 match {
//                      case o @ CyValues(_) => obj.deepMerge(o)
//                      case js => js
//                    }
//                  }
//                case (key, value) if (key == sv._1) =>
//                  found = true
//                  acc :+ key -> sv._2
//                case (key, value) => acc :+ key -> value
//              }
//            }
//          }
//
//          if (!found) fields :+ sv
//          else newFields
//
//        case head +: tail =>
//          var found = false
//          val headFields = fields match {
//            case Vector() => Vector(head)
//            case _ => fields.foldLeft(Vector[(String, CyValue)]()) { (acc, field) =>
//              field match {
//                case (key, obj: CyValues) if (key == head._1) =>
//                  found = true
//                  acc :+ key -> {
//                    head._2 match {
//                      case o @ CyValues(_) => obj.deepMerge(o)
//                      case js => js
//                    }
//                  }
//                case (key, value) if (key == head._1) =>
//                  found = true
//                  acc :+ key -> head._2
//                case (key, value) => acc :+ key -> value
//              }
//            }
//          }
//
//          if (!found) step(fields :+ head, tail)
//          else step(headFields, tail)
//
//      }
//    }
//
//    CyValues(step(fields.toVector, other.fields.toVector))
//  }

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
