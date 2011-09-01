<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%
	Date defaultStart = new Date();
	pageContext.setAttribute("defaultStart", defaultStart);
	Date defaultEnd = DateUtils.addDays(defaultStart, 3);
	pageContext.setAttribute("defaultEnd", defaultEnd);
%>

<div>
	<form id="promotion-form" method="post">
		<label for="discount_type">促销类型:</label>
		<div id="discount_type">
			<input id="type1" type="radio" name="discount_type" value="PRICE" checked="checked"></input>
			<label for="type1">减价</label>
			<input id="type2" type="radio" name="discount_type" value="DISCOUNT"></input>
			<label for="type2">打折</label>
		</div>
		<div>
			<label id='value-label'>减价:</label><br/>
			<input id="discount" class="text" type="text" name="discount_value"/>
		</div>
		<div>
			<label>开始时间:</label><br/>
			<input id="from" class="text" type="text" name="start_date" value='<s:date name="#attr.defaultStart" format="yyyy-MM-dd HH:mm"/>'/>
		</div>
		<div>
			<label>结束时间:</label><br/>
			<input id="to" class="text" type="text" name="end_date" value='<s:date name="#attr.defaultEnd" format="yyyy-MM-dd HH:mm"/>'/>
		</div>
		<div>
			<label for="title">活动名（至少2个字，最多5个字）:</label><br/>
			<input class="text" type="text" name="title" value="卖家促销"></input>
		</div>
		<div>
			<label for="description">活动描述:</label><br/>
			<input class="text" type="text" name="description"></input>
		</div>
	</form>
</div>

<script type="text/javascript">
$("#discount_type").buttonset();
$("#from").datetimepicker({
	stepMinute: 5
});
$("#to").datetimepicker({
	stepMinute: 5
});
$("input[name='discount_type']").change(function(){
	if ('PRICE' == $(this).val())
	{
		$("#value-label").text("减价：");
	}
	else
	{
		$("#value-label").text("折扣：");
	}
});
function validatePromotionForm()
{
	var discountValue = parseFloat($("#discount").val());
	if (!discountValue)
	{
		alert("请输入正确的减价或折扣数值。");
		return false;
	}
	var from = parseDatetime($("#from").val());
	if (!from)
	{
		alert("请输入正确的时间格式。");
		return false;
	}
	var to = parseDatetime($("#to").val());
	if (!to)
	{
		alert("请输入正确的时间格式。");
		return false;
	}
	if (from >= to)
	{
		alert("结束时间不能早于开始时间。");
		return false;
	}
	return true;
}
function parseDatetime(value) {
    var a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2})$/.exec(value);
    if (a) {
        return new Date(+a[1], +a[2] - 1, +a[3], +a[4], +a[5], +a[6]);
    }
    return null;
}
</script>