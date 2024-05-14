// Put your name and ID here 

const express = require("express");
const path = require("path");

const app = express();

app.use(express.static(
  	path.resolve(__dirname, "public")
));

// Open the database
const db = require('better-sqlite3')('./top40.db');
db.pragma('journal_mode = WAL');

/**
 * Function that returns the the unique artists in the database.
 */
app.get("/load", (req, res) => {
	res.json(db.prepare("SELECT DISTINCT artist FROM songlist").all());
});

/**
 * Function that returns the songs that match the search criteria.
 */
app.get("/search", (req, res) => {
	var rows;
	var count;
	if (req.query.artist == "select an artist" && req.query.title == "") { // no artist or title specified
		rows = db.prepare(`SELECT * FROM songlist LIMIT ? OFFSET ?`).all(req.query.limit, req.query.offset); // get songs
		count = db.prepare(`SELECT COUNT(*) FROM songlist`).get(); // count all songs
	} else if (req.query.title == "") { // no title specified
		rows = db.prepare(`SELECT * FROM songlist WHERE artist=? LIMIT ? OFFSET ?`).all(req.query.artist, req.query.limit, req.query.offset); // get songs by artist
		count = db.prepare(`SELECT COUNT(*) FROM songlist WHERE artist=?`).get(req.query.artist); // count number of songs total by artist
	} else if (req.query.artist == "select an artist") { // no artist specified
		rows = db.prepare(`SELECT * FROM songlist WHERE title LIKE ? LIMIT ? OFFSET ?`).all(`%${req.query.title.toUpperCase()}%`, req.query.limit, req.query.offset); // get songs with title
		count = db.prepare(`SELECT COUNT(*) FROM songlist WHERE title LIKE ?`).get(`%${req.query.title.toUpperCase()}%`); // count number of songs total with title
	} else {// both artist and title specified
		rows = db.prepare(`SELECT * FROM songlist WHERE title LIKE ? AND artist=? LIMIT ? OFFSET ?`).all(`%${req.query.title.toUpperCase()}%`, req.query.artist, req.query.limit, req.query.offset); // get songs with title and artist
		count = db.prepare(`SELECT COUNT(*) FROM songlist WHERE title LIKE ? AND artist=?`).get(`%${req.query.title.toUpperCase()}%`, req.query.artist); // count number of songs total with title and artist
	}
	res.json({numObjects: count['COUNT(*)'], values: [Number(req.query.offset), Number(req.query.offset) + Number(req.query.limit)], data: rows});
});


app.listen(3000, () => console.log("Starting up Top 40 Search on http://localhost:3000/"));
