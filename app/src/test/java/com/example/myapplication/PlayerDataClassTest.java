package com.example.myapplication;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Class to test proper functionality of the Player class
 *
 * @author Walter Ostrander
 *
 * May 12, 2022
 */
public class PlayerDataClassTest {
    private Player player;

    /**
     * Resetting the player before every test.
     */
    @Before
    public void setup() {
        player = new Player("player", false);
    }

    /**
     * Checking if the isAdmin method works properly.
     */
    @Test
    public void isAdminTest() {
        // assert the player is not an admin
        Assert.assertFalse(player.isAdmin());

        // assert the admin is an admin
        Assert.assertTrue((new Player("admin", true)).isAdmin());
    }

    /**
     * Checking if setting and getting the username works.
     */
    @Test
    public void usernameTest() {
        Assert.assertEquals(player.getUsername(), "player");
        player.setUsername("Nicholas");
        Assert.assertEquals(player.getUsername(), "Nicholas");
    }

    /**
     * Testing the size of the qrCode list inside before and after adding elements.
     */
    @Test
    public void qrCodeListTest() {
        ArrayList<ScoringQRCode> list = player.getQrCodes();
        int size = 0;
        Assert.assertEquals(list.size(), size);

        ScoringQRCode qrCode = new ScoringQRCode("hash");
        player.addScoringQRCode(qrCode);
        list = player.getQrCodes();
        Assert.assertEquals(list.size(), size+1);
        Assert.assertEquals(list.get(0), qrCode);
    }

    /**
     * Testing the total score.
     */
    @Test
    public void totalScoreTest() {
        int totalScore = 3;
        player.setTotalScore(totalScore);
        Assert.assertEquals(player.getTotalScore(), totalScore);
    }

    /**
     * Testing the highest score.
     */
    @Test
    public void highestScoreTest() {
        int highestScore = 34000;
        player.setHighestScore(highestScore);
        Assert.assertEquals(highestScore, player.getHighestScore());
    }

    /**
     * Testing to see if the qrCode has proper count.
     */
    @Test
    public void qrCodeCountTest() {
        player.resetQrCodeList();
        int numQrCodes = 45;
        for (int i = 0; i < numQrCodes; i++) {
            player.addScoringQRCode(new ScoringQRCode("hash"));
        }
        Assert.assertEquals(player.getQRCodeCount(), numQrCodes);
    }
}
