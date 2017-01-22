# pattern-matcher
Annotated suffix tree (AST) based pattern matching library.

Searches by given stream of words with using pattern rules.

Available patterns are:
*   Capital letters, i.e. ABC - matches CamelCase letters. For example, ABC matches with <b>A</b>xxx<b>B</b>xxxx<b>C</b>
*   Lower case letters makes matching more strictly, e.g. AbC matches with <b>Ab</b>xxx<b>C</b>yyy, and abcD with <b>abcD</b>xyz
*   Asterisk <i>*</i> matches with any sequence, e.g. *a*b matches with xxxx<b>a</b>xxxx<b>b</b>yyy and <b>ab</b>
*   Blank at the end of pattern matches with the end of word, e.g. '*ab ' matches with xxxx<b>ab</b>, but not with xxxxaby

