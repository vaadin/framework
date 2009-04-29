/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.launcher;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.itmill.toolkit.launcher.util.BrowserLauncher;

/**
 * This class starts servlet container and opens a simple control dialog.
 * 
 */
public class ITMillToolkitDesktopMode {

    public static void main(String[] args) {

        final Map serverArgs = ITMillToolkitWebMode.parseArguments(args);
        boolean deployed = false;
        try {
            // Default deployment: embedded.war
            deployed = deployEmbeddedWarfile(serverArgs);
        } catch (final IOException e1) {
            e1.printStackTrace();
            deployed = false;
        }

        // Check if deployment was succesful
        if (!deployed && !serverArgs.containsKey("webroot")) {
            // Default deployment failed, try other means
            if (new File("WebContent").exists()) {
                // Using WebContent directory as webroot
                serverArgs.put("webroot", "WebContent");
            } else {
                System.err.print("Failed to deploy Toolkit application. "
                        + "Please add --webroot parameter. Exiting.");
                return;
            }
        }

        // Start the Winstone servlet container
        final String url = ITMillToolkitWebMode.runServer(serverArgs,
                "Desktop Mode");

        if (!serverArgs.containsKey("nogui") && url != null) {
			
            // Open browser into application URL
            BrowserLauncher.openBrowser(url);

            // Open control dialog
            /*
             * Swing components should never be manipulated outside the event
             * dispatch thread.
             */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        openServerControlDialog(url);
                    } catch (HeadlessException e) {
                        // nop, starting from console
                    }
                }
            });
        }
    }

    /**
     * Open a control dialog for embedded server.
     * 
     * @param applicationUrl
     *            Application URL
     */
    private static void openServerControlDialog(final String applicationUrl) {

        // Main frame
        final String title = "Desktop Server";
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Create link label and listen mouse click
        final JLabel link = new JLabel("<html>"
                + "<center>Desktop Server is running at: <br>" + "<a href=\""
                + applicationUrl + "\">" + applicationUrl
                + "</a><br>Close this window to shutdown the server.</center>"
                + "</html>");
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserLauncher.openBrowser(applicationUrl);
            }
        });

        // Create a panel and add components to it.
        final JPanel contentPane = new JPanel(new FlowLayout());
        frame.setContentPane(contentPane);
        contentPane.add(link);

        // Close confirmation
        final JLabel question = new JLabel(
                "This will stop the server. Are you sure?");
        final JButton okButton = new JButton("OK");
        final JButton cancelButton = new JButton("Cancel");

        // List for close verify buttons
        final ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == okButton) {
                    System.exit(0);
                } else {
                    Rectangle bounds = frame.getBounds();
                    frame.setTitle(title);
                    contentPane.removeAll();
                    contentPane.add(link);
                    contentPane.setBounds(bounds);
                    frame.setBounds(bounds);
                    frame.setVisible(true);
                    frame.repaint();
                }
            }
        };
        okButton.addActionListener(buttonListener);
        cancelButton.addActionListener(buttonListener);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                final Rectangle bounds = frame.getBounds();
                frame.setTitle("Confirm close");
                contentPane.removeAll();
                contentPane.add(question);
                contentPane.add(okButton);
                contentPane.add(cancelButton);
                frame.setBounds(bounds);
                frame.setVisible(true);
                frame.repaint();
            }
        });

        // Position the window nicely
        final java.awt.Dimension screenSize = java.awt.Toolkit
                .getDefaultToolkit().getScreenSize();
        final int w = 270;
        final int h = 95;
        final int margin = 20;
        frame.setBounds(new Rectangle(screenSize.width - w - margin,
                screenSize.height - h - margin * 2, w, h));
        frame.toFront();
        frame.setVisible(true);
    }

    /**
     * Deploy file named "embedded.war" from classpath (inside jar file).
     * 
     * @param args
     * @return
     * @throws IOException
     */
    protected static boolean deployEmbeddedWarfile(Map args) throws IOException {
        final String embeddedWarfileName = "/embedded.war";
        final InputStream embeddedWarfile = ITMillToolkitDesktopMode.class
                .getResourceAsStream(embeddedWarfileName);
        if (embeddedWarfile != null) {
            final File tempWarfile = File.createTempFile("embedded", ".war")
                    .getAbsoluteFile();
            tempWarfile.getParentFile().mkdirs();
            tempWarfile.deleteOnExit();

            final String embeddedWebroot = "winstoneEmbeddedWAR";
            final File tempWebroot = new File(tempWarfile.getParentFile(),
                    embeddedWebroot);
            tempWebroot.mkdirs();

            final OutputStream out = new FileOutputStream(tempWarfile, true);
            int read = 0;
            final byte buffer[] = new byte[2048];
            while ((read = embeddedWarfile.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.close();
            embeddedWarfile.close();

            args.put("warfile", tempWarfile.getAbsolutePath());
            args.put("webroot", tempWebroot.getAbsolutePath());
            args.remove("webappsDir");
            args.remove("hostsDir");
            return true;
        }
        return false;
    }
}
