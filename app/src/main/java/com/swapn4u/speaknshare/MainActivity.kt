package com.swapn4u.speaknshare

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import android.view.View
import com.google.android.gms.ads.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var selectedLanguage = "en_IN"
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val languages = Locale.getAvailableLocales().map { locale ->
//            Languages(locale.displayLanguage,locale.language)
//        }
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        //https://stackoverflow.com/questions/7973023/what-is-the-list-of-supported-languages-locales-on-android


        btnSelectLanguages.setOnClickListener{
            val alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setTitle("Select a language")
            val items =
                    arrayOf("English", "Hindi", "Marathi", "Gujarati", "Kannada", "Tamil", "Malayalam", "Nepali", "Telugu", "Urdu")
            val locale_list = arrayOf("en_IN", "hi_IN", "mr-IN", "gu_IN", "kn_IN", "ta_IN", "ml_IN", "ne_IN", "te_IN", "ur_IN")
            val checkedItem = 0
            alertDialog.setSingleChoiceItems(
                    items,
                    checkedItem,
                    DialogInterface.OnClickListener { dialog, which ->
                        selectedLanguage = locale_list[which]
                        configureSpeakToWriteAction(selectedLanguage)
                        btnSelectLanguages.text = items[which]
                        dialog.dismiss()
                    })
            val alert: android.app.AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }


        btnTapToSpeak.setOnClickListener{
            configureSpeakToWriteAction(selectedLanguage)
        }

        btnShareText.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, tfSpeakingText.text.toString())
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        btnCopyText.setOnClickListener{
            val textToCopy = tfSpeakingText.text
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }

        //
        mAdView = findViewById(R.id.adView)
        mAdView.visibility = View.GONE
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

    }

    //CONFIGURE SPEAK TO WRITE OPRATION
    fun configureSpeakToWriteAction(locale: String) {
        // Get the Intent action
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Language model defines the purpose, there are special models for other use cases, like search.
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // Adding an extra language, you can use any language from the Locale class.
//        val locale = Locale(locale)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
        // Text that shows up on the Speech input prompt.
//        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "मीटिंगचा मायना लिहण्यासाठी बोला ")
        try {
            // Start the intent for a result, and pass in our request code.
            startActivityForResult(sttIntent, MainActivity.REQUEST_CODE_STT)

        } catch (e: ActivityNotFoundException) {
            // Handling error when the service is not available.
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Handle the result for our request code.
            REQUEST_CODE_STT -> {
                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        // Do what you want with the recognized text.
                        tfSpeakingText.setText(tfSpeakingText.text.toString() + recognizedText)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    fun configureLanguages() {
        //1
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase?.getReference("Festivals")

        //2
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {

            }
        })
    }
}