package com.example.swipedismiss2.app;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

public class SwipeDismissController {

    private Context mContext;
    private FrameLayout mLayoutDemo;
    // upView
    private Button mButtonDemo;
    // bottomView
    private Button mDeleteDemo;

    private OnDismissCallback mDismissCallback;
    private Object mToken;

    private long mAnimationTime;

    public interface OnDismissCallback {
        void onDismiss(View view, Object token);
    }

    public FrameLayout createSwipeDismissController(Context context, int index,
                                                    Object token, OnDismissCallback dismissCallback) {
        mToken = token;
        mDismissCallback = dismissCallback;

        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutDemo = (FrameLayout) inflater.inflate(R.layout.swipe_item, null);
        mButtonDemo = (Button) mLayoutDemo.findViewById(R.id.content);
        mButtonDemo.setText("View " + index);
        mButtonDemo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,
                        "Clicked " + ((Button) view).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mButtonDemo.setOnTouchListener(new SwipeDismissTouchListener(
                mButtonDemo));
        mDeleteDemo = (Button) mLayoutDemo.findViewById(R.id.delete);
        mDeleteDemo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performDismiss();
            }
        });

        mAnimationTime = mContext.getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        return mLayoutDemo;
    }

    private void performDismiss() {
        final LayoutParams layoutParams = mLayoutDemo.getLayoutParams();
        final int originalHeight = mLayoutDemo.getHeight();
        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1)
                .setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDismissCallback.onDismiss(mLayoutDemo, mToken);
                setAlpha(mLayoutDemo, 1.0f);
                setTranslationX(mLayoutDemo, 0.0f);
                layoutParams.height = originalHeight;
                mLayoutDemo.setLayoutParams(layoutParams);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height = (Integer) valueAnimator
                        .getAnimatedValue();
                mLayoutDemo.setLayoutParams(layoutParams);
            }
        });

        animator.start();
    }

    public class SwipeDismissTouchListener implements View.OnTouchListener {

        private int mSlop;
        private int mMinFlingVelocity;
        private int mMaxFlingVelocity;
        private VelocityTracker mVelocityTracker;

        private View mView;
        private int mViewWidth;

        private float mDownX;
        private float mTranslationX;
        private boolean mSwiping;
        private boolean mHalfing;

        public SwipeDismissTouchListener(View view) {
            ViewConfiguration vc = ViewConfiguration.get(view.getContext());
            mSlop = vc.getScaledTouchSlop();
            mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
            mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
            mView = view;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // offset because the view is translated during swipe
            motionEvent.offsetLocation(mTranslationX, 0);
            mViewWidth = mView.getWidth();

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    mDownX = motionEvent.getRawX();
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionEvent);
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    if (mVelocityTracker == null) {
                        break;
                    }

                    float deltaX = motionEvent.getRawX() - mDownX;
                    boolean dismissRight = deltaX > 0;
                    if (mHalfing) {
                        if (!dismissRight) {
                            reset();
                            break;
                        }
                    } else {
                        if (dismissRight) {
                            reset();
                            break;
                        }
                    }

                    boolean dismiss = false;
                    mVelocityTracker.addMovement(motionEvent);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float velocityX = Math.abs(mVelocityTracker.getXVelocity());
                    float velocityY = Math.abs(mVelocityTracker.getYVelocity());
                    if (Math.abs(deltaX) > mViewWidth / 2) {
                        dismiss = true;
                    } else if (mMinFlingVelocity <= velocityX
                            && velocityX <= mMaxFlingVelocity
                            && velocityY < velocityX) {
                        dismiss = true;
                        dismissRight = mVelocityTracker.getXVelocity() > 0;
                    }
                    if (dismiss) {
                        // dismiss
                        animate(mView).translationX(mHalfing ? 0 : -mViewWidth / 2)
                                .setDuration(mAnimationTime).setListener(null);
                        mHalfing = !mHalfing;
                    } else {
                        // cancel
                        animate(mView).translationX(mHalfing ? -mViewWidth / 2 : 0)
                                .setDuration(mAnimationTime).setListener(null);
                    }
                    reset();
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (mVelocityTracker == null) {
                        break;
                    }
                    mVelocityTracker.addMovement(motionEvent);

                    float deltaX = motionEvent.getRawX() - mDownX;
                    boolean dismissRight = deltaX > 0;
                    if (mHalfing) {
                        if (!dismissRight) {
                            break;
                        }
                    } else {
                        if (dismissRight) {
                            break;
                        }
                    }

                    if (Math.abs(deltaX) > mSlop) {
                        mSwiping = true;
                        mView.getParent().requestDisallowInterceptTouchEvent(true);
                        MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                        cancelEvent
                                .setAction(MotionEvent.ACTION_CANCEL
                                        | (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                        mView.onTouchEvent(cancelEvent);
                        cancelEvent.recycle();
                    }

                    if (mSwiping) {
                        mTranslationX = deltaX;
                        if (mHalfing) {
                            setTranslationX(mView, deltaX - mViewWidth / 2);
                        } else {
                            setTranslationX(mView, deltaX);
                        }
                    }
                    break;
                }
            }
            return false;
        }

        private void reset() {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mTranslationX = 0;
            mDownX = 0;
            mSwiping = false;
        }
    }
}
