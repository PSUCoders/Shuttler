package com.psucoders.shuttler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_authentication.*

class AuthenticationActivity : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val customText =
                "We've send you a confirmation email to ${currentUser?.email}, " +
                "if you didn't receive it please check in spam . If you already " +
                "authenticated your account please click on the button below"

        confirmationTextView.text = customText
    }

    /**
     * Check if user is verified
     * @param v Done button
     * @return void.
     */
    fun verifyEmail(v: View) {
        if (currentUser?.isEmailVerified == true) {
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }
        else {
            Snackbar.make(rootLayoutAuthentication, "Please verify your account", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser?.reload()
        currentUser?.getIdToken(true)
    }
}
