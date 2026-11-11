package com.pnc.jetpackcomposedemos.features.artists.domain

import javax.inject.Inject

class RefreshArtistsUseCase @Inject constructor(
    private val repository: ArtistRepository,
    private var somethin:Int = 0
) {

    suspend operator fun invoke() {
        repository.refreshArtists()
    }

}
