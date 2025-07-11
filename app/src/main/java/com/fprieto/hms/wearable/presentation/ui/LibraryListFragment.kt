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
import androidx.recyclerview.widget.LinearLayoutManager
import com.fprieto.hms.wearable.R
import com.fprieto.hms.wearable.databinding.FragmentLibraryListBinding
import com.fprieto.hms.wearable.model.audiobookshelf.Library
import com.fprieto.hms.wearable.presentation.vm.LibraryListResult
import com.fprieto.hms.wearable.presentation.vm.LibraryListViewModel
import com.fprieto.hms.wearable.presentation.vm.observeEvent
import javax.inject.Inject

class LibraryListFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var binding: FragmentLibraryListBinding
    private val viewModel by viewModels<LibraryListViewModel> { viewModelFactory }
    private lateinit var libraryAdapter: LibraryAdapter // Placeholder for Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.fetchLibraries()
    }

    private fun setupRecyclerView() {
        libraryAdapter = LibraryAdapter { library ->
            // Navigate to ItemListFragment
            val action = LibraryListFragmentDirections.actionLibraryListFragmentToItemListFragment(library.id, library.name)
            findNavController().navigate(action)
        }
        binding.recyclerViewLibraries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.libraryListResult.observeEvent(viewLifecycleOwner) { result ->
            when (result) {
                is LibraryListResult.Loading -> {
                    binding.progressBarLibraries.isVisible = true
                    binding.textViewEmptyLibraries.isVisible = false
                    binding.recyclerViewLibraries.isVisible = false
                }
                is LibraryListResult.Success -> {
                    binding.progressBarLibraries.isVisible = false
                    binding.textViewEmptyLibraries.isVisible = false
                    binding.recyclerViewLibraries.isVisible = true
                    libraryAdapter.submitList(result.libraries)
                }
                is LibraryListResult.Error -> {
                    binding.progressBarLibraries.isVisible = false
                    binding.textViewEmptyLibraries.text = result.message
                    binding.textViewEmptyLibraries.isVisible = true
                    binding.recyclerViewLibraries.isVisible = false
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is LibraryListResult.Empty -> {
                    binding.progressBarLibraries.isVisible = false
                    binding.textViewEmptyLibraries.text = "No libraries found."
                    binding.textViewEmptyLibraries.isVisible = true
                    binding.recyclerViewLibraries.isVisible = false
                }
            }
        }
    }
}

// Placeholder Adapter - to be implemented properly
class LibraryAdapter(private val onItemClick: (Library) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Library, LibraryAdapter.LibraryViewHolder>(LibraryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        // TODO: Inflate item_library.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_library, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val library = getItem(position)
        holder.bind(library)
        holder.itemView.setOnClickListener { onItemClick(library) }
    }

    class LibraryViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        // TODO: Get references to TextViews from item_library.xml and bind data
        private val nameTextView: android.widget.TextView = itemView.findViewById(R.id.textViewLibraryName)
        fun bind(library: Library) {
            nameTextView.text = library.name
            // TODO: Load icon if available
        }
    }

    class LibraryDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Library>() {
        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem == newItem
    }
}
