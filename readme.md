CURRENTLY NOT WORKING, APPLE UPDATED THEIR WEB DESIGN AND BROKE THIS PROJECT ENTIRELY

[EXECUTABLE DOWNLOAD LINK](https://github.com/pizzaboy314/iTunes-tracklist-parser/raw/master/iTunes-tracklist-parser.jar)
---
[RYM](https://rateyourmusic.com/) (soon to be Sonemic) is a music site much like IMDb for rating, cataloging, and finding new music.
the advanced tracklist input for adding a new album to the site is of the format:

TRACK NUM|TRACK NAME|TRACK DURATION

i was using iTunes as a source for a while, and getting it into that format through text manipulation.   
but that's stupid so I wrote this app, which gives you artist name, album title, release date, the full tracklist in the correct format, and automatically downloads the album artwork in high resolution.

example
---

given an input of https://itunes.apple.com/us/album/innerspeaker/id1087528731 the application will output:   

```
Tame Impala   
InnerSpeaker   
Aug 16, 2011   

1|It Is Not Meant to Be|5:22   
2|Desire Be Desire Go|4:26   
3|Alter Ego|4:47   
4|Lucidity|4:31   
5|Why Won't You Make up Your Mind?|3:19   
6|Solitude Is Bliss|3:55   
7|Jeremy's Storm|5:28   
8|Expectation|6:02   
9|The Bold Arrow of Time|3:48   
10|Runway Houses City Clouds|7:15   
11|I Don't Really Mind|3:46   

Source URL:   
https://itunes.apple.com/us/album/innerspeaker/id1087528731

Artwork downloaded to Downloads/covers.
```
