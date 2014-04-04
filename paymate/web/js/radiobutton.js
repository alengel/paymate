$(document).ready(function(){
  $('input[value="once"]').change(function(){
    $('.recurring').addClass('hidden');
    $('.datelabels').text('Schedule Payment Date').css('margin-right', '64px');;
  });
  
  $('input[value="recurring"]').change(function(){
    $('.recurring').removeClass('hidden');
    $('.datelabels').text('Start Date').css('margin-right', '159px');
  });
});