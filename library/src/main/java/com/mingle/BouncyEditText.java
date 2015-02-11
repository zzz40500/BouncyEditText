package com.mingle;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import com.mingle.bouncyhintedittext.R;


/**
 * Created by zzz40500 on 15/2/10.
 */
public class BouncyEditText extends EditText {



    private Paint paint;
    private Status status= Status.ANIMATION_NONE;
    private String hintText;
    private Interpolator animOutInterpolator;
    private Interpolator animInInterpolator;
    private float animInDuration=200.0f;
    private float animOutDuration=260.0f;
    private int hintColor=getResources().getColor(R.color.hint_color);

    private boolean isSetPadding=false;
    private boolean ishasHint= false;

    public BouncyEditText(Context context) {
        super(context);
        init();
    }

    public BouncyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);    init();
    }

    public BouncyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        init();
    }

    private void init() {
         paint=new Paint();

        animOutInterpolator = new OvershootInterpolator(1.3f);
        animInInterpolator = new DecelerateInterpolator();

        if(getHint()!=null) {
            ishasHint=true;
            hintText = getHint().toString();
            setHint("");

        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BouncyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    long startTime;

    private String preString;

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);


       if(!ishasHint){
           return;
       }

        if(TextUtils.isEmpty(preString)!=TextUtils.isEmpty( getText().toString())) {


            if (!TextUtils.isEmpty(getText().toString())) {
                status = Status.ANIMATION_OUT;

            } else {
                status = Status.ANIMATION_IN;
            }

            preString = (String) getText().toString();
            startTime = System.currentTimeMillis();
        }
    }



    public void setHintText(String hintText) {
        this.hintText = hintText;

        ishasHint=true;
        this.hintText = hintText;
        setHint("");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!ishasHint){
            return;
        }

        float maxHintY = getBaseline();
        switch (status){
            case  ANIMATION_IN:
                if(System.currentTimeMillis()-startTime<animInDuration){
                    float hintX=getCompoundPaddingLeft() + getScrollX()+(getWidth()-getCompoundPaddingRight()-getCompoundPaddingLeft())*(1- animInInterpolator.getInterpolation((System.currentTimeMillis()-startTime)/animInDuration));
                    paint.set(getPaint());
                    paint.setAntiAlias(true);
                    paint.setColor(hintColor) ;
                    canvas.drawText(hintText,hintX,maxHintY,paint);
                    postInvalidate();
                }else{
                    float hintX = getCompoundPaddingLeft() + getScrollX();
                    canvas.drawText(hintText, hintX, maxHintY, paint);
                }

            break;
            case  ANIMATION_OUT:
                if(System.currentTimeMillis()-startTime<animOutDuration){
                    float hintX=getCompoundPaddingLeft() + getScrollX()+(getWidth()-getCompoundPaddingRight()-getCompoundPaddingLeft())* animOutInterpolator.getInterpolation((System.currentTimeMillis()-startTime)/animOutDuration);
                    paint.set(getPaint());
                    paint.setAntiAlias(true);
                    paint.setColor(hintColor) ;
                    canvas.drawText(hintText,hintX,maxHintY,paint);
                    postInvalidate();
                }else{
                    float hintX=getCompoundPaddingLeft() + getScrollX()+(getWidth()-getCompoundPaddingRight()-getCompoundPaddingLeft());
                    canvas.drawText(hintText, hintX, maxHintY, paint);
                }

                break;
            case  ANIMATION_NONE:
                paint.set(getPaint());
                paint.setAntiAlias(true);
                paint.setColor(hintColor) ;

                if(!isSetPadding) {
                    setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + (int) paint.measureText(hintText), getPaddingBottom());

                    isSetPadding=true;
                }

                if(getText().toString().length()==0){
                    float hintX = getCompoundPaddingLeft() + getScrollX();

                    canvas.drawText(hintText,hintX,maxHintY,paint);
                }else{

                    float hintX=getCompoundPaddingLeft() + getScrollX()+(getWidth()-getCompoundPaddingRight()-getCompoundPaddingLeft());
                    canvas.drawText(hintText,hintX,maxHintY,paint);
                }

                break;
        }

    }


    public enum  Status{
        ANIMATION_IN,
        ANIMATION_NONE,
        ANIMATION_OUT;

    }
}
