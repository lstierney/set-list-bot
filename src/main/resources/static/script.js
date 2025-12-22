const input = document.getElementById('extraSongInput');
const textarea = document.getElementById('extraSongs');
const addBtn = document.getElementById('addExtraSong');
const clearBtn = document.getElementById('clearExtraSongs');

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
