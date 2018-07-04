package com.example.aravind_pt1748.quizapp102_kotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DBHelper(context : Context?, name : String?, factory : SQLiteDatabase.CursorFactory?, version : Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    val db = this.writableDatabase
    val readdb = this.readableDatabase

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DBHelper","onUpgrade in DBHelper called")
        if(db!=null){
            db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
            onCreate(db)
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DBHelper","onCreate in DBHelper called")
        val CREATE_QUESTIONS_TABLE = "CREATE TABLE $TABLE_QUESTIONS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_QUESTION TEXT,$COLUMN_ANSWER TEXT,$COLUMN_CHOICE1 TEXT,$COLUMN_CHOICE2 TEXT,$COLUMN_CHOICE3 TEXT,$COLUMN_CHOICE4 TEXT);"
        val CREATE_QUESTIONS_TABLE_TF = "CREATE TABLE $TABLE_QUESTIONS_TF ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_QUESTION TEXT,$COLUMN_ANSWER TEXT);"
        if (db != null) {
            db.execSQL(CREATE_QUESTIONS_TABLE)
            db.execSQL(CREATE_QUESTIONS_TABLE_TF)
        }
        else{
            Log.d("onCreate_DBHelper","onCreate in DBHelper cant be run as db is null")
        }
    }


    /*
        Function addQuestion has one job. It takes the incoming question object.
        It then copies these values into a content-value object.
        And then it inserts the content-value object into the table in the SQLiteDatabase.
        There r no content providers involved here.
     */
    fun addQuestion(question : MCQHolder) {
        Log.d("DBHelper","addQuestion called : ${question.Id} & ${question.question} & ${question.answer}")
        val values = ContentValues()
        values.put(COLUMN_QUESTION,question.question)
        values.put(COLUMN_ANSWER,question.answer)
        values.put(COLUMN_CHOICE1,question.choices[0])
        values.put(COLUMN_CHOICE2,question.choices[1])
        values.put(COLUMN_CHOICE3,question.choices[2])
        values.put(COLUMN_CHOICE4,question.choices[3])
        db.insert(TABLE_QUESTIONS,null,values)
    }

    fun addTFQuestion(question : TFHolder) {
        Log.d("DBHelper","addQuestion called : ${question.Id} & ${question.question} & ${question.answer}")
        val values = ContentValues()
        values.put(COLUMN_QUESTION,question.question)
        values.put(COLUMN_ANSWER,question.answer)
        db.insert(TABLE_QUESTIONS_TF,null,values)
    }

    fun findQuestion(question1 : String) : MCQHolder? {
        Log.d("DBHelper","findQuestion in DBHelper called")
        val query = "SELECT * FROM $TABLE_QUESTIONS WHERE $COLUMN_QUESTION = $question1"
        val cursor = readdb.rawQuery(query, null)
        var question : MCQHolder? = null
        if(cursor.moveToFirst()){
            cursor.moveToFirst()
            val Id = Integer.parseInt( cursor.getString (cursor.getColumnIndex(COLUMN_ID) ) )
            val questionText = cursor.getString( cursor.getColumnIndex(COLUMN_QUESTION) )
            val answerText = cursor.getString( cursor.getColumnIndex(COLUMN_ANSWER) )
            val choice1 = cursor.getString(cursor.getColumnIndex(COLUMN_CHOICE1))
            val choice2 = cursor.getString(cursor.getColumnIndex(COLUMN_CHOICE2))
            val choice3 = cursor.getString(cursor.getColumnIndex(COLUMN_CHOICE3))
            val choice4 = cursor.getString(cursor.getColumnIndex(COLUMN_CHOICE4))
            val choices = arrayOf(choice1,choice2,choice3,choice4)
            question = MCQHolder(Id, questionText, choices, answerText)
            cursor.close()
        }
        readdb.close()
        return question
    }

    fun deleteQuestion(question : String) : Boolean{
        Log.d("DBHelper","deleteQuestion() in DBHelper called")
        var result = false
        val query = "SELECT * FROM $TABLE_QUESTIONS WHERE $COLUMN_QUESTION = $question"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            db.delete(TABLE_QUESTIONS, "$COLUMN_QUESTION = ?", arrayOf(question) )
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    companion object {
        const val DATABASE_NAME = "QUESTIONS.db"
        const val DATABASE_VERSION = 1
        const val TABLE_QUESTIONS = "Questions"
        const val TABLE_QUESTIONS_TF = "TF_Questions"

        const val COLUMN_ID = "ID"
        const val COLUMN_QUESTION = "Question"
        const val COLUMN_ANSWER = "Answer"
        const val COLUMN_CHOICE1 = "Choice1"
        const val COLUMN_CHOICE2 = "Choice2"
        const val COLUMN_CHOICE3 = "Choice3"
        const val COLUMN_CHOICE4 = "Choice4"
    }

}