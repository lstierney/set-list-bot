# SetListBot

SetListBot is a Spring Boot application that generates XSPF compliant playlists from Word setlists. The app runs in Docker for easy deployment.

## Prerequisites

- [Docker](https://www.docker.com/get-started) installed
- [Docker Compose](https://docs.docker.com/compose/install/) installed
- A folder containing your audio files

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/lstierney/setlistbot.git
cd setlistbot
```

### 2. Create and configure .env
Edit .env to point to the folder containing your audio files e.g:

`AUDIO_BASE_PATH=/home/user/Music/SetlistTunes`

### 3. Start the application with Docker Compose
`docker-compose up -d`

This will:

* Pull or build the SetListBot Docker image
* Start the container with the live Spring profile
* Mount your audio files into the container at /audio

### Access the app

Open your browser at http://localhost:7777

### Notes
* The live Spring profile is always used, ensuring the app reads audio files from the mounted volume.
* Make sure the AUDIO_BASE_PATH directory exists and contains your audio files before starting the container.
* The container is set to restart unless stopped, so it will automatically recover from host reboots.
