package com.onaio.steps.model.ODKForm.strategy;

import android.content.Intent;
import android.net.Uri;

import com.onaio.steps.activities.HouseholdActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.helper.FileUtil;
import com.onaio.steps.model.Gender;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;
import com.onaio.steps.model.Member;
import com.onaio.steps.model.ODKForm.IForm;
import com.onaio.steps.model.ODKForm.ODKForm;
import com.onaio.steps.model.RequestCode;
import com.onaio.steps.model.ShadowDatabaseHelper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 29/09/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 16, manifest = "src/main/AndroidManifest.xml",shadows = {ShadowDatabaseHelper.class})
public class HouseholdMemberFormStrategyTest {
    private FileUtil fileUtilMock;
    private HouseholdActivity householdActivity;
    private Member selectedMember;
    private Household householdMock;
    private ODKForm odkForm;
    private IForm blankFormMock;

    private final int HHID_KEY = 1;
    private final String MEMBER_ID = "householdID-1";
    private final String SURNAME = "surname";
    private final String FIRST_NAME = "firstname";
    private final Gender GENDER = Gender.Male;
    private final int AGE = 28;
    private final String DEVICE_ID = "test_dev_id";

    @Before
    public void Setup(){
        stubHousehold();
        stubFileUtil();

        Intent intent = new Intent();
        Mockito.stub(householdMock.getPhoneNumber()).toReturn("8050342");
        Mockito.stub(householdMock.getComments()).toReturn("dummy comments");
        intent.putExtra(Constants.HH_HOUSEHOLD,householdMock);

        householdActivity = Robolectric.buildActivity(HouseholdActivity.class).withIntent(intent).create().get();
        blankFormMock = Mockito.mock(IForm.class);
    }

    private void stubFileUtil() {
        fileUtilMock = Mockito.mock(FileUtil.class);
        Mockito.stub(fileUtilMock.withData(Mockito.any(String[].class))).toReturn(fileUtilMock);
        Mockito.stub(fileUtilMock.withHeader(Mockito.any(String[].class))).toReturn(fileUtilMock);
    }

    private void stubHousehold() {
        householdMock = Mockito.mock(Household.class);
        selectedMember = new Member(HHID_KEY, SURNAME, FIRST_NAME, GENDER, AGE, householdMock, MEMBER_ID, false);
        Mockito.stub(householdMock.getSelectedMember(Mockito.any(DatabaseHelper.class))).toReturn(selectedMember);
        Mockito.stub(householdMock.getStatus()).toReturn(InterviewStatus.SELECTION_NOT_DONE);
    }

    @Test
    public void testSaveDataFile() throws IOException {
        //mock launch ODK with the household member
        String blankFormMediaPath = householdActivity.getFilesDir().getPath();
        String householdName = "household name";
        Uri blankFormURI = Uri.parse("uri");
        Mockito.stub(householdMock.getName()).toReturn(householdName);
        Mockito.stub(blankFormMock.getPath()).toReturn(blankFormMediaPath);
        Mockito.stub(blankFormMock.getUri()).toReturn(blankFormURI);
        odkForm = new ODKForm(blankFormMock, null);

        odkForm.open(new HouseholdMemberFormStrategy(householdMock, DEVICE_ID), householdActivity, RequestCode.SURVEY.getCode());

        Robolectric.shadowOf(householdActivity).getNextStartedActivityForResult();

        //see if generated csv file has the required columns
        File formMediaDir = new File(blankFormMediaPath);
        String absolutePath = new File(formMediaDir, Constants.ODK_DATA_FILENAME).getAbsolutePath();
        FileUtil fileUtil = new FileUtil();
        List<String[]> lines = fileUtil.readFile(absolutePath);
        String[] csvExpectedValues = new String[]{
                String.valueOf(HHID_KEY),
                null,
                MEMBER_ID,
                SURNAME,
                FIRST_NAME,
                String.valueOf(GENDER.getIntValue()),
                String.valueOf(AGE),
                null,
                DEVICE_ID
        };
        for(String[] curLine : lines) {
            for(int i = 0; i < csvExpectedValues.length; i++) {
                if(csvExpectedValues[i] != null) {
                    Assert.assertEquals(csvExpectedValues[i], curLine[i]);
                }
            }
        }
    }
}