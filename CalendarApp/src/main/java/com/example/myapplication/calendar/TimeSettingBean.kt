package com.example.myapplication.calendar

import com.chad.library.adapter.base.entity.MultiItemEntity

class TimeSettingBean(
        var title:String = "" ,
        var selected:Boolean = false
) : MultiItemEntity {
    override val itemType: Int
        get() = TimeSettingAdapter.ITEM_TYPE_CONTENT


}