package com.example.wetherforcastapp.ui.favorite.view

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModel
import com.example.wetherforcastapp.R
import com.example.wetherforcastapp.databinding.FragmentFavBinding
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavFragment : Fragment() {
    private lateinit var binding: FragmentFavBinding
    private lateinit var favWeatherListAdapter: FavWeatherListAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: FavViewModel by viewModels {
        FavViewModelFactory(
            RepoImpl.getInstance(
                IRemoteDataSourceImpl.getInstance(),
                LocalDataBaseImp.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("R3-pref", MODE_PRIVATE)
        setupRecyclerViews()

        binding.fab.setOnClickListener {
            Log.d("savePreference", "Before save: ${getPreference("map", "default")}")
            savePreference("map", "g")
            Log.d("savePreference", "After save: ${getPreference("map", "default")}")

            findNavController().navigate(R.id.action_favFragment_to_mapsFragment)
        }

        observeUIState()
    }

    private fun observeUIState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UIState.Loading -> {
                        showLoadingState()
                    }
                    is UIState.Success<*> -> {
                        val dataBaseEntity = state.data as List<DataBaseEntity>
                        Log.d("FavFragment", "Data received")
                        updateCurrentWeatherUI(dataBaseEntity)
                    }
                    is UIState.Failure -> {
                        Log.e("FavFragment", "Error: ${state.msg}")
                        showError(state.msg)
                    }
                    else -> {
                        Log.e("FavFragment", "Unexpected state")
                    }
                }
            }
        }
    }

    private fun updateCurrentWeatherUI(dataBaseEntity: List<DataBaseEntity>) {
        binding.progressBar2.visibility = View.GONE
        favWeatherListAdapter.submitList(dataBaseEntity)
    }

    private fun showLoadingState() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun showError(exception: Throwable) {
        Toast.makeText(requireContext(), "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
    }

    private fun setupRecyclerViews() {
        favWeatherListAdapter = FavWeatherListAdapter(
            { navigateToFavDEFragment(it) },
            { onDeleteFavClicked(it) }
        )
        binding.recyclerView.apply {
            adapter = favWeatherListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun navigateToFavDEFragment(dataBaseEntity: DataBaseEntity) {
        val bundle = Bundle().apply {
            putSerializable("dataBaseEntity", dataBaseEntity)
        }
        findNavController().navigate(R.id.action_favFragment_to_favDetailsFragment, bundle)
    }

    private fun deleteFromFav(favWeather: String) {
        viewModel.deleteFavWeather(favWeather)
        Toast.makeText(requireContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(favWeather: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Confirmation")
        builder.setMessage("Are you sure you want to delete this item from favorites?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteFromFav(favWeather)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun onDeleteFavClicked(favWeather: String) {
        showDeleteConfirmationDialog(favWeather)
    }

    private fun savePreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getPreference(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
}