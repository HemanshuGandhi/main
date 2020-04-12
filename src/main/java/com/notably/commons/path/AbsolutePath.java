package com.notably.commons.path;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.notably.commons.path.exceptions.InvalidPathException;
import com.notably.model.block.Title;

/**
 * Represents the Path to a Block, starting from the Root node.
 */
public class AbsolutePath implements Path, Comparable<AbsolutePath> {
    public static final String INVALID_ABSOLUTE_PATH = "Invalid absolute path";
    public static final String VALIDATION_REGEX = "\\/|(\\/" + Title.VALIDATION_REGEX + ")+\\/?";

    private final List<String> components;

    private AbsolutePath(String absolutePathString) {
        requireNonNull(absolutePathString);
        this.components = new ArrayList<>();
        for (String obj : absolutePathString.split("/")) {
            if (!obj.trim().equals("")) {
                this.components.add(obj.trim());
            }
        }
    }

    public AbsolutePath(List<String> absolutePathList) {
        requireNonNull(absolutePathList);
        this.components = absolutePathList;
    }

    /**
     * Check if a supplied string is a ValidAbsolutePath
     * @param absolutePathString
     * @return
     */
    public static boolean isValidAbsolutePath(String absolutePathString) {
        requireNonNull(absolutePathString);
        return absolutePathString.matches(VALIDATION_REGEX);
    }

    /**
     * Creates an absolute path from a string.
     * @param absolutePathString used to create an absolutePath.
     * @return Absolute Path.
     */
    public static AbsolutePath fromString(String absolutePathString) {
        requireNonNull(absolutePathString);
        if (!isValidAbsolutePath(absolutePathString)) {
            throw new InvalidPathException(INVALID_ABSOLUTE_PATH);
        }
        return new AbsolutePath(absolutePathString);
    }

    /**
     * Creates an absolute path from a list of components.
     * @param absoluteComponents used to create an absolutePath.
     * @return Absolute Path.
     */
    public static AbsolutePath fromComponents(List<String> absoluteComponents) {
        requireNonNull(absoluteComponents);
        return new AbsolutePath(absoluteComponents);
    }


    /**
     * Convert from relative path to absolute path.
     * @param relativePath used to convert to absolute path.
     * @param currentWorkingPath of the current working directory.
     * @return the converted AbsolutePath.
     */
    public static AbsolutePath fromRelativePath(RelativePath relativePath,
            AbsolutePath currentWorkingPath) {
        requireNonNull(relativePath);
        requireNonNull(currentWorkingPath);
        List<String> relativeComponents = relativePath.getComponents();
        List<String> base = currentWorkingPath.getComponents();
        return new AbsolutePath(PathUtil.normaliseRelativeComponents(base, relativeComponents));
    }

    /**
     * Convert Absolute path to Relative path.
     * @param currentWorkingPath of the current working directory.
     * @return the converted RelativePath
     */
    public RelativePath toRelativePath(AbsolutePath currentWorkingPath) {
        requireNonNull(currentWorkingPath);
        return RelativePath.fromAbsolutePath(this, currentWorkingPath);
    }

    @Override
    public List<String> getComponents() {
        return this.components;
    }

    @Override
    public String getStringRepresentation() {
        return "/" + String.join("/", this.components);
    }

    @Override
    public int compareTo(AbsolutePath path) {
        requireNonNull(path);

        if (components.size() != path.components.size()) {
            return components.size() - path.components.size();
        }

        int i = 0;
        while (i < components.size() && i < path.components.size()) {
            if (!components.get(i).equalsIgnoreCase(path.components.get(i))) {
                return components.get(i).compareToIgnoreCase(path.components.get(i));
            }
            i++;
        }

        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbsolutePath)) {
            return false;
        }

        AbsolutePath another = (AbsolutePath) object;
        return compareTo(another) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.components);
    }

    @Override
    public String toString() {
        return getStringRepresentation();
    }
}

