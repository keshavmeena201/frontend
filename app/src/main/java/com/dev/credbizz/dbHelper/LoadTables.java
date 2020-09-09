package com.dev.credbizz.dbHelper;

import android.content.Context;
import android.database.SQLException;

import com.dev.credbizz.extras.Keys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LoadTables {
    public LoadTables(Context context)
    {
        db = dbhelper.getInstance(context);

    }
    private static dbhelper db;
    public Context context;

    static final LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    public void LoadAllTables(Context context) {

        db = dbhelper.getInstance(context);
        this.context = context;
        init();

    }
    public void init() {

        try {

            initUser();

        } catch (SQLException e) {
            e.printStackTrace();
			/*AlertDialogManager.showAlertDialog(context, "Database Error",
					"Database Error Occured");*/
        }

    }


    public void initUser()
    {
        saveContacts();
        CreateTable(Keys.tbl_contacts,"");

    }

    public static LinkedHashMap<String, String> saveContacts() {
        fields.clear();
        fields.put(Keys.id, "INTEGER");
        fields.put(Keys.contact_id, "TEXT");
        fields.put(Keys.contact_name, "TEXT");
        fields.put(Keys.contact_alpha, "TEXT");
        fields.put(Keys.contact_number, "TEXT");
        fields.put(Keys.contact_credit_score, "TEXT");
        fields.put(Keys.is_credit_buzz_user, "BOOLEAN");
        fields.put(Keys.contact_selected, "BOOLEAN");
        fields.put(Keys.transaction_type, "INTEGER");
        return fields;
    }




    private void CreateTable(String $tablename, String primarykey) {
        String sql = dbhelper.createTable($tablename, fields, primarykey);
        dbhelper.executeSql(sql);
    }


    public void getDeleteLikes(String tablename)
    {
        dbhelper.executeSql("delete from "+tablename+" where userid=1");
    }

    public void insertData(String tablename, String primarykey, List<String> lst, LinkedHashMap<String, String> fields)
    {
        dbhelper.insertTable(tablename,fields,primarykey,lst);
    }

    public static void deleteTable(String tablename)
    {
        dbhelper.executeSql("delete from "+tablename);
        //ShowToast.Show(""+trues);
    }
    public void deleteTable(String tablename, String where, String wherepara)
    {
        dbhelper.executeSql("delete from "+tablename+ " where "+where+" = "+wherepara);
    }



    public void execSql(String qry)
    {
        dbhelper.executeSql(qry);
    }
    public static ArrayList<String[]> getData(String tablename) {
        return db.getData(tablename);
    }

    public static ArrayList<String[]> getDatabyqry(String qry) {
        return db.getDatabyqry(qry);
    }

    public static int getCount(String tablename, String Where, String paraWhere)
    {
        return db.getCount(tablename,Where,paraWhere);
    }
    public static int getCount(String tablename)
    {
        return db.getCount(tablename);
    }



}
