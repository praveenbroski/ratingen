package taxi.ratingen.ui.companyvalidation;

import android.content.Context;

import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface CompanyValidationNavigator extends BaseView {
    Context getAttachedContext();

    void showRequestDialog();
    void openDrawerActivity();
    void showExpiryMessage(String expirysoon);
}
