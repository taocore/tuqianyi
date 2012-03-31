(function() {
	$("#add-label-dialog").dialog({
		autoOpen: false,
		modal: true,
		width: 350,
		open : function() {
			$('#label-address').focus().select();
		},
		buttons: {
			确定: function() {
				var labelUrl = $('#label-address').val();
				if (!labelUrl)
				{
					alert('图片地址不能为空。');
					return false;
				}
				if (labelUrl.indexOf('http://') != 0)
				{
					alert('错误的网络地址。');
					return false;
				}
				window.utils.showProcessingDialog();
				var url = "add_label.action";
				var q = "label.src=" + labelUrl;
				$.ajax({
					url: url,
					data: q,
					type: 'POST',
					success: function(data) {
						window.utils.hideProcessingDialog();
						$("#add-label-dialog").dialog( "close" );
						$("#custom-labels").html(data);
					}
				});
				return false;
			},
			取消: function() {
				$(this).dialog( "close" );
			}
		}
	});

	$("#add-label, #design").button();
	
	$('#add-label').click(function(){
		$('#add-label-dialog').dialog('open');
		return false;
	});
	
})();
