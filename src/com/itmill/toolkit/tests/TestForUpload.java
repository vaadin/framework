package com.itmill.toolkit.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Upload;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Upload.FailedEvent;
import com.itmill.toolkit.ui.Upload.FailedListener;
import com.itmill.toolkit.ui.Upload.FinishedEvent;
import com.itmill.toolkit.ui.Upload.FinishedListener;
import com.itmill.toolkit.ui.Upload.StartedEvent;
import com.itmill.toolkit.ui.Upload.StartedListener;
import com.itmill.toolkit.ui.Upload.SucceededEvent;
import com.itmill.toolkit.ui.Upload.SucceededListener;

public class TestForUpload extends CustomComponent implements
        Upload.FinishedListener, FailedListener, SucceededListener,
        Upload.ProgressListener, StartedListener {

    Layout main = new OrderedLayout();

    Buffer buffer = new MemoryBuffer();

    Panel status = new Panel("Uploaded file:");

    private final Upload up;

    private final Label l;

    private final ProgressIndicator pi = new ProgressIndicator();

    private final Label memoryStatus;

    private final Select uploadBufferSelector;

    public TestForUpload() {
        setCompositionRoot(main);
        main.addComponent(new Label(
                "This is a simple test for upload application. "
                        + "Upload should work with big files and concurrent "
                        + "requests should not be blocked. Button 'b' reads "
                        + "current state into label below it. Memory receiver "
                        + "streams upload contents into memory. You may track"
                        + "consumption."
                        + "tempfile receiver writes upload to file and "
                        + "should have low memory consumption."));

        main
                .addComponent(new Label(
                        "Clicking on button b updates information about upload components status or same with garbage collector."));

        up = new Upload("Upload", buffer);
        up.setImmediate(true);
        up.addListener((FinishedListener) this);
        up.addListener((FailedListener) this);
        up.addListener((SucceededListener) this);
        up.addListener((StartedListener) this);

        up.setProgressListener(this);

        Button b = new Button("b", this, "readState");

        Button c = new Button("b with gc", this, "gc");

        main.addComponent(b);
        main.addComponent(c);

        uploadBufferSelector = new Select("Receiver type");
        uploadBufferSelector.setImmediate(true);
        uploadBufferSelector.addItem("memory");
        uploadBufferSelector.setValue("memory");
        uploadBufferSelector.addItem("tempfile");
        uploadBufferSelector
                .addListener(new AbstractField.ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        setBuffer();
                    }
                });
        main.addComponent(uploadBufferSelector);

        main.addComponent(up);
        l = new Label("Idle");
        main.addComponent(l);

        pi.setVisible(false);
        pi.setPollingInterval(1000);
        main.addComponent(pi);

        memoryStatus = new Label();
        main.addComponent(memoryStatus);

        status.setVisible(false);
        main.addComponent(status);

        Button restart = new Button("R");
        restart.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                getApplication().close();
            }
        });
        main.addComponent(restart);

    }

    private void setBuffer() {
        String id = (String) uploadBufferSelector.getValue();
        if ("memory".equals(id)) {
            buffer = new MemoryBuffer();
        } else if ("tempfile".equals(id)) {
            buffer = new TmpFileBuffer();
        }
        up.setReceiver(buffer);
    }

    public void gc() {
        Runtime.getRuntime().gc();
        readState();
    }

    public void readState() {
        StringBuffer sb = new StringBuffer();

        if (up.isUploading()) {
            sb.append("Uploading...");
            sb.append(up.getBytesRead());
            sb.append("/");
            sb.append(up.getUploadSize());
            sb.append(" ");
            sb.append(Math.round(100 * up.getBytesRead()
                    / (double) up.getUploadSize()));
            sb.append("%");
        } else {
            sb.append("Idle");
        }
        l.setValue(sb.toString());
        refreshMemUsage();
    }

    public void uploadFinished(FinishedEvent event) {
        status.removeAllComponents();
        InputStream stream = buffer.getStream();
        if (stream == null) {
            status.addComponent(new Label(
                    "Upload finished, but output buffer is null!!"));
        } else {
            status
                    .addComponent(new Label("<b>Name:</b> "
                            + event.getFilename(), Label.CONTENT_XHTML));
            status.addComponent(new Label("<b>Mimetype:</b> "
                    + event.getMIMEType(), Label.CONTENT_XHTML));
            status.addComponent(new Label("<b>Size:</b> " + event.getLength()
                    + " bytes.", Label.CONTENT_XHTML));

            status.addComponent(new Link("Download " + buffer.getFileName(),
                    new StreamResource(buffer, buffer.getFileName(),
                            getApplication())));

            status.setVisible(true);
        }
    }

    public interface Buffer extends StreamResource.StreamSource,
            Upload.Receiver {

        String getFileName();
    }

    public class MemoryBuffer implements Buffer {
        ByteArrayOutputStream outputBuffer = null;

        String mimeType;

        String fileName;

        public MemoryBuffer() {

        }

        public InputStream getStream() {
            if (outputBuffer == null) {
                return null;
            }
            return new ByteArrayInputStream(outputBuffer.toByteArray());
        }

        /**
         * @see com.itmill.toolkit.ui.Upload.Receiver#receiveUpload(String,
         *      String)
         */
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mimeType = MIMEType;
            outputBuffer = new ByteArrayOutputStream();
            return outputBuffer;
        }

        /**
         * Returns the fileName.
         * 
         * @return String
         */
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

        private FileInputStream stream;

        public TmpFileBuffer() {
            String tempFileName = "upload_tmpfile_"
                    + System.currentTimeMillis();
            try {
                file = File.createTempFile(tempFileName, null);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public InputStream getStream() {
            if (file == null) {
                return null;
            }
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @see com.itmill.toolkit.ui.Upload.Receiver#receiveUpload(String,
         *      String)
         */
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mimeType = MIMEType;
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Returns the fileName.
         * 
         * @return String
         */
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

    public void uploadFailed(FailedEvent event) {
        System.out.println(event);

        System.out.println(event.getSource());

    }

    public void uploadSucceeded(SucceededEvent event) {
        pi.setVisible(false);
        l.setValue("Finished upload, idle");
        System.out.println(event);
        setBuffer();
    }

    public void updateProgress(long readBytes, long contentLenght) {
        pi.setValue(new Float(readBytes / (float) contentLenght));

        refreshMemUsage();
    }

    private void refreshMemUsage() {
        memoryStatus.setValue("Not available in Java 1.4");
        /*
         * StringBuffer mem = new StringBuffer(); MemoryMXBean mmBean =
         * ManagementFactory.getMemoryMXBean(); mem.append("Heap (M):");
         * mem.append(mmBean.getHeapMemoryUsage().getUsed() / 1048576);
         * mem.append(" |�Non-Heap (M):");
         * mem.append(mmBean.getNonHeapMemoryUsage().getUsed() / 1048576);
         * memoryStatus.setValue(mem.toString());
         */
    }

    public void uploadStarted(StartedEvent event) {
        pi.setVisible(true);
        l.setValue("Started uploading file " + event.getFilename());
    }

}
