(function() {
	var $content = $('#items-selector');
	var $table = $('table', $content);
		
	function checkSelection()
	{
		$("tbody input.selector").each(function()
		{
			var num_iid = $(this).closest("tr").attr("num_iid");
			var found = $.grep(selectedItems, function(a){
				return a == num_iid;
			}, false);
			$(this).attr("checked", (found.length > 0));
			$(this).trigger('change');
		});
	}
	
	function clearSelection()
	{
		selectedItems = new Array();
		checkSelection();
	}
	
	function getFilterParameters()
	{
		var $form = $("#search-form");
		var saleStatus = $("select[name='filter.saleStatus']", $form).val();
		var banner = $("select[name='filter.saleStatus'] option:selected", $form).attr('banner');
		var cids = null;
		var c = $("select[name='filter.sellerCids']", $form);
		var cid = c.val();
		if (cid)
		{
			cids = [cid];
			$("option[parent='" + cid + "']", $form).each(function(){
				cids.push($(this).val());
			});
		}
		var status = $("select[name='filter.status']", $form).val();
		var keyword = $("input[name='filter.keyWord']", $form).val();
		if (keyword == '关键字')
		{
			keyword = null;
		}
		var q = 'filter.saleStatus=' + saleStatus;
		q += '&filter.banner=' + banner;
		if (cids)
		{
			q += '&filter.sellerCids=' + cids;
		}
		q += '&filter.status=' + status;
		if (keyword)
		{
			q += '&filter.keyWord=' + keyword;
		}
		return q;
	}
	
	function loadPage(number, callback) {
		$("#items").html("<img src='images/loading.gif'/>");
        var limit = parseInt($table.attr("pageSize"));;
        var offset = (number - 1) * limit;
        var q = $("#items").data('q') + "&option.limit=" + limit + "&option.offset=" + offset;
		$.ajax({
			url: "items.action",
			data: q,
			type: 'POST',
			success: function(data) {
				$("#items").html(data);
				checkSelection();
				var pageCount = parseInt($table.attr("pages"));
				$("#pager").pager({ pagenumber: number, pagecount: pageCount, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
				if (callback)
				{
					callback();
				}
			}
		});
    }
	
	function reload(callback)
	{
		var i = parseInt($table.attr("pageIndex"));
		loadPage(i+1, callback);
	}
	
	function updateStatus()
	{
		var $dialog = $("#progress-dialog");
		var isOpen = $dialog.dialog( "isOpen" );
		if (!isOpen)
		{
			clearTimeout($dialog.data('timer'));
			return;
		}
		$.ajax({
			  url: 'progress.action',
			  success: function( data ) {
				  $dialog.html(data);
			  }
		});
		var t = setTimeout(updateStatus, 2000);
		$dialog.data('timer', t);
	}
	
	function showProgressDialog()
	{
		$("#progress-dialog").dialog("open");
		updateStatus();
	}
	
	function hideProgressDialog()
	{
		$("#progress-dialog").dialog("close");
	}
	
	$("#pager").pager({ 
		pagenumber: 1, 
		pagecount: parseInt($table.attr("pages")), 
		buttonClickCallback: loadPage, 
		firstLabel: "首页", 
		prevLabel: "前一页", 
		nextLabel: "下一页", 
		lastLabel: "末页" 
	});
	
	$(".selection").tooltip();
	$("button").button();
	var $keyword = $("#keyword");
	$keyword.css('color', '#666');
	$keyword.focus(function() {
		if ($(this).val() == '关键字') {
			$(this).css('color', 'black');
			$(this).val('');
		}
	});
	$keyword.blur(function() {
		if ($(this).val() == '') {
			$(this).val('关键字');
			$(this).css('color', '#666');
		}
	});
	
	$("#search").click(function(){
		var q = getFilterParameters();
		$("#items").data('q', q);
		loadPage(1);
		return false;
	});
	
	$("#search-form").submit(function()
	{
		return false;
	});
	$("#clear-selection").click(clearSelection);
	$("#label-dialog").dialog({
		autoOpen: false,
		modal: true,
		width: 900,
		open: function(event, ui) {
			$("#search-form select").hide();
			$("#ctabs").tabs({
				cache: true
			});
		},
		close: function(event, ui){
			$("#search-form select").show();
		}
	});
	
	$("#progress-dialog").dialog({
		autoOpen: false,
		modal: true,
		open: function(event, ui) { 
			$(".ui-dialog-titlebar-close").hide(); 
		}
	});
	
	$("#batch-add-label").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		if (selectedItems.length > maxBatch)
		{
			alert("批量一次最多只能处理 " + maxBatch + " 件宝贝。");
			return false;
		}
		var $dialog = $("#label-dialog");
		var numIids = selectedItems.join();
		var q = "numIids=" + numIids;
		$.ajax({
			url: "merging.action",
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
						showProgressDialog();
						//showProcessingDialog();
						var q = 'numIids=' + numIids + merges;
						$.ajax({
							url: 'merge.action',
							data: q,
							type: 'POST',
							success: function(data){
								hideProgressDialog();
								//hideProcessingDialog();
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
	
	$("#batch-recover").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		showProgressDialog();
		$.ajax({
			url: "recover.action",
			data: {numIids: selectedItems.join()},
			success: function(data) {
				hideProgressDialog();
				reload(clearSelection);
			}
		});
		return false;
	});
})();