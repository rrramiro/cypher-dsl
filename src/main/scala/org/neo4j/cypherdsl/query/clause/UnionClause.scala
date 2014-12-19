package org.neo4j.cypherdsl.query.clause

import java.lang.StringBuilder

/**
 * UNION clause
 */
class UnionClause extends Clause {
  private var isAll: Boolean = false

  def asString(builder: StringBuilder) {
    val name: String = " UNION" + (if (isAll) " ALL" else "")
    builder.append(name)
  }

  def all {
    this.isAll = true
  }
}
