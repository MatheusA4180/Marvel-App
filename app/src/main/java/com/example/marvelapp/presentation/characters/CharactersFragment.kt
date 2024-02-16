package com.example.marvelapp.presentation.characters

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.annotation.ColorRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvelapp.R
import com.example.marvelapp.databinding.FragmentCharactersBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.presentation.characters.adapter.CharactersAdapter
import com.example.marvelapp.presentation.characters.adapter.CharactersLoadMoreStateAdapter
import com.example.marvelapp.presentation.characters.adapter.CharactersRefreshStateAdapter
import com.example.marvelapp.presentation.detail.DetailViewArg
import com.example.marvelapp.presentation.sort.SortFragment
import com.example.marvelapp.util.idlingresource.singleton.FlipperCharactersIdlingResource
import com.example.marvelapp.util.idlingresource.singleton.RecyclerCharactersIdlingResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CharactersFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    private var _binding: FragmentCharactersBinding? = null
    private val binding: FragmentCharactersBinding get() = _binding!!

    private val viewModel: CharactersViewModel by viewModels()

    private lateinit var searchView: SearchView

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    @JvmField
    var flipperCharactersIdlingResource: FlipperCharactersIdlingResource? = null

    @Inject
    @JvmField
    var recyclerCharactersIdlingResource: RecyclerCharactersIdlingResource? = null

    private val headerAdapter: CharactersRefreshStateAdapter by lazy {
        CharactersRefreshStateAdapter(
            charactersAdapter::retry
        )
    }

    private val charactersAdapter: CharactersAdapter by lazy {
        CharactersAdapter(imageLoader, recyclerCharactersIdlingResource) { character, view ->
            val extras = FragmentNavigatorExtras(
                view to character.name
            )

            val directions = CharactersFragmentDirections
                .actionCharactersFragmentToDetailFragment(
                    DetailViewArg(
                        characterId = character.characterId,
                        name = character.name,
                        imageUrl = character.imageUrl,
                        description = character.description
                    ),
                    character.name
                )

            findNavController().navigate(directions, extras)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCharactersBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCharactersAdapter()
        observeInitialLoadState()
        observeSortingData()
        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        observeSearchData()
        configIdlingResources()
        viewModel.searchCharacters()
    }

    private fun observeSearchData() {
        viewModel.state.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is CharactersViewModel.UiState.SearchResult -> {
                    charactersAdapter.submitData(viewLifecycleOwner.lifecycle, uiState.data)
                }
            }
        }
    }

    private fun configIdlingResources() {
        recyclerCharactersIdlingResource?.increment()
        binding.flipperCharacters.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            when ((view as ViewFlipper).displayedChild) {
                FLIPPER_CHILD_ERROR -> {
                    flipperCharactersIdlingResource?.decrement()
                }
            }
        }
    }

    private fun initCharactersAdapter() {
        postponeEnterTransition()
        with(binding.includeViewListCharacter) {
            recyclerCharacters.run {
                setHasFixedSize(true)
                adapter = charactersAdapter.withLoadStateHeaderAndFooter(
                    header = headerAdapter,
                    footer = CharactersLoadMoreStateAdapter(
                        charactersAdapter::retry
                    )
                )
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
                this@with.buttonGoToTop.setOnClickListener {
                    scrollToPosition(POSITION_LIST_FIRST_ITEM)
                }
                val layoutManger = layoutManager as LinearLayoutManager
                setOnScrollChangeListener { _, _, _, _, _ ->
                    if (layoutManger.findFirstCompletelyVisibleItemPosition()
                        == POSITION_LIST_FIRST_ITEM
                    ) {
                        buttonGoToTop.visibility = View.GONE
                    } else {
                        buttonGoToTop.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeInitialLoadState() {
        lifecycleScope.launch {
            charactersAdapter.loadStateFlow.collectLatest { loadState ->
                headerAdapter.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf {
                        it is LoadState.Error && charactersAdapter.itemCount > 0
                    } ?: loadState.prepend
                binding.flipperCharacters.displayedChild = when {
                    loadState.mediator?.refresh is LoadState.Loading -> {
                        setShimmerVisibility(true)
                        FLIPPER_CHILD_LOADING
                    }

                    loadState.mediator?.refresh is LoadState.Error
                            && charactersAdapter.itemCount == 0 -> {
                        setShimmerVisibility(false)
                        binding.includeViewCharactersErrorState.buttonRetry.setOnClickListener {
                            charactersAdapter.retry()
                        }
                        FLIPPER_CHILD_ERROR
                    }

                    loadState.source.refresh is LoadState.NotLoading
                            || loadState.mediator?.refresh is LoadState.NotLoading -> {
                        setShimmerVisibility(false)

                        FLIPPER_CHILD_CHARACTERS
                    }

                    else -> {
                        setShimmerVisibility(false)
                        FLIPPER_CHILD_CHARACTERS
                    }
                }
            }
        }
    }

    private fun setShimmerVisibility(visibility: Boolean) {
        binding.includeViewCharactersLoadingState.shimmerCharacters.run {
            isVisible = visibility
            if (visibility) {
                startShimmer()
            } else stopShimmer()
        }
    }

    private fun observeSortingData() {
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.charactersFragment)
        val observer = LifecycleEventObserver { _, event ->
            val isSortingApplied = navBackStackEntry.savedStateHandle.contains(
                SortFragment.SORTING_APPLIED_BASK_STACK_KEY
            )

            if (event == Lifecycle.Event.ON_RESUME && isSortingApplied) {
                viewModel.applySort()
                navBackStackEntry.savedStateHandle.remove<Boolean>(
                    SortFragment.SORTING_APPLIED_BASK_STACK_KEY
                )
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.characters_menu_items, menu)

        val searchItem = menu.findItem(R.id.menu_search)
        searchView = (searchItem.actionView as SearchView).apply {
            setThemeAndHintSearchView(
                colorIcons = R.color.white,
                colorText = R.color.white,
                colorHint = R.color.red_900,
                textHint = getString(R.string.search_hint)
            )
        }

        searchItem.setOnActionExpandListener(this)

        if (viewModel.currentSearchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(viewModel.currentSearchQuery, false)
        }

        searchView.run {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(this@CharactersFragment)
        }
    }

    private fun SearchView.setThemeAndHintSearchView(
        @ColorRes colorIcons: Int,
        @ColorRes colorText: Int,
        @ColorRes colorHint: Int,
        textHint: String
    ) {
        findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn)
            .setColorFilter(
                ResourcesCompat.getColor(resources, colorIcons, null),
                PorterDuff.Mode.SRC_ATOP
            )
        findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            .setColorFilter(
                ResourcesCompat.getColor(resources, colorIcons, null),
                PorterDuff.Mode.SRC_ATOP
            )
        findViewById<EditText>(androidx.appcompat.R.id.search_src_text).run {
            setTextColor(ResourcesCompat.getColor(resources, colorText, null))
            setHintTextColor(ResourcesCompat.getColor(resources, colorHint, null))
            hint = textHint
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_sort -> {
                findNavController().navigate(R.id.action_charactersFragment_to_sortFragment)
                true
            }

            else -> false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return query?.let {
            viewModel.currentSearchQuery = it
            viewModel.searchCharacters()
            true
        } ?: false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        viewModel.closeSearch()
        viewModel.searchCharacters()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        _binding = null
    }

    companion object {
        private const val POSITION_LIST_FIRST_ITEM = 0
        private const val FLIPPER_CHILD_LOADING = 0
        private const val FLIPPER_CHILD_CHARACTERS = 1
        private const val FLIPPER_CHILD_ERROR = 2
    }
}
