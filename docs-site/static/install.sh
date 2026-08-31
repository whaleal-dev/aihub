#!/usr/bin/env sh
set -eu

AIHUB_HOME="${AIHUB_HOME:-$HOME/.aihub}"
AIHUB_BIN_DIR="$AIHUB_HOME/bin"
AIHUB_LIB_DIR="$AIHUB_HOME/lib"
AIHUB_VERSION_FILE="$AIHUB_HOME/version.txt"
MAVEN_REPO="${AIHUB_MAVEN_REPO:-https://repo.maven.apache.org/maven2}"
METADATA_URL="$MAVEN_REPO/com/whaleal/aihub-cli/maven-metadata.xml"

say() {
  printf '%s\n' "$*"
}

fail() {
  printf 'aihub installer: %s\n' "$*" >&2
  exit 1
}

have_cmd() {
  command -v "$1" >/dev/null 2>&1
}

skip_path_update() {
  case "${AIHUB_SKIP_PATH_UPDATE:-}" in
    1|true|TRUE|yes|YES)
      return 0
      ;;
    *)
      return 1
      ;;
  esac
}

download_to() {
  url="$1"
  output="$2"
  if have_cmd curl; then
    curl -fsSL "$url" -o "$output"
    return
  fi
  if have_cmd wget; then
    wget -qO "$output" "$url"
    return
  fi
  fail "curl or wget is required"
}

download_text() {
  url="$1"
  if have_cmd curl; then
    curl -fsSL "$url"
    return
  fi
  if have_cmd wget; then
    wget -qO- "$url"
    return
  fi
  fail "curl or wget is required"
}

resolve_version() {
  if [ -n "${AIHUB_VERSION:-}" ]; then
    printf '%s' "$AIHUB_VERSION"
    return
  fi

  metadata="$(download_text "$METADATA_URL" | tr -d '\r\n')"
  version="$(printf '%s' "$metadata" | sed -n 's:.*<release>\([^<]*\)</release>.*:\1:p')"
  if [ -z "$version" ]; then
    version="$(printf '%s' "$metadata" | sed -n 's:.*<latest>\([^<]*\)</latest>.*:\1:p')"
  fi
  if [ -z "$version" ]; then
    fail "unable to resolve latest aihub-cli version from Maven metadata"
  fi
  printf '%s' "$version"
}

java_major_version() {
  version="$(
    java -version 2>&1 \
      | awk -F '"' '/version/ {print $2; exit}'
  )"
  if [ -z "$version" ]; then
    fail "unable to detect Java version"
  fi
  case "$version" in
    1.*)
      printf '%s' "$version" | cut -d. -f2
      ;;
    *)
      printf '%s' "$version" | cut -d. -f1
      ;;
  esac
}

ensure_java() {
  if ! have_cmd java; then
    fail "Java 8+ is required. Install Java first, then rerun this installer."
  fi
  major="$(java_major_version)"
  if [ "$major" -lt 8 ]; then
    fail "Java 8+ is required. Current Java major version: $major"
  fi
}

write_launcher() {
  launcher="$AIHUB_BIN_DIR/aihub"
  install_home_escaped="$(printf '%s' "$AIHUB_HOME" | sed "s/'/'\\\\''/g")"
  {
    printf '%s\n' '#!/usr/bin/env sh'
    printf '%s\n' 'set -eu'
    printf '\n'
    printf "INSTALL_HOME='%s'\n" "$install_home_escaped"
    cat <<'EOF'
AIHUB_HOME="${AIHUB_HOME:-$INSTALL_HOME}"
JAVA_BIN="${AIHUB_JAVA:-java}"
JAR_PATH="$AIHUB_HOME/lib/aihub-cli.jar"

if [ ! -f "$JAR_PATH" ]; then
  printf 'aihub launcher: missing %s\n' "$JAR_PATH" >&2
  exit 1
fi

if [ -n "${AIHUB_JAVA_OPTS:-}" ]; then
  # shellcheck disable=SC2086
  exec "$JAVA_BIN" $AIHUB_JAVA_OPTS -jar "$JAR_PATH" "$@"
fi

exec "$JAVA_BIN" -jar "$JAR_PATH" "$@"
EOF
  } > "$launcher"
  chmod +x "$launcher"
}

path_contains() {
  case ":$PATH:" in
    *":$1:"*) return 0 ;;
    *) return 1 ;;
  esac
}

ensure_path() {
  if skip_path_update; then
    say "Skipping PATH update because AIHUB_SKIP_PATH_UPDATE is set."
    return
  fi

  if path_contains "$AIHUB_BIN_DIR"; then
    say "aihub is already available on PATH in this shell."
    return
  fi

  shell_name="${SHELL:-}"
  case "$shell_name" in
    */zsh) rc_file="$HOME/.zshrc" ;;
    */bash) rc_file="$HOME/.bashrc" ;;
    *) rc_file="$HOME/.profile" ;;
  esac

  export_line="export PATH=\"$AIHUB_BIN_DIR:\$PATH\""
  if [ -f "$rc_file" ] && grep -F "$export_line" "$rc_file" >/dev/null 2>&1; then
    say "PATH entry already present in $rc_file"
    return
  fi

  {
    printf '\n# aihub installer\n'
    printf '%s\n' "$export_line"
  } >> "$rc_file"
  say "Added $AIHUB_BIN_DIR to PATH in $rc_file"
  say "Run: export PATH=\"$AIHUB_BIN_DIR:\$PATH\""
}

main() {
  ensure_java

  version="$(resolve_version)"
  jar_url="$MAVEN_REPO/com/whaleal/aihub-cli/$version/aihub-cli-$version-jar-with-dependencies.jar"
  tmp_jar="$AIHUB_LIB_DIR/aihub-cli.jar.tmp"
  jar_path="$AIHUB_LIB_DIR/aihub-cli.jar"

  say "Installing aihub-cli $version"
  mkdir -p "$AIHUB_BIN_DIR" "$AIHUB_LIB_DIR"
  download_to "$jar_url" "$tmp_jar"
  mv "$tmp_jar" "$jar_path"
  printf '%s\n' "$version" > "$AIHUB_VERSION_FILE"
  write_launcher
  ensure_path

  say ""
  say "Installed aihub-cli $version to $AIHUB_HOME"
  say "Restart your shell if 'aihub' is not found immediately."
  say "Then run: aihub --help"
}

main "$@"
