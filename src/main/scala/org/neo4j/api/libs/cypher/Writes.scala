package org.neo4j.api.libs.cypher

import scala.annotation.implicitNotFound


import Cyon._

/**
 * Cyon serializer: write an implicit to define a serializer for any type
 */
@implicitNotFound(
  "No Cyon serializer found for type ${A}. Try to implement an implicit Writes or Format for this type."
)
trait Writes[-A] {

  /**
   * Convert the object into a CyValue
   */
  def writes(o: A): CyValue

  /**
   * transforms the resulting CyValue using transformer function
   */
  def transform(transformer: CyValue => CyValue): Writes[A] = Writes[A] { a => transformer(this.writes(a)) }

  /**
   * transforms resulting CyValue using Writes[CyValue]
   */
  def transform(transformer: Writes[CyValue]): Writes[A] = Writes[A] { a => transformer.writes(this.writes(a)) }

}

@implicitNotFound(
  "No Cyon serializer as CyObject found for type ${A}. Try to implement an implicit OWrites or OFormat for this type."
)
trait OWrites[-A] extends Writes[A] {

  def writes(o: A): CyValues

}

object OWrites {

  def apply[A](f: A => CyValues): OWrites[A] = new OWrites[A] {
    def writes(a: A): CyValues = f(a)
  }

}

/**
 * Default Serializers.
 */
object Writes extends DefaultWrites {


  /*implicit val contravariantfunctorWrites:ContravariantFunctor[Writes] = new ContravariantFunctor[Writes] {

    def contramap[A,B](wa:Writes[A], f: B => A):Writes[B] = Writes[B]( b => wa.writes(f(b)) )

  }*/

  def apply[A](f: A => CyValue): Writes[A] = new Writes[A] {

    def writes(a: A): CyValue = f(a)

  }

}

/**
 * Default Serializers.
 */
trait DefaultWrites {

  /**
   * Serializer for Int types.
   */
  implicit object IntWrites extends Writes[Int] {
    def writes(o: Int) = CyNumber(o)
  }

  /**
   * Serializer for Short types.
   */
  implicit object ShortWrites extends Writes[Short] {
    def writes(o: Short) = CyNumber(o.toInt)
  }

  /**
   * Serializer for Byte types.
   */
  implicit object ByteWrites extends Writes[Byte] {
    def writes(o: Byte) = CyNumber(o.toInt)
  }

  /**
   * Serializer for Long types.
   */
  implicit object LongWrites extends Writes[Long] {
    def writes(o: Long) = CyNumber(o)
  }

  /**
   * Serializer for Float types.
   */
  implicit object FloatWrites extends Writes[Float] {
    def writes(o: Float) = CyNumber(o.toDouble)
  }

  /**
   * Serializer for Double types.
   */
  implicit object DoubleWrites extends Writes[Double] {
    def writes(o: Double) = CyNumber(o)
  }

  /**
   * Serializer for BigDecimal types.
   */
  implicit object BigDecimalWrites extends Writes[BigDecimal] {
    def writes(o: BigDecimal) = CyNumber(o)
  }

  /**
   * Serializer for Boolean types.
   */
  implicit object BooleanWrites extends Writes[Boolean] {
    def writes(o: Boolean) = CyBoolean(o)
  }

  /**
   * Serializer for String types.
   */
  implicit object StringWrites extends Writes[String] {
    def writes(o: String) = CyString(o)
  }

  /**
   * Serializer for Char types.
   */
  implicit object CharWrites extends Writes[Char] {
    def writes(o: Char) = CyString(o.toString)
  }

  /**
   * Serializer for Map[String,V] types.
   */
  implicit def mapWrites[V](implicit fmtv: Writes[V]): OWrites[collection.immutable.Map[String, V]] = OWrites[collection.immutable.Map[String, V]] { ts =>
    new CyValues(ts.map { case (k, v) => (k, toCyon(v)(fmtv)) }.toList)
  }




