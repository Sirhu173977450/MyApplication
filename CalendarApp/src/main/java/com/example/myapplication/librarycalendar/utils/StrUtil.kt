package com.example.myapplication.librarycalendar.utils


/**
 * Created by lihui on 2015/8/27.
 * 工具类：字符串处理工具
 */
object StrUtil {
    /**
     * null 转 ""
     * @param str
     * @return
     */
    @JvmStatic
    fun null2Str(str: String?): String {
        return str ?: ""
    }

    /**
     * String 转 int
     * @param str
     * @return
     */
    fun str2Int(str: String): Int {
        return try {
            str.toInt()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * String 转 int
     * @param str
     * @return
     */
    fun str2long(str: String): Long {
        return try {
            str.toLong()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * String 转 float
     * @param str
     * @return
     */
    fun str2float(str: String): Float {
        return try {
            str.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }

    /**
     * String 转 double
     * @param str
     * @return
     */
    fun str2double(str: String?): Double {
        return try {
            java.lang.Double.valueOf(str!!)
        } catch (e: Exception) {
            0.0
        }
    }

    /**
     * is null or its length is 0 or it is made by space
     *
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
    </pre> *
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return true, else return false.
     */
    fun isBlank(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.length == 0
    }

    /**
     * is null or its length is 0
     *
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
    </pre> *
     *
     * @param str
     * @return if string is null or its size is 0, return true, else return false.
     */
    fun isEmpty(str: String?): Boolean {
        return (str == null || str.length == 0) && isBlank(str)
    }


//    fun getSelectList(arr: ArrayList<String>?): String {
//        if (null == arr) return Gson().toJson(arrayListOf("1", "2", "3", "4", "5", "6", "7"))
//        var arrStr = arrayListOf<String>()
//        for (st in arr) {
//            when (st) {
//                "周一" -> {
//                    arrStr.add("1")
//                }
//                "周二" -> {
//                    arrStr.add("2")
//                }
//                "周三" -> {
//                    arrStr.add("3")
//                }
//                "周四" -> {
//                    arrStr.add("4")
//                }
//                "周五" -> {
//                    arrStr.add("5")
//                }
//                "周六" -> {
//                    arrStr.add("6")
//                }
//                "周日" -> {
//                    arrStr.add("7")
//                }
//                else -> {
//                    arrStr.add(st)
//                }
//            }
//        }
//        return Gson().toJson(arrStr)
//    }

    fun getSelectToList(arr: ArrayList<String>?): String {
        if (null == arr) return ""
        var arrStr = arrayListOf<String>()
        for (st in arr) {
            when (st) {
                "1" -> {
                    arrStr.add("周一")
                }
                "2" -> {
                    arrStr.add("周二")
                }
                "3" -> {
                    arrStr.add("周三")
                }
                "4" -> {
                    arrStr.add("周四")
                }
                "5" -> {
                    arrStr.add("周五")
                }
                "6" -> {
                    arrStr.add("周六")
                }
                "7" -> {
                    arrStr.add("周日")
                }
                else -> {
                    arrStr.add(st)
                }
            }
        }
        return arrStr.toString().replace("[", "").replace("]", "").trim()
    }

    fun getLabel(type: Int): String? {
        return if (type == 0) null else ""
    }


    fun getMoodStr(type: String): String {
        when (type) {
            "1" -> {
                return "轻松达成"
            }
            "2" -> {
                return "继续奋斗"
            }
            "3" -> {
                return "有点难度"
            }
            "4" -> {
                return "怀疑人生"
            }
            "5" -> {
                return "保持乐观"
            }
            else -> {
                return ""
            }
        }
    }

    /**
     * @param state :目标状态 1启用2暂停3已完成4已删除
     */
    fun getTargetState(state:String): String {
        when(state){
            "1"->{
                return "(启用)"
            }
            "2"->{
                return "(暂停)"
            }
            "3"->{
                return "(已完成)"
            }
            "4"->{
                return "(已删除)"
            }
            else ->{
                return ""
            }
        }
    }
}