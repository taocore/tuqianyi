<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="frames">
	<div class="frame-item" title="单击将边框应用到主图">无</div>
	<s:iterator value="frameLabels">
		<div class="frame-item" title="单击将边框应用到主图">
			<img src='<s:property value="src"/>' width="100%" height="100%" />
		</div>
	</s:iterator>
</div>

<script type="text/javascript">
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