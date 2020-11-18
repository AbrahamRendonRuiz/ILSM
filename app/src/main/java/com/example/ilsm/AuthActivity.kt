package com.example.ilsm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
            bundle.putString("message","Integracion de firebase completa")
            analytics.logEvent("InitScreen", bundle)
        //setup
        setup()
        //sesion
        sesion()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }
    private fun sesion(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider", null)
        if (email != null && provider !=null){
            authLayout.visibility = View.INVISIBLE
            showHome(email,ProviderType.valueOf(provider))
        }
    }
    private fun setup(){
        title = "LogIn"
        signUpButton.setOnClickListener(){
            if(emailTxt.text.isNotEmpty() && pswTxt.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailTxt.text.toString(), pswTxt.text.toString())
                    .addOnCompleteListener(){
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email  ?:"", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
            }
        }
        loginButton.setOnClickListener(){
            if(emailTxt.text.isNotEmpty() && pswTxt.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailTxt.text.toString(), pswTxt.text.toString())
                    .addOnCompleteListener(){
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email  ?:"", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
            }
        }
        googleButton.setOnClickListener(){
            //configuracion
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autentificando al usuario")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show();
    }
    private fun showHome(email: String, provider : ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==  GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(){
                        if (it.isSuccessful){
                            showHome(account.email ?: "",ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert()
            }

        }
    }
}