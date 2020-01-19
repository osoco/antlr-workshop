package es.osoco.workshops.antlr

import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.HTMLParser.HtmlAttributeValueContext
import groovy.transform.CompileStatic
import org.antlr.v4.runtime.tree.ParseTree
import org.jetbrains.annotations.NotNull

@CompileStatic
class CheckUrlVisitor extends HTMLParserBaseVisitor<String> {
    List<String> brokenUrls = []

    @Override
    String visitHtmlAttributeValue(@NotNull final HtmlAttributeValueContext ctx) {
        final ParseTree node = ctx.getChild(0)
        if (node && node.getText().startsWith('"http')) {
            final String url = node.getText().substring(1, node.getText().length() - 1)
            final int code = checkHttpStatus(url)
            LoggingFactory.instance.createLogging().info("${url} -> ${code}")
            if (code >= 400) {
                brokenUrls << url
            }
        }
        super.visitHtmlAttributeValue(ctx)
    }

    int checkHttpStatus(@NotNull final String url) {
        @NotNull final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection()
        connection.requestMethod = 'HEAD'
        connection.connect()
        connection.responseCode
    }
}
