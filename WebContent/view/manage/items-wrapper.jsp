<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="items-wrapper" >
	<div class="toolbar">
		<div class="left">
			<form id="search-form">
			<select name="filter.saleStatus">
				<option value="0">出售中</option>
				<option value="1" banner='for_shelved'>待上架</option>
				<option value="1" banner='sold_out'>已卖完</option>
			</select>
			<select name="filter.sellerCids">
				<option value=''>所有分类</option>
				<s:iterator value="categories">
					<option value='<s:property value="cid"/>' parent='<s:property value="parentCid"/>'>
						<s:if test="%{parentCid != 0}">└</s:if>
						<s:property value="name" />
					</option>
				</s:iterator>
			</select>
			<select name="filter.status">
				<option value="-1">所有状态</option>
				<option value="3">出错</option>
				<option value="4">成功</option>
			</select>
			<input id="keyword" type="text" name="filter.keyWord" value="关键字" style="margin:0.5em 0;"></input>
			<button id='search' style="margin:0.5em 0;">查找</button>
			</form>
		</div>
		<div class="right">
			<span class="selection" title='可跨页及跨查找结果选择'></span>
			<a href="#" id="clear-selection">清除重选</a>
			<button id='batch-add-label'>批量加标签</button>
			<button id='batch-recover'>批量恢复</button>
		</div>
		<div class="clear"></div>
	</div>
	<div id="user-items" nick="<s:property value='nick'/>">
		<s:include value="items.jsp"/>
	</div>
	<div class="pager span-16 separator"></div>
</div>

<script type="text/javascript">

$("#items-wrapper .pager").pager({ pagenumber: 1, pagecount: <s:property value="pagingItems.totalPages"/>, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });

function loadPage(number) {
	loadPage(number, null);
}

function loadPage(number, callback) {
	$("#user-items").html("<img src='images/loading.gif'/>");
    var limit = <s:property value="pagingItems.option.limit"/>;
    var offset = (number - 1) * limit;
    var nick = $('#user-items').attr('nick');
    var q = "nick=" + nick + "&option.limit=" + limit + "&option.offset=" + offset;
    var url = '<s:url action="user-items"/>';
	$.ajax({
		url: url,
		data: q,
		type: 'POST',
		success: function(data) {
			$("#user-items").html(data);
			var pageCount = parseInt($("#user-items-table").attr("pages"));
			$("#items-wrapper .pager").pager({ pagenumber: number, pagecount: pageCount, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
			if (callback)
			{
				callback();
			}
		}
	});
}

function reload()
{
	reload(null);
}

function reload(callback)
{
	var i = parseInt($("#user-items-table").attr("pageIndex"));
	loadPage(i+1, callback);
}	
</script>
