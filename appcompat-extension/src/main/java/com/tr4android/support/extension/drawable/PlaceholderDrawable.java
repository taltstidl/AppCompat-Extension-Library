package com.tr4android.support.extension.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.utils.ThemeUtils;

public class PlaceholderDrawable extends Drawable {
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


    public PlaceholderDrawable(int placeholderTextSize, int placeholderTextColor,
                               int placeholderImageSize) {
        mPlaceholderImageSize = placeholderImageSize;

        mPlaceholderCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlaceholderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlaceholderTextPaint.setTextAlign(Paint.Align.CENTER);
        mPlaceholderTextPaint.setTextSize(placeholderTextSize);
        mPlaceholderTextPaint.setColor(placeholderTextColor);
    }

    /**
     * Set a placeholder with a text and circle color.
     * <p/>
     * Note: This usually should be the first letter of a name. You can use the helper
     * {@link com.tr4android.support.extension.widget.CircleImageView#retrieveLetter(String)}
     * to extract the first letter of a given {@link String}.
     *
     * @param placeholderText Placeholder text to display in the middle of the circle
     * @param circleColor     Color to use for the circle
     */
    public void setPlaceholder(String placeholderText, @ColorInt int circleColor) {
        mPlaceholderImage = null;
        mPlaceholderText = placeholderText;
        mPlaceholderCirclePaint.setColor(circleColor);
    }

    /**
     * Set a placeholder with an image and circle color.
     *
     * @param drawable    The placeholder drawable
     * @param circleColor Color to use for the circle
     */
    public void setPlaceholder(Drawable drawable, @ColorInt int circleColor) {
        mPlaceholderImage = drawable;
        mPlaceholderText = "";
        mPlaceholderCirclePaint.setColor(circleColor);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        // draw placeholder circle
        float radius = Math.min(bounds.width(), bounds.height()) / 2f;
        float xPos = bounds.left + (bounds.width() / 2f);
        canvas.drawCircle(xPos, bounds.top + bounds.height() / 2f, radius, mPlaceholderCirclePaint);
        if (mPlaceholderImage == null) {
            // draw placeholder text
            float yPos = (bounds.top + (bounds.height() / 2f) -
                    ((mPlaceholderTextPaint.descent() + mPlaceholderTextPaint.ascent()) / 2f));
            canvas.drawText(mPlaceholderText, xPos, yPos, mPlaceholderTextPaint);
        } else {
            // draw placeholder image
            int horizontalPadding = (bounds.width() - mPlaceholderImageSize) / 2;
            int verticalPadding = (bounds.height() - mPlaceholderImageSize) / 2;
            mPlaceholderImage.setBounds(bounds.left + horizontalPadding, bounds.top + verticalPadding,
                    bounds.right - horizontalPadding, bounds.bottom - verticalPadding);
            mPlaceholderImage.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int i) {
        mPlaceholderCirclePaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPlaceholderCirclePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static class Builder {
        Context mContext;
        int mPlaceholderTextSize;
        int mPlaceholderTextColor;
        int mPlaceholderImageSize;
        int mPlaceholderCircleColor;
        String mPlaceholderString = "";
        Drawable mPlaceholderDrawable = null;

        public Builder(Context context) {
            mContext = context;
            mPlaceholderTextSize = context.getResources()
                    .getDimensionPixelSize(R.dimen.defaultPlaceholderTextSize);
            mPlaceholderTextColor = ThemeUtils.getThemeAttrColor(context,
                    android.R.attr.textColorPrimaryInverse);
            mPlaceholderImageSize = context.getResources()
                    .getDimensionPixelSize(R.dimen.defaultPlaceholderImageSize);
            mPlaceholderCircleColor = ThemeUtils.getThemeAttrColor(context, R.attr.colorAccent);
        }

        public Builder setPlaceholderTextSize(int textSize) {
            mPlaceholderTextSize = textSize;
            return this;
        }

        public Builder setPlaceholderTextColor(@ColorInt int color) {
            mPlaceholderTextColor = color;
            return this;
        }

        public Builder setPlaceholderImageSize(int imageSize) {
            mPlaceholderImageSize = imageSize;
            return this;
        }

        public Builder setPlaceholderCircleColor(@ColorInt int color) {
            mPlaceholderCircleColor = color;
            return this;
        }

        public Builder setPlaceholderText(@StringRes int resid) {
            return setPlaceholderText(mContext.getResources().getString(resid));
        }

        public Builder setPlaceholderText(String string) {
            mPlaceholderString = string;
            mPlaceholderDrawable = null;
            return this;
        }

        public Builder setPlaceholderImage(@DrawableRes int resId) {
            return setPlaceholderImage(ContextCompat.getDrawable(mContext, resId));
        }

        public Builder setPlaceholderImage(Drawable drawable) {
            mPlaceholderDrawable = drawable;
            mPlaceholderString = "";
            return this;
        }

        public PlaceholderDrawable build() {
            PlaceholderDrawable drawable = new PlaceholderDrawable(
                    mPlaceholderTextSize, mPlaceholderTextColor, mPlaceholderImageSize);
            if (mPlaceholderDrawable == null) {
                drawable.setPlaceholder(mPlaceholderString, mPlaceholderCircleColor);
            } else {
                drawable.setPlaceholder(mPlaceholderDrawable, mPlaceholderCircleColor);
            }
            return drawable;
        }
    }
}
