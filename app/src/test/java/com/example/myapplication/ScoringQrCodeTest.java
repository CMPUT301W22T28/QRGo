package com.example.myapplication;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import org.junit.Before;
import org.junit.Test;

public class ScoringQRCodeTest {
    private ScoringQRCode qrCode;


    @Before
    public void setup() {
        qrCode = new ScoringQRCode("hash");
    }

    @Test
    public void scoreTest() {

    }

}
