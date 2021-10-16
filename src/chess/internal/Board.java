package chess.internal;

public class Board {

    public static final int SIZE = 8;

    private final Square[] squares;

    private final Object lock;

    public final int size;

    /**
     * Constructs a new instance of the underlying {@code class}.
     *
     * @param source an eight by eight array describing the contents of {@code this}.
     * @throws IllegalArgumentException thrown when {@code source} is invalid in any way.
     */
    public Board(final String[][] source) {
        assert source != null : "source == null";
        this.size = source.length;
        this.squares = new Square[this.size * this.size];
        this.lock = new Object();
        this.apply(source);
    }

    public void apply(final String[][] source) {
        if (source != null && source.length == 8) {
            for (int i = 0; i < this.size; i++) {
                final String[] chunk = source[i];
                if (chunk != null && chunk.length == SIZE) {
                    for (int j = 0; j < this.size; j++) {
                        final String str = chunk[j];
                        if (Square.validate(str)) {
                            this.set(i, j, Square.create(str));
                        } else {
                            System.err.println("Die Koordinate (" + i + "," + j + ")"
                                    + " beinhaltet die unbekannte Bezeichnung \""
                                    + str + "\"");
                            this.set(i, j, Square.create(null));
                        }
                    }
                } else {
                    throw new IllegalArgumentException("source[" + i
                            + "] == null || source[" + i + "].length != 8");
                }
            }
        } else {
            throw new IllegalArgumentException("source == null || source.length != 8");
        }
    }

    public void set(final int x, final int y, final Square value) {
        assert x >= 0 && x < this.size : "x < 0 || x >= this.size";
        assert y >= 0 && y < this.size : "y < 0 || x >= this.size";
        synchronized (this.lock) {
            this.squares[y * this.size + x] = value;
        }
    }

    public Square get(final int x, final int y) {
        assert x >= 0 && x < this.size : "x < 0 || x >= this.size";
        assert y >= 0 && y < this.size : "y < 0 || x >= this.size";
        synchronized (this.lock) {
            return this.squares[y * this.size + x];
        }
    }

    @Override
    public String toString() {
        final StringBuilder out =
                new StringBuilder(this.size * (this.size + 3) + 2 * this.squares.length);
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                final Square piece = this.get(x, y);
                out.append('|');
                if (piece != null) {
                    out.append(piece.team).append(piece.figure);
                } else {
                    out.append("  ");
                }
            }
            out.append('|').append(System.lineSeparator());
        }
        return out.toString();
    }
}
