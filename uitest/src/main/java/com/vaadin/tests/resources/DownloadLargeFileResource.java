package com.vaadin.tests.resources;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import com.vaadin.server.FileResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;

public class DownloadLargeFileResource extends TestBase {

    private FileResource hugeFileResource = null;
    private long fileSize = (long) (1233.2 * 1024.0 * 1024.0);

    @Override
    protected void setup() {
        Button b = new Button("Download a "
                + String.format("%.1f", fileSize / 1024.0 / 1024.0) + "MB file",
                event -> download());
        addComponent(b);
    }

    protected void download() {
        if (hugeFileResource == null) {
            createFile();
        }

        getMainWindow().open(hugeFileResource);
    }

    private void createFile() {
        try {
            File hugeFile = Files.createTempFile("huge", ".txt").toFile();
            hugeFile.deleteOnExit();
            BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(hugeFile));
            int writeAtOnce = 1024 * 1024;
            byte[] b = new byte[writeAtOnce];
            for (int i = 0; i < fileSize; i += writeAtOnce) {
                os.write(b);
            }
            os.close();
            hugeFileResource = new FileResource(hugeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getDescription() {
        return "Click the button to download huge-file.txt. The file is generated on the first download.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5356;
    }

}
