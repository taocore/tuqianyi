<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="labels">
	<s:iterator value="labelCategories">
		<h3><a href="#"><s:property value="name" /></a></h3>
		<div class="labels-panel">
			<s:iterator value="labels">
				<div class="label-item" title="单击将标签添加到主图" label_id='<s:property value="id"/>'>
					<img class='label' src='<s:property value="src"/>' width="100%" height="100%" />
				</div>
			</s:iterator>
		</div>
	</s:iterator>
</div>

<script type="text/javascript">
	$("#labels").accordion();
	
	$(".label-item").die().live('click', function() {
			var labelId = $(this).attr('label_id');
			var $label = $(".label", this);
			var src = $label.attr('src');
			$label.removeAttr('width').removeAttr('height');
			var w = $label.width();
			var h = $label.height();
			$label.attr('width', '100%').attr('height', '100%');
			var width;
			var height;
			if (w >= h)
			{
				width = 100;
				height = h * 100 / w;
			}
			else
			{
				height = 100;
				width = w * 100 / h;
			}
			var $labelItem = $("<div class='merge'><img src='" + src + "' width='100%' height='100%'/></div>");
			$labelItem.appendTo("#main-pic")
			.css({
				width: width,
				height: height,
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
	
</script>