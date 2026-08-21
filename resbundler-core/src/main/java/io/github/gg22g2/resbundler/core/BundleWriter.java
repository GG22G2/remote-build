package io.github.gg22g2.resbundler.core;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Writes payload bundles. */
public final class BundleWriter {

    private BundleWriter() {
    }

    /**
     * Packs every regular file directly inside {@code dir} into {@code outFile}.
     *
     * @return number of payloads written
     */
    public static int pack(Path dir, Path outFile) throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .forEach(files::add);
        }

        List<String> names = new ArrayList<>(files.size());
        List<Long> sizes = new ArrayList<>(files.size());
        List<byte[]> hashes = new ArrayList<>(files.size());
        for (Path file : files) {
            names.add(file.getFileName().toString());
            sizes.add(Files.size(file));
            hashes.add(Hashes.sha256(Files.readAllBytes(file)));
        }

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(outFile)))) {
            out.writeInt(BundleFormat.MAGIC);
            out.writeInt(BundleFormat.VERSION);
            out.writeInt(files.size());
            for (int i = 0; i < files.size(); i++) {
                out.writeUTF(names.get(i));
                out.writeLong(sizes.get(i));
                out.write(hashes.get(i));
            }
            for (Path file : files) {
                try (InputStream in = Files.newInputStream(file)) {
                    in.transferTo(out);
                }
            }
        }
        return files.size();
    }
}
