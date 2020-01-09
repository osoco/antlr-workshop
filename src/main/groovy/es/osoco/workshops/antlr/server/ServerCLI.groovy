package es.osoco.workshops.antlr.server


import es.osoco.workshops.antlr.server.helpers.CommandLineHelper
import groovy.transform.CompileStatic
import lombok.NonNull
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

/**
 * Runs the server from the command line.
 */
@CompileStatic
class ServerCLI implements CLIOptions {

    /**
     * Builds the command-line options to build a {@link CommandLineParser parser}.
     * @return the {@link Options}.
     */
    @NonNull
    protected static Options buildOptions() {
        CommandLineHelper.buildOptions()
    }

    /**
     * Runs the Proof of Concept from the command line.
     * @param args the command-line arguments.
     */
    @SuppressWarnings('JavaIoPackageAccess')
    static void main(@NonNull final String[] args) {

        final NettyBackend backend = new NettyBackend()

        Runtime.runtime.addShutdownHook({ backend.stopServer() })

        final CommandLineParser parser = new DefaultParser()

        final Options options = buildOptions()

        final CommandLine cmd = parser.parse(options, args)

        final int port = CommandLineHelper.getPortValue(cmd)

        backend.launchServer(port)
    }
}
