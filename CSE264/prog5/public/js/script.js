
$(document).ready(function() {
	/**
	 * Ajax call to load the artists in the drop down
	 */
	$( () => {
		$.ajax(
			"/load",
			{
				type: "GET",
				dataType: "json",
				success: function(data) {
					fillDropDown(data);
				}
			}
		)
	});

	/**
	 * Ajax call to search for the songs
	 */
	$("#search").click((e) => {
		e.preventDefault();
		$("#n1").html(0);
		$("#n2").html(0);
		$("#n3").html(0);
		const limit = Number($("#numToDisplay").val());
		doSearch(limit, Number($("#n2").text()) < limit ? 0 :  limit + Number($("#n1").text()) - 1);
	});
	/**
	 * Ajax call to search for the next songs
	 */
	$("#next").click((e) => {
		e.preventDefault();
		if (Number($("#n2").text()) < Number($("#n3").text())) { // ensure not at end of list
			const limit = Number($("#numToDisplay").val());
			doSearch(limit, Number($("#n2").text()) < limit ? 0 :  limit + Number($("#n1").text()) - 1);
		}
	});
	/**
	 * Ajax call to search for the previous songs
	 */
	$("#prev").click((e) => {
		e.preventDefault();
		if (Number($("#n1").text()) > 1) { // ensure not at front of list
			const limit = Number($("#numToDisplay").val());
			doSearch(limit, Number($("#n1").text()) - limit  - 1 );
		}
	});

	/**
	 * Function to handle the ajax call for searching
	 * @param {Number} limit for the number of rows to display
	 * @param {Number} offset for the start of the search
	 */
	function doSearch(limit, offset) {
		$.ajax(
			"/search",
			{
				type: "GET",
				url: "/search",
				data: {
					artist: $("#artists").val(),
					title: $("#keyword").val(),
					limit: limit,
					offset: offset
				},
				success: function(data) {
					fillTable(data);
				}
			}
		);
	}
});

/**
 * Function to fill the table with the search results
 * @param {JSON} data 
 */
function fillTable(data) {
	// set the number of rows displayed
	$("#n1").html(data.numObjects == 0 ? 0 : Number(data.values[0]) + 1);
	$("#n2").html(data.values[1] > data.numObjects ? data.numObjects : data.values[1]);
	$("#n3").html(data.numObjects);

	$("#songs").empty();
	data.data.forEach((row) => {
		$("#songs").append(`<tr id=${row.numone == 1 ? "numOne" : ""}><td>${row.id}</td><td>${row.title}</td><td>${row.artist}</td></tr>`);
	});
}

/**
 * Function to fill the artist dropdown
 * @param {JSON} data 
 */
function fillDropDown(data) {
	$("#artists").html(`<option value="select an artist">Select an Artist</option>`);
	data.sort((a, b) => { if (a.artist.toLowerCase() < b.artist.toLowerCase()) return -1; else if (a.artist.toLowerCase() > b.artist.toLowerCase()) return 1; return 0;});
	data.forEach((row) => {
		$("#artists").append(`<option value="${row.artist}">${row.artist}</option>`);
	});
}

