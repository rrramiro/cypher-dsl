package org.neo4j.api.libs

package object cypher {
  implicit def convertCypherBuilderToCypherPaths(builder:CyRelationshipBuilder): CyPaths = builder.build
}
