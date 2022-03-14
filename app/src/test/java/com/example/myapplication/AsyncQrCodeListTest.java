package com.example.myapplication;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.fragments.profile.AsyncQrCodeList;
import com.example.myapplication.fragments.profile.ProfileEventListeners;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Testing the AsynQrCodeList for proper functionality
 *
 * @author Walter Ostrander
 *
 * May 12 2022
 */
public class AsyncQrCodeListTest implements ProfileEventListeners {
    private AsyncQrCodeList asyncQrCodeList;
    private int myMax = 5;
    private ArrayList<ScoringQRCode> qrCodes;
    final Object syncObject = new Object();
    private boolean hasEventTriggered;

    /**
     * The test must initialize this method that is the event listener
     *
     * @param qrCodes The list of qr codes that was filled asynchronously
     */
    @Override
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes) {
        this.qrCodes = qrCodes;
        hasEventTriggered = true;
    }

    /**
     * Before every test, setup runs to created a new AsyncQrCodeList and reset hasEventTriggered
     */
    @Before
    public void setup() {
        asyncQrCodeList = new AsyncQrCodeList(myMax, this);
        hasEventTriggered = false;
    }

    /**
     * Test if the asyncQrcode list triggers after sending myMax number of new qr codes.
     */
    @Test
    public void TestTrigger() {
        for (int i = 0; i < myMax; i++) {
            asyncQrCodeList.addToArray(new ScoringQRCode("hash"));
        }
        Assert.assertTrue(hasEventTriggered);
    }

    /**
     * Test if the array size is as it should be.
     */
    @Test
    public void arraySizeTest() {
        for (int i = 0; i < myMax; i++) {
            asyncQrCodeList.addToArray(new ScoringQRCode("hash"));
        }
        Assert.assertEquals(this.qrCodes.size(), myMax);
    }

    /**
     * Test to see if the array elements fill as expected.
     */
    @Test
    public void arrayElementTest() {
        ArrayList<ScoringQRCode> testCodes = new ArrayList<>();
        for (int i = 0; i < myMax; i++) {
            ScoringQRCode code = new ScoringQRCode("hash" + i);
            asyncQrCodeList.addToArray(code);
            testCodes.add(code);
        }
        for (int i = 0; i < myMax; i++) {
            Assert.assertEquals(testCodes.get(i), qrCodes.get(i));
        }
    }
}
