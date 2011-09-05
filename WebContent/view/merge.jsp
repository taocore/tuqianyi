<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div>
	<form id="label-form">
		<div id="merging-left" class="left">
			<table id="image-wrapper" cellpadding="0" cellspacing="0">
			<tr><td>
				<div id="main-pic">
				<s:if test="%{item == null}">
						<img src='images/unknown.jpg'/>
				</s:if>
				<s:else>
	    				<img src='<s:property value="item.picUrl"/>_310x310.jpg'/>
				</s:else>
				<div id="frame"></div>
				</div>
			</td></tr>
			</table>
			<div>
				<fieldset id="options">
					<legend>标签选项</legend>
					<label>缩放时保持长宽比：</label>
					<input type='checkbox' checked='checked'/><br/>
					<label>透明度：</label>
					<div id="opacity"></div>
				</fieldset>
			</div>
		</div>
		<div id="ctabs" class="right">
				<ul>
					<li><a href="#labels">标签</a></li>
					<li><a href="#text">文字</a></li>
					<li><a href="#frames">边框</a></li>
				</ul>
				<div id="labels">
					<s:iterator value="labelCategories">
						<h3><a href="#"><s:property value="name"/></a></h3>
						<div class="labels-panel">
							<s:iterator value="labels">
								<div class="label-item" title="单击将标签添加到主图"><img src='<s:property value="src"/>' width="100%" height="100%"/></div>
							</s:iterator>
						</div>
					</s:iterator>
					<h3><a href="#">自定义标签</a></h3>
					<div class="labels-panel">
						<s:iterator value="customLabels">
							<div class="label-item" title="单击将标签添加到主图"><img src='<s:property value="src"/>' width="100%" height="100%"/></div>
						</s:iterator>
					</div>
				</div>
				<div id="text">
					<div class="text-item"><img src="font.action?label.font=simhei"/></div>
				</div>
				<div id="frames">
					<div class="frame-item" title="单击将边框应用到主图">无</div>
					<s:iterator value="frameLabels">
							<div class="frame-item" title="单击将边框应用到主图"><img src='<s:property value="src"/>' width="100%" height="100%"/></div>
					</s:iterator>
				</div>
		</div>
	</form>
</div>

<script type="text/javascript">
	$("#ctabs").tabs();
	$("#labels").accordion();
	
	$(".label-item").click(
		function() {
			var $label = $("img", this);
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
	$("#opacity").slider();
	$(".frame-item").click(
		function() {
			var src = $("img", this).attr('src');
			if (src)
			{
				$("#frame").html("<img src='" + src + "' width='100%' height='100%'/>");
			}
			else
			{
				$("#frame").empty();
			}
	});
</script>