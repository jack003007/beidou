package com.ty.beidou.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.ty.beidou.view.ActivityPicFolders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 一个在ImageView显示缩略图的类
 */
public class BitmapCache extends Activity {

    public Handler h = new Handler();

    public final String TAG = getClass().getSimpleName();

    private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<>();

    public void put(String path, Bitmap bmp) {
        if (!TextUtils.isEmpty(path) && bmp != null) {
            imageCache.put(path, new SoftReference<>(bmp));
        }
    }

    /**
     * 显示图片
     *
     * @param iv
     * @param thumbPath
     * @param srcPath
     * @param callback
     */
    public void displayBmp(final ImageView iv, final String thumbPath,
                           final String srcPath, final ImageCallback callback) {
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(srcPath)) {
            Log.e(TAG, "no paths pass in");
            return;
        }

        final String path;
        final boolean isThumbPath;
        if (!TextUtils.isEmpty(thumbPath)) {
            //缩略图路径存在;
            path = thumbPath;
            isThumbPath = true;
        } else if (!TextUtils.isEmpty(srcPath)) {
            //原始图路径存在
            path = srcPath;
            isThumbPath = false;
        } else {
            //路径都不存在
            // iv.setImageBitmap(null);
            return;
        }

        if (imageCache.containsKey(path)) {
            SoftReference<Bitmap> reference = imageCache.get(path);
            Bitmap bmp = reference.get();
            if (bmp != null) {
                if (callback != null) {
                    callback.imageLoad(iv, bmp, srcPath);
                }
                iv.setImageBitmap(bmp);
                Log.d(TAG, "hit cache");
                return;
            }
        }
        iv.setImageBitmap(null);

        new Thread() {

            Bitmap thumbBitmap;

            public void run() {
                //根据路径生成Bitmap对象
                try {
                    if (isThumbPath) {
                        //只有缩略图路径时
                        thumbBitmap = BitmapFactory.decodeFile(thumbPath);
                        if (thumbBitmap == null) {
                            thumbBitmap = reviseImageSize(srcPath);
                        }
                    } else {
                        thumbBitmap = reviseImageSize(srcPath);
                    }
                } catch (Exception e) {

                }
                if (thumbBitmap == null) {
                    //默认图像
                    thumbBitmap = ActivityPicFolders.mBitmap;
                }
                put(path, thumbBitmap);

                Log.e(TAG, "-------thumbBitmap------" + thumbBitmap);

                if (callback != null) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.imageLoad(iv, thumbBitmap, srcPath);
                        }
                    });
                }
            }
        }.start();

    }

    /**
     * 图像缩放 小于256x256
     *
     * @param path
     * @return
     * @throws IOException
     */
    public Bitmap reviseImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        //防止OOM
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 256)
                    && (options.outHeight >> i <= 256)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    public interface ImageCallback {
        void imageLoad(ImageView imageView, Bitmap bitmap,
                       Object... params);
    }
}
