package com.unitedbustech.eld.ifta.update;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.vo.IftaDetailVo;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.IftaLog;
import com.unitedbustech.eld.domain.entry.RequestCache;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.FileUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description IftaCreatePresenter
 */
public class IftaUpdatePresenter implements IftaUpdateContract.Presenter {

    private Context context;

    private IftaUpdateContract.View view;

    public IftaUpdatePresenter(Context context, IftaUpdateContract.View view) {

        this.context = context;
        this.view = view;

        this.view.setPresenter(this);
    }

    @Override
    public void onDestroy() {

        view = null;
    }

    @Override
    public void updateFuel(final Map<String, String> params, final List<String> receiptPics, final IftaDetailVo iftaDetailVo) {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpFileRequest.Builder builder = new HttpFileRequest.Builder();

                boolean findCoach = false;
                List<RequestCache> requestCacheList = DataBaseHelper.getDataBase()
                        .requestCacheDao()
                        .listByType(RequestType.IFTA.getCode());
                if (requestCacheList != null) {

                    for (RequestCache requestCache : requestCacheList) {

                        JSONObject jsonObject = JsonUtil.parseObject(requestCache.getParamJson());
                        if (jsonObject == null) {
                            continue;
                        }

                        int id = JsonUtil.getInt(jsonObject, "id");
                        String originId = JsonUtil.getString(jsonObject, "appRecordId");
                        if ((!TextUtils.isEmpty(originId) && originId.equals(iftaDetailVo.getAppRecordId())) || (id != 0 && id == iftaDetailVo.getId())) {

                            findCoach = true;
                            builder.url(requestCache.getUrl());
                            DataBaseHelper.getDataBase().requestCacheDao().delete(requestCache);
                            break;
                        }
                    }
                }

                if (!findCoach) {

                    builder.url(Constants.API_FUEL_UPDATE);
                }
                builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
                builder.addParam("appRecordId", iftaDetailVo.getAppRecordId());
                for (Map.Entry<String, String> entry : params.entrySet()) {

                    builder.addParam(entry.getKey(), entry.getValue());
                }

                //将图片文件迁移到离线缓存目录下，如果该目录不存在则创建，如果文件已经存在，则覆盖
                File file = new File(Constants.OFFLINE_SUPPORT_PATH);
                if (!file.exists()) {

                    file.mkdirs();
                }
                List<String> targetPicPath = new ArrayList<>();
                for (String receiptPicPath : receiptPics) {

                    String fileName = new Date().getTime() + Constants.PIC_SUFFIX;
                    String filePath = Constants.OFFLINE_SUPPORT_PATH + fileName;
                    if (FileUtil.isFileExist(filePath)) {

                        FileUtil.delFiles(filePath);
                    }

                    FileUtil.copyFile(receiptPicPath, filePath);
                    targetPicPath.add(filePath);
                }

                for (int i = 0; i < targetPicPath.size(); i++) {

                    builder.addParam("receipt_file" + (i == 0 ? "" : i), new File(targetPicPath.get(i)));
                }

                IftaLog iftaLog = DataBaseHelper.getDataBase().iftaLogDao().getByOriginId(iftaDetailVo.getAppRecordId());
                if (iftaLog == null) {

                    iftaLog = DataBaseHelper.getDataBase().iftaLogDao().getByServerId(iftaDetailVo.getId());
                }
                if (iftaLog != null) {

                    iftaLog.update(iftaDetailVo.getId(), params, targetPicPath);
                    DataBaseHelper.getDataBase().iftaLogDao().update(iftaLog);
                }

                RequestCacheService.getInstance().cacheUpload(builder.build(), RequestType.IFTA);
                view.updateFuelSuccess();
            }
        });
    }

    @Override
    public void deleteFuel(final IftaDetailVo iftaDetailVo) {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest.Builder builder = new HttpRequest.Builder();
                builder.url(Constants.API_FUEL_DELETE)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .addParam("id", Integer.toString(iftaDetailVo.getId()));

                HttpResponse response = builder.build().get();
                if (response.isSuccess() || response.getCode() == RequestStatus.IFTA_DELETED.getCode()) {

                    IftaLog iftaLog = DataBaseHelper.getDataBase().iftaLogDao().getByOriginId(iftaDetailVo.getAppRecordId());
                    if (iftaLog != null) {

                        DataBaseHelper.getDataBase().iftaLogDao().delete(iftaLog);
                    }
                    view.deleteFuelSuccess();
                } else {

                    view.deleteFuelFailed(response.getCode(), response.getMsg());
                }
            }
        });
    }
}
