package com.ksolutions.whatNeed.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.ksolutions.whatNeed.R
import com.ksolutions.whatNeed.models.UserModel
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.concurrent.TimeUnit


class SignInActivity : BaseActivity() {

    private lateinit var phoneVal : String
    private lateinit var phone: EditText
    private lateinit var OTP : String

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sign_in)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        mAuth = FirebaseAuth.getInstance()

        phone = findViewById(R.id.et_mobile_sign_in)

        var intent = Intent(this, MainActivity::class.java)

        var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken)
            {
                et_mobile_sign_in.isEnabled = false
                sign_in_desc_text.visibility = VISIBLE
                sign_in_otp.visibility = VISIBLE
                btn_sign_in.visibility = VISIBLE
                OTP = verificationId
                hideProgressDialog()
            }
            override fun onVerificationCompleted(credential: PhoneAuthCredential)
            {
                //Toast.makeText(applicationContext, "Verification Completed", Toast.LENGTH_LONG).show()
                showErrorSnackBar("Verification Completed",false)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException)
            {
                // This callback is invoked if an invalid request for verification is made,
                // for instance if the the phone number format is invalid
                hideProgressDialog()

                if (e is FirebaseAuthInvalidCredentialsException)
                    showErrorSnackBar("Please enter valid Phone number.",true)
                else if (e is FirebaseTooManyRequestsException)
                    // The SMS quota for the project has been exceeded
                    showErrorSnackBar("Limit Exceeded",true)
                else
                    showErrorSnackBar("Facing Problem..Please try again",true)

                Toast.makeText(applicationContext, phoneVal, Toast.LENGTH_LONG).show()
            }
        }

        btn_get_otp.setOnClickListener {
            phoneVal = phone.text.toString().trim{ it<=' ' }

            if(validate(phone.text.toString()))
            {
                showProgressDialog(resources.getString(R.string.please_wait))

                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneVal) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

                //PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneVal, 60, TimeUnit.SECONDS, this, callbacks)
            }
        }

        btn_sign_in.setOnClickListener(){
            if(sign_in_otp.text.toString().isEmpty())
            {
                showErrorSnackBar("Please enter the OTP.",true)
                sign_in_otp.requestFocus()
            }
            else
            {
                showProgressDialog(resources.getString(R.string.please_wait))
                val credential = PhoneAuthProvider.getCredential(OTP, sign_in_otp.text.toString())
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun validate(phone: String):Boolean
    {
        return when {
            TextUtils.isEmpty(phone) ->{
                showErrorSnackBar("Please enter Mobile Number.",true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful)
                    {
                        // Sign in success, update UI with the signed-in user's information
                        //Toast.makeText(applicationContext, "OTP Verified Successfully", Toast.LENGTH_LONG).show()
                        if (task.result.additionalUserInfo!!.isNewUser)
                        {
                            hideProgressDialog()
                            showErrorSnackBar("Mobile Number isn't Registered Please Sign UP first.",true)
                            Handler().postDelayed({
                                deleteUser()
                            },2500)
                            hideProgressDialog()
                        }
                        else
                        {
                            //Toast.makeText(applicationContext, "Mobile Number Registered", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                        val user = task.result.user
                        // ...
                    }
                    else
                    {
                        hideProgressDialog()
                        showErrorSnackBar("OTP Verification Failed..",true)
                        if (task.exception is FirebaseAuthInvalidCredentialsException)
                        {
                            showErrorSnackBar("Please Enter Valid OTP..",true)
                            //Toast.makeText(applicationContext, "Invalid OTP", Toast.LENGTH_LONG).show()
                        }
                    }
                }
    }

    private fun deleteUser()
    {
        //Toast.makeText(applicationContext, "Deleting the User", Toast.LENGTH_LONG).show()
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        currentUser!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                //Toast.makeText(applicationContext, "Mobile Number Deleted Successfully", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
            else
            {
                Toast.makeText(applicationContext, "Faced Any Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signInSuccess(userModel: UserModel)
    {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar()
    {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }
}