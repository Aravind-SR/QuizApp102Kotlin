package com.example.aravind_pt1748.quizapp102_kotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Window

class TFActivity : AppCompatActivity() {

    lateinit var newFrag : Fragment
    var isShowingDialog : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState!=null){
            loadFragment(supportFragmentManager.getFragment(savedInstanceState,"TFFragment") as TFFragment)
            isShowingDialog = savedInstanceState.getBoolean("showTF")
        }
        else{
            loadFragment(null)
        }
        if(isShowingDialog){
            showExitDialog()
        }
    }

    private fun loadFragment(tfFrag : TFFragment?) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag("Main")
        if(tfFrag == null) {
            newFrag = TFFragment()
        }
        else{
            newFrag = tfFrag
        }
        if(prev==null) {
            ft.add(R.id.frame_main, newFrag, "Main")
        }
        else{
            ft.replace(R.id.frame_main, newFrag, "Main")
        }
        ft.commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState,"TFFragment",newFrag)
        outState?.putBoolean("showTF",isShowingDialog)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getBoolean("showTF")
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
                .setMessage("Sure you want to go back to main menu?")
                .setTitle("Exiting.....")
                .setPositiveButton("Yes")
                {
                    dialog, which ->
                    super.onBackPressed()
                    this.isShowingDialog = false
                }
                .setNegativeButton("No")
                {
                    dialog, which ->
                    this.isShowingDialog = false
                }
                .create()
                .show()
        this.isShowingDialog = true
    }

}
