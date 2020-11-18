package com.example.ilsm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType(){
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        //setup

        setup(email?:"",provider?:"")
        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()
    }
    private fun setup(email : String, provider : String){
        title="Inicio"
        emailTextView.text = email
        providerTextView.text = provider
        LogOutButton.setOnClickListener(){
            val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
        recordButton.setOnClickListener(){
            showRecord()
        }
    }
    private fun showRecord(){
        val recordIntent = Intent(this, RecordActivity::class.java).apply {

        }
        startActivity(recordIntent)

    }
}