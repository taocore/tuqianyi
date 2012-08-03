<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div>
<table>
	<thead>
		<tr>
		<th>用户</th>
		<th>开始</th>
		<th>结束</th>
		<th>付费</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="orders">
			<tr>
				<td>
					<div><s:property value="nick"/></div>
				</td>
				<td>
					<div><s:date name="orderCycleStart" format="yyyy-MM-dd" /></div>
				</td>
				<td>
					<div><s:date name="orderCycleEnd" format="yyyy-MM-dd"/></div>
				</td>
				<td>
					<div><s:property value="totalPayFee"/></div>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</div>