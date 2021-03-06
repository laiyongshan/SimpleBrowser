package com.renny.simplebrowser.business.helper;


import android.os.Environment;

import com.renny.simplebrowser.App;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.globe.helper.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * 文件存储目录
 * Created by yh on 2016/5/20.
 */
public enum Folders {
    cache("cache"),//
    temp("temp"),//临时目录
    crash("logs/crash"),//错误日志
    img("image"),//图片下载
    gallery("download", false),//图片保存
    download("download", false),//下载
    ;
    private String subFolder;
    private boolean isCache = true;
    public static File rootFolder;

    Folders(String subFolder, boolean isCache) {
        this.subFolder = subFolder;
        this.isCache = isCache;
    }

    Folders(String subFolder) {
        this.subFolder = subFolder;
    }

    /**
     * 设置文件存储根目录
     *
     * @param dir
     */
    public static void setRootFolder(File dir) {
        rootFolder = dir;
    }

    public File getRootFolder() {
        if (rootFolder == null) {
            if (!isCache) {
                rootFolder = App.getContext().getExternalFilesDir("data");//默认外部缓存
            } else {
                rootFolder = App.getContext().getExternalCacheDir();//默认外部缓存

            }
        }

        if (rootFolder == null) {
            Logs.common.e("外部存储不可用");
            if (!isCache) {
                rootFolder = App.getContext().getFilesDir();//外部存储不可用则用内部存储
            } else {
                rootFolder = App.getContext().getCacheDir();//内部存储
            }
        }
        if (rootFolder != null) {
            Logs.common.e("rootFolder:  " + rootFolder.getAbsolutePath());
        }
        return rootFolder;
    }

    public File getFolder() {
        return new File(getRootFolder(), subFolder);
    }

    public File getFile(String name) {
        return getFile(name, ".tmp");
    }

    public File getFile(String fileName, String defaultSuffix) {
        String[] nameArray = getName(fileName, defaultSuffix);
        File folder = getFolder();
        if (folder.mkdirs()) {
            return new File(folder, nameArray[0] + nameArray[1]);
        }
        return new File(folder, nameArray[0] + nameArray[1]);
    }

    public void cleanFolder() {
        FileUtil.deleteAll(getFolder());
    }

    /**
     * 取得目录
     *
     * @return
     */
    public File getSubFolder(String sub) {
        return new File(getFolder(), sub);
    }

    public File newTempFile() {
        String fileName = System.currentTimeMillis() + (int) (Math.random() * 1000) + ".temp";
        return newTempFile(fileName);
    }

    public File newTempFile(String fileName) {
        return newTempFile(fileName, ".temp");
    }

    public String[] getName(String fileName, String defaultSuffix) {
        String[] nameArray = fileName.split("\\.");
        String prefix;
        String suffix;
        if (nameArray.length == 2) {
            prefix = nameArray[0];
            suffix = "." + nameArray[1];
        } else {
            prefix = fileName;
            suffix = defaultSuffix;
        }
        return new String[]{prefix, suffix};
    }

    public File newTempFile(String fileName, String defaultSuffix) {
        String[] nameArray = getName(fileName, defaultSuffix);
        try {
            File folder = getFolder();
            if (folder.mkdirs()) {
                return File.createTempFile(nameArray[0], nameArray[1], folder);
            }
            return File.createTempFile(nameArray[0], nameArray[1], folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPath(String type) {
        String path = Environment.getExternalStoragePublicDirectory(type) + "/" + subFolder + "/";
        Logs.common.d("getPath:" + path);
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }
}
