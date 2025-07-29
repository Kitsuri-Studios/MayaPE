package org.levimc.launcher.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.levimc.launcher.R;
import org.levimc.launcher.core.versions.VersionManager;
import org.levimc.launcher.ui.dialogs.ApkVersionConfirmDialog;
import org.levimc.launcher.ui.dialogs.CustomAlertDialog;
import org.levimc.launcher.ui.dialogs.InstallProgressDialog;
import org.levimc.launcher.ui.views.MainViewModel;

import java.util.concurrent.Executors;

public class ApkImportManager {
    private final Activity activity;
    private final MainViewModel viewModel;
    private final InstallProgressDialog progressDialog;

    public ApkImportManager(Activity activity, MainViewModel viewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
        this.progressDialog = new InstallProgressDialog(activity);
    }

    public void handleApkImportResult(Intent data) {
        Uri apkUri = data.getData();
        if (apkUri == null) return;
        String initialVersionName = ApkUtils.extractMinecraftVersionNameFromUri(activity, apkUri);
        if ("Error Apk".equals(initialVersionName)) {
            new CustomAlertDialog(activity)
                    .setTitleText(activity.getString(R.string.illegal_apk_title))
                    .setMessage(activity.getString(R.string.not_mc_apk))
                    .setPositiveButton(activity.getString(R.string.exit), v -> {
                    })
                    .show();
            return;
        }
        ApkVersionConfirmDialog dialog = new ApkVersionConfirmDialog()
                .setInitialVersionName(initialVersionName)
                .setCallback(new ApkVersionConfirmDialog.Callback() {
                    @Override
                    public void onInstallClicked(String versionName) {
                        showProgress();
                        ApkInstaller installer = new ApkInstaller(activity, Executors.newSingleThreadExecutor(), new ApkInstaller.InstallCallback() {
                            @Override
                            public void onProgress(int progress) {

                            }

                            @Override
                            public void onSuccess(String versionName) {
                                activity.runOnUiThread(() -> {
                                    dismissProgress();
                                    Toast.makeText(
                                            activity,
                                            activity.getString(R.string.install_done, versionName),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    VersionManager.get(activity).loadAllVersions();
                                });
                            }

                            @Override
                            public void onError(String errorMsg) {
                                activity.runOnUiThread(() -> {
                                    dismissProgress();
                                    Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                                });
                            }
                        });
                        installer.install(apkUri, versionName);
                    }

                    @Override
                    public void onCancelled() {
                    }
                });
        dialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "ApkVersionConfirmDialog");
    }

    void showProgress() {
        //progressDialog.setProgress(0);
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    void dismissProgress() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    public void handleActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            handleApkImportResult(data);
        }
    }

}