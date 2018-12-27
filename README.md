spotty-fm
---------

Command-line tool and library for using Spotify and last.fm APIs.

I'll make this better configurable, but for now you need your own
Spotify and last.fm access credentials, a last.fm.json file containing
something like this:

```
{
    "name": "spotty-fm",
    "apikey": "YOUR_API_KEY",
    "secret": "YOUR_SECRET_HERE",
    "account": "YOUR_ACCOUNT_HERE"
}
```

and a spotify.json file like this:

```
{
    "clientid": "YOUR_SPOTIFY_CLIENT_ID",
    "secret":   "YOUR_SPOTIFY_SECRET"
}

```


*last.fm*:

```bash
lein run user-tag CodeFarmer "greatest songs of all time"
```

```bash
lein run user-loved CodeFarmer
```

