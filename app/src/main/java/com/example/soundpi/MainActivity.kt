package com.example.soundpi

import android.Manifest.permission.*
import android.app.Activity
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

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

        }else{
            Toast.makeText(this, "Device has been connected", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var searchList : ArrayList<BluetoothDevice>

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    checkPermission()
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        checkPermission()
        setContentView(R.layout.activity_main)

        val searchBluetoothBtn = findViewById<Button>(R.id.search_bluetooth_device_btn)
        val bluetoothManager =  getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter : BluetoothAdapter? = bluetoothManager.adapter
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices


        Log.i("CONNECTED DEVICES",pairedDevices.toString())

        if(bluetoothAdapter == null){
            Toast.makeText(this, "This Device does not have Bluetooth", Toast.LENGTH_SHORT).show()
        }

        searchBluetoothBtn?.setOnClickListener {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
                val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetooth.launch(bluetoothIntent)
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        super.onCreate(savedInstanceState)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(this, "Esta OK", Toast.LENGTH_SHORT).show()
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this, "No esta OK ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(baseContext, ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(ACCESS_BACKGROUND_LOCATION),
                    100 )
            }
            if (ContextCompat.checkSelfPermission(baseContext, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_CONNECT),
                    101 )
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

}

