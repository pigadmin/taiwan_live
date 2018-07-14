package com.live.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ApkUpdate {

    private Context context;
    private String packageName;
    private String apkurl;
    private String savePath;

    private DownLoadFileThread downLoadFileThread;
    private Handler handler;
    private String filename;

    public ApkUpdate(Context context, String apkurl) {
        this.context = context;

        File dir = context.getDir("tv", Context.MODE_PRIVATE
                | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        dir.mkdirs();
        this.packageName = context.getPackageName();
        this.apkurl = apkurl;
        this.savePath = dir.getAbsolutePath() + "/";
        handler = new Handler();
        filename = "tv.apk";
    }

    private ProgressDialog pBar;

    public void downloadAndInstall() {
        pBar = new ProgressDialog(context);
        pBar.setTitle("自动更新中...");
        pBar.setMessage("检测到程序有更新，正在自动下载更新...");
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setCancelable(false);


        pBar.setProgress(100);
        pBar.show();
        downLoadFileThread = new DownLoadFileThread(apkurl, savePath
                + this.filename, new DownloadCallback() {

            @Override
            public void onSuccess() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pBar.cancel();
                        System.out.println("下载成功");
                        getFilePermission(savePath + filename);
                        install(savePath + filename);
                    }
                });
            }

            @Override
            public void onPrecessing(int written, long total) {
                int c = (int) ((long) written * 100 / total);
                pBar.setProgress(c);
            }

            @Override
            public void onFail(Throwable e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                pBar.cancel();
            }

            @Override
            public void onCancel() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
        downLoadFileThread.start();
    }

    public static void install(final String path) {
        // TODO Auto-generated method stub
        System.out.println("install :" + path);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {

                    String cmd = "pm install -r " + path;

                    System.out.println(cmd);

                    Process process = Runtime.getRuntime().exec(cmd);
                    // process.wait();

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void install2(String fullfilepath) {
        getFilePermission(fullfilepath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + fullfilepath),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private void getFilePermission(String file) {
        ShellExecute ex = new ShellExecute();
        String[] cmd = {"chmod", "607", file};
        try {
            ex.execute(cmd, "/");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            install2(file);
        }
    }

    class DownLoadFileThread extends Thread {

        private String url;
        // 文件保存路径
        private String fullFilename;
        private DownloadCallback callback;

        public DownLoadFileThread(String url, String fullFilename,
                                  DownloadCallback callback) {
            this.url = url;
            this.fullFilename = fullFilename;
            this.callback = callback;
        }

        public void run() {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(this.url);
            HttpResponse response;

            FileOutputStream outStream = null;
            try {
                response = client.execute(get);

                HttpEntity entity = response.getEntity();
                long length = entity.getContentLength();
                InputStream is = entity.getContent();
                File f = new File(fullFilename);
                f.getParentFile().mkdirs();
                if (is != null) {
                    outStream = new FileOutputStream(new File(fullFilename));

                    byte[] buf = new byte[1024];
                    int ch = -1;
                    int count = 0;
                    while ((ch = is.read(buf)) != -1) {
                        if (this.isInterrupted()) {
                            callback.onCancel();
                            return;
                        }
                        outStream.write(buf, 0, ch);
                        count += ch;
                        if (count != 0) {
                            callback.onPrecessing(count, length);
                        }
                    }
                }
                outStream.flush();
                callback.onSuccess();
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFail(e);
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    interface DownloadCallback {

        public void onSuccess();

        public void onPrecessing(int written, long total);

        public void onFail(Throwable e);

        public void onCancel();
    }

    private class ShellExecute {
        public String execute(String[] cmmand, String directory)
                throws IOException {
            String result = "";
            try {
                ProcessBuilder builder = new ProcessBuilder(cmmand);
                if (directory != null)
                    builder.directory(new File(directory));
                builder.redirectErrorStream(true);
                Process process = builder.start();
                InputStream is = process.getInputStream();
                byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    result = result + new String(buffer);
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
