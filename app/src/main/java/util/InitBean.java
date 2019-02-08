package util;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.vimems.AdapterItem.CoachItem;
import com.vimems.AdapterItem.MemberItem;
import com.vimems.R;
import com.vimems.bean.Admin;
import com.vimems.bean.Coach;
import com.vimems.bean.Member;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class InitBean {

    public static ArrayList<Admin> adminArrayList=new ArrayList<>();
   // public static ArrayList<CoachItem> coachItemArrayList=new ArrayList<>();
    public static ArrayList<Coach> coachArrayList=new ArrayList<>();
   // public static ArrayList<MemberItem> memberItemArrayList=new ArrayList<>();
    public static ArrayList<Member> memberArrayList=new ArrayList<>();

    public static String TAG="INITLITEPAL";

//
//
////    begin
//    //使用SQLiteOpenHelper
//
//    /*public static VimEmsDatabaseHelper getDBHelper(Context context){
//        return new VimEmsDatabaseHelper(context,"vimems.db",null,1);
//    }
//
//    public static void initTable(Context context){
//        initAdminTable(context);
//        initCoachTable(context);
//        initMemberTable(context);
//    }
//    private static  void initAdminTable(Context context){
//        ContentValues adminValues=new ContentValues();
//        for(int i=0;i<10;i++){
//            adminValues.put("adminName","adminName"+i);
//            adminValues.put("loginName","adminLoginName"+i);
//            adminValues.put("adminPassword","123456");
//            adminValues.put("gender",(i%2==0)?"male":"female");
//            getDBHelper(context).getWritableDatabase().insert("admin",null,adminValues);
//        }
//    }
//    private static void initCoachTable(Context context){
//        ContentValues coachValues=new ContentValues();
//        for (int i = 0; i < 10; i++) {
//            coachValues.put("adminID",i/5+1);
//            coachValues.put("imageID",R.drawable.ic_launcher_foreground);
//            coachValues.put("coachName","coachName"+i);
//            coachValues.put("coachLoginName","coachLoginName"+i);
//            coachValues.put("loginPassword","123456");
//            coachValues.put("gender",(i%2==0)?"male":"female");
//            coachValues.put("birthdate",new Date().toString());
//            coachValues.put("coachRank",(i%3==0)?"A":(i%3==1)?"B":"C");
//            getDBHelper(context).getWritableDatabase().insert("coach",null,coachValues);
//        }
//    }
//
//    private static  void initMemberTable(Context context){
//        ContentValues memberValues=new ContentValues();
//        for (int i = 0; i <100 ; i++) {
//            memberValues.put("coachID",i/10+1);
//            memberValues.put("imageID",R.drawable.ic_launcher_foreground);
//            memberValues.put("memberName","memberName"+i);
//            memberValues.put("gender",(i%2==0)?"male":"female");
//            memberValues.put("birthdate",new Date().toString());
//            memberValues.put("height",1.8);
//            memberValues.put("weight",70);
//            memberValues.put("date",new Date().toString());
//            memberValues.put("age",20);
//            getDBHelper(context).getWritableDatabase().insert("member",null,memberValues);
//        }
//    }*/
////    end
//

    /**
     * 使用LitePal添加数据
     */
    public static void initLitePalTable(){

        initAdmin();
        initCoach();
        initMember();
        adminArrayList= (ArrayList<Admin>) LitePal.findAll(Admin.class);
        coachArrayList=(ArrayList<Coach>)LitePal.findAll(Coach.class);
        memberArrayList=(ArrayList<Member>)LitePal.findAll(Member.class);
    }

    public static void initAdmin(){
        Admin[] admins=new Admin[10];
        for (int i = 0; i <admins.length ; i++) {
            admins[i]=new Admin();
            admins[i].setAdminID(i);
            admins[i].setAdminName("adminName"+i);
            admins[i].setLoginName("adminLoginName"+i);
            admins[i].setAdminPassword("123456");
            admins[i].setGender((i%2==0)?"male":"female");
            admins[i].save();
        }
        Log.i(TAG, "initAdmin: ");
    }

    public static void initCoach(){

        Coach[] coaches=new Coach[30];
        for (int i = 0; i <coaches.length ; i++) {
            coaches[i]=new Coach();
            coaches[i].setCoachID(i);
            coaches[i].setAdminID(i/3);
            coaches[i].setImageID(R.drawable.ic_launcher_foreground);
            coaches[i].setCoachName("coachName"+i);
            coaches[i].setCoachLoginName("coachLoginName"+i);
            coaches[i].setLoginPassword("123456");
            coaches[i].setGender((i%2==0)?"male":"female");
            coaches[i].setBirthdate(new Date());
            coaches[i].setCoachRank((i%3==0)?"A":(i%3==1)?"B":"C");
            coaches[i].save();
        }
        Log.i(TAG, "initMember: ");
    }
    public static void initMember(){
        Member[] members=new Member[600];
        for (int i = 0; i < members.length; i++) {
            members[i]=new Member();
            members[i].setMemberID(i);
            members[i].setCoachID(i/20);
            members[i].setImageID(R.drawable.ic_launcher_foreground);
            members[i].setMemberName("memberName"+i);
            members[i].setGender((i%2==0)?"male":"female");
            members[i].setBirthdate(new Date());
            members[i].setHeight(1.8);
            members[i].setWeight(70);
            members[i].setDate(new Date());
            members[i].setAge(18);
            members[i].save();
        }
        Log.i(TAG, "initMember: ");
    }

    public static int getAdminCoachNum(int adminID,ArrayList<Coach> coachArrayList){
        int adminCoachCount=0;
        int getAdminID;
        Iterator<Coach> iterator=coachArrayList.iterator();
        while(iterator.hasNext()){
            getAdminID=iterator.next().getAdminID();
            if(getAdminID==adminID){
                adminCoachCount++;
            }
        }
        return  adminCoachCount;
    }

    public static int getCoachMemberNum(int coachID,ArrayList<Member> memberArrayList){

        int coachMemberCount=0;
        int getCoachID;
        Iterator<Member> iterator=memberArrayList.iterator();
        while (iterator.hasNext()){
            getCoachID=iterator.next().getCoachID();
            if(getCoachID==coachID){
                coachMemberCount++;
            }
        }
        return coachMemberCount;
    }


}
