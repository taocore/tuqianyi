(function() {
	function getFilterParameters()
	{
		var $form = $("#categories-form");
		var saleStatus = $("select[name='filter.saleStatus']", $form).val();
		var banner = $("select[name='filter.saleStatus'] option:selected", $form).attr('banner');
		var cids = null;
		var checkboxes = $("div[name='filter.sellerCids'] input[checked='checked']", $form);
		if (checkboxes)
		{
			cids = [];
			checkboxes.each(function(){
				cids.push($(this).parent().attr('value'));
			});
		}
		
		var q = 'filter.saleStatus=' + saleStatus;
		q += '&filter.banner=' + banner;
		if (cids)
		{
			q += '&filter.sellerCids=' + cids;
		}
		return q;
	}
	
	$('#categories-form button').button();
	
	$('input[type="checkbox"]').change(function(){
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

	$("#merge-by-category").click(function(){
		try
		{
			alert(getFilterParameters());
		}
		catch (error)
		{
			alert(error);
		}
		return false;
		var $dialog = $("#label-dialog");
		var url = "merging-category.action";
		var numIids = selectedItems.join();
		var q = "numIids=" + numIids;
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
						showProgressDialog();
						//showProcessingDialog();
						var url = 'merge.action';
						var q = 'numIids=' + numIids + merges;
						$.ajax({
							url: url,
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

	$("#recover-by-category").click(function(){
		showProgressDialog();
		var url = "recover.action?numIids=" + selectedItems.join();
		$.ajax({
			url: url,
			success: function(data) {
				hideProgressDialog();
				reload(clearSelection);
			}
		});
		return false;
	});
})();