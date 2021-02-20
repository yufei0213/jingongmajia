package com.unitedbustech.eld.util;

import android.support.annotation.NonNull;

import com.unitedbustech.eld.common.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 文件工具类
 */
public final class FileUtil {

    private static final String TAG = "FileUtil";

    private static final String LOG_ZIP_FILE_NAME = "logs.zip";

    /**
     * 读取文件内容，输出字符串
     * 默认编码 UTF-8
     *
     * @param path 文件路径
     */
    public static String readFileToString(@NonNull String path) {

        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(new File(path)));) {

            String str;
            while ((str = in.readLine()) != null) {

                sb.append(str);
            }
        } catch (Exception e) {

            e.getStackTrace();
        }

        return sb.toString();
    }

    /**
     * 重命名
     *
     * @param path    文件路径（不包含文件名）
     * @param oldName 原文件名
     * @param newName 新文件名
     */
    public static void renameFile(@NonNull String path, @NonNull String oldName, @NonNull String newName) {

        try {

            if (!oldName.equals(newName)) {

                File oldfile = new File(path + "/" + oldName);
                File newfile = new File(path + "/" + newName);
                if (!oldfile.exists()) {

                    throw new Exception("file not exit");
                }
                if (newfile.exists()) {

                    throw new Exception(path + newName + "has exit");
                } else {

                    oldfile.renameTo(newfile);
                }
            } else {

                throw new Exception("newname equals oldname");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @return File
     * @throws IOException io异常
     */
    public static File createFile(@NonNull String path, @NonNull String fileName) throws IOException {

        File file = new File(path, fileName);
        file.createNewFile();

        return file;
    }

    /**
     * 创建目录
     *
     * @param dirName 目录名
     * @return File
     */
    public static File createDir(@NonNull String dirName) {

        File dir = new File(dirName);

        try {

            if (!dir.exists()) {

                dir.mkdirs();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return dir;
    }

    /**
     * 判断文件是否存在，存在返回TRUE
     *
     * @param file 文件完整路径
     * @return 是否存在
     */
    public static boolean isFileExist(@NonNull String file) {

        boolean result = false;

        File temp = new File(file);

        try {

            result = temp.exists() ? true : false;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 判断文件夹是否为空
     *
     * @param filePath 目录地址
     * @return 是否为空
     */
    public static boolean isFolderEmpty(@NonNull String filePath) {

        boolean result = false;

        File temp = new File(filePath);

        try {

            if (temp.exists()) {

                if (temp.isDirectory()) {

                    result = temp.listFiles().length == 0 ? true : false;
                } else {

                    throw new Exception("is not folder");
                }
            } else {

                throw new Exception("file is not exit");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 清空文件夹
     *
     * @param folderPath 目录地址
     */
    public static void emptyFolder(@NonNull String folderPath) {

        try {

            File f = new File(folderPath);

            if (f.exists()) {

                if (f.isDirectory()) {

                    if (f.listFiles().length == 0) {

                        f.delete();
                    } else {

                        File[] delFile = f.listFiles();

                        int i = f.listFiles().length;

                        for (int j = 0; j < i; j++) {

                            if (delFile[j].isDirectory()) {

                                emptyFolder(delFile[j].getAbsolutePath());
                            }

                            delFile[j].delete();
                        }
                    }
                } else {

                    throw new Exception("is not folder");
                }
            } else {

                throw new Exception("file is not exit");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath 完整路径
     */
    public static void delFiles(@NonNull String filePath) {

        try {

            File f = new File(filePath);

            if (f.exists()) {

                if (f.isDirectory()) {

                    if (f.listFiles().length == 0) {

                        f.delete();
                    } else {

                        emptyFolder(filePath);
                        f.delete();
                    }
                } else {

                    f.delete();
                }
            } else {

                throw new Exception("file is not exit");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(@NonNull String oldPath, @NonNull String newPath) {

        try {

            if (new File(oldPath).exists()) {

                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);

                int byteread;
                byte[] buffer = new byte[1024 * 50];
                while ((byteread = inStream.read(buffer)) != -1) {

                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 打包日志文件。
     *
     * @param filenames 可变参数，传入需要打包的日志文件名
     * @return 返回打包后的文件名, 如果为null, 则为打包失败或者需要打包的文件不存在。
     */
    public static String zipLogs(String... filenames) {
        final int BUFFER = 2048;
        File sourceFile = new File(Constants.LOG_DEFAULT_PATH);
        String targetFileName = Constants.LOG_DEFAULT_PATH + LOG_ZIP_FILE_NAME;
        File destFile = new File(targetFileName);
        Set<String> stringSet = new HashSet<>(Arrays.asList(filenames));
        boolean hasFile = false;
        try {
            //如果该文件存在，则直接删除。
            if (destFile.exists()) {
                destFile.delete();
            }
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(targetFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            File[] fileList = sourceFile.listFiles();
            for (File file : fileList) {
                String absolutePath = file.getAbsolutePath();
                String name = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
                if (!stringSet.contains(name)) {

                    continue;
                }
                hasFile = true;
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (hasFile) {
            return targetFileName;
        } else {
            return null;
        }
    }

    /**
     * 压缩文件
     *
     * @param zipProgress 压缩进度
     * @param filenames   文件路径
     * @return 压缩包路径
     */
    public static void zipLogs(ZipProgress zipProgress, String... filenames) {

        long zipSize = 0;
        long totalSize = 0;

        final int BUFFER = 2048;
        File sourceFile = new File(Constants.LOG_DEFAULT_PATH);
        String targetFileName = Constants.LOG_DEFAULT_PATH + LOG_ZIP_FILE_NAME;
        File destFile = new File(targetFileName);
        Set<String> stringSet = new HashSet<>(Arrays.asList(filenames));
        zipProgress.onZipStart();
        boolean hasFile = false;
        try {
            //如果该文件存在，则直接删除。
            if (destFile.exists()) {
                destFile.delete();
            }
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(targetFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            File[] fileList = sourceFile.listFiles();

            for (File file : fileList) {

                String absolutePath = file.getAbsolutePath();
                String name = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
                if (!stringSet.contains(name)) {

                    continue;
                }

                totalSize += file.length();
            }

            for (File file : fileList) {
                String absolutePath = file.getAbsolutePath();
                String name = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
                if (!stringSet.contains(name)) {

                    continue;
                }
                hasFile = true;
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {

                    out.write(data, 0, count);

                    zipSize += count;
                    zipProgress.onZipProgress((int) (ConvertUtil.decimal2Point(zipSize / ((double) totalSize)) * 100));
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

        zipProgress.onZipComplete(hasFile ? targetFileName : null);
    }
}
