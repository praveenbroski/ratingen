package taxi.ratingen.ui.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Resources resources;
    SharedPrefence sharedPrefence;


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "DATA: " + remoteMessage.getData());
        Log.d(TAG, "Id: " + remoteMessage.getMessageId());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            if (remoteMessage.getNotification().getTitle() != null)
                displayNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
            else
                displayNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            if (CommonUtils.IsEmpty(remoteMessage.getData().get("message")))
                return;
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().get("message"));
                BaseResponse model = CommonUtils.getSingleObject(json.toString(), BaseResponse.class);

                if (remoteMessage.getData().get("title") != null &&
                        (remoteMessage.getData().get("title").equalsIgnoreCase("general notification") || remoteMessage.getData().get("title").equalsIgnoreCase("generalnotification"))) {
                    sendNotification(remoteMessage.getData().get("message"), 1);
                } else handleNow();


                if (!json.has("success_message"))
                    return;

                if (!json.isNull("success_message") && model.successMessage.equalsIgnoreCase("bill_generated") ||
                        model.successMessage.equalsIgnoreCase("driver arrived") ||
                        model.successMessage.equalsIgnoreCase("Trip Started") ||
                        model.successMessage.equalsIgnoreCase("pay_by_cash") ||
                        model.successMessage.equalsIgnoreCase("pay_balance_amount")) {
                  /*  Intent intent = new Intent(Constants.PushWaitingorAcceptByDriver);
                    intent.putExtra(Constants.EXTRA_Data, json.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
                    if (model.successMessage.equalsIgnoreCase("bill_generated")) {
                        Intent intent = new Intent(Constants.PushTripCompleted);
                        intent.putExtra(Constants.EXTRA_Data, json.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        sendNotification(getTranslatedString(R.string.Txt_TripCompleted), json.getInt("msg_id"));
                    } else if (model.successMessage.equalsIgnoreCase("Trip Started")) {
                        Intent intent = new Intent(Constants.PushTripCompleted);
                        intent.putExtra(Constants.EXTRA_Data, json.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        sendNotification(getTranslatedString(R.string.Txt_TripStarted), 23496);
                    } else if (model.successMessage.equalsIgnoreCase("driver arrived")) {
                        Intent intent = new Intent(Constants.PushTripCompleted);
                        intent.putExtra(Constants.EXTRA_Data, json.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        sendNotification(getTranslatedString(R.string.txt_arrived), 2342);
                    } else if (model.successMessage.equalsIgnoreCase("pay_by_cash"))
                        sendNotification(json.getString("message"), json.getInt("msg_id"));
                    else if (model.successMessage.equalsIgnoreCase("pay_balance_amount"))
                        sendNotification(json.getString("message"), json.getInt("msg_id"));


                } else if (!json.isNull("success_message") && model.successMessage.equalsIgnoreCase("Accepted")) {
                    Intent intent = new Intent(Constants.PushWaitingorAcceptByDriver);
                    intent.putExtra(Constants.EXTRA_Data, json.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    sendNotification(getTranslatedString(R.string.Txt_DriverAccepted), json.getInt("msg_id"));

                    // its for leave from top driver page when trip accepted.
                    Intent intent11 = new Intent(Constants.pushRejected);
                    intent11.putExtra("message", "Ride Has been scheduled");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent11);
                    //   startActivity(new Intent(this, DrawerAct.class).putExtra("Accept", "Accepted"));
                } else if (!json.isNull("success_message") && model.successMessage.equalsIgnoreCase("driver_rejected")) {
//                    TopDriverAct.show(this);
                    //this is for stay the top driver page when driver reject the request.
                    Intent intent = new Intent(Constants.pushRejected);
                    intent.putExtra("message", "Request Rejected");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else if (!json.isNull("success_message") && model.successMessage.equalsIgnoreCase("Trip cancelled")) {
                    Intent intent = new Intent(Constants.PushTripCancelled);
                    intent.putExtra(Constants.EXTRA_Data, model.successMessage);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    sendNotification(getTranslatedString(R.string.Txt_TripCanceled), json.getInt("msg_id"));
                } else if (!json.isNull("success_message") && model.successMessage.equalsIgnoreCase("no_driver_found")) {
                    Intent intent = new Intent(Constants.PushWaitingorAcceptByDriver);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    sendNotification(getTranslatedString(R.string.Txt_NoDriverFound), json.getInt("msg_id"));

                    Intent intent11 = new Intent(Constants.pushRejected);
                    intent11.putExtra("message", "No Driver Found");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent11);

                } else if (model.successMessage.equalsIgnoreCase("another_user_loggedin")) {
                    sendNotification(model.message, remoteMessage.getData().get("message"));

                } else if (!json.isNull("success_message") && model.successMessage.equalsIgnoreCase("Driver_started")) {
                    Intent intent = new Intent(Constants.PushWaitingorAcceptByDriver);
                    intent.putExtra(Constants.EXTRA_Data, json.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    sendNotification(getTranslatedString(R.string.Driver_started), json.getInt("msg_id"));
                } else if (model.successMessage.equalsIgnoreCase("requested_another_driver")) {
                    Log.e("anotherDriver==", "driver==" + "anotherriver");
                    Log.e("anotherDriver121==", "driver==" + model.request.request_id + "vd" + model.request.id);

                    Intent intent11 = new Intent(Constants.driverChanged);
                    intent11.putExtra("req_id", model.request.request_id);
                    intent11.putExtra("id", model.request.id);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent11);
                } else if (model.successMessage.equalsIgnoreCase("ride_later_cancelled_because_of_no_driver_found"))
                    sendNoDriverNotification(getTranslatedString(R.string.Txt_NoDriverFound), model.request.id);
                else if (model.successMessage.equalsIgnoreCase("user_declined"))
                    sendNotification(getTranslatedString(R.string.txt_user_declained), json.getInt("msg_id"));
                else if (model.successMessage.equalsIgnoreCase("user_actived"))
                    sendNotification(getTranslatedString(R.string.txt_user_approved), json.getInt("msg_id"));
                else if (model.successMessage.equalsIgnoreCase("user_cancellation_owes_waring"))
                    sendNotification(remoteMessage.getData().get("title"), json.getInt("msg_id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void Sendpath(JSONArray images) {

    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, int mesgid) {
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        try {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.ic_small_logo)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            JSONObject jMsg = new JSONObject(messageBody);
            if (jMsg.has("push_title"))
                notificationBuilder.setContentTitle(jMsg.getString("push_title"));
            else
                notificationBuilder.setContentTitle(getString(R.string.app_name));

            if (jMsg.has("push_sub_title"))
                notificationBuilder.setSubText(jMsg.getString("push_sub_title"));
            else
                notificationBuilder.setContentText(messageBody);

            if (jMsg.has("push_content"))
                notificationBuilder.setContentText(jMsg.getString("push_content"));
            else
                notificationBuilder.setContentText(messageBody);

            if (jMsg.has("image1") && !jMsg.getString("image1").equalsIgnoreCase("null")) {
                String iUrl = jMsg.getString("image1");
                try {
                    InputStream in = new URL(iUrl).openStream();
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    notificationBuilder.setLargeIcon(bmp);
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (jMsg.has("has_redirect_url") && jMsg.has("redirect_url")) {
                if (jMsg.getString("has_redirect_url").equals("1")) {
                    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
                    resultIntent.setData(Uri.parse(jMsg.getString("redirect_url")));

                    PendingIntent pending = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.setContentIntent(pending);

                    notificationBuilder.addAction(R.drawable.ic_link, getString(R.string.txt_open_link), pending);
                }
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, getString(R.string.default_notification_channel_id), importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
            wakupScreen();
        } catch (Exception e) {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.ic_small_logo)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(messageBody))
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, getString(R.string.default_notification_channel_id), importance);
                notificationManager.createNotificationChannel(mChannel);

            }
            notificationManager.notify(mesgid, notificationBuilder.build());
            wakupScreen();
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNoDriverNotification(String messageBody, int messageForIntente) {

        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.LATER_NO_DRIVER);
        intentBroadcast.putExtra("req_id", messageForIntente);
        intentBroadcast.putExtra("from", "push");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.ic_small_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String messageForIntente) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.B_REQUEST);
        intentBroadcast.putExtra(Constants.BroadcastsActions.B_REQUEST, messageForIntente);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);


        }
        notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }

    /** {@link android.os.PowerManager.WakeLock} is used to bring device from sleep mode **/
    private void wakupScreen() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, Constants.WAKE_LOCK_TAG);
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.WAKE_LOCK_TAG2);
            wl_cpu.acquire(10000);
        }
    }

    private void displayNotification(String messageBody) {
        Intent intent = new Intent(this, DrawerAct.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //  the activity from a service
        String channelId = getString(R.string.default_notification_ride_admin);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_ride_admin), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_ride_admin, notificationBuilder.build());
        wakupScreen();
    }

    private void displayNotification(String messageBody, String title) {
        Intent intent = new Intent(this, DrawerAct.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //  the activity from a service
        String channelId = getString(R.string.default_notification_ride_admin);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_ride_admin), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_ride_admin, notificationBuilder.build());
        wakupScreen();
    }

    /** Returns translated string for given resource id **/
    public String getTranslatedString(int id) {
        if (resources == null)
            resources = getResources();
        if (resources.getResourceEntryName(id).equalsIgnoreCase("app_name") || resources.getResourceEntryName(id).equalsIgnoreCase("app_title"))
            return super.getString(id);
        else
            return getLocalizedString(resources.getResourceEntryName(id));
    }

    /** Uses resource id to get translation from Translation sheet response **/
    public String getLocalizedString(String resId) {
        if (sharedPrefence == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPrefence = new SharedPrefence(preferences, preferences.edit());
        }
        String result = resId;
        try {
            JSONObject jsonObject;
            if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
                jsonObject = new JSONObject(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)));
                result = jsonObject.optString(resId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(result))
                result = resId;
        }
        return result;
    }
}