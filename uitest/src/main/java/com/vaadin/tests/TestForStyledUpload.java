package com.vaadin.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ProgressIndicator;

public class TestForStyledUpload extends LegacyApplication
        implements Upload.FinishedListener, FailedListener, SucceededListener,
        StartedListener {

    Layout main = new VerticalLayout();

    TmpFileBuffer buffer = new TmpFileBuffer();

    VerticalLayout statusLayout = new VerticalLayout();
    Panel status = new Panel("Uploaded file:", statusLayout);

    private final Upload up;

    private final Label l;

    private final Label transferred = new Label("");

    private final ProgressIndicator pi = new ProgressIndicator();

    private final Label memoryStatus;

    public TestForStyledUpload() {
        main.addComponent(new Label(
                "Clicking on button b updates information about upload components status or same with garbage collector."));

        up = new Upload(null, buffer);
        up.setButtonCaption("Select file");
        up.setImmediateMode(true);
        up.addFinishedListener(this);
        up.addFailedListener(this);
        up.addSucceededListener(this);
        up.addStartedListener(this);

        up.addProgressListener((readBytes, contentLenght) -> {
            pi.setValue(new Float(readBytes / (float) contentLenght));

            refreshMemUsage();

            transferred.setValue(
                    "Transferred " + readBytes + " of " + contentLenght);
        });

        final Button b = new Button("Update status", event -> readState());

        final Button c = new Button("Update status with gc", evenet -> gc());

        main.addComponent(up);
        l = new Label("Idle");
        main.addComponent(l);

        pi.setVisible(false);
        pi.setPollingInterval(300);
        main.addComponent(pi);
        main.addComponent(transferred);

        memoryStatus = new Label();
        main.addComponent(memoryStatus);

        statusLayout.setMargin(true);
        status.setVisible(false);
        main.addComponent(status);

        Button cancel = new Button("Cancel current upload");
        cancel.addClickListener(event -> buffer.cancel());

        main.addComponent(cancel);

        final Button restart = new Button("Restart demo application");
        restart.addClickListener(event -> TestForStyledUpload.this.close());
        main.addComponent(restart);
        main.addComponent(b);
        main.addComponent(c);
    }

    public void gc() {
        Runtime.getRuntime().gc();
        readState();
    }

    public void readState() {
        final StringBuilder sb = new StringBuilder();

        if (up.isUploading()) {
            sb.append("Uploading...");
            sb.append(up.getBytesRead());
            sb.append('/');
            sb.append(up.getUploadSize());
            sb.append(' ');
            sb.append(Math.round(
                    100 * up.getBytesRead() / (double) up.getUploadSize()));
            sb.append('%');
        } else {
            sb.append("Idle");
        }
        l.setValue(sb.toString());
        refreshMemUsage();
    }

    @Override
    public void uploadFinished(FinishedEvent event) {
        statusLayout.removeAllComponents();
        final InputStream stream = buffer.getStream();
        if (stream == null) {
            statusLayout.addComponent(
                    new Label("Upload finished, but output buffer is null!!"));
        } else {
            statusLayout.addComponent(new Label(
                    "<b>Name:</b> " + event.getFilename(), ContentMode.HTML));
            statusLayout.addComponent(
                    new Label("<b>Mimetype:</b> " + event.getMIMEType(),
                            ContentMode.HTML));
            statusLayout.addComponent(
                    new Label("<b>Size:</b> " + event.getLength() + " bytes.",
                            ContentMode.HTML));

            statusLayout
                    .addComponent(new Link("Download " + buffer.getFileName(),
                            new StreamResource(buffer, buffer.getFileName())));

            status.setVisible(true);
        }
    }

    public interface Buffer
            extends StreamResource.StreamSource, Upload.Receiver {

        String getFileName();
    }

    public class TmpFileBuffer implements Buffer {
        String mimeType;

        String fileName;

        private File file;

        private FileInputStream stream;

        public TmpFileBuffer() {
            final String tempFileName = "upload_tmpfile_"
                    + System.currentTimeMillis();
            try {
                file = Files.createTempFile(tempFileName, null).toFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            up.interruptUpload();
        }

        @Override
        public InputStream getStream() {
            if (file == null) {
                return null;
            }
            try {
                stream = new FileInputStream(file);
                return stream;
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @see com.vaadin.ui.Upload.Receiver#receiveUpload(String, String)
         */
        @Override
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mimeType = MIMEType;
            try {
                return new FileOutputStream(file);
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Returns the fileName.
         *
         * @return String
         */
        @Override
        public String getFileName() {
            return fileName;
        }

        /**
         * Returns the mimeType.
         *
         * @return String
         */
        public String getMimeType() {
            return mimeType;
        }

    }

    @Override
    public void uploadFailed(FailedEvent event) {
        pi.setVisible(false);
        l.setValue("Upload was interrupted");
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        pi.setVisible(false);
        l.setValue("Finished upload, idle");
        System.out.println(event);
    }

    private void refreshMemUsage() {
        // memoryStatus.setValue("Not available in Java 1.4");
        StringBuilder mem = new StringBuilder();
        MemoryMXBean mmBean = ManagementFactory.getMemoryMXBean();
        mem.append("Heap (M):");
        mem.append(mmBean.getHeapMemoryUsage().getUsed() / 1048576);
        mem.append(" | Non-Heap (M):");
        mem.append(mmBean.getNonHeapMemoryUsage().getUsed() / 1048576);
        memoryStatus.setValue(mem.toString());
    }

    @Override
    public void uploadStarted(StartedEvent event) {
        pi.setVisible(true);
        l.setValue("Started uploading file " + event.getFilename());
    }

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow();
        setTheme("runo");
        w.addComponent(main);
        setMainWindow(w);
    }

}
