package com.unitedbustech.eld.jsinterface;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.activity.Activity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.IftaDetailVo;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.IftaLog;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.ifta.create.IftaCreateActivity;
import com.unitedbustech.eld.ifta.update.IftaUpdateActivity;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description IFTA JS接口
 */
@JsInterface(name = "ifta")
public class IftaJsInterface extends BaseJsInterface {

    private static final String TAG = "IftaJsInterface";

    private static final String OPEN_CREATE_PAGE = "openCreatePage";
    private static final String OPEN_UPDATE_PAGE = "openUpdatePage";
    private static final String GET_FUEL_TYPE = "getFuelType";
    private static final String GET_STATE_LIST = "getStateList";
    private static final String GET_FUEL_HISTORY_LIST = "getFuelHistoryList";

    public IftaJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case OPEN_CREATE_PAGE:

                openCreatePage(data);
                break;
            case OPEN_UPDATE_PAGE:

                openUpdatePage(data);
                break;
            case GET_FUEL_TYPE:

                getFuelType(data);
                break;
            case GET_STATE_LIST:

                getStateList(data);
                break;
            case GET_FUEL_HISTORY_LIST:

                getFuelHistoryList(data);
                break;
            default:
                break;
        }
    }

    /**
     * 打开创建界面
     *
     * @param data 数据
     */
    private void openCreatePage(String data) {

        Intent intent = IftaCreateActivity.newIntent(context);
        context.startActivity(intent);
    }

    /**
     * 打开详情界面
     * <p>
     * 包含删除和编辑
     *
     * @param data 数据
     */
    private void openUpdatePage(String data) {

        IftaDetailVo iftaDetailVo = JsonUtil.parseObject(data, IftaDetailVo.class);

        Intent intent = IftaUpdateActivity.newIntent(context, iftaDetailVo);
        context.startActivity(intent);
    }

    /**
     * 获取全部燃油类型
     *
     * @param data 数据
     */
    private void getFuelType(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String content = AppUtil.readAssetsFile(context, "data/fuel-type.txt");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + content + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取全部的州列表
     *
     * @param data 数据
     */
    private void getStateList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String content = AppUtil.readAssetsFile(context, "data/state-list.txt");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + content + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取加油历史
     *
     * @param data 回调函数
     */
    private void getFuelHistoryList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                List<IftaLog> iftaLogList = DataBaseHelper.getDataBase().iftaLogDao().getByDriver(user.getDriverId());

                //获取加油记录，默认返回14天
                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_FUEL_LIST)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("start", "")
                        .addParam("end", "")
                        .build();

                final List<IftaDetailVo> result = new ArrayList<>();
                HttpResponse response = HttpRequest.get();
                if (response.isSuccess()) {

                    JSONObject object = JsonUtil.parseObject(response.getData());
                    if (object != null) {

                        String jsonArrayStr = JsonUtil.getString(object, "fuelRecords");
                        if (!TextUtils.isEmpty(jsonArrayStr)) {

                            result.addAll(JsonUtil.parseArray(jsonArrayStr, IftaDetailVo.class));
                        }
                    }
                }

                if (result.isEmpty()) {

                    if (response.isSuccess()) {

                        if (iftaLogList != null && !iftaLogList.isEmpty()) {

                            DataBaseHelper.getDataBase().iftaLogDao().delete(iftaLogList);
                        }
                    } else {

                        if (iftaLogList != null) {

                            for (IftaLog iftaLog : iftaLogList) {

                                result.add(new IftaDetailVo(iftaLog));
                            }
                        }
                    }
                } else {

                    if (iftaLogList != null && !iftaLogList.isEmpty()) {

                        DataBaseHelper.getDataBase().iftaLogDao().delete(iftaLogList);
                    }

                    List<IftaLog> newLocalHistory = new ArrayList<>();
                    for (IftaDetailVo iftaDetailVo : result) {

                        newLocalHistory.add(new IftaLog(iftaDetailVo));
                    }

                    DataBaseHelper.getDataBase().iftaLogDao().insert(newLocalHistory);
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(result) + "');");
                    }
                });
            }
        });
    }
}
