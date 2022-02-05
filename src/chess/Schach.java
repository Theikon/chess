package chess;

import chess.internal.Board;
import chess.internal.Pipeline;
import chess.internal.Window;

import javax.swing.JFrame;
import java.awt.event.WindowEvent;

/**
 * Eine Klasse, welche eine acht mal acht Reihung des komplexen Datentyps {@code
 * String} in einer grafischen Oberfläche darzustellen vermag.
 */
public class Schach implements AutoCloseable {

    private static final String[][] STANDARD = new String[][] {
            new String[] { "ST", "SB", " ", " ", " ", " ", "WB", "WT" },
            new String[] { "SS", "SB", " ", " ", " ", " ", "WB", "WS" },
            new String[] { "SL", "SB", " ", " ", " ", " ", "WB", "WL" },
            new String[] { "SD", "SB", " ", " ", " ", " ", "WB", "WD" },
            new String[] { "SK", "SB", " ", " ", " ", " ", "WB", "WK" },
            new String[] { "SL", "SB", " ", " ", " ", " ", "WB", "WL" },
            new String[] { "SS", "SB", " ", " ", " ", " ", "WB", "WS" },
            new String[] { "ST", "SB", " ", " ", " ", " ", "WB", "WT" }
    };

    private static boolean initialized;

    static {
        initialized = false;
        Pipeline.initialize();
    }

    /**
     * Returns {@code true} when {@code AWT} has been initialized; otherwise {@code false}.
     */
    private static boolean isInitialized() {
        return initialized;
    }

    /**
     * Erstellt eine neue Instanz der Klasse und setzt {@code brett} auf die
     * Standardposition.
     * 
     * @param brett eine acht mal acht Reihung.
     * @throws IllegalArgumentException erhoben wenn {@code brett} in irgendeiner
     * Weise ungültig ist.
     */
    public Schach(final String[][] brett) {
        if (brett == null) {
            throw new IllegalArgumentException("brett == null");
        } else {
            if (brett.length != Board.SIZE) {
                throw new IllegalArgumentException("brett.length != " + Board.SIZE);
            } else {
                for (int i = 0; i < brett.length; i++) {
                    final String[] chunk = brett[i];
                    if (chunk.length != brett.length) {
                        throw new IllegalArgumentException("brett[" + i + "].length != " + brett.length);
                    } else {
                        System.arraycopy(STANDARD[i], 0, chunk, 0, brett.length);
                    }
                }
            }
        }
    }

    /**
     * Erstellt eine neue Instanz der Klasse.
     */
    public Schach() {
    }

    /**
     * Zeigt {@code brett} auf einer grafischen Oberfläche an und wartet {@code
     * auszeit} Millisekunden.
     *
     * @param brett die anzuzeigende acht mal acht Reihung.
     * @param auszeit die zu wartende Zeit in Millisekunden.
     * @throws IllegalArgumentException erhoben wenn {@code brett} in irgendeiner
     * Weise ungültig ist.
     */
    public void zeige(final String[][] brett, final long auszeit) {
        if (brett == null) {
            throw new IllegalArgumentException("brett == null");
        } else {
            if (brett.length != Board.SIZE) {
                throw new IllegalArgumentException("brett.length != " + Board.SIZE);
            } else {
                final String[][] copy = new String[brett.length][brett.length];
                for (int i = 0; i < brett.length; i++) {
                    final String[] chunk = brett[i];
                    if (chunk.length != brett.length) {
                        throw new IllegalArgumentException("brett[" + i + "].length != " + brett.length);
                    } else {
                        System.arraycopy(chunk, 0, copy[i], 0, brett.length);
                    }
                }
                initialized = true;
                Window.current().postSync((final Window owner) -> {
                    try {
                        final JFrame peer = owner.getPeer();
                        owner.getRenderer().getBoard().apply(copy);
                        if (peer.isVisible()) {
                            peer.repaint();
                        } else {
                            peer.setVisible(true);
                        }
                    } catch (final IllegalArgumentException ex) {
                        System.err.println("Das Schachbrett konnte wegen eines Fehlers nicht aktualisiert werden.");
                    }
                });
                try {
                    if (auszeit > 0L) {
                        Thread.sleep(auszeit);
                    }
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Zeigt {@code brett} auf einer grafischen Oberfläche an und wartet eine Sekunde.
     *
     * @param brett die anzuzeigende acht mal acht Reihung.
     * @throws IllegalArgumentException erhoben wenn {@code brett} in irgendeiner
     * Weise ungültig ist.
     */
    public void zeige(final String[][] brett) {
        this.zeige(brett, 500L);
    }

    @Override
    public void close() {
        if (isInitialized()) {
            Window.current().postSync((final Window owner) -> {
                final JFrame peer = owner.getPeer();
                if (peer.isVisible()) {
                    peer.dispatchEvent(new WindowEvent(peer, WindowEvent.WINDOW_CLOSING));
                }
            });
        }
    }
}
