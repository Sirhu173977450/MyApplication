package com.example.myapplication.calendar

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.myapplication.dao.DataBaseDaoUtils
import com.example.myapplication.librarycalendar.utils.*
import com.example.myapplication.spinner.NiceSpinner
import com.google.gson.Gson
import kotlinx.android.synthetic.main.agenda_setting2.*
import kotlinx.android.synthetic.main.dailog_target_time_setting.view.*
import java.text.SimpleDateFormat
import java.util.*

class EditCalendarActivity2 : FragmentActivity(), OnTargetSettingItemClickListener  {
    private var etTitle: EditText? = null
    private var etDescription: EditText? = null
    private var title: TextView? = null

    // private Spinner spinnerRemind;
    private var spinnerRemind: NiceSpinner? = null
    private var spinnerRepeat: NiceSpinner? = null

    //	private Spinner spinnerRepeat;
    private var llStartTime: LinearLayout? = null
    var tvStartTime: TextView? = null
    private var llEndTime: LinearLayout? = null
    var tvEndTime: TextView? = null
    private var save: Button? = null
    private var cancel: Button? = null
    private var type = 0 // 类型：0为添加，1为更新
    private var text: String? = "" // 聊天界面传过来的值
    private var inflater: LayoutInflater? = null

    /** 日程事件  */
    private var calendarEvent: CalendarEvent2? = null
    var year = 0 // 日历点击时间年
    var month = 0 // 日历点击时间月
    var day = 0 // 日历点击时间日

