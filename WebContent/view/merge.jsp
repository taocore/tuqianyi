<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div>
	<form id="label-form">
		<div id="merging-left" class="left">
			<table id="image-wrapper" cellpadding="0" cellspacing="0">
			<tr><td>
				<div id="main-pic">
				<s:if test="%{item == null}">
						<img src='images/unknown.jpg'/>
				</s:if>
				<s:else>
	    				<img src='<s:property value="item.picUrl"/>_310x310.jpg'/>
				</s:else>
				<div id="frame"></div>
				</div>
			</td></tr>
			</table>
			<div>
				<fieldset id="options">
					<legend>标签选项</legend>
					<label>缩放时保持长宽比：</label>
					<input type='checkbox' checked='checked'/><br/>
					<label>透明度：</label>
					<div id="opacity"></div>
				</fieldset>
			</div>
		</div>
		<div id="ctabs" class="right">
				<ul>
					<li><a href="labels.action">标签</a></li>
					<li><a href="view/text.jsp">文字</a></li>
					<li><a href="frames.action">边框</a></li>
				</ul>
		</div>
	</form>
</div>

<script type="text/javascript">
	
	
	$("#opacity").slider();
</script>