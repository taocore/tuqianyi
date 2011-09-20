<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	response.setHeader("Pragma","No-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
%>
<div>
	正在处理宝贝 <span class="total"><s:property value="#session.total"/></span> 件，
	已完成 <span class="progress"><s:property value="#session.processed"/></span> 件...
</div>
<div id="progressbar"></div>
<script type="text/javascript">
	var total = <s:property value="#session.total"/>;
	var progress = <s:property value="#session.processed"/>;
	var value = progress * 100 / total; 
	$("#progressbar").progressbar({
		value: value
	});
</script>