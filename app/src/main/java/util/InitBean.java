package util;

import android.util.Log;

import com.vimems.AdapterItem.CoachItem;
import com.vimems.AdapterItem.MemberItem;
import com.vimems.R;
import com.vimems.bean.Coach;
import com.vimems.bean.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class InitBean {
    public static boolean isInit=false;
    public static String loginName;
    public  static String loginPWD;
    public static ArrayList<CoachItem> coachItemArrayList=new ArrayList<>();
    public static ArrayList<MemberItem> memberItemArrayList=new ArrayList<>();
    public static ArrayList<Member> memberArrayList=new ArrayList<>();
    public static void initCoachItemList(){
        Coach coach1=new Coach(1,1,1,"coachLoginName1","123456","男",new Date());
        Coach coach2=new Coach(2,1,2,"coachLoginName2","123456","男",new Date());
        Coach coach3=new Coach(3,1,3,"coachLoginName3","123456","男",new Date());
        Coach coach4=new Coach(4,1,4,"coachLoginName4","123456","女",new Date());
        Coach coach5=new Coach(5,1,5,"coachLoginName5","123456","女",new Date());
        Coach coach6=new Coach(6,2,6,"coachLoginName6","123456","女",new Date());
        Coach coach7=new Coach(7,2,7,"coachLoginName7","123456","女",new Date());
        Coach coach8=new Coach(8,2,8,"coachLoginName8","123456","女",new Date());
        for(int i=0;i<1;i++){
            CoachItem item1=new CoachItem(coach1.getLoginName(), R.drawable.ic_launcher_background,coach1.getCoachID());
            coachItemArrayList.add(item1);
            CoachItem item2=new CoachItem(coach2.getLoginName(),R.drawable.ic_launcher_background,coach2.getCoachID());
            coachItemArrayList.add(item2);
            CoachItem item3=new CoachItem(coach3.getLoginName(),R.drawable.ic_launcher_background,coach3.getCoachID());
            coachItemArrayList.add(item3);
            CoachItem item4=new CoachItem(coach4.getLoginName(),R.drawable.ic_launcher_background,coach4.getCoachID());
            coachItemArrayList.add(item4);
            CoachItem item5=new CoachItem(coach5.getLoginName(),R.drawable.ic_launcher_background,coach5.getCoachID());
            coachItemArrayList.add(item5);
            CoachItem item6=new CoachItem(coach6.getLoginName(),R.drawable.ic_launcher_background,coach6.getCoachID());
            coachItemArrayList.add(item6);
            CoachItem item7=new CoachItem(coach7.getLoginName(),R.drawable.ic_launcher_background,coach7.getCoachID());
            coachItemArrayList.add(item7);
            CoachItem item8=new CoachItem(coach8.getLoginName(),R.drawable.ic_launcher_background,coach8.getCoachID());
            coachItemArrayList.add(item8);

        }
    }
    public static  void initMemberItemList(){

        Member[]  members=new Member[110];
        for (int i = 0; i <=20; i++) {
            members[i]=new Member("member"+i,i,1,"男",new Date(),1.7,65,new Date(),22);
        }
        for (int i = 21; i <=40; i++) {
            members[i]=new Member("member"+i,i,2,"男",new Date(),1.7,65,new Date(),22);
        }
        for (int i = 41; i <=60; i++) {
            members[i]=new Member("member"+i,i,3,"男",new Date(),1.7,65,new Date(),22);
        }
        for (int i = 61; i <=70; i++) {
            members[i]=new Member("member"+i,i,4,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 71; i <=80; i++) {
            members[i]=new Member("member"+i,i,5,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 81; i <=90; i++) {
            members[i]=new Member("member"+i,i,6,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 91; i <=100; i++) {
            members[i]=new Member("member"+i,i,7,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 101; i <110; i++) {
            members[i]=new Member("member"+i,i,8,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 0; i < members.length; i++) {
            memberArrayList.add(members[i]);
        }
        for (int i = 0; i <memberArrayList.size() ; i++) {
            memberItemArrayList.add(new MemberItem(memberArrayList.get(i).getMemberName(),R.drawable.ic_launcher_background));
        }
    }
    public static int getCoachNum(ArrayList<CoachItem> coachItemArrayList){
        return coachItemArrayList.size();
    }
    public static int getTotalMemberNum(ArrayList<Member> memberArrayList ){
        return memberArrayList.size();
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
