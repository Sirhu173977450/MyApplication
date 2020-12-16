package com.example.myapplication.calendar

interface OnTargetSettingItemClickListener{
    fun onItemClick(type:Int,result:String){}
    fun onDialogHintClick(type:Int,targetId:String){}
    fun onItemClick(type: Int, targetId:String,moodId:String){}
    fun onItemClick(type:Int,result:ArrayList<String>){}
    fun onPunchComplete(){}
}