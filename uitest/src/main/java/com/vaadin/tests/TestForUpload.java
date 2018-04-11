package com.vaadin.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ProgressIndicator;
import com.vaadin.v7.ui.Select;
import com.vaadin.v7.ui.TextField;

public class TestForUpload extends CustomComponent
        implements Upload.ProgressListener {

    private static final long serialVersionUID = -3400119871764256575L;

    Layout main = new VerticalLayout();

    Buffer buffer = new MemoryBuffer();

    VerticalLayout statusLayout = new VerticalLayout();
    Panel status = new Panel("Uploaded file:", statusLayout);

    private final Upload up;

    private final Label l;

    private final ProgressIndicator pi = new ProgressIndicator();
    private final ProgressIndicator pi2 = new ProgressIndicator();

    private final Label memoryStatus;

    private final Select uploadBufferSelector;

    private TextField textField;

    private Label textFieldValue;

    private CheckBox beSluggish = new CheckBox("Be sluggish");

    private CheckBox throwExecption = new CheckBox(
            "Throw exception in receiver");

    private Button interrupt = new Button("Interrupt upload");

    public TestForUpload() {
        setCompositionRoot(main);
        main.addComponent(
                new Label("This is a simple test for upload application. "
                        + "Upload should work with big files and concurrent "
                        + "requests should not be blocked. Button 'b' reads "
                        + "current state into label below it. Memory receiver "
                        + "streams upload contents into memory. You may track"
                        + "consumption."
                        + "tempfile receiver writes upload to file and "
                        + "should have low memory consumption."));

        main.addComponent(new Label(
                "Clicking on button b updates information about upload components status or same with garbage collector."));

        textField = new TextField("Test field");
        textFieldValue = new Label();
        main.addComponent(textField);
        main.addComponent(textFieldValue);

        up = new Upload("Upload", buffer);
        up.setImmediateMode(true);

        final Button b = new Button("Reed state from upload",
                event -> readState());

        final Button c = new Button("Force GC", event -> gc());

        main.addComponent(b);
        main.addComponent(c);
        main.addComponent(beSluggish);
        main.addComponent(throwExecption);
        main.addComponent(interrupt);
        interrupt.addClickListener(event -> up.interruptUpload());

        uploadBufferSelector = new Select("StreamVariable type");
        uploadBufferSelector.setImmediate(true);
        uploadBufferSelector.addItem("memory");
        uploadBufferSelector.setValue("memory");
        uploadBufferSelector.addItem("tempfile");
        uploadBufferSelector.addValueChangeListener(event -> setBuffer());
        main.addComponent(uploadBufferSelector);

        main.addComponent(up);
        l = new Label("Idle");
        main.addComponent(l);

        up.addListener((Listener) event -> {
            // print out all events fired by upload for debug purposes
            System.out.println("Upload fired event | " + event);
        });

        up.addStartedListener(event -> {
            pi.setVisible(true);
            pi2.setVisible(true);
            l.setValue("Started uploading file " + event.getFilename());
            textFieldValue.setValue(" TestFields value at the upload start is:"
                    + textField.getValue());
        });

        up.addFinishedListener(event -> {
            pi.setVisible(false);
            pi2.setVisible(false);
            if (event instanceof Upload.FailedEvent) {
                Exception reason = ((Upload.FailedEvent) event).getReason();
                l.setValue("Finished with failure ( " + reason + "  ), idle");
            } else if (event instanceof Upload.SucceededEvent) {
                l.setValue("Finished with succes, idle");
            } else {
                l.setValue("Finished with unknow event");
            }

            statusLayout.removeAllComponents();
            final InputStream stream = buffer.getStream();
            if (stream == null) {
                statusLayout.addComponent(new Label(
                        "Upload finished, but output buffer is null"));
            } else {
                statusLayout.addComponent(
                        new Label("<b>Name:</b> " + event.getFilename(),
                                ContentMode.HTML));
                statusLayout.addComponent(
                        new Label("<b>Mimetype:</b> " + event.getMIMEType(),
                                ContentMode.HTML));
                statusLayout.addComponent(new Label(
                        "<b>Size:</b> " + event.getLength() + " bytes.",
                        ContentMode.HTML));

                statusLayout.addComponent(new Link(
                        "Download " + buffer.getFileName(),
                        new StreamResource(buffer, buffer.getFileName())));

                statusLayout.setVisible(true);
            }

            setBuffer();
        });

        up.addProgressListener((readBytes, contentLength) -> {
            pi2.setValue(new Float(readBytes / (float) contentLength));

            refreshMemUsage();
        });

        pi.setVisible(false);
        pi.setPollingInterval(1000);
        main.addComponent(pi);

        pi2.setVisible(false);
        pi2.setPollingInterval(1000);
        main.addComponent(pi2);

        memoryStatus = new Label();
        main.addComponent(memoryStatus);

        statusLayout.setMargin(true);
        status.setVisible(false);
        main.addComponent(status);

        final Button restart = new Button("R");
        restart.addClickListener(event -> {
            LegacyWindow window = (LegacyWindow) event.getButton().getUI();
            window.getApplication().close();
        });
        main.addComponent(restart);

    }

    private void setBuffer() {
        final String id = (String) uploadBufferSelector.getValue();
        if ("memory".equals(id)) {
            buffer = new MemoryBuffer();
        } else if ("tempfile".equals(id)) {
            buffer = new TmpFileBuffer();
        }
        up.setReceiver(buffer);
    }

    public void gc() {
        Runtime.getRuntime().gc();
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

    public interface Buffer
            extends StreamResource.StreamSource, Upload.Receiver {

        String getFileName();
    }

    public class MemoryBuffer implements Buffer {
        ByteArrayOutputStream outputBuffer = null;

        String mimeType;

        String fileName;

        public MemoryBuffer() {

        }

        @Override
        public InputStream getStream() {
            if (outputBuffer == null) {
                return null;
            }
            return new ByteArrayInputStream(outputBuffer.toByteArray());
        }

        /**
         * @see com.vaadin.ui.Upload.Receiver#receiveUpload(String, String)
         */
        @Override
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mimeType = MIMEType;
            outputBuffer = new ByteArrayOutputStream() {
                @Override
                public synchronized void write(byte[] b, int off, int len) {
                    beSluggish();
                    throwExecption();
                    super.write(b, off, len);
                }

            };
            return outputBuffer;
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

    public class TmpFileBuffer implements Buffer {
        String mimeType;

        String fileName;

        private File file;

        public TmpFileBuffer() {
            final String tempFileName = "upload_tmpfile_"
                    + System.currentTimeMillis();
            try {
                file = File.createTempFile(tempFileName, null);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public InputStream getStream() {
            if (file == null) {
                return null;
            }
            try {
                return new FileInputStream(file);
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
                return new FileOutputStream(file) {

                    @Override
                    public void write(byte[] b, int off, int len)
                            throws IOException {
                        beSluggish();
                        throwExecption();
                        super.write(b, off, len);
                    }

                };
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
    public void updateProgress(long readBytes, long contentLenght) {
        pi.setValue(new Float(readBytes / (float) contentLenght));

        refreshMemUsage();
    }

    private void refreshMemUsage() {
        memoryStatus.setValue("Not available in Java 1.4");

        StringBuilder mem = new StringBuilder();
        MemoryMXBean mmBean = ManagementFactory.getMemoryMXBean();
        mem.append("Heap (M):");
        mem.append(mmBean.getHeapMemoryUsage().getUsed() / 1048576);
        mem.append(" | Non-Heap (M):");
        mem.append(mmBean.getNonHeapMemoryUsage().getUsed() / 1048576);
        memoryStatus.setValue(mem.toString());

    }

    private void beSluggish() {
        if (beSluggish.getValue()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void throwExecption() {
        if (throwExecption.getValue()) {
            throwExecption.setValue(false);
            throw new RuntimeException("Test execption in receiver.");
        }
    }
}
