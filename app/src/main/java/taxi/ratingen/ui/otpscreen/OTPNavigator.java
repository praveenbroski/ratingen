package taxi.ratingen.ui.otpscreen;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface OTPNavigator extends BaseView {

    String getOpt();

    void openSignUpActivity();

    void FinishAct();

    void openHomeActivity();

    void startTimer(int resendTimer);

    BaseActivity getBaseAct();

    void openResendAlert();

    void finishActWithResult();

}
