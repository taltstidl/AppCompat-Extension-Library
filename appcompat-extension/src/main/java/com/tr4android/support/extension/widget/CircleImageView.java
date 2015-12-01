package com.tr4android.support.extension.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.utils.ThemeUtils;

/**
 * A supercharged ImageView that displays images as circles and creates placeholder images
 */
public class CircleImageView extends ImageView {
    private static final String LOG_TAG = "CircleImageView";

    // whether an image or a placeholder is displayed
    private boolean mIsPlaceholder = true;

    // whether the image should be clipped to a circle
    private boolean mIsCircleImageEnabled = true;

    // whether an image drawable is being resolved
    private boolean mIsResolvingDrawable;

    // default color for circle paint
    private int mPlaceholderCircleDefaultColor;

    // paint used for drawing the colored circle
    private Paint mPlaceholderCirclePaint;

    // paint used for drawing the placeholder text
    private Paint mPlaceholderTextPaint;

    // size of the placeholder icon
    private int mPlaceholderImageSize;

    // paint used for drawing the placeholder icon
    private Drawable mPlaceholderImage;

    // placeholder text (usually the first letter of a name)
    private String mPlaceholderText = "";

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        mIsCircleImageEnabled = a.getBoolean(R.styleable.CircleImageView_circleImageEnabled, true);
        mPlaceholderCircleDefaultColor = a.getColor(R.styleable.CircleImageView_placeholderCircleColor, ThemeUtils.getThemeAttrColor(getContext(), R.attr.colorAccent));
        int mPlaceholderTextSize = a.getDimensionPixelSize(R.styleable.CircleImageView_placeholderTextSize, getResources().getDimensionPixelSize(R.dimen.defaultPlaceholderTextSize));
        int mPlaceholderTextColor = a.getColor(R.styleable.CircleImageView_placeholderTextColor, ThemeUtils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimaryInverse));
        mPlaceholderImageSize = a.getDimensionPixelSize(R.styleable.CircleImageView_placeholderIconSize, getResources().getDimensionPixelSize(R.dimen.defaultPlaceholderImageSize));
        a.recycle();

        mPlaceholderCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlaceholderCirclePaint.setColor(mPlaceholderCircleDefaultColor);
        mPlaceholderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlaceholderTextPaint.setTextAlign(Paint.Align.CENTER);
        mPlaceholderTextPaint.setTextSize(mPlaceholderTextSize);
        mPlaceholderTextPaint.setColor(mPlaceholderTextColor);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (mIsCircleImageEnabled) {
            setImageDrawable(getCircleBitmapDrawable(bm));
        } else {
            if (bm != null) mIsPlaceholder = false;
            super.setImageBitmap(bm);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (mIsCircleImageEnabled && drawable != null && !(drawable instanceof RoundedBitmapDrawable)) {
            setImageDrawable(getCircleBitmapDrawable(getBitmapFromDrawable(drawable)));
        } else {
            if (drawable != null) mIsPlaceholder = false;
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        if (mIsCircleImageEnabled) {
            mIsResolvingDrawable = true;
            super.setImageResource(resId);
            mIsResolvingDrawable = false;
            setImageDrawable(getCircleBitmapDrawable(getBitmapFromDrawable(getDrawable())));
        } else {
            if (resId != 0) mIsPlaceholder = false;
            super.setImageResource(resId);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        if (mIsCircleImageEnabled) {
            mIsResolvingDrawable = true;
            super.setImageURI(uri);
            mIsResolvingDrawable = false;
            setImageDrawable(getCircleBitmapDrawable(getBitmapFromDrawable(getDrawable())));
        } else {
            if (uri != null) mIsPlaceholder = false;
            super.setImageURI(uri);
        }
    }

    /**
     * Set whether the image should be transformed to a circle or not
     *
     * @param enabled pass true to enable, false to disable circular images
     */
    public void setCircleImageEnabled(boolean enabled) {
        mIsCircleImageEnabled = enabled;
    }

    /**
     * Check whether the image is transformed to a circle or not
     *
     * @return true if circular images are enabled, false if they are disabled
     */
    public boolean isCircleImageEnabled() {
        return mIsCircleImageEnabled;
    }

    /**
     * Set a placeholder with a text. This will use the default circle color.
     * <p/>
     * Note: This usually should be the first letter of a name. You can use the helper {@link #retrieveLetter(String)}
     * to extract the first letter of a given {@link String}.
     *
     * @param placeholderText Placeholder text to display in the middle of the circle
     */
    public void setPlaceholder(String placeholderText) {
        setPlaceholder(placeholderText, mPlaceholderCircleDefaultColor);
    }

    /**
     * Set a placeholder with a text and circle color.
     * <p/>
     * Note: This usually should be the first letter of a name. You can use the helper {@link #retrieveLetter(String)}
     * to extract the first letter of a given {@link String}.
     *
     * @param placeholderText Placeholder text to display in the middle of the circle
     * @param circleColor     Color to use for the circle
     */
    public void setPlaceholder(String placeholderText, @ColorInt int circleColor) {
        mIsPlaceholder = true;
        mPlaceholderImage = null;
        mPlaceholderText = placeholderText;
        mPlaceholderCirclePaint.setColor(circleColor);
        setImageDrawable(null);
    }

    /**
     * Set a placeholder with an image. This will use the default circle color.
     *
     * @param resId The resource id of the placeholder drawable
     */
    public void setPlaceholder(@DrawableRes int resId) {
        setPlaceholder(ContextCompat.getDrawable(getContext(), resId), mPlaceholderCircleDefaultColor);
    }

    /**
     * Set a placeholder with an image. This will use the default circle color.
     *
     * @param drawable The placeholder drawable
     */
    public void setPlaceholder(Drawable drawable) {
        setPlaceholder(drawable, mPlaceholderCircleDefaultColor);
    }

    /**
     * Set a placeholder with an image and circle color.
     *
     * @param resId The resource id of the placeholder drawable
     * @param circleColor Color to use for the circle
     */
    public void setPlaceholder(@DrawableRes int resId, @ColorInt int circleColor) {
        setPlaceholder(ContextCompat.getDrawable(getContext(), resId), circleColor);
    }

    /**
     * Set a placeholder with an image and circle color.
     *
     * @param drawable The placeholder drawable
     * @param circleColor Color to use for the circle
     */
    public void setPlaceholder(Drawable drawable, @ColorInt int circleColor) {
        mIsPlaceholder = true;
        mPlaceholderImage = drawable;
        mPlaceholderText = "";
        mPlaceholderCirclePaint.setColor(circleColor);
        setImageDrawable(null);
    }

    /**
     * Small Helper for extracting the first letter of a {@link String}.
     *
     * @param text the full text representing e.g. a name
     * @return the first letter uppercase
     */
    public static String retrieveLetter(String text) {
        return String.valueOf(text.charAt(0)).toUpperCase();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mIsResolvingDrawable) return;
        super.onDraw(canvas);
        if (mIsPlaceholder) {
            // draw placeholder circle
            float radius = Math.min(canvas.getWidth(), canvas.getHeight()) / 2;
            int xPos = (canvas.getWidth() / 2);
            canvas.drawCircle(xPos, canvas.getHeight() / 2, radius, mPlaceholderCirclePaint);
            if (mPlaceholderImage == null) {
                // draw placeholder text
                int yPos = (int) ((canvas.getHeight() / 2) -
                        ((mPlaceholderTextPaint.descent() + mPlaceholderTextPaint.ascent()) / 2));
                canvas.drawText(mPlaceholderText, xPos, yPos, mPlaceholderTextPaint);
            } else {
                // draw placeholder image
                int horizontalPadding = (canvas.getWidth() - mPlaceholderImageSize)/2;
                int verticalPadding = (canvas.getHeight() - mPlaceholderImageSize)/2;
                mPlaceholderImage.setBounds(horizontalPadding, verticalPadding, horizontalPadding + mPlaceholderImageSize, verticalPadding + mPlaceholderImageSize);
                mPlaceholderImage.draw(canvas);
            }
        }
    }

    /**
     * Internal Helper for creating a circle bitmap drawable using the {@link android.support.v4.graphics.drawable.RoundedBitmapDrawable}
     *
     * @param bitmap
     * @return the {@link android.support.v4.graphics.drawable.RoundedBitmapDrawable} containing the bitmap
     */
    private RoundedBitmapDrawable getCircleBitmapDrawable(Bitmap bitmap) {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setCornerRadius(Math.max(bitmap.getWidth() / 2, bitmap.getHeight() / 2));
        drawable.setAntiAlias(true);
        return drawable;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            Log.w(LOG_TAG, "For better performance consider using setImageBitmap() instead!");
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(Math.max(2, drawable.getIntrinsicWidth()), Math.max(2, drawable.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
}
