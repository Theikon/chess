package chess.internal;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Square {

    public static final Map<String, Square> CACHE;

    public static final Color LIGHT;

    public static final Color DARK;

    public static final Color LIGHT_HIGHLIGHT;

    public static final Color DARK_HIGHLIGHT;

    public static final char EMPTY = 'X';

    public static final char PAWN = 'B';

    public static final char BISHOP = 'L';

    public static final char KNIGHT = 'S';

    public static final char ROOK = 'T';

    public static final char QUEEN = 'D';

    public static final char KING = 'K';

    static {
        CACHE = new HashMap<>(16, 1F);
        LIGHT = new Color(238, 238, 211);
        DARK = new Color(124, 148, 88);
        LIGHT_HIGHLIGHT = new Color(223, 128, 108);
        DARK_HIGHLIGHT = new Color(200, 110, 84);
    }

    public static boolean isFigure(final char type) {
        switch (Character.toUpperCase(type)) {
            case PAWN:
            case BISHOP:
            case KNIGHT:
            case ROOK:
            case QUEEN:
            case KING:
                return true;
            default:
                return false;
        }
    }

    public static boolean validate(final String str) {
        if (str != null) {
            switch (str.length()) {
                case 0:
                    return true;
                case 1:
                    final char chr = str.charAt(0);
                    return Team.NONE.equals(Team.parseLeniently(chr))
                            || 'H' == chr
                            || 'h' == chr;
                case 2:
                    Team team = Team.parseLeniently(str.charAt(0));
                    if (Team.NONE.equals(team)) {
                        final char type = str.charAt(1);
                        return 'H' == type || 'h' == type;
                    } else { 
                        return (Team.WHITE.equals(team) || Team.BLACK.equals(team))
                                && isFigure(str.charAt(1));
                    }
                case 3:
                    team = Team.parseLeniently(str.charAt(0));
                    char type = str.charAt(1);
                    char attr = str.charAt(2);
                    return (Team.WHITE.equals(team) || Team.BLACK.equals(team))
                            && isFigure(type)
                            && ('H' == attr || 'h' == attr || 'M' == attr || 'm' == attr);
                case 4:
                    team = Team.parseLeniently(str.charAt(0));
                    type = str.charAt(1);
                    char attr0 = str.charAt(2);
                    char attr1 = str.charAt(2);
                    return (Team.WHITE.equals(team) || Team.BLACK.equals(team))
                            && isFigure(type)
                            && ('H' == attr0 || 'h' == attr0 || 'M' == attr0 || 'm' == attr0)
                            && ('H' == attr1 || 'h' == attr1 || 'M' == attr1 || 'm' == attr1);
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    public static Square create(final String str) {
        final String qry = str != null ? str.replaceAll("[Mm]", "") : null;
        if (qry != null && qry.length() > 1) {
            try {
                final Team team = Team.parse(qry.charAt(0));
                final char type = Character.toUpperCase(qry.charAt(1));
                final boolean highlight = qry.endsWith("H") || qry.endsWith("h");
                // highlight the square?
                final String lookup = (team.toString() + type + (highlight ? "H" : "")).intern();
                Square result = CACHE.get(lookup);
                if (result == null && !Team.NONE.equals(team)) {
                    result = new Square(team, type, highlight);
                    CACHE.put(lookup, result);
                } else if (result == null) {
                    result = new Square(highlight);
                    CACHE.put(lookup, result);
                }
                return result;
            } catch (final IllegalArgumentException ex) {
                // fallback to an empty square
                return create(null);
            }
        } else {
            final boolean highlight = "H".equals(qry) || "h".equals(qry);
            final String lookup = (Square.EMPTY + (highlight ? "H" : "")).intern();
            Square result = CACHE.get(lookup);
            if (result == null) {
                result = new Square(highlight);
                CACHE.put(lookup, result);
            }
            return result;
        }
    }

    public final Team team;

    public final char figure;

    public final boolean highlight;

    public final BufferedImage icon;

    private Square(final boolean highlight) {
        this.team = Team.NONE;
        this.figure = '\0';
        this.highlight = highlight;
        this.icon = null;
    }

    private Square(final Team team,
                   final char figure,
                   final boolean highlight) {
        assert team != null : "team == null";
        this.team = team;
        this.figure = figure;
        this.highlight = highlight;
        final String path = "/" + this.team + this.figure + ".png";
        BufferedImage icon;
        try (final InputStream in = Square.class.getResourceAsStream(path)) {
            icon = in != null ? ImageIO.read(in) : null;
            if (icon == null) {
                throw new IllegalArgumentException("Could not load resource at \"" + path + "\"");
            }
        } catch (final IOException ex) {
            throw new IllegalArgumentException("Could not load resource at \"" + path + "\"", ex);
        }
        this.icon = icon;
    }

    @Override
    public String toString() {
        return this.team.toString() + this.figure;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Square) {
            final Square square = (Square) obj;
            return this.figure == square.figure && this.team == square.team;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.team.hashCode() + (int) this.figure;
    }
}
