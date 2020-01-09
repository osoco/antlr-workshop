package es.osoco.workshops.antlr.server

import es.osoco.workshops.antlr.cli.helpers.CommandLineChecker
import es.osoco.workshops.antlr.cli.CommandLineHelper
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import groovy.transform.CompileStatic

/**
 * Runs the server from the command line.
 */
@CompileStatic
class ServerCLI
    implements CLIOptions {

    /**
     * Builds the command-line options to build a {@link CommandLineParser parser}.
     * @return the {@link Options}.
     */
    protected static Options buildOptions() {
        final Options result = CommandLineHelper.buildOptions()

        result.with {
            addOption(PORT_OPTION, 'port', true, 'the server port')
        }

        result
    }

    /**
     * Runs the Proof of Concept from the command line.
     * @param args the command-line arguments.
     */
    @SuppressWarnings('JavaIoPackageAccess')
    static void main(final String[] args) {

        final CommandLineParser parser = new DefaultParser()

        final Options options = buildOptions()

        final Logging logging = LoggingFactory.instance.createLogging()

        try {
            final CommandLine cmd = parser.parse(options, args)

            final String port = cmd.getOptionValue(PORT_OPTION)

        } catch (ParseException invalidInvocation) {

            logging.error('Invalid arguments.')
            logging.error(invalidInvocation.message)
            final HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp('server', options)
            invalidInvocation.printStackTrace(System.err)
        }
    }
}
