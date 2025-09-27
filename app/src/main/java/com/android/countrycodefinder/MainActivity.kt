package com.android.countrycodefinder

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.countrycodefinder.databinding.ActivityMainBinding
import com.android.countrycodefinder.tagsphere.VectorDrawableTagItem
import com.bumptech.glide.Glide
import com.magicgoop.tagsphere.OnTagTapListener
import com.magicgoop.tagsphere.item.TagItem
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var viewModel: ViewModels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

        val repository = UserRepository()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewModels(repository) as T
            }
        })[ViewModels::class.java]

        textToSpeech = TextToSpeech(this) {
            textToSpeech.language = Locale.UK
            textToSpeech.setPitch(1f)
            val voice = Voice("en-gb-x-rjs#male_1-local", Locale.getDefault(), 1, 1, false, null)
            textToSpeech.voice = voice
        }

        viewModel.loadCountries()

        setupAutoCompleteSearch()
        observeCountryData()
        setUpTagImageSphere()
    }

    private fun setupAutoCompleteSearch() {
        viewModel.countries.observe(this) { countryList ->
            val countryNames = countryList.map { it.name }
            val adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countryNames)
            binding.svSearchView.setAdapter(adapter)

            binding.svSearchView.setOnItemClickListener { _, _, position, _ ->
                adapter.getItem(position)?.let {
                    viewModel.searchCountry(it)
                    hideKeyboard()
                }
            }

            binding.svSearchView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        clearCountryInfo()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun setUpTagImageSphere() {
        val drawableResList = listOf(
            R.drawable.ad,
            R.drawable.af,
            R.drawable.ai,
            R.drawable.al,
            R.drawable.ao,
            R.drawable.aq,
            R.drawable.ax,
            R.drawable.ad,
            R.drawable.af,
            R.drawable.ai,
            R.drawable.al,
            R.drawable.ao,
            R.drawable.aq,
            R.drawable.ax,
            R.drawable.ad,
            R.drawable.af,
            R.drawable.ai,
            R.drawable.bb,
            R.drawable.bh,
            R.drawable.bt,
            R.drawable.ax,
            R.drawable.ad,
            R.drawable.af,
            R.drawable.ai,
            R.drawable.al,
            R.drawable.ao,
            R.drawable.aq,
            R.drawable.ax,
            R.drawable.bb,
            R.drawable.bh,
            R.drawable.bt,
            R.drawable.ai,
            R.drawable.al,
            R.drawable.bb,
            R.drawable.bh,
            R.drawable.bt,
            R.drawable.ax,
            R.drawable.bb,
            R.drawable.ad,
            R.drawable.bb,
            R.drawable.bh,
            R.drawable.bt
        )

        val tags = mutableListOf<VectorDrawableTagItem>()

        drawableResList.forEach { id ->
            val drawable = getVectorDrawable(id)
            if (drawable != null) {
                tags.add(VectorDrawableTagItem(drawable))
            }
        }
        binding.tagView.addTagList(tags)
        binding.tagView.setRadius(2.75f)
        binding.tagView.setOnTagTapListener(object : OnTagTapListener {
            override fun onTap(tagItem: TagItem) {
                textToSpeech.speak("Flags", TextToSpeech.QUEUE_FLUSH, null)
            }
        })
    }

    private fun getVectorDrawable(id: Int): Drawable? = ContextCompat.getDrawable(this, id)

//    private fun setupTagSphere() {
//        viewModel.countries.observe(this) { countryList ->
//            val textPaint = TextPaint().apply {
//                isAntiAlias = true
//                textSize = 20f
//                color = Color.WHITE
//            }
//            binding.tagView.setTextPaint(textPaint)
//
//            val tags = listOf(
//                "Australia",
//                "Bulgaria",
//                "Colombia",
//                "Eritrea",
//                "France",
//                "Germany",
//                "Hungary",
//                "India",
//                "Pitcairn",
//                "Jamaica",
//                "United Kingdom",
//                "Sweden",
//                "Japan",
//                "Kiribati",
//                "Liberia",
//                "Malaysia",
//                "USA",
//                "SwitzerLand",
//                "Canada",
//                "Brazil",
//                "Italy",
//                "Indonesia",
//                "Poland",
//                "Portugal"
//            ).map { TextTagItem(text = it) }
//
//            binding.tagView.addTagList(tags)
//
//            binding.tagView.setOnTagTapListener(object : OnTagTapListener {
//                override fun onTap(item: TagItem) {
//                    if (item is TextTagItem) {
//                        val countryName = item.text
//                        binding.svSearchView.setText(countryName)
//                        binding.svSearchView.dismissDropDown()
//                        viewModel.searchCountry(countryName)
//                        hideKeyboard()
//                    }
//                }
//            })
//        }
//    }

    private fun observeCountryData() {
        viewModel.selectedCountry.observe(this) { country ->
            if (country != null) {
                val flagUrl = "https://flagcdn.com/w320/${country.iso.lowercase()}.png"
                Glide.with(this).load(flagUrl).into(binding.ivImageView)
                binding.ivImageView.visibility = View.VISIBLE
                binding.tvPhoneCode.text = "Country Code: +${country.phonecode}"
                binding.tvPhoneCode.visibility = View.VISIBLE
                binding.tagView.visibility = View.GONE

                textToSpeech.speak(country.name, TextToSpeech.QUEUE_FLUSH, null)
            } else {
                Toast.makeText(this, "Country not found", Toast.LENGTH_SHORT).show()
                clearCountryInfo()
            }
        }
    }

    private fun clearCountryInfo() {
        binding.ivImageView.setImageDrawable(null)
        binding.ivImageView.visibility = View.GONE
        binding.tvPhoneCode.text = ""
        binding.tvPhoneCode.visibility = View.GONE
        binding.tagView.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.svSearchView.windowToken, 0)
        binding.svSearchView.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    override fun onPause() {
        super.onPause()
        textToSpeech.stop()
    }
}