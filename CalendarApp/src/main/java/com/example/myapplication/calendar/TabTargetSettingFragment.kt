package com.example.myapplication.calendar

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.myapplication.librarycalendar.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_target_setting.*


/**
 * Created by yichao.hu on 2020/09/26 21:18.
 */
class TabTargetSettingFragment(
        var mOnItemClickListener: OnTargetSettingItemClickListener,
        var mSelectedTextChangeListener: OnSelectedTextChangeListener,
        //频次1、按天 2、按周 3、按月
        var mType: Int = 1
) : Fragment() {

    fun TabTargetSettingFragment() {}

    val MIN_MARK = 0

    val MAX_MARK = 30

    var mList = arrayListOf<MultiItemEntity>()

    var adapter = TargetSettingChildAdapter(arrayListOf(), mType)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_target_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //第三个tab页面,展示输入框
        if (mType != 3) {
            viewSwitcher.displayedChild = 0
            rc_view.layoutManager = GridLayoutManager(context!!, 4)
            rc_view.adapter = adapter
            adapter.setOnItemChildClickListener { adapter, view, position ->
                if (adapter.getItemViewType(position) == TargetSettingChildAdapter.ITEM_TYPE_CONTENT
                        && view.id == R.id.tv_time) {
                    var result = ""
                    if (mType == 1) {
                        val item = adapter.data[position] as TargetSettingChildBean
                        val lastIsSelected = (adapter.data[adapter.data.size - 1] as TargetSettingChildBean).selected
                        //默认可多选模式
                        item.selected = !item.selected
                        LogUtils.e("TAG______lastIsSelected:" + lastIsSelected)
                        LogUtils.e("TAG______position:" + position)
                        //当点击选择的是每天，除了每天，其他的都设置为不选中
                        if (position == 7 && item.selected) {
                            for (i in adapter.data.indices) {
                                val item = adapter.data[i] as TargetSettingChildBean
                                if (item.title != "每天") {
                                    item.selected = false
                                }
                            }
                        } else {
                            //计算除了每天的item,其他多选中的item的总count数
                            var count = 0
                            for (i in adapter.data.indices) {
                                val item = adapter.data[i] as TargetSettingChildBean
                                if (item.selected && item.title != "每天") {
                                    count++
                                }
                            }
                            //如果默认值选中的每天，此时点击，就需要把每天设置为未选中，每天与其他选项互斥
                            if (lastIsSelected) {
                                (adapter.data[adapter.data.size - 1] as TargetSettingChildBean).selected = false
                            }
                            //如果每天的item未选中，同时其他多选的item全部选中，此时将每天的item选中，其他的互斥
                            if (count == 7) {
                                for (i in adapter.data.indices) {
                                    val item = adapter.data[i] as TargetSettingChildBean
                                    item.selected = item.title == "每天"
                                }
                            }
                            LogUtils.e("TAG______count:" + count)
                        }
                    } else {
                        for (i in adapter.data.indices) {
                            val item = adapter.data[i] as TargetSettingChildBean
                            if (i == position) {
                                item.selected = !item.selected
                                result = item.title
                            } else {
                                item.selected = false
                            }
                        }
                    }
                    mSelectedTextChangeListener.onSelectedTextChangeListener(mType, result)
                    adapter.notifyDataSetChanged()
                }
            }
            getData()
        } else {
            viewSwitcher.displayedChild = 1
            et_editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && !s.equals("")) {
                        var markVal = 0
                        markVal = try {
                            s.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }
                        if (markVal > MAX_MARK) {
                            Toast.makeText(context,"天数不能超过30",Toast.LENGTH_SHORT).show()
//                            ToastUtils.show("天数不能超过30")
                            et_editText.setText(MAX_MARK.toString())
                        }
                        return
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (start > 1) {
                        val num = Integer.parseInt(s.toString());
                        if (num > MAX_MARK) {
                            et_editText.setText(MAX_MARK.toString())
                        } else if (num < MIN_MARK)
                            et_editText.setText(MIN_MARK.toString())
                        return
                    }
                    mSelectedTextChangeListener.onSelectedTextChangeListener(mType, et_editText.text.toString().trim())
                }
            })
        }
    }

    //获取EditText 内容
    fun getEditTextContent(): ArrayList<String> {
        val stringJson= arrayListOf<String>()
         if (mType in 1..2) {
            for (item in adapter.data) {
                val bean = (item as TargetSettingChildBean)
                if (bean.selected) {
                    stringJson.add(bean.title)
                }
            }
        } else {
            val result = et_editText.text.toString().trim()
             if (!TextUtils.isEmpty(result)) {
                 stringJson.add(result)
             } else {
                 stringJson.add("1")
             }
        }
//        return Gson().toJson(stringJson)
        return stringJson
    }

    //获取getType
    fun getType(): Int {
        return mType
    }

    private fun getData() {
        mList.clear()
        when (mType) {
            1 -> {
                mList.add(TargetSettingChildBean("周一"))
                mList.add(TargetSettingChildBean("周二"))
                mList.add(TargetSettingChildBean("周三"))
                mList.add(TargetSettingChildBean("周四"))
                mList.add(TargetSettingChildBean("周五"))
                mList.add(TargetSettingChildBean("周六"))
                mList.add(TargetSettingChildBean("周日"))
                mList.add(TargetSettingChildBean("每天", true))

            }
            2 -> {
                mList.add(TargetSettingChildBean("1"))
                mList.add(TargetSettingChildBean("2"))
                mList.add(TargetSettingChildBean("3", true))
                mList.add(TargetSettingChildBean("4"))
                mList.add(TargetSettingChildBean("5"))
                mList.add(TargetSettingChildBean("6"))
            }
        }
        adapter.setList(mList)
    }

}