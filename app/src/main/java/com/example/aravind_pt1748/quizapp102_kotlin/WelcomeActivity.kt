package com.example.aravind_pt1748.quizapp102_kotlin

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import kotlinx.android.synthetic.main.welcome_main.*

class WelcomeActivity : AppCompatActivity() {

    var isShowingDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_main)

        button_mcq.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            startActivity(Intent(this, TFActivity::class.java))
        }
        window.exitTransition = Slide()
        if (savedInstanceState != null) {
            isShowingDialog = savedInstanceState.getBoolean("show")
        }
        if (isShowingDialog) {
            showExitDialog()
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
        .setMessage("Sure you want to exit the app?")
        .setTitle("Exiting.....")
        .setPositiveButton("Yes")
        {
            dialog, which ->
            super.onBackPressed()
            isShowingDialog = false
        }
        .setNegativeButton("No")
        {
            dialog, which ->
            isShowingDialog = false
        }
        .create()
        .show()
        isShowingDialog = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("show",isShowingDialog)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getBoolean("show")
    }
}
