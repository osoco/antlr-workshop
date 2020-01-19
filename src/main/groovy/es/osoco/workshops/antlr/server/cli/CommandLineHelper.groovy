package es.osoco.workshops.antlr.server.cli

import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.server.cli.CLIOptions
import groovy.transform.CompileStatic
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options

import java.text.ParseException

@CompileStatic
class CommandLineHelper implements CLIOptions {
    /**
     * Builds the command-line options to build a {@link org.apache.commons.cli.CommandLineParser parser}.
     * @return the {@link Options}.
     */
    static Options buildOptions() {
        final Options result = new Options()

        result.with {
            addOption(PORT_OPTION, 'port', true, 'the server port')
        }

        result
    }

    /**
     * Retrieves the value of port option.
     * @param cmd the command-line.
     * @return such value.
     */
    static int getPortValue(final CommandLine cmd) {
        Integer result

        String providedPort = cmd.getOptionValue(PORT_OPTION)
        if (!providedPort) {
            providedPort = System.getProperty(PORT_SYSTEM_PROPERTY)
        }
        if (!providedPort) {
            providedPort = System.getenv(PORT_ENV_VARIABLE)
        }
        if (providedPort) {
            try {
                result = Integer.parseInt(providedPort)
            } catch (final ParseException invalidPort) {
                LoggingFactory.instance.createLogging().warn("Invalid port: ${providedPort}", invalidPort)
                result = null
            }
        }

        if (result) {
            LoggingFactory.instance.createLogging().debug("Using port: ${result}")
        } else {
            result = DEFAULT_PORT
            LoggingFactory.instance.createLogging().debug("Using default port: ${result}")
        }

        result
    }
}

