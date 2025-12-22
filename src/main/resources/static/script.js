const input = document.getElementById('extraSongInput');
const textarea = document.getElementById('extraSongs');
const addBtn = document.getElementById('addExtraSong');
const clearBtn = document.getElementById('clearExtraSongs');

let defaultShown = true;

// Fetch default extra songs from backend
window.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/extraSongs');
        if (response.ok) {
            const songs = await response.json();
            if (songs.length > 0) {
                textarea.value = songs.map(s => s.title).join('\n');
            }
        } else {
            console.warn('Failed to load default extra songs:', response.status);
        }
    } catch (err) {
        console.error('Error fetching default extra songs:', err);
    }
});

addBtn.addEventListener('click', () => {
    const value = input.value.trim();
    if (!value) return;

    // Overwrite default list on first real entry
    if (defaultShown) {
        textarea.value = value;
        defaultShown = false;
    } else {
        textarea.value += '\n' + value;
    }

    input.value = '';
});

clearBtn.addEventListener('click', () => {
    textarea.value = '';
    defaultShown = false;
});
