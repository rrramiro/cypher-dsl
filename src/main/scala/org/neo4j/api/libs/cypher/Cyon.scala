package org.neo4j.api.libs.cypher






/**
 * Helper functions to handle CyValues.
 */
object Cyon {

  def node(labels: String*) = CyNode.apply(CyValues.apply(Map.empty), new CyLabels(labels: _*))

  /**
   * Provided a Writes implicit for its type is available, convert any object into a CyValue.
   *
   * @param o Value to convert in Cyon.
   */
  def toCyon[T](o: T)(implicit tjs: Writes[T]): CyValue = tjs.writes(o)

  def stringify(cypher: CyValue): String = ""

  /**
   * Next is the trait that allows Simplified Cyon syntax :
   *
   * Example :
   * {{{
   * CyObject(Seq(
   *    "key1", CyString("value"),
   *    "key2" -> CyNumber(123),
   *    "key3" -> CyObject(Seq("key31" -> CyString("value31")))
   * )) == Cyon.obj( "key1" -> "value", "key2" -> 123, "key3" -> obj("key31" -> "value31"))
   *
   * CyArray(CyString("value"), CyNumber(123), CyBoolean(true)) == Cyon.arr( "value", 123, true )
   * }}}
   *
   * There is an implicit conversion from any Type with a Cyon Writes to CyValueWrapper
   * which is an empty trait that shouldn't end into unexpected implicit conversions.
   *
   * Something to note due to `CyValueWrapper` extending `NotNull` :
   * `null` or `None` will end into compiling error : use CyNull instead.
   */
  sealed trait CyValueWrapper extends NotNull

  private case class CyValueWrapperImpl(field: CyValue) extends CyValueWrapper

  import scala.language.implicitConversions

  implicit def toCyFieldCyValueWrapper[T](field: T)(implicit w: Writes[T]): CyValueWrapper = CyValueWrapperImpl(w.writes(field))

  def values(fields: (String, CyValueWrapper)*): CyValues = new CyValues(fields.map{f =>
    (f._1, f._2.asInstanceOf[CyValueWrapperImpl].field)
  })



}