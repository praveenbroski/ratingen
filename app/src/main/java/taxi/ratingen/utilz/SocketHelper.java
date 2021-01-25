package taxi.ratingen.utilz;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class SocketHelper {

    static Socket mSocket;
    static SharedPrefence sharedPrefence;
    static SocketListener socketDataReceiver;
    static String TAG = "SocketHelperLog";
    static String TAG_EVENT = "SocketHelperEventLog";
    static String pendingTypeData, typesDriversData = null;

    static Long lastSocketConnected, isDriversLastListed;
    static boolean isInsideTrip = false, isDisconnectCalled = true;

    public static void init(SharedPrefence prefence, SocketListener socketDataReceivers, String tag, boolean isInTrip) {
        sharedPrefence = prefence;
        socketDataReceiver = socketDataReceivers;
        isInsideTrip = isInTrip;
        setSocketListener();
    }

    public static void setSocketListener() {
        if (mSocket != null && mSocket.connected())
            return;
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = true;
        opts.transports = new String[]{WebSocket.NAME};
        try {
            if (mSocket == null)
                mSocket = IO.socket(Constants.URL.SOCKET_URL, opts);
            if (!(mSocket.connected())) {
                Log.v(TAG, "xxxxxxxxxxxxxxxxxxxxx " + (mSocket != null ? ("Is connected: " + mSocket.connected()) : "mSocket is Null"));
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                mSocket.on("user_vehicle_types", types);
                mSocket.on("trip_status", trip_status);
                mSocket.on("cancelled_request", cancelled_request);
                mSocket.on("ride_later_cancelled_because_of_no_driver_found", rideLaterNoCaptainAlert);
                mSocket.on(Constants.NetworkParameters.TIME_TAKES, duration_handler);
                if(isDisconnectCalled)
                    mSocket.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnectSocket() {
        if (mSocket == null)
            return;
        /* ********Trunning Off Socket********/
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            object.put("socket_id", mSocket.id() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("disconnect", object.toString());
        /* *********************************/
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("user_vehicle_types", types);
        mSocket.off("trip_status", trip_status);
        mSocket.off("cancelled_request", cancelled_request);
        mSocket.off("ride_later_cancelled_because_of_no_driver_found", rideLaterNoCaptainAlert);
        mSocket.off(Constants.NetworkParameters.TIME_TAKES, duration_handler);
    }

    private static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "connected");
            JSONObject object = new JSONObject();
            try {
                object.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (socketDataReceiver != null && socketDataReceiver.isNetworkConnected() && isDisconnectCalled) {
                mSocket.emit(Constants.NetworkParameters.start_connect, object.toString());
                isDisconnectCalled=false;
                Log.v(TAG, "start_connect = " + object.toString() + " Connected = " + mSocket.connected());
            }
            /* if ((System.currentTimeMillis() - lastSocketConnected) > 2000 && socketDataReceiver != null
                    && socketDataReceiver.isNetworkConnected() && mSocket != null) {
                mSocket.emit(Constants.NetworkParameters.start_connect, object.toString());
                lastSocketConnected = System.currentTimeMillis();
                Log.v(TAG, "start_connect = " + object.toString());
            }*/
        }
    };

    private static Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "onDisconnect");
            isDisconnectCalled=true;
            if (socketDataReceiver != null)
                socketDataReceiver.OnDisconnect();
        }
    };

    private static Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "onConnectError");
            isDisconnectCalled=true;
            if (socketDataReceiver != null)
                socketDataReceiver.OnConnectError();
        }
    };

    private static Emitter.Listener types = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.v(TAG, "SocketTriggering---***************----types" + args[0]);
            if (socketDataReceiver != null && args != null)
                typesDriversData = args[0].toString();
            socketDataReceiver.Types(args[0].toString());
        }
    };

    private static Emitter.Listener trip_status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.v(TAG_EVENT, "trip_status: " + args[0].toString());
            if (socketDataReceiver != null && args != null)
                socketDataReceiver.TripStatus(args[0].toString());
        }
    };

    private static Emitter.Listener cancelled_request = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG_EVENT, "cancelled_request");
            if (args != null && args.length > 0 && args[0] != null && !CommonUtils.IsEmpty(args[0].toString()) && socketDataReceiver != null)
                socketDataReceiver.CancelledRequest(args[0].toString());
        }
    };

    private static Emitter.Listener rideLaterNoCaptainAlert = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0 && args[0] != null && !CommonUtils.IsEmpty(args[0].toString()) && socketDataReceiver != null) {
                socketDataReceiver.RideLaterNoCaptainAlert(args[0].toString());
            }
        }
    };

    private static Emitter.Listener duration_handler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0 && args[0] != null && !CommonUtils.IsEmpty(args[0].toString()) && socketDataReceiver != null) {
                socketDataReceiver.DurationHandler(args[0].toString());
            }
        }
    };

    public static void sendTypes(String types) {
        Log.v(TAG, "types Data--------------" + types);
        if (mSocket != null) {
//            Log.v(TAG, "types Data--------Socket=" + mSocket.connected());
            if (!mSocket.connected()) {
                setSocketListener();
                pendingTypeData = types;
            } else if (mSocket.connected()) {
//                Log.v(TAG, "types Data----Sent----Socket=" + mSocket.connected());
                mSocket.emit("user_vehicle_types", types);
                pendingTypeData = null;
            }
        }
    }

    public static void sendRiderByTypes(String get_rider_by_type) {
        if (mSocket != null) {
            if (!mSocket.connected())
                setSocketListener();
            mSocket.emit("get_rider_by_type", get_rider_by_type);
            Log.v(TAG, "get_rider_by_type--------------" + get_rider_by_type);
        }
    }

    public static boolean isSocketConnected() {
        return (mSocket != null) && mSocket.connected();
    }

    public static void reConnectSocket() {
        setSocketListener();
    }

    public static String getLastLoadedTypes() {
        Log.v(TAG, "getLastLoadedTypes--------------");
        return typesDriversData;
    }

    public interface SocketListener {

        void Types(String typesString);

        void TripStatus(String trip_status);

        void CancelledRequest(String cancelled_request);

        void RideLaterNoCaptainAlert(String ride_later_no_captain);

        void DurationHandler(String duration_handler);

        boolean isNetworkConnected();

        void OnConnect();

        void OnDisconnect();

        void OnConnectError();

    }

}
