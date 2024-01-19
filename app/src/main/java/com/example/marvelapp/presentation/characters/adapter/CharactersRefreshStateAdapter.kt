package com.example.marvelapp.presentation.characters.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.marvelapp.presentation.characters.adapter.CharactersRefreshStateViewHolder

class CharactersRefreshStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<CharactersRefreshStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = CharactersRefreshStateViewHolder.create(parent, retry)

    override fun onBindViewHolder(
        holder: CharactersRefreshStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)
}
