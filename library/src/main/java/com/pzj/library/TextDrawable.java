package com.pzj.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * 文本图像
 *
 * 参考：https://github.com/amulyakhare/TextDrawable
 *
 * @author PengZhenjin
 * @date 2017-6-27
 */
public class TextDrawable extends ShapeDrawable {

    private static final float SHADE_FACTOR = 0.9f;

    private final RectShape shape;
    private final Paint     textPaint;
    private final Paint     borderPaint;
    private final String    text;
    private final int       height;
    private final int       width;
    private final int       fontSize;
    private final float     radius;
    private final int       borderThickness;

    private TextDrawable(Builder builder) {
        super(builder.shape);

        // shape properties
        this.shape = builder.shape;
        this.height = builder.height;
        this.width = builder.width;
        this.radius = builder.radius;

        // text
        this.text = builder.toUpperCase ? builder.text.toUpperCase() : builder.text;

        // text paint settings
        this.fontSize = builder.fontSize;
        this.textPaint = new Paint();
        this.textPaint.setColor(builder.textColor);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setFakeBoldText(builder.isBold);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTypeface(builder.font);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setStrokeWidth(builder.borderThickness);

        // border paint settings
        this.borderThickness = builder.borderThickness;
        this.borderPaint = new Paint();
        this.borderPaint.setColor(getDarkerShade(builder.bgColor));
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(borderThickness);

        // drawable paint bgColor
        Paint paint = getPaint();
        paint.setColor(builder.bgColor);
    }

    /**
     * 获取暗阴影色
     *
     * @param color
     *
     * @return
     */
    private int getDarkerShade(int color) {
        return Color.rgb((int) (SHADE_FACTOR * Color.red(color)), (int) (SHADE_FACTOR * Color.green(color)), (int) (SHADE_FACTOR * Color.blue(color)));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = getBounds();

        // draw border
        if (this.borderThickness > 0) {
            drawBorder(canvas);
        }

        int count = canvas.save();
        canvas.translate(r.left, r.top);

        // draw text
        int width = this.width < 0 ? r.width() : this.width;
        int height = this.height < 0 ? r.height() : this.height;
        int fontSize = this.fontSize < 0 ? (Math.min(width, height) / 2) : this.fontSize;
        this.textPaint.setTextSize(fontSize);
        canvas.drawText(this.text, width / 2, height / 2 - ((this.textPaint.descent() + this.textPaint.ascent()) / 2), this.textPaint);

        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(getBounds());
        rect.inset(this.borderThickness / 2, this.borderThickness / 2);

        if (this.shape instanceof OvalShape) {
            canvas.drawOval(rect, this.borderPaint);
        }
        else if (this.shape instanceof RoundRectShape) {
            canvas.drawRoundRect(rect, this.radius, this.radius, this.borderPaint);
        }
        else {
            canvas.drawRect(rect, this.borderPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        this.textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        this.textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override
    public int getIntrinsicHeight() {
        return this.height;
    }

    public static IShapeBuilder builder() {
        return new Builder();
    }

    public static class Builder implements IConfigBuilder, IShapeBuilder, IBuilder {

        private String    text;
        private int       textColor;
        private int       bgColor;
        private int       borderThickness;
        private int       width;
        private int       height;
        private int       fontSize;
        private boolean   isBold;
        private boolean   toUpperCase;
        private float     radius;
        private Typeface  font;
        private RectShape shape;

        private Builder() {
            this.text = "";
            this.bgColor = Color.GRAY;
            this.textColor = Color.WHITE;
            this.borderThickness = 0;
            this.width = -1;
            this.height = -1;
            this.shape = new RectShape();
            this.font = Typeface.create("sans-serif-light", Typeface.NORMAL);
            this.fontSize = -1;
            this.isBold = false;
            this.toUpperCase = false;
        }

        public IConfigBuilder width(int width) {
            this.width = width;
            return this;
        }

        public IConfigBuilder height(int height) {
            this.height = height;
            return this;
        }

        public IConfigBuilder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public IConfigBuilder withBorder(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        public IConfigBuilder useFont(Typeface font) {
            this.font = font;
            return this;
        }

        public IConfigBuilder fontSize(int size) {
            this.fontSize = size;
            return this;
        }

        public IConfigBuilder bold() {
            this.isBold = true;
            return this;
        }

        public IConfigBuilder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        @Override
        public IConfigBuilder beginConfig() {
            return this;
        }

        @Override
        public IShapeBuilder endConfig() {
            return this;
        }

        @Override
        public IBuilder rect() {
            this.shape = new RectShape();
            return this;
        }

        @Override
        public IBuilder round() {
            this.shape = new OvalShape();
            return this;
        }

        @Override
        public IBuilder roundRect(int radius) {
            this.radius = radius;
            float[] radii = { radius, radius, radius, radius, radius, radius, radius, radius };
            this.shape = new RoundRectShape(radii, null, null);
            return this;
        }

        @Override
        public TextDrawable buildRect(String text, int textColor, int bgColor) {
            rect();
            return build(text, textColor, bgColor);
        }

        @Override
        public TextDrawable buildRoundRect(String text, int textColor, int bgColor, int radius) {
            roundRect(radius);
            return build(text, textColor, bgColor);
        }

        @Override
        public TextDrawable buildRound(String text, int textColor, int bgColor) {
            round();
            return build(text, textColor, bgColor);
        }

        @Override
        public TextDrawable build(String text, int textColor, int bgColor) {
            if (text != null && !"".equals(text)) {
                text = text.substring(0, 1);    // 截取第一个字符
            }
            this.text = text;
            this.textColor = textColor;
            this.bgColor = bgColor;
            return new TextDrawable(this);
        }
    }

    public interface IConfigBuilder {
        IConfigBuilder width(int width);

        IConfigBuilder height(int height);

        IConfigBuilder textColor(int color);

        IConfigBuilder withBorder(int thickness);

        IConfigBuilder useFont(Typeface font);

        IConfigBuilder fontSize(int size);

        IConfigBuilder bold();

        IConfigBuilder toUpperCase();

        IShapeBuilder endConfig();
    }

    public interface IBuilder {
        TextDrawable build(String text, int textColor, int bgColor);
    }

    public interface IShapeBuilder {

        IConfigBuilder beginConfig();

        IBuilder rect();

        IBuilder round();

        IBuilder roundRect(int radius);

        TextDrawable buildRect(String text, int textColor, int bgColor);

        TextDrawable buildRoundRect(String text, int textColor, int bgColor, int radius);

        TextDrawable buildRound(String text, int textColor, int bgColor);
    }
}