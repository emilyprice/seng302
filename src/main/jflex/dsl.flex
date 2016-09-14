package seng302;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.Location;


%%

%class DslLexer


%line
%column
%unicode
%caseless
/* Uncomment for debugging info.
%debug*/




%cupsym DslSymbol
%cup

   
/* Declarations
 * The contents of this block is copied verbatim into the Lexer class, this
 * provides the ability to create member variable and methods to use in the
 * action blocks for rules.
*/
%{
   class MyComplexSymbol extends ComplexSymbolFactory.ComplexSymbol {

      MyComplexSymbol(String name, int type, Location left, Location right, Object val){
        super(name, type, left,right,val);
      }
      MyComplexSymbol(String name, int type, Location left, Location right){
        super(name, type, left,right);
      }

      public String toString(){
          return this.name;
      }
   }

   class MyComplexSymbolFactory extends ComplexSymbolFactory{
        public Symbol newSymbol(String name, int type, Location left, Location right){
            return new MyComplexSymbol(name, type, left, right);
        }

        public Symbol newSymbol(String name, int type, Location left, Location right, Object val){
            return new MyComplexSymbol(name, type, left, right, val);
        }

   }

   ComplexSymbolFactory csf = new MyComplexSymbolFactory();

   private Symbol symbol(String name, int type, Object value) {
       Location left = new Location(yyline+1,yycolumn+1,yychar);
       Location right= new Location(yyline+1,yycolumn+yylength(), yychar+yylength());
       return csf.newSymbol(name, type, left, right, value);
   }

   private Symbol symbol(String name, int type) {
       Location left = new Location(yyline+1,yycolumn+1,yychar);
       Location right= new Location(yyline+1,yycolumn+yylength(), yychar+yylength());
       return csf.newSymbol(name, type, left, right);
   }



    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        //System.err.println("Obtain token " + DslSymbol.terminalNames[type] + " \"" + yytext() + "\"" );
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        //System.err.println("Obtain token " + DslSymbol.terminalNames[type] + " \"" + yytext() + "\"" );
        return new Symbol(type, yyline, yycolumn, value);
    }


%}
   

/*
  Macro Declarations
*/

