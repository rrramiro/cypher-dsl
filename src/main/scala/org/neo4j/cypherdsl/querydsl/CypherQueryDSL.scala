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

import javax.annotation.Nullable

import com.mysema.query.lucene.LuceneSerializer
import com.mysema.query.types.{Constant, Expression => MysemaExpression, FactoryExpression, Operation, Ops, ParamExpression, Path, Predicate, SubQueryExpression, TemplateExpression, Visitor}
import org.neo4j.cypherdsl.{CypherQuery, Identifier, Property}
import org.neo4j.cypherdsl.expression.{BooleanExpression, Expression, NumericExpression, StringExpression}
import org.neo4j.cypherdsl.query.Value

/**
 * Methods here are used to integrate Cypher DSL with the QueryDSL library. Create QBeans with QueryDSL
 * and use those as parameters for these methods, which can then be used with the regular Cypher DSL methods.
 */
object CypherQueryDSL {
  private val luceneSerializer: LuceneSerializer = new LuceneSerializer(true, true)

  def identifier(entityPath: Path[_]): Identifier = {
    return CypherQuery.identifier(entityPath.getMetadata.getElement.toString)
  }

  def property(entityPath: Path[_]): Property = {
    return CypherQuery.identifier(entityPath.getRoot.toString).property(entityPath.getMetadata.getElement.toString)
  }

  def string(entityPath: Path[_]): StringExpression = {
    return CypherQuery.identifier(entityPath.getRoot.toString).string(entityPath.getMetadata.getElement.toString)
  }

  def number(entityPath: Path[_]): NumericExpression = {
    return CypherQuery.identifier(entityPath.getRoot.toString).number(entityPath.getMetadata.getElement.toString)
  }

  def toQuery(query: Predicate): String = {
    return luceneSerializer.toQuery(query, null).toString
  }

  def toBooleanExpression(predicate: Predicate): BooleanExpression = {
    return predicate.accept(new Visitor[BooleanExpression, BooleanExpression] {
      def visit(constant: Constant[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        return null
      }

      def visit(factoryExpression: FactoryExpression[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        return null
      }

      def visit(operation: Operation[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        val id: String = operation.getOperator.getId
        if (id == Ops.AND.getId) {
          return CypherQuery.and(operation.getArg(0).accept(this, null), operation.getArg(1).accept(this, null))
        }
        else if (id == Ops.OR.getId) {
          return CypherQuery.or(operation.getArg(0).accept(this, null), operation.getArg(1).accept(this, null))
        }
        else if (id == Ops.NOT.getId) {
          return CypherQuery.not(operation.getArg(0).accept(this, null))
        }
        else if (id == Ops.EQ.getId) {
          return arg(operation.getArg(0)).eq(arg(operation.getArg(1)).asInstanceOf[StringExpression])
        }
        else if (id == Ops.NE.getId) {
          return arg(operation.getArg(0)).ne(arg(operation.getArg(1)).asInstanceOf[StringExpression])
        }
        else if (id == Ops.GT.getId) {
          return arg(operation.getArg(0)).gt(arg(operation.getArg(1)).asInstanceOf[StringExpression])
        }
        else if (id == Ops.LT.getId) {
          return arg(operation.getArg(0)).lt(arg(operation.getArg(1)).asInstanceOf[StringExpression])
        }
        else if (id == Ops.GOE.getId) {
          return arg(operation.getArg(0)).gte(arg(operation.getArg(1)).asInstanceOf[StringExpression])
        }
        else if (id == Ops.LOE.getId) {
          return arg(operation.getArg(0)).lte(arg(operation.getArg(1)).asInstanceOf[StringExpression])
        }
        else if (id == Ops.EXISTS.getId) {
          return CypherQuery.has(arg(operation.getArg(0)).asInstanceOf[Expression])
        }
        else if (id == Ops.IS_NULL.getId) {
          return CypherQuery.isNull(arg(operation.getArg(0)).asInstanceOf[Expression])
        }
        else if (id == Ops.IS_NOT_NULL.getId) {
          return CypherQuery.isNotNull(arg(operation.getArg(0)).asInstanceOf[Expression])
        }
        else if (id == Ops.LIKE.getId) {
          return arg(operation.getArg(0)).regexp(arg(operation.getArg(1)))
        }
        else {
          throw new IllegalArgumentException("Unknown operator:" + id + " in expression " + operation)
        }
      }

      def visit(paramExpression: ParamExpression[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        return null
      }

      def visit(path: Path[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        return null
      }

      def visit(subQueryExpression: SubQueryExpression[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        return null
      }

      def visit(templateExpression: TemplateExpression[_], @Nullable booleanExpression: BooleanExpression): BooleanExpression = {
        return null
      }

      def arg(expression: MysemaExpression[_]): Value = {
        if (expression.isInstanceOf[Constant[_]]) {
          return new Value(CypherQuery.literal((expression.asInstanceOf[Constant[_]]).getConstant.toString))
        }
        else if (expression.isInstanceOf[ParamExpression[_]]) {
          return new Value(CypherQuery.param((expression.asInstanceOf[ParamExpression[_]]).getName))
        }
        else if (expression.isInstanceOf[Path[_]]) {
          val path: Path[_] = expression.asInstanceOf[Path[_]]
          return new Value(identifier(path.getRoot).string(path.getMetadata.getElement.toString))
        }
        else {
          throw new IllegalArgumentException("Unknown argument type:" + expression)
        }
      }
    }, null)
  }
}

