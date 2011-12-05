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
package org.commonjava.routem.conf.couch;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.commonjava.routem.conf.RouteMConfigConstants.CONFIG_PATH;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.inject.Production;
import org.commonjava.routem.inject.RouteMData;
import org.commonjava.web.config.ConfigurationException;
import org.commonjava.web.config.DefaultConfigurationListener;
import org.commonjava.web.config.dotconf.DotConfConfigurationReader;

public class RouteMConfigFactory
    extends DefaultConfigurationListener
{
    private DefaultCouchDBConfiguration config;

    private DefaultUserManagerConfig userConfig;

    public RouteMConfigFactory()
        throws ConfigurationException
    {
        super( DefaultCouchDBConfiguration.class, DefaultUserManagerConfig.class );
    }

    @PostConstruct
    protected void load()
        throws ConfigurationException
    {
        final File configFile = new File( CONFIG_PATH );
        if ( configFile.isFile() )
        {
            InputStream stream = null;
            try
            {
                stream = new FileInputStream( CONFIG_PATH );
                new DotConfConfigurationReader( this ).loadConfiguration( stream );
            }
            catch ( final IOException e )
            {
                throw new ConfigurationException( "Cannot open configuration file: %s. Reason: %s", e, CONFIG_PATH,
                                                  e.getMessage() );
            }
            finally
            {
                closeQuietly( stream );
            }
        }
        else
        {
            throw new ConfigurationException( "Cannot load configuration. File %s is missing.", CONFIG_PATH );
        }
    }

    @Produces
    @Production
    @Default
    public synchronized UserManagerConfiguration getUserConfiguration()
    {
        if ( userConfig == null )
        {
            userConfig =
                new DefaultUserManagerConfig( "admin@changeme.com", "admin123", "Admin", "User", getConfiguration(),
                                              "routem-users" );
        }

        return userConfig;
    }

    @Produces
    @Production
    @RouteMData
    @Default
    public synchronized CouchDBConfiguration getConfiguration()
    {
        if ( config == null )
        {
            config = new DefaultCouchDBConfiguration( "http://localhost:5984/routem" );
        }

        return config;
    }

    @Override
    public void configurationComplete()
        throws ConfigurationException
    {
        config = getConfiguration( DefaultCouchDBConfiguration.class );
        userConfig = getConfiguration( DefaultUserManagerConfig.class );
    }

}