    var mDaoUtils = DataBaseDaoUtils(this)
    var mList = arrayListOf<MultiItemEntity>()
    var mTimeAdapter = TargetSettingAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agenda_setting2)
        inflater = LayoutInflater.from(this)
        val has_permission = PackageManager.PERMISSION_GRANTED == packageManager
                .checkPermission("android.permission.WRITE_CALENDAR", packageName)
        if (!has_permission) {
            Toast.makeText(this, "权限被禁止，请前往app详情设置权限", Toast.LENGTH_SHORT).show()
            return
        }
        calendarEvent = if (null != intent.getSerializableExtra("objectEntity")) {
            intent.getSerializableExtra("objectEntity") as CalendarEvent2?
        } else {
            CalendarEvent2()
        }
        type = intent.getIntExtra("type", 0)
        title = findViewById<View>(R.id.title) as TextView
        if (type == 0) {
            title!!.setText(R.string.event_add)
            year = intent.getIntExtra("year", 0)
            month = intent.getIntExtra("month", -1)
            day = intent.getIntExtra("day", 0)
        } else if (type == 1) {
            title!!.setText(R.string.event_update)
        }
        llStartTime = findViewById<View>(R.id.ll_start_time) as LinearLayout
        tvStartTime = findViewById<View>(R.id.tv_start_time) as TextView
        setAgendaTime(0, type, year, month, day) // 设置日程事件时间
        llEndTime = findViewById<View>(R.id.ll_end_time) as LinearLayout
        tvEndTime = findViewById<View>(R.id.tv_end_time) as TextView
        setAgendaTime(1, type, year, month, day) // 设置日程事件时间
        etTitle = findViewById<View>(R.id.et_editText) as EditText
        etDescription = findViewById<View>(R.id.et_agenda_description) as EditText
        text = intent.getStringExtra("text")
        if (type == 0 && null != text && text != "") {
            etTitle!!.setText(text)
            etDescription!!.setText(text)
        }
        spinnerRemind = findViewById<View>(R.id.spin_agenda_remind) as NiceSpinner
        spinnerRepeat = findViewById<View>(R.id.spin_agenda_repeat) as NiceSpinner
        if (type == 1) {
            etTitle!!.setText(calendarEvent!!.title)
            etDescription!!.setText(calendarEvent!!.description)
            etTitle!!.setSelection(calendarEvent!!.title.length)
            etDescription!!.setSelection(calendarEvent!!.description.length)
        }
        setAgendaRemind() // 设置提醒时间
        setAgendaRepeat() // 设置重复
        save = findViewById<View>(R.id.btn_save) as Button
        save!!.setOnClickListener(View.OnClickListener {
            val eventTitle = etTitle!!.text.toString()
            val description = etDescription!!.text.toString()
            calendarEvent!!.title = eventTitle
            calendarEvent!!.description = description
            if (calendarEvent!!.endTime < calendarEvent!!.beginTime) {
                Utils.toast(this@EditCalendarActivity2, "结束日期不能小于开始日期")
                return@OnClickListener
            }
            //TODO:多选日期
            val timeJson = arrayListOf<Long>()
            for (item in mTimeAdapter.data) {
                if (item.itemType == TargetSettingAdapter.ITEM_TYPE_CONTENT) {
                    timeJson.add((item as TargetSettingTimeBean).timestamp)
                }
            }

            //时间提醒开关 1开2关
            Log.e("thansferData_ timeJson_", timeJson.toString())
            if (timeJson.isEmpty()) {
                /*ToastUtils.show("时间提醒不能為空！");*/
                Toast.makeText(this,"时间提醒不能為空!",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

//            var timeList =  Gson().toJson(timeJson)

            if (type == 0) {
                //TODO: 循环添加
                for (item in timeJson){
                    calendarEvent!!.beginTime = item
                    //结束时间必须大于开始时间 延后一分钟
                    calendarEvent!!.endTime = item +60000

                    val result = CalendarsResolver.getInstance().addData(this@EditCalendarActivity2, calendarEvent)
                    if (result >= 0) {
                        Log.e("addEventId: ", result.toString() + "")
                        calendarEvent!!.eventId = result.toLong()

                        //TODO:添加事件到本地数据库
                        val b = mDaoUtils.insertEvent(calendarEvent)
                        if (b) Log.e("local add ", "添加本地数据库记录成功！")
                        Utils.toast(this@EditCalendarActivity2, "添加事件成功！")
                    }
                }
            } else if (type == 1) {
                val result = CalendarsResolver.getInstance().updateData(this@EditCalendarActivity2,
                        calendarEvent)
                if (result) {
                    Utils.toast(this@EditCalendarActivity2, "更新事件成功！")
                }
            }
            val data = Intent()
            data.putExtra("objectEntity", calendarEvent)
            setResult(Activity.RESULT_OK, data)
            finish()
        })
        cancel = findViewById<View>(R.id.btn_cancel) as Button
        cancel!!.setOnClickListener { finish() }


        //时间选择
        rl_punch_card.setNoDoubleClickListener { initSelectedDialog(1) }
        rl_keep_day.setNoDoubleClickListener { initSelectedDialog(4) }
        rl_start_time.setNoDoubleClickListener { initTimePicker(0) }

        rv_view.adapter = mTimeAdapter
        rv_view.layoutManager = GridLayoutManager(this, 3)
        mTimeAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (adapter.getItemViewType(position)) {
                TargetSettingAdapter.ITEM_TYPE_Add_IMAGE -> {
                    when (view.id) {
                        R.id.cl_add -> {
                            //时间提醒 最多可添加3个
                            if (mTimeAdapter.data.isNotEmpty() && mTimeAdapter.data.size >= 4) {
//                                ToastUtils.show("一个目标时间提醒时间最多设置3个")
                                return@setOnItemChildClickListener
                            }
                            initTimePicker(1)
                        }
                    }
                }
                TargetSettingAdapter.ITEM_TYPE_CONTENT -> {
                    when (view.id) {
                        R.id.iv_delete -> {
                            adapter.data.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
        }

        if (mList.size > 0) {
            mList.add(mList.size, VhData(describe = "已打卡", _itemType = TargetSettingAdapter.ITEM_TYPE_Add_IMAGE))
        } else {
            mList.add(VhData(describe = "已打卡", _itemType = TargetSettingAdapter.ITEM_TYPE_Add_IMAGE))
        }
        mTimeAdapter.setList(mList)
    }


    var mSelectedDialog: TargetSettingDialog? = null
    private fun initSelectedDialog(type: Int) {
        mSelectedDialog = TargetSettingDialog.init()
        mSelectedDialog!!.setFromType(type)
        mSelectedDialog!!.setOnItemClickListener(this)
        mSelectedDialog!!.setDimAmount(0.3f)
        mSelectedDialog!!.setGravity(Gravity.BOTTOM)
        mSelectedDialog!!.show(supportFragmentManager)
    }

    override fun onItemClick(type: Int, targetId: String, moodId: String) { //心情打卡回调
//        mMoodPunchCardDialog?.dismiss()
//        initEditTextMoodCardDialog(type, targetId, moodId)
    }

    /**
     * @param type 0:开始时间，1：时间提醒
     */
    var pvTime: TimePickerView? = null
    var mSelectedDate: Date? = null
    fun initTimePicker(type: Int) {
        val endDate = Calendar.getInstance()
        endDate[2069, 2] = 28
        pvTime = TimePickerBuilder(this, OnTimeSelectListener { date, _ ->
            Log.i("pvTime", "date :" + date.time)
            mSelectedDate = date
            if (type == 0) {
//                tv_start_time_count.text = getTime(0)
            } else {
                val item = TargetSettingTimeBean()
                item.title = getTime(1).toString()
                item.timestamp = mSelectedDate!!.time
                mTimeAdapter.data.add(mTimeAdapter.data.size - 1, item)
                mTimeAdapter.notifyDataSetChanged()
            }
        })
                .setTimeSelectChangeListener { date -> mSelectedDate = date }
                .setType(if (type == 0) booleanArrayOf(true, true, true, false, false, false) else booleanArrayOf(false, false, false, true, true, false))
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener { Log.i("pvTime", "onCancelClickListener") }
                .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.0f)
                .setRangDate(Calendar.getInstance(), endDate)
                .isAlphaGradient(true)
                .setDividerColor(Color.parseColor("#00000000"))
                .setLabel(StrUtil.getLabel(type), StrUtil.getLabel(type), StrUtil.getLabel(type), "点", "分", StrUtil.getLabel(type))
                .setLayoutRes(R.layout.dailog_target_time_setting) {
                    it.tv_title.text = if (type == 0) "设置开始时间" else "设置提醒时间"
                    it.tv_next.text = if (type == 0) "下一项" else "完成"
                    it.tv_next.setOnClickListener {
                        if (type == 0) {
                            pvTime?.returnData()
                            pvTime?.dismiss()
                            //时间提醒 最多可添加3个
                            if (mTimeAdapter.data.isNotEmpty() && mTimeAdapter.data.size < 4) {
                                initTimePicker(1)
                            }
                        } else {
                            pvTime?.returnData()
                            pvTime?.dismiss()
                        }
                    }
                }.build()
        val mDialog = pvTime!!.dialog
        if (mDialog != null) {
            val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM)
            params.leftMargin = 0
            params.rightMargin = 0
            pvTime!!.dialogContainerLayout.layoutParams = params
            val dialogWindow = mDialog.window
            if (dialogWindow != null) {
                //修改动画样式
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim)
                dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f)
            }
        }
        pvTime?.show()
    }

    //可根据需要自行截取数据显示
    private fun getTime(type: Int): String? {
        if (null == mSelectedDate) return ""
        Log.d("getTime()", "choice date millis: " + mSelectedDate!!.time)
        val format = if (type == 0) SimpleDateFormat("yyyy/MM/dd") else SimpleDateFormat("HH:mm")
        return format.format(mSelectedDate)
    }

    /**
     * 设置日程事件时间
     *
     * @param
     */
    private fun setAgendaTime(flag: Int, type: Int, year: Int, month: Int, day: Int) {
        if (type == 0) {
            val calendar = GregorianCalendar()
            if (year != 0) calendar.add(Calendar.YEAR, year - calendar[Calendar.YEAR])
            if (month >= 0) calendar.add(Calendar.MONTH, month - calendar[Calendar.MONTH])
            if (day != 0) calendar.add(Calendar.DAY_OF_MONTH, day - calendar[Calendar.DAY_OF_MONTH])
            if (flag != 0) calendar.add(Calendar.HOUR_OF_DAY, 1)
            if (flag == 0) {
                calendarEvent!!.beginTime = calendar.timeInMillis
            } else {
                calendarEvent!!.endTime = calendar.timeInMillis
            }
            val agendaTimeString = DateUtils.timeStamp2Date(calendar.timeInMillis, "yyyy-MM-dd HH:mm:ss")
            (if (flag == 0) tvStartTime else tvEndTime)!!.text = agendaTimeString
        } else {
            val agendaTimeString = DateUtils.timeStamp2Date(
                    if (flag == 0) calendarEvent!!.beginTime else calendarEvent!!.endTime, "yyyy-MM-dd HH:mm:ss")
            (if (flag == 0) tvStartTime else tvEndTime)!!.text = agendaTimeString
        }
        (if (flag == 0) llStartTime else llEndTime)!!.setOnClickListener { setTimeText(if (flag == 0) tvStartTime else tvEndTime, flag, year, month, day) }
    }

    /**
     * 设置提醒时间
     *
     * @param
     */
    private fun setAgendaRemind() {
        // 默认选择提前15分钟
        val agendaRemindValues = resources.getIntArray(R.array.agenda_remind_int)
        var position = 0
        for (i in agendaRemindValues.indices) {
            if (calendarEvent!!.remind == agendaRemindValues[i]) position = i
        }
        val datasRemind = resources.getStringArray(R.array.agenda_remind_string)
        val dataset: List<String> = ArrayList(Arrays.asList(*datasRemind))
        spinnerRemind!!.attachDataSource(dataset)
        spinnerRemind!!.selectedIndex = position
        spinnerRemind!!.addOnItemClickListener { parent, view, position, id -> calendarEvent!!.remind = agendaRemindValues[position] }
    }

    /**
     * 设置事件重复方式
     *
     * @param
     */
    private fun setAgendaRepeat() {
        val repeat = resources.getStringArray(R.array.agenda_repeat_string_values)
        val repeat2 = resources.getStringArray(R.array.agenda_repeat_string_values2)
        var position = 0
        val repeatValue = calendarEvent!!.repeat
        for (i in repeat2.indices) {
            if (repeatValue != null && repeatValue.contains(repeat2[i])) {
                position = i
            }
        }
        val datasRepeat = resources.getStringArray(R.array.agenda_repeat_string)
        val dataset: List<String> = ArrayList(Arrays.asList(*datasRepeat))
        spinnerRepeat!!.attachDataSource(dataset)
        spinnerRepeat!!.selectedIndex = position
        spinnerRepeat!!.addOnItemClickListener { parent, view, position, id -> calendarEvent!!.repeat = String.format(repeat[position], "MO", "WE", "FR") }
    }

    private fun setTimeText(textViewTime: TextView?, flag: Int, year: Int, month: Int, day: Int) {
        var year = year
        var month = month
        var day = day
        val viewDate = inflater!!.inflate(R.layout.data_time_picker, null)
        val datePicker = viewDate.findViewById<View>(R.id.dp_agenda) as DatePicker
        val timePicker = viewDate.findViewById<View>(R.id.tp_agenda) as TimePicker
        // int year = 0;
        // int month = 0;
        // int day = 0;
        var hour = 0
        var minute = 0
        val calendar = GregorianCalendar()
        if (type == 0) {
            if (year == 0) year = datePicker.year
            if (month < 0) month = datePicker.month
            if (day == 0) day = datePicker.dayOfMonth
            hour = timePicker.currentHour
            if (flag == 1) hour = timePicker.currentHour + 1
            minute = timePicker.currentMinute
            calendar[year, month, day, hour] = minute
        } else {
            calendar.time = Date(if (flag == 0) calendarEvent!!.beginTime else calendarEvent!!.endTime)
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH]
            day = calendar[Calendar.DAY_OF_MONTH]
            hour = calendar[Calendar.HOUR_OF_DAY]
            minute = calendar[Calendar.MINUTE]
        }
        datePicker.init(year, month, day, null)
        timePicker.currentHour = hour
        timePicker.currentMinute = minute
        val dialog = AlertDialog.Builder(this@EditCalendarActivity2).setView(viewDate)
                .setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
                    val calendar = GregorianCalendar()
                    val year = datePicker.year
                    val month = datePicker.month
                    val day = datePicker.dayOfMonth
                    val hour = timePicker.currentHour
                    val minute = timePicker.currentMinute
                    calendar[year, month, day, hour] = minute
                    var time: String? = ""
                    if (flag == 0) {
                        calendarEvent!!.beginTime = calendar.timeInMillis
                        time = DateUtils.timeStamp2Date(calendarEvent!!.beginTime, "yyyy-MM-dd HH:mm:ss")
                    } else {
                        calendarEvent!!.endTime = calendar.timeInMillis
                        time = DateUtils.timeStamp2Date(calendarEvent!!.endTime, "yyyy-MM-dd HH:mm:ss")
                    }
                    textViewTime!!.text = time
                }.create()
        dialog.show()
    }
}