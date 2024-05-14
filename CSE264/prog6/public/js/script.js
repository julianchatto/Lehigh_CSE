document.addEventListener('DOMContentLoaded', function() {
    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');
    canvas.width = window.innerWidth - 15;
    canvas.height = window.innerHeight - 15;

    // Function to draw the "Launch" button
    function drawLaunchButton() {
        ctx.fillStyle = '#007BFF'; // Bootstrap primary button color
        ctx.fillRect(10, 10, 100, 50); // Position at bottom-left
        ctx.fillStyle = 'black'; // Text color
        ctx.font = '20px Arial';
        ctx.fillText('Launch', 30, 40);
    }

    // Initial drawing
    drawLaunchButton();

    // Function to get random color
    function getRandomColor() {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }

    const tiles = [];

    canvas.addEventListener('click', function(event) {
        // Check if the "Launch" button was clicked
        if (event.clientX >= 10 && event.clientX <= 110 && event.clientY >= 10 && event.clientY <= 60) {
            $.ajax({
                url: '/getword', 
                success: function(word) {
                    const wordButton = {
                        word: word,
                        color: getRandomColor(),
                        x: canvas.width, 
                        y: 0, 
                        vx:  -5, // Horizontal velocity
                        vy: 5 // Vertical velocity
                    };
                    tiles.push(wordButton);
                }
            });
        }
    });

    // Function to update and draw tiles
    function updateTiles() {
        ctx.clearRect(0, 0, canvas.width, canvas.height); // Clear the canvas
        drawLaunchButton(); // Redraw the "Launch" button
 
        tiles.forEach(button => {
            // Update position
            button.x += button.vx;
            button.y += button.vy;

            // Check for collision with canvas boundaries and reverse velocity
            if (button.x <= 0 || button.x >= canvas.width) button.vx *= -1;
            if (button.y <= 0 || button.y >= canvas.height) button.vy *= -1;

            // Draw the tiles
            ctx.fillStyle = button.color;
            ctx.fillRect(button.x, button.y, 100, 50); // Rectangle for the word
            ctx.fillStyle = getColor(button.color); 
            ctx.font = '20px Arial';
            ctx.fillText(button.word, button.x + 10, button.y + 30); // Position text inside the rectangle
        });

        requestAnimationFrame(updateTiles); // Keep updating and drawing
    }

    // This code is from https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color and https://stackoverflow.com/questions/12043187/how-to-check-if-hex-color-is-too-black
    function getColor(color) {
        const r = parseInt(color.slice(1, 3), 16) / 255;
        const g = parseInt(color.slice(3, 5), 16) / 255;
        const b = parseInt(color.slice(5, 7), 16) / 255;

        // Calculate luminance
        const luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;

        // Return black for light backgrounds and white for dark backgrounds
        return luminance > 0.5 ? 'black' : 'white';
    }
    updateTiles(); // Start the animation loop

});