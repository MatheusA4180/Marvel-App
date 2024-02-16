package com.example.testing.model

import com.example.core.domain.model.Comic

class ComicFactory {

    fun create(comic: FakeComic) = when (comic) {
        FakeComic.FakeComic1 -> Comic(
            id = 2211506,
            imageUrl = "http://fakecomigurl.jpg"
        )
    }

    sealed class FakeComic {
        object FakeComic1 : FakeComic()
    }
}
