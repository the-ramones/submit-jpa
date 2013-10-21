/* Reports!
 * Library for formating dates. Usefull when Datepicker functionality cannot be
 * applied (for example, with <label>, <span> HTML elements)
 */

/*
 * Locale-holder cookie name
 * @type String
 */
var reports_locale_cookie_name = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE";

/*
 * Check User locale. Hardcoded dependency
 */
function getUserLocale() {
    var cookies = document.cookie.split(';');
    var locale;
    var c_length = cookies.length;
    for (var i = 0; i < c_length; i++) {
        cookie = cookies[i];
        if (cookie.indexOf(reports_locale_cookie_name) !== -1) {
            locale = cookie.split('=')[1];
            break;
        }
    }
    if (locale) {
        return locale;
    } else {
        return "en";
    }
}

var localized_months = {
    "default": ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    "en": ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    "ru": ['Янв', 'Дек', 'Мар', 'Апр', 'Мая', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']
};

var localized_months_full = {
    "default": ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
    "en": ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
    "ru": ['Января', 'Декабря', 'Марта', 'Апреля', 'Мая', 'Июня', 'Июля', 'Августа', 'Сентября', 'Октябрь', 'Ноябрь', 'Декабря']
};

/*
 * Date format:
 *  d - day, 1 or 2 digit
 *  dd - day, 2 digit 
 *  M - month, 1 or 2 digit
 *  MM - month, 2 digits
 *  MMM - month, in words, in abbreviation form
 *  MMMM - month, in words
 *  yy - year, last 2 digit
 *  yyyy - year, 4 digit
 */
var sampleFormat = "dd MMM yyyy";
var formatRegexp = /^(d){1,2}( )+(m){1,4}( )+(yy|yyyy)$/i;

function formatDate(date, format) {
    if (format === undefined) {
        format = sampleFormat;
    }
    if (formatRegexp.test(format)) {
        var ar = format.split(' ');
        ar = ar.filter(function(e) {
            if (e.length <= 0) {
                return false;
            } else {
                return true;
            }
        });
        var fDay = ar[0];
        var fMonth = ar[1];
        var fYear = ar[2];
        var year = date.getFullYear();
        var month = date.getMonth();
        var day = date.getDate();
        var locale = getUserLocale();
        // day of month
        var formattedDay = day;
        if (ar[0] === 'dd') {
            if (fDay < 10) {
                var formattedDay = '0' + day;
            }
        }
        // month of year
        var formattedMonth;
        switch (fMonth) {
            case 'M':
                {
                    formattedMonth = '' + month;
                    break;
                }
            case 'MM':
                {
                    if (month < 10) {
                        formattedMonth = '0' + month;
                    }
                    break;
                }
            case 'MMM':
                {
                    formattedMonth = localized_months[locale][month];
                    break;
                }
            case 'MMMM':
                {
                    formattedMonth = localized_months_full[locale][month];
                    break;
                }
            case 'mmm':
                {
                    formattedMonth = localized_months[locale][month].toLowerCase();
                    break;
                }
            case 'mmmm':
                {
                    formattedMonth = localized_months_full[locale][month].toLowerCase();
                    break;
                }
            default:
                {
                    formattedMonth = '' + month;
                }
        }
        // year
        var formattedYear = '' + year;
        if (fYear === 'yy') {
            year = '' + year;
            formattedYear = year.substr(year.length - 2, 2);
        }
        return formattedDay + ' ' + formattedMonth + ' ' + formattedYear;
    } else {
        console.log("Wrong date format passed. Must passed JS RegExp (/^(d){1,2}( )+(m){1,4}( )+(yy|yyyy)$/i)");
        return "";
    }
}


