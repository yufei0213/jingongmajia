package com.interest.calculator.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yufei0213
 * @date 2018/7/3
 * @description 图片工具类
 */
public class PictureUtil {

    /**
     * 压缩图片
     *
     * @param filePath 图片路径
     */
    public static void compress(String filePath) {

        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String directory = filePath.substring(0, filePath.lastIndexOf("/") + 1);

        File tempFile = new File(directory + fileName + ".temp");
        if (tempFile.exists()) {

            tempFile.delete();
        }

        try (FileOutputStream out = new FileOutputStream(tempFile)) {

            Bitmap bm = getSmallBitmap(filePath);
            bm = toturn(bm, filePath);
            bm.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();

            //删除旧图片
            File oldFile = new File(filePath);
            oldFile.delete();

            //图片重命名
            File newFile = new File(directory + fileName);
            tempFile.renameTo(newFile);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 将图片旋转角度调整为0
     *
     * @param path 图片路径
     * @return Bitmap
     */
    public static Bitmap toturn(Bitmap img, String path) {

        Matrix matrix = new Matrix();
        matrix.postRotate(readPictureDegree(path));

        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {

        int degree = 0;
        try {

            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片压缩并转化为文件流
     *
     * @param filePath 图片路径
     * @return 文件流
     */
    public static InputStream bitmapToStream(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 60, baos);
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(
                baos.toByteArray());
        return isBm;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath 图片路径
     * @return Bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 800, 480);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options   图片信息
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @return 缩放比
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int suitedValue = reqHeight > reqWidth ? reqHeight : reqWidth;
            final int heightRatio = Math.round((float) height / (float) suitedValue);
            final int widthRatio = Math.round((float) width / (float) suitedValue);

            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;//用最大
        }

        return inSampleSize;
    }
}
