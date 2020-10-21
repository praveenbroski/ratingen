package taxi.ratingen.ui.drawerscreen.notification;

import taxi.ratingen.retro.base.BaseResponse;

import java.util.List;

public interface NotificationListNavigator {
    void openNotifications(List<BaseResponse.NotificationList> notificationList);

    boolean isNetworkConnected();
}
