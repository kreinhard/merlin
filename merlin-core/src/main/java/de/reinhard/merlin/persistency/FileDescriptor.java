package de.reinhard.merlin.persistency;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Information about a file location. Used for auto-matching of template files and template definition files (if
 * the filenames are equal (including path, excluding file extension).
 * <br/>
 * If the template files and definition files aren't stored in a local file system this class can also be used.
 * Directory may represent the area, the relative path the sub area for clustering template files.
 */
public class FileDescriptor implements Cloneable {
    private static Logger log = LoggerFactory.getLogger(FileDescriptor.class);
    private String directory;
    private String relativePath;
    private String filename;
    private Date lastUpdate;
    private String hashId;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        hashId = null;
    }

    /**
     * Sets the property directory as absolute path of the given dir.
     *
     * @param dir
     * @return this for chaining.
     */
    public FileDescriptor setDirectory(Path dir) {
        directory = PersistencyRegistry.getDefault().getCanonicalPathString(dir);
        hashId = null;
        return this;
    }

    /**
     * @return path relative to directory path (excluding filename). Needed for automatching template definition files with same
     * relative path and filename (excluding the file extension).
     */
    public String getRelativePath() {
        return relativePath;
    }

    public FileDescriptor setRelativePath(String relativePath) {
        this.relativePath = relativePath;
        hashId = null;
        return this;
    }

    /**
     * @param path path including file name.
     * @return
     */
    public FileDescriptor setRelativePath(Path path) {
        hashId = null;
        Path dirPath = Paths.get(directory);
        Path filePath = PersistencyRegistry.getDefault().getCanonicalPath(path);
        Path relPath = dirPath.relativize(filePath);
        Path parent = relPath.getParent();
        this.relativePath = parent != null ? parent.toString() : "";
        this.filename = path.getFileName().toString();
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public FileDescriptor setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * For syncing purposes: do only update if any modification of the representing file is newer than the last update.
     *
     * @return Date of last update of this object (if set).
     */
    @Transient
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public FileDescriptor setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    @Transient
    public String getCanonicalPathString() {
        Path path = getCanonicalPath();
        return PersistencyRegistry.getDefault().getCanonicalPathString(path);
    }

    public Path getCanonicalPath() {
        Path path = Paths.get(directory, relativePath, filename);
        return path;
    }

    /**
     * Checks if the filename matches the other filename (excluding the file extension).
     *
     * @param other
     * @return true, if the filenames matches and the files are both placed in the same directory (including relative path).
     */
    public boolean matches(FileDescriptor other) {
        if (!StringUtils.equals(directory, other.directory)) {
            return false;
        }
        if (!StringUtils.equals(relativePath, other.relativePath)) {
            return false;
        }
        if (filename == null || other.filename == null) {
            return false;
        }
        String filenameWithoutExtension = FilenameUtils.removeExtension(filename).trim().toLowerCase();
        String otherFilenameWithoutExtension = FilenameUtils.removeExtension(other.filename).trim().toLowerCase();
        return filenameWithoutExtension.equals(otherFilenameWithoutExtension);
    }

    /**
     * Checks weather the given file was modified after last update.
     *
     * @param file
     * @return true, if the gifen file was modified after last update or if last update is not set.
     */
    public boolean isModified(Path file) {
        if (lastUpdate == null) {
            return true;
        }
        Date lastModified = new Date(PersistencyRegistry.getDefault().getLastModified(file));
        return lastModified.after(lastUpdate);
    }

    @Override
    public boolean equals(Object obj) {
        FileDescriptor other = (FileDescriptor) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(directory, other.directory);
        eb.append(relativePath, other.relativePath);
        eb.append(filename, other.filename);
        return eb.isEquals();
    }

    /**
     * Base64 encoded Hash.
     *
     * @return
     */
    public String getBiUniqueHashId() {
        if (hashId != null) {
            return hashId;
        }
        hashId = PersistencyRegistry.getDefault().getBiUniqueHashId(Paths.get(directory, relativePath, filename));
        return hashId;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(directory).append(relativePath).append(filename);
        return hcb.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tos.append("directory", directory);
        tos.append("relativePath", relativePath);
        tos.append("filename", filename);
        return tos.toString();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " isn't cloneable: " + ex.getMessage(), ex);
        }
    }
}
