document.getElementById('setlistForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const playlistName = document.getElementById('playlistName').value;

    try {
        const response = await fetch(form.action, {
            method: 'POST',
            body: formData
        });

        const unmatchedDiv = document.getElementById('unmatchedSongs');
        unmatchedDiv.innerHTML = '';

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = playlistName + '.xspf';
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } else if (response.status === 400) {
            const data = await response.json();
            if (data.unmatchedSongs && data.unmatchedSongs.length > 0) {
                unmatchedDiv.textContent = 'Unmatched songs: ' +
                data.unmatchedSongs
                  .map(song => `${song.title} (${song.key})`)
                  .join(', ');
            } else {
                unmatchedDiv.textContent = 'Some songs could not be matched.';
            }
        } else {
            unmatchedDiv.textContent = 'Unexpected error: ' + response.status;
        }
    } catch (err) {
        document.getElementById('unmatchedSongs').textContent = 'Error communicating with server.';
        console.error(err);
    }
});