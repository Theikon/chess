package chess.internal;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Renderer extends JPanel {

    public static final Color BACKGROUND = new Color(48, 46, 43);

    public static final Font FONT = Font.decode("Dialog-14-BOLD");

    private final Board board;

    public Renderer() {
        super(null, true);
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        this.board = new Board(new String[Board.SIZE][Board.SIZE]);
        this.setBackground(BACKGROUND);
        this.setFont(FONT);
    }

    @Override
    protected void paintComponent(final Graphics context) {
        super.paintComponent(context);
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        assert context != null : "context == null";
        final Graphics2D graphics = (Graphics2D) context;
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        final int size = this.board.size;
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int length = Math.min(width / size, height / size);
        final int offsetX = (width - size * length) / 2;
        final int offsetY = (height - size * length) / 2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final boolean even = (i + j) % 2 == 0;
                final Square square = this.board.get(i, j);
                final Color light, dark;
                if (square.highlight) {
                    light = Square.LIGHT_HIGHLIGHT;
                    dark = Square.DARK_HIGHLIGHT;
                } else {
                    light = Square.LIGHT;
                    dark = Square.DARK;
                }
                final int x = offsetX + i * length;
                final int y = offsetY + j * length;
                graphics.setPaint(even ? light : dark);
                graphics.fillRect(x, y, length, length);
                graphics.setPaint(even ? dark : light);
                graphics.drawString((" " + i + "," + j).intern(), x, y +  graphics.getFontMetrics().getHeight());
                graphics.drawImage(square.icon, x, y, length, length, null);
            }
        }
    }

    public Board getBoard() {
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        return this.board;
    }
}
