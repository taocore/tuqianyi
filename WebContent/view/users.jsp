<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div>

<div>
	<span class="separator"><a href="#">新增</a></span>
	<span class="separator"><a href="#">删除</a></span>
	<span class="selection separator"></span>
</div>
<hr/>

<div id="user-table">
	<s:include value="users_table.jsp"/>
</div>

<div id="pager" class="pager span-16 separator">
</div>

</div>

<script type="text/javascript">
	//ajaxPaging('#user-table');
	var selectedUsers = new Array();
	
	function checkSelection()
	{
		$("tbody input.selector").each(function()
		{
			var nick = $(this).closest("tr").attr("nick");
			var found = $.grep(selectedUsers, function(a){
				return a.nick == nick;
			}, false);
			$(this).attr("checked", (found.length > 0));
			$(this).trigger('change');
		});
	}
	
	function loadPage(number) {
        var limit = <s:property value="users.option.limit"/>;
        var offset = (number - 1) * limit;
        var url = "users_table.action?option.limit=" + limit + "&option.offset=" + offset;
		$.ajax({
			url: url,
			success: function(data) {
				$("#user-table").html(data);
				checkSelection();
				$(".pager").pager({ pagenumber: number, pagecount: <s:property value="users.totalPages"/>, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
			}
		});
    }
	
	$(".pager").pager({ pagenumber: 1, pagecount: <s:property value="users.totalPages"/>, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
</script>