package com.example.myapplication.calendar

import com.chad.library.adapter.base.entity.MultiItemEntity

class TargetSettingTimeBean(var title:String = "00:00",var timestamp:Long = 0) : MultiItemEntity {

    override val itemType: Int
        get() = TargetSettingAdapter.ITEM_TYPE_CONTENT
}