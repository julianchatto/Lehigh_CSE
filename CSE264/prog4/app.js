// Julian Chattopadhyay juc226

const express = require("express");
const path = require("path");

const app = express();

let books = [];

app.use(express.static(
  	path.resolve(__dirname, "public")
));

/**
 * GET request to /load
*/
app.get("/load", (req, res) => {
  	res.end(JSON.stringify(books));
});

/**
 * GET request to /add
*/
app.get("/add", (req, res) => {
	books.push(new Book(req.query.title, req.query.author, req.query.publisher, req.query.year, req.query.genre, req.query.formatOfBook));
	res.end(JSON.stringify(books[books.length - 1]));
});

/**
 * GET request to /list	
 */
app.get("/list", (req, res) => {
	res.end(JSON.stringify(books));
});

app.listen(3000, () => console.log("started on http://localhost:3000/"));


/**
 * Class to represent a book
 * @param {string} title - The title of the book
 * @param {string} author - The author of the book
 * @param {string} publisher - The publisher of the book
 * @param {number} year - The year the book was published
 * @param {string} genre - The genre of the book
 * @param {string[]} formatOfBook - The format(s) of the book
 */
class Book {
  	constructor(title, author, publisher, year, genre, formatOfBook) {
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.year = year;
		this.genre = genre;
		this.formatOfBook = formatOfBook;
  	}
}