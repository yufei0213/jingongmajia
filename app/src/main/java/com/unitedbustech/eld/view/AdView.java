package com.unitedbustech.eld.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.http.HttpFileDownloadCallback;
import com.unitedbustech.eld.http.HttpFileRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description DaillLogDetail 顶部菜单选项
 */
public class AdView extends RelativeLayout implements View.OnClickListener {

    private static final int COUNT = 5;
    private int count;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            count--;
            button.setText("Skip(" + count + "s)");
            if (count == 0) {
                adViewListener.onAdSkip();
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private String adUrl;
    private AdViewListener adViewListener;

    private RelativeLayout containerView;
    private ImageView adView;
    private FontButton button;

    public AdView(Context context) {

        this(context, null);
    }

    public AdView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_ad, this, true);

        containerView = this.findViewById(R.id.container);
        adView = this.findViewById(R.id.ad_img);
        button = this.findViewById(R.id.negative_btn);

        containerView.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.container:
                adViewListener.onAdClick(adUrl);
                break;
            case R.id.negative_btn:
                adViewListener.onAdSkip();
                break;
            default:
                break;
        }
    }

    public void setUrl(String adImgUrl, String adUrl) {
        this.adUrl = adUrl;
        HttpFileRequest.Builder builder = new HttpFileRequest.Builder()
                .url(adImgUrl)
                .downloadListener(new HttpFileDownloadCallback() {
                    @Override
                    public void onDownloadSuccess(final String filePath) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                FileInputStream fis = null;
                                try {
                                    //把图片转化为字节流
                                    fis = new FileInputStream(filePath);
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
                                    adView.setImageBitmap(bitmap);
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

                                count = COUNT;
                                handler.postDelayed(runnable, 1000);
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {
                        adViewListener.onAdSkip();
                    }
                });
        builder.build().download();
    }

    public void setAdViewListener(AdViewListener adViewListener) {
        this.adViewListener = adViewListener;
    }
}
