package com.unitedbustech.eld.prepare;

import android.content.Context;

import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.ThreadUtil;

/**
 * @author yufei0213
 * @date 2018/1/28
 * @description PreparePresenter
 */
public class PreparePresenter implements PrepareContract.Presenter {

    private Context context;

    private PrepareContract.View view;

    public PreparePresenter(Context context, PrepareContract.View view) {

        this.context = context;
        this.view = view;

        this.view.setPresenter(this);
    }

    @Override
    public void onDestroy() {

        this.view.endLoading();
        this.view = null;
    }

    @Override
    public void getUserInfo() {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int carrierId = SystemHelper.getUser().getCarriedId();
                Carrier carrier = DataBaseHelper.getDataBase().carrierDao().getCarrier(carrierId);

                int driverId = SystemHelper.getUser().getDriverId();
                Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(driverId);

                if (view != null) {

                    view.setUserInfo(carrier.getName(), driver.getName());
                }
            }
        });
    }

    @Override
    public void loadData() {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                ModelCenter.getInstance().init(new ModelCenter.InitOverCallback() {
                    @Override
                    public void initDataOver() {

                        if (view != null) {

                            view.loadDataFinish(true);
                        }
                    }

                    @Override
                    public void initDataFailure() {

                        if (view != null) {

                            view.loadDataFinish(false);
                        }
                    }
                });
            }
        });
    }
}
