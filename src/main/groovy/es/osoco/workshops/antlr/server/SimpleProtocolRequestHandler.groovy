package es.osoco.workshops.antlr.server

import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.SimpleProtocolCommandVisitor
import es.osoco.workshops.antlr.SimpleProtocolLexer
import es.osoco.workshops.antlr.SimpleProtocolParser
import groovy.transform.CompileStatic
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.jetbrains.annotations.NotNull

@CompileStatic
class SimpleProtocolRequestHandler {

    String process(@NotNull final String message) {
        LoggingFactory.instance.createLogging().info("Received ${message}")
        SimpleProtocolParser parser = buildParser(message)
        parseCommand(parser)
    }

    SimpleProtocolParser buildParser(@NotNull final String message) {
        SimpleProtocolLexer lexer = new SimpleProtocolLexer(new ANTLRInputStream(message))
        CommonTokenStream tokens = new CommonTokenStream(lexer)
        new SimpleProtocolParser(tokens)
    }

    String parseCommand(SimpleProtocolParser parser) {
        ParseTree tree = parser.command()

        SimpleProtocolCommandVisitor visitor = new SimpleProtocolCommandVisitor()

        visitor.visit(tree)

        visitor.response
    }
}
