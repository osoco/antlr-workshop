//;-*- mode: antlr -*-
grammar UrlChecker;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

command : checkUrl | quit;

checkUrl : CHECK_URL URL;
quit: QUIT;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

WS : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip;

// keywords
CHECK_URL: 'check-url';
QUIT : 'quit';
URL: 'http' 's'? '://' ~[ ]*;
