//;-*- mode: antlr -*-
grammar SimpleProtocol;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
command : reload | next | prev | quit;

reload : RELOAD;
next: NEXT;
prev: PREV;
quit: QUIT;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

WS : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip;

// keywords
RELOAD: 'reload';
NEXT: 'next';
PREV : 'prev';
QUIT: 'quit';