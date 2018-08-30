package com.start.download;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity  implements EasyPermissions.PermissionCallbacks{

    public final static int PERMMISSION_WRITE_READ_EXTERNAL_STORAGE = 1000;
    public final static String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    private Context context;

    @BindView(R.id.textview) TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        if (!EasyPermissions.hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMMISSION_WRITE_READ_EXTERNAL_STORAGE);
        }else {
            startDownload();
        }
    }

    public void startDownload() {
        final MutiThreadDownloadTask task = new MutiThreadDownloadTask("http://116.62.9.17:8080/examples/2.mp4", Environment.getExternalStorageDirectory() + File.separator + "2.mp4");
        task.setOnProgressUpdateListener(new MutiThreadDownloadTask.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(float progress, boolean isCompleted) {
                textView.setText((int)progress+"%");
                if(isCompleted) {
                    textView.append("下载完成\n");
                    textView.append("文件路径："+task.filePath);
                }
            }
        });
        task.startDownload();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PERMMISSION_WRITE_READ_EXTERNAL_STORAGE:
                startDownload();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PERMMISSION_WRITE_READ_EXTERNAL_STORAGE:
                EasyPermissions.requestPermissions(this, "需要读取文件权限", PERMMISSION_WRITE_READ_EXTERNAL_STORAGE, permissions);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
