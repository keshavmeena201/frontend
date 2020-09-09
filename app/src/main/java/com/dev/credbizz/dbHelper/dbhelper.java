package com.dev.credbizz.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dev.credbizz.extras.Keys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class dbhelper extends SQLiteOpenHelper {
   /* publicshare dbhelper(Context context) {
        super(context, "db.db", null, 1);
    }

    @Override
    publicshare void onCreate(SQLiteDatabase sd) {
        sd.execSQL("create table likes(id INTEGER,userid INTEGER,postid INTEGER)");
        sd.execSQL("create table comments(id INTEGER,userid INTEGER,postid INTEGER,comment TEXT)");
    }

    @Override
    publicshare void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
*/

    public static final String dbname = "creditbuzz.db";
    private static dbhelper db;

    private static final String flag = "failure";
    public static SQLiteDatabase sd;
    static final int version=1;


    public static Context context;
    public dbhelper(Context context)
    {
        super(context,dbname,null,version);
        dbhelper.context =context;
    }
    public dbhelper(Context context, int version) {
        super(context, dbname, null, Keys.DBVERSION);
        dbhelper.context =context;
        // TODO Auto-generated constructor stub
    }

    public static synchronized dbhelper getInstance(Context context) {
        if (db == null) {
            //int v=sd.getVersion();

            db = new dbhelper(context,Keys.DBVERSION);
            sd = db.getWritableDatabase();

            return db;
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //onUpgrade(db, 2, Keys.Endpoint.DBVERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub



    }

    // Creae Table Structure
    public static String createTable(String table_name,
                                     Map<String, String> fields, String primary_key) {

        String sql = "CREATE TABLE IF NOT EXISTS " + table_name + " ( ";

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getKey().equals(primary_key))
                sql += entry.getKey() + ' ' + (entry.getValue())
                        + " PRIMARY KEY AUTOINCREMENT";
            else
                sql += entry.getKey() + ' ' + (entry.getValue());
            sql += ",";
        }

        sql = removeLastCharacter(sql);
        sql += ')';
        return sql;

    }

    public static boolean executeSql(String sql) throws SQLException {
        try {
            sd.execSQL(sql);
            return true;
        } catch (SQLException e) {
            throw e;
        }

    }

    public static String insertTable(String $table_name,
                                     LinkedHashMap<String, String> fields, String $primary_key,
                                     List<String> strvalues) {
        ContentValues cv = new ContentValues();

        // String sql = "INSERT INTO " +$table_name+" values ( ";
        int i = 0;

        for (LinkedHashMap.Entry<String, String> entry : fields.entrySet()) {

            if (entry.getKey().equals($primary_key))
                cv.put(entry.getKey(), strvalues.get(i));
            else {
                cv.put(entry.getKey(), strvalues.get(i));
            }

            i++;

        }
        sd.insert($table_name, null, cv);

        // context.getContentResolver().insert(Constants.CONTENT_URI, cv);
        return flag;

    }

    public String deleteSQL(String $table_name,
                            LinkedHashMap<String, String> whereclausefields) {

        String sql = "DELETE FROM " + $table_name + " ";

        if (whereclausefields.size() > 0) {
            sql += " where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {
                sql += " " + entry.getKey() + "='" + entry.getValue() + "' and";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
        }

        return sql;

    }

    public String UpdateSQL(String $table_name,
                            LinkedHashMap<String, String> updatefields,
                            LinkedHashMap<String, String> whereclausefields) {
        // TODO Auto-generated method stub

        String sql = "update " + $table_name + " set ";

        for (LinkedHashMap.Entry<String, String> entry : updatefields
                .entrySet()) {
            sql += entry.getKey() + "='" + entry.getValue() + "',";
        }
        sql = removeLastCharacter(sql);
        if (whereclausefields.size() > 0) {

            sql += " where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {
                sql += " " + entry.getKey() + "='" + entry.getValue() + "' and";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
            //Dbhelper.executeSql(sql);
        }

        return sql;
    }

    private static String removeLastCharacter(String sql) {
        if (sql.length() > 0 && sql.charAt(sql.length() - 1) == ',') {
            sql = sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    public Cursor selectSQL(String $table_name, String[] fields,
                            LinkedHashMap<String, String> whereclausefields, LinkedHashMap<String, String> or_whereclausefields, String orderby,
                            String groupby, String limit, boolean flag) {
        // TODO Auto-generated method stub
        String sql = "select ";
        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1)
                sql += fields[i] + " from " + $table_name + " ";
            else
                sql += fields[i] + ",";
        }

        //sdds = whereclausefields.isEmpty();

        if (!whereclausefields.isEmpty()) {
            sql += " where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {

                sql += " " + entry.getKey() + "='" + entry.getValue() + "' and";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
        }
        if (!or_whereclausefields.isEmpty()) {
            if (!whereclausefields.isEmpty())
                sql += " OR ";
            else
                sql +=" where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {

                sql += " " + entry.getKey() + "='" + entry.getValue() + "' or";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
        }

        if (groupby != null)
            sql += " Group by " + groupby;

        if (orderby != null) {
            sql += " Order By " + orderby;
        }



        if (limit != null)
            sql += " limit " + limit;

        return sd.rawQuery(sql, null);

    }

    public Cursor selectSQL(String $table_name,
                            LinkedHashMap<String, String> whereclausefields, String orderby,
                            String groupby, String limit, boolean flag2) {
        // TODO Auto-generated method stub
        String sql = "select count(*) from " + $table_name;

        //sdds = whereclausefields.isEmpty();

        if (!whereclausefields.isEmpty()) {
            sql += " where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {

                sql += " " + entry.getKey() + "='" + entry.getValue() + "' and";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
        }

        if (orderby != null) {
            sql += " Ordery By " + orderby;
        }

        if (groupby != null)
            sql += " Group by " + groupby;

        if (limit != null)
            sql += " limit " + limit;

        return sd.rawQuery(sql, null);
    }


    public int getMax(String $table_name, String field) {
        // TODO Auto-generated method stub
        String sql = "SELECT max("+field+") FROM "+$table_name;
        Cursor c=sd.rawQuery(sql, null);
        while(c.moveToNext())
        {
            return c.getInt(0);
        }
        c.close();
        return 0;
    }


    public Cursor selectLikeSQL(String $table_name, String[] fields,
                                LinkedHashMap<String, String> whereclausefields, LinkedHashMap<String, String> or_whereclausefields, String orderby,
                                String groupby, String limit, boolean flag) {
        // TODO Auto-generated method stub
        String sql = "select ";
        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1)
                sql += fields[i] + " from " + $table_name + " ";
            else
                sql += fields[i] + ",";
        }

        //sdds = whereclausefields.isEmpty();

        if (!whereclausefields.isEmpty()) {
            sql += " where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {

                sql += " " + entry.getKey() + " like '%" + entry.getValue() + "%' and";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
        }
        if (!or_whereclausefields.isEmpty()) {
            if (!whereclausefields.isEmpty())
                sql += " OR ";
            else
                sql +=" where ";
            for (LinkedHashMap.Entry<String, String> entry : whereclausefields
                    .entrySet()) {

                sql += " " + entry.getKey() + " like '%" + entry.getValue() + "%' or";
            }
            sql = sql.substring(0, sql.lastIndexOf(" ")) + " ";
        }

        if (groupby != null)
            sql += " Group by " + groupby;

        if (orderby != null) {
            sql += " Order By " + orderby;
        }



        if (limit != null)
            sql += " limit " + limit;

        return sd.rawQuery(sql, null);

    }

    public Cursor makeQuery(String sql) {
        // TODO Auto-generated method stub
        return sd.rawQuery(sql, null);
    }

    public Cursor makeQuerys(String tablename) {
        // TODO Auto-generated method stub
        return sd.rawQuery("select * from "+tablename, null);
    }


    public ArrayList<String[]> getData(String tablename) {
        // TODO Auto-generated method stub
        Cursor c= sd.rawQuery("select * from "+tablename, null);
        ArrayList<String[]> lst=new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String[] temp = new String[c.getColumnCount()];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = c.getString(i);
            }
            lst.add(temp);
        }
        c.close();

        return lst;
    }

    public ArrayList<String[]> getDatabyqry(String qry) {
        // TODO Auto-generated method stub
        Cursor c= sd.rawQuery(qry, null);
        ArrayList<String[]> lst=new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String[] temp = new String[c.getColumnCount()];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = c.getString(i);
            }
            lst.add(temp);
        }
        c.close();

        return lst;
    }


    public int makeSumQuery(String $table_name, String field) {
        // TODO Auto-generated method stub

        String sql = "SELECT sum("+field+") FROM "+$table_name;
        Cursor c=sd.rawQuery(sql, null);
        while(c.moveToNext())
        {
            return c.getInt(0);
        }
        c.close();
        return 0;
    }

    public int getCount(String $table_name, String field, String para) {
        // TODO Auto-generated method stub

        String sql = "select count(*) from "+$table_name+" where "+field+"=" + para;
        Cursor c=sd.rawQuery(sql, null);
        while(c.moveToNext())
        {
            return c.getInt(0);
        }
        c.close();
        return 0;
    }
    public int getCount(String $table_name) {
        // TODO Auto-generated method stub

        String sql = "select count(*) from "+$table_name;
        Cursor c=sd.rawQuery(sql, null);
        while(c.moveToNext())
        {
            return c.getInt(0);
        }
        c.close();
        return 0;
    }

}
