package com.daniel.spotyinsights

import com.daniel.spotyinsights.domain.model.Album
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.model.TrackArtist
import org.junit.Assert.*
import org.junit.Test

class TrackModelUnitTest {
    @Test
    fun createTrackArtist_andCheckProperties() {
        val artist = TrackArtist(id = "1", name = "Artist Name", spotifyUrl = "https://spotify.com/artist/1")
        assertEquals("1", artist.id)
        assertEquals("Artist Name", artist.name)
        assertEquals("https://spotify.com/artist/1", artist.spotifyUrl)
    }

    @Test
    fun createAlbum_andCheckProperties() {
        val album = Album(id = "10", name = "Album Name", releaseDate = "2020-01-01", imageUrl = "https://image.url", spotifyUrl = "https://spotify.com/album/10")
        assertEquals("10", album.id)
        assertEquals("Album Name", album.name)
        assertEquals("2020-01-01", album.releaseDate)
        assertEquals("https://image.url", album.imageUrl)
        assertEquals("https://spotify.com/album/10", album.spotifyUrl)
    }

    @Test
    fun createTrack_andCheckEquality() {
        val artist = TrackArtist(id = "1", name = "Artist Name", spotifyUrl = "https://spotify.com/artist/1")
        val album = Album(id = "10", name = "Album Name", releaseDate = "2020-01-01", imageUrl = "https://image.url", spotifyUrl = "https://spotify.com/album/10")
        val track1 = Track(
            id = "100",
            name = "Track Name",
            artists = listOf(artist),
            album = album,
            durationMs = 180000,
            popularity = 50,
            previewUrl = null,
            spotifyUrl = "https://spotify.com/track/100",
            explicit = false
        )
        val track2 = track1.copy()
        assertEquals(track1, track2)
        assertEquals("Track Name", track1.name)
        assertEquals(180000, track1.durationMs)
        assertFalse(track1.explicit)
    }
} 