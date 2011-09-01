<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="user-table">
<thead><tr>
	<th><input type="checkbox" class="selector"></input></th>
	<th>Name</th>
	<th>Last Login</th>
	<th>操作</th>
</tr></thead>
<tbody>
	<s:iterator value="users.items">    
	<tr class="drow" nick='<s:property value="nick" />'>
	<td><input type="checkbox" class="selector"></input></td>
	<td class="nick"><s:property value="nick" /></td>
	<td class="last-login"><s:property value="lastLogin" /></td>
	<td><a href="#" class="separator">details</a><a href="#" class="add_label seperator">add label</a></td>
	</tr>
	</s:iterator>
</tbody>
</table>

<div id="dialog" title="Basic dialog">
	<p></p>
</div>

<script type="text/javascript">

	$(".user-table").selectable({selectionChanged: selectionChanged});
	
	function selectionChanged(row)
	{
		var nick = row.attr("nick");
		var lastLogin = row.find("td.last-login").text();
		var selected = $("input", row).attr("checked");
		if (selected)
		{
			var found = $.grep(selectedUsers, function(a){
				return a.nick == nick;
			}, false);
			if (found.length == 0)
			{
				var u = {nick: nick, lastLogin: lastLogin};
				selectedUsers.push(u);
			}
		}
		else
		{
			selectedUsers = $.grep(selectedUsers, function(a){
				return a.nick == nick;
			}, true);
		}
		$(".selection").text("已选中 " + selectedUsers.length + " 项");
	}
	
	$( "#dialog" ).dialog({
		autoOpen: false,
		show: "blind",
		hide: "explode"
	});

	$(".add_label").click(function(){
		var nick = $(this).closest("tr").attr("nick");
		$("#dialog p").text(nick);
		$( "#dialog" ).dialog( "open" );
		return false;
	});
</script>