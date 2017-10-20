package com.vaadin.tests.resources;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.annotations.Push;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@Push
public class DownloadWithPush extends AbstractTestUIWithLog {

    private static class GeneratedStream extends InputStream {
        int read = 0;
        int next = 'a';
        private final int size;

        public GeneratedStream(int size) {
            this.size = size;
        }

        @Override
        public int read() throws IOException {
            if (available() == 0) {
                return -1;
            }

            read++;
            next++;
            if (next > 'z') {
                next = 'a';
            }
            return next;
        }

        @Override
        public int available() throws IOException {
            return size - read;
        }
    }

    private final class DownloadResoure extends StreamResource {
        private DownloadResoure(StreamSource streamSource, String filename) {
            super(streamSource, filename);
        }

        @Override
        public DownloadStream getStream() {
            DownloadStream ds = super.getStream();
            ds.setParameter("Content-Disposition",
                    "attachment; filename=" + getFilename() + ";");
            return ds;
        }
    }

    private Resource hugeFileResource = createResource();;
    private int fileSize = 300 * (1024 * 1024);

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Download a "
                + String.format("%.1f", fileSize / 1024.0 / 1024.0) + "MB file",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getUI().getPage().open(hugeFileResource, "", false);
                    }
                });
        addComponent(b);
    }

    private Resource createResource() {
        Resource hugeFileResource = new DownloadResoure(new StreamSource() {
            @Override
            public InputStream getStream() {
                return new GeneratedStream(fileSize);
            }
        }, "hugefile.txt");
        return hugeFileResource;
    }

    @Override
    protected Integer getTicketNumber() {
        return 19709;
    }

}
