package io.gomint.server.logging;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

@Plugin( name = TerminalConsoleAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true )
public class TerminalConsoleAppender extends AbstractAppender {

    // Name used in log4j2.xml
    static final String PLUGIN_NAME = "TerminalConsole";

    private static final String PROPERTY_PREFIX = "terminal";
    private static final String JLINE_OVERRIDE_PROPERTY = PROPERTY_PREFIX + ".jline";

    // Grab early or we will infinite loop with the log4j2 stdout redirection
    private static final PrintStream stdout = System.out;

    private static boolean initialized;
    private static Terminal terminal;
    private static LineReader reader;

    /**
     * Return the build up jLine terminal or null when the init failed
     *
     * @return jLine terminal or null
     */
    public static Terminal getTerminal() {
        return terminal;
    }

    /**
     * Set a new jLine reader. Readers are used to parse stdin
     *
     * @param newReader The new line reader
     */
    public static void setReader( LineReader newReader ) {
        if ( newReader != null && newReader.getTerminal() != terminal ) {
            throw new IllegalArgumentException( "Reader was not created with TerminalConsoleAppender.getTerminal()" );
        }

        reader = newReader;
    }

    private TerminalConsoleAppender( String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions ) {
        super( name, filter, layout, ignoreExceptions );
        initializeTerminal();
    }

    private static void initializeTerminal() {
        if ( !initialized ) {
            initialized = true;

            // Check for override (force enable jline) and IntelliJ which supports colors
            Boolean jlineOverride = getOptionalBooleanProperty( JLINE_OVERRIDE_PROPERTY );
            boolean dumb = jlineOverride == Boolean.TRUE || System.getProperty( "java.class.path" ).contains( "idea_rt.jar" );

            if ( jlineOverride != Boolean.FALSE ) {
                try {
                    terminal = TerminalBuilder.builder().dumb( dumb ).build();
                } catch ( IllegalStateException e ) {
                    LOGGER.warn( "Disabling terminal, you're running in an unsupported environment.", e );
                } catch ( IOException e ) {
                    LOGGER.error( "Failed to initialize terminal. Falling back to standard output", e );
                }
            }
        }
    }

    @Override
    public void append( LogEvent event ) {
        if ( terminal != null ) {
            if ( reader != null ) {
                // Draw the prompt line again if a reader is available
                reader.callWidget( LineReader.CLEAR );
                terminal.writer().print( getLayout().toSerializable( event ) );
                reader.callWidget( LineReader.REDRAW_LINE );
                reader.callWidget( LineReader.REDISPLAY );
            } else {
                terminal.writer().print( getLayout().toSerializable( event ) );
            }

            terminal.writer().flush();
        } else {
            stdout.print( getLayout().toSerializable( event ) );
        }
    }

    public static void close() throws IOException {
        if ( initialized ) {
            initialized = false;
            if ( terminal != null ) {
                try {
                    terminal.close();
                } finally {
                    terminal = null;
                }
            }
        }
    }

    @PluginFactory
    public static TerminalConsoleAppender createAppender(
        @Required( message = "No name provided for TerminalConsoleAppender" ) @PluginAttribute( "name" ) String name,
        @PluginElement( "Filter" ) Filter filter,
        @PluginElement( "Layout" ) @Nullable Layout<? extends Serializable> layout,
        @PluginAttribute( value = "ignoreExceptions", defaultBoolean = true ) boolean ignoreExceptions ) {

        if ( layout == null ) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new TerminalConsoleAppender( name, filter, layout, ignoreExceptions );
    }

    private static Boolean getOptionalBooleanProperty( String name ) {
        String value = PropertiesUtil.getProperties().getStringProperty( name );
        if ( value == null ) {
            return null;
        }

        if ( value.equalsIgnoreCase( "true" ) ) {
            return Boolean.TRUE;
        } else if ( value.equalsIgnoreCase( "false" ) ) {
            return Boolean.FALSE;
        } else {
            LOGGER.warn( "Invalid value for boolean input property '{}': {}", name, value );
            return null;
        }
    }

}
