package org.neo4j.api.libs.cypher

import scala.annotation.implicitNotFound


/**
 * Cyon serializer: write an implicit to define a serializer for any type
 */
@implicitNotFound(
  "No Cyon serializer found for type ${A}. Try to implement an implicit Writes or Format for this type."
)
trait WritesCyPath[-A] {

  /**
   * Convert the object into a CyNode
   */
  def writes(o: A): CyPath

}

@implicitNotFound(
  "No Cyon serializer as CyObject found for type ${A}. Try to implement an implicit OWrites or OFormat for this type."
)
trait OWrites[-A] extends WritesCyPath[A] {

  def writes(o: A): CyPath

}

object OWrites {

  def apply[A](f: A => CyPath): OWrites[A] = new OWrites[A] {
    def writes(a: A): CyPath = f(a)
  }

}

/**
 * Default Serializers.
 */
object WritesCyPath extends DefaultWritesCyNode {


  def apply[A](f: A => CyPath): WritesCyPath[A] = new WritesCyPath[A] {

    def writes(a: A): CyPath = f(a)

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
  //    new CyNodes(ts.map { case (k, v) => (k, toCyon(v)(fmtv)) }.toList)
  //  }


  /**
   * Serializer for CyNodes.
   */
  implicit object cyPathsWritesCyPaths extends WritesCyPath[CyPath] {
    def writes(o: CyPath) = o
  }

  /**
   * Serializer for Option.
   */
  implicit def OptionWrites[T](implicit fmt: WritesCyPath[T]): WritesCyPath[Option[T]] = new WritesCyPath[Option[T]] {
    def writes(o: Option[T]) = o match {
      case Some(value) => fmt.writes(value)
      case None => CyPathNull
    }
  }


}