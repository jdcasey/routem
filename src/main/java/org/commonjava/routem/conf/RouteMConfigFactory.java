package org.commonjava.routem.conf;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.routem.inject.RouteMData;
import org.commonjava.web.config.ConfigurationException;
import org.commonjava.web.config.DefaultConfigurationListener;
import org.commonjava.web.config.dotconf.DotConfConfigurationReader;

public class RouteMConfigFactory
    extends DefaultConfigurationListener
{
    private static final String CONFIG_PATH = "/etc/aprox/security.conf";

    private DefaultCouchDBConfiguration config;

    public RouteMConfigFactory()
        throws ConfigurationException
    {
        super( DefaultCouchDBConfiguration.class );
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
    @RouteMData
    @Default
    public CouchDBConfiguration getConfiguration()
    {
        return config;
    }

    @Override
    public void configurationComplete()
        throws ConfigurationException
    {
        config = getConfiguration( DefaultCouchDBConfiguration.class );
    }

}
