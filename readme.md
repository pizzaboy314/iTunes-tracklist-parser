No longer officially supported; replaced by [this](https://pizzaboy314.github.io/SonemicAppleAlbumParser/) web project
---
[EXECUTABLE DOWNLOAD LINK](https://github.com/pizzaboy314/iTunes-tracklist-parser/raw/master/iTunes-tracklist-parser.jar)

[RYM](https://rateyourmusic.com/) (soon to be Sonemic) is a music site much like IMDb for rating, cataloging, and finding new music.
the advanced tracklist input for adding a new album to the site is of the format:

TRACK NUM|TRACK NAME|TRACK DURATION

Using a Apple Music album URL as a source, this app gives you the artist name, album title, release date, album type, the full tracklist in the correct format, and a full list of featured artists. It also automatically downloads the album artwork in high resolution.

example
---

given an input of https://music.apple.com/us/album/to-pimp-a-butterfly/974187289 the application will output:   

```
Kendrick Lamar
To Pimp a Butterfly
Mar 16, 2015

Type: Album

1|Wesley's Theory|4:47
2|For Free? (Interlude)|2:10
3|King Kunta|3:54
4|Institutionalized|4:31
5|These Walls|5:00
6|u|4:28
7|Alright|3:39
8|For Sale? (Interlude)|4:51
9|Momma|4:43
10|Hood Politics|4:52
11|How Much a Dollar Cost|4:21
12|Complexion (A Zulu Love)|4:23
13|The Blacker the Berry|5:28
14|You Ain't Gotta Lie (Momma Said)|4:01
15|i|5:36
16|Mortal Man|12:07

Track Features
George Clinton:   1
Thundercat:       1,5
James Fauntleroy: 11
Ronald Isley:     11
Rapsody:          12
Snoop Dogg:       4
Bilal:            4,5
Anna Wise:        4,5

Source URL:
https://itunes.apple.com/us/album/to-pimp-a-butterfly/974187289

Artwork automatically downloaded to Downloads/covers.
```
