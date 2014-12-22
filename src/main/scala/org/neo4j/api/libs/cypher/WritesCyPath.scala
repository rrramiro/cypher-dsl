package org.neo4j.api.libs.cypher

import scala.annotation.implicitNotFound


/**
 * Cypher serializer: write an implicit to define a serializer for any type
 */
@implicitNotFound(
  "No Cypher serializer found for type ${A}. Try to implement an implicit Writes or Format for this type."
)
trait WritesCyPath[-A] {

  /**
   * Convert the object into a CyNode
   */
  def writes(o: A): CyPaths

}

@implicitNotFound(
  "No Cypher serializer as CyObject found for type ${A}. Try to implement an implicit OWrites or OFormat for this type."
)
trait OWrites[-A] extends WritesCyPath[A] {

  def writes(o: A): CyPaths

}

object OWrites {

  def apply[A](f: A => CyPaths): OWrites[A] = new OWrites[A] {
    def writes(a: A): CyPaths = f(a)
  }

}

/**
 * Default Serializers.
 */
object WritesCyPath extends DefaultWritesCyNode {


  def apply[A](f: A => CyPaths): WritesCyPath[A] = new WritesCyPath[A] {

    def writes(a: A): CyPaths = f(a)

  }

}

/**
 * Default Serializers.
 */
trait DefaultWritesCyNode {


  //  /**
  //   * Serializer for Map[String,V] types.
  //   */
  //  implicit def mapWrites[V](implicit fmtv: Writes[V]): OWrites[collection.immutable.Map[String, V]] = OWrites[collection.immutable.Map[String, V]] { ts =>
  //    new CyNodes(ts.map { case (k, v) => (k, toCypher(v)(fmtv)) }.toList)
  //  }


  /**
   * Serializer for CyNodes.
   */
  implicit object cyPathsWritesCyPaths extends WritesCyPath[CyPaths] {
    def writes(o: CyPaths) = o
  }

  /**
   * Serializer for Option.
   */
  implicit def OptionWrites[T](implicit fmt: WritesCyPath[T]): WritesCyPath[Option[T]] = new WritesCyPath[Option[T]] {
    def writes(o: Option[T]) = o match {
      case Some(value) => fmt.writes(value)
      case None => CyPaths(CyPathNull, CyRelationships(List.empty))
    }
  }

}