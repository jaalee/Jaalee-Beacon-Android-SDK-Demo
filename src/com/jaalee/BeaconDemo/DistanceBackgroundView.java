package com.jaalee.BeaconDemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.jaalee.BeaconDemo.R;

/**
 * http://www.jaalee.com/
 * @author JAALEE, Inc.
 * We have been trying to provide better services and products! Jaalee Beacon makes 
 * life more simple and cheerful! If you are interested in our product, 
 * please contact us in following ways. We will provide the best service wholeheartedly for you!
 * 
 * Buy Jaalee Beacon: sales@jaalee.com
 * 
 * Technical Support: dev@jaalee.com
 * 
 */
public class DistanceBackgroundView extends View {

  private final Drawable drawable;

  public DistanceBackgroundView(Context context, AttributeSet attrs) {
    super(context, attrs);
    drawable = context.getResources().getDrawable(R.drawable.bg_distance);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int width = drawable.getIntrinsicWidth() * canvas.getHeight() / drawable.getIntrinsicHeight();
    int deltaX = (width - canvas.getWidth()) / 2;
    drawable.setBounds(-deltaX, 0, width - deltaX, canvas.getHeight());
    drawable.draw(canvas);
  }
}
