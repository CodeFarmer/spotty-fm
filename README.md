spotty-fm
---------

Command-line tool and library for using Spotify and last.fm APIs.

I'll make this better configurable, but for now you need your own
Spotify and last.fm access credentials in the template config.json
file in the root directory.

If you want to do anything requiring user authorization, you'll need
to configure 'authserver' in the spotify section of the JSON file to
point to a running instance of
[spotty-auth-server](http://github.com/CodeFarmer/spotty-auth-server).

#### last.fm

```bash
$ lein run lastfm-user-tag CodeFarmer "greatest songs of all time"
```

```bash
$ lein run lastfm-user-loved CodeFarmer
```

#### spotify

Track search by text:

```bash
$ lein run spotify-search-tracks "Christine and the Queens Tilted"
```

"I'm feeling lucky":

```bash
$ lein run spotify-search-track "Christine and the Queens Tilted"
```

Track search by isrc:

```bash
$ lein run spotify-search-track "isrc:USQE91000035"
{"title":"Rill Rill","artist":"Sleigh Bells","isrc":"USQE91000035","spotify-id":"3GbjOImkNo5nFT2GQxVr11"}
```

NOTE: Getting alist of Spotify isrc fields:

```
$ lein run spotify-search-tracks "Christine and the Queens Tilted" | jq 'map(.isrc)'
[
  "FR6P11500950",
  "FR6P11501170",
  "FR6P11600660",
  "FR6P11601310",
  "FR6P11600620",
  "FR6P11500390",
  "FR6P11600640",
  "FR6P11600680",
  "GBHAR1617201",
  "GBHAR1617202",
  "GBRBE0791292",
  "ESA011623719",
  "USLZJ1692930",
  "GBRBE0791293"
]
```

Reusing spotify authentication token using environment variable:

```bash

$ lein run spotify-auth-token
"BQAIpqoi83ubm8xkhDT0gqWzaGI7eaXjP_v4Q3QOcz5_g3TllIplXTECxYmHjY1wRzBoc3z492ES6kNa0So"

$ SPOTIFY_AUTH_TOKEN=BQAIpqoi83ubm8xkhDT0gqWzaGI7eaXjP_v4Q3QOcz5_g3TllIplXTECxYmHjY1wRzBoc3z492ES6kNa0So lein run spotify-search-track "Sonic Youth Unwind"
{"title":"Unwind","artist":"Sonic Youth","isrc":"USGF19582505","spotify-id":"2i9Ga5iAjizEYUFXTHPOKv"}
```

##### User authorization

```bash
$ lein run spotify-user-auth | jq
{
  "access_token": "A_REALLY_LONG_ACESS_TOKEN",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "A_REALLY_LONG_REFRESH_TOKEN",
  "scope": ""
}
```

A browser window appears and user authorization happens on Spotify

```bash
$ SPOTIFY_AUTH_TOKEN=A_REALLY_LONG_ACCESS_TOKEN lein run spotify-current-user | jq
{
  "display_name": "codefarmer",
  "external_urls": {
    "spotify": "https://open.spotify.com/user/codefarmer"
  },
  "followers": {
    "href": null,
    "total": 1
  },
  "href": "https://api.spotify.com/v1/users/codefarmer",
  "id": "codefarmer",
  "images": [],
  "type": "user",
  "uri": "spotify:user:codefarmer"
}
```

You can ask for specific authorization scopes:

```bash

lein run spotify-user-auth playlist-modify-public playlist-modify-private

```

If you omit the SPOTIFY_AUTH_TOKEN variable setting, the user auth step happens automatically.

NOTE that if you supply the SPOTIFY_AUTH_TOKEN environment variable to
later calls that require specific priveleges, its scope will not be
checked! So make sure you use the right auth call, or Spotify will
refuse permission to do things.

##### playlists

```bash

SPOTIFY_AUTH_TOKEN=A_REALLY_LONG_ACCESS_TOKEN lein run spotify-current-user-playlists | jq

{
  "href": "https://api.spotify.com/v1/users/codefarmer/playlists?offset=0&limit=20",
  "items": [
    {
      "images": [
        {
          "height": 640,
          "url": "https://mosaic.scdn.co/640/23c496a83b3d5ad1bc2873c7bec793dcb12b45c42b59708cd72885507fd342cfe71df55e78b7b218467b198f28674ab3136278d0d79ca6010a6a104794c5cf08ea9783b4b8373e3def893991eb80dcad",
          "width": 640
        },
        {
          "height": 300,
          "url": "https://mosaic.scdn.co/300/23c496a83b3d5ad1bc2873c7bec793dcb12b45c42b59708cd72885507fd342cfe71df55e78b7b218467b198f28674ab3136278d0d79ca6010a6a104794c5cf08ea9783b4b8373e3def893991eb80dcad",
          "width": 300
        },
		
		// ... much more data, but no tracks

```

Retrieve playlist by id:

```bash

SPOTIFY_AUTH_TOKEN=BQDdz9O_E3vro3uT9kxKptdbB98SqRb3YtyaZXevq2YvsCsityOBVpPPmTxa0XV7JK6aq6WhMwbFuOENrgTXbOKz-bba2KTzTk6TUl0Ft-LsOTwiVExdQEXNPUG9QvDiIVC3gE6gklxMqlL6N9lG lein run spotify-get-playlist 6lBLh2ovTqIU1Cxd6zFgU9 | jq

{
  "description": "",
  "images": [
    {
      "height": 640,
      "url": "https://mosaic.scdn.co/640/23c496a83b3d5ad1bc2873c7bec793dcb12b45c42b59708cd72885507fd342cfe71df55e78b7b218467b198f28674ab3136278d0d79ca6010a6a104794c5cf08ea9783b4b8373e3def893991eb80dcad",
      "width": 640
    },
    {
      "height": 300,
      "url": "https://mosaic.scdn.co/300/23c496a83b3d5ad1bc2873c7bec793dcb12b45c42b59708cd72885507fd342cfe71df55e78b7b218467b198f28674ab3136278d0d79ca6010a6a104794c5cf08ea9783b4b8373e3def893991eb80dcad",
      "width": 300
    },
	
	// ... much more data ensues, including tracks

```

Create a playlist:

```bash

$ lein run spotify-create-playlist "Test created playlist" | jq

{
  "description": "Created automatically by spotty-fm",
  "images": [],
  "name": "Test created playlist",
  "snapshot_id": "MSwyOTBlMTNhZDRmZjcyNmM1ZjBhYjc4NGE3Yzk4MWVmNjQ5ODFhNGEx",
  "public": true,
  "tracks": {
    "href": "https://api.spotify.com/v1/playlists/7LIO0XUoThpo6fyAqaXO4W/tracks",
    "items": [],
    "limit": 100,
    "next": null,
    "offset": 0,
    "previous": null,
    "total": 0
  },
  "type": "playlist",
  "collaborative": false,
  "external_urls": {
    "spotify": "https://open.spotify.com/playlist/7LIO0XUoThpo6fyAqaXO4W"
  },
  "id": "7LIO0XUoThpo6fyAqaXO4W",
  "uri": "spotify:user:codefarmer:playlist:7LIO0XUoThpo6fyAqaXO4W",
  "followers": {
    "href": null,
    "total": 0
  },
  "owner": {
    "display_name": "codefarmer",
    "external_urls": {
      "spotify": "https://open.spotify.com/user/codefarmer"
    },
    "href": "https://api.spotify.com/v1/users/codefarmer",
    "id": "codefarmer",
    "type": "user",
    "uri": "spotify:user:codefarmer"
  },
  "href": "https://api.spotify.com/v1/playlists/7LIO0XUoThpo6fyAqaXO4W",
  "primary_color": null
}

```

#### lastfm to spotify

Given a list of lastfm simple-tracks, return them paired with the
results of spotify track searches:

```bash
$ lein run lastfm-user-tag CodeFarmer "ohrwurm" | lein run lastfm-and-spotify | jq
[
  [
    {
      "title": "Royals",
      "artist": "Lorde",
      "mbid": "6f1b86fd-279a-4672-9161-fefb7d000db1"
    },
    {
      "title": "Royals",
      "artist": "Lorde",
      "isrc": "NZUM71200031",
      "spotify-id": "2dLLR6qlu5UJ5gk0dKz0h3"
    }
  ],
  [
    {
      "title": "Bike Dream",
      "artist": "Rostam",
      "mbid": ""
    },
    {
      "title": "Bike Dream",
      "artist": "Rostam",
      "isrc": "USNO11700271",
      "spotify-id": "1acb8u70kClk6NjITaZyuG"
    }
  ],
  [
    {
      "title": "Rill Rill",
      "artist": "Sleigh Bells",
      "mbid": "e55dff8b-16a4-45cf-b9ec-0cd7d01e4e8b"
    },
    {
      "title": "Rill Rill",
      "artist": "Sleigh Bells",
      "isrc": "USQE91000035",
      "spotify-id": "3GbjOImkNo5nFT2GQxVr11"
    }
  ]
  // ...
]
```

Convert a lastfm tag to a list of spotify track ISRCs:

```bash

$ lein run lastfm-user-tag CodeFarmer "ohrwurm" | lein run lastfm-and-spotify | jq 'map(.[1].isrc)'
[
  "NZUM71200031",
  "USNO11700271",
  "USQE91000035",
  null,
  "GBPKZ0700148",
  null,
  "USRE10900198",
  "SEVVX0300201",
  "USSM10013407",
  "USRE19900691",
  "ushm21213023",
  "TCABE1211333",
  "GBKPL1021720",
  "CAG140000038",
  null,
  "GBAEN9700055",
  "CAFB50700202",
  "GBARL1200684",
  "USK110404401",
  "US4CB9910238",
  "GBUM70804501",
  "USIR20080217",
  "USTV10600295",
  "GBUM71603186",
  "USVT10300030",
  "SEBPA0600024",
  "GBARL1201014",
  "USFW40455408",
  null,
  "GBUM70804485"
]

```

Directly creating a Spotify playlists from a last.fm tag:

```bash

$ lein run tag-to-playlist CodeFarmer ohrwurm | jq


  "description": "Created automatically by spotty-fm",
  "images": [
    {
      "height": 640,
      "url": "https://mosaic.scdn.co/640/1bf2f37433589c773fa9e77a231febbcc59de11087870cccd6d0fa2b7507c379f4f98d8f6dfdadc1d715fc9f329671b465cc19c224604616189f691ef06f0658ccc185e6e884fe4af76b217c4fe478c7",
      "width": 640
    },
	
	// and a lot more data :)

```

(This next one can end up running afoul of spotify's rate API limits, dealing with the retry-after header is a near-future intention but) build a playlist for a user's loved tracks:

```bash

$ lein run loved-to-playlist CodeFarmer

```
