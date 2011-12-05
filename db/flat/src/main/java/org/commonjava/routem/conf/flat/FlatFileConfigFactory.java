package org.commonjava.routem.conf.flat;

import java.io.File;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class FlatFileConfigFactory
{

    private static final String DEFAULT_DATA_FILE = System.getProperty( "user.home", ".m2/routem/data.json" );

    private static final String DATA_FILE_SYSPROP = "routem.flat.data";

    public static final String DATA_FILE_ALIAS = DATA_FILE_SYSPROP;

    @Produces
    @Named( FlatFileConfigFactory.DATA_FILE_ALIAS )
    public File getConfigFile()
    {
        final String path = System.getProperty( DATA_FILE_SYSPROP, DEFAULT_DATA_FILE );
        return new File( path );
    }

}
