//;-*- mode: antlr -*-
grammar SimpleProtocol;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

command : reload | next | prev;

reload : RELOAD;
next: NEXT;
prev: PREV;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

WS : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip;

// keywords
RELOAD: 'reload';
NEXT: 'next';
PREV : 'prev';
