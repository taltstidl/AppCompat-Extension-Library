package com.tr4android.support.extension.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.internal.widget.ThemeUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.tr4android.appcompat.extension.R;

/**
 * A supercharged ImageView that displays images as circles and creates placeholder images
 */
public class CircleImageView extends ImageView {
    private static final String LOG_TAG = "CircleImageView";

    // whether an image or a placeholder is displayed
    private boolean mIsPlaceholder = true;

    // whether an image uri is being resolced
    private boolean mIsResolvingDrawable;

    // paint used for drawing the colored circle
    private Paint mCirclePaint;

    // paint used for drawing the placeholder text
    private Paint mTextPaint;

    // paint used for drawing the placeholder image
    private Paint mImagePaint;

    // placeholder text (usually the first letter of a name)
    private String mText = "";

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        int mPlaceholderCircleColor = a.getColor(R.styleable.CircleImageView_placeholderCircleColor, ThemeUtils.getThemeAttrColor(getContext(), R.attr.colorAccent));
        int mPlaceholderTextSize = a.getDimensionPixelSize(R.styleable.CircleImageView_placeholderTextSize, getResources().getDimensionPixelSize(R.dimen.defaultPlaceholderTextSize));
        int mPlaceholderTextColor = a.getColor(R.styleable.CircleImageView_placeholderTextColor, ThemeUtils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimaryInverse));
        a.recycle();

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mPlaceholderCircleColor);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mPlaceholderTextSize);
        mTextPaint.setColor(mPlaceholderTextColor);
        mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null) mIsPlaceholder = false;
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) mIsPlaceholder = false;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        if (resId != 0) mIsPlaceholder = false;
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(Uri uri) {
        if (uri != null) mIsPlaceholder = false;
        super.setImageURI(uri);
    }

    public void setCircleImageBitmap(Bitmap bm) {
        setImageDrawable(getCircleBitmapDrawable(bm));
    }

    public void setCircleImageDrawable(Drawable drawable) {
        setImageDrawable(getCircleBitmapDrawable(getBitmapFromDrawable(drawable)));
    }

    public void setCircleImageResource(@DrawableRes int resId) {
        mIsResolvingDrawable = true;
        setImageResource(resId);
        mIsResolvingDrawable = false;
        setImageDrawable(getCircleBitmapDrawable(getBitmapFromDrawable(getDrawable())));
    }

    public void setCircleImageURI(Uri uri) {
        mIsResolvingDrawable = true;
        setImageURI(uri);
        mIsResolvingDrawable = false;
        setImageDrawable(getCircleBitmapDrawable(getBitmapFromDrawable(getDrawable())));
    }

    /**
     * Set a placeholder with a text. This will use the default or last circle color.
     * <p/>
     * Note: This usually should be the first letter of a name. You can use the helper {@link #retrieveLetter(String)}
     * to extract the first letter of a given {@link String}.
     *
     * @param placeholderText Placeholder text to display in the middle of the circle
     */
    public void setPlaceholder(String placeholderText) {
        mIsPlaceholder = true;
        mText = placeholderText;
        setImageDrawable(null);
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
        mText = placeholderText;
        mCirclePaint.setColor(circleColor);
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
            // draw placeholder (with image or text)
            float radius = Math.min(canvas.getWidth(), canvas.getHeight()) / 2;
            int xPos = (canvas.getWidth() / 2);
            canvas.drawCircle(xPos, canvas.getHeight() / 2, radius, mCirclePaint);
            int yPos = (int) ((canvas.getHeight() / 2) -
                    ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
            canvas.drawText(mText, xPos, yPos, mTextPaint);
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
