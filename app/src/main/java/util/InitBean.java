package util;

import com.vimems.AdapterItem.CoachItem;
import com.vimems.AdapterItem.MemberItem;
import com.vimems.R;
import com.vimems.bean.Admin;
import com.vimems.bean.Coach;
import com.vimems.bean.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class InitBean {
    public static boolean isInit=false;

    public static ArrayList<Admin> adminArrayList=new ArrayList<>();
    public static ArrayList<CoachItem> coachItemArrayList=new ArrayList<>();
    public static ArrayList<Coach> coachArrayList=new ArrayList<>();
    public static ArrayList<MemberItem> memberItemArrayList=new ArrayList<>();
    public static ArrayList<Member> memberArrayList=new ArrayList<>();

    public static void initAdminList(){
        for (int i = 0; i <10; i++) {
            adminArrayList.add(new Admin(i,"adminName"+i,"adminLoginName"+i,"123456","男"));
        }
    }
    public static void initCoachItemList(){
        Coach[] coachs=new Coach[8];
        coachs[0]=new Coach(1,1,R.drawable.ic_launcher_background,"coachName1","coachLoginName1","123456","男",new Date(),"C");
        coachs[1]=new Coach(2,1,R.drawable.ic_launcher_background,"coachName2","coachLoginName2","123456","男",new Date(),"B");
        coachs[2]=new Coach(3,1,R.drawable.ic_launcher_background,"coachName3","coachLoginName3","123456","男",new Date(),"A");
        coachs[3]=new Coach(4,1,R.drawable.ic_launcher_background,"coachName4","coachLoginName4","123456","女",new Date(),"B");
        coachs[4]=new Coach(5,1,R.drawable.ic_launcher_background,"coachName5","coachLoginName5","123456","女",new Date(),"C");
        coachs[5]=new Coach(6,2,R.drawable.ic_launcher_background,"coachName6","coachLoginName6","123456","女",new Date(),"A");
        coachs[6]=new Coach(7,2,R.drawable.ic_launcher_background,"coachName7","coachLoginName7","123456","女",new Date(),"B");
        coachs[7]=new Coach(8,2,R.drawable.ic_launcher_background,"coachName8","coachLoginName8","123456","女",new Date(),"A");

        for (int i = 0; i < 8; i++) {
            coachArrayList.add(coachs[i]);
        }
        for(int i=0;i<1;i++){
            CoachItem item1=new CoachItem(coachs[0].getCoachLoginName(), R.drawable.ic_launcher_background,coachs[0].getCoachID());
            coachItemArrayList.add(item1);
            CoachItem item2=new CoachItem(coachs[1].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[1].getCoachID());
            coachItemArrayList.add(item2);
            CoachItem item3=new CoachItem(coachs[2].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[2].getCoachID());
            coachItemArrayList.add(item3);
            CoachItem item4=new CoachItem(coachs[3].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[3].getCoachID());
            coachItemArrayList.add(item4);
            CoachItem item5=new CoachItem(coachs[4].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[4].getCoachID());
            coachItemArrayList.add(item5);
            CoachItem item6=new CoachItem(coachs[5].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[5].getCoachID());
            coachItemArrayList.add(item6);
            CoachItem item7=new CoachItem(coachs[6].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[6].getCoachID());
            coachItemArrayList.add(item7);
            CoachItem item8=new CoachItem(coachs[7].getCoachLoginName(),R.drawable.ic_launcher_background,coachs[7].getCoachID());
            coachItemArrayList.add(item8);

        }
    }
    public static  void initMemberItemList(){

        Member[]  members=new Member[110];
        for (int i = 0; i <=20; i++) {
            members[i]=new Member("member"+i,i,1,R.drawable.ic_launcher_foreground,"男",new Date(),1.7,65,new Date(),22);
        }
        for (int i = 21; i <=40; i++) {
            members[i]=new Member("member"+i,i,2,R.drawable.ic_launcher_foreground,"男",new Date(),1.7,65,new Date(),22);
        }
        for (int i = 41; i <=60; i++) {
            members[i]=new Member("member"+i,i,3,R.drawable.ic_launcher_foreground,"男",new Date(),1.7,65,new Date(),22);
        }
        for (int i = 61; i <=70; i++) {
            members[i]=new Member("member"+i,i,4,R.drawable.ic_launcher_foreground,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 71; i <=80; i++) {
            members[i]=new Member("member"+i,i,5,R.drawable.ic_launcher_foreground,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 81; i <=90; i++) {
            members[i]=new Member("member"+i,i,6,R.drawable.ic_launcher_foreground,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 91; i <=100; i++) {
            members[i]=new Member("member"+i,i,7,R.drawable.ic_launcher_foreground,"女",new Date(),1.7,65,new Date(),18);
        }
        for (int i = 101; i <110; i++) {
            members[i]=new Member("member"+i,i,8,R.drawable.ic_launcher_foreground,"女",new Date(),1.7,65,new Date(),18);
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
