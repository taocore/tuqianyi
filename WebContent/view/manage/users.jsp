<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div>
<table id="users-table" pages='<s:property value="pagingItems.totalPages" />' pageIndex='<s:property value="pagingItems.currentPage" />'>
	<tbody>
		<s:iterator value="pagingItems.items">
			<tr class="drow" num_iid='<s:property value="numIid"/>'>
				<td class='nick' nick='<s:property value="nick"/>'>
					<div><a href='#'><s:property value="nick"/></a></div>
				</td>
				<td>
					<div><s:date name="lastUpdate" format='yyyy-MM-dd'/></div>
				</td>
				<td class="op">
					<div><a class='order-link' href="#">查看订单</a></div>
					<div><a class="delete-user-link" href="#">删除用户</a></div>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</div>
<script type="text/javascript">
$('#users-table .nick a').click(function(){
	var nick = $(this).closest('tr').find('td.nick').attr('nick');
	var url = 'user-items-wrapper.action?nick=' + encodeURI(nick);
	$('#tabs').tabs( "add", url, nick);
	return false;
});

$('#users-table .order-link').click(function(){
	var nick = $(this).closest('tr').find('td.nick').attr('nick');
	var url = 'orders.action?nick=' + encodeURI(nick);
	$('#tabs').tabs( "add", url, nick);
	return false;
});
</script>