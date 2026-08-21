package io.github.gg22g2.resbundler.core;

/** Metadata describing one payload stored inside a bundle. */
public final class BundleEntry {

    private final String name;
    private final long size;
    private final String sha256;

    public BundleEntry(String name, long size, String sha256) {
        this.name = name;
        this.size = size;
        this.sha256 = sha256;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getSha256() {
        return sha256;
    }

    @Override
    public String toString() {
        return name + " (" + size + " bytes, sha256=" + sha256 + ")";
    }
}
