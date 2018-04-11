package com.vaadin.tests.tb3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class ChangedTB3TestLocator extends TB3TestLocator {

    @Override
    protected <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
            String basePackage, String[] ignoredPackages) throws IOException {

        return getChangedTestClasses(baseClass);
    }

    protected List<String> getChangedFilePaths() {
        List<String> filePaths = new ArrayList<>();

        for (DiffEntry diff : getDiffs()) {
            if (diff.getChangeType() != ChangeType.DELETE) {
                filePaths.add(diff.getNewPath());
                System.out.println("Using changed file: " + diff.getNewPath());
            }
        }

        return filePaths;
    }

    private List<DiffEntry> getDiffs() {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setWorkTree(new File("."))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();

            List<DiffEntry> diffsInWorkingTree = getDiffsInWorkingTree(
                    repository);

            if (diffsInWorkingTree.isEmpty()) {
                return getDiffsInHead(repository);
            }

            return diffsInWorkingTree;

        } catch (IOException | NoWorkTreeException | GitAPIException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DiffEntry> getDiffsInWorkingTree(Repository repository)
            throws GitAPIException {
        Git git = new Git(repository);
        DiffCommand diffCommand = git.diff();

        List<DiffEntry> diffsInWorkingTree = new ArrayList<>();

        for (DiffEntry diff : diffCommand.call()) {
            if (pathIsExcluded(diff.getNewPath())) {
                continue;
            }

            diffsInWorkingTree.add(diff);
        }

        return diffsInWorkingTree;
    }

    private boolean pathIsExcluded(String path) {
        // Exclude temporary junit files and screenshots.
        return path.startsWith("uitest/junit")
                || getScreenshotDirectory().contains(path);
    }

    private String getScreenshotDirectory() {
        return PrivateTB3Configuration
                .getProperty(PrivateTB3Configuration.SCREENSHOT_DIRECTORY);
    }

    private List<DiffEntry> getDiffsInHead(Repository repository)
            throws AmbiguousObjectException, IncorrectObjectTypeException,
            IOException, MissingObjectException {
        RevWalk rw = new RevWalk(repository);
        ObjectId head = repository.resolve(Constants.HEAD);
        RevCommit commit = rw.parseCommit(head);
        RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repository);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);
        List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

        return diffs;
    }

    private <T> List<Class<? extends T>> getChangedTestClasses(
            Class<T> baseClass) {
        List<String> changedTestFilePaths = getTestFilePaths();
        List<Class<? extends T>> testClasses = new ArrayList<>();

        for (String filePath : changedTestFilePaths) {
            String path = filePath.replace("uitest/src/", "").replace(".java",
                    "");
            String className = path.replace("/", ".");
            addClassIfMatches(testClasses, className, baseClass);
        }

        return testClasses;
    }

    private List<String> getTestFilePaths() {
        List<String> changedTestFilePaths = new ArrayList<>();

        for (String filePath : getChangedFilePaths()) {
            if (filePath.toLowerCase(Locale.ROOT).startsWith("uitest")
                    && filePath.toLowerCase(Locale.ROOT).endsWith(".java")) {
                changedTestFilePaths.add(filePath);
            }
        }

        return changedTestFilePaths;
    }

}
