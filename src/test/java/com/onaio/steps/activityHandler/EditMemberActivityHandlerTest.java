package com.onaio.steps.activityHandler;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.onaio.steps.R;
import com.onaio.steps.activity.EditMemberActivity;
import com.onaio.steps.activity.MemberActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.HouseholdStatus;
import com.onaio.steps.model.Member;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Config(emulateSdk = 16,manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class EditMemberActivityHandlerTest {

    private MemberActivity memberActivityMock;
    private Member memberMock;
    private EditMemberActivityHandler editMemberActivityHandler;

    @Before
    public void setup(){
        memberActivityMock = Mockito.mock(MemberActivity.class);
        memberMock= Mockito.mock(Member.class);
        editMemberActivityHandler = new EditMemberActivityHandler(memberActivityMock, memberMock);
    }

    @Test
    public void ShouldHandleResultForResultOkCode(){
        Intent intentMock = Mockito.mock(Intent.class);
        editMemberActivityHandler.handleResult(intentMock, Activity.RESULT_OK);
        Mockito.verify(memberActivityMock).finish();
    }

    @Test
    public void ShouldBeAbleToOpenEditMemberActivityWhenMenuIdMatches(){
        assertTrue(editMemberActivityHandler.shouldOpen(R.id.action_edit));
    }

    @Test
    public void ShouldNotBeAbleToOpenEditMemberActivityForOtheRMenuId(){
        assertFalse(editMemberActivityHandler.shouldOpen(R.id.action_settings));
    }

    @Test
    public void ShouldCheckWhetherResultForProperRequestCodeCanBeHandled(){
        assertTrue(editMemberActivityHandler.canHandleResult(Constants.EDIT_MEMBER_IDENTIFIER));
    }

    @Test
    public void ShouldCheckWhetherResultForOtherRequestCodeCanNotBeHandled(){
        assertFalse(editMemberActivityHandler.canHandleResult(Constants.NEW_MEMBER_IDENTIFIER));
    }

    @Test
    public void ShouldOpenWhenMemberIsNotNull(){
        editMemberActivityHandler.open();
        Mockito.verify(memberActivityMock).startActivityForResult(Mockito.argThat(matchIntent()), Mockito.eq(Constants.EDIT_MEMBER_IDENTIFIER));
    }

    private ArgumentMatcher<Intent> matchIntent() {
        return new ArgumentMatcher<Intent>() {
            @Override
            public boolean matches(Object argument) {
                Intent intent = (Intent) argument;
                Member actualMember = (Member) intent.getSerializableExtra(Constants.MEMBER);
                Assert.assertEquals(memberMock, actualMember);
                Assert.assertEquals(EditMemberActivity.class.getName(),intent.getComponent().getClassName());
                return true;
            }
        };
    }

    @Test
    public void ShouldInactivateEditOptionForSelectedMember(){
        Menu menuMock = Mockito.mock(Menu.class);
        Household household = new Household("1234", "any name", "123456789", "1", HouseholdStatus.NOT_SELECTED, "");
        Mockito.stub(memberMock.getHousehold()).toReturn(household);
        Mockito.stub(memberMock.getId()).toReturn(1);

        assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldInactivate());
    }

    @Test
    public void ShouldBeAbleToActivateEditOptionInMenuItem(){
        Menu menuMock = Mockito.mock(Menu.class);
        MenuItem menuItemMock = Mockito.mock(MenuItem.class);
        Mockito.stub(menuMock.findItem(R.id.action_edit)).toReturn(menuItemMock);

        editMemberActivityHandler.withMenu(menuMock).activate();

        Mockito.verify(menuItemMock).setEnabled(true);
    }

    @Test
    public void ShouldBeAbleToInactivateEditOptionInMenuItem(){
        Menu menuMock = Mockito.mock(Menu.class);
        MenuItem menuItemMock = Mockito.mock(MenuItem.class);
        Mockito.stub(menuMock.findItem(R.id.action_edit)).toReturn(menuItemMock);

        editMemberActivityHandler.withMenu(menuMock).inactivate();

        Mockito.verify(menuItemMock).setEnabled(false);
    }
    }
