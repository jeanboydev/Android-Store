package com.jeanboy.app.store;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeanboy on 2020/04/24 11:46.
 */
public class FileUtil {

    public static class VideoInfo {
        public long id;
        public Uri uri;
        public String displayName;
        public String title;
        public String mimeType;
        public String artist;
        public String album;
        public Bitmap thumbnail;
        public int width;
        public int height;
        public long duration;
        public long dateAdded;
        public long size;
    }

    public static class AudioInfo {
        public long id;
        public Uri uri;
        public String displayName;
        public String title;
        public String mimeType;
        public String artist;
        public String album;
        public int width;
        public int height;
        public long duration;
        public long dateAdded;
        public long size;
    }

    public static class ImageInfo {
        public long id;
        public Uri uri;
        public String displayName;
        public String title;
        public String mimeType;
        public int width;
        public int height;
        public long dateAdded;
        public long size;
    }


    public static List<VideoInfo> toLoadVideos(Context context) {
        List<VideoInfo> dataList = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.ALBUM,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Video.Media.DURATION : MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE
        };

//        String selection = MediaStore.Video.Media.DURATION + " >= ?";
//        String[] selectionArgs = new String[]{
//                String.valueOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MILLISECONDS))
//        };

        String selection = null;
        String[] selectionArgs = null;

        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        try (Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor == null) return dataList;
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM);
            int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
            int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
            int durationColumn = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            }
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                VideoInfo info = new VideoInfo();
                info.id = cursor.getLong(idColumn);
                info.uri = getVideoUri(info.id);
                info.displayName = cursor.getString(displayNameColumn);
                info.title = cursor.getString(titleColumn);
                info.mimeType = cursor.getString(mimeTypeColumn);
                info.artist = cursor.getString(artistColumn);
                info.album = cursor.getString(albumColumn);
                info.thumbnail = getVideoThumbnail(contentResolver, info.id);
                info.width = cursor.getInt(widthColumn);
                info.height = cursor.getInt(heightColumn);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.duration = cursor.getLong(durationColumn);
                }
                info.dateAdded = cursor.getLong(dateAddedColumn);
                info.size = cursor.getLong(sizeColumn);
                dataList.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static List<ImageInfo> toLoadImages(Context context) {
        List<ImageInfo> dataList = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.SIZE
        };

        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " ASC";

        try (Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            if (cursor == null) return dataList;
            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int dateAddedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int mimeTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
            int widthColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
            int heightColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
            int sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            while (cursor.moveToNext()) {
                ImageInfo info = new ImageInfo();
                info.id = cursor.getLong(idColumnIndex);
                info.uri = getImageUri(info.id);
                info.displayName = cursor.getString(displayNameColumnIndex);
                info.title = cursor.getString(titleColumnIndex);
                info.mimeType = cursor.getString(mimeTypeColumnIndex);
                info.width = cursor.getInt(widthColumnIndex);
                info.height = cursor.getInt(heightColumnIndex);
                info.dateAdded = cursor.getLong(dateAddedColumnIndex);
                info.size = cursor.getLong(sizeColumnIndex);
                dataList.add(info);
            }
        }

        return dataList;
    }

    public static List<AudioInfo> toLoadAudios(Context context) {
        List<AudioInfo> dataList = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Audio.Media.DURATION : MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.WIDTH,
                MediaStore.Audio.Media.HEIGHT,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
        };

//        String selection = MediaStore.Video.Media.DURATION + " >= ?";
//        String[] selectionArgs = new String[]{
//                String.valueOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MILLISECONDS))
//        };

        String selection = null;
        String[] selectionArgs = null;

        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor == null) return dataList;
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.WIDTH);
            int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.HEIGHT);
            int durationColumn = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            }
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            while (cursor.moveToNext()) {
                AudioInfo info = new AudioInfo();
                info.id = cursor.getLong(idColumn);
                info.uri = getVideoUri(info.id);
                info.displayName = cursor.getString(displayNameColumn);
                info.title = cursor.getString(titleColumn);
                info.mimeType = cursor.getString(mimeTypeColumn);
                info.artist = cursor.getString(artistColumn);
                info.album = cursor.getString(albumColumn);
                info.width = cursor.getInt(widthColumn);
                info.height = cursor.getInt(heightColumn);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.duration = cursor.getLong(durationColumn);
                }
                info.dateAdded = cursor.getLong(dateAddedColumn);
                info.size = cursor.getLong(sizeColumn);
                dataList.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static Bitmap getVideoThumbnail(ContentResolver contentResolver, long id) {
        return MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
    }

    public static Uri getVideoUri(long id) {
        return ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
    }

    public static Uri getImageUri(long id) {
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
    }

    public static Uri getAudioUri(long id) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    }

    public static Uri insertMedia(Context context, File file) {
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        String fileName = file.getName();
        String mimeType = getMimeType(fileName);
        long totalSpace = file.getTotalSpace();
        Uri uri = null;
        if (mimeType.contains("image")) {
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.TITLE, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.Images.Media.SIZE, totalSpace);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            }
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else if (mimeType.contains("video")) {
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Video.Media.TITLE, fileName);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, mimeType);
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.SIZE, totalSpace);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
                contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            }
            uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else if (mimeType.contains("audio")) {
            contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Audio.Media.TITLE, fileName);
            contentValues.put(MediaStore.Audio.Media.MIME_TYPE, mimeType);
            contentValues.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.Audio.Media.SIZE, totalSpace);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
                contentValues.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());
            }
            uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
        }

        BufferedInputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            if (uri != null) {
                outputStream = contentResolver.openOutputStream(uri);
            }
            if (outputStream != null) {
                byte[] buffer = new byte[1024];
                while (inputStream.read(buffer) != -1) {
                    outputStream.write(buffer);
                }
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uri;
    }

    public static String getMimeType(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static Bitmap getBitmap(Context context, Uri uri) {
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        try {
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");// r:read; w:write
            if (parcelFileDescriptor != null) {
                return BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getInputStream(Context context, Uri uri) {
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        try {
            return contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPath(Context context, Uri uri) {
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            return uri.getPath();
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // 以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String column = "_data";
        String[] projection = {column};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
