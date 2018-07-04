package com.example.aravind_pt1748.quizapp102_kotlin

import android.os.AsyncTask
import android.os.Build
import android.support.v4.app.Fragment
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.tf_fragment_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder

/*
class LoadDataFromURL(mcqFrag : Fragment) : AsyncTask<String,Void,String> () {

    val callingFragment = mcqFrag

    override fun onPreExecute() {
        super.onPreExecute()
        if(callingFragment is MCQFragment) {
            callingFragment.main_parent.visibility = View.INVISIBLE
            callingFragment.progress_bar.visibility = View.VISIBLE
        }
        else if(callingFragment is TFFragment) {
            callingFragment.main_parent_tf.visibility = View.INVISIBLE
            callingFragment.progress_bar_tf.visibility = View.VISIBLE
        }
    }

    override fun doInBackground(vararg params: String?): String {
        var value = ""
        try {
            val url = URL(params[0])
            val httpConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpConnection.inputStream
            val bufferedReader = InputStreamReader(inputStream)
            value = inputStream.bufferedReader().use(BufferedReader::readText)
            Log.d("Async", " Obtained from URL : $value")
            bufferedReader.close()
        }

        catch (e:MalformedURLException){
            Log.d("Async",e.toString())
        }
        catch (e:IOException){
            Log.d("Async",e.toString()+"IO")
        }
        return value
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.d("Async", " Obtained from URL in onPostExecute : $result")
        if(callingFragment is MCQFragment) {
            callingFragment.parseJSON(result)
            callingFragment.main_parent.visibility = View.VISIBLE
            callingFragment.progress_bar.visibility = View.INVISIBLE
        }
        else if(callingFragment is TFFragment) {
            callingFragment.parseJSONTF(result)
            callingFragment.main_parent_tf.visibility = View.VISIBLE
            callingFragment.progress_bar_tf.visibility = View.INVISIBLE
        }
    }

}
*/

fun loadDataAsync(param : String) : String{
    var value = ""
    try {
        val url = URL(param)
        val httpConnection = url.openConnection() as HttpURLConnection
        val inputStream = httpConnection.inputStream
        val bufferedReader = InputStreamReader(inputStream)
        value = inputStream.bufferedReader().use(BufferedReader::readText)
        Log.d("Async", " Obtained from URL : $value")
        bufferedReader.close()
    }

    catch (e:MalformedURLException){
        Log.d("Async",e.toString())
    }
    catch (e:IOException){
        Log.d("Async",e.toString()+"IO")
    }
    return value
}

fun PrePostExecution.loadDataAsyncInOrder(param : String) {
    launch(UI){
        preExec()
        val result = async {
            loadDataAsync(param)
        }.await()
        postExec(result)
    }

}
