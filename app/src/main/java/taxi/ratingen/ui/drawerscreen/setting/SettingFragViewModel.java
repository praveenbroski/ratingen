package taxi.ratingen.ui.drawerscreen.setting;

import android.content.Context;

import androidx.databinding.ObservableField;

import android.view.View;

import com.google.gson.Gson;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 12/20/17.
 */

public class SettingFragViewModel extends BaseNetwork<BaseResponse, SettingNavigator> {
    public ObservableField<String> txt_Language_update = new ObservableField<>();
    SharedPrefence sharedPrefence;

    public SettingFragViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    /** Click listener for language selector. Shows language selection dialog. **/
    public void onClickLanguageChange(View view) {
        showAlertDialog(view.getContext());
    }

    /** Set the selected language name to the settings item **/
    public void setValues() {
        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)))
            txt_Language_update.set(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE));
    }

    /** Creates {@link List} with available languages and shows the language selection dialog **/
    private void showAlertDialog(final Context context) {
        final List<String> items = new ArrayList<>();
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUAGES) != null) {
            for (String key : gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.LANGUAGES), String[].class))
                switch (key) {
                    case "en":
                        items.add("English" + " (en)");
                        break;
                    case "ar":
                        items.add("العربية" + " (ar)");
                        break;
                    case "fr":
                        items.add("française" + " (fr)");
                        break;
                    case "es":
                        items.add("Español" + " (es)");
                        break;
                    case "ja":
                        items.add("日本語" + " (ja)");
                        break;
                    case "ko":
                        items.add("한국어" + " (ko)");
                        break;
                    case "pt":
                        items.add("português" + " (pt)");
                        break;
                    case "zh":
                        items.add("中文" + " (zh)");
                        break;
                }
        }
        getmNavigator().langguageItems(items);

       /* final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getmNavigator().getAtachedContext().getTranslatedString(R.string.text_select_language));
        builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                txt_Language_update.set(items.get(item));
                String language = "English";
                language = items.get(item);
                String loc;
                if (language.equalsIgnoreCase("Portuguese")) {
                    loc = "pt";
                } else if (language.equalsIgnoreCase("Arabic")) {
                    loc = "ar";
                } else if (language.equalsIgnoreCase("Chinese")) {
                    loc = "zh";
                } else if (language.equalsIgnoreCase("Spanish")) {
                    loc = "es";
                } else if (language.equalsIgnoreCase("Japanese")) {
                    loc = "ja";
                } else if (language.equalsIgnoreCase("Korean")) {
                    loc = "ko";
                } else {
                    loc = "en";
                }
                sharedPrefence.savevalue(SharedPrefence.LANGUANGE, loc);
                txt_Language_update.set(loc);
                if ("ar".equalsIgnoreCase(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale("ar"));
                    LanguageUtil.changeLanguageType(context, locale);
                } else if (!TextUtils.isEmpty(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale(loc));
                    LanguageUtil.changeLanguageType(context, locale);
                } else {
                    LanguageUtil.changeLanguageType(context, Locale.ENGLISH);
                }
                if ("ar".equalsIgnoreCase(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale("ar"));
                    LanguageUtil.changeLanguageType(context, locale);
                } else if (!TextUtils.isEmpty(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale(loc));
                    LanguageUtil.changeLanguageType(context, locale);
                } else {
                    LanguageUtil.changeLanguageType(context, Locale.ENGLISH);
                }
                *//*context.startActivity(new Intent(context, DrawerAct.class));
                ((DrawerAct) context).finish();*//*


            }
        });
        AlertDialog alert = builder.create();
        alert.show();*/

    }


}
