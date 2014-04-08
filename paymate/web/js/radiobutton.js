$(document).ready(function() {
    $('input[value="once"]').change(function() {
        $('.recurring').addClass('hidden');
        $('.datelabels').text('Schedule Date').css('margin-right', '0');
        ;
    });

    $('input[value="recurring"]').change(function() {
        $('.recurring').removeClass('hidden');
        $('.datelabels').text('Start Date').css('margin-right', '31px');
    });
});