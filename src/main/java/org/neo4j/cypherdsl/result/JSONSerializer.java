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
package org.neo4j.cypherdsl.result;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;

import java.util.Map;

/**
 * Serializer that converts Cypher execution results to JSON.
 */
public class JSONSerializer
{
    private final ObjectMapper mapper = new ObjectMapper();

    public ArrayNode toJSON( Iterable<Map<String, Object>> result )
    {
        ArrayNode root = mapper.createArrayNode();

        for ( Map<String, Object> stringObjectMap : result )
        {
            ObjectNode entry = root.objectNode();

            for ( Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet() )
            {
                if ( stringObjectEntry.getValue() instanceof Path )
                {
                    entry.put( stringObjectEntry.getKey(), stringObjectEntry.getValue().toString() );
                }
                else if ( stringObjectEntry.getValue() instanceof Node )
                {
                    Node node = (Node) stringObjectEntry.getValue();
                    ObjectNode nodeNode = entry.objectNode();
                    nodeNode.put( "_id", node.getId() );
                    for ( String propertyName : node.getPropertyKeys() )
                    {
                        addProperty( nodeNode, propertyName, node.getProperty( propertyName ) );
                    }
                    entry.put( stringObjectEntry.getKey(), nodeNode );
                }
                else
                {
                    addProperty( entry, stringObjectEntry.getKey(), stringObjectEntry.getValue() );
                }
            }

            root.add( entry );
        }
        return root;
    }

    private void addProperty( ObjectNode node, String name, Object value )
    {
        if ( value instanceof String )
        {
            node.put( name, value.toString() );
        }
        else if ( value instanceof Long )
        {
            Long number = (Long) value;
            node.put( name, number );
        }
        else if ( value instanceof Integer )
        {
            Integer number = (Integer) value;
            node.put( name, number );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown value type:" + value.getClass() );
        }
    }
}
