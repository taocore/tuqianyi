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
		应用到期后，已贴标签的宝贝并不会自动恢复，如果到期后不想继续使用本应用，请在到期前自己恢复所有已贴标签的宝贝，否则，将永远无法恢复。
</div>
<s:url action="sync" var="syncLink"></s:url>
<div class="span-23 last">
	您现在使用的是【图签易】<s:if test="%{#session.VERSION == 1}">
	    	<span class='large strong'>初级版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
		</s:if>
		<s:elseif test="%{#session.VERSION == 2}">
    		<span class='large strong'>中级版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
		</s:elseif>
		<s:elseif test="%{#session.VERSION == 3}">
    		<span class='large strong'>高级版</span>，支持宝贝<span class='large strong'><s:property value="allowedItems"/></span>件
		</s:elseif>。
</div>
<div>
	已贴标签宝贝<span class='large strong'><s:property value="mergedItemsCount"/></span>件。如有出入，请<span class='large'><a href="${syncLink}">点此</a></span>同步。
</div>
<div>
	此服务将于<span class='large strong'><s:date name="serviceEnd" format="yyyy年M月d日"/></span>（还有<span class='large strong'><s:property value="left"/></span>天）到期，如需继续使用，请及时续订。
</div>
</div>