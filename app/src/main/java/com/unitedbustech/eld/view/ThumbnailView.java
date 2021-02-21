package com.unitedbustech.eld.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.http.HttpFileDownloadCallback;
import com.unitedbustech.eld.http.HttpFileRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/6/26
 * @description 缩略图视图
 */
public class ThumbnailView extends FrameLayout implements View.OnClickListener {

    private Handler handler;
    private ImageView imageView;
    private ImageView closeBtn;

    private String imageFilePath;

    private ThumbnailViewListener listener;

    public ThumbnailView(@NonNull Context context) {
        this(context, null);
    }

    public ThumbnailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbnailView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        handler = new Handler(Looper.getMainLooper());

        LayoutInflater.from(context).inflate(R.layout.view_thumbnail, this, true);

        imageView = this.findViewById(R.id.image_view);
        closeBtn = this.findViewById(R.id.close_btn);

        imageView.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.image_view:

                if (!TextUtils.isEmpty(imageFilePath)) {

                    File imageFile = new File(imageFilePath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        Uri contentUri = FileProvider.getUriForFile(getContext(), Constants.FILE_PROVIDER, imageFile);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(contentUri, "image/*");

                    } else {

                        intent.setDataAndType(Uri.fromFile(imageFile), "image/*");
                    }

                    getContext().startActivity(intent);
                }
                break;
            case R.id.close_btn:

                if (listener != null) {

                    listener.onViewClose(this, imageFilePath);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置监听
     *
     * @param listener 监听器
     */
    public void setListener(ThumbnailViewListener listener) {

        this.listener = listener;
    }

    /**
     * 取消监听
     */
    public void cancelListener() {

        this.listener = null;
    }

    /**
     * 设置是否可编辑，即是否能够关闭
     *
     * @param isEnable 是都能够关闭
     */
    public void setEditEnable(boolean isEnable) {

        this.closeBtn.setVisibility(isEnable ? VISIBLE : GONE);
    }

    /**
     * 显示网络图片
     *
     * @param downloadUrl 图片下载链接
     */
    public void addNetworkPic(String downloadUrl) {

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumbnail_loading));
        downloadPic(downloadUrl);
    }

    /**
     * 显示图片
     *
     * @param imageFilePath 图片的存储路径
     */
    public void setImageView(String imageFilePath) {

        this.imageFilePath = imageFilePath;

        FileInputStream fis = null;
        try {
            //把图片转化为字节流
            fis = new FileInputStream(this.imageFilePath);
            //2.为图片设置100K的缓存
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            //3.设置图片颜色显示优化方式
            //ALPHA_8：每个像素占用1byte内存（8位）
            //ARGB_4444:每个像素占用2byte内存（16位）
            //ARGB_8888:每个像素占用4byte内存（32位）
            //RGB_565:每个像素占用2byte内存（16位）
            //Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4 bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            opts.inPurgeable = true;
            //5.设置图片缩放比例
            //width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张//分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为//512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
            opts.inSampleSize = 4;
            //6.设置解码图片的尺寸信息
            opts.inInputShareable = true;
            //7.把流转化图片
            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, opts);
            //8.显示图片
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } finally {

            try {

                if (fis != null) {

                    fis.close();//关闭流
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 现在网络图片并显示
     *
     * @param downloadUrl 网络图片下载地址
     */
    private void downloadPic(String downloadUrl) {

        HttpFileRequest.Builder builder = new HttpFileRequest.Builder()
                .url(downloadUrl)
                .downloadListener(new HttpFileDownloadCallback() {
                    @Override
                    public void onDownloadSuccess(final String filePath) {

                        File file = new File(filePath);
                        File newFile = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1) + new Date().getTime() + Constants.PIC_SUFFIX);
                        file.renameTo(newFile);

                        final String resultFilePath = newFile.getPath();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                ThumbnailView.this.setImageView(resultFilePath);
                                if (listener != null) {

                                    listener.onViewDownloadSuccess(resultFilePath);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumbnail_failed));
                                if (listener != null) {

                                    listener.onViewDownloadFailed();
                                }
                            }
                        });
                    }
                });
        builder.build().download();
    }

    /**
     * 相册视图监听器
     */
    public interface ThumbnailViewListener {

        /**
         * 图片被关闭
         *
         * @param v       视图
         * @param picPath 图片路径
         */
        void onViewClose(View v, String picPath);

        /**
         * 图片下载并显示成功
         *
         * @param picPath 图片的本地存储路径
         */
        void onViewDownloadSuccess(String picPath);

        /**
         * 图片下载失败
         */
        void onViewDownloadFailed();
    }
}
