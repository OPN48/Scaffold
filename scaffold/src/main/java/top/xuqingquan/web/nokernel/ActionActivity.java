package top.xuqingquan.web.nokernel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import top.xuqingquan.utils.Timber;
import top.xuqingquan.web.publics.AgentWebUtils;

import java.io.File;
import java.util.List;

public final class ActionActivity extends Activity {

    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String KEY_URI = "KEY_URI";
    public static final String KEY_FROM_INTENTION = "KEY_FROM_INTENTION";
    public static final String KEY_FILE_CHOOSER_INTENT = "KEY_FILE_CHOOSER_INTENT";
    private static RationaleListener mRationaleListener;
    private static PermissionListener mPermissionListener;
    private static ChooserListener mChooserListener;
    private Action mAction;
    public static final int REQUEST_CODE = 0x254;

    public static void start(Activity activity, Action action) {
        Intent mIntent = new Intent(activity, ActionActivity.class);
        mIntent.putExtra(KEY_ACTION, action);
        activity.startActivity(mIntent);
    }

    public static void setChooserListener(ChooserListener chooserListener) {
        mChooserListener = chooserListener;
    }

    public static void setPermissionListener(PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
    }

    private void cancelAction() {
        mChooserListener = null;
        mPermissionListener = null;
        mRationaleListener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Timber.i("savedInstanceState:" + savedInstanceState);
            return;
        }
        Intent intent = getIntent();
        mAction = intent.getParcelableExtra(KEY_ACTION);
        if (mAction == null) {
            cancelAction();
            this.finish();
            return;
        }
        if (mAction.getAction() == Action.ACTION_PERMISSION) {
            permission(mAction);
        } else if (mAction.getAction() == Action.ACTION_CAMERA) {
            realOpenCamera();
        } else {
            fetchFile();
        }
    }

    private void fetchFile() {
        if (mChooserListener == null) {
            finish();
        }
        realOpenFileChooser();
    }

    private void realOpenFileChooser() {
        try {
            if (mChooserListener == null) {
                finish();
                return;
            }
            Intent mIntent = getIntent().getParcelableExtra(KEY_FILE_CHOOSER_INTENT);
            if (mIntent == null) {
                cancelAction();
                return;
            }
            this.startActivityForResult(mIntent, REQUEST_CODE);
        } catch (Throwable throwable) {
            Timber.i("找不到文件选择器");
            chooserActionCallback(-1, null);
            Timber.e(throwable);
        }
    }

    private void chooserActionCallback(int resultCode, Intent data) {
        if (mChooserListener != null) {
            mChooserListener.onChoiceResult(REQUEST_CODE, resultCode, data);
            mChooserListener = null;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            chooserActionCallback(resultCode, mUri != null ? new Intent().putExtra(KEY_URI, mUri) : data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permission(Action action) {
        List<String> permissions = action.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            mPermissionListener = null;
            mRationaleListener = null;
            finish();
            return;
        }
        if (mRationaleListener != null) {
            boolean rationale = false;
            for (String permission : permissions) {
                rationale = shouldShowRequestPermissionRationale(permission);
                if (rationale) {
                    break;
                }
            }
            mRationaleListener.onRationaleResult(rationale, new Bundle());
            mRationaleListener = null;
            finish();
            return;
        }
        if (mPermissionListener != null) {
            requestPermissions(permissions.toArray(new String[]{}), 1);
        }
    }

    private Uri mUri;

    private void realOpenCamera() {
        try {
            if (mChooserListener == null) {
                finish();
            }
            File mFile = WebUtils.createImageFile(this);
            if (mFile == null) {
                mChooserListener.onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
                mChooserListener = null;
                finish();
            }
            Intent intent = WebUtils.getIntentCaptureCompat(this, mFile);
            // 指定开启系统相机的Action
            mUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            this.startActivityForResult(intent, REQUEST_CODE);
        } catch (Throwable t) {
            Timber.i("找不到系统相机");
            if (mChooserListener != null) {
                mChooserListener.onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
            }
            mChooserListener = null;
            Timber.e(t);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionListener != null) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_FROM_INTENTION, mAction.getFromIntention());
            mPermissionListener.onRequestPermissionsResult(permissions, grantResults, mBundle);
        }
        mPermissionListener = null;
        finish();
    }

    public interface RationaleListener {
        void onRationaleResult(boolean showRationale, Bundle extras);
    }

    public interface PermissionListener {
        void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras);
    }

    public interface ChooserListener {
        void onChoiceResult(int requestCode, int resultCode, Intent data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
