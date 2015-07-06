package com.vaadin.tests.components.upload;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Panel;

public class DragAndDropUploadAndInteractions extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox comboBox = new ComboBox();
        for (int i = 0; i < 10; i++) {
            comboBox.addItem("Test " + i);
        }
        addComponent(comboBox);
        Button b = new Button("Dummy");
        addComponent(b);
        Panel p = new Panel();
        p.setHeight(200, Unit.PIXELS);
        p.setWidth(200, Unit.PIXELS);
        MyUploadPanel myUploadPanel = new MyUploadPanel(p);
        addComponent(myUploadPanel);
    }

    class MyUploadPanel extends DragAndDropWrapper implements DropHandler {
        private static final long serialVersionUID = 1L;

        public MyUploadPanel(Component root) {
            super(root);
            setDropHandler(this);
        }

        @Override
        public void drop(DragAndDropEvent event) {
            WrapperTransferable tr = (WrapperTransferable) event
                    .getTransferable();
            Html5File[] files = tr.getFiles();

            if (files != null) {
                List<Html5File> filesToUpload = Arrays.asList(files);
                for (Html5File file : filesToUpload) {
                    file.setStreamVariable(new MyStreamVariable());
                }
            }
        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }

    }

    class MyStreamVariable implements StreamVariable {
        private static final long serialVersionUID = 1L;

        @Override
        public OutputStream getOutputStream() {
            return new ByteArrayOutputStream();
        }

        @Override
        public boolean listenProgress() {
            return true;
        }

        long lastEvent = 0;
        long lastTime = 0;

        @Override
        public void onProgress(StreamingProgressEvent event) {
            long received = event.getBytesReceived() - lastEvent;
            long now = new Date().getTime();
            long time = now - lastTime;
            lastTime = now;
            lastEvent = event.getBytesReceived();
            if (time == 0) {
                return;
            }
            log("Received " + received + " bytes in " + time + "ms: "
                    + formatSize(received / (time / 1000.0)) + "/s");
            log("Streaming OnProgress - ContentLength: "
                    + formatSize(event.getContentLength())
                    + " - Bytes Received: "
                    + formatSize(event.getBytesReceived()));
        }

        @Override
        public void streamingStarted(StreamingStartEvent event) {
            lastEvent = 0;
            lastTime = new Date().getTime();
            log("Streaming Started - ContentLength: "
                    + formatSize(event.getContentLength())
                    + " - Bytes Received: "
                    + formatSize(event.getBytesReceived()));
        }

        @Override
        public void streamingFinished(StreamingEndEvent event) {
            log("Streaming Finished - ContentLength: "
                    + formatSize(event.getContentLength())
                    + " - Bytes Received: "
                    + formatSize(event.getBytesReceived()));
        }

        @Override
        public void streamingFailed(StreamingErrorEvent event) {
            log("Streaming Failed - ContentLength: "
                    + formatSize(event.getContentLength())
                    + " - Bytes Received: "
                    + formatSize(event.getBytesReceived()));
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }

    }

    protected String formatSize(double contentLength) {
        double d = contentLength;
        int suffix = 0;
        String[] suffixes = new String[] { "B", "KB", "MB", "GB", "TB" };
        while (d > 1024) {
            suffix++;
            d /= 1024.0;
        }
        return String.format("%.1f %s", d, suffixes[suffix]);
    }

    @Override
    protected String getTestDescription() {
        return "Drop a large (100 MB) file using IE10 and interact with the application while uploading. Ensure the uploads succeeds even though you are interacting with the app.";
    }
}
