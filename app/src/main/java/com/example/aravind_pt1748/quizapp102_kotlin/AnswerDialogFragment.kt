package com.example.aravind_pt1748.quizapp102_kotlin

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.dialog_answer.*

class AnswerDialogFragment : DialogFragment() {

    companion object {

        const val answerCorrectlyTag = "AnswerCorrectly"
        const val answerTag = "CorrectAnswer"

        fun newInstance(answerCorrectly : Boolean, answer : String) : AnswerDialogFragment {
            val fragment = AnswerDialogFragment()
            val arguments = Bundle()
            arguments.putBoolean(answerCorrectlyTag,answerCorrectly)
            arguments.putString(answerTag,answer)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            //val view = container!!.inflate(R.layout.dialog_answer)
            val view = inflater.inflate(R.layout.dialog_answer,container,false)
            return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val answerStatus = arguments!!.getBoolean(answerCorrectlyTag)
        val animBounce = AnimationUtils.loadAnimation(context,R.anim.bounce)
        answer_image.startAnimation(animBounce)
        if(answerStatus){
            answer_image.setImageResource(R.drawable.tick)
            dialog_answer.text = "Correct"
        }
        else{
            answer_image.setImageResource(R.drawable.wrong)
            dialog_answer.text = "Wrong"
        }
        dialog_answer_actual.text = "Answer is : ${arguments!!.getString(answerTag)}"
        dialog_button_OK.setOnClickListener {
            dismiss()
            targetFragment?.onActivityResult(targetRequestCode,0,null)
        }
    }

}