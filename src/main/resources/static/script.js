const fileInput = document.getElementById('file');
const playlistNameInput = document.getElementById('playlistName');
const input = document.getElementById('extraSongInput');
const textarea = document.getElementById('extraSongs');
const addBtn = document.getElementById('addExtraSong');
const clearBtn = document.getElementById('clearExtraSongs');
const unmatchedDiv = document.getElementById('unmatchedSongs');

window.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/extraSongs');
        if (response.ok) {
            const songs = await response.json();
            textarea.value = songs.map(s => s.title).join('\n');
        }
    } catch (err) {
        console.error(err);
    }
});

fileInput.addEventListener('change', () => {
    const file = fileInput.files[0];
    if (file) {
        playlistNameInput.value = file.name
            .replace(/\.[^.]+$/, '')       // strip extension
            .replace(/\./g, '_')           // dots to underscores
            .replace(/(_\d{4})\1+/g, '$1'); // remove consecutive duplicate years e.g. _2026_2026 -> _2026
    }
});

document.getElementById('setlistForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    unmatchedDiv.innerHTML = '';

    const response = await fetch('/upload', {
        method: 'POST',
        body: new FormData(e.target)
    });

    if (response.ok) {
        const disposition = response.headers.get('Content-Disposition');
        const filename = disposition?.match(/filename="(.+)"/)?.[1] ?? 'playlist.xspf';
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        URL.revokeObjectURL(url);
    } else {
        const data = await response.json();
        const songs = data.unmatchedSongs;
        unmatchedDiv.innerHTML =
            '<p>The following songs could not be matched to an audio file:</p><ul>'
            + songs.map(s => `<li>${s.title}${s.key ? ' (' + s.key + ')' : ''}</li>`).join('')
            + '</ul>';
    }
});

addBtn.addEventListener('click', () => {
    const value = input.value.trim();
    if (!value) return;

    textarea.value = textarea.value
        ? textarea.value + '\n' + value
        : value;

    input.value = '';
});

clearBtn.addEventListener('click', () => {
    textarea.value = '';
});
