package com.vsu.fedosova.stepcounter.ui.screens.activity_main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.robinhood.ticker.TickerUtils
import com.squareup.picasso.Picasso
import com.vsu.fedosova.stepcounter.R
import com.vsu.fedosova.stepcounter.databinding.FragmentStepsBinding
import com.vsu.fedosova.stepcounter.services.StepCounterService
import com.vsu.fedosova.stepcounter.ui.screens.activity_main.adapter.AdapterSteps
import com.vsu.fedosova.stepcounter.ui.screens.dialog_settings.DialogSettings
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

const val apiKey = "48974e2ce5894ee988f131649230805"
const val apiKeyYandex = "aa5abd0a-9c35-44af-8bbe-ffd06bc8072b"

class StepsFragment : Fragment() {
    private lateinit var adapter: WeatherAdapter
    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val model: MainViewModel by activityViewModels()

    private val viewModel: MainActivityViewModel by activityViewModels()
    private val stepAdapter: AdapterSteps by lazy { AdapterSteps() }
    private var _binding: FragmentStepsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkPermission()
        updateCurrentCard()
        updateListHours()

        with(binding) {
            rvStatistikaShagov.apply {
                adapter = stepAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            tvTikerMeters.setCharacterLists(TickerUtils.provideNumberList())
            tvTikerKalorii.setCharacterLists(TickerUtils.provideNumberList())

            btSave.setOnClickListener {
                viewModel.saveDataForTheDay()
            }
            btDelete.setOnClickListener { showSnackBarDelete(it) }
            btSetting.setOnClickListener { DialogSettings.show(childFragmentManager) }
        }
        StepCounterService.startService(requireContext())

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiStatUiState.collect { state ->
                when (state) {
                    is MainActivityUiState.NoStatisticsData -> {
                        binding.rvStatistikaShagov.visibility = View.INVISIBLE
                        binding.tvNoStatisticsData.visibility = View.VISIBLE
                    }

                    is MainActivityUiState.YesStatisticsData -> {
                        binding.rvStatistikaShagov.visibility = View.VISIBLE
                        binding.tvNoStatisticsData.visibility = View.INVISIBLE
                        stepAdapter.submitList(state.data)
                    }

                    is MainActivityUiState.NoUserData -> {
                        binding.tvNoUserData.visibility = View.VISIBLE
                        DialogSettings.show(childFragmentManager)
                    }

                    is MainActivityUiState.YesUserData -> {
                        binding.tvNoUserData.visibility = View.INVISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.stepCounterUiState.collect { data ->
                binding.tickerAndProgrss.renderParams(data.progress, data.steps, data.stepPlane)
                binding.tvTikerMeters.text = data.km
                binding.tvTikerKalorii.text = data.kkal
            }
        }
    }

    private fun showSnackBarDelete(view: View) {
        Snackbar.make(view, getString(R.string.snack_bar_delete_header), Snackbar.LENGTH_LONG)
            .apply {
                anchorView = view
                setTextColor(ContextCompat.getColor(requireContext(), R.color.snow))
                setActionTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.heavy_clouds))
//                setAction(getString(R.string.snack_bar_delete_action)) { viewModel.clearDatabase() }
                setAction(getString(R.string.snack_bar_delete_action)) {
                    ContextCompat.getColor(requireContext(), R.color.heavy_clouds).let { color ->
                        setActionTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        setBackgroundTint(color)
                    }
                    viewModel.clearDatabase()
                }
            }.show()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveDataForNow()
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }



    private fun checkLocation(){
        if (isLocationEnabled()){
            getLocation()
        } else{
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled() : Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation(){
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }

    //Главное окно
    @SuppressLint("SetTextI18n")
    private fun updateCurrentCard()= with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            val tempMaxMin = "${it.maxTemp}°C ~ ${it.minTemp}°C"
            tvData.text = it.time
            tvCity.text = it.city
            if (it.currentTemp.isEmpty())
                tvCurrentTemp.text = tempMaxMin
            else
                tvCurrentTemp.text = "${it.currentTemp} °C"
            tvCondition.text = String(it.condition.toByteArray(Charsets.ISO_8859_1))
            tvMaxMin.text = if(it.currentTemp.isEmpty()) "" else tempMaxMin
            Picasso.get().load("https:" + it.imageUrl).into(imWeather)
        }
    }

    private fun updateListHours()= with(binding){
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
            Log.d("HoursFragmentTag", "hours: ${it.hours}")
        }
    }

    private fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter(null)
        rcView.adapter = adapter
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(wItem.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                wItem.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"),
                "",
            )
            list.add(item)
        }
        return list
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            // ToDo: если пользователь не дал разрешение, уведомить его об этом и о последствиях
        }
    }

    private fun requestWeatherData(city: String) {

        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                apiKey +
                "&q=" +
                city +
                "&days=" +
                "7" +
                "&lang=ru" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result -> parseWeatherData(result)
            },
            { error ->
                Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject) : List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val city = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                city,
                day.getString("date"),
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel){


        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            weatherItem.hours
        )

        model.liveDataCurrent.value = item
        Log.d("MyLog", "City: ${item.city}")
        Log.d("MyLog", "Time: ${item.time}")
        Log.d("MyLog", "Condition: ${item.condition}")
        Log.d("MyLog", "CurrentTemp: ${item.currentTemp}")
        Log.d("MyLog", "ImageUrl: ${item.imageUrl}")
        Log.d("MyLog", "--------")
        Log.d("MyLog", "MaxTemp: ${item.maxTemp}")
        Log.d("MyLog", "MinTemp: ${item.minTemp}")
        Log.d("MyLog", "Hours: ${item.hours}")

        Log.d("MyLog", "Condition: ${String(item.condition.toByteArray(Charsets.ISO_8859_1))}")
    }

}