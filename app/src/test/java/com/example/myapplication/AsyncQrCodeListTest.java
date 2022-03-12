package com.example.myapplication;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.ui.profile.AsyncQrCodeList;
import com.example.myapplication.ui.profile.ProfileEventListeners;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class AsyncQrCodeListTest implements ProfileEventListeners {
    private AsyncQrCodeList asyncQrCodeList;
    private int myMax = 5;
    private ArrayList<ScoringQRCode> qrCodes;
    final Object syncObject = new Object();
    private boolean hasEventTriggered;


    @Override
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes) {
        this.qrCodes = qrCodes;
        hasEventTriggered = true;
    }

    @Before
    public void setup() {
        asyncQrCodeList = new AsyncQrCodeList(myMax, this);
        hasEventTriggered = false;
    }

    @Test
    public void TestTrigger() {
        for (int i = 0; i < myMax; i++) {
            asyncQrCodeList.addToArray(new ScoringQRCode("hash"));
        }
        Assert.assertTrue(hasEventTriggered);
    }

    @Test
    public void arraySizeTest() {
        for (int i = 0; i < myMax; i++) {
            asyncQrCodeList.addToArray(new ScoringQRCode("hash"));
        }
        Assert.assertEquals(this.qrCodes.size(), myMax);
    }

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
