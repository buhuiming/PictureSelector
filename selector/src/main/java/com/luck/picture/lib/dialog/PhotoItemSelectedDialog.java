package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.immersive.ImmersiveManager;
import com.luck.picture.lib.interfaces.OnItemClickListener;
import com.luck.picture.lib.utils.DensityUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author：luck
 * @date：2019-12-12 16:39
 * @describe：PhotoSelectedDialog
 */
public class PhotoItemSelectedDialog extends DialogFragment implements View.OnClickListener {
    public static final int IMAGE_CAMERA = 0;
    public static final int VIDEO_CAMERA = 1;
    private boolean isCancel = true;

    public static PhotoItemSelectedDialog newInstance() {
        return new PhotoItemSelectedDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
        return inflater.inflate(R.layout.ps_dialog_camera_selected, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvPicturePhoto = view.findViewById(R.id.ps_tv_photo);
        TextView tvPictureVideo = view.findViewById(R.id.ps_tv_video);
        TextView tvPictureCancel = view.findViewById(R.id.ps_tv_cancel);
        tvPictureVideo.setOnClickListener(this);
        tvPicturePhoto.setOnClickListener(this);
        tvPictureCancel.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            ImmersiveManager.navigationBarPadding(view);
        }
        view.setClipToOutline(true);
        view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialogStyle();
    }

    /**
     * DialogFragment Style
     */
    private void initDialogStyle() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(DensityUtil.getRealScreenWidth(getContext()), RelativeLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.PictureThemeDialogFragmentAnim);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.PictureThemeDialogFragmentAnimTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        try {
            //noinspection deprecation
            dialog.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );

            dialog.getWindow().setNavigationBarColor(Color.WHITE);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } catch (Exception e) {
            Log.e("PhotoDialog", Log.getStackTraceString(e));
        }

        return dialog;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    public interface OnDismissListener {
        void onDismiss(boolean isCancel, DialogInterface dialog);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (onItemClickListener != null) {
            if (id == R.id.ps_tv_photo) {
                onItemClickListener.onItemClick(v, IMAGE_CAMERA);
                isCancel = false;
            } else if (id == R.id.ps_tv_video) {
                onItemClickListener.onItemClick(v, VIDEO_CAMERA);
                isCancel = false;
            }
        }
        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(isCancel, dialog);
        }
    }
}
