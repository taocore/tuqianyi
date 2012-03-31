(function() {
	$(".custom-label-item").hover(
			function()
			{
				$(".custom-label-tool", $(this)).show();
			},
			function()
			{
				$(".custom-label-tool", $(this)).hide();
			}
		);
		
		$('.custom-label-tool').click(function(){
			if (confirm('确定删除？'))
			{
				var url = "delete_label.action";
				var q = "label.id=" + $(this).closest('.custom-label-item').attr('label_id');
				var $label = $(this);
				$.ajax({
					url: url,
					data: q,
					type: 'POST',
					success: function(data) {
						if (data == 'ok')
						{
							window.utils.hideProcessingDialog();
							$label.closest('.custom-label-item').remove();
						}
						else
						{
							alert(data);
						}
					}
				});
			}
			return false;
		});
})();
