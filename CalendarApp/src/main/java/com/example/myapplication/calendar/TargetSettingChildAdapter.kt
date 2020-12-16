package com.example.myapplication.calendar

import android.graphics.Typeface
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TargetSettingChildAdapter(
        items: MutableList<MultiItemEntity>,type:Int
) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(items) {

    companion object {
        val ITEM_TYPE_CONTENT = 0x501
    }

    init {
        addItemType(ITEM_TYPE_CONTENT, R.layout.item_target_setting_child_content)
        addChildClickViewIds(R.id.tv_time)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (helper.itemViewType) {
            ITEM_TYPE_CONTENT -> {
                setItemTypeLabelData(helper, item as TargetSettingChildBean)
            }
        }
    }

    private fun setItemTypeLabelData(holder: BaseViewHolder, item: TargetSettingChildBean) {
        val tv_time = holder.getView<TextView>(R.id.tv_time)
        tv_time.text = item.title
        tv_time.isSelected = item.selected
        tv_time.setTypeface(null, if(item.selected) Typeface.BOLD else Typeface.NORMAL);
    }
}

