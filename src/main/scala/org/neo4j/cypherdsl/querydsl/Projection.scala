/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypherdsl.querydsl

import com.mysema.query.types.{Expression => MysemaExpression}
import com.mysema.query.types.Path
import com.mysema.query.types.Projections
import com.mysema.query.types.QBean
import com.mysema.query.types.path.PathBuilder
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.List
import java.util.Map

/**
 * Projection is responsible for converting the results of a query into an iterable of instances
 * of a given class.
 */
object Projection{
  def apply[T](targetClass: Class[T]) = {
    val entity: PathBuilder[T] = new PathBuilder[T](targetClass, "entity")
    val fields: Array[Field] = targetClass.getFields
    val fieldExpressions: Array[MysemaExpression[_]] = new Array[MysemaExpression[_]](fields.length)

    var i: Int = 0
    while (i < fields.length) {
      fieldExpressions(i) = entity.getString(fields(i).getName)
      i += 1
    }

    new Projection[T]( Projections.fields(targetClass, fieldExpressions: _*))
  }
}

class Projection[T](bean: QBean[T]) {

  def iterable(result: Iterable[Map[String, AnyRef]]): Iterable[T] = {
    val entities: List[T] = new ArrayList[T]
    import scala.collection.JavaConversions._
    for (stringObjectMap <- result) {
      val args: Array[AnyRef] = new Array[AnyRef](stringObjectMap.size)
      var idx: Int = 0
      import scala.collection.JavaConversions._
      for (expression <- bean.getArgs) {
        args(idx) = stringObjectMap.get((expression.asInstanceOf[Path[_]]).getMetadata.getElement.toString)
        idx += 1
      }
      entities.add(bean.newInstance(args))
    }
    return entities
  }
}
