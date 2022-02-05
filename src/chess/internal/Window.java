package chess.internal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.Pipe;
import java.util.function.Function;

public class Window extends KeyAdapter {

    private static Window CURRENT = null;

    static {
        final String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (final RuntimeException
                | ReflectiveOperationException
                | UnsupportedLookAndFeelException ex) {
            System.err.println("Look and feel \"" + lookAndFeel + "\" is unsupported");
        }
    }

    public static Window current() {
        if (CURRENT == null) {
            CURRENT = Window.create();
        }
        return CURRENT;
    }

    /**
     * Constructs a new instance of the {@code class} while ensuring that
     * initialisation occurs on the event dispatch thread.
     * <p>
     * Please note that invoking the method outside the event dispatch thread
     * requires the calling thread to wait.
     *
     * @throws IllegalStateException thrown when construction failed for any reason.
     */
    private static Window create() {
        if (EventQueue.isDispatchThread()) {
            return new Window();
        } else {
            try {
                final Window[] result = new Window[1];
                EventQueue.invokeAndWait(() -> result[0] = new Window());
                return result[0];
            } catch (final InvocationTargetException | InterruptedException ex) {
                throw new IllegalStateException("Unable to construct \""
                        + Window.class.getName() + " synchronously", ex);
            }
        }
    }

    private final JFrame peer;

    private final Renderer renderer;

    private final StringBuilder data;

    private Window() {
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        this.peer = new JFrame("Chess");
        this.renderer = new Renderer();
        this.data = new StringBuilder(16);
        this.peer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final Dimension maximum = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds()
                .getSize();
        final Dimension size =
                new Dimension(Math.min(768, maximum.width), Math.min(576, maximum.height));
        this.peer.setSize(size);
        this.peer.setMinimumSize(size);
        this.peer.setLocationRelativeTo(null);
        this.peer.add(this.renderer);
        this.peer.setAlwaysOnTop(true);
        this.peer.addKeyListener(this);
    }

    public Renderer getRenderer() {
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        return this.renderer;
    }

    public JFrame getPeer() {
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        return this.peer;
    }

    /**
     * Posts a task to the event dispatch thread synchronously.
     * <p>
     * Please note that the calling thread will wait until the task in question is
     * complete.
     * 
     * @param task a task to run on the event dispatch thread.
     * @throws IllegalStateException thrown when the task failed for any reason.
     * @see #postAsync(Task)
     */
    public void postSync(final Task<Window> task) {
        assert task != null : "task == null";
        try {
            EventQueue.invokeAndWait(() -> task.run(this));
        } catch (final InvocationTargetException | InterruptedException ex) {
            throw new IllegalStateException("Unable to post \"" + task + "\" synchronously", ex);
        }
    }

    /**
     * Posts a task to the event dispatch thread synchronously and returns its result
     * to the caller.
     * <p>
     * Please note that the calling thread will wait until the task in question is
     * complete.
     *
     * @param task a task to run on the event dispatch thread.
     * @throws IllegalStateException thrown when the task failed for any reason.
     */
    @SuppressWarnings("unchecked")
    public <R> R postSync(final Function<Window, R> task) {
        assert task != null : "task == null";
        try {
            final Object[] result = new Object[1];
            EventQueue.invokeAndWait(() -> result[0] = task.apply(this));
            return (R) result[0];
        } catch (final InvocationTargetException | InterruptedException ex) {
            throw new IllegalStateException("Unable to post \"" + task + "\" synchronously", ex);
        }
    }

    /**
     * Posts a task to the event dispatch thread asynchronously.
     *
     * @param task a task to run on the event dispatch thread.
     */
    public void postAsync(final Runnable task) {
        assert task != null : "task == null";
        EventQueue.invokeLater(() -> {
            try {
                task.run();
            } catch (final RuntimeException ex) {
                System.err.println("Unable to post \"" + task + "\" asynchronously");
                ex.printStackTrace(System.err);
            }
        });
    }

    /**
     * Posts a task to the event dispatch thread asynchronously.
     *
     * @param task a task to run on the event dispatch thread.
     */
    public void postAsync(final Task<Window> task) {
        assert task != null : "task == null";
        EventQueue.invokeLater(() -> {
            try {
                task.run(this);
            } catch (final RuntimeException ex) {
                System.err.println("Unable to post \"" + task + "\" asynchronously");
                ex.printStackTrace(System.err);
            }
        });
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        assert event != null : "event == null";
        final char ch = event.getKeyChar();
        if (ch == '\b' && this.data.length() > 0) {
            this.data.deleteCharAt(this.data.length() - 1);
            this.renderer.repaint();
        } else if (ch == '\n') {
            this.data.append('\n');
            Pipeline.send(this.data.toString());
            this.data.delete(0, this.data.length());
            this.renderer.repaint();
        } else if (Character.isLetterOrDigit(ch)) {
            this.data.append(ch);
            this.renderer.repaint();
        } else if (Character.isWhitespace(ch)) {
            this.data.append(' ');
            this.renderer.repaint();
        }
        this.renderer.update("> " + this.data + " ");
    }
}
