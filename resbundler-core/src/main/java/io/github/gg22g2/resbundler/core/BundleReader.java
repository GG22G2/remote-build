package io.github.gg22g2.resbundler.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/** Reads and verifies payload bundles. */
public final class BundleReader {

    private BundleReader() {
    }

    /** Reads the index section of {@code bundle}. */
    public static List<BundleEntry> index(Path bundle) throws IOException {
        try (DataInputStream in = open(bundle)) {
            int count = readHeader(in);
            return readIndex(in, count);
        }
    }

    /** Recomputes every payload hash; returns true when all match the index. */
    public static boolean verify(Path bundle) throws IOException {
        try (DataInputStream in = open(bundle)) {
            int count = readHeader(in);
            List<BundleEntry> entries = readIndex(in, count);
            byte[] buffer = new byte[8192];
            for (BundleEntry entry : entries) {
                MessageDigest digest = newDigest();
                long remaining = entry.getSize();
                while (remaining > 0) {
                    int read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (read < 0) {
                        return false;
                    }
                    digest.update(buffer, 0, read);
                    remaining -= read;
                }
                if (!Hashes.toHex(digest.digest()).equals(entry.getSha256())) {
                    return false;
                }
            }
            return true;
        }
    }

    private static DataInputStream open(Path bundle) throws IOException {
        return new DataInputStream(new BufferedInputStream(Files.newInputStream(bundle)));
    }

    private static int readHeader(DataInputStream in) throws IOException {
        int magic = in.readInt();
        if (magic != BundleFormat.MAGIC) {
            throw new IOException("not a payload bundle (bad magic)");
        }
        int version = in.readInt();
        if (version != BundleFormat.VERSION) {
            throw new IOException("unsupported bundle version: " + version);
        }
        return in.readInt();
    }

    private static List<BundleEntry> readIndex(DataInputStream in, int count) throws IOException {
        List<BundleEntry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String name = in.readUTF();
            long size = in.readLong();
            byte[] hash = new byte[BundleFormat.HASH_BYTES];
            in.readFully(hash);
            entries.add(new BundleEntry(name, size, Hashes.toHex(hash)));
        }
        return entries;
    }

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
