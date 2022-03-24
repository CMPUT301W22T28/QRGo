package com.example.myapplication;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

/**
 * Unit tests for ScoringQRCode class
 *
 * @author Marc-Andre Haley
 *
 * March 10 2022
 */
public class ScoringQRCodeTest {
    private ScoringQRCode scoringQRCode;

    @Before
    public void setup(){
        scoringQRCode = new ScoringQRCode("EWWmkZgTZ3kqRuwcsOks");
    }

    /**
     * Unit test for getGeoHash method
     */
    @Test
    public void getGeoHashTest(){
        scoringQRCode.setLatitude(53.1);
        scoringQRCode.setLongitude(-112.9);
        Assert.assertEquals(scoringQRCode.getGeoHash(), "c3rwnfs9zm");
    }

    /**
     * Unit test for getGeolocationString method
     */
    @Test
    public void getGeolocationStringTest(){
        Assert.assertEquals("Not specified", scoringQRCode.getGeolocationString());
        scoringQRCode.setLatitude(53.1);
        scoringQRCode.setLongitude(-112.9);
        Assert.assertEquals("[53.1, -112.9]", scoringQRCode.getGeolocationString());
    }
}
