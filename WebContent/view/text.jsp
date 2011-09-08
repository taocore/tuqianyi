<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="text">
	<div class="text-item">
		<img src="font.action?label.font=simhei" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=simkai" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=simsun" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=msyh" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=hkst" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=mnjccy" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=mnjdh" width="100%" height="100%" />
	</div>
	<div class="text-item">
		<img src="font.action?label.font=mnxf" width="100%" height="100%" />
	</div>
</div>

<script type="text/javascript">
	$(".text-item").click(
			function() {
				var $label = $("img", this);
				var src = $label.attr('src');
				var w = $(this).width();
				var h = $(this).height();
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
				var $textMerge = $("<div class='merge'><img src='" + src + "' width='100%' height='100%'/></div>");
				$textMerge.appendTo("#main-pic")
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
						$(".label-tool", this).show();
					},
					function()
					{
						$(".label-tool", this).hide();
					}
				).data('option', {
					aspectRatio: true, 
					opacity: 100
				}).mousedown(function(){
					$(this).addClass('ui-selected');
					$('#main-pic .merge').not(this).removeClass('ui-selected');
					$('#image-label-options').hide();
					$('#text-label-options').show();
					var opacity = $(this).data('option').opacity;
					$('#opacity').slider('value', opacity);
				}).trigger('mousedown');
				
				$("<div class='label-tool hide'><img src='images/cross_small.png'/></div>")
				.appendTo($textMerge)
				.click(function(){
					$textMerge.remove();
					$('#text-label-options').hide();
				});
			});
</script>