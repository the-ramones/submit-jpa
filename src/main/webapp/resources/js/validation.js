/* Reports!
 * Locale-holder cookie name
 * @type String
 */
var reports_locale_cookie_name = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE";
/*
 * Map of RegExp for a particular locale
 * @type type
 */
var locale_map = {
    "default": /^( )*(0?[1-9]|[12][0-9]|3[01]) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec){1} \d{4}( )*$/i,
    "en": /^( )*(0?[1-9]|[12][0-9]|3[01]) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec){1} \d{4}( )*$/i,
    "ru": /^( )*(0?[1-9]|[12][0-9]|3[01]) (Янв|Дек|Мар|Апр|Май|Июн|Июл|Авг|Сен|Окт|Ноя|Дек){1} \d{4}( )*$/i
};
var feb_map = {
    "default": /^(Feb)$/i,
    "en": /^(Feb)$/i,
    "ru": /^(Фев)$/i
};
var non31_months_map = {
    "default": /^(Apr|Jun|Sep|Nov){1}$/i,
    "en": /^(Apr|Jun|Sep|Nov){1}$/i,
    "ru": /^(Апр|Июн|Сен|Ноя){1}$/i
};

var performer_regexp = "^[a-zа-я][ 0-9a-zа-я-#@%&\\$]{1,255}(?!-)$";
var activity_regexp = "^[ a-zа-я0-9-#@%&\\$]{1,255}(?!-)$";
/*
 * Add a custom validation method
 * @param {type} constraint validation mathod name
 * @param {type} constraint validation method implementation
 */
$.validator.addMethod("reportdate", function(value, element) {
    /*
     * if pass the regexp, verify correctness of the date 
     * valid: 31 Jan 2010 or 29 Dec 2012 
     * invalid: 29 Dec 2013 or 31 Mar 2013
     */
    /* if a date has been specified it must be a valid date, even if it is optional field
     * result = this.optional(element) || /(0?[1-9]|[12][0-9]|3[01]) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec){1} \d{4}$/i.test(value);    
     */
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
    var result = locale_map[locale] ? locale_map[locale] : locale_map["default"];
    var feb = feb_map[locale] ? feb_map[locale] : feb_map["default"];
    var non31 = non31_months_map[locale] ? non31_months_map[locale] : non31_months_map["default"];

    /*
     * If element is optional and left blank (with respect to backslashes), value
     * is supposed to ba valid. If value is optional and a value entered, it will
     * be validated against regular expression to avoid ambiguity.
     */
    if (this.optional(element) && (value.trim() === "")) {
        return true;
    }
    if (result.test(value)) {
        parts = value.split(' ');
        var day = parts[0];
        var month = parts[1];
        if (feb.test(month)) {
            if (day == 29) {
                if (day % 4 !== 0 || day % 100 === 0 || day % 400 !== 0) {
                    return false;
                }
                ;
            } else if (day > 28) {
                return false;
            }
        }
        if (non31.test(month)) {
            if (day > 30) {
                return false;
            }
        } else {
            if (day > 31) {
                return false;
            }
        }
        return true;
    }
    return false;
});

$.validator.addMethod("reportpassword", function(value, element) {
    return /^[a-zA-Z0-9_!@#-\\$\\^\\*]{4,18}$/.test(value);
});

$.validator.addMethod("reportusername", function(value, element) {
    return /^[a-zA-Z0-9_-]{4,16}|([a-z0-9_\\.-]+)@([\da-z\\.-]+)\\.([a-z\\.]{2,6})$/.test(value);
});

$.validator.addMethod("regexp", function(value, element, regexp) {
    var reg = new RegExp(regexp, 'i');
    return this.optional(element) || reg.test(value);
});

/*
 * Used RegExps + ISO Date
 * 
 * English version:
 * /(0?[1-9]|[12][0-9]|3[01])(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec){1}\d{4}$/i
 * Russian version
 * /(0?[1-9]|[12][0-9]|3[01])(Янв|Дек|Мар|Апр|Май|Июн|Июл|Авг|Сен|Окт|Ноя|Дек){1}\d{4}$/i
 * ISO date (YYYY-MM-DD)
 * ^((((19|20)(([02468][048])|([13579][26]))-02-29))|((20[0-9][0-9])|(19[0-9][0-9]))-((((0[1-9])|(1[0-2]))-((0[1-9])|(1\d)|(2[0-8])))|((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30)))))$
 */

/*
 * Submission form valitators
 */
$("form[name='report-add']").validate({
//  debug: true,
    rules: {
        startDate: {
            required: true,
            reportdate: true
        },
        endDate: {
            reportdate: true
        },
        performer: {
            required: true,
            rangelength: [1, 255],
            regexp: performer_regexp
        },
        activity: {
            required: true,
            rangelength: [1, 255],
            regexp: activity_regexp
        }
    },
    focusInvalid: false
});

$("form[name='search-report']").validate({
//  debug: true,
    rules: {
        id: {
            required: true,
            digits: true,
            min: 1
        }
    }
});

$("form[name='report-request']").validate({
//  debug: true,
    rules: {
        performer: {},
        timePeriod: {},
        startDate: {
            required: true,
            reportdate: true
        },
        endDate: {
            required: true,
            reportdate: true
        }
    },
    focusInvalid: false
});

$("form[name='login-form']").validate({
//  debug: true,
    rules: {
        j_username: {
            required: true,
            minlength: 4,
            reportusername: true
        },
        j_password: {
            required: true,
            minlength: 4,
            reportpassword: true
        }
    }
});