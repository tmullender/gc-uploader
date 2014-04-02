package co.escapeideas.gc.uploader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 01/04/14
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
public class Watcher {
    private final List<String> accepetedExtensions;
    private final File newDirectory;

    public Watcher(Configuration configuration) {
        accepetedExtensions = getAcceptableExtensions(configuration);
        newDirectory = getNewDirectory(configuration);
    }

    private File getNewDirectory(Configuration configuration) {
        return new File(configuration.getNewDirectory());
    }

    private List<String> getAcceptableExtensions(Configuration configuration) {
        return Arrays.asList(configuration.getAcceptableExtensions().split(","));
    }

    public File[] findNewFiles() {
        if (!newDirectory.exists()){
            newDirectory.mkdirs();
        }
        return newDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return accepetedExtensions.contains(getExtension(name));
            }
        });
    }
}
