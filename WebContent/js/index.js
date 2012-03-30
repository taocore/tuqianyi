$(function() {
	$('#upgrade').click(function(){
		var version = parseInt($('#header').attr('version'));
		if (version == 0)
		{
			$('#info-upgrade').hide().fadeIn('slow');
			return false;
		}
	});
	
	$( "#tabs" ).tabs({
		cache: true,
		spinner: '载入中...'
	});
	
	$("#top").click(function(){
		$(window).scrollTop(0);
	});
	
	$("#bottom").click(function(){
		$(window).scrollTop($(document).height());
	});
	
	$(".old-edition").click(function(){
		//location.href = location.href.replace(location.host + '/tuqianyi3/', 'taocore.w44.mc-test.com/tuqianyi/Tuqianyi.html');
		location.href = location.href.replace('/tuqianyi3/', '/tuqianyi/Tuqianyi.html');
	});
	
	$("#processing-dialog").dialog({
		autoOpen: false,
		modal: true,
		resizable: false,
		open: function(event, ui) { 
			$(".ui-dialog-titlebar-close").hide(); 
		}
	});
	
	window.utils = {};
	
	window.utils.showProcessingDialog = function()
	{
		$("#processing-dialog").dialog("open");
	};
	
	window.utils.hideProcessingDialog = function()
	{
		$("#processing-dialog").dialog("close");
	};
});