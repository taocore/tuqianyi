<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<table id="items-table" 
		pageSize='<s:property value="option.limit" />'
		pages='<s:property value="pagingItems.totalPages" />' 
		pageIndex='<s:property value="pagingItems.currentPage" />'>
	<thead>
		<tr>
			<th><input type="checkbox" class="selector"></input>
			</th>
			<th>主图</th>
			<th>基本信息</th>
			<th>出错原因</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="pagingItems.items">
			<tr class="drow" num_iid='<s:property value="numIid"/>'>
				<td style='width:20px;'><input type="checkbox" class="selector"></input>
				</td>
				<td class='item-main-pic'>
					<a href='http://item.taobao.com/item.htm?id=<s:property value="numIid"/>' target="_blank">
						<s:if test="%{action == 0 && status == 2}">
	    					<img class="pic" src='<s:property value="oldPicUrl"/>_80x80.jpg' src_310='<s:property value="picUrl"/>_310x310.jpg' />
						</s:if>
						<s:else>
    						<img class="pic" src='<s:property value="picUrl"/>_80x80.jpg' src_310='<s:property value="picUrl"/>_310x310.jpg' />
						</s:else>
					</a>
				</td>
				<td class="item-details">
					<div><s:property value="title"/></div>
					<div>价格：<s:property value="price"/>元</div>
				</td>
				<td class='error-msg'>
					<s:if test="%{status == 3}">
	    				<div class='error'><s:property value="errorMsg"/></div>
	    				<div><a href='http://fuwu.taobao.com/serv/detail.htm?service_id=13101' target='_blank'>使用【宝安】一键扫描所有属性错误！</a></div>
					</s:if>
				</td>
				<td class="status" status='<s:property value="status"/>'>
					<s:if test="%{status == 4}">
	    				<div><img src='images/tick_32.png'/></div>
	    				<div>成功</div>
					</s:if>
					<s:elseif test="%{status == 3}">
    					<div><img src='images/warning_32.png'/></div>
	    				<div>失败</div>
					</s:elseif>
					<s:elseif test="%{status == 2}">
    					<div><img src='images/go.png'/></div>
	    				<div>处理中</div>
					</s:elseif>
				</td>
				<td class="op">
					<s:if test="%{status == 4 || status == 3}">
						<div><a class="recover-link" href="#">恢复</a></div>
						<div><a class="change-label-link" href="#">换标签</a></div>
					</s:if>
					<s:elseif test="%{status == 0}">
	    				<a class="add-label-link" href="#">贴标签</a>
					</s:elseif>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<script type="text/javascript">
	$("#items-table").mytable({selectionChanged: selectionChanged});
	$(".pic").tooltip({
		showURL: false,
		bodyHandler: function() { 
	        return $("<img/>").attr("src", $(this).attr('src_310')); 
	    } 
	});
	$(".add-label-link").click(function(){
		var currentItem = $(this).closest("tr").attr("num_iid");
		var $dialog = $("#label-dialog");
		var url = "merging.action";
		var q = "numIids=" + currentItem;
		$.ajax({
			url: url,
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
						window.utils.showProcessingDialog();
						var url = 'merge.action';
						var q = 'numIids=' + currentItem + merges;
						$.ajax({
							url: url,
							data: q,
							type: 'POST',
							success: function(data){
								window.utils.hideProcessingDialog();
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
	
	$(".change-label-link").click(function(){
		var currentItem = $(this).closest("tr").attr("num_iid");
		var $dialog = $("#label-dialog");
		var url = "merging.action";
		var q = "numIids=" + currentItem;
		$.ajax({
			url: url,
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
						window.utils.showProcessingDialog();
						var url = 'change-label.action';
						var q = 'numIids=' + currentItem + merges;
						$.ajax({
							url: url,
							data: q,
							type: 'POST',
							success: function(data){
								window.utils.hideProcessingDialog();
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
	
	$(".recover-link").click(function(){
		window.utils.showProcessingDialog();
		var numIid = $(this).closest("tr").attr("num_iid");
		var url = "recover.action?numIids=" + numIid;
		$.ajax({
			url: url,
			success: function(data) {
				window.utils.hideProcessingDialog();
				reload();
			}
		});
		return false;
	});

	function selectionChanged(row)
	{
		var num_iid = row.attr("num_iid");
		var selected = $("input", row).attr("checked");
		if (selected)
		{
			var found = $.grep(selectedItems, function(a){
				return a == num_iid;
			}, false);
			if (found.length == 0)
			{
				selectedItems.push(num_iid);
			}
		}
		else
		{
			selectedItems= $.grep(selectedItems, function(a){
				return a == num_iid;
			}, true);
		}
		$(".selection").text("已选中 " + selectedItems.length + " 项");
	}
	
	function merge(callback)
	{
		var url = "merge.action";
		var q = "numIids=";
		$.ajax({
			url: url,
			data: q,
			type: 'POST',
			success: function(data) {
				callback();
			}
		});
	}
</script>