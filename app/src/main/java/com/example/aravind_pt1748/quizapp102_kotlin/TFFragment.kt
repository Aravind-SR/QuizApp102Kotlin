package com.example.aravind_pt1748.quizapp102_kotlin

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tf_fragment_main.*
import kotlinx.coroutines.experimental.*
import org.json.JSONObject
import java.util.*

/*
    Function List
    . onCreateView
    . onActivityCreated
    . setButtonClicks
    . populateFragment
    . storeData
    . checkAnswer
    . changeScore
    . showDialog
    . nextQuestion
 */

class TFFragment : Fragment(), PrePostExecution {

    var qId : Int = -1
    lateinit var question : String
    var answer : Boolean? = null
    private val REQUEST_CODE = 909
    private var tfList : ArrayList<TFHolder> = arrayListOf()
    var count = 0
    var mcqBackup : String? = null
    var dbhelper : DBHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tf_fragment_main,container,false)
        dbhelper = DBHelper(context,DBHelper.DATABASE_NAME,null,DBHelper.DATABASE_VERSION)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        textView_score.text = "0"
        setButtonClicks()
        if(savedInstanceState!=null){
            restoreData(savedInstanceState)
        }
        else{
            nextQuestion(null)
           // LoadDataFromURL(this).execute("https://opentdb.com/api.php?amount=20&type=boolean")
            loadDataAsyncInOrder("https://opentdb.com/api.php?amount=20&type=boolean")

        }
        val move = ScrollingMovementMethod()
        textView_question_tf.movementMethod = move
    }

    private fun setButtonClicks(){
        button_false.setOnClickListener{
            checkAnswer(false)
        }
        button_true.setOnClickListener{
            checkAnswer(true)
        }
        button_backhome.setOnClickListener {
            startActivity(Intent(context,WelcomeActivity::class.java))
        }

    }

    private fun populateFragment(question : TFHolder) {
        Log.d("TFFrag","populateFragment() in TF called")
        textView_question_tf.text = question.question
        storeData(question)
    }

    private fun storeData(questionHolder: TFHolder) {
        Log.d("TFFrag","storeData() in TF called")
        question = questionHolder.question
        qId = questionHolder.Id
        answer = questionHolder.answer
    }

    private fun checkAnswer(selectedAnswer : Boolean) {
        Log.d("TFFrag","checkAnswer() in TF called")
        val isAnswerRight = (selectedAnswer == answer)
        if(isAnswerRight) {
            changeScore()
        }
        showDialog(isAnswerRight)
    }

    private fun changeScore(){
        Log.d("TFFrag","changeScore() in TF called")
        val oldScore = Integer.parseInt(textView_score.text.toString())
        val newScore = oldScore + 10
        textView_score.text = newScore.toString()
    }

    private fun showDialog(answerCorrectly : Boolean) {
        Log.d("TFFrag","showDialog() in TF called")
        val fm = activity?.supportFragmentManager
        val ft = fm?.beginTransaction()
        val dialogFrag = AnswerDialogFragment.newInstance(answerCorrectly,answer.toString().capitalize())
        dialogFrag.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
        dialogFrag.setTargetFragment(this,REQUEST_CODE)
        dialogFrag.isCancelable = false
        dialogFrag.show(ft,"Dialog")
    }

    fun nextQuestion(question : TFHolder?) {
        Log.d("TFFrag","nextQuestion($question) in TF called")
        //val index : Int = chooseQuestion()
        //val question : MCQHolder = getQuestion(index)
        if(question==null) {
            populateFragment(TFHolder(1, "ur name is Aravind", true))
        }
        else{
            populateFragment(question)
        }
        textView_question_tf.scrollTo(0,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("TFFrag","onActivityResult() in TF called")
        super.onActivityResult(requestCode, resultCode, data)
        count++
        if(requestCode == REQUEST_CODE){
            if(count<10){
                nextQuestion(tfList.get(count))
                dbhelper!!.addTFQuestion(tfList.get(count))
            }
            else{
                displayThankYouScreen()
            }
        }
    }

    private fun displayThankYouScreen(){
        Log.d("TFFrag","displayThankYou() in TF called")
        main_parent_tf.visibility = View.INVISIBLE
        button_backhome.visibility = View.VISIBLE
        final_screen.visibility = View.VISIBLE
        final_screen.text = "End of game.\n Your final score is ${textView_score.text}.\nHave a nice day!! "
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("TFFrag","onSaveInstanceState() in TF called")
        super.onSaveInstanceState(outState)
        saveData(outState)
    }

    private fun saveData(bundle : Bundle) {
        bundle.putInt("qId",qId)
        //bundle.putString("Question",question)
        bundle.putString("Question",textView_question_tf.text.toString())
        bundle.putBoolean("Answer", answer!!)
        bundle.putInt("Count",count)
        bundle.putInt("Score",Integer.parseInt(textView_score.text.toString()))
        bundle.putString("MCQBackup",mcqBackup)
        Log.d("MCQ_TAG","saveData in MCQFragment called $question, $answer")
    }

    private fun restoreData(bundle : Bundle) {
        Log.d("MCQ_TAG","restoreData in MCQFragment called $bundle")
        mcqBackup = bundle.getString("MCQBackup")
        parseJSONTF(mcqBackup)
        question = bundle.getString("Question")
        qId = bundle.getInt("qId", -1)
        answer = bundle.getBoolean("Answer")
        textView_question_tf.text = bundle.getString("Question","def")
        textView_score.text = bundle.getInt("Score").toString()
        count = bundle.getInt("Count")
        if(count>=10){
            displayThankYouScreen()
        }
        Log.d("MCQ_TAG","Score = ${textView_score.text}")
    }

    fun parseJSONTF(result : String?){
        Log.d("TFFrag","parseJSONTF() in TF called")
        Log.d("ParseJSONTF","ParseJSONTF : argument : $result")
        mcqBackup = result
        val jsonObject_Level1 = JSONObject(result)
        val jsonArray = jsonObject_Level1.getJSONArray("results")
        var i=0
        while ( i<jsonArray.length() ) {
            val ques = jsonArray.getJSONObject(i)
            var question = ques.getString("question")
            question = Html.fromHtml(question , Html.FROM_HTML_MODE_LEGACY).toString()
            var correct_answer = ques.getString("correct_answer")
            correct_answer = Html.fromHtml(correct_answer , Html.FROM_HTML_MODE_LEGACY).toString()
            val mcq = TFHolder(i,question,correct_answer.toBoolean())
            Log.d("ParseJSONTF","mcq : $mcq")
            tfList.add(mcq)
            i++
        }
        nextQuestion(tfList.get(count))
        Log.d("Async","parseJSONTF : $tfList")
    }

    override suspend fun preExec() {
        Log.d("TFFrag","preExec() in TF called")
        main_parent_tf.visibility = View.INVISIBLE
        progress_bar_tf.visibility = View.VISIBLE
    }

    override suspend fun postExec(result: String?) {
        Log.d("TFFrag","postExec() in TF called")
        parseJSONTF(result)
        main_parent_tf.visibility = View.VISIBLE
        progress_bar_tf.visibility = View.INVISIBLE
    }
}