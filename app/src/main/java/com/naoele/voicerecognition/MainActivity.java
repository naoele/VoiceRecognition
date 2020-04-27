package com.naoele.voicerecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.naoele.voicerecognition.Dialogs.ErrorDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_ERROR_CODE = 9000;
    private static final int REQUEST_PERMISSION = 3000;

    private TextView _textView;

    // 言語
    private int _lang = 0;

    private String[] _permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String ERR_DIALOG = "err";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            // permissionチェック。これをいれないと6.0以上のOSではダイアログが表示されない
            checkPermission(_permissions);
        }

        _textView = findViewById(R.id.txv_display);
        Button buttonStart = findViewById(R.id.btn_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 音声認識を開始
                speech();
            }
        });
    }

    private void speech() {

        // 音声認識の　Intent インスタンス
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if (_lang == 0) {
            // 日本語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
        } else if (_lang == 1) {
            // 英語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString());
        } else if (_lang == 2) {
            // Off line mode
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }

        // 録音
        intent.putExtra("android.speech.extra.GET_AUDIO", true);
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.PROMPT", "マイクに向かって話してください。");
        intent.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            // 認識結果を ArrayList で取得
            ArrayList<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (candidates != null && candidates.size() > 0) {
                // 認識結果候補で一番有力なものを表示
                _textView.setText(candidates.get(0));
            }

        }
    }

    /**
     * パーミッションの許可をチェック
     */
    public void checkPermission(String[] permissions) {
        boolean isPermissionDenied = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionDenied = false;
            }
        }
        if (!isPermissionDenied) {
            // 拒否していた場合
            requestPermissions(permissions);
        }
    }

    /**
     * パーミッションの許可を求める
     */
    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {

            boolean isShowPermissionRequestDialog = true;
            List<String> deniedPermissions = new ArrayList<String>();

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        // 以前パーミッションリクエストを拒否した場合もう一度
                        deniedPermissions.add(permissions[i]);
                    } else {
                        // 拒否してかつパーミッションリクエストダイアログを今後表示しないを選択した場合
                        isShowPermissionRequestDialog = false;
                        deniedPermissions.add(permissions[i]);
                    }
                }
            }
            if (isShowPermissionRequestDialog) {
                if (deniedPermissions.size() > 0) {
                    String[] ary = new String[deniedPermissions.size()];
                    checkPermission(deniedPermissions.toArray(ary));
                }
            } else {
                ErrorDialog.show("アプリは正常に動作しないので\n設定画面から権限を許可してください。", getSupportFragmentManager(), ERR_DIALOG);
//                Toast.makeText(this, "アプリは正常に動作しないので\n設定画面から権限を許可してください。", Toast.LENGTH_LONG).show();
            }
        }
    }

}
