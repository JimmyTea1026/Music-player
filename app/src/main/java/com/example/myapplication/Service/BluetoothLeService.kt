package com.example.myapplication.Service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.myapplication.PlayPage.PlayPageViewModel
import java.util.UUID

class BluetoothLeService : Service() {
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var deviceAddress: String? = null
    private var targetDevice: String = "JimmyBLE"

    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        initializeBluetooth()
        connect()
    }

    private fun initializeBluetooth() {
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter

        Log.i("BLE", "BLE service activate")
    }


    @SuppressLint("MissingPermission")
    private fun connect() {
        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name
            deviceName?.let {
                if (it == targetDevice) {
                    Log.i("BLE", "Find Jimmyble")
                    deviceAddress = result.device.address
                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
                    connectToDevice(deviceAddress!!)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("error", "BLE Scan failed with error code $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(address: String) {
        val device = bluetoothAdapter?.getRemoteDevice(address)
        device?.let {
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
            Log.i("BLE", "JimmyBLE connect success")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        private val serviceUUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
        private val characteristicUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.i("GATT", "Connected")
                gatt?.discoverServices()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if(status == BluetoothGatt.GATT_SUCCESS){
                val targetService = gatt.getService(serviceUUID)
                val targetCharacteristic = targetService.getCharacteristic(characteristicUUID)
                gatt.setCharacteristicNotification(targetCharacteristic, true)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            if(characteristic.uuid == characteristicUUID){
                val cmd = value.contentToString()
                mediaPlayerController(cmd)
                Log.i("", cmd)
            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
    }

    private fun mediaPlayerController(cmd : String){
        when (cmd) {
            "[80]" -> PlayPageViewModel.mediaPlayerStartPause()
            "[68]" -> PlayPageViewModel.setSong(1)
            "[85]" -> PlayPageViewModel.setSong(-1)
        }
    }
}
