package taxi.ratingen.ui.forgot;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface ForgotNavigator extends BaseView {

  void   openLoginActivity();
  void   OpenCountrycodeINvisible();
  void   OpenCountrycodevisible();
  String   getCountryCode();
  void   setCountryFlag(String flag);

  BaseActivity getAttachedContext();
}
