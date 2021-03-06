package learn.li.login2test.RoundIndicator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import learn.li.login2test.R;

/**
 * Created by 李天烨 on 2016/11/30.
 */

public class RoundIndicatorView extends View {
    private Paint paint_1;
    private Paint paint_2, paint_3, paint_4;
    private Context context;
    private int maxNum, startAngle, sweepAngle, radius, mWidth, mHeight;
    private int sweepInWidth; //内圆的宽度
    private int sweepOutWidth; //外圆的宽度
    private int currentNum=0; //需设置setter，getter供属性动画使用
    private String[] text ={" "," "," "," "," "};
    private int[] indicatorColor = {0xffffffff, 0x00ffffff, 0x99ffffff, 0xffffffff};

    public int getCurrentNum(){
        return currentNum;
    }
    public void setCurrentNum(int currentNum){
        this.currentNum = currentNum;
        invalidate();
    }

    public RoundIndicatorView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public RoundIndicatorView(final Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.context = context;
        setBackgroundColor(0xffff6347);
        initAttr(attrs);
        initPaint();
    }

    public void setCurrentNumAnim(int num){
        float duration = Math.abs(num-currentNum)/maxNum*1500+500;//根据进度差计算动画时间
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentNum", num);
        anim.setDuration((long) Math.min(duration, 2000));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        anim.start();
    }

    private int calculateColor(int value){
        ArgbEvaluator evaluator = new ArgbEvaluator();
        float fraction = 0;
        int color = 0;
        if (value<=maxNum/2){
            fraction = (float)value/(maxNum/2);
            color = (int)evaluator.evaluate(fraction, 0xff00ced1, 0xff00ced1);//由红到橙
        }else {
            fraction = ((float)value-maxNum/2)/(maxNum/2);
            color = (int)evaluator.evaluate(fraction, 0xff00ced1, 0xff00ced1);//由橙到蓝
        }
        return color;
    }

    public RoundIndicatorView(Context context) {
        super(context);
    }

