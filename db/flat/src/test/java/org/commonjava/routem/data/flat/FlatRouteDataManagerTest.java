package org.commonjava.routem.data.flat;

import static org.apache.commons.io.FileUtils.readFileToString;

import java.io.File;

import org.apache.log4j.Level;
import org.commonjava.routem.RouteDataManagerTCK;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.util.logging.Log4jUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class FlatRouteDataManagerTest
    extends RouteDataManagerTCK
{

    @Rule
    public TemporaryFolder tempFile = new TemporaryFolder();

    private RouteDataManager mgr;

    private File configFile;

    @BeforeClass
    public static void initLogging()
    {
        Log4jUtil.configure( Level.DEBUG );
    }

    @Before
    public void init()
        throws Exception
    {
        configFile = tempFile.newFile( "routem-flat.json" );
        mgr = new FlatRouteDataManager( configFile );
    }

    @After
    public void dumpFile()
        throws Exception
    {
        final String json = readFileToString( configFile );

        System.out.println( json );
    }

    @Override
    protected RouteDataManager getDataManager()
        throws Exception
    {
        return mgr;
    }

}
