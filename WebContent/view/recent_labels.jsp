<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:iterator value="labels">
	<div class="label-item" title="单击将标签添加到主图" label_id='<s:property value="id"/>'>
		<img class='label' src='<s:property value="src"/>' width="100%" height="100%" />
	</div>
</s:iterator>
