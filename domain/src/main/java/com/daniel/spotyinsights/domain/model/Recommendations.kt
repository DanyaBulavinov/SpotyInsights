package com.daniel.spotyinsights.domain.model

data class RecommendationSeed(
    val id: String,
    val type: SeedType,
    val href: String
)

enum class SeedType {
    ARTIST,
    TRACK,
    GENRE
}

data class RecommendationParameters(
    val seedArtists: List<String> = emptyList(),
    val seedTracks: List<String> = emptyList(),
    val seedGenres: List<String> = emptyList(),
    val limit: Int = 20,
    val minPopularity: Int? = null,
    val maxPopularity: Int? = null,
    val targetPopularity: Int? = null
) {
    init {
        require(seedArtists.size + seedTracks.size + seedGenres.size <= 5) {
            "Total number of seeds must not exceed 5"
        }
        require(limit in 1..100) {
            "Limit must be between 1 and 100"
        }
        minPopularity?.let { min ->
            require(min in 0..100) {
                "Minimum popularity must be between 0 and 100"
            }
        }
        maxPopularity?.let { max ->
            require(max in 0..100) {
                "Maximum popularity must be between 0 and 100"
            }
        }
        targetPopularity?.let { target ->
            require(target in 0..100) {
                "Target popularity must be between 0 and 100"
            }
        }
    }
}

data class Recommendations(
    val tracks: List<Track>,
    val seeds: List<RecommendationSeed>
) 