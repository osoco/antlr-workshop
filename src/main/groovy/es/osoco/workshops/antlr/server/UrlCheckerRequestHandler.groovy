package es.osoco.workshops.antlr.server

import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.UrlCheckerCommandVisitor
import es.osoco.workshops.antlr.UrlCheckerLexer
import es.osoco.workshops.antlr.UrlCheckerParser
import groovy.transform.CompileStatic
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.jetbrains.annotations.NotNull

@CompileStatic
class UrlCheckerRequestHandler {

    String process(@NotNull final String message) {
        LoggingFactory.instance.createLogging().info("Received ${message}")
        @NotNull final UrlCheckerParser parser = buildParser(message)
        parseCommand(parser)
    }

    UrlCheckerParser buildParser(@NotNull final String message) {
        @NotNull final UrlCheckerLexer lexer = new UrlCheckerLexer(new ANTLRInputStream(message))
        @NotNull final CommonTokenStream tokens = new CommonTokenStream(lexer)
        new UrlCheckerParser(tokens)
    }

    String parseCommand(@NotNull final UrlCheckerParser parser) {
        @NotNull final ParseTree tree = parser.command()

        @NotNull final UrlCheckerCommandVisitor visitor = new UrlCheckerCommandVisitor()

        visitor.visit(tree)

        visitor.response
    }
}
