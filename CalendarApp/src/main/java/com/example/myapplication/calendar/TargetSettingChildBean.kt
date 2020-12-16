package com.example.myapplication.calendar

import com.chad.library.adapter.base.entity.MultiItemEntity

class TargetSettingChildBean(
        var title:String = "",
        var selected:Boolean = false)
    : MultiItemEntity {

    override val itemType: Int
        get() = TargetSettingChildAdapter.ITEM_TYPE_CONTENT


}