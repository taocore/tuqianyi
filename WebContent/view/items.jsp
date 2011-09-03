<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<table id="items-table" pages='<s:property value="pagingItems.totalPages" />' pageIndex='<s:property value="pagingItems.currentPage" />'>
	<thead>
		<tr>
			<th><input type="checkbox" class="selector"></input>
			</th>
			<th>主图</th>
			<th>宝贝详情</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="pagingItems.items">
			<tr class="drow" num_iid='<s:property value="numIid"/>'>
				<td><input type="checkbox" class="selector"></input>
				</td>
				<td class="pic">
					<a href='http://item.taobao.com/item.htm?id=<s:property value="numIid"/>' target="_blank">
						<img src='<s:property value="picUrl"/>_80x80.jpg'/>
					</a>
				</td>
				<td class="item-details">
					<div><s:property value="title"/></div>
					<div>价格：<s:property value="price"/>元</div>
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
	    				<div>失败</div>
					</s:elseif>
				</td>
				<td class="op">
					<s:if test="%{merged}">
						<div><a class="cancel-promotion-link" href="#">还原</a></div>
					</s:if>
					<s:else>
	    				<a class="add-label-link" href="#">贴标签</a>
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<script type="text/javascript">
	$("#items-table").selectable({selectionChanged: selectionChanged});
	$(".promotion-details").tooltip();
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
						return false;
					},
					取消: function() {
						$(this).dialog( "close" );
					}
				});
				$dialog.dialog("open");
			}
		});
		return false;
	});
	
	$(".cancel-promotion-link").click(function(){
		var promotionId = $(this).closest("tr").attr("promotion_id");
		var url = "delete_promotion.action?promotionDeleteRequest.promotionId=" + promotionId;
		$.ajax({
			url: url,
			success: function(data) {
				reload();
			}
		});
		return false;
	});
	
	$(".update-promotion-link").click(function(){
		var currentItem = $(this).closest("tr").attr("num_iid");
		var $dialog = $("#label-dialog");
		$dialog.dialog("option", "buttons", {
			确定: function() {
				if (!validatePromotionForm())
				{
					return false;
				}
				showProcessingDialog();
				var url = "update_promotion.action";
				var q = getPromotionParameter() + "&promotionAddRequest.numIids=" + currentItem;
				$.ajax({
					url: url,
					data: q,
					type: 'POST',
					success: function(data) {
						hideProcessingDialog();
						$dialog.dialog( "close" );
						reload(clearSelection);
					}
				});
				return false;
			},
			取消: function() {
				$(this).dialog( "close" );
			}
		});
		$dialog.dialog("open");
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
</script>