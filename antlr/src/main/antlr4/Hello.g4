grammar Hello;
r : 'hello' ID ;
Id : [a-z]+ ;
WS:[ \t\r\n]+ -> skip ;