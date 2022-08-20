package com.ei.kalavarafoods;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ei.kalavarafoods.db.notificaiton.NotificationDbContract;
import com.ei.kalavarafoods.db.notificaiton.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class DBHelperDisplayItems extends SQLiteOpenHelper implements
        NotificationDbContract.TableOperations, NotificationDbContract.TableConstants {

    public static final String DATABASE_NAME = "MetroMart.db";
    public static final String TABLE_NAME = "Test";
    public static final String TABLE_MAIN_SUB_DATA = "TableMainSubData";

    public static final String COL_1 = "PRODUCT_CATEGORY";
    public static final String COL_2 = "PRODUCT_ID";
    public static final String COL_3 = "TITLE";
    public static final String COL_4 = "UNIT";
    public static final String COL_5 = "PRICE_OLD";
    public static final String COL_6 = "PRICE_NEW";
    public static final String COL_7 = "IMAGE";
    public static final String COL_8 = "ORDER_QUANTITY";
    public static final String COL_9 = "WISHLIST_STATUS";
    private static final String COL_10 = "PRODUCT_OFFERPRICE";
    private static final String MSCOL_1 = "category_main";
    private static final String MSCOL_2 = "category_id";
    private static final String MSCOL_3 = "category_title";
    private static final String MSCOL_4 = "category_name";


    public DBHelperDisplayItems(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(PRODUCT_CATEGORY TEXT,PRODUCT_ID TEXT,TITLE TEXT,UNIT TEXT,PRICE_OLD TEXT,PRICE_NEW TEXT,IMAGE TEXT,ORDER_QUANTITY TEXT,WISHLIST_STATUS TEXT,PRODUCT_OFFERPRICE TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MAIN_SUB_DATA + "(category_main TEXT,category_id TEXT,category_title TEXT,category_name TEXT)");
        db.execSQL(NotificationDbContract.DbExplorer.CREATE_NOTIFICATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN_SUB_DATA);
        db.execSQL(NotificationDbContract.DbExplorer.DROP_NOTIFICATION_TABLE);
        onCreate(db);
    }

    public boolean addItems(String Product_Category, String Product_ID, String Title, String Unit, String Price_Old, String Price_New,
                            String Image, String Order_Quantity, String Wishlist_Status, String Offer_Price) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, Product_Category);
        contentValues.put(COL_2, Product_ID);
        contentValues.put(COL_3, Title);
        contentValues.put(COL_4, Unit);
        contentValues.put(COL_5, Price_Old);
        contentValues.put(COL_6, Price_New);
        contentValues.put(COL_7, Image);
        contentValues.put(COL_8, Order_Quantity);
        contentValues.put(COL_9, Wishlist_Status);
        contentValues.put(COL_10, Offer_Price);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public Cursor getCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM "+NOTIFICATION_TABLE_NAME,null);
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_8 + " > 0 ", null);
        return res;
    }

    public boolean updateItems(String OrderQuantity, String ItemID) { // update the cart
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, OrderQuantity);
        db.update(TABLE_NAME, contentValues, "PRODUCT_ID = ?", new String[]{ItemID});
        return true;
    }

    public boolean updateWishProduct(String wish, String ItemID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_9, wish);
        db.update(TABLE_NAME, contentValues, "PRODUCT_ID = ?", new String[]{ItemID});
        return true;
    }

    // whole item update
    public synchronized boolean updateItemDetails(String Product_Category, String Product_ID, String Title,
                                                  String Unit, String Price_Old, String Price_New, String Image,
                                                  String Order_Quantity, String Wishlist_Status, String Offer_Price) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, Product_Category);
        contentValues.put(COL_3, Title);
        contentValues.put(COL_4, Unit);
        contentValues.put(COL_5, Price_Old);
        contentValues.put(COL_6, Price_New);
        contentValues.put(COL_7, Image);
//        contentValues.put(COL_8, Order_Quantity);
        contentValues.put(COL_9, Wishlist_Status);
        contentValues.put(COL_10, Offer_Price);
        db.update(TABLE_NAME, contentValues, "PRODUCT_ID = ?", new String[]{Product_ID});
        return true;
    }

    public boolean DeleteCartItems(String ItemID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, "0");
        db.update(TABLE_NAME, contentValues, "PRODUCT_ID = ?", new String[]{ItemID});
        return true;
    }

    public boolean ClearCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, "0");
        db.update(TABLE_NAME, contentValues, "PRODUCT_ID != ?", new String[]{"0"});
        return true;
    }

    public void deleteItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void clearItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM");
    }

    public void clearMainSubData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MAIN_SUB_DATA);
        db.execSQL("VACUUM");
        Log.e("cleared", "MSData");
    }

    public void addMainSubData(String category_main, String category_id, String category_title, String category_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MSCOL_1, category_main);
        contentValues.put(MSCOL_2, category_id);
        contentValues.put(MSCOL_3, category_title);
        contentValues.put(MSCOL_4, category_name);
        Log.e("result ", String.valueOf(db.insert(TABLE_MAIN_SUB_DATA, null, contentValues)));
    }

    public Cursor getMainSubData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_MAIN_SUB_DATA, null);
        return res;
    }

    public String getOrderQuantity(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_8 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = " + id, null);
        cursor.moveToFirst();
        String orderQuantity = cursor.getString(cursor.getColumnIndex(COL_8));
        cursor.close();
        return orderQuantity;
    }

    @Override
    public void insertNotification(NotificationItem notificationItem) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, notificationItem.getTitle());
        contentValues.put(COLUMN_MESSAGE, notificationItem.getMessage());
        contentValues.put(COLUMN_IMAGE, notificationItem.getImage());
        db.insert(NOTIFICATION_TABLE_NAME, null, contentValues);
    }

    @Override
    public List<NotificationItem> readNotificationList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(NotificationDbContract.DbExplorer.SELECT_ALL_FROM_NOTIFICATION_TABLE, null);
        List<NotificationItem> notificationItemList = new ArrayList<>();
        while (cursor.moveToNext())
            notificationItemList.add(readSingleNotificationFromCursor(cursor));
        return notificationItemList;
    }

    @Override
    public NotificationItem readSingleNotificationFromCursor(Cursor cursor) {
        NotificationItem notificationItem = new NotificationItem();
        notificationItem.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        notificationItem.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        notificationItem.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)));
        notificationItem.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
        return notificationItem;
    }
}
