package com.jeanboy.app.store;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toReadDataUser(View view) {
        File filesDir = this.getFilesDir();
        File cacheDir = this.getCacheDir();

        Log.e(MainActivity.class.getSimpleName(), "-------getFilesDir---" + filesDir.getAbsolutePath());
        Log.e(MainActivity.class.getSimpleName(), "-------getCacheDir---" + cacheDir.getAbsolutePath());
    }

    public void toWriteDataUser(View view) {
        File filesDir = this.getFilesDir();
        File newFile = new File(filesDir, "a.txt");
        try {
            boolean isSucceed = newFile.createNewFile();
            if (isSucceed) {
                Log.e(MainActivity.class.getSimpleName(), "-------newFile---" + newFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toReadAndroidData(View view) {
        File test = this.getExternalFilesDir("test");
        File externalCacheDir = this.getExternalCacheDir();
        Log.e(MainActivity.class.getSimpleName(), "-------getExternalFilesDir test---" + test.getAbsolutePath());
        Log.e(MainActivity.class.getSimpleName(), "-------getExternalCacheDir---" + externalCacheDir.getAbsolutePath());
    }

    public void toWriteAndroidData(View view) {
        File test = this.getExternalFilesDir("test");
        File newFile = new File(test, "a.txt");
        try {
            boolean isSucceed = newFile.createNewFile();
            if (isSucceed) {
                Log.e(MainActivity.class.getSimpleName(), "------test-newFile---" + newFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toReadVideos(View view) {
        List<FileUtil.VideoInfo> videoList = FileUtil.toLoadVideos(this);
        for (FileUtil.VideoInfo videoInfo : videoList) {
            Log.e(MainActivity.class.getSimpleName(), videoInfo.toString());
        }
    }

    public void toInsertVideos(View view) {
        try {
            InputStream inputStream = this.getAssets().open("1.mp4");

            File file = new File(this.getCacheDir(), "1.mp4");
            boolean isSucceed = file.createNewFile();
            if (isSucceed) {
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                while ((inputStream.read(b)) != -1) {
                    outputStream.write(b);
                }
                inputStream.close();
                outputStream.close();
            }
            Log.e(MainActivity.class.getSimpleName(), "------path----" + file.getAbsolutePath());

            Uri uri = FileUtil.insertMedia(this, file);
            Log.e(MainActivity.class.getSimpleName(), "------uri----" + uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toReadImages(View view) {
        List<FileUtil.ImageInfo> imageList = FileUtil.toLoadImages(this);
        for (FileUtil.ImageInfo imageInfo : imageList) {
            Log.e(MainActivity.class.getSimpleName(), imageInfo.toString());
        }
    }

    public void toInsertImages(View view) {
        try {
            InputStream inputStream = this.getAssets().open("1.jpg");

            File file = new File(this.getCacheDir(), "1.jpg");
            boolean isSucceed = file.createNewFile();
            if (isSucceed) {
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                while ((inputStream.read(b)) != -1) {
                    outputStream.write(b);
                }
                inputStream.close();
                outputStream.close();
            }
            Log.e(MainActivity.class.getSimpleName(), "------path----" + file.getAbsolutePath());

            Uri uri = FileUtil.insertMedia(this, file);
            Log.e(MainActivity.class.getSimpleName(), "------uri----" + uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toReadAudio(View view) {
        List<FileUtil.AudioInfo> audioList = FileUtil.toLoadAudios(this);
        for (FileUtil.AudioInfo audioInfo : audioList) {
            Log.e(MainActivity.class.getSimpleName(), audioInfo.toString());
        }
    }

    public void toReadFromUri(View view) {
        List<FileUtil.ImageInfo> imageList = FileUtil.toLoadImages(this);
        for (FileUtil.ImageInfo imageInfo : imageList) {
            Uri uri = imageInfo.uri;
            if (uri != null) {
                Log.e(MainActivity.class.getSimpleName(), uri.toString());
                InputStream inputStream = FileUtil.getInputStream(MainActivity.this, uri);

                File file = new File(this.getCacheDir(), "2.jpg");
                try {
                    boolean isSucceed = file.createNewFile();
                    if (inputStream != null && isSucceed) {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        byte[] b = new byte[1024];
                        while ((inputStream.read(b)) != -1) {
                            outputStream.write(b);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                    Log.e(MainActivity.class.getSimpleName(), "------path----" + file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void toReadUriPath(View view) {
        List<FileUtil.ImageInfo> imageList = FileUtil.toLoadImages(this);
        for (FileUtil.ImageInfo imageInfo : imageList) {
            Uri uri = imageInfo.uri;
            if (uri != null) {
                Log.e(MainActivity.class.getSimpleName(), FileUtil.getPath(MainActivity.this, uri));
                return;
            }
        }
    }
}