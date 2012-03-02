(function() {
	$("#labels").accordion();
	
	$(".label-item").die().live('click', function() {
			var labelId = $(this).attr('label_id');
			var $label = $(".label", this);
			var src = $label.attr('src');
			var $labelItem = $("<div class='merge'><img src='" + src + "' width='100%'/></div>");
			$labelItem.appendTo("#main-pic")
			.css({
				width: 100,
				position: 'absolute',
				top: 0,
				left: 0
			}).draggable({
				containment : "#main-pic",
				scroll : false
			}).resizable({
				containment : "#main-pic",
				aspectRatio : true,
				minWidth : 20,
				minHeight : 20,
				maxWidth : 310,
				maxHeight : 310,
				autoHide: true,
				handles: 'n, e, s, w, ne, se, sw, nw'
			}).hover(
				function()
				{
					$(".label-tool", $labelItem).show();
				},
				function()
				{
					$(".label-tool", $labelItem).hide();
				}
			).data('option', {
				aspectRatio: true, 
				opacity: 100
			}).mousedown(function(){
				$(this).addClass('ui-selected');
				$('#main-pic .merge').not(this).removeClass('ui-selected');
				$('#text-label-options').hide();
				$('#image-label-options').show();
				var opacity = $(this).data('option').opacity;
				$('#opacity').slider('value', opacity);
			}).trigger('mousedown');
			$('img', $labelItem).load(function(){
				$labelItem.height($(this).height());
			});
			
			if (labelId)
			{
				$labelItem.data('id', labelId);
			}
			
			$("<div title='删除' class='label-tool hide'><img src='images/cross_small.png'/></div>")
			.appendTo($labelItem)
			.click(function(){
				$labelItem.remove();
				$('#image-label-options').hide();
			});
	});
})();
