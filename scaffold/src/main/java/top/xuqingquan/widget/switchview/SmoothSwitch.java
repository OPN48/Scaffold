package top.xuqingquan.widget.switchview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.SwitchCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import top.xuqingquan.R;
import top.xuqingquan.utils.ViewUtils;

public class SmoothSwitch extends SwitchCompat {//平滑

    public static final int SMALL = 0;
    public static final int LARGE = 1;

    @IntDef({SMALL, LARGE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }

    private Context mContext;
    @Type
    private int type = SMALL;
    public String leftString = "";
    public String rightString = "";
    @ColorInt
    public int borderColor = -1;
    @ColorInt
    public int trackColor = -1;
    @ColorInt
    public int thumbColor = -1;
    @ColorInt
    public int trackTextColor = -1;
    @Dimension
    public float borderWidth = 0;
    private SwitchTrackTextDrawable switchTrackTextDrawable;

    public SmoothSwitch(Context context) {
        super(context);
    }

    public SmoothSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setShowText(true);
        mContext = context;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SmoothSwitch);
        if (a.getString(R.styleable.SmoothSwitch_size) != null) {
            type = a.getInteger(R.styleable.SmoothSwitch_size, 0);
        }
        if (a.getString(R.styleable.SmoothSwitch_leftTrackText) != null) {
            leftString = a.getString(R.styleable.SmoothSwitch_leftTrackText);
        }
        if (a.getString(R.styleable.SmoothSwitch_rightTrackText) != null) {
            rightString = a.getString(R.styleable.SmoothSwitch_rightTrackText);
        }
        if (a.getString(R.styleable.SmoothSwitch_borderColor) != null) {
            borderColor = a.getColor(R.styleable.SmoothSwitch_borderColor, borderColor);
        }
        if (a.getString(R.styleable.SmoothSwitch_trackColor) != null) {
            trackColor = a.getColor(R.styleable.SmoothSwitch_trackColor, trackColor);
        }
        if (a.getString(R.styleable.SmoothSwitch_thumbColor) != null) {
            thumbColor = a.getColor(R.styleable.SmoothSwitch_thumbColor, thumbColor);
        }
        if (a.getString(R.styleable.SmoothSwitch_trackTextColor) != null) {
            trackTextColor = a.getColor(R.styleable.SmoothSwitch_trackTextColor, trackTextColor);
        }
        if (a.getString(R.styleable.SmoothSwitch_borderWidth) != null) {
            borderWidth = a.getDimension(R.styleable.SmoothSwitch_borderWidth, 0);
        }
        a.recycle();
        setSwitch();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setSwitch() {
        if (type == LARGE) {
            switchTrackTextDrawable = new SwitchTrackTextDrawable(leftString, rightString, (int) dp2px(15), (int) dp2px(220), (int) dp2px(38), (int) dp2px(borderWidth), (int) dp2px(19), trackColor, trackTextColor, borderColor);
            this.setTrackDrawable(switchTrackTextDrawable);
            this.setThumbDrawable(addSelector(LARGE));
        } else if (type == SMALL) {
            switchTrackTextDrawable = new SwitchTrackTextDrawable(leftString, rightString, (int) dp2px(11), (int) dp2px(162), (int) dp2px(28), (int) dp2px(borderWidth), (int) dp2px(19), trackColor, trackTextColor, borderColor);
            this.setTrackDrawable(switchTrackTextDrawable);
            this.setThumbDrawable(addSelector(SMALL));
        }
        this.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                changeBackground(true);
            } else {
                changeBackground(false);
            }
            return false;
        });

    }

    public StateListDrawable addSelector(@Type int type) {
        StateListDrawable res = new StateListDrawable();
        if (type == LARGE) {
            this.setSwitchTextAppearance(mContext, R.style.LargeText);
            res.addState(new int[]{android.R.attr.state_checked}, getResources().getDrawable(R.drawable.large_switch));
            res.addState(new int[]{-android.R.attr.state_checked}, getResources().getDrawable(R.drawable.large_switch));
        } else if (type == SMALL) {
            this.setSwitchTextAppearance(mContext, R.style.SmallText);
            res.addState(new int[]{android.R.attr.state_checked}, getResources().getDrawable(R.drawable.small_switch));
            res.addState(new int[]{-android.R.attr.state_checked}, getResources().getDrawable(R.drawable.small_switch));
        }
        res.setTint(thumbColor);
        return res;
    }

    public void changeBackground(boolean isMoving) {
        switchTrackTextDrawable.changeBackground(isMoving);
    }

    private float dp2px(float dp) {
        return ViewUtils.dp2px(getContext(), dp);
    }
}