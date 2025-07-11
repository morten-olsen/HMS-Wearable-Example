package com.fprieto.hms.wearable.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fprieto.hms.wearable.R
import com.fprieto.hms.wearable.databinding.FragmentItemListBinding
import com.fprieto.hms.wearable.model.audiobookshelf.LibraryItem
import com.fprieto.hms.wearable.presentation.vm.ItemListResult
import com.fprieto.hms.wearable.presentation.vm.ItemListViewModel
import com.fprieto.hms.wearable.presentation.vm.observeEvent
import javax.inject.Inject

class ItemListFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var binding: FragmentItemListBinding
    private val viewModel by viewModels<ItemListViewModel> { viewModelFactory }
    private val args: ItemListFragmentArgs by navArgs()
    private lateinit var itemAdapter: ItemAdapter // Placeholder for Adapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = args.libraryName ?: "Items"

        setupRecyclerView()
        observeViewModel()
        viewModel.fetchItems()
    }

    private fun setupRecyclerView() {
        itemAdapter = ItemAdapter { libraryItem ->
            // Navigate to PlayerFragment
            val action = ItemListFragmentDirections.actionItemListFragmentToPlayerFragment(
                libraryItem.id,
                libraryItem.media.metadata?.let {
                    when(it) {
                        is com.fprieto.hms.wearable.model.audiobookshelf.BookMetadata -> it.title
                        is com.fprieto.hms.wearable.model.audiobookshelf.PodcastMetadata -> it.title
                        else -> "Unknown Title"
                    }
                } ?: "Unknown Title"
            )
            findNavController().navigate(action)
        }
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.itemListResult.observeEvent(viewLifecycleOwner) { result ->
            when (result) {
                is ItemListResult.Loading -> {
                    binding.progressBarItems.isVisible = true
                    binding.textViewEmptyItems.isVisible = false
                    binding.recyclerViewItems.isVisible = false
                }
                is ItemListResult.Success -> {
                    binding.progressBarItems.isVisible = false
                    binding.textViewEmptyItems.isVisible = false
                    binding.recyclerViewItems.isVisible = true
                    itemAdapter.submitList(result.items)
                }
                is ItemListResult.Error -> {
                    binding.progressBarItems.isVisible = false
                    binding.textViewEmptyItems.text = result.message
                    binding.textViewEmptyItems.isVisible = true
                    binding.recyclerViewItems.isVisible = false
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is ItemListResult.Empty -> {
                    binding.progressBarItems.isVisible = false
                    binding.textViewEmptyItems.text = "No items found in this library."
                    binding.textViewEmptyItems.isVisible = true
                    binding.recyclerViewItems.isVisible = false
                }
            }
        }
    }
}

// Placeholder Adapter - to be implemented properly
class ItemAdapter(private val onItemClick: (LibraryItem) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<LibraryItem, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // TODO: Inflate item_audiobook.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audiobook, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class ItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val titleTextView: android.widget.TextView = itemView.findViewById(R.id.textViewTitle)
        private val authorTextView: android.widget.TextView = itemView.findViewById(R.id.textViewAuthor)
        // private val coverImageView: android.widget.ImageView = itemView.findViewById(R.id.imageViewCover)

        fun bind(item: LibraryItem) {
            val metadata = item.media.metadata
            if (metadata != null) {
                when(metadata) {
                    is com.fprieto.hms.wearable.model.audiobookshelf.BookMetadata -> {
                        titleTextView.text = metadata.title ?: "No Title"
                        authorTextView.text = metadata.authorName ?: "Unknown Author"
                    }
                    is com.fprieto.hms.wearable.model.audiobookshelf.PodcastMetadata -> {
                        titleTextView.text = metadata.title ?: "No Title"
                        authorTextView.text = metadata.author ?: "Unknown Author"
                    }
                }
            } else {
                titleTextView.text = "No Title Data"
                authorTextView.text = "Unknown Author"
            }
            // TODO: Load cover image using Glide/Picasso and item.media.coverPath
        }
    }

    class ItemDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<LibraryItem>() {
        override fun areItemsTheSame(oldItem: LibraryItem, newItem: LibraryItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LibraryItem, newItem: LibraryItem): Boolean = oldItem == newItem
    }
}
