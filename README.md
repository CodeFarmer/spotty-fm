spotty-fm
---------

Command-line tool and library for using Spotify and last.fm APIs.

I'll make this better configurable, but for now you need your own
Spotify and last.fm access credentials in the template config.json
file in the root directory.

#### last.fm

```bash
lein run lastfm-user-tag CodeFarmer "greatest songs of all time"
```

```bash
lein run lastfm-user-loved CodeFarmer
```

#### spotify

Track search by text:

```bash
lein run spotify-search-tracks "Christine and the Queens Tilted"
```

"I'm feeling lucky":

```bash
lein run spotify-search-track "Christine and the Queens Tilted"
```

NOTE: Getting alist of Spotify isrc fields:

```
lein run spotify-search-tracks "Christine and the Queens Tilted" | jq 'map(.isrc)'
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

#### lastfm to spotify

Convert a lastfm tag to a list of spotify track ISRCs:

```bash

lein run lastfm-user-tag CodeFarmer "ohrwurm" | lein run lastfm-and-spotify | jq 'map(.[1].isrc)'
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
