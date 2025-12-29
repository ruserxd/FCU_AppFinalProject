package fcu.app.appclassfinalproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardView extends View {

  private Paint paint;
  private Path currentPath;
  private List<Path> paths;
  private List<Paint> paints;
  private int currentColor = Color.BLACK;
  private float strokeWidth = 5f;

  public WhiteboardView(Context context) {
    super(context);
    init();
  }

  public WhiteboardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public WhiteboardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setColor(currentColor);
    paint.setStrokeWidth(strokeWidth);

    paths = new ArrayList<>();
    paints = new ArrayList<>();
    currentPath = new Path();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawColor(Color.WHITE);

    for (int i = 0; i < paths.size(); i++) {
      canvas.drawPath(paths.get(i), paints.get(i));
    }

    if (!currentPath.isEmpty()) {
      canvas.drawPath(currentPath, paint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        currentPath = new Path();
        currentPath.moveTo(x, y);
        invalidate();
        return true;

      case MotionEvent.ACTION_MOVE:
        currentPath.lineTo(x, y);
        invalidate();
        return true;

      case MotionEvent.ACTION_UP:
        paths.add(new Path(currentPath));
        Paint pathPaint = new Paint(paint);
        paints.add(pathPaint);
        currentPath.reset();
        invalidate();
        return true;

      default:
        return false;
    }
  }

  public void setColor(int color) {
    currentColor = color;
    paint.setColor(color);
  }

  public void setStrokeWidth(float width) {
    strokeWidth = width;
    paint.setStrokeWidth(width);
  }

  public void clear() {
    paths.clear();
    paints.clear();
    currentPath.reset();
    invalidate();
  }

  public void undo() {
    if (!paths.isEmpty()) {
      paths.remove(paths.size() - 1);
      paints.remove(paints.size() - 1);
      invalidate();
    }
  }

  public void addPath(Path path, Paint pathPaint) {
    paths.add(path);
    paints.add(pathPaint);
    invalidate();
  }
}

