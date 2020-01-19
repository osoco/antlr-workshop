package es.osoco.workshops.antlr

import es.osoco.logging.Logging
import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.UrlCheckerParser.QuitContext
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
class UrlCheckerCommandVisitor extends UrlCheckerBaseVisitor<String> {

    String response = ""

    @Override
    String visitQuit(@NotNull final QuitContext ctx) {
        System.exit(0)
        ""
    }

    // TODO
    /*
    @Override
    String visitCheckUrl(@NotNull final CheckUrlContext ctx) {
        final String url = ctx.getChild(1).getText()
        checkUrl(url)
        super.visitCheckUrl(ctx)
    }
    */

    void checkUrl(@NotNull final String url) {
        @NotNull final Logging logging = LoggingFactory.instance.createLogging()
        logging.info("Checking url ${url}")
        try {
            @NotNull final String content = url.toURL().text
            if (content) {
                // TODO
                // @NotNull final HTMLParser parser = buildParser(content)
                // for (@NotNull final String brokenUrl: checkUrls(parser)) {
                //      response += "Broken url: ${brokenUrl}\n"
                // }
            }
        } catch (final Throwable cannotAccessUrl) {
            logging.error("Cannot access ${url}", cannotAccessUrl)
        }
    }

    // TODO
    /*
    @NotNull
    List<String> checkUrls(@NotNull final ??? parser) {
        @NotNull final ParseTree tree = parser.htmlDocument()

        @NotNull final CheckUrlVisitor visitor = new CheckUrlVisitor()

        visitor.visit(tree)

        visitor.brokenUrls
    }
    */
}
