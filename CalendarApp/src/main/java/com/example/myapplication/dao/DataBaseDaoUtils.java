package com.example.myapplication.dao;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.gen.CalendarEvent2Dao;
import com.example.myapplication.librarycalendar.utils.CalendarEvent2;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


public class DataBaseDaoUtils {

    private static final String TAG = DataBaseDaoUtils.class.getSimpleName();
    private MessageDaoManager mManager;

    public DataBaseDaoUtils(Context context){
        mManager = MessageDaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成meizi记录的插入，如果表未创建，先创建Meizi表
     * @param meizi
     * @return
     */
    public boolean insertEvent(CalendarEvent2 meizi){
        boolean flag = false;
        try {
            flag = mManager.getDaoSession().getCalendarEvent2Dao().insert(meizi) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "insert Meizi :" + flag + "-->" + meizi.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param meiziList
     * @return
     */
    public boolean insertMultMeizi(final List<CalendarEvent2> meiziList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (CalendarEvent2 meizi : meiziList) {
                        mManager.getDaoSession().insertOrReplace(meizi);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param meizi
     * @return
     */
    public boolean update(CalendarEvent2 meizi){
        boolean flag = false;
        try {
            mManager.getDaoSession().getCalendarEvent2Dao().save(meizi);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i(TAG, "insert Meizi :" + flag + "-->" + meizi.toString());
        return flag;
    }


    /**
     * 删除单条记录
     * @param meizi
     * @return
     */
    public boolean deleteMeizi(CalendarEvent2 meizi){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(meizi);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param eventId 事件Id
     * @return
     */
    public boolean deleteForEventId(long eventId){
        boolean flag = false;
        try {
            //按照eventId删除
            mManager.getDaoSession().queryBuilder(CalendarEvent2.class)
                    .where(CalendarEvent2Dao.Properties.EventId.eq(eventId))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(CalendarEvent2.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<CalendarEvent2> queryAll(){
        return mManager.getDaoSession().loadAll(CalendarEvent2.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public CalendarEvent2 queryMeiziById(long key){
        return mManager.getDaoSession().load(CalendarEvent2.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<CalendarEvent2> queryMeiziByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(CalendarEvent2.class, sql, conditions);
    }

    /**
     * TODO 使用queryBuilder进行查询
     * @return
     */
//    public List<CalendarEvent2> queryMeiziByQueryBuilder(Integer id){
//        QueryBuilder<CalendarEvent2> queryBuilder = mManager.getDaoSession().queryBuilder(CalendarEvent2.class);
////        return queryBuilder.where(MeiziDao.Properties._id.eq(id)).list();
//        Log.e(TAG, "insert Meizi query :"+ id);
//        return queryBuilder.where(CalendarEvent2Dao.Properties.NotificationId.eq(id)).list();
//    }

    /**
     * 使用eventId进行查询
     * @return
     */
    public List<CalendarEvent2> queryByEventId(long eventId){
        List<CalendarEvent2> result = null;
        try {
            QueryBuilder<CalendarEvent2> queryBuilder = mManager.getDaoSession().queryBuilder(CalendarEvent2.class);
            result = queryBuilder.where(CalendarEvent2Dao.Properties.EventId.eq(eventId)).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "insert query eventId:"+ eventId);
        return result;
    }


    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        mManager.closeConnection();
    }
}
