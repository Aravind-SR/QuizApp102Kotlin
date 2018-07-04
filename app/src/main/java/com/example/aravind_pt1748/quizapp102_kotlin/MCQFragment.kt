package com.example.aravind_pt1748.quizapp102_kotlin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.experimental.launch
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

class MCQFragment : Fragment(),PrePostExecution {

    var qId : Int = -1
    lateinit var question : String
    lateinit var answer : String
    private val REQUEST_CODE = 101
    private var mcqList : ArrayList<MCQHolder> = arrayListOf()
    var count = 0
    var mcqBackup : String? = null
    var dbhelper : DBHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main,container,false)
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
            //LoadDataFromURL(this).execute("https://opentdb.com/api.php?amount=20&type=multiple")
            launch {
                loadDataAsyncInOrder("https://opentdb.com/api.php?amount=20&type=multiple")
            }
        }
        val move = ScrollingMovementMethod()
        textView_question.movementMethod = move
    }

    private fun setButtonClicks(){
        button_answer1.setOnClickListener{
            checkAnswer(button_answer1.text.toString())
        }
        button_answer2.setOnClickListener{
            checkAnswer(button_answer2.text.toString())
        }
        button_answer3.setOnClickListener{
            checkAnswer(button_answer3.text.toString())
        }
        button_answer4.setOnClickListener{
            checkAnswer(button_answer4.text.toString())
        }
        button_backhome.setOnClickListener {
            startActivity(Intent(context,WelcomeActivity::class.java))
        }
    }

    private fun populateFragment(question : MCQHolder) {
        textView_question.text = question.question
        val rand = generateRandom()
        Log.d("rand","$rand")
        button_answer1.text = question.choices[rand[0]]
        button_answer2.text = question.choices[rand[1]]
        button_answer3.text = question.choices[rand[2]]
        button_answer4.text = question.choices[rand[3]]
        storeData(question)
    }

    private fun storeData(questionHolder: MCQHolder) {
        question = questionHolder.question
        qId = questionHolder.Id
        answer = questionHolder.answer
    }

    private fun generateRandom() : ArrayList<Int> {
        var qList: ArrayList<Int> = arrayListOf()
        var number: Int
        var count = 0
        while (count < 4) {
            number = Random().nextInt(4)
            if (!qList.contains(number)) {
                qList.add(number)
                count++
            }
        }
        return qList
    }


        private fun checkAnswer(selectedAnswer : String) {
        val isAnswerRight = (selectedAnswer == answer)
        if(isAnswerRight) {
            changeScore()
        }
        showDialog(isAnswerRight)
    }

    private fun changeScore(){
            val oldScore = textView_score.text.toString().toInt()
            val newScore = oldScore + 10
            textView_score.text = newScore.toString()
    }

    private fun showDialog(answerCorrectly : Boolean) {
        val fm = activity?.supportFragmentManager
        val ft = fm?.beginTransaction()
        val dialogFrag = AnswerDialogFragment.newInstance(answerCorrectly,answer)
        dialogFrag.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
        dialogFrag.setTargetFragment(this,REQUEST_CODE)
        dialogFrag.isCancelable = false
        dialogFrag.show(ft,"Dialog")

    }

    fun nextQuestion(question : MCQHolder?) {
        //val index : Int = chooseQuestion()
        //val question : MCQHolder = getQuestion(index)
        if(question==null) {
            populateFragment(MCQHolder(1, "WHat is ur name?", arrayOf("Aravind", "Phantom", "Behemoth", "SR"), "Aravind"))
        }
        else{
            populateFragment(question)
        }
        textView_question.scrollTo(0,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        count++
        if(requestCode == REQUEST_CODE){
            //nextQuestion(MCQHolder(3,"Where do u work?", arrayOf("Sastra","DAV","Zoho","KVM"), "Zoho"))
            if(count<10){
            nextQuestion(mcqList.get(count))
            dbhelper!!.addQuestion(mcqList.get(count))
            }
            else{
                displayThankYouScreen()
            }
        }
    }

    private fun displayThankYouScreen(){
        main_parent.visibility = View.INVISIBLE
        button_backhome.visibility = View.VISIBLE
        final_screen.visibility = View.VISIBLE
        final_screen.text = "End of game.\n Your final score is ${textView_score.text}.\nHave a nice day!! "
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveData(outState)
    }

    private fun saveData(bundle : Bundle) {
        bundle.putInt("qId",qId)
        //bundle.putString("Question",question)
        bundle.putString("Question",textView_question.text.toString())
        bundle.putString("Answer",answer)
        bundle.putString("Button1",button_answer1.text.toString())
        bundle.putString("Button2",button_answer2.text.toString())
        bundle.putString("Button3",button_answer3.text.toString())
        bundle.putString("Button4",button_answer4.text.toString())
        bundle.putInt("Count",count)
        bundle.putInt("Score",Integer.parseInt(textView_score.text.toString()))
        bundle.putString("MCQBackup",mcqBackup)
        Log.d("MCQ_TAG","saveData in MCQFragment called $question, $answer")
    }

    private fun restoreData(bundle : Bundle) {
        Log.d("MCQ_TAG","restoreData in MCQFragment called $bundle")
        mcqBackup = bundle.getString("MCQBackup")
        parseJSON(mcqBackup)
        question = bundle.getString("Question")
        qId = bundle.getInt("qId", -1)
        answer = bundle.getString("Answer","def")
        textView_question.text = bundle.getString("Question","def")
        textView_score.text = bundle.getInt("Score").toString()
        button_answer1.text = bundle.getString("Button1")
        button_answer2.text = bundle.getString("Button2")
        button_answer3.text = bundle.getString("Button3")
        button_answer4.text = bundle.getString("Button4")
        count = bundle.getInt("Count")
        if(count>=10){
            displayThankYouScreen()
        }
        Log.d("MCQ_TAG","Score = ${textView_score.text}")
    }

    public fun parseJSON(result : String?) {
        Log.d("ParseJSON", "ParseJSON : argument : $result")
        mcqBackup = result
        if (result != "") {
            val jsonObject_Level1 = JSONObject(result)
            val jsonArray = jsonObject_Level1.getJSONArray("results")
            var i = 0
            while (i < jsonArray.length()) {
                val ques = jsonArray.getJSONObject(i)
                var question = ques.getString("question")
                question = Html.fromHtml(question, Html.FROM_HTML_MODE_LEGACY).toString()
                var correct_answer = ques.getString("correct_answer")
                correct_answer = Html.fromHtml(correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()
                val incorrect_answers = ques.getJSONArray("incorrect_answers")
                var incorrect_1 = incorrect_answers.get(0) as String
                incorrect_1 = Html.fromHtml(incorrect_1, Html.FROM_HTML_MODE_LEGACY).toString()
                var incorrect_2 = incorrect_answers.get(1) as String
                incorrect_2 = Html.fromHtml(incorrect_2, Html.FROM_HTML_MODE_LEGACY).toString()
                var incorrect_3 = incorrect_answers.get(2) as String
                incorrect_3 = Html.fromHtml(incorrect_3, Html.FROM_HTML_MODE_LEGACY).toString()
                val choices = arrayOf(correct_answer, incorrect_1, incorrect_2, incorrect_3)
                val mcq = MCQHolder(i, question, choices, correct_answer)
                Log.d("ParseJSON", "mcq : $mcq")
                mcqList.add(mcq)
                i++
            }
            nextQuestion(mcqList.get(count))
            Log.d("Async", "parseJSON : $mcqList")
        }
    }

    override suspend fun preExec() {
        main_parent.visibility = View.INVISIBLE
        progress_bar.visibility = View.VISIBLE
    }

    override suspend fun postExec(result : String?) {
        parseJSON(result)
        main_parent.visibility = View.VISIBLE
        progress_bar.visibility = View.INVISIBLE
    }
}

