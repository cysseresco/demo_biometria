package com.example.mybiometricapp

import android.content.ComponentCallbacks
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

interface  BiometricAuthCallback{
    fun onSuccess()
    fun onError()
    fun onNotRecognized()
}

object BiometricUtils {

    fun isDeviceReady(context: Context): Boolean = //desde fuera solo me interesa usar esta
        getCapability(context) == BIOMETRIC_SUCCESS

    fun showPrompt(
        title: String ="Autenticación Biométrica",
        subtitle: String ="Introduce tus credenciales",
        description: String ="Introduce tu huella para verificar que eres tú",
        cancelButton: String ="Cancelar",
        activity: AppCompatActivity, //para poder mostrar la activity
        callback: BiometricAuthCallback //escribe en el callback, no devuelve nada
    ) {
        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(BIOMETRIC_WEAK) //si no se dispone de huella pero tengo otro tipo uso el suplente - BIOMETRIC_WEAK
                .setNegativeButtonText(cancelButton)
                .build() //importante
        val prompt : BiometricPrompt = initPrompt(activity, callback)
        prompt.authenticate(promptInfo)
    }

    private fun initPrompt(activity: AppCompatActivity, callback: BiometricAuthCallback) : BiometricPrompt{
        val executor = ContextCompat.getMainExecutor(activity)
        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback(){

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult)
            {
                super.onAuthenticationSucceeded(result)
                callback.onSuccess()
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                callback.onError()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onNotRecognized()
            }
        }
        return BiometricPrompt(activity, executor, authenticationCallback)
    }

    private fun getCapability(context: Context): Int =
        BiometricManager.from(context).canAuthenticate(BIOMETRIC_WEAK)
        //devuelve BIOMETRIC_SUCCESS si se PUEDE autenticar
        //el biometricmanager debe ser de la librería androidx no de hardware
}
