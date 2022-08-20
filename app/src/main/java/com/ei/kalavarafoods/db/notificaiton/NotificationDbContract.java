package com.ei.kalavarafoods.db.notificaiton;

import android.database.Cursor;

import com.ei.kalavarafoods.db.notificaiton.model.NotificationItem;

import java.util.List;

/**
 * Created by ULLAS BABU on 02-Dec-17.
 */

public class NotificationDbContract {
    public class DbExplorer implements TableConstants {
        public static final String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + NOTIFICATION_TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                COLUMN_TITLE + " TEXT," + COLUMN_MESSAGE + " TEXT," + COLUMN_IMAGE + " TEXT)";

        public static final String DROP_NOTIFICATION_TABLE = "DROP TABLE IF EXISTS " + NOTIFICATION_TABLE_NAME;
        public static final String SELECT_ALL_FROM_NOTIFICATION_TABLE = "SELECT * FROM " + NOTIFICATION_TABLE_NAME
                + " ORDER BY " + COLUMN_ID + " DESC";
    }

    public interface TableConstants {
        String NOTIFICATION_TABLE_NAME = "notificationTable";
        String COLUMN_ID = "id";
        String COLUMN_TITLE = "offer";
        String COLUMN_MESSAGE = "validTill";
        String COLUMN_IMAGE = "image";
    }

    public interface TableOperations {
        void insertNotification(NotificationItem notificationItem);

        List<NotificationItem> readNotificationList();

        NotificationItem readSingleNotificationFromCursor(Cursor cursor);
    }
}
