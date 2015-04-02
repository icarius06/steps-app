package com.onaio.steps.activityHandler;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

import com.onaio.steps.R;
import com.onaio.steps.activityHandler.Interface.IMenuHandler;
import com.onaio.steps.activityHandler.Interface.IPrepare;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.helper.Dialog;
import com.onaio.steps.model.Member;

public class DeleteMemberHandler implements IMenuHandler,IPrepare {
    private Activity activity;
    private Member member;
    private static final int MENU_ID= R.id.action_member_delete;
    private Menu menu;

    public DeleteMemberHandler(Activity activity, Member member) {
        this.activity = activity;
        this.member = member;
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == MENU_ID;
    }

    @Override
    public boolean open() {
        DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                member.delete(new DatabaseHelper(activity));
                HouseholdActivityHandler handler = new HouseholdActivityHandler(activity, member.getHousehold());
                handler.open();
            }
        };
        new Dialog().confirm(activity,confirmListener,Dialog.EmptyListener,R.string.member_delete_confirm, R.string.confirm_ok);
        return true;
    }

    public DeleteMemberHandler withMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

    @Override
    public boolean shouldInactivate() {
        return String.valueOf(member.getId()).equals(member.getHousehold().getSelectedMember());
    }

    @Override
    public void inactivate() {
        MenuItem menuItem = menu.findItem(MENU_ID);
        menuItem.setEnabled(false);

    }

    @Override
    public void activate() {
        MenuItem menuItem = menu.findItem(MENU_ID);
        menuItem.setEnabled(true);

    }
}
