package util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//传统的使用继承SQLiteOpenHelper类，操作数据库
public class VimEmsDatabaseHelper extends SQLiteOpenHelper {
    private String TAG="VimEmsDatabaseHelper";

    private String adminTable="create table admin(" +
            "adminID integer primary key autoincrement," +
            "adminName text," +
            "loginName text," +
            "adminPassword text," +
            "gender text)";
    private String coachTable="create table coach(" +
            "coachID integer primary key autoincrement," +
            "adminID integer," +
            "imageID integer," +
            "coachName text," +
            "coachLoginName text," +
            "loginPassword text," +
            "gender text," +
            "birthdate text," +
            "coachRank text)";
    private String memberTable="create table member(" +
            "memberID integer primary key autoincrement," +
            "coachID integer," +
            "imageID integer," +
            "memberName text," +
            "gender text," +
            "birthdate text," +
            "height real," +
            "weight real," +
            "date text," +
            "age integer)";
    public VimEmsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(adminTable);
        db.execSQL(coachTable);
        db.execSQL(memberTable);
        Log.d(TAG, "onCreate() returned: " + "数据库表创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists adminTable");
        db.execSQL("drop table if exists coachTable");
        db.execSQL("drop table if exists memberTable");
        onCreate(db);
    }
}