    private void initAttr(AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundIndicatorView);
        maxNum = array.getInt(R.styleable.RoundIndicatorView_maxNum, 200);
        startAngle = array.getInt(R.styleable.RoundIndicatorView_startAngle, 160);
        sweepAngle = array.getInt(R.styleable.RoundIndicatorView_sweepAngle, 220);
        //内外圈弧的宽度
        sweepInWidth = dp2px(9);
        sweepOutWidth = dp2px(4);
        array.recycle();
    }

    private void initPaint(){
        paint_1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_1.setDither(true);
        paint_1.setStyle(Paint.Style.STROKE);
        paint_1.setColor(0xffffffff);
        paint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_4 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if (wMode == MeasureSpec.EXACTLY){
            mWidth = wSize;
        }else {
            mWidth = dp2px(300);
        }
        if (hMode == MeasureSpec.EXACTLY){
            mHeight = hSize;
        }else {
            mHeight = dp2px(400);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oleh){
        super.onSizeChanged(w, h, oldw, oleh);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        radius = getMeasuredWidth()/3;//不要在构造方法里初始化，那时还没测量高度的值
        canvas.save();
        canvas.translate(mWidth/2, (mWidth)/2);
        drawRound(canvas);//画内外圆弧
        drawScale(canvas);//画刻度
        drawIndicator(canvas);//画当前进度值
        drawCenterText(canvas);//画中间的文字
        canvas.restore();

    }

    private void drawRound(Canvas canvas){
        canvas.save();
        //内圆
        paint_1.setAlpha(0x40);
        paint_1.setStrokeWidth(sweepInWidth);
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint_1);
        //外圆
        paint_1.setStrokeWidth(sweepOutWidth);
        int w = dp2px(10);
        RectF rectF1 = new RectF(-radius-w, -radius-w, radius+w, radius+w);
        canvas.drawArc(rectF1, startAngle, sweepAngle, false, paint_1);
        canvas.restore();
    }

    private void drawScale(Canvas canvas){
        canvas.save();
        float angle = (float) sweepAngle/30;//刻度间隔
        canvas.rotate(-270+startAngle);//将起始刻度点旋转到正上方（270）
        for (int i=0; i<=30; i++){
            if (i%6 == 0){//画粗刻度和刻度值
                paint_1.setStrokeWidth(dp2px(2));
                paint_1.setAlpha(0x70);
                canvas.drawLine(0, -radius-sweepInWidth/2, 0, -radius+sweepInWidth/2+dp2px(1), paint_1);
                drawText(canvas, i*maxNum/30+"", paint_1);
            }else {//画细刻度
                paint_1.setStrokeWidth(dp2px(1));
                paint_1.setAlpha(0x50);
                canvas.drawLine(0, -radius-sweepInWidth/2, 0, -radius+sweepInWidth/2, paint_1);
            }
            if (i==3||i==9||i==15||i==21||i==27){//画刻度区间文字
                paint_1.setStrokeWidth(dp2px(2));
                paint_1.setAlpha(0x50);
                drawText(canvas, text[(i-3)/6], paint_1);
            }
            canvas.rotate(angle);//逆时针
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas, String text, Paint paint){
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(8));
        float width = paint.measureText(text);//相比getTextBounds来说，这个方法获得的类型时float，更精确些
        canvas.drawText(text, -width/2, -radius+dp2px(15), paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawIndicator(Canvas canvas){
        canvas.save();
        paint_2.setStyle(Paint.Style.STROKE);
        int sweep;
        if (currentNum<=maxNum){
            sweep = (int)((float)currentNum/(float)maxNum*sweepAngle);
        }else {
            sweep = sweepAngle;
        }
        paint_2.setStrokeWidth(sweepOutWidth);
        Shader shader = new SweepGradient(0, 0, indicatorColor, null);
        paint_2.setShader(shader);
        int w = dp2px(10);
        RectF rectF = new RectF(-radius-w, -radius-w, radius+w, radius+w);
        canvas.drawArc(rectF, startAngle, sweep, false, paint_2);
        float x = (float) ((radius+dp2px(10))*Math.cos(Math.toRadians(startAngle+sweep)));
        float y = (float) ((radius+dp2px(10))*Math.sin(Math.toRadians(startAngle+sweep)));
        paint_3.setStyle(Paint.Style.FILL);
        paint_3.setColor(0xffffffff);
        paint_3.setMaskFilter(new BlurMaskFilter(dp2px(3), BlurMaskFilter.Blur.SOLID));//需关闭硬件加速
        canvas.drawCircle(x, y, dp2px(3), paint_3);
        canvas.restore();
    }

    private void drawCenterText(Canvas canvas){
        canvas.save();
        paint_4.setStyle(Paint.Style.FILL);
        paint_4.setTextSize(radius/2);
        paint_4.setColor(0xffffffff);
        canvas.drawText(currentNum+"", -paint_4.measureText(currentNum+"")/2, 0, paint_4);
        paint_4.setTextSize(radius/4);
        String content = "心跳";
        if (currentNum<maxNum*1/5){
            content += text[0];
        }else if (currentNum >= maxNum*1/5 && currentNum<maxNum*2/5){
            content += text[1];
        }else if (currentNum >= maxNum*2/5 && currentNum<maxNum*3/5){
            content += text[2];
        }else if (currentNum >= maxNum*3/5 && currentNum<maxNum*4/5){
            content += text[3];
        }else if (currentNum >= maxNum*4/5){
            content += text[4];
        }
        Rect r = new Rect();
        paint_4.getTextBounds(content, 0, content.length(), r);
        canvas.drawText(content, -r.width()/2, r.height()+20, paint_4);
        canvas.restore();
    }
    //一些工具方法
    protected int dp2px(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }
    protected int sp2px(int sp){
        return (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }
    public static DisplayMetrics getScreenMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
