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
			<th>促销详情</th>
			<th>活动时间</th>
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
					<s:if test="%{promoted}">
						<div class="origin-price quiet">原价：<s:property value="price"/>元</div>
	    				<div class="promotion-price">促销价：<s:property value="promotionPrice"/>元</div>
					</s:if>
					<s:else>
	    				<div>价格：<s:property value="price"/>元</div>
					</s:else>
				</td>
				<td class="promotion-details" title="<s:property value="promotionDescription"/>">
					
					<s:if test="%{promoted}">
						<span class="notice">
						<s:property value="promotionTitle"/>
	    				<s:if test='%{"PRICE".equals(discountType)}'>
	    					减<s:property value="discountValue"/>元
						</s:if>
						<s:else>
		    				<s:property value="discountValue"/>折
						</s:else>
						</span>
					</s:if>
					<s:else>
	    				<span class="quiet">未参加促销</span>
					</s:else>
				</td>
				<td class="promotion-time" status='<s:property value="status"/>'>
					<s:if test="%{promoted}">
	    				<div>开始：<s:property value="startDate"/></div>
	    				<div>结束：<s:property value="endDate"/></div>
					</s:if>
				</td>
				<td class="op">
					<s:if test="%{promoted}">
						<div><a class="cancel-promotion-link" href="#">恢复</a></div>
					</s:if>
					<s:else>
	    				<a class="promote-link" href="#">加标签</a>
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<script type="text/javascript">
	$("#items-table").selectable({selectionChanged: selectionChanged});
	$(".promotion-details").tooltip();
	$(".promote-link").click(function(){
		var currentItem = $(this).closest("tr").attr("num_iid");
		var $dialog = $("#promote-dialog");
		$dialog.dialog("option", "buttons", {
			确定: function() {
				if (!validatePromotionForm())
				{
					return false;
				}
				showProcessingDialog();
				var url = "add_promotion.action";
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
		var $dialog = $("#promote-dialog");
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