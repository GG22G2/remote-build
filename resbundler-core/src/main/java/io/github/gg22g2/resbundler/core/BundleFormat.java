package io.github.gg22g2.resbundler.core;

/**
 * On-disk layout of a payload bundle (.bin):
 * magic (4 bytes) | version (4 bytes) | entry count (4 bytes) | index | payload bytes.
 * Each index entry: name (modified UTF) | payload size (8 bytes) | SHA-256 (32 bytes).
 */
public final class BundleFormat {

    private BundleFormat() {
    }

    /** Magic bytes: "RBND". */
    public static final int MAGIC = 0x52424E44;
    public static final int VERSION = 1;
    public static final int HASH_BYTES = 32;
}
