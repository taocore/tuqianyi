<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div style="width:750px">
	<s:iterator value="users">
		<div class='left' style="width:200px;">
			<s:if test="levelImage != null">
				<img src="<s:property value="levelImage"/>"/>
			</s:if>
			<s:property value="nick"/>
		</div>
	</s:iterator>
</div>