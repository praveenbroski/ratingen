package taxi.ratingen.ui.registration;

import android.content.Intent;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface RegistrationNavigator extends BaseView{

    void OpenCameraIntent();

    void OpenGalleryIntent();

    void OpenApplayRefferalLayout();

    void setPhoneNumber(String phoneNumber);

    void setDisablePhoneNumber();

    boolean getPermissions();

    void hideVisibleKeyboard();

//    String getCountryCode();
//    String getCountryNameShort();
//    void setCountryCode(String flag);
//    void Facebookclicked();
//    void gplusclicked();

    void OpenDrawerAct();

    BaseActivity getBaseAct();

    void goBack();

    void getPath(Intent data);

}
