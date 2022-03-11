package com.example.myapplication;


import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerDataClassTest {
    private Player player;

    @Before
    public void setup() {
        player = new Player("player", false);
    }

    @Test
    public void isAdminTest() {
        // assert the player is not an admin
        Assert.assertFalse(player.isAdmin());

        // assert the admin is an admin
        Assert.assertTrue((new Player("admin", true)).isAdmin());
    }

    @Test
    public void usernameTest() {
        Assert.assertEquals(player.getUsername(), "player");
        player.setUsername("Nicholas");
        Assert.assertEquals(player.getUsername(), "Nicholas");
    }

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

    @Test
    public void totalScoreTest() {
        int totalScore = 3;
        player.setTotalScore(totalScore);
        Assert.assertEquals(player.getTotalScore(), totalScore);
    }

    @Test
    public void highestScoreTest() {
        int highestScore = 34000;
        player.setHighestScore(highestScore);
        Assert.assertEquals(highestScore, player.getHighestScore());
    }

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
