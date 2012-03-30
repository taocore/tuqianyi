$(function() {
	var $table = $("#items-table");
	
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
	
	$table.mytable({selectionChanged: selectionChanged});
	
	$(".pic", $table).tooltip({
		showURL: false,
		bodyHandler: function() { 
	        return $("<img/>").attr("src", $(this).attr('src_310')); 
	    } 
	});
	
	$(".add-label-link", $table).click(function(){
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
									window.utils.reload();
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
	
	$(".change-label-link", $table).click(function(){
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
									window.utils.reload();
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
	
	$(".recover-link", $table).click(function(){
		window.utils.showProcessingDialog();
		var numIid = $(this).closest("tr").attr("num_iid");
		var url = "recover.action?numIids=" + numIid;
		$.ajax({
			url: url,
			success: function(data) {
				window.utils.hideProcessingDialog();
				window.utils.reload();
			}
		});
		return false;
	});
});