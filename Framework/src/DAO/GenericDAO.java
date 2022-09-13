package DAO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import inter.KeyAnnotation;
import model.BaseModel;

public class GenericDAO {
    private static Field[] GetFields(Object obj) {
        return obj.getClass().getDeclaredFields();
    }

    private static String[] getAnnotationsValue(Object obj) {
        Field[] champs = GetFields(obj);
        KeyAnnotation[] list = new KeyAnnotation[champs.length];
        String[] listCol = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            list[i] = champs[i].getAnnotation(KeyAnnotation.class);
            if (list[i].column().equals(""))
                listCol[i] = champs[i].getName();
            else
                listCol[i] = list[i].column();
        }
        return listCol;
    }

    public static String[] ToStringStar(Field[] champs,String type) {
        String[] fields = new String[champs.length];
        for (int i = 0; i < champs.length; i++) {
            fields[i] = type + champs[i].getName().toLowerCase();
        }
        return fields;
    }

    public static Boolean checkMethod(Method m, String[] check) {
        for (String s : check) {
            if (m.getName().toLowerCase().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private static Method[] getMethods(Object obj,String type) {
        Field[] champs = GetFields(obj);
        Method[] list = obj.getClass().getDeclaredMethods();
        Method[] nameMethod = new Method[champs.length];
        int j = 0;
        String[] list_method = ToStringStar(champs,type);
        for (Method method : list) {
            if (checkMethod(method, list_method)) {
                nameMethod[j] = method;
                j++;
            }
        }
        Method[] trie = new Method[list_method.length];
        for (int i = 0; i < trie.length; i++) {
            String nom = type + champs[i].getName().toLowerCase();
            for (int l = 0; l < trie.length; l++) {
                if (nom.equals(nameMethod[l].getName().toLowerCase())) {
                    trie[i] = nameMethod[l];
                }
            }
        }
        return trie;
    }
    private static Class[] getTypeMethods(Method[] trie)
    {
        Class[] toReturn = new Class[trie.length];
        for (int i = 0; i < trie.length ; i++) {
            toReturn[i] = trie[i].getReturnType();
        }
        return toReturn;
    }
    public static void saveAll(Object obj, Connection con) throws Exception {
        Field[] list = GetFields(obj);
        Method[] m = getMethods(obj,"get");
        String[] listAnnot = getAnnotationsValue(obj);
        KeyAnnotation table = obj.getClass().getAnnotation(KeyAnnotation.class);
        StringBuilder sql = new StringBuilder("INSERT INTO " + table.nameTable() + "(");
        for (int i = 0; i < listAnnot.length; i++) {
            if (listAnnot[i].equals("")) {
                listAnnot[i] = list[i].getName();
            }
            if (i == listAnnot.length - 1) {
                sql.append(listAnnot[i]).append(") ");
                break;
            }

            sql.append(listAnnot[i]).append(",");
        }
        sql.append("VALUES (");
        for (int i = 0; i < m.length; i++) {
            if (i == m.length - 1) {
                if ((m[i].invoke(obj, new Object[0]) instanceof Integer)
                        || (m[i].invoke(obj, new Object[0]) instanceof Double)) {
                    sql.append(m[i].invoke(obj)).append(")");
                } else {
                    sql.append("'").append(m[i].invoke(obj)).append("')");
                }
                break;
            }
            if ((m[i].invoke(obj, new Object[0]) instanceof Integer)
                    || (m[i].invoke(obj, new Object[0]) instanceof Double)) {
                sql.append(m[i].invoke(obj)).append(",");
            } else {
                sql.append("'").append(m[i].invoke(obj)).append("',");
            }
        }
        System.out.println(sql);
        PreparedStatement stat = null;
        try {
            stat = con.prepareStatement(sql.toString());
        } catch (Exception e) {
            throw e;
        }
        finally{
            stat.close();
            con.close();
        }
    }

    public static void delete(BaseModel obj, Connection con) throws Exception {
        KeyAnnotation table = obj.getClass().getAnnotation(KeyAnnotation.class);
        String sql = "delete from " + table.nameTable() + " where id="+obj.getId();
        System.out.println(sql);
        PreparedStatement stat = null;
        try {
            stat = con.prepareStatement(sql);
           // stat.executeUpdate();
        } catch (Exception e) {
            throw e;
        }
        finally{
            stat.close();
            con.close();
        }
    }
    public static ArrayList<Object> SelectAll(Object obj,Connection con) throws Exception
    {
        KeyAnnotation table = obj.getClass().getAnnotation(KeyAnnotation.class);
        Method[] m = getMethods(obj,"set");
        Class[] type = getTypeMethods(getMethods(obj,"get"));
        String[] listAnnot = getAnnotationsValue(obj);
        ArrayList<Object> list = new ArrayList<Object>();
        String sql = "select * from "+table.nameTable();
        System.out.println(sql);
        PreparedStatement stat = null;
        try {
            stat = con.prepareStatement(sql);
            ResultSet res =  stat.executeQuery();
            int j=0;
            while (res.next()) {
                obj = obj.getClass().getConstructor().newInstance();
                for (Method met:m) {
                    Object zvt = res.getObject(listAnnot[j]);
                    if (zvt.getClass()==Integer.class)
                    {
                        met.invoke(obj,(Integer)zvt);
                    }else{
                        met.invoke(obj,type[j].cast(zvt));
                    }
                    j++;
                }
                list.add(obj);
                j=0;
            }
        } catch (Exception e) {
            throw e;
        }
        finally{
            stat.close();
            con.close();
        }
        return list;
    }
    public static ArrayList<Object> FindByParam(Object obj,Connection con) throws Exception
    {
        ArrayList<Object> list = new ArrayList<Object>();
        Method[] set = getMethods(obj,"set");
        KeyAnnotation table = obj.getClass().getAnnotation(KeyAnnotation.class);

        Method[] get = getMethods(obj,"get");
        Class[] type = getTypeMethods(getMethods(obj,"get"));
        String[] listAnnot = getAnnotationsValue(obj);
        String sql = "select * from "+table.nameTable()+" where 1=1";
        for (int i = 0; i < get.length; i++) {
            if (get[i].invoke(obj)!=null) {
                if ((get[i].invoke(obj) instanceof Integer)
                        || (get[i].invoke(obj) instanceof Double)) {
                    sql+=" AND "+listAnnot[i]+"="+get[i].invoke(obj);
                }else {
                    sql+=" AND "+listAnnot[i]+" like '%"+get[i].invoke(obj)+"%'";
                }
            }
        }
        System.out.println(sql);
        PreparedStatement stat = null;
        try {
            stat = con.prepareStatement(sql);
            ResultSet res =  stat.executeQuery();
            int j=0;
            while (res.next()) {
                obj = obj.getClass().getConstructor().newInstance();
                for (Method met:set) {
                    Object zvt = res.getObject(listAnnot[j]);
                    if (zvt.getClass()==Integer.class)
                    {
                        met.invoke(obj,(Integer)zvt);
                    }else{
                        met.invoke(obj,type[j].cast(zvt));
                    }
                    j++;
                }
                list.add(obj);
                j=0;
            }
        } catch (Exception e) {
            throw e;
        }
        finally{
            stat.close();
            con.close();
        }
        return list;

    }


}
