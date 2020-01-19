package es.osoco.workshops.antlr

// import es.osoco.workshops.antlr.HTMLParser.HtmlAttributeValueContext
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
// TODO
class CheckUrlVisitor {// extends HTMLParserBaseVisitor<String> {
    List<String> brokenUrls = []

/*
    @Override
    String visitHtmlAttributeValue(@NotNull final HtmlAttributeValueContext ctx) {
        // TODO: implement me
        super.visitHtmlAttributeValue(ctx)
    }
*/

    int checkHttpStatus(@NotNull final String url) {
        @NotNull final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection()
        connection.requestMethod = 'HEAD'
        connection.connect()
        connection.responseCode
    }
}
