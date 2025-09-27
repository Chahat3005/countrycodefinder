package com.android.countrycodefinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.countrycodefinder.datamodels.DataClass
import kotlinx.coroutines.launch

class ViewModels(private val repository: UserRepository) : ViewModel() {

    private val _countries = MutableLiveData<List<DataClass>>()
    val countries: LiveData<List<DataClass>> get() = _countries

    private val _selectedCountry = MutableLiveData<DataClass?>()
    val selectedCountry: LiveData<DataClass?> get() = _selectedCountry

    fun loadCountries() {
        viewModelScope.launch {
            val result = repository.fetchCountries()
            result?.forEach{
                println("COUNTRY: ${it.name}, ISO: ${it.iso}, CODE: ${it.phonecode}")

            }
            _countries.postValue(result ?: emptyList())
        }
    }

    fun searchCountry(query: String) {
        val list = _countries.value
        val match = list?.find {
            it.name.equals(query.trim(), ignoreCase = true)
        }
        _selectedCountry.postValue(match)
    }
}
