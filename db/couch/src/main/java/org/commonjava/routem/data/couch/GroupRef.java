/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.routem.data.couch;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.routem.model.couch.GroupDoc;

public class GroupRef
    extends CouchDocRef
{

    public GroupRef( final String groupId )
    {
        super( namespaceId( GroupDoc.NAMESPACE, groupId ) );
    }

    public static GroupRef[] generateKeys( final String groupId )
    {
        final String[] parts = groupId.split( "\\." );
        final GroupRef[] refs = new GroupRef[parts.length];

        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for ( final String part : parts )
        {
            if ( sb.length() < 1 )
            {
                refs[i] = new GroupRef( part );
                sb.append( part );
            }
            else
            {
                sb.append( '.' )
                  .append( part );
                refs[i] = new GroupRef( sb.toString() );
            }

            i++;
        }

        return refs;
    }

}
