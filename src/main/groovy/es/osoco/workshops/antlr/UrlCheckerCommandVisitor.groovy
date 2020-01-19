package es.osoco.workshops.antlr

import es.osoco.logging.Logging
import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.UrlCheckerParser.CheckUrlContext
import es.osoco.workshops.antlr.UrlCheckerParser.QuitContext
import groovy.transform.CompileStatic
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.jetbrains.annotations.NotNull

import javax.swing.text.html.HTML

@CompileStatic
class UrlCheckerCommandVisitor extends UrlCheckerBaseVisitor<String> {

    String response

    @Override
    String visitQuit(@NotNull final QuitContext ctx) {
        System.exit(0)
        ""
    }

    @Override
    String visitCheckUrl(@NotNull final CheckUrlContext ctx) {
        final String url = ctx.getChild(1).getText()
        checkUrl(url)
    }

    void checkUrl(@NotNull final String url) {
        final Logging logging = LoggingFactory.instance.createLogging()
        logging.info("Checking url ${url}")
        try {
            final String content = url.toURL().text
            if (content) {
                final HTMLParser parser = buildParser(content)
                for (@NotNull final String brokenUrl: checkUrls(parser)) {
                    response << "Broken url: ${brokenUrl}\n"
                }
            }
        } catch (final Throwable cannotAccessUrl) {
            logging.error("Cannot access ${url}", cannotAccessUrl)
        }
    }

    HTMLParser buildParser(@NotNull final String content) {
        final HTMLLexer lexer = new HTMLLexer(new ANTLRInputStream(content))
        final CommonTokenStream tokens = new CommonTokenStream(lexer)
        new HTMLParser(tokens)
    }

    List<String> checkUrls(@NotNull final HTMLParser parser) {
        final ParseTree tree = parser.htmlDocument()

        final CheckUrlVisitor visitor = new CheckUrlVisitor()

        visitor.visit(tree)

        visitor.brokenUrls
    }
}
