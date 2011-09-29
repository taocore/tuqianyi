<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.awt.Font" %>
<%@ page import="com.tuqianyi.model.TextLabel" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%
	final String[] fontKeys = {"simhei", "simkai", "simsun",
	"msyh", "hkst", "mnjccy", "mnjdh", "mnxf", "mshkj", "hdzb_46", "stliti", "jdjykd"};
	pageContext.setAttribute("fonts", fontKeys);
%>

<div id="text">
	<s:iterator value="#attr.fonts">
		<div class="text-item" f='<s:property/>' title="单击将标签添加到主图">
			<img src="font.action?label.font=<s:property/>" width="100%" height="100%" />
		</div>
	</s:iterator>
</div>

<script type="text/javascript">
	$(".text-item").click(
			function() {
				var mergeId = 'm_' + mid++;
				var font = $(this).attr('f');
				var $label = $("img", this);
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
				
				var $textMerge = $("<div class='merge t loading'></div>");
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
					//aspectRatio : true,
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
					mid: mergeId,
					font: font,
					text: '热卖',
					color: '#ff0000',
					background: '',
					style: <%=Font.PLAIN%>,
					line: <%=TextLabel.LINE_NONE%>,
					borderWidth: 0,
					//aspectRatio: true, 
					opacity: 100
				}).mousedown(function(){
					$(this).addClass('ui-selected');
					$('#main-pic .merge').not(this).removeClass('ui-selected');
					$('#image-label-options').hide();
					$('#text-label-options').show();
					var option = $(this).data('option');
					$('#text').val(option.text);
					$('#fore-color').val(option.color).change();
					$('#back-color').val(option.background).change();
					$('#style input[name="style"]').removeAttr('checked');
					$('#style input[value="' + option.style + '"]').attr('checked', 'checked');
					$('#style input').change();
					$('#line input[name="line"]').removeAttr('checked');
					$('#line input[value="' + option.line + '"]').attr('checked', 'checked');
					$('#line input').change();
					$('#border-width').slider('value', option.borderWidth);
					$('#text-opacity').slider('value', option.opacity);
				}).trigger('mousedown');
				
				var src = "text.action?label.id=" + mergeId + "&label.font=" + font;
				$("<img class='t hide' src='" + src + "' width='100%' height='100%'/>")
				.appendTo($textMerge)
				.load(function(){
					$(this).parent().removeClass('loading');
					$(this).show();
				});
				
				$("<div class='label-tool hide' title='删除'><img src='images/cross_small.png'/></div>")
				.appendTo($textMerge)
				.click(function(){
					$textMerge.remove();
					$('#text-label-options').hide();
				});
			});
</script>