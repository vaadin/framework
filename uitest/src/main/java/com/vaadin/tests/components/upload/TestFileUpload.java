package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;

public class TestFileUpload extends TestBase implements Receiver {

    private Log log = new Log(5);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    protected void setup() {
        Upload u = new Upload("Upload", new Upload.Receiver() {

            @Override
            public OutputStream receiveUpload(String filename,
                    String mimeType) {
                return baos;
            }
        });
        u.setId("UPL");
        u.addFailedListener(event -> {
            String hash = DigestUtils.md5Hex(baos.toByteArray());

            log.log("<span style=\"color: red;\">Upload failed. Name: "
                    + event.getFilename() + ", Size: " + baos.size() + ", md5: "
                    + hash + "</span>");
            baos.reset();
        });
        u.addSucceededListener(event -> {
            String hash = DigestUtils.md5Hex(baos.toByteArray());
            log.log("Upload finished. Name: " + event.getFilename() + ", Size: "
                    + baos.size() + ", md5: " + hash);
            baos.reset();
        });
        u.setImmediateMode(false);

        addComponent(log);
        addComponent(u);
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        getMainWindow().showNotification("Receiving upload");
        return new ByteArrayOutputStream();
    }

    @Override
    protected Integer getTicketNumber() {
        return 6465;
    }

    @Override
    protected String getDescription() {
        return "Creates and prints an MD5 hash of any uploaded file.";
    }

}
