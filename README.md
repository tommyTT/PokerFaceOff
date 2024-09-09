# PokerFaceOff

The following instructions describe how to use this program:

- the program can be run from the command line via the `tt.Main` class
- without any arguments from the command line, the program will use a random two player game of five card draw poker and score the hands.
- by giving an integer value as the first argument, this determines the number of players that should be playing
  - the number of players must be between 2 and 7
- after the number of players is given, a list of all hands can be passed to the program
  - the number of hands **must** match the number of players
  - there cannot be duplicate cards in the specified hands
  - all hands must contain exactly 5 cards
  - a card must be denoted by its value and suit (in order)
    - e.g. the ace of spades must be represented as the value `AS`
  - a hand must either be delimited by quotation marks or use comma or semicolon between each card


## Examples
- a 4 player game with random cards drawn: \
`./gradlew run --args "4"`
- a 4 player game with all hands specified: \
`./gradlew run --args "4 AS,AH,3D,2S,5D AD,4D,TH,TS,TC 4H,4S,4C,6H,8D 7H,7D,7S,7C,3H"`
