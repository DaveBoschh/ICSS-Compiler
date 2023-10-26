grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: (variableAssignment | styleRule)* EOF;
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression+ SEMICOLON;
styleRule: selector OPEN_BRACE ruleBody CLOSE_BRACE;
selector: tagSelector | classSelector | idSelector;
ruleBody: (declaration| ifClause | variableAssignment)*;
declaration: propertyName COLON expression SEMICOLON;
expression: literal |
            expression MUL expression |
            expression (PLUS | MIN) expression;
ifClause: IF BOX_BRACKET_OPEN (variableReference | boolLiteral) BOX_BRACKET_CLOSE OPEN_BRACE ruleBody CLOSE_BRACE elseClause?;
elseClause: ELSE OPEN_BRACE ruleBody CLOSE_BRACE;
literal: boolLiteral |
        colorLiteral |
        percentageLiteral |
        pixelLiteral |
        scalarLiteral |
        variableReference;

percentageLiteral: PERCENTAGE;
colorLiteral: COLOR;
boolLiteral: TRUE | FALSE;
variableReference: CAPITAL_IDENT;
tagSelector: LOWER_IDENT;
scalarLiteral: SCALAR;
propertyName: LOWER_IDENT;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;
pixelLiteral: PIXELSIZE;



