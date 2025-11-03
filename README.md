# SetListBot

SetListBot is a Spring Boot application that generates XSPF compliant playlists from Word (docx) "setlists". The app runs in Docker for easy deployment.

## Example

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
    ... etc ...
```

## Prerequisites

- [Docker](https://www.docker.com/get-started) installed
- [Docker Compose](https://docs.docker.com/compose/install/) installed
- A folder containing your audio files

## Getting Started

### 1. Clone the repository

```bash
git clone git@github.com:lstierney/set-list-bot.git
cd setlistbot
```

### 2. Create and configure .env
Edit .env to point to the folder containing your audio files e.g:

`AUDIO_FILE_LOCATION=/home/user/Music/SetlistTunes`

### 3. Start the application with Docker Compose
`docker-compose up -d`

This will:

* Pull or build the SetListBot Docker image
* Mount your audio files into the container at /audio

### Access the app

Open your browser at http://localhost:7777

### Notes
* Make sure the AUDIO_FILE_LOCATION directory exists and contains your audio files before starting the container. Only this directory will be searched. A recursive search WILL NOT be performed.
* The container is set to restart unless stopped, so it will automatically recover from host reboots.
