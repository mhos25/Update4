grammar Additionrecursion;

program: (statement)+;

statement: println ';'
         | print ';'
         | varDeclarationList ';'
         | varDeclaration ';'
         | assignment ';'
         | branch
         | do_while ';'
         | while_statement
         | incrementation ';'
         | decrement ';'
         | forLoop 
         | booleanVarDeclaration ';'
         | booleanVarAssignment ';'
         ;

expression: lft= expression '/' rght=expression #Div
                | lft=expression '*' rght=expression #Mult
                | lft=expression '-' rght=expression #Minus
                | lft=expression '+' rght=expression #Plus
                | lft=expression operator = ('<'| '<=' | '>' |'>=') rght=expression #Relational
                | lft=expression '&&' rght=expression #And
                | lft=expression '||' rght=expression #Or
		| number=NUM #Number
                | txt= STRING #String
                | varName=IDENTIFIER #Variable 
                | inverseBoolean #InverseBool
                ;

varDeclaration: 'int' varName=IDENTIFIER '=' exp = expression   #VarDeclarationAndAssignment
                |'int' varName=IDENTIFIER   #SimpleVarDeclaration
                | ',' varName= IDENTIFIER	#ListDeclaration
                ;
				
				
varDeclarationList: varDeclarationList varDeclaration
		| varDeclaration
		;

assignment: varName=IDENTIFIER '=' expr=expression  #directVarAssignment
            | varName= IDENTIFIER '=' input = userInput #InputVar
            ;

println: 'println(' txt=STRING '+' varName=IDENTIFIER ')' #PrintStatementAndVariable
         | 'println(' argument=expression ')' #simplePrintStatement 
         ;

print: 'print(' argument=expression ')' ;

branch: 'if' '(' condition= expression ')' onTrue=block 'else' onFalse=block ;

do_while : 'do' loop=block 'while' '(' condition= expression ')';

while_statement: 'while' '(' condition = expression ')' loopCondition= block;

block: '{'statement* '}';

userInput: 'UserInputReader.readInt()';

incrementation: varName= IDENTIFIER '++';

decrement: varName= IDENTIFIER '--';

forLoop: 'for' '('  initialization=forInitialization  ';' condition = expression ';' change=forChange ')' loopCondition= block ;

forInitialization: varDeclaration   #DeclareVariable_For
                 | assignment       #AssignVariable_For
                 ;

forChange: incrementation
           | decrement
           ;

booleanVarDeclaration: 'boolean' variableName = IDENTIFIER          #SimpleBooleanVariableDeclaration
                     | 'boolean' variableName = IDENTIFIER '=' value = booleanValue #BooleanVariableDeclarationAndAssignment
                     ; 

booleanVarAssignment: variableName = IDENTIFIER '=' value=booleanValue;

booleanValue: 'true'    #true
            | 'false'   #false
            ;

inverseBoolean: '!' variableName = IDENTIFIER;

NUM: [0-9]+;

NEWLINE : [\r\n]+ ;

IDENTIFIER: [a-zA-Z][a-zA-Z0-9]*;

WHITESPACE: [ \t\n\r]+ -> skip;

STRING: '"' .*? '"';