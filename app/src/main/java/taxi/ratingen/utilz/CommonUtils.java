/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package taxi.ratingen.utilz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.graphics.BitmapCompat;
import androidx.databinding.BindingAdapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.DrawableRes;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.Step;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.exception.CustomException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by amitshekhar on 07/07/17.
 */

public final class CommonUtils {
    private static final double EARTHRADIUS = 6366198;

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    /**
     * @param context reference of the Class
     * @return the progress dialog instance.
     */
    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * @param email user entered email
     * @return true or false whether valid or inValid email.
     */
    public static boolean isEmailValid(String email) {
//        Pattern pattern;
//        Matcher matcher;
//        final String EMAIL_PATTERN =
//                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//        pattern = Pattern.compile(EMAIL_PATTERN);
//        matcher = pattern.matcher(email);
//        return matcher.matches();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    /**
     * @param s user entered string on UI
     * @return true or false whether the entered string is empty or not.
     */
    public static boolean IsEmpty(String s) {
        return s == null || s.isEmpty() || s.equalsIgnoreCase("null") || s.length() == 0;
    }

   /* public static String getTimeStamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }*/

    @BindingAdapter({"image"})
    public static void loadImage(ImageView view, String url) {
        //    Glide.with(view.getContext()).load(url).centerCrop().into(view);
    }

    /*  */

    /**
     * Helper method to format information about a place nicely.
     *//*
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
     *//*   Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));*//*
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }*/

    /**
     * @param bytes Value of ByteArrayOutPutStream.
     * @return null
     */
    public static String WriteImage(ByteArrayOutputStream bytes) {
        File ExternalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(ExternalStorageDirectory + File.separator + "profile.jpg");
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());

            return file.getAbsolutePath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static int GetScreenwidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return displayMetrics.widthPixels;
    }

