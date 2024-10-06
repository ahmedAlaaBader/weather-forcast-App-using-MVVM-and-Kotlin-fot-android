package com.example.wetherforcastapp.ui.favorite.view

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.wetherforcastapp.R
import com.example.wetherforcastapp.databinding.FragmentMapsBinding
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModel
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModelFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap


    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var address1: String
    private val favViewModel: FavViewModel by viewModels {
        FavViewModelFactory(
            RepoImpl.getInstance(
                IRemoteDataSourceImpl.getInstance(),
                LocalDataBaseImp.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            if (latitude != null && longitude != null) {
                setToFavWeather(address1, latitude!!, longitude!!)
                binding.bottomBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Location saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Select a location first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addresses?.get(0)
            val fullAddress = address?.adminArea ?: "Unknown Location"
            mMap.addMarker(MarkerOptions().position(latLng).title(fullAddress))

            address1 = fullAddress
            latitude = latLng.latitude
            longitude = latLng.longitude

            binding.locationInfo.text = "$address1\n$latitude, $longitude"
            binding.bottomBar.visibility = View.VISIBLE
        }


        mMap.setOnMarkerClickListener { marker ->
            Log.i("TAG", "Marker clicked: ${marker.title}")
            address1 = marker.title ?: "Unknown Location"
            latitude = marker.position.latitude
            longitude = marker.position.longitude
            binding.locationInfo.text = "$address1\n$latitude, $longitude"
            binding.bottomBar.visibility = View.VISIBLE
            true
        }
    }

    private fun setToFavWeather(address: String, latitude: Double, longitude: Double) {
        favViewModel.fetchWeatherAndSaveToLocal(latitude, longitude,address)
    }

}