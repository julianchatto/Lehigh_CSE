let pubs = new Set();
let auths = new Set();


/**
 * Function to load the rows of the table with the books
 * @param {Book[]} books 
 */
function loadBookRows(books) {
	$("#books").empty();
	$("#rowTitle").empty();
	if (books.length == 0) { // If there are no books, then return
		return;
	}
	$("#rowTitle").append(`<tr><th>Title</th><th>Author</th><th>Publisher</th><th>Year</th><th>Genre</th><th>Type</th></tr>`);
	books.forEach((book, index) => {
		let str = "";
		let count = 0;
		for (let i = 0; i < book.formatOfBook.length; i++) { // Count the number of formats
			if (book.formatOfBook[i] != "") {
				count++;
			}
		}
		for (let i = 0; i < book.formatOfBook.length; i++) { // Add the book formats to the string
			if (book.formatOfBook[i] != "" && count > 1) {
				str += book.formatOfBook[i] + ", ";
				count--;
			} else if (book.formatOfBook[i] != "") { // for the last book format
				str += book.formatOfBook[i];
			}
		}

		$("#books").append(`<tr><td>${book.title}</td><td>${book.author}</td><td>${book.publisher}</td><td>${book.year}</td><td>${book.genre}</td><td>${str}</td></tr>`)
	});
}

function loadDropDowns(books) {
	books.forEach((book) => {
		if (book.publisher != "" && !pubs.has(book.publisher)) { // Add the publisher to the dropdown iff it is not already there and a publisher is entered
			$("#publisherSelect").append(`<option value ="${book.publisher}">${book.publisher}</option>`);
			pubs.add(book.publisher);
		}
		if (book.author != "" && !auths.has(book.author)) { // Add the author to the dropdown iff it is not already there and a author is entered
			$("#authorSelect").append(`<option value ="${book.author}">${book.author}</option>`);
			auths.add(book.author);
		}
	});

}


/**
 * Function to reset all of the form elements and add publisher and author to dropdowns
 * @param {Book[]} book 
 */
function clearElementsAndAddToDropDown(book) {
	$("#title").val("");
	$("#author").val("");
	$("#publisher").val("");
	$("#year").val("");
	$("#genre").val("");
	$("#publisherSelect").val("select");
	$("#authorSelect").val("select");
	$("#hardcover").prop("checked", false);
	$("#paperback").prop("checked", false);
	$("#ebook").prop("checked", false);
	$("#audio").prop("checked", false);

	if (book.publisher != "" && !pubs.has(book.publisher)) { // Add the publisher to the dropdown iff it is not already there and a publisher is entered
		$("#publisherSelect").append(`<option value ="${book.publisher}">${book.publisher}</option>`);
		pubs.add(book.publisher);
	}
	if (book.author != "" && !auths.has(book.author)) { // Add the author to the dropdown iff it is not already there and a author is entered
		$("#authorSelect").append(`<option value ="${book.author}">${book.author}</option>`);
		auths.add(book.author);
	}
}

$(document).ready(function() {	
	/**
	 * On page load, load the books from the server
	 */
	$( () => {
		$.ajax(
			"/load",
			{
				type: "GET",
				dataType: "json",
				success: function (tasks) {
					loadBookRows(tasks);
					loadDropDowns(tasks);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					alert("Error: " + jqXHR.responseText);
					alert("Error: " + textStatus);
					alert("Error: " + errorThrown);
				}
			}
		);

	});


	/**
	 * On the add button click, add the book to the server
	 */
	$("#add").click(() => {
		$.ajax(
			"/add",
			{
				type: "GET",
				processData: true,
				data: {
					title: $("#title").val(),
					// Determine if author/publisher is selected from dropdown
					author: $("#authorSelect").val() != "select" ? $("#authorSelect").val() : $("#author").val(), 
					publisher: $("#publisherSelect").val() != "select" ? $("#publisherSelect").val() : $("#publisher").val(),
					year: $("#year").val(),
					genre: $("#genre").val(),
					formatOfBook: [
						$("#hardcover").is(":checked") ? "Hardcover" : "",
						$("#paperback").is(":checked") ? "Paperback" : "",
						$("#ebook").is(":checked") ? "eBook" : "",
						$("#audio").is(":checked") ? "Audio" : ""
					]
				},
				dataType: "json",
				success: function (book) {
					clearElementsAndAddToDropDown(book);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					alert("Error: " + jqXHR.responseText);
					alert("Error: " + textStatus);
					alert("Error: " + errorThrown);
				}
			}
		);
	});
	
	/**
	 * On the list button click, load the books from the server
	 */
	$("#list").click(() => {
		$.ajax(
			"/list",
			{
				type: "GET",
				processData: true,
				dataType: "json",
				success: function (books) {
					loadBookRows(books);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					alert("Error: " + jqXHR.responseText);
					alert("Error: " + textStatus);
					alert("Error: " + errorThrown);
				}
			}
		);

	});
});