package com.exease.react.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.NotificationManager;
import java.util.ArrayList;
import java.util.Set;

import android.util.Log;

/**
 * A high level notification manager
 *
 * Warps the system notification API to make managing direct and scheduled
 * notification easy.
 */
public class RCTNotificationManager {
    final static String PREFERENCES_KEY = "ReactNativeSystemNotification";
    public Context context = null;
    public SharedPreferences sharedPreferences = null;

    /**
     * Constructor.
     */
    public RCTNotificationManager(Context context) {
        this.context = context;
        this.sharedPreferences = getSharedPreferences(context);
    }

    static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Create a notification.
     */
    public Notification create(
        Integer notificationID,
        NotificationAttributes notificationAttributes
    ) {
        Notification notification = new Notification(context, notificationID, notificationAttributes);

        notification.create();

        return notification;
    }

    /**
     * Create or update (if exists) a notification.
     */
    public Notification createOrUpdate(
        Integer notificationID,
        NotificationAttributes notificationAttributes
    ) {
        if (getIDs().contains(notificationID)) {
            Notification notification = find(notificationID);

            notification.update(notificationAttributes);
            return notification;

        } else {
            return create(notificationID, notificationAttributes);
        }
    }

    /**
     * Get all notification ids.
     */
    public ArrayList<Integer> getIDs() {
        Set<String> keys = sharedPreferences.getAll().keySet();
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for (String key : keys) {
            try {
                ids.add(Integer.parseInt(key));
                // TODO: Delete out-dated notifications BTW
            } catch (Exception e) {
                Log.e("ReactSystemNotification", "RCTNotificationManager: getIDs Error: " + Log.getStackTraceString(e));
            }
        }

        return ids;
    }

    /**
     * Get a notification by its id.
     */
    public Notification find(Integer notificationID) {
        Notification notification = new Notification(context, notificationID, null);

        if (notification.getAttributes() == null) notification.loadAttributesFromPreferences();

        return notification;
    }

    /**
     * Delete a notification by its id.
     */
    public Notification delete(Integer notificationID) {
        return find(notificationID).delete();
    }

    /**
     * Clear a notification by its id.
     */
    public Notification clear(Integer notificationID) {
        return find(notificationID).clear();
    }

    /**
     * Clear all notifications.
     */
    public void clearAll() {
        NotificationManager systemNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        systemNotificationManager.cancelAll();
    }
}
