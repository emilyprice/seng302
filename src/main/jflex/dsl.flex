package seng302;

import java_cup.runtime.*;

%%

%class DslLexer


%line
%column
%unicode

%cupsym DslSymbol
%cup
   
/* Declarations
 * The contents of this block is copied verbatim into the Lexer class, this
 * provides the ability to create member variable and methods to use in the
 * action blocks for rules.
*/
%{   
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   

/*
  Macro Declarations
*/

WhiteSpace = \p{Whitespace}
Number = \p{Digit}+
Note = [A-G|a-g]#?[-1]?[0-9]?
//Note = ^[A-G|a-g][#|b]?[1-7]?$|^[A|B|a|b][#|b]?0$|^[C|c][#|b]8$
   
%%

/* Rules
   YYINTIAL means that they are only matched at the beginning e.g "help play" would only
   match help because play is not at the beginning.
*/

<YYINITIAL> {
    "help"             { return symbol(DslSymbol.COMMAND_HELP); }
    "play"             { return symbol(DslSymbol.COMMAND_PLAY); }
    "find"             { return symbol(DslSymbol.COMMAND_FIND); }
    "note"             { return symbol(DslSymbol.COMMAND_NOTE); }
    "midi"             { return symbol(DslSymbol.COMMAND_MIDI); }
    "version"          { return symbol(DslSymbol.COMMAND_VERSION); }
    "semitone up"       { return symbol(DslSymbol.COMMAND_SEMITONE_UP);}
    "semitone down"    {return symbol(DslSymbol.COMMAND_SEMITONE_DOWN);}
    {Number}           { return symbol(DslSymbol.NUMBER, new Integer(yytext())); }
    {Note}             { return symbol(DslSymbol.NOTE, new String(yytext())); }

    {WhiteSpace}       { /* Ignore whitespace */ }
}

/* Throw an exception if we have no matches */
[^]                    { throw new RuntimeException("Illegal character <"+yytext()+">"); }