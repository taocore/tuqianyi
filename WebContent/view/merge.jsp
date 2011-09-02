<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div>
	<form id="label-form">
		<div class="left">
			<table id="image-wrapper" cellpadding="0" cellspacing="0">
				<tr><td>
				<img id="main-pic" src='<s:property value="item.picUrl"/>_310x310.jpg'/>
				</td></tr>
			</table>
			<div id="options"></div>
		</div>
		<div class="right">
			<div id="labels">
				<s:iterator value="labelCategories">
					
						<h3><a href="#"><s:property value="name"/></a></h3>
						<div class="labels-panel">
							<s:iterator value="labels">
								<div class="label-item"><img src='<s:property value="src"/>' width="40" height="40"/></div>
							</s:iterator>
						</div>
					
				</s:iterator>
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">
$("#labels").accordion();
</script>