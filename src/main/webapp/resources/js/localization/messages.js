/*
 * Default messages for the jQuery validation plugin.
 * Locale: EN (English; english language)
 */
(function($) {
    $.extend($.validator.messages, {
        required: "This field is required.",
        remote: "Please fix this field.",
        email: "Please enter a valid email address.",
        url: "Please enter a valid URL.",
        date: "Please enter a valid date.",
        dateISO: "Please enter a valid date (ISO).",
        number: "Please enter a valid number.",
        digits: "Please enter only digits.",
        creditcard: "Please enter a valid credit card number.",
        equalTo: "Please enter the same value again.",
        maxlength: $.validator.format("Please enter no more than {0} characters."),
        minlength: $.validator.format("Please enter at least {0} characters."),
        rangelength: $.validator.format("Please enter a value between {0} and {1} characters long."),
        range: $.validator.format("Please enter a value between {0} and {1}."),
        max: $.validator.format("Please enter a value less than or equal to {0}."),
        min: $.validator.format("Please enter a value greater than or equal to {0}."),
        reportdate: "Please enter a date in DD M YYYY format (12 Jan 2013)",
        reportusername: "Please enter a correct username/email (a-Z0-9!@#$* signs allowed)",
        reportpassword: "Please enter a correct password (a-zA-Z0-9_!@#-$^*, 6-18 characters)"
    });
}(jQuery));