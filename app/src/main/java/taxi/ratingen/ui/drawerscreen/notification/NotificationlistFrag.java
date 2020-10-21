package taxi.ratingen.ui.drawerscreen.notification;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.NotificationListLayBinding;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.ui.base.BaseFragment;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class NotificationlistFrag extends BaseFragment<NotificationListLayBinding, NotificationListViewModel> implements NotificationListNavigator {

    public static final String TAG = "NotificationlistFrag";

    @Inject
    NotificationListViewModel notificationListViewModel;
    NotificationListLayBinding notificationListLayBinding;

    LinearLayoutManager linearLayoutManager;
    NotificationListAdapter notificationListAdapter;

    public static NotificationlistFrag newInstance() {
        return new NotificationlistFrag();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationListLayBinding = getViewDataBinding();
        notificationListViewModel.setNavigator(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        notificationListViewModel.notificationApi();

        getBaseActivity().HideNshowToolbar(true);

        notificationListLayBinding.toolbar.setNavigationOnClickListener(v -> {
            goBack();
        });
    }

    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(NotificationlistFrag.this)
                .commit();
    }

    @Override
    public NotificationListViewModel getViewModel() {
        return notificationListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.notification_list_lay;
    }

    @Override
    public void openNotifications(List<BaseResponse.NotificationList> notificationList) {
        if (notificationList.size() > 0) {
            notificationListViewModel.noItemFound.set(false);
            notificationListAdapter = new NotificationListAdapter(getActivity(), notificationList);
            notificationListLayBinding.notiRecycler.setLayoutManager(linearLayoutManager);
            notificationListLayBinding.notiRecycler.setAdapter(notificationListAdapter);
        } else {
            notificationListViewModel.noItemFound.set(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getBaseActivity().HideNshowToolbar(false);
    }

}
