package com.proj.agnus.comun;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Display;

/**
 * Created by Arturo on 3/22/2018.
 */

public class Imagen_Circular {

    public Bitmap getCircleBitmap(Bitmap bitmap, Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int tam = width/4;

        Bitmap output;
        Canvas canvas = null;
        final int color = 0xffff0000;
        final Paint paint = new Paint();
        Rect rect = null;

        if (bitmap.getHeight() > 501) {
            output = Bitmap.createBitmap(tam, tam, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(output);
            rect = new Rect(0, 0, tam, tam);
        } else {
            output = Bitmap.createBitmap(tam, tam, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(output);
            rect = new Rect(0, 0, tam, tam);
        }

        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        Bitmap bmScaled = Bitmap.createScaledBitmap(bitmap, tam, tam, true);
        canvas.drawBitmap(bmScaled, rect, rect, paint);
        return output;
    }
}
