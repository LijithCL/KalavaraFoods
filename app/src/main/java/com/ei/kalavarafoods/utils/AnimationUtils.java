package com.ei.kalavarafoods.utils;

import android.animation.ObjectAnimator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ei.kalavarafoods.ItemExpandedActivity;
import com.ei.kalavarafoods.ui.category.SubCategoryFragment;
import com.ei.kalavarafoods.ui.main.wishlist.WishListFragment;

public class AnimationUtils {
    public static void animate(SubCategoryFragment.Cart_RecycleAdapter.ViewHolderRec holder, boolean down) {
        if (down)
            YoYo.with(Techniques.BounceInUp).duration(1500).playOn(holder.itemView);
        else
            YoYo.with(Techniques.BounceInDown).duration(1500).playOn(holder.itemView);
    }

    public static void animateHor(ItemExpandedActivity.RCViewHolder holder, boolean b) {
        if (b)
            YoYo.with(Techniques.BounceInRight).duration(1500).playOn(holder.itemView);
        else
            YoYo.with(Techniques.BounceInLeft).duration(1500).playOn(holder.itemView);
    }

    public static void animateWishList(WishListFragment.Wishlist_RCAdapter.VHWishList holder, boolean down) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationY", down ? 250 : -250, 0);
        animator.setDuration(700);
        animator.start();
    }
}
