# pattern-matcher
Annotated suffix tree (AST) based pattern matching library.

Searches within given collection of words with using of pattern rules.

Available patterns are:
*   Capital letters, i.e. ABC - matches CamelCase letters. For example, ABC matches with <b>A</b>xxx<b>B</b>xxxx<b>C</b>
*   Lower case letters makes matching more strictly, e.g. AbC matches with <b>Ab</b>xxx<b>C</b>yyy, and abcD with <b>abcD</b>xyz
*   Asterisk \* matches with any sequence, e.g. \*a\*b matches with xxxx<b>a</b>xxxx<b>b</b>yyy and <b>ab</b>
*   Blank at the end of pattern matches with the end of word, e.g. '\*ab ' matches with xxxx<b>ab</b>, but not with xxxxaby


Usage Example

```java
// load the list of words
PatternMatcher<String> matcher = new PatternMatcher<>(
    List.of("AxB", "AyyBcC", "AzzzBdCD", "BCD")
);

// search by pattern 'AB'
MatchingResultSet<String> res = matcher.match("AB");

// dump result
res.getResultSet().forEach((word, entries) -> {
    System.out.print(word + " : ");
    entries.getMatchings().forEach(System.out::print);
    System.out.println();
});

```

The code above outputs all matched words with intervals of each matching, e.g.:
```
AxB : [0 , 1)[2 , 3)
AyyBcC : [0 , 1)[3 , 4)
AzzzBdCD : [0 , 1)[4 , 5)
```
