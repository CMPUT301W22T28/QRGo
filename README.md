# QRGo #

### Description ###
This is an application that follows in the same premise as PokemonGo, however with QRCodes. Users navigate their way to QRCodes and scan them to score points and compete with other players.

### Members ### 
- Amro Amanuddein
- Walter Ostrander
- Dawood Ali
- Sankalp Saini
- Marc-Andre Haley
- Ervin Binu Joseph

### Demo Video ###
 [![Project 0a: Degrees | CS50 AI 2020](http://img.youtube.com/vi/Zrwvpql3P4o/0.jpg)](https://youtu.be/Zrwvpql3P4o)



### Features (User Stories) ###

#### Login ####
- Automatic login to existing account through device recognition.
- Signing up page with some bulletproofing in terms of checking for existing usernames.
- Can login to your existing account if you are using a different device through a Login QRCode.

#### Camera Page #####
- Click on the camera icon to open the camera so you can scan QRCodes.
- If you try to scan a Scoring QRCode that has you've already scanned, it won't allow that action (error message will be displayed).
- If you try to scan a Login QRCode, it won't allow that action (error message will be displayed)
- If you try to scan a Profile QRCode, you will be directed to that players profile.
- Once you scan a Scoring QR Code that you've never scanned before, it's score will be displayed and you will be prompted to choose if you wish to save a picture of the qrcode and its geolocation.
- If you choose to save the picture, it will be compressed and the image size will be displayed.
- Upon clicking Save button, the QRCode will be saved to your profile.

#### Profile Page ####
- Can view your own profile's username, and some summary statistics including: total score, total qrcodes (count) scanned and the top scoring qrcode you scanned.
- Can view a list of the different qrcodes that you scanned (the image you took, if any and the score of that qrcode) sorted by highest score in ascending order.
- If you click on one of the qrcodes it will take you to the post created upon scanning the qrcode.
- On this page, you can generate your Login QRCode which will allow you to Login to your account on other devices.
- You can also generate your Profile QRCode which if your friends scan it, it will direct you to your profile and they can see all your stats.
- **NOTE:** If you have admin privileges, you will be able to delete/remove users once you're on their profile page. 

#### Post page ####
- After clicking on one of the qrcodes listed on a player's profile, you will be directed to the post page.
- On the post page, you can view the post of the scoring qrcode itself which may/may not have an image saved (depending on what the user chose when saving the qrcode). *NOTE* that only users which have scanned this qrcode can see the image (if there is one) this adds some element of mystery and it prevents users that haven't scannned the qrcode from screenshotting and scanning the image. 
- On the post tab also they can see the number of players that scanned this qrcode and the geolocation.
- Users on this page can also see the comments section and they can add comments. 
- Users on this page can see which users have scanned this qrcode.
- **NOTE:** If you have admin privileges, you will be able to delete/remove scoring qrcode posts.

#### Search Page ####
- Here, you can search for usernames and by clicking on a username, you are directed to that username's profile page.

#### Map Page ####
- The map page allows you to navigate a map where scanned QRCodes will be shown on the map as a pin.
- By clicking on the pin, you are able to see the address and score of that QRCode.

#### Leaderboards Page ####
- This page contains three tabs that allow you to sort the player rankings by highest scoring qr code, the count (number of qrcodes scanned) and the sum (sum of all qrcodes scanned). 
- By clicking on any of the players on the leaderboard, you will be directed to their profile.


