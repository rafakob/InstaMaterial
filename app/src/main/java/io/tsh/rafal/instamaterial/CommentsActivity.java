package io.tsh.rafal.instamaterial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentsActivity extends AppCompatActivity implements SendCommentButton.OnSendClickListener{
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.contentRoot) LinearLayout contentRoot;
    @Bind(R.id.rvComments) RecyclerView rvComments;
    @Bind(R.id.llAddComment) LinearLayout llAddComment;
    @Bind(R.id.btnSendComment) SendCommentButton btnSendComment;
    @Bind(R.id.etComment) EditText etComment;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
        setupToolbar();
        setupFeed();
        btnSendComment.setOnSendClickListener(this);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);


        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            commentsAdapter.addItem();
            commentsAdapter.setAnimationsLocked(false);
            commentsAdapter.setDelayEnterAnimation(false);
            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

//            etComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem inboxMenuItem = menu.findItem(R.id.action_inbox).setActionView(R.layout.menu_item_view);
        return true;
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }

}