# resbundler

Multi-module Maven project that builds `resbundler`, a small CLI for packing
binary payload files into a single `.bin` bundle and verifying payload
integrity. The CLI is compiled to a fully static Linux binary with GraalVM
Native Image and musl.

## Modules

- `resbundler-core` — bundle format, writer, reader/verifier.
- `resbundler-cli` — command line frontend (`pack` / `info` / `verify`).

## Build

Inside the build container (`ghcr.io/gg22g2/graalvm-jdk-musl-maven`):

```sh
mvn -B -Pnative,native-linux-musl -DskipTests package
```

The static binary lands in `resbundler-cli/target/resbundler`.
Release artifacts ship the binary together with a prebuilt payload bundle
under `lib/`.

## CI

`.github/workflows/build-service.yml` runs the build in the container image
and stores the output as a workflow artifact. See the workflow file for
configuration details.
