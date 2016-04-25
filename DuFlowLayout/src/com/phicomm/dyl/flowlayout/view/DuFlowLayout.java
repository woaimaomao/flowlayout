package com.phicomm.dyl.flowlayout.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class DuFlowLayout extends ViewGroup {

    public DuFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DuFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DuFlowLayout(Context context) {
        this(context, null);
    }

    
    List<List<View>> allViews = new ArrayList<>();
    List<Integer> lineHeights = new ArrayList<>();
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        allViews.clear();
        lineHeights.clear();
        int width = getMeasuredWidth();
        int childCount = getChildCount();
//        Log.i("dyl++", "childCount == "+ childCount);
        List<View> lineViews = new ArrayList<View>();
        int lineWidth = 0;
        int lineHeight = 0;
        for(int i = 0 ; i<childCount; i++){ 
            View child = getChildAt(i);
            MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if(lineWidth + childWidth + mlp.leftMargin + mlp.rightMargin > width - getPaddingRight() - getPaddingLeft()){
                allViews.add(lineViews);
                lineHeights.add(lineHeight);
                lineHeight = childHeight + mlp.bottomMargin + mlp.topMargin;
                lineWidth = childWidth + mlp.leftMargin + mlp.rightMargin;
                lineViews = new ArrayList<View>();
                lineViews.add(child);
            }else{
                lineViews.add(child);
                lineWidth += (childWidth + mlp.leftMargin + mlp.rightMargin);
                lineHeight = Math.max(lineHeight, childHeight + mlp.bottomMargin + mlp.topMargin);
            }
            
            if(i == childCount - 1){
                allViews.add(lineViews);
                lineHeights.add(lineHeight);
            }
        }
        
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int allViewsSize = allViews.size();
        Log.i("dyl++", "allViewsSize == "+ allViewsSize);
        for(int i=0; i<allViewsSize; i++){
            List<View> lines = allViews.get(i);
            int lineH = lineHeights.get(i);
            int linesSize = lines.size();
            for(int j=0; j<linesSize; j++){
                View child = lines.get(j);
                if(child.getVisibility() == View.GONE){
                    continue;
                }
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                int cl = left + mlp.leftMargin;
                int ct = top + mlp.topMargin;
                int cr = cl + child.getMeasuredWidth() ;
                int cb = ct + child.getMeasuredHeight() ;
                child.layout(cl, ct, cr, cb);
                left += mlp.leftMargin + child.getMeasuredWidth() +mlp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineH;
        }
        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        // EXACTLY
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        
        // AT_MOST
        int width = 0;
        int height = 0;
        int lineWidth = 0;
        int lineHeight = 0;
        int childCount = getChildCount();
        for(int i=0; i< childCount; i++){
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            if(lineWidth + childWidth + lp.leftMargin + lp.rightMargin > sizeWidth - getPaddingLeft() - getPaddingRight()){
                width = Math.max(width, lineWidth);
                lineWidth = childWidth + lp.leftMargin + lp.rightMargin;
                height += lineHeight;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
            }else{
                lineWidth += (childWidth + lp.leftMargin + lp.rightMargin);
                lineHeight = Math.max( lineHeight, childHeight+lp.bottomMargin + lp.topMargin);
            }
            if(i == childCount -1){
                width = Math.max(width, childWidth);
                height += lineHeight;
            }
        }
        // 12-14 09:06:40.652: I/dyl++(8340): width = 684  height = 216
        // 12-14 09:07:22.040: I/dyl++(9009): width = 684  height = 216
        Log.i("dyl++", "width = "+width + "  height = "+height);
        setMeasuredDimension(modeWidth == MeasureSpec.AT_MOST ? width + getPaddingLeft() + getPaddingRight(): sizeWidth, modeHeight == MeasureSpec.AT_MOST ? height + getPaddingBottom() + getPaddingTop(): sizeHeight);
        
    }
    
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

}
