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
				$("<div><img src='" + src + "' width='100%' height='100%'/></div>").appendTo("#main-pic")
				.css({
					width: width,
					height: height,
					border: '1px solid lightblue',
					position: 'absolute',
					top: 0,
					left: 0,
					cursor: 'move'
				}).draggable({
					containment : "#main-pic",
					scroll : false
				}).resizable({
					containment : "#main-pic",
					aspectRatio : true,
					minWidth : 20,
					minHeight : 20,
					maxWidth : 310,
					maxHeight : 310
				}).dblclick(function(){
					var pos = $(this).position();
					alert(pos.left + ", " + pos.top);
				});
			});
</script>