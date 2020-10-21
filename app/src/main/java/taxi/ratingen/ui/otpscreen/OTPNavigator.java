package taxi.ratingen.ui.otpscreen;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface OTPNavigator extends BaseView {

    public String getOpt();

    public void openSinupuActivity();

    public void FinishAct();

    public void openHomeActivity();

    void startTimer(int resendTimer);

    void resendOtp();

    BaseActivity getBaseAct();
}
