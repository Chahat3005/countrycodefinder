package com.android.countrycodefinder.datamodels

import android.media.Image

data class DataClass(
    val name: String,
    val iso: String,
    val image: Image,
    val phonecode: Int
)
