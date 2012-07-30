<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	response.setHeader("Pragma","No-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
%>
<div id='service-info' style='height:500px;'>
<div class="notice">
	服务到期后，宝贝上的标签不会消失。如需恢复，请订购免费体验版。
</div>

<div>
	您现在使用的是【图签易】
	<s:if test="%{#session.VERSION == 0}">
	   	<span class='large strong blue'>体验版</span>，支持宝贝<span class='large strong blue'><s:property value="allowedItems"/></span>件
	</s:if>
	<s:elseif test="%{#session.VERSION == 1}">
	   	<span class='large strong blue'>初级版</span>，支持宝贝<span class='large strong blue'><s:property value="allowedItems"/></span>件
	</s:elseif>
	<s:elseif test="%{#session.VERSION == 2}">
    	<span class='large strong blue'>中级版</span>，支持宝贝<span class='large strong blue'><s:property value="allowedItems"/></span>件
	</s:elseif>
	<s:elseif test="%{#session.VERSION == 3}">
    	<span class='large strong blue'>高级版</span>，支持宝贝<span class='large strong blue'><s:property value="allowedItems"/></span>件
	</s:elseif>。
</div>
<s:url action="sync" var="syncLink"></s:url>
<div>
	已贴标签宝贝<span class='large strong blue'><s:property value="mergedItemsCount"/></span>件。如有出入，请<span class='large'><a id='sync-link' href="${syncLink}">点此同步</a></span>。
</div>
<div>
	此服务将于<span class='large strong blue'><s:date name="serviceEnd" format="yyyy年M月d日"/></span>（还有<span class='large strong blue'><s:property value="left"/></span>天）到期，如需继续使用，请及时续订。
</div>

	<div>
		<a id='upgrade-link' href="http://fuwu.taobao.com/using/serv_upgrade.htm?service_id=6371" target='_blank'>升级</a>
		<a class='strong' style='color:red;' href="http://seller.taobao.com/fuwu/shopshow/shop_index.htm?page_id=95889&isv_id=62112477&page_rank=2&tab_type=1" target="_blank">推荐</a>
	</div>
</div>

<script type="text/javascript">
$('#upgrade-link').button();
$('#sync-link').click(function(){
	$("#service-info").html("<img src='images/loading.gif'/>");
	$.ajax({
		url: "${syncLink}",
		success: function() {
			$( "#tabs" ).tabs('load', 3);
		}
	});
	return false;
})
</script>