package com.example.swipedismiss2.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SimpleViewsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpleviews);

        final ViewGroup dismissableContainer = (ViewGroup) findViewById(R.id.dismissable_container);
        for (int i = 0; i < 20; i++) {
            SwipeDismissController swipeDismissController = new SwipeDismissController();
            FrameLayout layout = swipeDismissController
                    .createSwipeDismissController(this, i, null,
                            new SwipeDismissController.OnDismissCallback() {
                                @Override
                                public void onDismiss(View view, Object token) {
                                    dismissableContainer.removeView(view);
                                }
                            }
                    );
            dismissableContainer.addView(layout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
