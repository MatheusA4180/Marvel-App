package com.example.marvelapp.framework.network.response

import com.example.core.domain.model.Character
import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("thumbnail")
    val thumbnail: ThumbnailResponse,
    @SerializedName("description")
    val description: String
)

fun CharacterResponse.toCharacterModel(): Character {
    return Character(
        characterId = id,
        name = this.name,
        imageUrl = this.thumbnail.getHttpsUrl(),
        description = this.description
    )
}
