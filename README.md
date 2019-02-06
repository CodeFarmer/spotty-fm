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

If you omit the SPOTIFY_AUTH_TOKEN variable setting, the user auth step happens automatically.
