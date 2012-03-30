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

<script type="text/javascript" src='js/items.js'></script>