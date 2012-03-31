(function() {
	var $form = $('#categories-form');
	
	function getFilter()
	{
		var saleStatus = $("select[name='filter.saleStatus']", $form).val();
		var banner = $("select[name='filter.saleStatus'] option:selected", $form).attr('banner');
		var cids = null;
		var checkboxes = $("div[name='filter.sellerCids'] input:checked", $form);
		if (checkboxes)
		{
			cids = [];
			checkboxes.each(function(){
				cids.push($(this).parent().attr('value'));
			});
		}
		
		var q = 'filter.saleStatus=' + saleStatus;
		q += '&filter.banner=' + banner;
		if (cids && cids.length > 0)
		{
			q += '&filter.sellerCids=' + cids;
		}
		else
		{
			alert('未选择分类。');
			return false;
		}
		return q;
	}
	
	$('button', $form).button();
	
	$('input[type="checkbox"]', $form).change(function(){
		var cid = $(this).parent().attr('value');
		var pid = $(this).parent().attr('parent');
		if (parseInt(pid) == 0)
		{
			var v = $(this).attr('checked');
			if (v)
			{
				$('div[parent="' + cid + '"] input').attr('checked', 'checked');
			}
			else
			{
				$('div[parent="' + cid + '"] input').removeAttr('checked');
			}
		}
	});

	$("#merge-by-category", $form).click(function(){
		var filter = getFilter();
		if (!filter)
		{
			return false;
		}
		var $dialog = $("#label-dialog");
		$.ajax({
			url: "merging.action",
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
//						showProgressDialog();
						window.utils.showProcessingDialog();
						var q = filter + merges;
						$.ajax({
							url: 'merge-by-categories.action',
							data: q,
							type: 'POST',
							success: function(data){
//								hideProgressDialog();
								window.utils.hideProcessingDialog();
								if (data == 'ok')
								{
									$dialog.dialog('close');
									alert("已加入处理队列。");
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

//	$("#recover-by-category", $form).click(function(){
//		showProgressDialog();
//		var url = "recover.action?numIids=" + selectedItems.join();
//		$.ajax({
//			url: url,
//			success: function(data) {
//				hideProgressDialog();
//				alert("完成。");
//			}
//		});
//		return false;
//	});
})();