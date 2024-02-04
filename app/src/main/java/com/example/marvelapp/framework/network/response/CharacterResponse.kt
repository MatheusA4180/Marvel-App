package com.example.marvelapp.framework.network.response

import com.google.gson.annotations.SerializedName
import com.example.core.domain.model.Character

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
        imageUrl = "${this.thumbnail.path}.${this.thumbnail.extension}"
            .replace("http", "https"),
        description = this.description
    )
}
