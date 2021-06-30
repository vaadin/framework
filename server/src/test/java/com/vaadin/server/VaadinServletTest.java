package com.vaadin.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Tests for {@link VaadinServlet}.
 * <p>
 * NOTE: some of these tests are not thread safe. Add
 * {@code @net.jcip.annotations.NotThreadSafe} to this class if this module is
 * converted to use {@code maven-failsafe-plugin} for parallelization.
 */
public class VaadinServletTest {

    @After
    public void tearDown() {
        Assert.assertNull(VaadinService.getCurrent());
    }

    @Test
    public void testGetLastPathParameter() {
        assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com"));
        assertEquals(";a",
                VaadinServlet.getLastPathParameter("http://myhost.com;a"));
        assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com/hello"));
        assertEquals(";b=c", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;b=c"));
        assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com/hello/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;a/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;a=1/"));
        assertEquals(";b", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b"));
        assertEquals(";b=1", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1"));
        assertEquals(";b=1,c=2", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1,c=2"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1,c=2/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;a/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;a=1/"));
        assertEquals(";b", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b"));
        assertEquals(";b=1", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1"));
        assertEquals(";b=1,c=2", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2/"));
    }

    @Test
    public void getStaticFilePath() {
        VaadinServlet servlet = new VaadinServlet();

        // Mapping: /VAADIN/*
        // /VAADIN
        assertNull(servlet
                .getStaticFilePath(createServletRequest("/VAADIN", null)));
        // /VAADIN/ - not really sensible but still interpreted as a resource
        // request
        assertEquals("/VAADIN/", servlet
                .getStaticFilePath(createServletRequest("/VAADIN", "/")));
        // /VAADIN/vaadinBootstrap.js
        assertEquals("/VAADIN/vaadinBootstrap.js", servlet.getStaticFilePath(
                createServletRequest("/VAADIN", "/vaadinBootstrap.js")));
        // /VAADIN/foo bar.js
        assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(
                createServletRequest("/VAADIN", "/foo bar.js")));
        // /VAADIN/.. - not normalized and disallowed in this method
        assertEquals("/VAADIN/..", servlet
                .getStaticFilePath(createServletRequest("/VAADIN", "/..")));

        // Mapping: /*
        // /
        assertNull(servlet.getStaticFilePath(createServletRequest("", null)));
        // /VAADIN
        assertNull(
                servlet.getStaticFilePath(createServletRequest("", "/VAADIN")));
        // /VAADIN/
        assertEquals("/VAADIN/", servlet
                .getStaticFilePath(createServletRequest("", "/VAADIN/")));
        // /VAADIN/foo bar.js
        assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(
                createServletRequest("", "/VAADIN/foo bar.js")));
        // /VAADIN/.. - not normalized and disallowed in this method
        assertEquals("/VAADIN/..", servlet
                .getStaticFilePath(createServletRequest("", "/VAADIN/..")));
        // /BAADIN/foo.js
        assertNull(servlet
                .getStaticFilePath(createServletRequest("", "/BAADIN/foo.js")));

        // Mapping: /myservlet/*
        // /myservlet
        assertNull(servlet
                .getStaticFilePath(createServletRequest("/myservlet", null)));
        // /myservlet/VAADIN
        assertNull(servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN")));
        // /myservlet/VAADIN/
        assertEquals("/VAADIN/", servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN/")));
        // /myservlet/VAADIN/foo bar.js
        assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN/foo bar.js")));
        // /myservlet/VAADIN/.. - not normalized and disallowed in this method
        assertEquals("/VAADIN/..", servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN/..")));
        // /myservlet/BAADIN/foo.js
        assertNull(servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/BAADIN/foo.js")));

    }

    @Test
    @SuppressWarnings("deprecation")
    public void directoryIsNotResourceRequest() throws Exception {
        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = TemporaryFolder.builder().build();
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
            Path tempArchive = archiveFile.toPath();
            String tempArchivePath = tempArchive.toString().replaceAll("\\\\",
                    "/");

            assertFalse(
                    "Folder 'VAADIN' in jar should not be an allowed resource.",
                    servlet.isAllowedVAADINResourceUrl(request, new URL(
                            "jar:file:///" + tempArchivePath + "!/VAADIN")));

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
                                    + "!/VAADIN/folder")));

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
        assertTrue("Can not run concurrently with other test",
                VaadinServlet.OPEN_FILE_SYSTEMS.isEmpty());

        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = TemporaryFolder.builder().build();
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
        assertTrue("Can not run concurrently with other test",
                VaadinServlet.OPEN_FILE_SYSTEMS.isEmpty());

        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = TemporaryFolder.builder().build();
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

    @Test
    public void openingJarFileSystemForDifferentFilesInSameJar_existingFileSystemIsUsed()
            throws IOException, URISyntaxException, ServletException {
        assertTrue("Can not run concurrently with other test",
                VaadinServlet.OPEN_FILE_SYSTEMS.isEmpty());

        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());

        TemporaryFolder folder = TemporaryFolder.builder().build();
        folder.create();

        try {
            File archiveFile = createJAR(folder);
            String tempArchivePath = archiveFile.toPath().toString()
                    .replaceAll("\\\\", "/");

            URL folderResourceURL = new URL(
                    "jar:file:///" + tempArchivePath + "!/VAADIN");

            URL fileResourceURL = new URL(
                    "jar:file:///" + tempArchivePath + "!/file.txt");

            servlet.getFileSystem(folderResourceURL.toURI());
            servlet.getFileSystem(fileResourceURL.toURI());

            assertEquals("Same file should be marked for both resources",
                    (Integer) 2, VaadinServlet.OPEN_FILE_SYSTEMS.entrySet()
                            .iterator().next().getValue());
            servlet.closeFileSystem(folderResourceURL.toURI());
            assertEquals("Closing resource should be removed from jar uri",
                    (Integer) 1, VaadinServlet.OPEN_FILE_SYSTEMS.entrySet()
                            .iterator().next().getValue());
            servlet.closeFileSystem(fileResourceURL.toURI());
            assertTrue("Closing last resource should clear marking",
                    VaadinServlet.OPEN_FILE_SYSTEMS.isEmpty());

            try {
                FileSystems.getFileSystem(folderResourceURL.toURI());
                fail("Jar FileSystem should have been closed");
            } catch (FileSystemNotFoundException fsnfe) {
                // This should happen as we should not have an open FileSystem
                // here.
            }
        } finally {
            folder.delete();
        }
    }

    @Test
    public void concurrentRequestsToJarResources_checksAreCorrect()
            throws IOException, InterruptedException, ExecutionException,
            URISyntaxException, ServletException {
        assertTrue("Can not run concurrently with other test",
                VaadinServlet.OPEN_FILE_SYSTEMS.isEmpty());

        VaadinServlet servlet = new VaadinServlet();
        servlet.init(new MockServletConfig());
        // this request isn't actually used for anything within the
        // isAllowedVAADINResourceUrl calls, no need to configure it
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        TemporaryFolder folder = TemporaryFolder.builder().build();
        folder.create();

        try {
            File archiveFile = createJAR(folder);
            String tempArchivePath = archiveFile.toPath().toString()
                    .replaceAll("\\\\", "/");

            URL fileNotResourceURL = new URL(
                    "jar:file:///" + tempArchivePath + "!/file.txt");
            String fileNotResourceErrorMessage = "File file.text outside "
                    + "folder 'VAADIN' in jar should not be a static resource.";

            checkAllowedVAADINResourceConcurrently(servlet, request,
                    fileNotResourceURL, fileNotResourceErrorMessage, false);
            ensureFileSystemsCleared(fileNotResourceURL);

            URL folderNotResourceURL = new URL(
                    "jar:file:///" + tempArchivePath + "!/VAADIN");
            String folderNotResourceErrorMessage = "Folder 'VAADIN' in "
                    + "jar should not be a static resource.";

            checkAllowedVAADINResourceConcurrently(servlet, request,
                    folderNotResourceURL, folderNotResourceErrorMessage, false);
            ensureFileSystemsCleared(folderNotResourceURL);

            URL fileIsResourceURL = new URL(
                    "jar:file:///" + tempArchivePath + "!/VAADIN/file.txt");
            String fileIsResourceErrorMessage = "File 'file.txt' inside "
                    + "VAADIN folder within jar should be a static resource.";

            checkAllowedVAADINResourceConcurrently(servlet, request,
                    fileIsResourceURL, fileIsResourceErrorMessage, true);
            ensureFileSystemsCleared(fileIsResourceURL);
        } finally {
            folder.delete();
        }
    }

    private HttpServletRequest createServletRequest(String servletPath,
            String pathInfo) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn(servletPath);
        Mockito.when(request.getPathInfo()).thenReturn(pathInfo);
        Mockito.when(request.getRequestURI()).thenReturn("/context" + pathInfo);
        Mockito.when(request.getContextPath()).thenReturn("/context");
        return request;
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
        Path tempArchive = archiveFile.toPath();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(
                Files.newOutputStream(tempArchive))) {
            // Create a file to the zip
            zipOutputStream.putNextEntry(new ZipEntry("/file.txt"));
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
        }
        return archiveFile;
    }

    private File createWAR(TemporaryFolder folder, File archiveFile)
            throws IOException {
        Path tempArchive = archiveFile.toPath();
        File warFile = new File(folder.getRoot(), "fake.war");
        warFile.createNewFile();
        Path warArchive = warFile.toPath();

        try (ZipOutputStream warOutputStream = new ZipOutputStream(
                Files.newOutputStream(warArchive))) {
            // Create a file to the zip
            warOutputStream.putNextEntry(new ZipEntry(archiveFile.getName()));
            warOutputStream.write(Files.readAllBytes(tempArchive));

            warOutputStream.closeEntry();
        }
        return warFile;
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
    private void checkAllowedVAADINResourceConcurrently(VaadinServlet servlet,
            HttpServletRequest request, URL resourceURL,
            String resourceErrorMessage, boolean expected)
            throws InterruptedException, ExecutionException {
        int THREADS = 5;

        List<Callable<Result>> fileNotResource = IntStream.range(0, THREADS)
                .mapToObj(i -> {
                    Callable<Result> callable = () -> {
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
                    };
                    return callable;
                }).collect(Collectors.toList());

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<Result>> futures = executor.invokeAll(fileNotResource);
        List<String> exceptions = new ArrayList<>();

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

    private void ensureFileSystemsCleared(URL fileResourceURL)
            throws URISyntaxException {
        assertFalse("URI should have been cleared",
                VaadinServlet.OPEN_FILE_SYSTEMS
                        .containsKey(fileResourceURL.toURI()));
        try {
            FileSystems.getFileSystem(fileResourceURL.toURI());
            fail("FileSystem for file resource should be closed");
        } catch (FileSystemNotFoundException fsnfe) {
            // This should happen as we should not have an open FileSystem
            // here.
        }
    }

    private static class Result {
        final Exception exception;

        Result(Exception exception) {
            this.exception = exception;
        }
    }
}
