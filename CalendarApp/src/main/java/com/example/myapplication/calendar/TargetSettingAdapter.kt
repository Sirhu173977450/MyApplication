package com.example.myapplication.calendar

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TargetSettingAdapter(
        items: MutableList<MultiItemEntity>
) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(items) {

    companion object {
        const val ITEM_TYPE_Add_IMAGE = 0x501
        const val ITEM_TYPE_CONTENT = 0x502
    }

    init {
        addItemType(ITEM_TYPE_Add_IMAGE, R.layout.item_target_setting_add)
        addItemType(ITEM_TYPE_CONTENT, R.layout.item_target_setting_content)
        addChildClickViewIds(R.id.cl_add)
        addChildClickViewIds(R.id.iv_delete)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (helper.itemViewType) {
            ITEM_TYPE_Add_IMAGE -> {
                setItemTypeLabelImage(helper, item as VhData)
            }
            ITEM_TYPE_CONTENT -> {
                setItemTypeLabelData(helper, item as TargetSettingTimeBean)
            }
        }
    }

    private fun setItemTypeLabelImage(holder: BaseViewHolder, item: VhData) {
    }

    private fun setItemTypeLabelData(holder: BaseViewHolder, item: TargetSettingTimeBean) {
        holder.setText(R.id.tv_time,item.title)
    }
}

