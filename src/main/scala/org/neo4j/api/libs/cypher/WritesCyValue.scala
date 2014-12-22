package org.neo4j.api.libs.cypher

import scala.annotation.implicitNotFound

/**
 * Cypher serializer: write an implicit to define a serializer for any type
 */
@implicitNotFound(
  "No Cypher serializer found for type ${A}. Try to implement an implicit Writes or Format for this type."
)
trait WritesCyValue[-A] {

  /**
   * Convert the object into a CyValue
   */
  def writes(o: A): CyValue

  /**
   * transforms the resulting CyValue using transformer function
   */
  def transform(transformer: CyValue => CyValue): WritesCyValue[A] = WritesCyValue[A] { a => transformer(this.writes(a))}

  /**
   * transforms resulting CyValue using Writes[CyValue]
   */
  def transform(transformer: WritesCyValue[CyValue]): WritesCyValue[A] = WritesCyValue[A] { a => transformer.writes(this.writes(a))}

}


/**
 * Default Serializers.
 */
object WritesCyValue extends DefaultWritesCyValue {

  def apply[A](f: A => CyValue): WritesCyValue[A] = new WritesCyValue[A] {

    def writes(a: A): CyValue = f(a)

  }

}

/**
 * Default Serializers.
 */
trait DefaultWritesCyValue {

  /**
   * Serializer for Int types.
   */
  implicit object IntWritesCyValue$ extends WritesCyValue[Int] {
    def writes(o: Int) = CyNumber(o)
  }

  /**
   * Serializer for Short types.
   */
  implicit object ShortWritesCyValue$ extends WritesCyValue[Short] {
    def writes(o: Short) = CyNumber(o.toInt)
  }

  /**
   * Serializer for Byte types.
   */
  implicit object ByteWritesCyValue$ extends WritesCyValue[Byte] {
    def writes(o: Byte) = CyNumber(o.toInt)
  }

  /**
   * Serializer for Long types.
   */
  implicit object LongWritesCyValue$ extends WritesCyValue[Long] {
    def writes(o: Long) = CyNumber(o)
  }

  /**
   * Serializer for Float types.
   */
  implicit object FloatWritesCyValue$ extends WritesCyValue[Float] {
    def writes(o: Float) = CyNumber(o.toDouble)
  }

  /**
   * Serializer for Double types.
   */
  implicit object DoubleWritesCyValue$ extends WritesCyValue[Double] {
    def writes(o: Double) = CyNumber(o)
  }

  /**
   * Serializer for BigDecimal types.
   */
  implicit object BigDecimalWritesCyValue$ extends WritesCyValue[BigDecimal] {
    def writes(o: BigDecimal) = CyNumber(o)
  }

  /**
   * Serializer for Boolean types.
   */
  implicit object BooleanWritesCyValue$ extends WritesCyValue[Boolean] {
    def writes(o: Boolean) = CyBoolean(o)
  }

  /**
   * Serializer for String types.
   */
  implicit object StringWritesCyValue$ extends WritesCyValue[String] {
    def writes(o: String) = CyString(o)
  }

  /**
   * Serializer for Char types.
   */
  implicit object CharWritesCyValue$ extends WritesCyValue[Char] {
    def writes(o: Char) = CyString(o.toString)
  }

  /**
   * Serializer for CyValues.
   */
  implicit object CyValueWritesCyValue$ extends WritesCyValue[CyValue] {
    def writes(o: CyValue) = o
  }

  /**
   * Serializer for Option.
   */
  implicit def OptionWrites[T](implicit fmt: WritesCyValue[T]): WritesCyValue[Option[T]] = new WritesCyValue[Option[T]] {
    def writes(o: Option[T]) = o match {
      case Some(value) => fmt.writes(value)
      case None => CyNull
    }
  }

  /**
   * Default Serializer java.uti.Date -> CyNumber(d.getTime (nb of ms))
   */
  implicit object DefaultDateWritesCyValue$ extends WritesCyValue[java.util.Date] {
    def writes(d: java.util.Date): CyValue = CyNumber(d.getTime)
  }

  /**
   * Serializer for scala.Enumeration by name.
   */
  implicit def enumNameWrites[E <: Enumeration]: WritesCyValue[E#Value] = new WritesCyValue[E#Value] {
    def writes(value: E#Value): CyValue = CyString(value.toString)
  }

}