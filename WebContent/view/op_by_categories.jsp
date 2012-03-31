<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<form id='categories-form'>
	<select name="filter.saleStatus">
		<option value="0">出售中</option>
		<option value="1" banner='for_shelved'>待上架</option>
		<option value="2" banner='sold_out'>已卖完</option>
	</select>
	<div name="filter.sellerCids" class='border' style='padding:10px;margin:10px 0;'>
		<!-- option value=''>所有分类</option -->
		<s:iterator value="categories">
			<div value='<s:property value="cid"/>'
				parent='<s:property value="parentCid"/>'
				style=
				<s:if test="%{parentCid != 0}">
					'margin-left:20px;'
				</s:if>
				<s:else>'margin-left:10px;'</s:else>>
				<s:if test="%{parentCid != 0}">└</s:if>
				<input type='checkbox'></input>
				<s:property value="name" />
			</div>
		</s:iterator>
	</div>
	<button id='merge-by-category' style="margin: 0.5em 0;">贴标签</button>
	<!-- button id='recover-by-category' style="margin: 0.5em 0;">恢复</button -->
</form>
<script type="text/javascript" src='js/op_by_categories.js'></script>