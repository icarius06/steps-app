package com.onaio.steps.activityHandler;

import android.app.Activity;
import android.content.Intent;

import com.onaio.steps.R;
import com.onaio.steps.activity.HouseholdActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.model.Household;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Config(emulateSdk = 16,manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class EditHouseholdActivityHandlerTest {

    private final int MENU_ID = R.id.action_edit;
    @Mock
    private HouseholdActivity activityMock;
    @Mock
    private Household householdMock;
    private EditHouseholdActivityHandler editHouseholdActivityHandler;

    @Before
    public void Setup(){
        activityMock = mock(HouseholdActivity.class);
        householdMock = mock(Household.class);
        editHouseholdActivityHandler = new EditHouseholdActivityHandler(activityMock, householdMock);
    }

    @Test
    public void ShouldBeAbleToOpenWhenMenuIdMatches(){
        assertTrue(editHouseholdActivityHandler.shouldOpen(MENU_ID));
    }

    @Test
    public void ShouldNotBeAbleToOpenForDifferentMenuId(){
        assertFalse(editHouseholdActivityHandler.shouldOpen(R.id.action_refused));
    }

    @Test
    public void ShouldPopForConfirmationWhenOpened(){
        editHouseholdActivityHandler.open();

        verify(activityMock).startActivityForResult(Mockito.argThat(householdIntentMatcher()), Mockito.eq(Constants.EDIT_HOUSEHOLD_IDENTIFIER));
    }

    private ArgumentMatcher<Intent> householdIntentMatcher() {
        return new ArgumentMatcher<Intent>() {
            @Override
            public boolean matches(Object argument) {
                Intent intent = (Intent) argument;
                Household householdActual = (Household) intent.getSerializableExtra(Constants.HOUSEHOLD);
                assertEquals(householdMock,householdActual);
                return true;
            }
        };
    }

    @Test
    public void ShouldBeAbleToHandleResultForEditHouseholdIdentifier(){
        assertTrue(editHouseholdActivityHandler.canHandleResult(Constants.EDIT_HOUSEHOLD_IDENTIFIER));
    }

    @Test
    public void ShouldNotBeAbleToHandleResultForOtherIdentifier(){
        assertFalse(editHouseholdActivityHandler.canHandleResult(Constants.NEW_HOUSEHOLD_IDENTIFIER));
    }

    @Test
    public void ShouldFinishTheActivityWhenResultHandledWithOkResult(){
        editHouseholdActivityHandler.handleResult(new Intent(),Activity.RESULT_OK);

        verify(activityMock).finish();
    }

    @Test
    public void ShouldNotFinishTheActivityWhenResultHandledForOtherResult(){
        editHouseholdActivityHandler.handleResult(new Intent(), Activity.RESULT_CANCELED);

        verify(activityMock,never()).finish();
    }

}