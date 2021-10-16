package chess.internal;

public enum Team {

    WHITE("W"),
    
    BLACK("S"),

    NONE("");

    public static Team parseLeniently(final char chr) {
        switch (chr) {
            case 'W':
            case 'w':
                return WHITE;
            case 'S':
            case 's':
                return BLACK;
            case ' ':
            case 'x':
            case 'X':
                return NONE;
            default:
                return null;
        }
    }

    public static Team parse(final char chr) {
        final Team result = parseLeniently(chr);
        if (result == null) {
            throw new IllegalArgumentException("'" + chr + "'");
        }
        return result;
    }

    private final String descriptor;

    Team(final String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String toString() {
        return this.descriptor;
    }
}
