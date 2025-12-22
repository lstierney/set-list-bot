# SetListBot

SetListBot is a Dockerised Spring Boot application that generates
[XSPF compliant playlists](https://www.xspf.org/)
from Word (.docx) setlists.

You upload a setlist, SetListBot matches the songs to your audio files on disk, and downloads a ready-to-use playlist for VLC (or anything that supports XSPF).

## Example Output:
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<playlist xmlns="http://xspf.org/ns/0/" version="1">
  <title>Howlin_Wolf_25_10_2025</title>
  <trackList>
    <track>
      <location>file:///mnt/fileserver/audio/Why%20I%20Sing%20the%20Blues_C_Orig.mp3</location>
      <title>Why I Sing The Blues (C)</title>
    </track>
    <track>
      <location>file:///mnt/fileserver/audio/Worried%20Life%20Blues%20(C).mp3</location>
      <title>Worried Life Blues (C)</title>
    </track>
  </trackList>
</playlist>
```

## Prerequisites

* Docker
* Docker Compose
* A directory containing your audio files

## Getting Started
1. Create a working directory
```
mkdir setlistbot
cd setlistbot
```

2. Create a .env file

```
cd setlistbot
vi .env
```

.env contents

```
AUDIO_FILE_LOCATION=/path/to/your/audio/files
DEFAULT_EXTRA_SONGS="Song One Title, Song Two Title"
```

Notes:

`AUDIO_FILE_LOCATION` must point to an existing directory on your machine that holds your audio files (no recursion)

`DEFAULT_EXTRA_SONGS` are optional “non-setlist” songs (e.g. soundcheck or encore). Songs are comma-separated and quoted.

3. In the same, `setlistbot` directory, Create `docker-compose.yml`

`vi docker-compose.yml`

Contents:
```
services:
  setlistbot:
    image: ghcr.io/lstierney/setlistbot:latest
    environment:
      - AUDIO_FILE_LOCATION=${AUDIO_FILE_LOCATION}
      - DEFAULT_EXTRA_SONGS=${DEFAULT_EXTRA_SONGS}
    container_name: setlistbot
    ports:
      - "7777:8080"
    env_file:
      - .env
    volumes:
      - "${AUDIO_FILE_LOCATION}:/audio:ro"
    restart: unless-stopped
```
Note: edit `.env` rather than this file to change config values.

4. Start the application

`docker compose up -d`

Docker will pull the published image from GitHub Container Registry and start the app.

## Using SetListBot

Open your browser at:

http://localhost:7777

From there you can:

Upload a Word (.docx) setlist

Optionally add "extra" songs that are not in the setlist (soundcheck, encore, etc.)

Download a generated XSPF playlist

## Notes & Limitations

* Only the directory specified by `AUDIO_FILE_LOCATION` is searched. Subdirectories are not scanned
* Song keys are inferred from audio file names where possible, using these formats
```
song_title_A.mp3 # => Key is A
song title G.wav # => Key is G
song title (F).mp3 # => Key is F
```
* If any songs cannot be matched, the playlist is not generated and you’ll get a list of unmatched songs instead.

## Updating to a New Version
When a new image is published:
```
docker compose pull
docker compose down
docker compose up -d
```

If HTML/JS changes don’t appear to update:

```
docker compose build --no-cache
docker compose up -d
```
## Development

This README is intended for running SetListBot.

If you want to build or modify it yourself, clone the repository and run it locally using the development instructions in the repo.
