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

#### Search Page ####
- Here, you can search for usernames and by clicking on a username, you are directed to that username's profile page.

#### Map Page ####
- The map page allows you to navigate a map where scanned QRCodes will be shown on the map as a pin.
- By clicking on the pin, you are able to see the address and score of that QRCode.

#### Leaderboards Page ####



### Storyboard and App Flowchart ###
