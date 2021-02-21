package com.unitedbustech.eld.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.logs.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/6/26
 * @description 缩略图容器视图
 */
public class ThumbnailContainerView extends LinearLayout implements View.OnClickListener, ThumbnailView.ThumbnailViewListener {

    private static final String TAG = "ThumbnailContainerView";

    /**
     * 能够添加的最大图片数
     */
    private static final int MAX_PIC_COUNT = 3;

    private Context context;

    private LinearLayout imageContainer;
    private ImageView addImageBtn;

    /**
     * 是否可以编辑
     */
    private boolean isEditUnable;

    private List<ThumbnailView> thumbnailViewList;

    /**
     * 图片文件路径数据
     */
    private List<String> picPathArray;

    /**
     * 监听器
     */
    private OnAddImageListener listener;

    public ThumbnailContainerView(Context context) {
        this(context, null);
    }

    public ThumbnailContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbnailContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_thumbnail_container, this, true);

        this.imageContainer = this.findViewById(R.id.image_container);
        this.addImageBtn = this.findViewById(R.id.add_btn);

        this.addImageBtn.setOnClickListener(this);

        this.context = context;
        this.picPathArray = new ArrayList<>();
        this.thumbnailViewList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.add_btn:

                if (listener != null) {

                    listener.onAddImage();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewClose(View v, String picPath) {

        ((ThumbnailView) v).cancelListener();
        imageContainer.removeView(v);
        for (String item : picPathArray) {

            if (item.equals(picPath)) {

                picPathArray.remove(item);
                this.thumbnailViewList.remove(v);
                break;
            }
        }

        checkAddBtn();
    }

    @Override
    public void onViewDownloadSuccess(String picPath) {

        picPathArray.add(picPath);
        checkAddBtn();
    }

    @Override
    public void onViewDownloadFailed() {

    }

    /**
     * 添加监听
     *
     * @param listener 监听器
     */
    public void setListener(OnAddImageListener listener) {

        this.listener = listener;
    }

    /**
     * 设置当前是否可以编辑
     *
     * @param editable 是否能够编辑
     */
    public void setEditable(boolean editable) {

        this.isEditUnable = !editable;
        checkAddBtn();

        for (ThumbnailView thumbnailView : thumbnailViewList) {

            thumbnailView.setEditEnable(!this.isEditUnable);
        }
    }

    /**
     * 新加图片
     *
     * @param picPath 图片路径
     */
    public void addPic(String picPath) {

        if (picPathArray.size() >= MAX_PIC_COUNT) {

            Logger.w(TAG, "pic size can't more than 3.");
            return;
        }

        picPathArray.add(picPath);
        checkAddBtn();

        ThumbnailView thumbnailView = new ThumbnailView(context);
        thumbnailView.setListener(this);
        thumbnailView.setImageView(picPath);

        this.thumbnailViewList.add(thumbnailView);

        imageContainer.addView(thumbnailView);
    }

    /**
     * 显示图片
     *
     * @param isEditable 是否可以编辑
     * @param picPaths   网络图片下载链接
     */
    public void addPics(boolean isEditable, String... picPaths) {

        this.isEditUnable = !isEditable;
        for (String picPath : picPaths) {

            ThumbnailView thumbnailView = new ThumbnailView(context);
            thumbnailView.setListener(this);
            thumbnailView.setEditEnable(!this.isEditUnable);

            if (picPath.startsWith("http") || picPath.startsWith("https")) {

                thumbnailView.addNetworkPic(picPath);
            } else {

                thumbnailView.setImageView(picPath);
                picPathArray.add(picPath);
            }

            this.thumbnailViewList.add(thumbnailView);

            imageContainer.addView(thumbnailView);
        }

        checkAddBtn();
    }

    /**
     * 获取当前显示的图片的本地路径
     *
     * @return 图片存储路径
     */
    public List<String> getPicPath() {

        if (picPathArray == null) {

            picPathArray = new ArrayList<>();
        }

        return picPathArray;
    }

    /**
     * 检查添加按钮是否需要显示
     */
    private void checkAddBtn() {

        if (!this.isEditUnable && picPathArray.size() < MAX_PIC_COUNT) {

            addImageBtn.setVisibility(VISIBLE);
        } else {

            addImageBtn.setVisibility(GONE);
        }
    }

    /**
     * 添加图片按钮被点击
     */
    public interface OnAddImageListener {

        void onAddImage();
    }
}
