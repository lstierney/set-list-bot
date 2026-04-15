# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Project Does

SetListBot is a Spring Boot application that converts Word (.docx) setlists into [XSPF](https://www.xspf.org/) playlists. The user uploads a `.docx` file listing song titles, the app fuzzy-matches them against audio files on disk, and returns a ready-to-use XSPF playlist for VLC (or returns a JSON list of unmatched songs if any fail to match).

## Important Rules

- **Never push to remote.** Only commit locally unless explicitly instructed otherwise.

## Commands

```bash
# Build and run all tests
mvn clean package

# Run tests only
mvn test

# Run a single test class
mvn test -Dtest=AudioFileMatcherServiceTest

# Run mutation tests (PITest)
mvn org.pitest:pitest-maven:mutationCoverage

# Run locally (dev profile uses src/test/resources/audio/ as the audio directory)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Docker (production)
docker compose up -d
```

## Spring Profiles

- **`dev`** (`application-dev.properties`): `AUDIO_FILE_SEARCH_PATH` and `PLAYLIST_AUDIO_BASE_PATH` both point to `src/test/resources/audio/`. Used for local development and integration tests.
- **`live`** (default, `application-live.properties`): `AUDIO_FILE_SEARCH_PATH=/audio` (Docker volume), `PLAYLIST_AUDIO_BASE_PATH` read from the `.env` file's `AUDIO_FILE_LOCATION`. Used in Docker deployment.

Integration tests annotate with `@ActiveProfiles("dev")` to pick up the dev config.

## Architecture

The core pipeline on each `POST /upload` request:

1. **`SongService.parseSetList()`** — reads the `.docx` via Apache POI, strips leading line numbers, and extracts each song's title and optional key (the last word if it's a single letter A–G).
2. **`AudioFileMatcherService.matchSongsToAudioFiles()`** — lists audio files from `AUDIO_FILE_SEARCH_PATH` (top level only, no recursion), then fuzzy-matches each song title against filenames using FuzzyWuzzy `tokenSetRatio` with a threshold of 85. Normalization strips parentheses, punctuation, and collapses whitespace before matching.
3. **`AudioFileMatcherResults`** DTO — separates results into `matches` (Song → Path) and `notMatched` (List\<Song>).
4. **`SetListControllerImpl`** — if all songs matched, delegates to `PlayListService` and returns the XSPF as a file download; if any are unmatched, returns HTTP 400 with a JSON body containing `unmatchedSongs`.
5. **`PlayListService.buildPlaylist()`** — builds XSPF XML. If a song has no key (wasn't in the docx), it calls `AudioFileNameParser.getKeyFromPath()` to infer it from the audio filename.

**`AudioFileNameParser.getKeyFromPath()`** detects musical key from filenames in three formats (checked in order):
- Parenthetical: `Song Title (C).mp3`
- Underscore-separated: `Song_Title_C.mp3`, `Song_Title_C_Orig.mp3`
- Trailing letter after space: `Song Title C.mp3`

**`GET /extraSongs`** returns the `DEFAULT_EXTRA_SONGS` env var as a list — these are loaded by the frontend as default extra songs (e.g. soundcheck, encore).

## Key Configuration Variables

| Variable | Purpose |
|---|---|
| `AUDIO_FILE_SEARCH_PATH` | Directory scanned for audio files (no subdirectory recursion) |
| `PLAYLIST_AUDIO_BASE_PATH` | Path prefix written into `<location>` elements in the XSPF |
| `DEFAULT_EXTRA_SONGS` | Comma-separated song titles pre-loaded as "extra" songs |

## Test Structure

- `controller/unit/` — plain unit tests for controller logic
- `controller/web/` — `@WebMvcTest` slice tests for HTTP layer
- `integration/` — `@SpringBootTest` + `@ActiveProfiles("dev")` full-stack tests using real audio files from `src/test/resources/audio/`
- `testutils/TestStubs` — shared fixtures (e.g. `MockMultipartFile` helpers)
- PITest is configured to run mutation tests against all classes under `uk.seaofgreen.setlistbot.*` except the main application class; `toString()` methods are excluded.

## CI/CD

- **Jenkins** (`Jenkinsfile`): polls SCM every minute, runs `mvn clean package`, archives the JAR.
- **GitHub Actions** (`.github/workflows/maven.yml`): runs on push/PR.
- **Docker image** is published to GitHub Container Registry (`ghcr.io/lstierney/setlistbot:latest`).
