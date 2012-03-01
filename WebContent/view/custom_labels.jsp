<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
	response.setHeader("Pragma","No-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
%>

<s:iterator value="customLabels">
	<div class="label-item custom-label-item" title="单击将标签添加到主图"
		label_id='<s:property value="id"/>'>
		<img class='label' src='<s:property value="src"/>' width="100%" height="100%" />
		<div class='custom-label-tool hide' title='删除'>
			<img src='images/cross_small.png' />
		</div>
	</div>
</s:iterator>

<script type="text/javascript" src='js/custom_labels.js'></script>