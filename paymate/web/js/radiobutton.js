$(document).ready(function(){
  $('#once').change(function(){
    $('.recurring').addClass('hidden');
  });
  
  $('#recurring').change(function(){
    $('.recurring').removeClass('hidden');
  });
});