<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<table id="user-items-table" pages='<s:property value="pagingItems.totalPages" />' pageIndex='<s:property value="pagingItems.currentPage" />'>
	<thead>
		<tr>
			<th>原图</th>
			<th>现图</th>
			<th>出错原因</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="pagingItems.items">
			<tr class="drow" num_iid='<s:property value="numIid"/>'>
				<td class='item-main-pic'>
					<a href='http://item.taobao.com/item.htm?id=<s:property value="numIid"/>' target="_blank">
						<img class="pic" src='<s:property value="oldPicUrl"/>_80x80.jpg' src_310='<s:property value="picUrl"/>_310x310.jpg' />
					</a>
				</td>
				<td class='item-main-pic'>
					<a href='http://item.taobao.com/item.htm?id=<s:property value="numIid"/>' target="_blank">
						<img class="pic" src='<s:property value="picUrl"/>_80x80.jpg' src_310='<s:property value="picUrl"/>_310x310.jpg' />
					</a>
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
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<script type="text/javascript">

</script>