package taxi.ratingen.ui.drawerscreen.profilescrn;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 12/27/17.
 */

public interface ProfileNavigator extends BaseView{

    void OpenCameraIntent();
    void OpenGalleryIntent();
    void SendBroadcast();
    void finishAct();

    void alertSelectCameraGalary();
    void refreshFocusScroll();

    BaseActivity getBaseAct();

    void openFirstNameUpdate();

    void openLastNameUpdate();

    void openMailUpdate();

    void openPhoneUpdate();

    void languageItems(List<String> items);

    void setSelectedLanguage(String s);

    void refreshScreen();

}