WhiteSpace = \p{Whitespace}
Number = \p{Digit}
Note = [A-G|a-g]([#|b|x]|(bb))?[0-8]?|[A|B|D|E|F|G|a|b|d|e|f|g]([#|b|x]|(bb))?("-1")?|[C-F|c-f]([#|b|x]|(bb))?[9]?|[C|c][#|x]?(-1)?|[G|g](b|bb)?[9]?
MidiNote = (0?[0-9]?[0-9]|1[01][0-9]|12[0-7])
Atom = [^\s|;]+
SemiColon = ";"
ScaleType = "major"|"minor"|"melodic minor"|"mel minor"|"ionian"|"dorian"|"phrygian"|"lydian"|"mixolydian"|"aeolian"|"locrian"|"minormajor"|"dorian b2"|"lydian #5"|"lydian dominant"|"mixolydian b6"|"locrian #2"|"altered"|"blues"|"major pentatonic"|"minor pentatonic"|"harmonic minor"|"harm minor"
PlayStyle = "arpeggio"
SharedChordAndInterval = "diminished 7th"|"diminished seventh"|"major 7th"|"major seventh"|"minor 7th"|"minor seventh"
ChordType = "seventh"|"7th"|"half dim"|"half diminished"|"half diminished 7th"|"half diminished seventh"|"half dim seventh"|"half dim 7th"|"dim seventh"|"dim 7th"|"dim"|"diminished"
Direction = "updown"|"up"|"down"
InversionSpecifier = "inversion 1"|"inversion 2"|"inversion 3"|"inv 1"|"inv 2"|"inv 3"
PosNum = \p{Digit}+
Interval = "unison"|(major\s(second|2nd|third|3rd|sixth|6th|ninth|9th|tenth|10th|thirteenth|13th|fourteenth|14th))|(minor\s(second|2nd|third|3rd|sixth|6th|ninth|9th|tenth|10th|thirteenth|13th|fourteenth|14th))|(augmented\s(fourth|4th|eleventh|11th))|(diminished\s(fifth|5th))|(perfect\s(fourth|4th|fifth|5th|eleventh|11th|twelfth|12th|octave))|"double octave"
RhythmType = (([0-9]+\/[0-9]+)(([ ][0-9]+\/[0-9]+)+)*)|"straight"|"medium"|"heavy"|"light"

SharpsFlats = ([1-7](#|b))|0#b|0b#
RomanNumerals = (I|II|III|IV|V|VI|VII)

%%

/* Rules
   YYINTIAL means that they are only matched at the beginning e.g "help play" would only
   match help because play is not at the beginning.
*/

<YYINITIAL> {
    "help"             { return symbol(DslSymbol.COMMAND_HELP); }
    "note"             { return symbol(DslSymbol.COMMAND_NOTE); }
    "midi"             { return symbol(DslSymbol.COMMAND_MIDI); }
    "tempo"            { return symbol(DslSymbol.COMMAND_TEMPO); }
    "rhythm"            { return symbol(DslSymbol.COMMAND_RHYTHM); }
    "version"          { return symbol(DslSymbol.COMMAND_VERSION); }
    "semitone up"      { return symbol(DslSymbol.COMMAND_SEMITONE_UP);}
    "semitone down"    {return symbol(DslSymbol.COMMAND_SEMITONE_DOWN);}
    "scale"            {return symbol(DslSymbol.COMMAND_SCALE);}
    "midi scale"       {return symbol(DslSymbol.COMMAND_MIDI_SCALE);}
    "enharmonic higher"  { return symbol(DslSymbol.COMMAND_ENHARMONIC_HIGHER); }
    "enharmonic lower"   { return symbol(DslSymbol.COMMAND_ENHARMONIC_LOWER); }
    "force set tempo"  { return symbol(DslSymbol.COMMAND_FORCE_SET_TEMPO); }
    "simple enharmonic" { return symbol(DslSymbol.COMMAND_SIMPLE_ENHARMONIC); }
    "set tempo"        { return symbol(DslSymbol.COMMAND_SET_TEMPO);  }
    "find chord"        { return symbol(DslSymbol.COMMAND_FIND_CHORD);  }
    "find chord all"        { return symbol(DslSymbol.COMMAND_FIND_CHORD_ALL);  }
    "set rhythm"        { return symbol(DslSymbol.COMMAND_SET_RHYTHM);  }
    "play scale"        {return symbol(DslSymbol.COMMAND_PLAY_SCALE); }
    "play chord"        {return symbol(DslSymbol.COMMAND_PLAY_CHORD);}
    "play interval"     {return symbol(DslSymbol.COMMAND_PLAY_INTERVAL); }
    "play"             { return symbol(DslSymbol.COMMAND_PLAY_NOTE);    }
    {InversionSpecifier} {return symbol(DslSymbol.INVERSION_SPECIFIER, new String(yytext()));}
    "interval"          {return symbol(DslSymbol.COMMAND_INTERVAL); }
    "crotchet duration"    { return symbol(DslSymbol.COMMAND_CROTCHET_DURATION); }
    "meaning of"       { return symbol(DslSymbol.COMMAND_MUSICAL_TERM_MEANING); }
    "origin of"         {return symbol(DslSymbol.COMMAND_MUSICAL_TERM_ORIGIN); }
    "category of"       {return symbol(DslSymbol.COMMAND_MUSICAL_TERM_CATEGORY); }
    "add musical term"  {return symbol(DslSymbol.COMMAND_ADD_MUSICAL_TERM); }
    "all enharmonics"   {return symbol(DslSymbol.COMMAND_ALL_ENHARMONICS); }
    "undo"              {return symbol(DslSymbol.COMMAND_UNDO); }
    "redo"              {return symbol(DslSymbol.COMMAND_REDO); }
    "twinkle"           {return symbol(DslSymbol.COMMAND_TWINKLE);}
    "chord"             {return symbol(DslSymbol.COMMAND_CHORD);}
    "interval enharmonic" {return symbol(DslSymbol.COMMAND_INTERVAL_ENHARMONIC);}
    "scale signature"    {return symbol(DslSymbol.COMMAND_SHOW_KEYSIGNATURE);}
    "scale sig"          {return symbol(DslSymbol.COMMAND_SHOW_KEYSIGNATURE);}
    "scale signature num"    {return symbol(DslSymbol.COMMAND_SHOW_KEYSIGNATURE_NUM);}
    "scale sig num"     {return symbol(DslSymbol.COMMAND_SHOW_KEYSIGNATURE_NUM);}
    "scale signature with" {return symbol(DslSymbol.COMMAND_SCALE_WITH_KEYSIG);}
    "scale sig with"    {return symbol(DslSymbol.COMMAND_SCALE_WITH_KEYSIG); }
    "instrument"        {return symbol(DslSymbol.COMMAND_SHOW_CURRENT_INSTRUMENT); }
    "all instruments"  {return symbol(DslSymbol.COMMAND_SHOW_ALL_INSTRUMENTS); }
    "set instrument"    {return symbol(DslSymbol.COMMAND_SET_INSTRUMENT); }
    "quality of"        {return symbol(DslSymbol.COMMAND_QUALITY_OF);}
    "chord function"    {return symbol(DslSymbol.COMMAND_CHORD_FUNCTION);}
    "function of"       {return symbol(DslSymbol.COMMAND_FUNCTION_OF);}
    "mode of"           {return symbol(DslSymbol.COMMAND_GET_MODE);}
    "parent of"         {return symbol(DslSymbol.COMMAND_PARENT_OF);}
    {RomanNumerals}     {return symbol(DslSymbol.ROMAN_NUMERALS, new String(yytext()));}
    {SharpsFlats}       {return symbol(DslSymbol.SHARPSFLATS, new String(yytext()));}
    {PlayStyle}         {return symbol(DslSymbol.PLAY_STYLE, new String(yytext())); }
    {Note}              {return symbol(DslSymbol.NOTE, new String(yytext())); }
    {Number}            {return symbol(DslSymbol.NUMBER, new String(yytext())); }
    {MidiNote}          {return symbol(DslSymbol.MIDINOTE, new String(yytext())); }
    {ScaleType}         {return symbol(DslSymbol.SCALE_TYPE, new String(yytext()));}
    {ChordType}         {return symbol(DslSymbol.CHORD_TYPE, new String(yytext()));}
    {Direction}         {return symbol(DslSymbol.DIRECTION, new String(yytext()));}
    {RhythmType}         {return symbol(DslSymbol.RHYTHM_TYPE, new String(yytext()));}
    {ScaleType}         {return symbol(DslSymbol.SCALE_TYPE, new String(yytext()));}
    {PosNum}            {return symbol(DslSymbol.POSNUM, new String(yytext()));}
    {Interval}          {return symbol(DslSymbol.INTERVAL, new String(yytext()));}
    {SharedChordAndInterval} {return symbol(DslSymbol.SHARED_CHORD_AND_INTERVAL, new String(yytext()));}
    {SemiColon}         {return symbol(DslSymbol.SEMIC);}
    {Atom}             { return symbol(DslSymbol.ATOM, new String(yytext()));}
    {WhiteSpace}       { /* Ignore whitespace */ }

}
