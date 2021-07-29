package com.example.gpopt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final int sdkInt = Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
    }

    public void Init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            Toast.makeText(this, "android 11+ 请点击按钮，一键优化。", Toast.LENGTH_LONG).show();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Toast.makeText(this, "android 11+ 没有开启访问权限，请允许。", Toast.LENGTH_LONG).show();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "android 11- 请开启储存空间权限", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "android 11- 请点击按钮，一键优化。", Toast.LENGTH_LONG).show();
        }
    }

    public boolean CheckR() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    public void Selector(View view) {
        Toast.makeText(this, "android API" + sdkInt, Toast.LENGTH_LONG).show();
        if (!CheckR()) {
            Op1();
        } else {
            Op2();
        }
    }

    public void Op1() {
        File file = Environment.getExternalStorageDirectory();
        String path = file.getPath() + "/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android";
        file = new File(path, "EnjoyCJZC.ini");
        if (file.exists()) {
            boolean ok = file.delete();
            if (!ok) {
                Toast.makeText(this, "文件更新失败", Toast.LENGTH_LONG).show();
            }
        }
        if (!file.exists()) {
            try {
                boolean ok = file.createNewFile();
                if (!ok) {
                    Toast.makeText(this, "文件创建失败", Toast.LENGTH_LONG).show();
                }
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("[FansSwitcher]\n" +
                        "+CVars=r.PUBGMaxSupportQualityLevel=3\n" +
                        "+CVars=r.PUBGDeviceFPSLow=6\n" +
                        "+CVars=r.PUBGDeviceFPSMid=6\n" +
                        "+CVars=r.PUBGDeviceFPSHigh=6\n" +
                        "+CVars=r.PUBGDeviceFPSHDR=6\n" +
                        "+CVars=r.PUBGDeviceFPSDef=6\n" +
                        "+CVars=r.PUBGDeviceFPSUltralHigh=6\n" +
                        "+CVars=r.PUBGMSAASupport=4\n" +
                        "+CVars=r.PUBGLDR=1\n");
                fileWriter.flush();
                fileWriter.close();
                Toast.makeText(this, "优化已完成", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "权限不足或和平精英未安装并登录", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Op2() {
        File file = Environment.getExternalStorageDirectory();
        String path = file.getPath() + "/Android/data/Test";
        String uriString = fileUriUtils.changeToUri(path);
        Uri uri = Uri.parse(uriString);
        if (!fileUriUtils.isGrant(this)) {
            Grant(uriString);
        } else {
            DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
            assert documentFile != null;
            documentFile.createFile("none", "EnjoyCJZC.ini");
            Toast.makeText(this, "优化完成", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Grant(String uriString) {
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        Toast.makeText(this, uriString, Toast.LENGTH_LONG).show();
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }
        this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (data == null) {
            return;
        }
        if (requestCode == 1 && (uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
        }
    }
}