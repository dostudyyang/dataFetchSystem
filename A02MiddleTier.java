import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class A02MiddleTier {

    final String URL="jdbc:mysql://127.0.0.1:3306/a02schema";
    final String USERNAME="root";
    final String PASSWORD="root1234";
    PreparedStatement ps= null;
    Connection con = null;


    static{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public  Connection   getCon(){

        try {
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }


    public  PreparedStatement createStatement(String sql){

        try {
            ps =  getCon().prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }

    public  void close(){
        if(ps!=null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public  void close(ResultSet rs){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close();
    }

    public void find(A02MiddleTier util, JCheckBox eventConference,JCheckBox eventJournal,JCheckBox eventBook,JRadioButton allDates,JTextField fromDate,JTextField toDate,JTextArea queryOutput){
        String sql ="Select Name, Date  from (";
        String date ="";
        A02FrontEnd a02FrontEnd =new A02FrontEnd();
        if(eventConference.isSelected()){
            sql+="(Select Name, EvDate as Date from Event, EventConference  where EventConference.EventID=ID)\n";
            if(eventJournal.isSelected()|| eventBook.isSelected()){sql+="union";}
        }
        if(eventJournal.isSelected() ){
            sql+="(Select Name, MAX(ActivityDate) as Date \nfrom Event, ActivityHappens, EventJournal  where ActivityHappens.EventID=ID and EventJournal.EventID=ID\nGroup By ID )\n";
            if(eventBook.isSelected()){sql+="union";}
        }
        if(eventBook.isSelected()){
            sql+="(Select Name, MAX(ActivityDate) as Date \nfrom Event, ActivityHappens, EventBook  where ActivityHappens.EventID=ID and EventBook.EventID=ID\nGroup By ID) ";
        }

        sql+=") as a ";
        if(!allDates.isSelected()){
            if(fromDate.getText().length()>0){
                sql+="where Date >="+"\'"+fromDate.getText()+"\'";
                if(toDate.getText().length()>0){
                    sql+=" and  Date <="+"\'"+toDate.getText()+"\'";
                }
            }
            else{
                sql+="where Date <="+"\'"+toDate.getText()+"\'";
            }
        }

        sql+=";";

        PreparedStatement ps = util.createStatement(sql);
        ResultSet rs = null;
        String result="";
        System.out.println(sql);
        try {
            rs = ps.executeQuery();
            while (rs.next()){
                String Name= rs.getString("Name");
                String Date = rs.getString("Date");
                result += Name + "    " + Date + "\n";
            }
            queryOutput.setText("Query:\n"+sql+"\n\n"+"Output:\n"  + result);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            util.close(rs);
        }
    }
}
