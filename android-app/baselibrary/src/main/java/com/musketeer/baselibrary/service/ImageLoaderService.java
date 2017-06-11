package com.musketeer.baselibrary.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.musketeer.baselibrary.R;
import com.musketeer.baselibrary.bean.DefaultImageLoadTask;
import com.musketeer.baselibrary.bean.ImageLoadTask;
import com.musketeer.baselibrary.tools.LruCache;
import com.musketeer.baselibrary.tools.impl.MemAndFSLruCache;
import com.musketeer.baselibrary.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongxuqi on 15-11-22.
 */
public class ImageLoaderService extends Service {
    private static final String TAG = "ImageLoaderService";
    public static final int THREAD_NUM = 2;

    private static ImageLoaderService instance;

    protected static final List<ImageLoadTask> requestList = new LinkedList<>();
    protected static ImageLoadOption defaultImageLoadOption = null;
    //用于避免URL重复加载
    protected static final Map<String, List<ImageLoadTask>> loadingMap = new HashMap<>();

    //request executor
    protected boolean isRun=false;

    protected File dir;
    protected File CacheFile;
    private LruCache<String, Bitmap> cache;
    private boolean hasNewImage = false;

    public static ImageLoaderService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dir = new File(Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name) + File.separator + "imagecache");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 初始化缓存
        cache = new MemAndFSLruCache<String, Bitmap>(Runtime.getRuntime().maxMemory() / 4, dir.getAbsolutePath()) {
            @Override
            protected Bitmap readValue(FileInputStream in) throws IOException {
                return BitmapFactory.decodeStream(in);
            }

            @Override
            protected boolean writeValue(Bitmap bitmap, FileOutputStream out) {
                return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }

            @Override
            public long sizeof(Bitmap bitmap) {
                return bitmap.getByteCount();
            }

            @Override
            protected void onSavedValueToFile(String s, Bitmap bitmap) {
                hasNewImage = true;
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (hasNewImage) {
                            saveFileRecord();
                            hasNewImage = false;
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 读SD卡中的缓存类
        CacheFile = new File(dir.getAbsolutePath() + File.separator + "cache");
        if (CacheFile.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CacheFile));
                LinkedHashMap<String, String> fileMap = (LinkedHashMap<String, String>) ois.readObject();
                ((MemAndFSLruCache<String, Bitmap>) cache).setFileMap(fileMap);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        instance = this;

        isRun=true;
        for (int i = 0; i < THREAD_NUM; i++) {
            new ImageLoadThread().start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isRun=false;
        saveFileRecord();
    }

    protected void saveFileRecord() {
        CacheFile.delete();
        try {
            CacheFile.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CacheFile));
            oos.writeObject(((MemAndFSLruCache<String, Bitmap>) cache).getFileMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadImage(@NonNull ImageView imageView, @NonNull String imageUrl) {
        loadImage(imageView, imageUrl, defaultImageLoadOption);
    }

    public static void loadImage(@NonNull ImageView imageView, @NonNull String imageUrl, ImageLoadOption imageLoadOption) {
        ImageLoadTask imageRequest = new DefaultImageLoadTask(imageView, imageUrl, imageLoadOption);
        addTask(imageRequest);
    }

    public static void addTask(@NonNull ImageLoadTask imageRequest) {
        imageRequest.preProcess();
        if (getInstance() != null) {
            imageRequest.bitmap = getInstance().cache.get(imageRequest.imageUrl);
            if (imageRequest.bitmap != null) {
                imageRequest.doOnfinish();
                return;
            }
        }
        synchronized (requestList) {
            requestList.add(imageRequest);
            requestList.notifyAll();
        }
    }

    public void setDefaultImageLoadOption(ImageLoadOption defaultOption) {
        defaultImageLoadOption = defaultOption;
    }

    private Handler handler = new Handler();

    private class ImageLoadRunnable implements Runnable {
        private final ImageLoadTask imageRequest;

        public ImageLoadRunnable(@NonNull ImageLoadTask imageRequest) {
            this.imageRequest = imageRequest;
        }

        @Override
        public void run() {
            synchronized (loadingMap) {
                if (imageRequest.bitmap != null) {
                    if (loadingMap.containsKey(imageRequest.imageUrl)) {
                        List<ImageLoadTask> list = loadingMap.get(imageRequest.imageUrl);
                        for (ImageLoadTask item : list) {
                            item.bitmap = imageRequest.bitmap;
                            item.doOnfinish();
                        }
                        loadingMap.remove(imageRequest.imageUrl);
                    } else {
                        imageRequest.doOnfinish();
                    }
                } else {
                    if (loadingMap.containsKey(imageRequest.imageUrl)) {
                        List<ImageLoadTask> list = loadingMap.get(imageRequest.imageUrl);
                        for (ImageLoadTask item : list) {
                            item.errorProcess();
                        }
                        loadingMap.remove(imageRequest.imageUrl);
                    } else {
                        imageRequest.errorProcess();
                    }
                }
            }
        }
    }

    private class ImageLoadThread extends Thread {
        @Override
        public void run() {
            while (isRun) {
                ImageLoadTask imageRequest = null;
                synchronized (requestList) {
                    if (requestList.size()>0) {
                        imageRequest = requestList.get(0);
                        requestList.remove(0);
                    }
                }
                if (imageRequest != null) {
                    doTask(imageRequest);
                } else {
                    try {
                        synchronized (requestList) {
                            requestList.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        void doTask(ImageLoadTask imageRequest) {
            imageRequest.bitmap = cache.get(imageRequest.imageUrl);
            if (imageRequest.bitmap == null) {

                //查看URL图片是否正在加载中
                synchronized (loadingMap) {
                    if (loadingMap.containsKey(imageRequest.imageUrl)) {
                        loadingMap.get(imageRequest.imageUrl).add(imageRequest);
                        return;
                    }
                    List<ImageLoadTask> list = new LinkedList<>();
                    list.add(imageRequest);
                    loadingMap.put(imageRequest.imageUrl, list);
                }

                //加载URL图片
                LogUtils.d(TAG, "Load Image: " + imageRequest.imageUrl);
                try {
                    URL url =  new URL(imageRequest.imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    imageRequest.bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    conn.getInputStream().close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imageRequest.bitmap != null) {
                    cache.put(imageRequest.imageUrl, imageRequest.bitmap);
                }
            }
            handler.post(new ImageLoadRunnable(imageRequest));
        }
    }
}
