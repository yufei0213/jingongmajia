package com.unitedbustech.eld.ifta.create;

import android.content.Context;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.IftaLog;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.FileUtil;
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
public class IftaCreatePresenter implements IftaCreateContract.Presenter {

    private Context context;

    private IftaCreateContract.View view;

    public IftaCreatePresenter(Context context, IftaCreateContract.View view) {

        this.context = context;
        this.view = view;

        this.view.setPresenter(this);
    }

    @Override
    public void onDestroy() {

        view = null;
    }

    @Override
    public void createFuel(final Map<String, String> params, final List<String> receiptPics) {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpFileRequest.Builder builder = new HttpFileRequest.Builder();
                builder.url(Constants.API_FUEL_CREATE)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken());
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

                IftaLog iftaLog = new IftaLog(params, targetPicPath);
                DataBaseHelper.getDataBase().iftaLogDao().insert(iftaLog);
                builder.addParam("appRecordId", iftaLog.getOriginId());

                RequestCacheService.getInstance().cacheUpload(builder.build(), RequestType.IFTA);
                view.createFuelSuccess();
            }
        });
    }
}
