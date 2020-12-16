package com.example.myapplication.calendar

import android.graphics.Typeface
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TimeSettingAdapter(
        items: MutableList<MultiItemEntity>
) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(items) {

    companion object {
        val ITEM_TYPE_CONTENT = 0x502
    }

    init {
        addItemType(ITEM_TYPE_CONTENT, R.layout.item_time_setting_content)
        addChildClickViewIds(R.id.tv_time)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (helper.itemViewType) {
            ITEM_TYPE_CONTENT -> {
                setItemTypeLabelData(helper, item as TimeSettingBean)
            }
        }
    }

    private fun setItemTypeLabelData(holder: BaseViewHolder, item: TimeSettingBean) {
        val tv_time = holder.getView<TextView>(R.id.tv_time)
        tv_time.text = item.title
        tv_time.isSelected = item.selected
        tv_time.setTypeface(null, if(item.selected) Typeface.BOLD else Typeface.NORMAL);
    }
}

