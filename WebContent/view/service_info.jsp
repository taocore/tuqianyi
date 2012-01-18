<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	response.setHeader("Pragma","No-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
%>
<div style='height:500px;'>
<div class="notice">
	服务到期后，已贴标签的宝贝并不会自动恢复。
</div>

<div>
	您现在使用的是【图签易】
	<s:if test="%{#session.VERSION == 0}">
	   	<span class='large strong'>体验版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
	</s:if>
	<s:elseif test="%{#session.VERSION == 1}">
	   	<span class='large strong'>初级版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
	</s:elseif>
	<s:elseif test="%{#session.VERSION == 2}">
    	<span class='large strong'>中级版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
	</s:elseif>
	<s:elseif test="%{#session.VERSION == 3}">
    	<span class='large strong'>高级版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
	</s:elseif>。
</div>
<s:url action="sync" var="syncLink"></s:url>
<div>
	已贴标签宝贝<span class='large strong'><s:property value="mergedItemsCount"/></span>件。如有出入，请<span class='large'><a id='sync-link' href="${syncLink}">点此同步</a></span>。
</div>
<div>
	此服务将于<span class='large strong'><s:date name="serviceEnd" format="yyyy年M月d日"/></span>（还有<span class='large strong'><s:property value="left"/></span>天）到期，如需继续使用，请及时续订。
</div>

<div><a id='upgrade-link' href="http://fuwu.taobao.com/using/serv_upgrade.htm?service_id=6371" target='_blank'>升级</a></div>
</div>
</body>
<script type="text/javascript">
$('#upgrade-link').button();
$('#sync-link').click(function(){
	$.ajax({
		url: "${syncLink}",
		success: function() {
			$( "#tabs" ).tabs('load', 2);
		}
	});
	return false;
})
</script>