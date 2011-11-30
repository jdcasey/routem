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
package org.commonjava.routem.webctl;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.commonjava.auth.shiro.couch.web.CouchShiroSetupListener;
import org.commonjava.util.logging.Logger;

@WebListener
public class ShiroSetupListener
    extends CouchShiroSetupListener
{

    private final Logger logger = new Logger( getClass() );

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        logger.info( "Initializing CouchDB Shiro authentication/authorization realm..." );
        setAutoCreateAuthorizationInfo( true );
        super.contextInitialized( sce );
        logger.info( "...done." );
    }

}
