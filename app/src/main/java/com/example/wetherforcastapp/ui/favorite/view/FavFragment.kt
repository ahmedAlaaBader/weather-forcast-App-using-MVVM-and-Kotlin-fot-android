package com.example.wetherforcastapp.ui.favorite.view

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
import com.bumptech.glide.Glide
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModel
import com.example.wetherforcastapp.R
import com.example.wetherforcastapp.databinding.FragmentFavBinding
import com.example.wetherforcastapp.databinding.FragmentHomeBinding
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModelFactory
import com.example.wetherforcastapp.ui.helperClasess.LocationPermissions
import com.example.wetherforcastapp.ui.home.view.DaysWeatherListAdapter
import com.example.wetherforcastapp.ui.home.view.HourlyWeatherListAdapter
import com.example.wetherforcastapp.ui.home.viewmodel.HomeViewModel
import com.example.wetherforcastapp.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class favFragment : Fragment() {
    private lateinit var binding: FragmentFavBinding
    private lateinit var favWeatherListAdapter: FavWeatherListAdapter

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

        setupRecyclerViews()
        observeUIState()


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_favFragment_to_mapsFragment)
        }
    }
    private fun observeUIState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UIState.Loading -> {
                        showLoadingState()

                    }
                    is UIState.Success <*> -> {
                        val dataBaseEntity = state.data as List<DataBaseEntity>
                        Log.d("HomeFragment", "Current weather data received")
                        updateCurrentWeatherUI(dataBaseEntity)
                    }
                    is UIState.Failure -> {
                        Log.e("HomeFragment", "Error: ${state.msg}")
                        // viewModel.fetchCurrentWeatherLocal()

                        showError(state.msg)
                    }
                    else ->{
                        Log.e("HomeFragment", "else:")}

                }
            }
        }
    }



    private fun updateCurrentWeatherUI(dataBaseEntity:List<DataBaseEntity>) {
        binding.progressBar2.visibility = View.GONE

        // Update RecyclerViews with forecast data
        favWeatherListAdapter.submitList(dataBaseEntity)

    }

    private fun showLoadingState() {

        binding.progressBar2.visibility = View.VISIBLE
        //Toast.makeText(requireContext(), "loading", Toast.LENGTH_LONG).show()
    }

    private fun showError(exception: Throwable) {
        // Handle error (show a Toast, Snackbar, or TextView with the error message)
        Toast.makeText(requireContext(), "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
    }
    private fun setupRecyclerViews() {
        favWeatherListAdapter =FavWeatherListAdapter({ navigateToFavDEFragment(it) },
            { onDeleteFavClicked(it) })
        binding.recyclerView.apply {
            adapter = favWeatherListAdapter
            layoutManager =
                LinearLayoutManager(requireContext())
        }
    }
    private fun navigateToFavDEFragment(dataBaseEntity: DataBaseEntity) {
        val bundle = Bundle().apply {
            putSerializable("dataBaseEntity", dataBaseEntity)
        }
       // findNavController().navigate(R.id.action_nav_slideshow_to_favFragment, bundle)
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
            dialog.dismiss() // Just dismiss the dialog
        }

        builder.create().show()
    }
    private fun onDeleteFavClicked(favWeather: String) {
        showDeleteConfirmationDialog(favWeather)
    }
}