  /**
   * Serializer for CyValues.
   */
  implicit object CyValueWrites extends Writes[CyValue] {
    def writes(o: CyValue) = o
  }

  /**
   * Serializer for Option.
   */
  implicit def OptionWrites[T](implicit fmt: Writes[T]): Writes[Option[T]] = new Writes[Option[T]] {
    def writes(o: Option[T]) = o match {
      case Some(value) => fmt.writes(value)
      case None => CyNull
    }
  }

  /**
   * Serializer for java.util.Date
   * @param pattern the pattern used by SimpleDateFormat
   */
  def dateWrites(pattern: String): Writes[java.util.Date] = new Writes[java.util.Date] {
    def writes(d: java.util.Date): CyValue = CyString(new java.text.SimpleDateFormat(pattern).format(d))
  }

  /**
   * Default Serializer java.uti.Date -> CyNumber(d.getTime (nb of ms))
   */
  implicit object DefaultDateWrites extends Writes[java.util.Date] {
    def writes(d: java.util.Date): CyValue = CyNumber(d.getTime)
  }

  /**
   * Serializer for org.joda.time.DateTime
   * @param pattern the pattern used by SimpleDateFormat
   */
  def jodaDateWrites(pattern: String): Writes[org.joda.time.DateTime] = new Writes[org.joda.time.DateTime] {
    val df = org.joda.time.format.DateTimeFormat.forPattern(pattern)
    def writes(d: org.joda.time.DateTime): CyValue = CyString(d.toString(df))
  }

  /**
   * Default Serializer org.joda.time.DateTime -> CyNumber(d.getMillis (nb of ms))
   */
  implicit object DefaultJodaDateWrites extends Writes[org.joda.time.DateTime] {
    def writes(d: org.joda.time.DateTime): CyValue = CyNumber(d.getMillis)
  }

  /**
   * Serializer for org.joda.time.LocalDate
   * @param pattern the pattern used by org.joda.time.format.DateTimeFormat
   */
  def jodaLocalDateWrites(pattern: String): Writes[org.joda.time.LocalDate] = new Writes[org.joda.time.LocalDate] {
    val df = org.joda.time.format.DateTimeFormat.forPattern(pattern)
    def writes(d: org.joda.time.LocalDate): CyValue = CyString(d.toString(df))
  }

  /**
   * Default Serializer org.joda.time.LocalDate -> CyString(ISO8601 format (yyyy-MM-dd))
   */
  implicit object DefaultJodaLocalDateWrites extends Writes[org.joda.time.LocalDate] {
    def writes(d: org.joda.time.LocalDate): CyValue = CyString(d.toString)
  }

  /**
   * Serializer for org.joda.time.LocalTime
   * @param pattern the pattern used by org.joda.time.format.DateTimeFormat
   */
  def jodaLocalTimeWrites(pattern: String): Writes[org.joda.time.LocalTime] = new Writes[org.joda.time.LocalTime] {
    def writes(d: org.joda.time.LocalTime): CyValue = CyString(d.toString(pattern))
  }

  /**
   * Default Serializer org.joda.time.LocalDate -> CyString(ISO8601 format (HH:mm:ss.SSS))
   */
  implicit object DefaultJodaLocalTimeWrites extends Writes[org.joda.time.LocalTime] {
    def writes(d: org.joda.time.LocalTime): CyValue = CyString(d.toString)
  }

  /**
   * Serializer for java.sql.Date
   * @param pattern the pattern used by SimpleDateFormat
   */
  def sqlDateWrites(pattern: String): Writes[java.sql.Date] = new Writes[java.sql.Date] {
    def writes(d: java.sql.Date): CyValue = CyString(new java.text.SimpleDateFormat(pattern).format(d))
  }

  /**
   * Serializer for java.util.UUID
   */
  implicit object UuidWrites extends Writes[java.util.UUID] {
    def writes(u: java.util.UUID) = CyString(u.toString())
  }

  /**
   * Serializer for scala.Enumeration by name.
   */
  implicit def enumNameWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(value: E#Value): CyValue = CyString(value.toString)
  }

}