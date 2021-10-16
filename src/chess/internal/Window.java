package chess.internal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Window extends WindowAdapter {

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

    private Window() {
        assert EventQueue.isDispatchThread() : "!EventQueue.isDispatchThread()";
        this.peer = new JFrame("Chess");
        this.renderer = new Renderer();
        this.peer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final Dimension maximum = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds()
                .getSize();
        final Dimension minimum =
                new Dimension(Math.min(768, maximum.width), Math.min(576, maximum.height));
        final Dimension size;
        if (JFrame.MAXIMIZED_BOTH == Vault.getInt("window" + ".maximized", JFrame.NORMAL)) {
            size = minimum;
            this.peer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            size = new Dimension(
                    Math.min(Math.abs(Vault.getInt("window.width", minimum.width)), maximum.width),
                    Math.min(Math.abs(Vault.getInt("window.height", minimum.height)), maximum.height));
        }
        this.peer.setSize(size);
        this.peer.setMinimumSize(minimum);
        this.peer.setLocationRelativeTo(null);
        this.peer.add(this.renderer);
        this.peer.setAlwaysOnTop(Boolean.parseBoolean(Vault.get("window.alwaysOnTop", "true")));
        this.peer.addWindowListener(this);
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
    public void windowClosing(final WindowEvent event) {
        assert event != null : "event == null";
        Vault.put("window.width", String.valueOf(this.peer.getWidth()));
        Vault.put("window.height", String.valueOf(this.peer.getHeight()));
        Vault.put("window.maximized", String.valueOf(this.peer.getExtendedState()
                & JFrame.MAXIMIZED_BOTH));
        Vault.put("window.alwaysOnTop", String.valueOf(this.peer.isAlwaysOnTop()));
        Vault.save();
    }
}
