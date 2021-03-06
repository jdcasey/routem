package org.commonjava.routem.data.flat;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.commonjava.routem.conf.flat.FlatFileConfigFactory;
import org.commonjava.routem.data.DefaultRouteDataManager;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.routem.model.RouteMReplicationData;
import org.commonjava.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Singleton
@Named( "flat" )
@Alternative
@Specializes
public class FlatRouteDataManager
    extends DefaultRouteDataManager
    implements RouteDataManager
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    @Named( FlatFileConfigFactory.DATA_FILE_ALIAS )
    private File configFile;

    private boolean syncEnabled = true;

    FlatRouteDataManager()
    {
    }

    public FlatRouteDataManager( final File configFile )
        throws RouteMDataException
    {
        this.configFile = configFile;
        load();
    }

    @PostConstruct
    public synchronized void load()
        throws RouteMDataException
    {
        if ( configFile == null || !configFile.isFile() )
        {
            logger.debug( "Cannot load route data from: '%s'. File was not configured, is missing, or is unreadable.",
                          configFile );
            return;
        }

        String json = null;
        try
        {
            json = readFileToString( configFile );
        }
        catch ( final IOException e )
        {
            throw new RouteMDataException( "Failed to read route information from: %s. Reason: %s", e, configFile,
                                           e.getMessage() );
        }

        if ( json != null )
        {
            final Gson gson = new GsonBuilder().setPrettyPrinting()
                                               .create();

            final RouteMReplicationData dto = gson.fromJson( json, RouteMReplicationData.class );

            if ( dto != null )
            {
                syncEnabled = false;

                if ( dto.getGroups() != null )
                {
                    for ( final Group group : dto.getGroups() )
                    {
                        store( group );
                    }
                }

                if ( dto.getMirrors() != null )
                {
                    for ( final MirrorOf mirror : dto.getMirrors() )
                    {
                        store( mirror );
                    }
                }

                syncEnabled = true;
            }
        }
    }

    public synchronized void sync()
        throws RouteMDataException
    {
        if ( !syncEnabled )
        {
            return;
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting()
                                           .create();

        final RouteMReplicationData dto =
            new RouteMReplicationData( getAllGroupDefinitions(), getAllMirrorOfDefinitions() );

        if ( dto.isEmpty() && configFile.exists() )
        {
            configFile.delete();
        }

        final String json = gson.toJson( dto );

        final File dir = configFile.getParentFile();
        if ( dir != null && !dir.exists() && !dir.mkdirs() )
        {
            throw new RouteMDataException( "Cannot create config directory: %s", dir );
        }

        FileWriter out = null;
        try
        {
            out = new FileWriter( configFile );
            out.write( json );
        }
        catch ( final IOException e )
        {
            throw new RouteMDataException( "Failed to write route information to: %s. Reason: %s", e, configFile,
                                           e.getMessage() );
        }
        finally
        {
            closeQuietly( out );
        }
    }

    @Override
    public synchronized boolean store( final MirrorOf mirrorOf )
        throws RouteMDataException
    {
        final boolean result = super.store( mirrorOf );
        sync();
        return result;
    }

    @Override
    public synchronized boolean store( final MirrorOf mirrorOf, final boolean skipIfExists )
        throws RouteMDataException
    {
        final boolean result = super.store( mirrorOf, skipIfExists );
        sync();
        return result;
    }

    @Override
    public synchronized boolean store( final Group group )
        throws RouteMDataException
    {
        final boolean result = super.store( group );
        sync();
        return result;
    }

    @Override
    public synchronized boolean store( final Group group, final boolean skipIfExists )
        throws RouteMDataException
    {
        final boolean result = super.store( group, skipIfExists );
        sync();
        return result;
    }

    @Override
    public synchronized void delete( final MirrorOf mirrorOf )
        throws RouteMDataException
    {
        super.delete( mirrorOf );
        sync();
    }

    @Override
    public synchronized void delete( final Group group )
        throws RouteMDataException
    {
        super.delete( group );
        sync();
    }

    @Override
    public synchronized void deleteGroup( final String groupId )
        throws RouteMDataException
    {
        super.deleteGroup( groupId );
        sync();
    }

    @Override
    public synchronized void deleteMirror( final String canonicalUrl, final String targetUrl )
        throws RouteMDataException
    {
        super.deleteMirror( canonicalUrl, targetUrl );
        sync();
    }

    @Override
    public void deleteMirror( final String mirrorId )
        throws RouteMDataException
    {
        super.deleteMirror( mirrorId );
        sync();
    }

    @Override
    public void install()
        throws RouteMDataException
    {
        super.install();
        sync();
    }

}
