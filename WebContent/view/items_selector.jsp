<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
	var selectedItems = new Array();
	var mid = 0;
	var maxBatch = 100;
</script>

<div>
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
	<div id="items">
		<s:include value="items.jsp"/>
	</div>
	<div id="pager" class="pager span-16 separator"></div>
</div>

<div id="label-dialog" title="贴标签">
	<s:include value="merge.jsp"/>
</div>

<div id='progress-dialog' title='正在处理...'>
</div>

<script type="text/javascript">
	$(".selection").tooltip();
	$("button").button();
	$("#clear-selection").click(clearSelection);
	$("#label-dialog").dialog({
		autoOpen: false,
		modal: true,
		width: 900,
		open: function(event, ui) {
			$("#search-form select").hide();
			$("#ctabs").tabs({
				cache: true
			});
		},
		close: function(event, ui){
			$("#search-form select").show();
		}
	});
	
	$("#progress-dialog").dialog({
		autoOpen: false,
		modal: true,
		open: function(event, ui) { 
			$(".ui-dialog-titlebar-close").hide(); 
		}
	});
	
	$("#batch-add-label").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		if (selectedItems.length > maxBatch)
		{
			alert("批量一次最多只能处理 " + maxBatch + " 件宝贝。");
			return false;
		}
		var $dialog = $("#label-dialog");
		var numIids = selectedItems.join();
		var q = "numIids=" + numIids;
		$.ajax({
			url: "merging.action",
			data: q,
			type: 'POST',
			success: function(data) {
				$dialog.html(data);
				$dialog.dialog("option", "buttons", {
					确定: function() {
						var merges = getMerges();
						if (!merges)
						{
							alert("未改变原图。");
							return false;
						}
						showProgressDialog();
						//showProcessingDialog();
						var q = 'numIids=' + numIids + merges;
						$.ajax({
							url: 'merge.action',
							data: q,
							type: 'POST',
							success: function(data){
								hideProgressDialog();
								//hideProcessingDialog();
								if ((data == 'ok'))
								{
									$dialog.dialog('close');
									reload();
								}
								else
								{
									alert(data);
								}
							}
						});
						return false;
					},
					取消: function() {
						$(this).dialog( "close" );
						return false;
					}
				});
				$dialog.dialog("open");
			}
		});
		return false;
	});
	
	$("#batch-recover").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		showProgressDialog();
		$.ajax({
			url: "recover.action",
			data: {numIids: selectedItems.join()},
			success: function(data) {
				hideProgressDialog();
				reload(clearSelection);
			}
		});
		return false;
	});
	
	function checkSelection()
	{
		$("tbody input.selector").each(function()
		{
			var num_iid = $(this).closest("tr").attr("num_iid");
			var found = $.grep(selectedItems, function(a){
				return a == num_iid;
			}, false);
			$(this).attr("checked", (found.length > 0));
			$(this).trigger('change');
		});
	}
	
	function clearSelection()
	{
		selectedItems = new Array();
		checkSelection();
	}
	
	var $keyword = $("#keyword");
	$keyword.css('color', '#666');
	$keyword.focus(function() {
		if ($(this).val() == '关键字') {
			$(this).css('color', 'black');
			$(this).val('');
		}
	});
	$keyword.blur(function() {
		if ($(this).val() == '') {
			$(this).val('关键字');
			$(this).css('color', '#666');
		}
	});
	
	$("#search").click(function(){
		var q = getFilterParameters();
		$("#items").data('q', q);
		loadPage(1);
		return false;
	});
	
	$("#search-form").submit(function()
	{
		return false;
	});
	
	function getFilterParameters()
	{
		var $form = $("#search-form");
		var saleStatus = $("select[name='filter.saleStatus']", $form).val();
		var banner = $("select[name='filter.saleStatus'] option:selected", $form).attr('banner');
		var cids = null;
		var c = $("select[name='filter.sellerCids']", $form);
		var cid = c.val();
		if (cid)
		{
			cids = [cid];
			$("option[parent='" + cid + "']", $form).each(function(){
				cids.push($(this).val());
			});
		}
		var status = $("select[name='filter.status']", $form).val();
		var keyword = $("input[name='filter.keyWord']", $form).val();
		if (keyword == '关键字')
		{
			keyword = null;
		}
		var q = 'filter.saleStatus=' + saleStatus;
		q += '&filter.banner=' + banner;
		if (cids)
		{
			q += '&filter.sellerCids=' + cids;
		}
		q += '&filter.status=' + status;
		if (keyword)
		{
			q += '&filter.keyWord=' + keyword;
		}
		return q;
	}
	
	$("#pager").pager({ pagenumber: 1, pagecount: <s:property value="pagingItems.totalPages"/>, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
	
	function loadPage(number) {
		loadPage(number, null);
	}
	
	function loadPage(number, callback) {
		$("#items").html("<img src='images/loading.gif'/>");
        var limit = <s:property value="pagingItems.option.limit"/>;
        var offset = (number - 1) * limit;
        var q = $("#items").data('q') + "&option.limit=" + limit + "&option.offset=" + offset;
		$.ajax({
			url: "items.action",
			data: q,
			type: 'POST',
			success: function(data) {
				$("#items").html(data);
				checkSelection();
				var pageCount = parseInt($("#items-table").attr("pages"));
				$("#pager").pager({ pagenumber: number, pagecount: pageCount, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
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
		var i = parseInt($("#items-table").attr("pageIndex"));
		loadPage(i+1, callback);
	}
	
	function showProgressDialog()
	{
		$("#progress-dialog").dialog("open");
		updateStatus();
	}
	
	function updateStatus()
	{
		var $dialog = $("#progress-dialog");
		var isOpen = $dialog.dialog( "isOpen" );
		if (!isOpen)
		{
			clearTimeout($dialog.data('timer'));
			return;
		}
		$.ajax({
			  url: 'progress.action',
			  success: function( data ) {
				  $dialog.html(data);
			  }
		});
		var t = setTimeout("updateStatus()", 2000);
		$dialog.data('timer', t);
	}
	
	function hideProgressDialog()
	{
		$("#progress-dialog").dialog("close");
	}
</script>
