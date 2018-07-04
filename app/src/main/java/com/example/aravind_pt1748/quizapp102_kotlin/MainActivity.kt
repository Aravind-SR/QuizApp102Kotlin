package com.example.aravind_pt1748.quizapp102_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var newFrag : Fragment
    var isShowingDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState!=null){
            loadFragment(supportFragmentManager.getFragment(savedInstanceState,"MCQFragment") as MCQFragment)
            isShowingDialog = savedInstanceState.getBoolean("show")
        }
        else{
            loadFragment(null)
        }
        if(isShowingDialog){
            showExitDialog()
        }
    }

    private fun loadFragment(mcqFrag : MCQFragment?) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag("Main")
        if(mcqFrag == null) {
            newFrag = MCQFragment()
        }
        else{
            newFrag = mcqFrag
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
        supportFragmentManager.putFragment(outState,"MCQFragment",newFrag)
        outState?.putBoolean("show",isShowingDialog)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getBoolean("show")
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

}
