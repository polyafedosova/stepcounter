package com.vsu.fedosova.stepcounter.ui.screens.activity_main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.vsu.fedosova.stepcounter.R
import com.vsu.fedosova.stepcounter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.vsu.fedosova.stepcounter.ui.util.extensions.goToAppSetting
import com.vsu.fedosova.stepcounter.ui.util.extensions.toast

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.fragment_container)
        setupWithNavController(binding.bottomNav, navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STEP_SENSOR_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
                    goToAppSetting()
                }
            } else toast(getString(R.string.dper_restart))
        }
    }

    companion object {
        private const val REQUEST_CODE_STEP_SENSOR_PERMISSION = 1
    }
}