package es.osoco.workshops.antlr

import es.osoco.workshops.antlr.SimpleProtocolParser.CommandContext
import es.osoco.workshops.antlr.SimpleProtocolParser.QuitContext
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
class SimpleProtocolCommandVisitor extends SimpleProtocolBaseVisitor<String> {

    String response

    @Override
    String visitCommand(@NotNull final CommandContext ctx) {

        this.response = ctx.getChild(0).getText()

        return super.visitCommand(ctx)
    }

    @Override
    String visitQuit(@NotNull final QuitContext ctx) {
        System.exit(0)
        ""
    }
}
