package io.github.gg22g2.resbundler.cli;

import io.github.gg22g2.resbundler.core.BundleEntry;
import io.github.gg22g2.resbundler.core.BundleReader;
import io.github.gg22g2.resbundler.core.BundleWriter;

import java.nio.file.Path;
import java.util.List;

/**
 * resbundler CLI: packs directories of payload blobs into a single .bin
 * bundle and verifies bundle integrity.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }
        try {
            switch (args[0]) {
                case "pack" -> {
                    require(args, 3);
                    int count = BundleWriter.pack(Path.of(args[1]), Path.of(args[2]));
                    System.out.println("packed " + count + " payload(s) into " + args[2]);
                }
                case "info" -> {
                    require(args, 2);
                    List<BundleEntry> entries = BundleReader.index(Path.of(args[1]));
                    System.out.println(entries.size() + " payload(s):");
                    entries.forEach(e -> System.out.println("  " + e));
                }
                case "verify" -> {
                    require(args, 2);
                    boolean ok = BundleReader.verify(Path.of(args[1]));
                    System.out.println(ok ? "OK" : "CORRUPT");
                    if (!ok) {
                        System.exit(2);
                    }
                }
                default -> {
                    usage();
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void require(String[] args, int count) {
        if (args.length != count) {
            usage();
            System.exit(1);
        }
    }

    private static void usage() {
        System.err.println("usage: resbundler <command> [args]");
        System.err.println("  pack <dir> <out.bin>   pack payloads into one bundle");
        System.err.println("  info <bundle.bin>      list bundle index");
        System.err.println("  verify <bundle.bin>    verify payload checksums");
    }
}
