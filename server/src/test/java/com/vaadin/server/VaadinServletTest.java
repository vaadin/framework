package com.vaadin.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class VaadinServletTest {

    @Test
    public void testGetLastPathParameter() {
        Assert.assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com"));
        Assert.assertEquals(";a",
                VaadinServlet.getLastPathParameter("http://myhost.com;a"));
        Assert.assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com/hello"));
        Assert.assertEquals(";b=c", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;b=c"));
        Assert.assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com/hello/"));
        Assert.assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;a/"));
        Assert.assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;a=1/"));
        Assert.assertEquals(";b", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b"));
        Assert.assertEquals(";b=1", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1"));
        Assert.assertEquals(";b=1,c=2", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1,c=2"));
        Assert.assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1,c=2/"));
        Assert.assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;a/"));
        Assert.assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;a=1/"));
        Assert.assertEquals(";b", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b"));
        Assert.assertEquals(";b=1", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1"));
        Assert.assertEquals(";b=1,c=2", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2"));
        Assert.assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2/"));
    }

    /**
     * Creates an archive file {@code fake.jar} that contains two
     * {@code file.txt} files, one of which resides inside {@code VAADIN}
     * directory.
     *
     * @param folder
     *            temporary folder that should house the archive file
     * @return the archive file
     * @throws IOException
     */
    private File createJAR(TemporaryFolder folder) throws IOException {
        File archiveFile = new File(folder.getRoot(), "fake.jar");
        archiveFile.createNewFile();

        ZipOutputStream zipOutputStream = new ZipOutputStream(
                getOutputStream(archiveFile));

        // Create a file to the zip
        zipOutputStream.putNextEntry(new ZipEntry("file.txt"));
        zipOutputStream.closeEntry();
        // Create a directory to the zip
        zipOutputStream.putNextEntry(new ZipEntry("VAADIN/"));
        zipOutputStream.closeEntry();
        // Create a file to the directory
        zipOutputStream.putNextEntry(new ZipEntry("VAADIN/file.txt"));
        zipOutputStream.closeEntry();
        // Create another directory to the zip
        zipOutputStream.putNextEntry(new ZipEntry("VAADIN/folder/"));
        zipOutputStream.closeEntry();
        zipOutputStream.close();

        return archiveFile;
    }

    @Test
    @SuppressWarnings("deprecation")
    public void directoryIsNotResourceRequest() throws Exception {
        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = new TemporaryFolder();
        folder.create();

        try {
            File vaadinFolder = folder.newFolder("VAADIN");
            vaadinFolder.createNewFile();

            // generate URL so it is not ending with / so that we test the
            // correct method
            String rootAbsolutePath = folder.getRoot().getAbsolutePath()
                    .replaceAll("\\\\", "/");
            if (rootAbsolutePath.endsWith("/")) {
                rootAbsolutePath = rootAbsolutePath.substring(0,
                        rootAbsolutePath.length() - 1);
            }
            URL folderPath = new URL("file:///" + rootAbsolutePath);

            assertFalse("Folder on disk should not be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request, folderPath));

            // Test any path ending with / to be seen as a directory
            assertFalse(
                    "Fake should not check the file system nor be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            new URL("file:///fake/")));

            File archiveFile = createJAR(folder);
            String tempArchive = archiveFile.getPath();
            String tempArchivePath = tempArchive.replaceAll("\\\\", "/");

            assertFalse(
                    "Folder 'VAADIN' in jar should not be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request, new URL(
                            "jar:file:///" + tempArchivePath + "!/VAADIN/")));

            assertFalse(
                    "File 'file.txt' inside jar should not be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request, new URL(
                            "jar:file:///" + tempArchivePath + "!/file.txt")));

            assertTrue(
                    "File 'file.txt' inside VAADIN folder within jar should be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            new URL("jar:file:///" + tempArchivePath
                                    + "!/VAADIN/file.txt")));

            assertFalse(
                    "Directory 'folder' inside VAADIN folder within jar should not be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            new URL("jar:file:///" + tempArchivePath
                                    + "!/VAADIN/folder/")));

            assertFalse(
                    "File 'file.txt' outside of a jar should not be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request, new URL(
                            "file:///" + rootAbsolutePath + "/file.txt")));

            assertTrue(
                    "File 'file.txt' inside VAADIN folder outside of a jar should be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            new URL("file:///" + rootAbsolutePath
                                    + "/VAADIN/file.txt")));

        } finally {
            folder.delete();
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void isAllowedVAADINResource_jarWarFileScheme_detectsAsStaticResources()
            throws IOException, URISyntaxException, ServletException {

        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = new TemporaryFolder();
        folder.create();

        try {
            File archiveFile = createJAR(folder);
            File warFile = createWAR(folder, archiveFile);

            // Instantiate URL stream handler factory to be able to handle war:
            WarURLStreamHandlerFactory.getInstance();

            URL folderResourceURL = new URL("jar:war:" + warFile.toURI().toURL()
                    + "!/" + archiveFile.getName() + "!/VAADIN/folder");

            Assert.assertTrue(
                    "Should be evaluated as a static request because we cannot "
                            + "determine non-file resources within jar files.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            folderResourceURL));

            URL fileResourceURL = new URL("jar:war:" + warFile.toURI().toURL()
                    + "!/" + archiveFile.getName() + "!/VAADIN/file.txt");

            Assert.assertTrue("Should be evaluated as a static request.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            fileResourceURL));
        } finally {
            folder.delete();
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void isAllowedVAADINResource_jarInAJar_detectsAsStaticResources()
            throws IOException, URISyntaxException, ServletException {

        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = new TemporaryFolder();
        folder.create();

        try {
            File archiveFile = createJAR(folder);
            File warFile = createWAR(folder, archiveFile);

            URL folderResourceURL = new URL("jar:" + warFile.toURI().toURL()
                    + "!/" + archiveFile.getName() + "!/VAADIN/folder");

            Assert.assertTrue(
                    "Should be evaluated as a static request because we cannot "
                            + "determine non-file resources within jar files.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            folderResourceURL));

            URL fileResourceURL = new URL("jar:" + warFile.toURI().toURL()
                    + "!/" + archiveFile.getName() + "!/VAADIN/file.txt");

            Assert.assertTrue("Should be evaluated as a static request.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            fileResourceURL));

            URL fileNonStaticResourceURL = new URL(
                    "jar:" + warFile.toURI().toURL() + "!/"
                            + archiveFile.getName() + "!/file.txt");

            Assert.assertFalse(
                    "Should not be evaluated as a static request even within a "
                            + "jar because it's not within 'VAADIN' folder.",
                    servlet.isAllowedVAADINResourceUrl(request,
                            fileNonStaticResourceURL));
        } finally {
            folder.delete();
        }
    }

    private File createWAR(TemporaryFolder folder, File archiveFile)
            throws IOException {
        File warFile = new File(folder.getRoot(), "fake.war");
        warFile.createNewFile();

        ZipOutputStream warOutputStream = new ZipOutputStream(
                getOutputStream(warFile));

        // Create a file to the zip
        warOutputStream.putNextEntry(new ZipEntry(archiveFile.getName()));
        warOutputStream.write(readAllBytes(archiveFile));

        warOutputStream.closeEntry();
        warOutputStream.close();

        return warFile;
    }

    private byte[] readAllBytes(File file) {
        InputStream is;
        try {
            is = new FileInputStream(file);
            int length = (int) file.length();
            char[] buffer = new char[length];
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            reader.read(buffer);
            reader.close();
            return buffer.toString().getBytes();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private FileOutputStream getOutputStream(File file) {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
        }
        return stream;
    }

    /**
     * Performs the resource URL validity check in five threads simultaneously,
     * and ensures that the results match the given expected value.
     *
     * @param servlet
     *            VaadinServlet instance
     * @param request
     *            HttpServletRequest instance (does not need to be properly
     *            initialized)
     * @param resourceURL
     *            the resource URL to validate
     * @param resourceErrorMessage
     *            the error message if the validity check results don't match
     *            the expected value
     * @param expected
     *            expected value from the validity check
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("deprecation")
    private void checkAllowedVAADINResourceConcurrently(
            final VaadinServlet servlet, final HttpServletRequest request,
            final URL resourceURL, final String resourceErrorMessage,
            final boolean expected)
            throws InterruptedException, ExecutionException {
        int THREADS = 5;

        List<Callable<Result>> fileNotResource = new ArrayList<Callable<Result>>();
        for (int i = 0; i < THREADS; i++) {
            Callable<Result> callable = new Callable<Result>() {
                @Override
                public Result call() {
                    try {
                        if (expected != servlet.isAllowedVAADINResourceUrl(
                                request, resourceURL)) {
                            throw new IllegalArgumentException(
                                    resourceErrorMessage);
                        }
                    } catch (Exception e) {
                        return new Result(e);
                    }
                    return new Result(null);
                }
            };
            fileNotResource.add(callable);
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<Result>> futures = executor.invokeAll(fileNotResource);
        List<String> exceptions = new ArrayList<String>();

        executor.shutdown();

        for (Future<Result> resultFuture : futures) {
            Result result = resultFuture.get();
            if (result.exception != null) {
                exceptions.add(result.exception.getMessage());
            }
        }

        assertTrue("There were exceptions in concurrent calls {" + exceptions
                + "}", exceptions.isEmpty());
    }

    private static class Result {
        final Exception exception;

        Result(Exception exception) {
            this.exception = exception;
        }
    }
}
