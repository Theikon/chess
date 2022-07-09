# chess
A graphical chess environment for learning Java.

## Getting started

The goal of this project was to design a lightweight library for Java beginners. As such, showing the standard chess position is possible with a mere three lines of code:

```java
String[][] brett = new String[8][8];
Schach schach = new Schach(brett);
schach.zeige(brett);
```

Please note that this library does not provide a complete implementation of chess. No rules and moves are programmed into it.

![Screenshot](https://user-images.githubusercontent.com/31321459/137604378-c849608a-44f5-4ee3-9c38-41a717aabeb5.png)

The following code snippet demonstrates all features available within the library:

```java
String[][] brett = new String[8][8];
Schach schach = new Schach();

// generate black pieces
brett[0][0] = "SB";
brett[0][1] = "SS";
brett[0][2] = "SL";
brett[0][3] = "ST";
brett[0][4] = "SD";
brett[0][5] = "SK";
schach.zeige(brett);

// generate white pieces
brett[1][0] = "WB";
brett[1][1] = "WS";
brett[1][2] = "WL";
brett[1][3] = "WT";
brett[1][4] = "WD";
brett[1][5] = "WK";
schach.zeige(brett);

// highlight the white pieces
for (int i = 0; i < 6; i++) {
  brett[1][i] += "H";
}
schach.zeige(brett);
```

### Acknowledgements

Artwork by [Colin M.L. Burnett](https://en.wikipedia.org/wiki/User:Cburnett) - Own work, [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/).
