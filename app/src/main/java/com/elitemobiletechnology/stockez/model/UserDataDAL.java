package com.elitemobiletechnology.stockez.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by SteveYang on 15/9/28.
 */
public class UserDataDAL {
    private static final String TAG = "UserDataDAL";
    private static UserDataDAL userDataDAL = new UserDataDAL();
    private final static String userDataFileName = "userStocks";

    private UserDataDAL() {
    }

    public static UserDataDAL get() {
        return userDataDAL;
    }

    public void saveUserStockPreference(Context context, ArrayList<StockPreference> map) {
        try {
            File path = context.getFilesDir();
            File file = new File(path, userDataFileName);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (Exception ioe) {
            Log.e(TAG, ioe != null ? ioe.getMessage() : "");
        }
    }

    public ArrayList<StockPreference> getUserStockPreference(Context context) {
        ArrayList<StockPreference> preferenceList = null;
        try {
            File path = context.getFilesDir();
            File file = new File(path, userDataFileName);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                preferenceList = (ArrayList) ois.readObject();
                ois.close();
                fis.close();
            }
        } catch (Exception ioe) {
            Log.e(TAG, ioe.toString());
        }
        if(preferenceList ==null){
            preferenceList = new ArrayList<>();
        }
        return preferenceList;
    }

}
