package taxi.ratingen.ui.drawerscreen.addcard;

import androidx.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import java.util.HashMap;

public class AddCardViewModel extends BaseNetwork<BaseResponse, AddCardNavigator> {

    public ObservableField<String> cardExpiry = new ObservableField<>();
    public ObservableField<String> cardNumber = new ObservableField<>();
    public ObservableField<String> cardCVV = new ObservableField<>();
    public ObservableField<String> cardName = new ObservableField<>();
    HashMap<String, String> hashMap;

    public AddCardViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
    }

    /** form {@link HashMap} with query parameters for API call **/
    @Override
    public HashMap<String, String> getMap() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));

        hashMap.put(Constants.NetworkParameters.CardHolderName, cardName.get());
        hashMap.put(Constants.NetworkParameters.CVV_NUM, CommonUtils.convertBase64(CommonUtils.addRandomNumber(CommonUtils.getReOrdered(cardCVV.get()))));
        hashMap.put(Constants.NetworkParameters.EXP_DATE, cardExpiry.get());
        hashMap.put(Constants.NetworkParameters.CARD_NUM, cardNumber.get().replace(" ", ""));
        return hashMap;
    }

    /** {@link TextWatcher} to add space every 4 characters in card no. field **/
    public TextWatcher cardTextWatcher = new TextWatcher() {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 16) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;
        }
    };

//    public TextWatcher cardTextWatcher = new TextWatcher() {
//
//        private static final int TOTAL_SYMBOLS = 27; // size of pattern 0000-0000-0000-0000-0000-0000
//        private static final int TOTAL_DIGITS = 22; // max numbers of digits in pattern: 0000 x 4
//        private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
//        private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
//        private static final char DIVIDER = ' ';
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // noop
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // noop
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
//                s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
//            }
//        }
//
//        private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
//            boolean isCorrect = s.length() < totalSymbols; // check size of entered string
//            for (int i = 0; i < s.length(); i++) { // check that every element is right
//                if (i > 0 && (i + 1) % dividerModulo == 0) {
//                    isCorrect &= divider == s.charAt(i);
//                } else {
//                    isCorrect &= Character.isDigit(s.charAt(i));
//                }
//            }
//            return isCorrect;
//        }
//
//        private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
//            final StringBuilder formatted = new StringBuilder();
//
//            for (int i = 0; i < digits.length; i++) {
//                if (digits[i] != 0) {
//                    formatted.append(digits[i]);
//                    if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
//                        formatted.append(divider);
//                    }
//                }
//            }
//
//            return formatted.toString();
//        }
//
//        private char[] getDigitArray(final Editable s, final int size) {
//            char[] digits = new char[size];
//            int index = 0;
//            for (int i = 0; i < s.length() && index < size; i++) {
//                char current = s.charAt(i);
//                if (Character.isDigit(current)) {
//                    digits[index] = current;
//                    index++;
//                }
//            }
//            return digits;
//        }
//    };

    /** {@link TextWatcher} to add '/' between month & year in card expiry field **/
    public TextWatcher cardExpiryTextWatcher = new TextWatcher() {

        private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
        private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
        private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
        private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
        private static final char CARD_DATE_DIVIDER = '/';

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // noop
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // noop
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
                s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
            }
        }

        private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
            boolean isCorrect = s.length() <= size;
            for (int i = 0; i < s.length(); i++) {
                if (i > 0 && (i + 1) % dividerPosition == 0) {
                    isCorrect &= divider == s.charAt(i);
                } else {
                    isCorrect &= Character.isDigit(s.charAt(i));
                }
            }
            return isCorrect;
        }

        private String concatString(char[] digits, int dividerPosition, char divider) {
            final StringBuilder formatted = new StringBuilder();

            for (int i = 0; i < digits.length; i++) {
                if (digits[i] != 0) {
                    formatted.append(digits[i]);
                    if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                        formatted.append(divider);
                    }
                }
            }

            return formatted.toString();
        }

        private char[] getDigitArray(final Editable s, final int size) {
            char[] digits = new char[size];
            int index = 0;
            for (int i = 0; i < s.length() && index < size; i++) {
                char current = s.charAt(i);
                if (Character.isDigit(current)) {
                    digits[index] = current;
                    index++;
                }
            }
            return digits;
        }
    };

    /** called when API call is successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        if(response.successMessage.equalsIgnoreCase("Card_Added_Successfully"))
        {
          getmNavigator().openPaymentFrag(response.getPayment());
        }
    }

    /** called when API call fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        if(e!=null &&e.getMessage()!=null){
            getmNavigator().showMessage(e.getMessage());
        }

    }

    /** calls add card API when add card button is clicked **/
    public void onClickPayNow(View view) {
        getmNavigator().showMessage(translationModel.txt_this_feature_unvailabe_demo);
        return;
      /*  if (CommonUtils.IsEmpty(cardNumber.get())) {
            getmNavigator().showMessage("Card number should not be empty");
        } else if (cardNumber.get().length() < 12) {
            getmNavigator().showMessage("Card Number Not valid");
        } else if (CommonUtils.IsEmpty(cardCVV.get())) {
            getmNavigator().showMessage("CVV cannot be Empty");
        } else if (CommonUtils.IsEmpty(cardExpiry.get())) {
            getmNavigator().showMessage("Expiry Date cannot be empty");
        } else if (CommonUtils.IsEmpty(cardName.get())) {
            getmNavigator().showMessage("Name cannot be Empty");
        } else {
            addCardNetwork();
        }*/
    }
}
