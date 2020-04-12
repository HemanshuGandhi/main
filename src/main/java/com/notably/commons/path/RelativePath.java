package com.notably.commons.path;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.notably.commons.path.exceptions.InvalidPathException;
import com.notably.model.block.Title;

/**
 * Represents a path to the block relative to the current directory.
 */
public class RelativePath implements Path, Comparable<RelativePath> {
    public static final String INVALID_RELATIVE_PATH = "Invalid relative path";
    public static final String VALIDATION_REGEX =
            "(\\.|\\..|" + Title.VALIDATION_REGEX + ")(\\/(\\.|\\..|" + Title.VALIDATION_REGEX + "))*\\/?";

    private final List<String> components;

    private RelativePath(String relativePathString) {
        requireNonNull(relativePathString);
        this.components = new ArrayList<>();
        for (String obj : relativePathString.split("/")) {
            if (!obj.trim().equals("")) {
                this.components.add(obj.trim());
            }
        }
    }

    private RelativePath(List<String> components) {
        requireNonNull(components);
        this.components = components;
    }

    /**
     * Check if string is a valid relative path.
     * @param relativePathString String used to check validity.
     * @return returns true if String is a valid relative Path.
     */
    public static boolean isValidRelativePath(String relativePathString) {
        requireNonNull(relativePathString);
        return relativePathString.matches(VALIDATION_REGEX);
    }

    /**
     * Instantiate a RelativePath object if the string is valid.
     * @param relativePathString used to create RelativePath.
     * @return the converted RelativePath object.
     */
    public static RelativePath fromString(String relativePathString) {
        requireNonNull(relativePathString);
        if (!isValidRelativePath(relativePathString)) {
            throw new InvalidPathException("Not a relative path");
        }
        return new RelativePath(relativePathString);

    }

    /**
     * Instantiate a relativePath from components.
     * @param components to create a relativePath.
     * @return a relativePath object.
     */
    public static RelativePath fromComponents(List<String> components) {
        requireNonNull(components);
        return new RelativePath(components);

    }

    /**
     * Convert from absolute path to relative path.
     * @param absolutePath to convert to relative path.
     * @param currentWorkingPath of the current working directory.
     * @return the converted relative path.
     */
    public static RelativePath fromAbsolutePath(AbsolutePath absolutePath,
            AbsolutePath currentWorkingPath) {
        requireNonNull(absolutePath);
        requireNonNull(currentWorkingPath);
        int sizeOfCurrent = currentWorkingPath.getComponents().size();
        int sizeOfAbsolute = absolutePath.getComponents().size();
        List<String> componentsToRelative;
        if (sizeOfCurrent > sizeOfAbsolute) {
            componentsToRelative = Collections.nCopies(sizeOfAbsolute - sizeOfCurrent, "..");
        } else {
            componentsToRelative = absolutePath.getComponents().subList(sizeOfCurrent, sizeOfAbsolute);
        }
        return fromComponents(componentsToRelative);
    }

    /**
     * Convert relative path to absolute path.
     * @param currentWorkingPath of the current working directory.
     * @return the absolute path of the relative path.
     */
    public AbsolutePath toAbsolutePath(AbsolutePath currentWorkingPath) {
        requireNonNull(currentWorkingPath);
        return AbsolutePath.fromRelativePath(this, currentWorkingPath);
    }

    @Override
    public List<String> getComponents() {
        return this.components;
    }

    @Override
    public String getStringRepresentation() {
        return String.join("/", this.components);
    }

    @Override
    public int compareTo(RelativePath path) {
        requireNonNull(path);

        List<String> components = PathUtil.normaliseRelativeComponents(this.components);
        List<String> pathComponents = PathUtil.normaliseRelativeComponents(path.components);

        if (components.size() != pathComponents.size()) {
            return components.size() - pathComponents.size();
        }

        int i = 0;
        while (i < components.size() && i < pathComponents.size()) {
            if (!components.get(i).equalsIgnoreCase(pathComponents.get(i))) {
                return components.get(i).compareToIgnoreCase(pathComponents.get(i));
            }
            i++;
        }

        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RelativePath)) {
            return false;
        }

        RelativePath another = (RelativePath) object;
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

