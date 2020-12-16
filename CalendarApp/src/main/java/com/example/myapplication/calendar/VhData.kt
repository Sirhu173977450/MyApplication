package com.example.myapplication.calendar

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by rzq@jf.com
 * 公共简单布局用的
 */

open class VhData(
        title :String= "",_itemType :Int= 0,
                describe :String= "",anyObject :Any?= null
) : MultiItemEntity {
    var _itemType = _itemType

    var title = title  //标题
    var resoutId = 0
    var describe = describe
    var anyObject = anyObject

    override val itemType: Int
        get() = _itemType
}
