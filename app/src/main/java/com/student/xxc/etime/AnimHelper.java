package com.student.xxc.etime;

import android.support.v4.view.ViewCompat;
import android.view.View;

public final class AnimHelper{
    /*
    public void runEnterAnimation(View view, int position) {
        if (animationsLocked)  {
            AnimHelper.clear(view);
            return;
        }

        if (position > lastAnimatedPosition) {//lastAnimatedPosition是int类型变量，默认-1，
            lastAnimatedPosition = position;
            view.setTranslationX(-500);
            view.setAlpha(0.f);           //item项一开始完全透明
            view.animate()
                    .translationY(0).alpha(1.f)                                //设置最终效果为完全不透明
                    //并且在原来的位置
                    .setStartDelay(delayEnterAnimation ? 200 * (position) : 0)//根据item的位置设置延迟时间?出现闪烁清除不掉
                    //达到依次动画一个接一个进行的效果
                    .setInterpolator(new DecelerateInterpolator(0.5f))     //设置动画位移先快后慢的效果
                    .setDuration(700)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }

    }
    */
       public static void clear(View v) {
           ViewCompat.setAlpha(v, 1);
           ViewCompat.setScaleY(v, 1);
           ViewCompat.setScaleX(v, 1);
           ViewCompat.setTranslationY(v, 0);
           ViewCompat.setTranslationX(v, 0);
           ViewCompat.setRotation(v, 0);
           ViewCompat.setRotationY(v, 0);
           ViewCompat.setRotationX(v, 0);
           ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
           ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
           ViewCompat.animate(v).setInterpolator(null).setStartDelay(0);
       }


}
