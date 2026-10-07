package util;

import javax.swing.*;
import java.awt.*;

public class GlobalExceptionHandler {

    public static void install() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();

            SwingUtilities.invokeLater(() -> {
                JTextArea textArea = new JTextArea(10, 40);
                textArea.setText(getStackTrace(throwable));
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

                JScrollPane scroll = new JScrollPane(textArea);

                JOptionPane.showMessageDialog(null,
                    scroll,
                    "Unexpected Error — " + throwable.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
            });
        });
    }

    private static String getStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n\n");
        for (StackTraceElement e : t.getStackTrace()) {
            sb.append("    at ").append(e.toString()).append("\n");
        }
        if (t.getCause() != null) {
            sb.append("\nCaused by: ").append(t.getCause().toString()).append("\n");
        }
        return sb.toString();
    }
}