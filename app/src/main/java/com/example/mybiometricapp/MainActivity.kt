package com.example.mybiometricapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SharedMemory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BiometricAuthCallback {

    companion object{
        private const val USER_DATA = "user_data"
        private const val NAME = "Name"
        private const val EMAIL = "Email"
        private const val PHONE = "Phone"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkBiometricCapability()
        showBiometricPrompt()
        fillUserData()
        saveUserData()
        bt_save_data.setOnClickListener { saveUserData() } //guardar con eventos de click
    }

    override fun onSuccess() { //huella reconocida correctamente
        layout_parent.visibility = View.VISIBLE
    }

    override fun onError() {
        finish() //si se sobrepasan los intentos cierro la app directamente
    }

    override fun onNotRecognized() {
        Log.d("MainActivity", "Huella no reconocida")
    }

    private fun checkBiometricCapability(){//comprobar si el dispositivo admite biometría
        if (!BiometricUtils.isDeviceReady(this)){
            finish()
        }else{
            Toast.makeText(this, "Biometría disponible", Toast.LENGTH_LONG).show()
        }
    }

    private fun showBiometricPrompt(){//
       BiometricUtils.showPrompt(
           activity = this,
           callback = this,
       )
    }

    private fun fillUserData(){//relena con los datos de las sharedpreferences si los hay
        val sharedPreferences: SharedPreferences
                = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        et_name.setText(sharedPreferences.getString(NAME,""))
        et_email.setText(sharedPreferences.getString(EMAIL,""))
        et_phone.setText(sharedPreferences.getString(PHONE,""))
    }

    private fun saveUserData(){//
        val sharedPreferences: SharedPreferences
                = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(NAME, et_name.toString())
        editor.putString(EMAIL, et_email.toString())
        editor.putString(PHONE, et_phone.toString())
        editor.apply()
    }
}