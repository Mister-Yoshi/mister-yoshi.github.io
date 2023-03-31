// Get elements to be updated
var body = document.body;
var logo = document.querySelector('.logo-link img');
var searchBar = document.querySelector('.search-bar');
var buttons = document.querySelectorAll('.button');
var quoteBox = document.querySelector('.quote-box');
var footer = document.querySelector('footer');

// Dark mode toggle function
function toggleDarkMode() {
  body.classList.toggle('dark');
  logo.classList.toggle('dark');
  searchBar.classList.toggle('dark');
  quoteBox.classList.toggle('dark');
  footer.classList.toggle('dark');

  buttons.forEach(function(button) {
    button.classList.toggle('dark');
  });
}

// Array of James Dean quotes
var quotes = [
  "Dream as if you'll live forever. Live as if you'll die today.",
  "The gratification comes in the doing, not in the results.",
  "To me, acting is the most logical way for people's neuroses to manifest themselves, in this great need we all have to express ourselves.",
  "If a man can bridge the gap between life and death, if he can live on after he's dead, then maybe he was a great man.",
  "Only the gentle are ever really strong.",
  "It's not the size of the dog in the fight, it's the size of the fight in the dog.",
  "Take it easy driving– the life you save may be mine.",
  "The only way to make sense out of change is to plunge into it, move with it, and join the dance.",
  "I'm not going to change the way I look or the way I feel to conform to anything. I've always been a freak. So I've been a freak all my life and I have to live with that, you know. I'm one of those people."
];

// Display a random quote on page load
window.onload = function() {
  var quote = quotes[Math.floor(Math.random() * quotes.length)];
  document.getElementById("quote").innerHTML = quote;
};
