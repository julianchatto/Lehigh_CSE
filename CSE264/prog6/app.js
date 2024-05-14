// Julian Chattopadhyay juc226

const express = require("express");
const fs = require("fs").promises; // Import the promises API for fs
const path = require("path");

const app = express();

app.use(express.static(
  path.resolve(__dirname, "public")
));


let words = [];

// Load the list of words from the file
fs.readFile(path.resolve(__dirname, "words.txt"), 'utf8')
	.then(data => {
		words = data.split('\r\n'); 
	})
	.catch(err => {
		console.error("Failed to read file:", err);
	});

app.use(express.static(path.resolve(__dirname, "public")));

// Create a router that returns a random word
app.get('/getword', (req, res) => {
	res.send(words[Math.floor(Math.random() * words.length)]);
});

app.listen(3000, () => console.log("Starting up on http://localhost:3000"));