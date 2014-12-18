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

package org.neo4j.cypherdsl.query.clause;

import org.neo4j.cypherdsl.AsString;

import java.io.Serializable;
import java.util.List;

/**
 * Base class for all clauses
 */
public abstract class Clause
        implements AsString, Serializable, Cloneable
{
    protected void clauseAsString( StringBuilder builder, String name, List<? extends AsString> asStringList,
                                   String separator )
    {
        if ( !asStringList.isEmpty() )
        {
            if ( builder.length() > 0 )
            {
                builder.append( ' ' );
            }
            builder.append( name ).append( ' ' );

            for ( int i = 0; i < asStringList.size(); i++ )
            {
                AsString asString = asStringList.get( i );
                if ( i > 0 )
                {
                    builder.append( separator );
                }
                asString.asString( builder );
            }
        }
    }
}
