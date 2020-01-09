package es.osoco.workshops.antlr.server

import groovy.transform.CompileStatic

@CompileStatic
interface CLIOptions {

    String PORT_OPTION = 'p'

    String PORT_SYSTEM_PROPERTY = 'es.osoco.workshops.antlr.server.port'

    String PORT_ENV_VARIABLE = 'ES_OSOCO_WORKSHOPS_ANTLR_SERVER_PORT'

    int DEFAULT_PORT = 9999
}