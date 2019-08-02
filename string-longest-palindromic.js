function longestPalindromic(str) {
    if (!str || str.length == 0) return "";
    var start = 0, end = 0;

    for (var i = 0; i < str.length; i++) {
        var len1 = expandAroundCenter(str, i, i);
        var len2 = expandAroundCenter(str, i, i + 1);

        var max = Math.max(len1, len2);
        if (max > end - start) {
            start = i - Math.floor((max - 1) / 2);
            end = i + Math.floor(max / 2);
        }
    }

    return str.substring(start, end + 1);
}

function expandAroundCenter(str, left, right) {
    while (left >= 0 && right < str.length && str[left] == str[right]) {
        left--;
        right++;
    }

    return right - left - 1;
}


// test
console.log(longestPalindromic("abbba") == "abbba");
console.log(longestPalindromic("abba") == "abba");
console.log(longestPalindromic("aba") == "aba");
console.log(longestPalindromic("cabbad") == "abba");
console.log(longestPalindromic("This is racecar and madam, but the number is 121”.") == " racecar ");
