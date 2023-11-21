package com.example.soundpi

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter : BluetoothAdapter

    @SuppressLint("MissingPermission")
    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            bluetoothAdapter.startDiscovery()
        }else{
            Toast.makeText(this, "Device has been connected", Toast.LENGTH_SHORT).show()
        }
    }

    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    Log.i("APP_STATUS","DEVICE FOUND : ${device!!.name}")
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission()
        }
        setContentView(R.layout.activity_main)

        val searchBluetoothBtn = findViewById<Button>(R.id.search_bluetooth_device_btn)
        val bluetoothManager =  getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        searchBluetoothBtn?.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetooth.launch(bluetoothIntent)
            }
            else
            {
                bluetoothAdapter.startDiscovery()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(baseContext, ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(ACCESS_BACKGROUND_LOCATION, ACCESS_COARSE_LOCATION),
                    100 )
            }
            if (ContextCompat.checkSelfPermission(baseContext, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_CONNECT),
                    101 )
            }
            if (ContextCompat.checkSelfPermission(baseContext, BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_SCAN),
                    102 )
            }
            if (ContextCompat.checkSelfPermission(baseContext, BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_ADMIN),
                    103 )
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

}

