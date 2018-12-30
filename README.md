spotty-fm
---------

Command-line tool and library for using Spotify and last.fm APIs.

I'll make this better configurable, but for now you need your own
Spotify and last.fm access credentials in the template config.json
file in the root directory.

#### last.fm

```bash
lein run user-tag CodeFarmer "greatest songs of all time"
```

```bash
lein run user-loved CodeFarmer
```