    public static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    //NOt used
    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    //Not used.
    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }
    /*
     * Only conversion with vector drawable
     * */

    public static BitmapDescriptor getBitmapDescriptor(Context context, @DrawableRes int id) {
      /*  Drawable drawable = ContextCompat.getDrawable(context, id);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);*/

        return BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(context, id));
    }

    /**
     * @param context    instance of particular class or fragment
     * @param drawableId
     * @return bitmap image
     */
    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (context != null)
                drawable = ContextCompat.getDrawable(context, drawableId);
        } else
            drawable = VectorDrawableCompat.create(context.getResources(), drawableId, context.getTheme());
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }


    /**
     * @param value is a Double instance
     * @return a double value with doubleDecimalformat.
     */
    public static String doubleDecimalFromat(Double value) {
        String result = value + "";
        try {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            decimalFormat.setDecimalFormatSymbols(otherSymbols);
            result = decimalFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String doubleDecimalFromatFloat(Float value) {
        String result = value + "";
        try {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            decimalFormat.setDecimalFormatSymbols(otherSymbols);
            result = decimalFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Not used
     *
     * @param value is a Double instance
     * @return a double value with trippleDecimalFromat.
     */
    public static String trippleDecimalFromat(Double value) {
        if (value == null)
            return value + "";
        String result = value + "";
        try {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("0.000", otherSymbols);
            result = decimalFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param phoneNumber is string from user entered.
     * @return a string value of remove first zero.
     */
    public static String removeFirstZeros(String phoneNumber) {
        String resolvedPhoeNumber = phoneNumber;
        do {
            if (resolvedPhoeNumber.startsWith("0"))
                resolvedPhoeNumber = resolvedPhoeNumber.substring(1, resolvedPhoeNumber.length() == 1 ? resolvedPhoeNumber.length() : resolvedPhoeNumber.length());
        } while (resolvedPhoeNumber.startsWith("0"));
        return resolvedPhoeNumber;
    }

    /**
     * @param context is instance of particular class.
     * @return a boolean value whether Gps is on or not
     */
    public static boolean isGpscheck(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void ShowGpsDialog(final Activity context) {

        android.app.AlertDialog.Builder gpsBuilder = new android.app.AlertDialog.Builder(context);
        gpsBuilder.setCancelable(false);
        gpsBuilder.setTitle(context.getString(R.string.dialog_no_gps))
                .setMessage(context.getString(R.string.dialog_no_gps_messgae))
                .setPositiveButton(context.getString(R.string.dialog_enable_gps), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivityForResult(intent, BaseActivity.REQUEST_ENABEL_LOCATION);

                     /*   Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                        intent.putExtra("enabled", true);
                        context.sendBroadcast(intent);*/
                        dialog.dismiss();


                    }
                })
                .setNegativeButton(context.getString(R.string.Txt_Exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                        dialog.dismiss();
                    }
                });
        AlertDialog gpsAlertDialog = gpsBuilder.create();
        gpsAlertDialog.show();
    }

    /**
     * @param response  is array of latlng for draw a route.
     * @param routeBean
     * @return
     */
    public static Route parseRoute(JsonObject response, Route routeBean) {

        Step stepBean;
        //   JSONObject jObject = new JSONObject(response);
        JsonArray jArray = response.getAsJsonArray("routes");
        for (int i = 0; i < jArray.size(); i++) {
            JsonObject innerjObject = jArray.get(i).getAsJsonObject();
            if (innerjObject != null) {
                JsonArray innerJarry = innerjObject.getAsJsonArray("legs");
                for (int j = 0; j < innerJarry.size(); j++) {
                    JsonObject jObjectLegs = innerJarry.get(j).getAsJsonObject();
                    routeBean.setDistanceText(jObjectLegs.getAsJsonObject("distance").get("text").getAsString());
                    routeBean.setDistanceValue(jObjectLegs.getAsJsonObject("distance").get("value").getAsInt());
                    routeBean.setDurationText(jObjectLegs.getAsJsonObject("distance").get("text").getAsString());
                    routeBean.setDurationValue(jObjectLegs.getAsJsonObject("duration").get("value").getAsInt());
                    routeBean.setStartAddress(jObjectLegs.get("start_address").getAsString());
                    if (jObjectLegs.has("end_address"))
                        routeBean.setEndAddress(jObjectLegs.get("end_address").getAsString());
                    routeBean.setStartLat(jObjectLegs.getAsJsonObject("start_location").get("lat").getAsDouble());
                    routeBean.setStartLon(jObjectLegs.getAsJsonObject("start_location").get("lng").getAsDouble());
                    routeBean.setEndLat(jObjectLegs.getAsJsonObject("end_location").get("lat").getAsDouble());
                    routeBean.setEndLon(jObjectLegs.getAsJsonObject("end_location").get("lng").getAsDouble());
                    JsonArray jstepArray = jObjectLegs
                            .getAsJsonArray("steps");
                    if (jstepArray != null) {
                        for (int k = 0; k < jstepArray.size(); k++) {
                            stepBean = new Step();
                            JsonObject jStepObject = jstepArray
                                    .get(k).getAsJsonObject();
                            if (jStepObject != null) {

                                stepBean.setHtml_instructions(jStepObject
                                        .get("html_instructions").getAsString());
                                stepBean.setStrPoint(jStepObject
                                        .getAsJsonObject("polyline")
                                        .get("points").getAsString());
                                stepBean.setStartLat(jStepObject
                                        .getAsJsonObject("start_location")
                                        .get("lat").getAsDouble());
                                stepBean.setStartLon(jStepObject
                                        .getAsJsonObject("start_location")
                                        .get("lng").getAsDouble());
                                stepBean.setEndLat(jStepObject
                                        .getAsJsonObject("end_location")
                                        .get("lat").getAsDouble());
                                stepBean.setEndLong(jStepObject
                                        .getAsJsonObject("end_location")
                                        .get("lng").getAsDouble());

                                stepBean.setListPoints(new PolyLineUtils()
                                        .decodePoly(stepBean.getStrPoint()));
                                routeBean.getListStep().add(stepBean);
                            }
                        }
                    }
                }

            }

        }
        return routeBean;
    }

    /**
     * @param json       is a string value of Api response.
     * @param modelclass is pojo class which the response depends.
     * @param <T>        is Any dataType.
     * @return nothing
     */
    public static <T> T getSingleObject(String json, Class<T> modelclass) {
        try {
            return new Gson().fromJson(json, modelclass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ObjectToString(Object data) {
        return new Gson().toJson(data);
    }

    public static Object StringToObject(String message, Class objectClassName) {
        return new Gson().fromJson(message, objectClassName);
    }

    public static <T> String arrayToString(ArrayList<T> list) {
        Gson g = new Gson();
        return g.toJson(list);
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    /**
     * @param inputValue is a string contains CVV number of Added card.
     * @return the ReOrdered cvv number.
     */
    public static String getReOrdered(String inputValue) {
        String result = inputValue;
        try {
            if (inputValue != null && inputValue.length() > 0) {
                char[] arrayChar = inputValue.toCharArray();
                result = arrayChar[arrayChar.length - 1] + "";
                if (arrayChar.length > 1)
                    for (int i = 0; i < (arrayChar.length - 1); i++) {
                        result += arrayChar[i];
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param inputValue is a string value CVV.
     * @return the Random number instead of CVV.
     */
    public static String addRandomNumber(String inputValue) {
        String result = inputValue;
        try {
            result = (10 + new Random().nextInt(90)) + inputValue;
            result = result + (100 + new Random().nextInt(900));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param inputValue is a string of CVV number
     * @return the encrypted CVV number.
     */
    public static String convertBase64(String inputValue) {
        String result = inputValue;
        try {
            byte[] data = inputValue.getBytes(StandardCharsets.UTF_8);

            result = Base64Utils.encode(data).trim();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * @param list countrycode list
     * @param defaultCountry String with current/Default country.
     * @return CountryListModel with selected country from the list
     */
    public static CountryListModel getDefaultCountryDetails(List<CountryListModel> list, String defaultCountry) {
        for (CountryListModel model : list) {
            if (model.iso2!=null&&model.iso2.equalsIgnoreCase(defaultCountry)) {
                return model;
            }
        }
        return null;
    }

    public static String getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage = getResizedBitmapLessThan500KB(inImage, 500);
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = null;//= MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        try {
            path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File file = new File(path, System.currentTimeMillis() + ".jpg");
            fOut = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close();
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (path);
    }

    public static Bitmap getResizedBitmapLessThan500KB(Bitmap image, int maxSize) {
        if (BitmapCompat.getAllocationByteCount(image) > (1000 * 1024)) {
            int width = image.getWidth();
            int height = image.getHeight();
            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 0) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(image, width, height, true);
        } else {
            return image;
        }
    }

    public static String converErrors(ResponseBody response) {
        //  Converter<ResponseBody, BaseResponse> converter = .responseBodyConverter(BaseResponse.class, new Annotation[0]);
        String value = "";
        try {
            BaseResponse baseResponse = (BaseResponse) CommonUtils.StringToObject(response.string(), BaseResponse.class);
            if (baseResponse != null && baseResponse.getErrors() != null) {
                Log.e("respo==", "" + baseResponse.getErrors());
                for (Map.Entry<String, List<String>> error : baseResponse.getErrors().entrySet()) {
                    value = KeySearchClass.KeySearch(error);
                }
            } else {
                Log.e("ErrorMessage==", "msg" + baseResponse.message);
                if (!TextUtils.isEmpty(baseResponse.message))
                    value = baseResponse.message;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static CustomException convertToException(ResponseBody response) {
        String value = "";
        CustomException exception = null;
        try {
            BaseResponse baseResponse = (BaseResponse) CommonUtils.StringToObject(response.string(), BaseResponse.class);
            if (baseResponse != null && baseResponse.getErrors() != null) {
                Log.e("respo==", "" + baseResponse.getErrors());
                for (Map.Entry<String, List<String>> error : baseResponse.getErrors().entrySet()) {
                    value = KeySearchClass.KeySearch(error);
                }
                if (IsEmpty(value)) {
                    value = baseResponse.message;
                }
                exception = new CustomException(baseResponse.statusCode, value);
            } else {
                Log.e("ErrorMessage==", "msg" + baseResponse.message);
                if (!TextUtils.isEmpty(baseResponse.message))
                    value = baseResponse.message;
                exception = new CustomException(baseResponse.statusCode, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return exception;
    }

}
