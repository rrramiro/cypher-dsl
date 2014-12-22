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
package org.neo4j.cypherdsl.query.clause

import java.io.Serializable
import java.lang.StringBuilder

import org.neo4j.cypherdsl.AsString

/**
 * Base class for all clauses
 */
abstract class Clause extends AsString with Serializable with Cloneable {
  protected def clauseAsString(builder: StringBuilder, name: String, asStringList: List[_ <: AsString], separator: String) {
    if (!asStringList.isEmpty) {
      if (builder.length > 0) {
        builder.append(' ')
      }
      builder.append(name).append(' ')

      var i: Int = 0
      while (i < asStringList.size) {
        val asString: AsString = asStringList(i)
        if (i > 0) {
          builder.append(separator)
        }
        asString.asString(builder)
        i += 1
      }
    }
  }
}
