# MEROS1

~   G R A M M A R   ~

expr -> term expr2

expr2 -> + term expr2
        |- term expr2
        |e

term -> factor term2

term2-> ** factor term2
       |e

factor -> num
        |(expr)

num -> digit num
      |digit

digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9




~   T A B L E S   ~

ΖΗΤΟΥΜΕΝΟ    |                  ΥΠΟΛΟΓΙΣΜΟΣ                                            |        ΑΠΟΤΕΛΕΣΜΑ
-------------|-------------------------------------------------------------------------|---------------------------
first(expr)  | first(term) = first(factor) = { first(num) , ( }= { first(digit) , ( }  |  {0,1,2,3,4,5,6,7,8,9,( }
first(expr2) |                                                                         |  {+,-,e}
first(term)  | first(factor) = { first(num) , ( }= { first(digit) , ( }                |  {0,1,2,3,4,5,6,7,8,9,( }
first(term2) |                                                                         |  {**,e}
first(factor)| { first(num) , ( } = { first(digit) , ( }                               |  {0,1,2,3,4,5,6,7,8,9,( }
first(num)   | first(digit)                                                            |  {0,1,2,3,4,5,6,7,8,9 }
first(digit) |                                                                         |  {0,1,2,3,4,5,6,7,8,9 }



ΖΗΤΟΥΜΕΝΟ      |                  ΥΠΟΛΟΓΙΣΜΟΣ                            |        ΑΠΟΤΕΛΕΣΜΑ
---------------|---------------------------------------------------------|----------------------------------
follow(expr)   |                                                         | {),EOF}
follow(expr2)  | follow(expr)                                            | {),EOF}
follow(term)   | first(expr2) = { + , - , e } = { + , - , follow(expr) } | {+,-,),EOF}   
follow(term2)  | follow(term)                                            | {+,-,),EOF}   
follow(factor) | first(term2) = {**,e} {**,follow(term)}                 | {**,+,-,),EOF}  
follow(num)    | follow (factor)                                         | {**,+,-,),EOF} 
follow(digit)  | first(num) U follow(num)                                | {0,1,2,3,4,5,6,7,8,9,**,+,-,),EOF}


ΖΗΤΟΥΜΕΝΟ      |         ΥΠΟΛΟΓΙΣΜΟΣ           |        ΑΠΟΤΕΛΕΣΜΑ
---------------|-------------------------------|---------------------------
first+(expr)   | first(expr)                   | {0,1,2,3,4,5,6,7,8,9,( }
first+(expr2)  | first(expr2) U follow(expr2)  | {+,-,),EOF}
first+(term)   | first(term)                   | {0,1,2,3,4,5,6,7,8,9,( }
first+(term2)  | first+(term2) U follow(term2) | {**,+,-,),EOF}
first+(factor) | first(factor)                 | {0,1,2,3,4,5,6,7,8,9,( }
first+(num)    | first(num)                    | {0,1,2,3,4,5,6,7,8,9}
first+(digit)  | first(digit)                  | {0,1,2,3,4,5,6,7,8,9,}



~    P A R S I N G   T A B L E      ~
* ΟΛΑ ΤΑ ΚΕΝΑ EINAI ERROR


|       |    0,...,9    |      +       |     -        |      **       |      (     |    )    |    $      
--------|---------------|--------------|--------------|---------------|------------|---------|---------
|expr   | term expr2    |              |              |               | term expr2 |         |
|expr2  |               | + term expr2 | - term expr2 |               |            |    e    |    e
|term   |factor term2   |              |              |               |factor term2|         |
|term2  |               |      e       |        e     |** factor term2|            |    e    |    e
|factor |      num      |              |              |               |  (expr)    |         |
|num    |digit num|digit|              |              |               |            |         |    
|digit  |    0,...,9    |              |              |               |            |         |       
 