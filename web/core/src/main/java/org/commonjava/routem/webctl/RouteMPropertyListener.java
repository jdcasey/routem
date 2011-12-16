package org.commonjava.routem.webctl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.commonjava.util.logging.Logger;

@WebListener
public class RouteMPropertyListener
    implements ServletContextListener
{

    private final Logger logger = new Logger( getClass() );

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        final ServletContext context = sce.getServletContext();
        final Enumeration<String> names = context.getInitParameterNames();

        final Properties props = System.getProperties();
        final Properties changes = new Properties();
        boolean changed = false;
        while ( names.hasMoreElements() )
        {
            final String name = names.nextElement();
            if ( name.startsWith( "routem." ) )
            {
                final String value = context.getInitParameter( name );
                props.setProperty( name, value );
                changes.setProperty( name, value );
                changed = true;
            }
        }

        if ( changed )
        {
            logger.info( "Setting system properties from servlet context:\n\n%s", new Object()
            {
                @Override
                public String toString()
                {
                    final StringWriter sw = new StringWriter();
                    changes.list( new PrintWriter( sw ) );
                    return sw.toString();
                }
            } );

            System.setProperties( props );
        }
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
    }

}
