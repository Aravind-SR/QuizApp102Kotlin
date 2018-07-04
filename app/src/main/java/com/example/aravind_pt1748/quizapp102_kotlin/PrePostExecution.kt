package com.example.aravind_pt1748.quizapp102_kotlin

interface PrePostExecution {
    suspend fun preExec()
    suspend fun postExec(result : String?)
}