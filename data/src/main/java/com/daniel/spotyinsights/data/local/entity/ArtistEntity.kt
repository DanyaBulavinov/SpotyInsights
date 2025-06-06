import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detailed_artists")
data class DetailedArtistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val spotifyUrl: String,
    val popularity: Int,
    val fetchTimeMs: Long // To track when the data was fetched
)
